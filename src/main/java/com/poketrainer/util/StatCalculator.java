package com.poketrainer.util;

import com.poketrainer.model.Pokemon;

public class StatCalculator {

    private StatCalculator() {
    }

    public static float calculateScore(Pokemon pokemon) {
        int hp = safe(pokemon.getHp());
        int attack = safe(pokemon.getAttackStat());
        int defense = safe(pokemon.getDefenseStat());
        int speed = safe(pokemon.getSpeedStat());

        return (hp*0.25f) + (attack * 0.35f) + (defense*0.10f) + (speed *0.3f);
    }

    private static int safe(Integer value) {
        return value == null ? 0 : value;
    }
}