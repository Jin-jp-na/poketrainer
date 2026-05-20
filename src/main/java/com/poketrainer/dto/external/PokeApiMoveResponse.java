package com.poketrainer.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PokeApiMoveResponse {

    private String name;
    private PokeApiPokemonResponse.NamedResource type;
    private Integer power;
    private Integer accuracy;

    public PokeApiMoveResponse() {
    }

    public String getName() {
        return name;
    }

    public PokeApiPokemonResponse.NamedResource getType() {
        return type;
    }

    public Integer getPower() {
        return power;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(PokeApiPokemonResponse.NamedResource type) {
        this.type = type;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }
}
