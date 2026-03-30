package io.github.haminsang.devmate.domain.health.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "health_check_log")
public class HealthCheckLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_target_id")
    private ServerTarget serverTarget;

    // 응답 상태코드
    private Integer statusCode;

    // 응답 시간 (ms)
    private Long responseTime;

    // 성공 여부
    private boolean success;

    // 실패 원인
    private String errorMessage;

    private LocalDateTime checkedAt;

    @Builder
    public HealthCheckLog(ServerTarget serverTarget, Integer statusCode,
                          Long responseTime, boolean success, String errorMessage) {
        this.serverTarget = serverTarget;
        this.statusCode = statusCode;
        this.responseTime = responseTime;
        this.success = success;
        this.errorMessage = errorMessage;
        this.checkedAt = LocalDateTime.now();
    }
}