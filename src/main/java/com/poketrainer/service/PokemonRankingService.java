package com.poketrainer.service;

import com.poketrainer.exception.ResourceNotFoundException;
import com.poketrainer.model.Pokemon;
import com.poketrainer.repository.PokemonRepository;
import com.poketrainer.util.StatCalculator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PokemonRankingService {

    private final PokemonRepository pokemonRepository;

    public PokemonRankingService(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    public List<Pokemon> getAllPokemon() {
        return pokemonRepository.findAll();
    }

    public Pokemon getPokemonById(Long id) {
        return pokemonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pokemon not found with id: " + id));
    }

    public List<Pokemon> getRankedPokemon() {
        return pokemonRepository.findAllByOrderByScoreDescNameAsc();
    }

    public List<Pokemon> getTop10Pokemon() {
        return pokemonRepository.findTop10ByOrderByScoreDescNameAsc();
    }

    public void recalculateAllScoresAndRanks() {
        List<Pokemon> allPokemon = pokemonRepository.findAll();

        for (Pokemon pokemon : allPokemon) {
            pokemon.setScore(StatCalculator.calculateScore(pokemon));
        }

        pokemonRepository.saveAll(allPokemon);

        List<Pokemon> sortedPokemon = pokemonRepository.findAllByOrderByScoreDescNameAsc();

        int rank = 1;
        for (Pokemon pokemon : sortedPokemon) {
            pokemon.setRankPosition(rank++);
        }

        pokemonRepository.saveAll(sortedPokemon);
    }
}