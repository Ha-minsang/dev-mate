package io.github.haminsang.devmate.domain.pr.dto;

import lombok.Getter;

@Getter
public class PrAlertRequest {

    private String repoName;
    private int staleHours;
}