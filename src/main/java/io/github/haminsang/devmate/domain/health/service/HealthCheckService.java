package io.github.haminsang.devmate.domain.health.service;

import io.github.haminsang.devmate.domain.health.entity.HealthCheckLog;
import io.github.haminsang.devmate.domain.health.entity.ServerTarget;
import io.github.haminsang.devmate.domain.health.repository.HealthCheckLogRepository;
import io.github.haminsang.devmate.domain.health.repository.ServerTargetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class HealthCheckService {

    private final ServerTargetRepository serverTargetRepository;
    private final HealthCheckLogRepository healthCheckLogRepository;
    private final WebClient webClient;

    public HealthCheckService(ServerTargetRepository serverTargetRepository,
                              HealthCheckLogRepository healthCheckLogRepository,
                              @Qualifier("webClient") WebClient webClient) {
        this.serverTargetRepository = serverTargetRepository;
        this.healthCheckLogRepository = healthCheckLogRepository;
        this.webClient = webClient;
    }

    public void checkAll() {
        List<ServerTarget> targets = serverTargetRepository.findAllByActiveTrue();
        targets.forEach(this::check);
    }

    public HealthCheckLog check(ServerTarget target) {
        long startTime = System.currentTimeMillis();
        try {
            Integer statusCode = webClient.get()
                    .uri(target.getUrl())
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(5))
                    .map(response -> response.getStatusCode().value())
                    .block();

            long responseTime = System.currentTimeMillis() - startTime;
            boolean success = statusCode != null && statusCode < 400;

            HealthCheckLog checkLog = HealthCheckLog.builder()
                    .serverTarget(target)
                    .statusCode(statusCode)
                    .responseTime(responseTime)
                    .success(success)
                    .build();

            return healthCheckLogRepository.save(checkLog);

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            log.error("헬스체크 실패: {} - {}", target.getUrl(), e.getMessage());

            HealthCheckLog failLog = HealthCheckLog.builder()
                    .serverTarget(target)
                    .responseTime(responseTime)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();

            return healthCheckLogRepository.save(failLog);
        }
    }
}