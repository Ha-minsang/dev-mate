package io.github.haminsang.devmate.domain.health.repository;

import io.github.haminsang.devmate.domain.health.entity.ServerTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServerTargetRepository extends JpaRepository<ServerTarget, Long> {

    // 활성화된 서버 목록 조회
    List<ServerTarget> findAllByActiveTrue();
}