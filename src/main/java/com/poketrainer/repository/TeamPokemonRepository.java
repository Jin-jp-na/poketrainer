package com.poketrainer.repository;

import com.poketrainer.model.TeamPokemon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamPokemonRepository extends JpaRepository<TeamPokemon, Long> {
}