package com.poketrainer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(
        name = "pokemon_moves",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_pokemon_move",
                columnNames = {"pokemon_id", "name"}
        )
)
public class PokemonMove {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    @JsonIgnore
    private Pokemon pokemon;

    @Column(nullable = false)
    private String name;

    private String type;
    private Integer powerValue;
    private Integer accuracy;
    private String learnMethod;

    public PokemonMove() {
    }

    public PokemonMove(Pokemon pokemon, String name, String learnMethod) {
        this.pokemon = pokemon;
        this.name = name;
        this.learnMethod = learnMethod;
    }

    public Long getId() {
        return id;
    }

    public Pokemon getPokemon() {
        return pokemon;
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

    public Integer getAccuracy() {
        return accuracy;
    }

    public String getLearnMethod() {
        return learnMethod;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPokemon(Pokemon pokemon) {
        this.pokemon = pokemon;
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

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }

    public void setLearnMethod(String learnMethod) {
        this.learnMethod = learnMethod;
    }
}
