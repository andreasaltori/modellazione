package it.unicam.cs.mpgc.rpg122627.engine;

/**
 * Esito di una prova di abilità: d20 + valore dell'abilità contro una difficoltà.
 */
public record SkillCheck(int roll, int bonus, int difficulty) {

    public static final int SIDES = 20;

    /** Esegue una prova tirando il dado indicato. */
    public static SkillCheck attempt(Dice dice, int bonus, int difficulty) {
        return new SkillCheck(dice.roll(SIDES), bonus, difficulty);
    }

    public int total() {
        return roll + bonus;
    }

    public boolean success() {
        return total() >= difficulty;
    }

    /** Descrizione leggibile, ad esempio {@code [d20: 12 + 3 = 15 contro 13]}. */
    public String describe() {
        return "[d20: " + roll + " + " + bonus + " = " + total() + " contro " + difficulty + "]";
    }
}
