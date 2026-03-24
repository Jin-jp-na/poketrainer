package com.poketrainer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddMoveRequest {

    @NotBlank(message = "Move name is required")
    private String name;

    @NotBlank(message = "Move type is required")
    private String type;

    @NotNull(message = "Move power is required")
    private Integer powerValue;

    public AddMoveRequest() {
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Integer getPowerValue() {
        return powerValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPowerValue(Integer powerValue) {
        this.powerValue = powerValue;
    }
}