package io.github.haminsang.devmate.batch;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DailyReportJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DailyReportTasklet dailyReportTasklet;

    public DailyReportJobConfig(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            DailyReportTasklet dailyReportTasklet
    ) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dailyReportTasklet = dailyReportTasklet;
    }

    @Bean
    public Job dailyReportJob() {
        return new JobBuilder("dailyReportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(dailyReportStep())
                .build();
    }

    @Bean
    public Step dailyReportStep() {
        return new StepBuilder("dailyReportStep", jobRepository)
                .tasklet(dailyReportTasklet, transactionManager)
                .build();
    }
}