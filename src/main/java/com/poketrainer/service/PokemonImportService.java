package com.poketrainer.service;

import com.poketrainer.dto.external.PokeApiPokemonResponse;
import com.poketrainer.model.Pokemon;
import com.poketrainer.repository.PokemonRepository;
import com.poketrainer.util.StatCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PokemonImportService {

    private static final Logger logger = LoggerFactory.getLogger(PokemonImportService.class);
    private static final String POKE_API_URL = "https://pokeapi.co/api/v2/pokemon/";
    private static final int INITIAL_POKEMON_COUNT = 151;

    private final RestTemplate restTemplate;
    private final PokemonRepository pokemonRepository;
    private final PokemonRankingService pokemonRankingService;

    public PokemonImportService(RestTemplate restTemplate,
                                PokemonRepository pokemonRepository,
                                PokemonRankingService pokemonRankingService) {
        this.restTemplate = restTemplate;
        this.pokemonRepository = pokemonRepository;
        this.pokemonRankingService = pokemonRankingService;
    }

    public Pokemon importPokemonByIdOrName(String idOrName) {
        Pokemon savedPokemon = fetchAndSavePokemon(idOrName);
        pokemonRankingService.recalculateAllScoresAndRanks();
        return savedPokemon;
    }

    public synchronized boolean ensureInitialPokemonLoaded() {
        if (pokemonRepository.count() >= INITIAL_POKEMON_COUNT) {
            return false;
        }

        List<String> missingPokemonIds = findMissingInitialPokemonIds();

        if (missingPokemonIds.isEmpty()) {
            return false;
        }

        logger.info("Importing {} missing Pokemon from PokeAPI.", missingPokemonIds.size());

        int successCount = 0;
        for (String pokemonId : missingPokemonIds) {
            try {
                fetchAndSavePokemon(pokemonId);
                successCount++;
            } catch (RestClientException e) {
                logger.warn("Failed to import Pokemon #{}: Network error - {}", pokemonId, e.getMessage());
            } catch (IllegalArgumentException e) {
                logger.warn("Failed to import Pokemon #{}: Invalid data - {}", pokemonId, e.getMessage());
            } catch (Exception e) {
                logger.error("Failed to import Pokemon #{}: {}", pokemonId, e.getMessage(), e);
            }
        }

        if (successCount == 0) {
            logger.warn("Could not import Pokemon data from PokeAPI. Returning the data already stored locally.");
            return false;
        }

        pokemonRankingService.recalculateAllScoresAndRanks();
        logger.info("Pokemon import finished. Imported {} Pokemon in this run.", successCount);

        return true;
    }

    private List<String> findMissingInitialPokemonIds() {
        return java.util.stream.IntStream.rangeClosed(1, INITIAL_POKEMON_COUNT)
                .filter(id -> !pokemonRepository.existsById((long) id))
                .mapToObj(String::valueOf)
                .toList();
    }

    private Pokemon fetchAndSavePokemon(String idOrName) {
        if (idOrName == null || idOrName.isBlank()) {
            throw new IllegalArgumentException("Pokemon id or name must not be blank");
        }

        String url = POKE_API_URL + idOrName.trim().toLowerCase();

        PokeApiPokemonResponse response =
                restTemplate.getForObject(url, PokeApiPokemonResponse.class);

        if (response == null) {
            throw new IllegalArgumentException("Pokemon not found in API");
        }

        Pokemon pokemon = mapToEntity(response);
        pokemon.setScore(StatCalculator.calculateScore(pokemon));

        return pokemonRepository.save(pokemon);
    }

    private Pokemon mapToEntity(PokeApiPokemonResponse response) {
        String primaryType = null;
        String secondaryType = null;

        if (response.getTypes() != null && !response.getTypes().isEmpty()) {
            var sortedTypes = response.getTypes().stream()
                    .sorted(Comparator.comparingInt(PokeApiPokemonResponse.PokemonTypeSlot::getSlot))
                    .toList();

            var primaryTypeSlot = sortedTypes.get(0);
            if (primaryTypeSlot.getType() != null) {
                primaryType = primaryTypeSlot.getType().getName();
            }

            if (sortedTypes.size() > 1) {
                var secondaryTypeSlot = sortedTypes.get(1);
                if (secondaryTypeSlot.getType() != null) {
                    secondaryType = secondaryTypeSlot.getType().getName();
                }
            }
        }

        Integer hp = extractStat(response, "hp");
        Integer attack = extractStat(response, "attack");
        Integer defense = extractStat(response, "defense");
        Integer specialAttack = extractStat(response, "special-attack");
        Integer specialDefense = extractStat(response, "special-defense");
        Integer speed = extractStat(response, "speed");

        String spriteUrl = response.getSprites() != null
                ? response.getSprites().getFront_default()
                : null;

        Pokemon pokemon = new Pokemon(
                response.getId(),
                response.getName(),
                primaryType,
                secondaryType,
                hp,
                attack,
                defense,
                speed,
                spriteUrl
        );

        pokemon.setSpecialAttackStat(specialAttack);
        pokemon.setSpecialDefenseStat(specialDefense);
        pokemon.setHeight(response.getHeight());
        pokemon.setWeight(response.getWeight());
        pokemon.setAbilities(extractAbilities(response));

        return pokemon;
    }

    private Integer extractStat(PokeApiPokemonResponse response, String statName) {
        if (response.getStats() == null) {
            return 0;
        }

        Optional<PokeApiPokemonResponse.PokemonStatSlot> stat = response.getStats().stream()
                .filter(s -> s.getStat() != null && statName.equalsIgnoreCase(s.getStat().getName()))
                .findFirst();

        return stat.map(PokeApiPokemonResponse.PokemonStatSlot::getBase_stat).orElse(0);
    }

    private String extractAbilities(PokeApiPokemonResponse response) {
        if (response.getAbilities() == null || response.getAbilities().isEmpty()) {
            return null;
        }

        return response.getAbilities().stream()
                .filter(abilitySlot -> abilitySlot.getAbility() != null && abilitySlot.getAbility().getName() != null)
                .sorted(Comparator.comparingInt(PokeApiPokemonResponse.PokemonAbilitySlot::getSlot))
                .map(abilitySlot -> abilitySlot.getAbility().getName()
                        + (abilitySlot.isIs_hidden() ? " (hidden)" : ""))
                .toList()
                .stream()
                .reduce((first, second) -> first + "," + second)
                .orElse(null);
    }
}
