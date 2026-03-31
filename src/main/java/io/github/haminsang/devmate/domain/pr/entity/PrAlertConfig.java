package io.github.haminsang.devmate.domain.pr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "pr_alert_config")
public class PrAlertConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 모니터링할 레포 이름
    private String repoName;

    // PR 방치 기준 시간 (시간 단위)
    private int staleHours;

    // 활성화 여부
    private boolean active;

    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public static PrAlertConfig create(String repoName, int staleHours) {
        PrAlertConfig config = new PrAlertConfig();
        config.repoName = repoName;
        config.staleHours = staleHours;
        config.active = true;
        return config;
    }

    public void toggleActive() {
        this.active = !this.active;
    }
}