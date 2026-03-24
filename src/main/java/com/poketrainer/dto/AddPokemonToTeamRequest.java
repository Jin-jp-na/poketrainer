package com.poketrainer.dto;

import jakarta.validation.constraints.NotNull;

public class AddPokemonToTeamRequest {

    @NotNull(message = "Pokemon ID is required")
    private Long pokemonId;

    public AddPokemonToTeamRequest() {
    }

    public Long getPokemonId() {
        return pokemonId;
    }

    public void setPokemonId(Long pokemonId) {
        this.pokemonId = pokemonId;
    }
}