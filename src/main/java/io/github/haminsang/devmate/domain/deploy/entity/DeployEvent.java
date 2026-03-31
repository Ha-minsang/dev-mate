package io.github.haminsang.devmate.domain.deploy.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "deploy_event")
public class DeployEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 레포 이름
    private String repoName;

    // 브랜치 이름
    private String branchName;

    // 커밋 메시지
    private String commitMessage;

    // 배포자
    private String pusher;

    // 커밋 URL
    private String commitUrl;

    private LocalDateTime deployedAt;

    @Builder
    public DeployEvent(String repoName, String branchName, String commitMessage,
                       String pusher, String commitUrl) {
        this.repoName = repoName;
        this.branchName = branchName;
        this.commitMessage = commitMessage;
        this.pusher = pusher;
        this.commitUrl = commitUrl;
        this.deployedAt = LocalDateTime.now();
    }
}