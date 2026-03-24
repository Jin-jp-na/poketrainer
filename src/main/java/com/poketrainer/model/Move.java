package com.poketrainer.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "moves")
public class Move {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private Integer powerValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_pokemon_id", nullable = false)
    @JsonBackReference
    private TeamPokemon teamPokemon;

    public Move() {
    }

    public Move(String name, String type, Integer powerValue, TeamPokemon teamPokemon) {
        this.name = name;
        this.type = type;
        this.powerValue = powerValue;
        this.teamPokemon = teamPokemon;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Integer getPowerValue() {
        return powerValue;
    }

    public TeamPokemon getTeamPokemon() {
        return teamPokemon;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPowerValue(Integer powerValue) {
        this.powerValue = powerValue;
    }

    public void setTeamPokemon(TeamPokemon teamPokemon) {
        this.teamPokemon = teamPokemon;
    }
}