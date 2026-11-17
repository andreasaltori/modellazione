package it.unicam.cs.mpgc.rpg122627.model;

import java.util.Objects;

/**
 * Un argomento di conversazione con una persona.
 *
 * @param grantsClue indizio ottenuto se la prova riesce, oppure {@code null}
 * @param requires   indizio che il giocatore deve già avere per poter sollevare l'argomento, oppure {@code null}
 */
public record Topic(String id, String question, String answer, Skill skill, int difficulty,
                    String grantsClue, String requires) {

    public Topic {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(question, "question");
        Objects.requireNonNull(answer, "answer");
        Objects.requireNonNull(skill, "skill");
        if (difficulty < 1) {
            throw new IllegalArgumentException("Difficoltà non valida per l'argomento " + id);
        }
        grantsClue = grantsClue == null || grantsClue.isBlank() ? null : grantsClue;
        requires = requires == null || requires.isBlank() ? null : requires;
    }
}
