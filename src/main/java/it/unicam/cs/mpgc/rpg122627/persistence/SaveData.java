package it.unicam.cs.mpgc.rpg122627.persistence;

import it.unicam.cs.mpgc.rpg122627.engine.GameState;
import it.unicam.cs.mpgc.rpg122627.engine.GameStatus;
import it.unicam.cs.mpgc.rpg122627.model.Investigator;
import it.unicam.cs.mpgc.rpg122627.model.Skill;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fotografia serializzabile di una partita. Contiene solo lo stato variabile e l'identificatore
 * del caso: i dati statici del caso non vengono duplicati nel salvataggio.
 */
public record SaveData(String caseId, String playerName, int level, int experience, Map<String, Integer> skills,
                       String location, int timeLeft, List<String> clues, List<String> topics, String status) {

    public SaveData {
        skills = Map.copyOf(skills);
        clues = List.copyOf(clues);
        topics = List.copyOf(topics);
    }

    /** Crea il salvataggio a partire dallo stato corrente. */
    public static SaveData from(String caseId, GameState state) {
        Investigator investigator = state.investigator();
        Map<String, Integer> skills = new LinkedHashMap<>();
        investigator.skills().forEach((skill, value) -> skills.put(skill.name(), value));
        return new SaveData(caseId, investigator.name(), investigator.level(), investigator.experience(), skills,
                state.location(), state.timeLeft(), List.copyOf(state.foundClues()),
                List.copyOf(state.resolvedTopics()), state.status().name());
    }

    /** Ricostruisce lo stato di gioco. */
    public GameState toState() {
        Map<Skill, Integer> skillValues = new EnumMap<>(Skill.class);
        skills.forEach((name, value) -> skillValues.put(Skill.valueOf(name), value));
        Investigator investigator = new Investigator(playerName, level, experience, skillValues);
        return new GameState(investigator, location, timeLeft, clues, topics, GameStatus.valueOf(status));
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("caseId", caseId);
        json.put("playerName", playerName);
        json.put("level", level);
        json.put("experience", experience);
        json.put("skills", new LinkedHashMap<String, Object>(skills));
        json.put("location", location);
        json.put("timeLeft", timeLeft);
        json.put("clues", List.copyOf(clues));
        json.put("topics", List.copyOf(topics));
        json.put("status", status);
        return json;
    }

    public static SaveData fromJson(Map<String, Object> json) {
        Map<String, Integer> skills = new LinkedHashMap<>();
        Json.object(json.get("skills")).forEach((name, value) -> {
            if (!(value instanceof Number number)) {
                throw new IllegalArgumentException("Valore di abilità non valido: " + name);
            }
            skills.put(name, number.intValue());
        });
        return new SaveData(Json.string(json, "caseId"), Json.string(json, "playerName"),
                Json.integer(json, "level"), Json.integer(json, "experience"), skills,
                Json.string(json, "location"), Json.integer(json, "timeLeft"),
                Json.strings(json, "clues"), Json.strings(json, "topics"), Json.string(json, "status"));
    }
}
