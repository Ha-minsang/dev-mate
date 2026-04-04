package io.github.haminsang.devmate.scheduler;

import io.github.haminsang.devmate.domain.health.service.HealthCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HealthCheckScheduler {

    private final HealthCheckService healthCheckService;

    // 1분마다 헬스체크 실행
    @Scheduled(fixedDelay = 60000)
    public void run() {
        log.info("헬스체크 스케줄러 실행");
        healthCheckService.checkAll();
    }
}