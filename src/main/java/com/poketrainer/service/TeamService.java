package com.poketrainer.service;

import com.poketrainer.dto.AddMoveRequest;
import com.poketrainer.dto.AddPokemonToTeamRequest;
import com.poketrainer.dto.CreateTeamRequest;
import com.poketrainer.exception.ResourceNotFoundException;
import com.poketrainer.model.Move;
import com.poketrainer.model.Pokemon;
import com.poketrainer.model.Team;
import com.poketrainer.model.TeamPokemon;
import com.poketrainer.model.User;
import com.poketrainer.repository.MoveRepository;
import com.poketrainer.repository.PokemonRepository;
import com.poketrainer.repository.TeamPokemonRepository;
import com.poketrainer.repository.TeamRepository;
import com.poketrainer.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamPokemonRepository teamPokemonRepository;
    private final PokemonRepository pokemonRepository;
    private final MoveRepository moveRepository;

    public TeamService(TeamRepository teamRepository,
                       UserRepository userRepository,
                       TeamPokemonRepository teamPokemonRepository,
                       PokemonRepository pokemonRepository,
                       MoveRepository moveRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.teamPokemonRepository = teamPokemonRepository;
        this.pokemonRepository = pokemonRepository;
        this.moveRepository = moveRepository;
    }

    public Team createTeam(CreateTeamRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Team team = new Team(request.getName(), user);
        return teamRepository.save(team);
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Team getTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
    }

    public TeamPokemon addPokemonToTeam(Long teamId, AddPokemonToTeamRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        if (team.getTeamPokemons().size() >= 6) {
            throw new IllegalArgumentException("A team cannot have more than 6 Pokemon");
        }

        Pokemon pokemon = pokemonRepository.findById(request.getPokemonId())
                .orElseThrow(() -> new ResourceNotFoundException("Pokemon not found"));

        TeamPokemon teamPokemon = new TeamPokemon(
                team.getTeamPokemons().size() + 1,
                team,
                pokemon
        );

        return teamPokemonRepository.save(teamPokemon);
    }

    public Move addMoveToTeamPokemon(Long teamPokemonId, AddMoveRequest request) {
        TeamPokemon teamPokemon = teamPokemonRepository.findById(teamPokemonId)
                .orElseThrow(() -> new ResourceNotFoundException("Team Pokemon not found"));

        if (teamPokemon.getMoves().size() >= 4) {
            throw new IllegalArgumentException("A Pokemon cannot have more than 4 moves");
        }

        Move move = new Move(
                request.getName(),
                request.getType(),
                request.getPowerValue(),
                teamPokemon
        );

        return moveRepository.save(move);
    }

    public void deleteTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        teamRepository.delete(team);
    }
}