package io.github.haminsang.devmate.api.dashboard;

import io.github.haminsang.devmate.api.common.ApiResponse;
import io.github.haminsang.devmate.domain.deploy.entity.DeployEvent;
import io.github.haminsang.devmate.domain.deploy.service.DeployEventService;
import io.github.haminsang.devmate.domain.health.entity.HealthCheckLog;
import io.github.haminsang.devmate.domain.health.entity.ServerTarget;
import io.github.haminsang.devmate.domain.health.repository.HealthCheckLogRepository;
import io.github.haminsang.devmate.domain.health.repository.ServerTargetRepository;
import io.github.haminsang.devmate.domain.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ServerTargetRepository serverTargetRepository;
    private final HealthCheckLogRepository healthCheckLogRepository;
    private final DeployEventService deployEventService;
    private final NotificationLogRepository notificationLogRepository;

    @GetMapping
    public ApiResponse<DashboardResponse> getDashboard() {
        List<ServerTarget> servers = serverTargetRepository.findAll();

        List<DashboardResponse.ServerStatusDto> serverStatusList = servers.stream()
                .map(server -> {
                    List<HealthCheckLog> logs = healthCheckLogRepository
                            .findTop10ByServerTargetOrderByCheckedAtDesc(server);

                    HealthCheckLog latest = logs.isEmpty() ? null : logs.get(0);

                    return DashboardResponse.ServerStatusDto.builder()
                            .id(server.getId())
                            .name(server.getName())
                            .url(server.getUrl())
                            .lastCheckSuccess(latest != null && latest.isSuccess())
                            .lastResponseTime(latest != null ? latest.getResponseTime() : null)
                            .build();
                })
                .toList();

        List<DeployEvent> recentDeploys = deployEventService.getRecentEvents();

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayNotificationCount = notificationLogRepository
                .countBySentAtAfter(todayStart);

        DashboardResponse response = DashboardResponse.builder()
                .servers(serverStatusList)
                .recentDeploys(recentDeploys)
                .todayNotificationCount(todayNotificationCount)
                .build();

        return ApiResponse.ok(response);
    }
}