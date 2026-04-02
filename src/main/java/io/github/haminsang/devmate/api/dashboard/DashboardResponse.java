package io.github.haminsang.devmate.api.dashboard;

import io.github.haminsang.devmate.domain.deploy.entity.DeployEvent;
import io.github.haminsang.devmate.domain.health.entity.ServerTarget;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardResponse {

    // 서버 상태 목록
    private final List<ServerStatusDto> servers;

    // 최근 배포 이력
    private final List<DeployEvent> recentDeploys;

    // 오늘 알림 발송 건수
    private final long todayNotificationCount;

    @Getter
    @Builder
    public static class ServerStatusDto {

        private final Long id;
        private final String name;
        private final String url;
        // 마지막 헬스체크 성공 여부
        private final boolean lastCheckSuccess;
        // 마지막 응답 시간 (ms)
        private final Long lastResponseTime;
    }
}