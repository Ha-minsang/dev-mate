package io.github.haminsang.devmate.domain.deploy.service;

import io.github.haminsang.devmate.domain.deploy.entity.DeployEvent;
import io.github.haminsang.devmate.domain.deploy.repository.DeployEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeployEventService {

    private final DeployEventRepository deployEventRepository;

    // 배포 이벤트 저장
    public DeployEvent save(DeployEvent deployEvent) {
        return deployEventRepository.save(deployEvent);
    }

    // 전체 최근 배포 이력 조회
    public List<DeployEvent> getRecentEvents() {
        return deployEventRepository.findTop20ByOrderByDeployedAtDesc();
    }

    // 레포별 최근 배포 이력 조회
    public List<DeployEvent> getRecentEventsByRepo(String repoName) {
        return deployEventRepository.findTop10ByRepoNameOrderByDeployedAtDesc(repoName);
    }
}