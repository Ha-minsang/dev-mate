package io.github.haminsang.devmate.domain.health.dto;

import lombok.Getter;

@Getter
public class ServerTargetRequest {

    private String name;
    private String url;
    private String managerEmail;
}