package io.github.haminsang.devmate.domain.pr.repository;

import io.github.haminsang.devmate.domain.pr.entity.PrAlertConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrAlertConfigRepository extends JpaRepository<PrAlertConfig, Long> {

    // 활성화된 PR 알림 설정 목록 조회
    List<PrAlertConfig> findAllByActiveTrue();
}