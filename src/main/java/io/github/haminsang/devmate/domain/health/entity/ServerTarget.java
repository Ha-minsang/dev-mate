package io.github.haminsang.devmate.domain.health.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "server_target")
public class ServerTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 서버 이름
    private String name;

    // 헬스체크 URL
    private String url;

    // 담당자 이메일
    private String managerEmail;

    // 활성화 여부
    private boolean active;

    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public static ServerTarget create(String name, String url, String managerEmail) {
        ServerTarget target = new ServerTarget();
        target.name = name;
        target.url = url;
        target.managerEmail = managerEmail;
        target.active = true;
        return target;
    }

    public void toggleActive() {
        this.active = !this.active;
    }
}