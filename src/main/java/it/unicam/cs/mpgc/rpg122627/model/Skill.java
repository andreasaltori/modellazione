package it.unicam.cs.mpgc.rpg122627.model;

/**
 * Le abilità investigative del protagonista. Ogni prova (esame di un luogo, interrogatorio)
 * è associata a una di queste abilità.
 */
public enum Skill {
    PERCEPTION("Percezione"),
    LOGIC("Logica"),
    EMPATHY("Empatia");

    private final String label;

    Skill(String label) {
        this.label = label;
    }

    /** Nome da mostrare all'utente. */
    public String label() {
        return label;
    }
}
