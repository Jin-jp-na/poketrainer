package com.poketrainer.repository;

import com.poketrainer.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    Optional<Pokemon> findByName(String name);
    boolean existsByName(String name);
    List<Pokemon> findAllByOrderByScoreDescNameAsc();
    List<Pokemon> findTop10ByOrderByScoreDescNameAsc();
}