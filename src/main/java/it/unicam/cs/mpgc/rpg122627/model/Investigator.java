package it.unicam.cs.mpgc.rpg122627.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Il personaggio controllato dal giocatore. Responsabilità: nome, livello, esperienza e valori
 * delle abilità; la regola di avanzamento di livello vive qui e solo qui.
 */
public final class Investigator {

    public static final int BASE_SKILL = 2;
    public static final int SPECIALTY_BONUS = 2;
    private static final int EXPERIENCE_PER_LEVEL = 5;

    private final String name;
    private final Map<Skill, Integer> skills = new EnumMap<>(Skill.class);
    private int level;
    private int experience;

    /** Ricostruisce un investigatore esistente (usato anche dalla persistenza). */
    public Investigator(String name, int level, int experience, Map<Skill, Integer> skillValues) {
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Il nome non può essere vuoto");
        }
        if (level < 1 || experience < 0) {
            throw new IllegalArgumentException("Livello o esperienza non validi");
        }
        this.name = name.trim();
        this.level = level;
        this.experience = experience;
        for (Skill skill : Skill.values()) {
            skills.put(skill, skillValues.getOrDefault(skill, BASE_SKILL));
        }
    }

    /** Crea un nuovo investigatore di livello 1 con una specializzazione. */
    public static Investigator create(String name, Skill specialty) {
        Objects.requireNonNull(specialty, "specialty");
        return new Investigator(name, 1, 0, Map.of(specialty, BASE_SKILL + SPECIALTY_BONUS));
    }

    public String name() {
        return name;
    }

    public int level() {
        return level;
    }

    public int experience() {
        return experience;
    }

    public int experienceForNextLevel() {
        return EXPERIENCE_PER_LEVEL * level;
    }

    public int skill(Skill skill) {
        return skills.get(skill);
    }

    public Map<Skill, Integer> skills() {
        return Collections.unmodifiableMap(skills);
    }

    /**
     * Assegna esperienza guadagnata usando un'abilità. Se si sale di livello, l'abilità usata
     * aumenta di 1 ("si impara facendo").
     *
     * @return true se almeno un livello è stato guadagnato
     */
    public boolean gainExperience(Skill used, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("L'esperienza non può essere negativa");
        }
        experience += amount;
        boolean levelledUp = false;
        while (experience >= experienceForNextLevel()) {
            experience -= experienceForNextLevel();
            level++;
            skills.merge(used, 1, Integer::sum);
            levelledUp = true;
        }
        return levelledUp;
    }
}
