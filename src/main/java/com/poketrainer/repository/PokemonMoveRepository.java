package com.poketrainer.repository;

import com.poketrainer.model.PokemonMove;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PokemonMoveRepository extends JpaRepository<PokemonMove, Long> {
    List<PokemonMove> findByPokemonIdOrderByNameAsc(Long pokemonId);
    boolean existsByPokemonIdAndName(Long pokemonId, String name);
    Optional<PokemonMove> findByPokemonIdAndName(Long pokemonId, String name);
}
