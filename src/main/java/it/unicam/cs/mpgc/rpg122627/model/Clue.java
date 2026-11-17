package it.unicam.cs.mpgc.rpg122627.model;

import java.util.Objects;

/**
 * Un indizio del caso. Può essere trovato esaminando un luogo (se {@code locationId} è presente)
 * oppure ottenuto interrogando una persona (se è citato da un {@link Topic}).
 *
 * @param hint       cosa il giocatore vede prima di scoprire l'indizio
 * @param locationId luogo in cui si cerca l'indizio, {@code null} se si ottiene solo da un interrogatorio
 * @param skill      abilità richiesta per trovarlo (solo per indizi legati a un luogo)
 * @param difficulty difficoltà della prova (solo per indizi legati a un luogo)
 * @param requires   indizio che deve essere già noto per poter cercare questo, oppure {@code null}
 */
public record Clue(String id, String title, String description, String hint, String locationId,
                   Skill skill, int difficulty, String requires) {

    public Clue {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(description, "description");
        locationId = blankToNull(locationId);
        requires = blankToNull(requires);
        if (locationId != null && (skill == null || hint == null || difficulty < 1)) {
            throw new IllegalArgumentException("L'indizio " + id + " è legato a un luogo: servono hint, skill e difficoltà");
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
