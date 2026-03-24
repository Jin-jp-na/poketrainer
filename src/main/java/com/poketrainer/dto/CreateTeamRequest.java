package com.poketrainer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateTeamRequest {

    @NotBlank(message = "Team name is required")
    private String name;

    @NotNull(message = "User ID is required")
    private Long userId;

    public CreateTeamRequest() {
    }

    public String getName() {
        return name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}