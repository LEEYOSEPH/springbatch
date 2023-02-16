package com.example.springbach.batch.jobs;

import com.example.springbach.entity.Accounts;
import com.example.springbach.entity.Members;
import com.example.springbach.repository.AccountsRepository;
import com.example.springbach.repository.MembersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SpringBatchDbMigrationConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private MembersRepository membersRepository;
    private AccountsRepository accountsRepository;

    private static final int chunkSize = 10000;

    @Bean
    public Job dbMigrationJob() {
        return jobBuilderFactory.get("dbMigrationJob")
                .incrementer(new RunIdIncrementer())
                .start(dbMigrationStep())
                .build();
    }

    @Bean
    public Step dbMigrationStep() {
        return stepBuilderFactory.get("dbMigrationStep")
                .<Members, Accounts>chunk(chunkSize)
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
    public ItemProcessor<Members, Accounts> itemProcessor() {
        return new ItemProcessor<Members, Accounts>() {
            @Override
            public Accounts process(Members item) throws Exception {
                return new Accounts(item);
            }
        };
    }
    @Bean
    public ItemWriter<Accounts> itemWriter() {
       return new ItemWriter<Accounts>() {
           @Override
           public void write(List<? extends Accounts> items) throws Exception {
               accountsRepository.saveAll(items);
           }
       };
    }
}
