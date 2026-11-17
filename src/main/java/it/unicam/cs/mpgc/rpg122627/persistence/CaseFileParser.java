package it.unicam.cs.mpgc.rpg122627.persistence;

import it.unicam.cs.mpgc.rpg122627.model.CaseFile;
import it.unicam.cs.mpgc.rpg122627.model.Clue;
import it.unicam.cs.mpgc.rpg122627.model.Location;
import it.unicam.cs.mpgc.rpg122627.model.Person;
import it.unicam.cs.mpgc.rpg122627.model.Skill;
import it.unicam.cs.mpgc.rpg122627.model.Topic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Converte il JSON di un caso in un {@link CaseFile}. È l'unico punto che conosce il formato dei
 * file dei casi: per aggiungere un caso basta scrivere un nuovo file con questo formato.
 */
public final class CaseFileParser {

    private CaseFileParser() {
    }

    public static CaseFile parse(String json) {
        Map<String, Object> root = Json.object(Json.parse(json));

        Map<String, Location> locations = new LinkedHashMap<>();
        for (Object item : Json.arrayOrEmpty(root, "locations")) {
            Map<String, Object> o = Json.object(item);
            Location location = new Location(Json.string(o, "id"), Json.string(o, "name"),
                    Json.string(o, "description"), Json.strings(o, "exits"));
            locations.put(location.id(), location);
        }

        Map<String, Clue> clues = new LinkedHashMap<>();
        for (Object item : Json.arrayOrEmpty(root, "clues")) {
            Map<String, Object> o = Json.object(item);
            String skill = Json.optionalString(o, "skill");
            Clue clue = new Clue(Json.string(o, "id"), Json.string(o, "title"), Json.string(o, "description"),
                    Json.optionalString(o, "hint"), Json.optionalString(o, "location"),
                    skill == null ? null : parseSkill(skill),
                    o.containsKey("difficulty") ? Json.integer(o, "difficulty") : 0,
                    Json.optionalString(o, "requires"));
            clues.put(clue.id(), clue);
        }

        Map<String, Person> people = new LinkedHashMap<>();
        for (Object item : Json.arrayOrEmpty(root, "people")) {
            Map<String, Object> o = Json.object(item);
            List<Topic> topics = new ArrayList<>();
            for (Object topicItem : Json.arrayOrEmpty(o, "topics")) {
                Map<String, Object> t = Json.object(topicItem);
                topics.add(new Topic(Json.string(t, "id"), Json.string(t, "question"), Json.string(t, "answer"),
                        parseSkill(Json.string(t, "skill")), Json.integer(t, "difficulty"),
                        Json.optionalString(t, "grants"), Json.optionalString(t, "requires")));
            }
            Person person = new Person(Json.string(o, "id"), Json.string(o, "name"),
                    Json.string(o, "description"), Json.string(o, "location"), topics);
            people.put(person.id(), person);
        }

        return new CaseFile(Json.string(root, "id"), Json.string(root, "title"), Json.string(root, "intro"),
                Json.integer(root, "timeLimit"), Json.string(root, "startLocation"),
                locations, people, clues,
                Json.string(root, "culprit"), Json.integer(root, "requiredEvidence"),
                new java.util.LinkedHashSet<>(Json.strings(root, "keyClues")), Json.string(root, "solution"));
    }

    private static Skill parseSkill(String name) {
        try {
            return Skill.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Abilità sconosciuta: " + name, e);
        }
    }
}
