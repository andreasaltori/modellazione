package it.unicam.cs.mpgc.rpg122627.engine;

/** Sorgente di casualità del gioco. Astratta per poter usare dadi truccati nei test. */
@FunctionalInterface
public interface Dice {

    /** Tira un dado con il numero di facce indicato; il risultato è tra 1 e {@code sides}. */
    int roll(int sides);
}
