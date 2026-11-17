package it.unicam.cs.mpgc.rpg122627.engine;

import it.unicam.cs.mpgc.rpg122627.model.Investigator;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * La parte variabile di una partita: è tutto ciò che viene salvato. I dati statici del caso
 * stanno in {@code CaseFile}.
 */
public final class GameState {

    private final Investigator investigator;
    private final Set<String> foundClues = new LinkedHashSet<>();
    private final Set<String> resolvedTopics = new LinkedHashSet<>();
    private String location;
    private int timeLeft;
    private GameStatus status;

    public GameState(Investigator investigator, String location, int timeLeft,
                     Collection<String> foundClues, Collection<String> resolvedTopics, GameStatus status) {
        this.investigator = Objects.requireNonNull(investigator, "investigator");
        this.location = Objects.requireNonNull(location, "location");
        this.timeLeft = Math.max(0, timeLeft);
        this.foundClues.addAll(foundClues);
        this.resolvedTopics.addAll(resolvedTopics);
        this.status = Objects.requireNonNull(status, "status");
    }

    public Investigator investigator() {
        return investigator;
    }

    public String location() {
        return location;
    }

    public int timeLeft() {
        return timeLeft;
    }

    public GameStatus status() {
        return status;
    }

    public Set<String> foundClues() {
        return Collections.unmodifiableSet(foundClues);
    }

    public Set<String> resolvedTopics() {
        return Collections.unmodifiableSet(resolvedTopics);
    }

    public boolean hasClue(String clueId) {
        return foundClues.contains(clueId);
    }

    public boolean isTopicResolved(String topicId) {
        return resolvedTopics.contains(topicId);
    }

    public void moveTo(String locationId) {
        this.location = Objects.requireNonNull(locationId, "locationId");
    }

    public void spendTime(int amount) {
        timeLeft = Math.max(0, timeLeft - amount);
    }

    public void addClue(String clueId) {
        foundClues.add(clueId);
    }

    public void resolveTopic(String topicId) {
        resolvedTopics.add(topicId);
    }

    public void setStatus(GameStatus status) {
        this.status = Objects.requireNonNull(status, "status");
    }
}
