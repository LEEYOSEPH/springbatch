package com.example.springbach.batch.jobs;

import com.example.springbach.batch.MembersFieldSetMapper;
import com.example.springbach.entity.Members;
import com.example.springbach.repository.MembersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

/**
*
*run : --spring.batch.job.names=dbInsertJob
*
* */
@Configuration
@RequiredArgsConstructor
public class SpringBatchInsertDbConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private MembersRepository membersRepository;

    private static final int chunkSize = 10000;

    @Bean
    public Job dbInsertJob() {
        return jobBuilderFactory.get("dbInsertJob")
                .incrementer(new RunIdIncrementer())
                .start(dbInsertStep())
                .build();
    }

    @Bean
    public Step dbInsertStep() {
        return stepBuilderFactory.get("dbInsertStep")
                .<Members, Members>chunk(chunkSize)
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader<Members> itemReader() {
        return new FlatFileItemReaderBuilder<Members>()
                .name("itemReader")
                .resource(new FileSystemResource("Members.csv"))
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(new MembersFieldSetMapper())
                .linesToSkip(1)
                .build();
    }

    @Bean
    public RepositoryItemWriter<Members> itemWriter() {
        return new RepositoryItemWriterBuilder<Members>()
                .repository(membersRepository)
                .methodName("save")
                .build();
    }

}
