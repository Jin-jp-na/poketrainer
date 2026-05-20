package com.poketrainer.service;

import com.poketrainer.dto.external.PokeApiPokemonResponse;
import com.poketrainer.dto.external.PokeApiMoveResponse;
import com.poketrainer.exception.ResourceNotFoundException;
import com.poketrainer.model.Pokemon;
import com.poketrainer.model.PokemonMove;
import com.poketrainer.repository.PokemonMoveRepository;
import com.poketrainer.repository.PokemonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PokemonMoveService {

    private static final Logger logger = LoggerFactory.getLogger(PokemonMoveService.class);
    private static final String POKE_API_POKEMON_URL = "https://pokeapi.co/api/v2/pokemon/";
    private static final String POKE_API_MOVE_URL = "https://pokeapi.co/api/v2/move/";

    private final RestTemplate restTemplate;
    private final PokemonRepository pokemonRepository;
    private final PokemonMoveRepository pokemonMoveRepository;

    public PokemonMoveService(RestTemplate restTemplate,
                              PokemonRepository pokemonRepository,
                              PokemonMoveRepository pokemonMoveRepository) {
        this.restTemplate = restTemplate;
        this.pokemonRepository = pokemonRepository;
        this.pokemonMoveRepository = pokemonMoveRepository;
    }

    public List<PokemonMove> getMovesForPokemon(Long pokemonId) {
        Pokemon pokemon = pokemonRepository.findById(pokemonId)
                .orElseThrow(() -> new ResourceNotFoundException("Pokemon not found with id: " + pokemonId));

        List<PokemonMove> moves = pokemonMoveRepository.findByPokemonIdOrderByNameAsc(pokemonId);

        if (moves.isEmpty()) {
            importMoveNamesFromPokeApi(pokemon);
            moves = pokemonMoveRepository.findByPokemonIdOrderByNameAsc(pokemonId);
        }

        if (moves.stream().anyMatch(this::isMissingMoveDetails)) {
            enrichMissingMoveDetails(moves);
            moves = pokemonMoveRepository.findByPokemonIdOrderByNameAsc(pokemonId);
        }

        return moves;
    }

    public void saveMoveNamesForPokemon(Pokemon pokemon, List<PokeApiPokemonResponse.PokemonMoveSlot> moveSlots) {
        if (pokemon == null || pokemon.getId() == null || moveSlots == null || moveSlots.isEmpty()) {
            return;
        }

        moveSlots.stream()
                .filter(slot -> slot.getMove() != null && slot.getMove().getName() != null)
                .sorted(Comparator.comparing(slot -> slot.getMove().getName()))
                .forEach(slot -> saveMoveNameIfMissing(pokemon, slot));
    }

    private void importMoveNamesFromPokeApi(Pokemon pokemon) {
        try {
            PokeApiPokemonResponse response = restTemplate.getForObject(
                    POKE_API_POKEMON_URL + pokemon.getId(),
                    PokeApiPokemonResponse.class
            );

            if (response != null) {
                saveMoveNamesForPokemon(pokemon, response.getMoves());
            }
        } catch (RestClientException e) {
            logger.warn("Could not import moves for Pokemon #{}: {}", pokemon.getId(), e.getMessage());
        }
    }

    private void saveMoveNameIfMissing(Pokemon pokemon, PokeApiPokemonResponse.PokemonMoveSlot slot) {
        String name = slot.getMove().getName();

        if (pokemonMoveRepository.existsByPokemonIdAndName(pokemon.getId(), name)) {
            return;
        }

        PokemonMove pokemonMove = new PokemonMove(pokemon, name, resolveLearnMethod(slot));
        pokemonMoveRepository.save(pokemonMove);
    }

    private void enrichMissingMoveDetails(List<PokemonMove> moves) {
        List<PokemonMove> movesToEnrich = moves.stream()
                .filter(this::isMissingMoveDetails)
                .toList();

        Map<Long, PokeApiMoveResponse> detailsByMoveId = new ConcurrentHashMap<>();
        movesToEnrich.parallelStream()
                .filter(move -> move.getId() != null)
                .forEach(move -> {
                    PokeApiMoveResponse details = fetchMoveDetails(move.getName());
                    if (details != null) {
                        detailsByMoveId.put(move.getId(), details);
                    }
                });

        movesToEnrich.forEach(move -> {
            applyMoveDetails(move, detailsByMoveId.get(move.getId()));
            pokemonMoveRepository.save(move);
        });
    }

    private boolean isMissingMoveDetails(PokemonMove move) {
        return move.getType() == null || move.getType().isBlank();
    }

    private PokeApiMoveResponse fetchMoveDetails(String moveName) {
        try {
            return restTemplate.getForObject(POKE_API_MOVE_URL + moveName, PokeApiMoveResponse.class);
        } catch (RestClientException e) {
            logger.warn("Could not import details for move {}: {}", moveName, e.getMessage());
            return null;
        }
    }

    private void applyMoveDetails(PokemonMove pokemonMove, PokeApiMoveResponse moveDetails) {
        if (moveDetails == null) {
            return;
        }

        if (moveDetails.getType() != null) {
            pokemonMove.setType(moveDetails.getType().getName());
        }
        pokemonMove.setPowerValue(moveDetails.getPower());
        pokemonMove.setAccuracy(moveDetails.getAccuracy());
    }

    private String resolveLearnMethod(PokeApiPokemonResponse.PokemonMoveSlot slot) {
        if (slot.getVersion_group_details() == null || slot.getVersion_group_details().isEmpty()) {
            return null;
        }

        PokeApiPokemonResponse.VersionGroupDetail detail = slot.getVersion_group_details().get(0);
        if (detail.getMove_learn_method() == null) {
            return null;
        }

        return detail.getMove_learn_method().getName();
    }

}
