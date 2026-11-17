package it.unicam.cs.mpgc.rpg122627.engine;

import java.util.Random;
import java.util.random.RandomGenerator;

/** Dado basato su un generatore pseudocasuale. */
public final class RandomDice implements Dice {

    private final RandomGenerator generator;

    public RandomDice() {
        this(new Random());
    }

    public RandomDice(RandomGenerator generator) {
        this.generator = generator;
    }

    @Override
    public int roll(int sides) {
        if (sides < 1) {
            throw new IllegalArgumentException("Un dado ha almeno una faccia");
        }
        return generator.nextInt(sides) + 1;
    }
}
