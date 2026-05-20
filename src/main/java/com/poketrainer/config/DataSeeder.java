package com.poketrainer.config;

import com.poketrainer.repository.PokemonRepository;
import com.poketrainer.service.PokemonImportService;
import com.poketrainer.service.PokemonRankingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "poketrainer.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final PokemonImportService pokemonImportService;
    private final PokemonRepository pokemonRepository;
    private final PokemonRankingService pokemonRankingService;

    public DataSeeder(PokemonImportService pokemonImportService,
                      PokemonRepository pokemonRepository,
                      PokemonRankingService pokemonRankingService) {
        this.pokemonImportService = pokemonImportService;
        this.pokemonRepository = pokemonRepository;
        this.pokemonRankingService = pokemonRankingService;
    }

    @Override
    public void run(String... args) {
        try {
            boolean importedPokemon = pokemonImportService.ensureInitialPokemonLoaded();

            if (!importedPokemon && pokemonRepository.count() > 0) {
                logger.info("Pokemon data already exists. Recalculating scores and ranks.");
                pokemonRankingService.recalculateAllScoresAndRanks();
            }
        } catch (Exception e) {
            logger.error("Pokemon startup import failed. The application will continue to start, but Pokemon data may be unavailable until the import issue is fixed.", e);
        }
    }
}
