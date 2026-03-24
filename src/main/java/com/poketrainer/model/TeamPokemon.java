package com.poketrainer.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "team_pokemon")
public class TeamPokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int slotNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonBackReference
    private Team team;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pokemon_id", nullable = false)
    private Pokemon pokemon;

    @OneToMany(mappedBy = "teamPokemon", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Move> moves = new ArrayList<>();

    public TeamPokemon() {
    }

    public TeamPokemon(int slotNumber, Team team, Pokemon pokemon) {
        this.slotNumber = slotNumber;
        this.team = team;
        this.pokemon = pokemon;
    }

    public Long getId() {
        return id;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public Team getTeam() {
        return team;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setPokemon(Pokemon pokemon) {
        this.pokemon = pokemon;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }
}