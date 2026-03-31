package io.github.haminsang.devmate.domain.deploy.repository;

import io.github.haminsang.devmate.domain.deploy.entity.DeployEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeployEventRepository extends JpaRepository<DeployEvent, Long> {

    // 레포별 최근 배포 이력 조회
    List<DeployEvent> findTop10ByRepoNameOrderByDeployedAtDesc(String repoName);

    // 전체 최근 배포 이력 조회
    List<DeployEvent> findTop20ByOrderByDeployedAtDesc();
}