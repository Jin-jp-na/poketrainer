package com.poketrainer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pokemon")
public class Pokemon {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String primaryType;
    private String secondaryType;

    private Integer hp;
    private Integer attackStat;
    private Integer defenseStat;
    private Integer speedStat;

    private String spriteUrl;

    private Float score;
    private Integer rankPosition;

    public Pokemon() {
    }

    public Pokemon(Long id, String name, String primaryType, String secondaryType,
                   Integer hp, Integer attackStat, Integer defenseStat, Integer speedStat,
                   String spriteUrl) {
        this.id = id;
        this.name = name;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
        this.hp = hp;
        this.attackStat = attackStat;
        this.defenseStat = defenseStat;
        this.speedStat = speedStat;
        this.spriteUrl = spriteUrl;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public String getSecondaryType() {
        return secondaryType;
    }

    public Integer getHp() {
        return hp;
    }

    public Integer getAttackStat() {
        return attackStat;
    }

    public Integer getDefenseStat() {
        return defenseStat;
    }

    public Integer getSpeedStat() {
        return speedStat;
    }

    public String getSpriteUrl() {
        return spriteUrl;
    }

    public Float getScore() {
        return score;
    }

    public Integer getRankPosition() {
        return rankPosition;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrimaryType(String primaryType) {
        this.primaryType = primaryType;
    }

    public void setSecondaryType(String secondaryType) {
        this.secondaryType = secondaryType;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    public void setAttackStat(Integer attackStat) {
        this.attackStat = attackStat;
    }

    public void setDefenseStat(Integer defenseStat) {
        this.defenseStat = defenseStat;
    }

    public void setSpeedStat(Integer speedStat) {
        this.speedStat = speedStat;
    }

    public void setSpriteUrl(String spriteUrl) {
        this.spriteUrl = spriteUrl;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setRankPosition(Integer rankPosition) {
        this.rankPosition = rankPosition;
    }
}