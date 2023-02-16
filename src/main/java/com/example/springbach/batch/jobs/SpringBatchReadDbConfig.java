package com.example.springbach.batch.jobs;

import com.example.springbach.dto.MembersResponseDto;
import com.example.springbach.entity.Members;
import com.example.springbach.repository.MembersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;



/**
 *      desc : DB데이터를 .csv 파일로 만들기
 *      run : --spring.batch.job.names=readDbJob
 *
 * */
@Configuration
@RequiredArgsConstructor
public class SpringBatchReadDbConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final MembersRepository membersRepository;

    private static final int chunkSize = 10000;

    @Bean
    public Job readDbJob() {
        return jobBuilderFactory.get("readDbJob")
                .incrementer(new RunIdIncrementer())
                .start(readDbStep())
                .build();
    }

    @Bean
    public Step readDbStep() {
        return stepBuilderFactory.get("readDbStep")
                .<Members, MembersResponseDto>chunk(chunkSize)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Members> itemReader() {
        return new RepositoryItemReaderBuilder<Members>()
                .name("itemReader")
                .repository(membersRepository)
                .methodName("findAll")
                .pageSize(chunkSize)
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Members,MembersResponseDto> itemProcessor() {
        return new ItemProcessor<Members, MembersResponseDto>() {
            @Override
            public MembersResponseDto process(Members item) throws Exception {
                return new MembersResponseDto(item);
            }
        };
    }

    @Bean
    public FlatFileItemWriter<MembersResponseDto> itemWriter() {
        //파일에 작성할 필드를 추출
        BeanWrapperFieldExtractor<MembersResponseDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        // 필드명
        fieldExtractor.setNames(new String[]{"memberName","age","birthDay","date"});
        fieldExtractor.afterPropertiesSet();

        // Line 구분값 설정
        DelimitedLineAggregator<MembersResponseDto> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        FileSystemResource fileSystemResource = new FileSystemResource("Members_list.txt");

        return new FlatFileItemWriterBuilder<MembersResponseDto>()
                .name("membersItemWriter")
                .resource(fileSystemResource)
                .lineAggregator(lineAggregator)
                .build();
    }

}
