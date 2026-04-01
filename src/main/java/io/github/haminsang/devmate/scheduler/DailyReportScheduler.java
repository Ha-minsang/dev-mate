package io.github.haminsang.devmate.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DailyReportScheduler {

    private final JobOperator jobOperator;
    private final Job dailyReportJob;

    public DailyReportScheduler(
            JobOperator jobOperator,
            @Qualifier("dailyReportJob") Job dailyReportJob
    ) {
        this.jobOperator = jobOperator;
        this.dailyReportJob = dailyReportJob;
    }

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void run() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobOperator.start(dailyReportJob, params);
            log.info("일일 리포트 배치 실행 요청 완료. executionId={}", jobExecution.getId());
        } catch (Exception e) {
            log.error("일일 리포트 배치 실행 실패", e);
        }
    }
}