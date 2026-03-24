package com.poketrainer.controller;

import com.poketrainer.dto.AddMoveRequest;
import com.poketrainer.dto.AddPokemonToTeamRequest;
import com.poketrainer.dto.CreateTeamRequest;
import com.poketrainer.model.Move;
import com.poketrainer.model.Team;
import com.poketrainer.model.TeamPokemon;
import com.poketrainer.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Team createTeam(@Valid @RequestBody CreateTeamRequest request) {
        return teamService.createTeam(request);
    }

    @GetMapping
    public List<Team> getAllTeams() {
        return teamService.getAllTeams();
    }

    @GetMapping("/{teamId}")
    public Team getTeamById(@PathVariable Long teamId) {
        return teamService.getTeamById(teamId);
    }

    @PostMapping("/{teamId}/pokemon")
    @ResponseStatus(HttpStatus.CREATED)
    public TeamPokemon addPokemonToTeam(@PathVariable Long teamId,
                                        @Valid @RequestBody AddPokemonToTeamRequest request) {
        return teamService.addPokemonToTeam(teamId, request);
    }

    @PostMapping("/pokemon/{teamPokemonId}/moves")
    @ResponseStatus(HttpStatus.CREATED)
    public Move addMoveToTeamPokemon(@PathVariable Long teamPokemonId,
                                     @Valid @RequestBody AddMoveRequest request) {
        return teamService.addMoveToTeamPokemon(teamPokemonId, request);
    }

    @DeleteMapping("/{teamId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeam(@PathVariable Long teamId) {
        teamService.deleteTeam(teamId);
    }
}