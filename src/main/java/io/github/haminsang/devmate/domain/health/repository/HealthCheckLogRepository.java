package io.github.haminsang.devmate.domain.health.repository;

import io.github.haminsang.devmate.domain.health.entity.HealthCheckLog;
import io.github.haminsang.devmate.domain.health.entity.ServerTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthCheckLogRepository extends JpaRepository<HealthCheckLog, Long> {

    // 서버별 최근 이력 조회
    List<HealthCheckLog> findTop10ByServerTargetOrderByCheckedAtDesc(ServerTarget serverTarget);

    long countBySuccessFalse();
}