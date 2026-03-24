package com.poketrainer.controller;

import com.poketrainer.model.Pokemon;
import com.poketrainer.service.PokemonImportService;
import com.poketrainer.service.PokemonRankingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pokemon")
@CrossOrigin(origins = "*")
public class PokemonController {

    private final PokemonImportService pokemonImportService;
    private final PokemonRankingService pokemonRankingService;

    public PokemonController(PokemonImportService pokemonImportService,
                             PokemonRankingService pokemonRankingService) {
        this.pokemonImportService = pokemonImportService;
        this.pokemonRankingService = pokemonRankingService;
    }

    @PostMapping("/import/{idOrName}")
    @ResponseStatus(HttpStatus.CREATED)
    public Pokemon importPokemon(@PathVariable String idOrName) {
        return pokemonImportService.importPokemonByIdOrName(idOrName);
    }

    @GetMapping
    public List<Pokemon> getAllPokemon() {
        pokemonImportService.ensureInitialPokemonLoaded();
        return pokemonRankingService.getAllPokemon();
    }

    @GetMapping("/ranked")
    public List<Pokemon> getRankedPokemon() {
        pokemonImportService.ensureInitialPokemonLoaded();
        return pokemonRankingService.getRankedPokemon();
    }

    @GetMapping("/top10")
    public List<Pokemon> getTop10Pokemon() {
        pokemonImportService.ensureInitialPokemonLoaded();
        return pokemonRankingService.getTop10Pokemon();
    }

    @PostMapping("/recalculate-ranks")
    public String recalculateRanks() {
        pokemonRankingService.recalculateAllScoresAndRanks();
        return "Pokemon scores and ranks recalculated successfully";
    }

    @GetMapping("/{id}")
    public Pokemon getPokemonById(@PathVariable Long id) {
        pokemonImportService.ensureInitialPokemonLoaded();
        return pokemonRankingService.getPokemonById(id);
    }
}
