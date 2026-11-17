package it.unicam.cs.mpgc.rpg122627.model;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * La definizione completa di un caso (dati statici, mai modificati durante la partita).
 * Il costruttore garantisce che il caso sia coerente: riferimenti esistenti, luoghi raggiungibili,
 * indizi ottenibili e soluzione dimostrabile.
 *
 * @param keyClues         indizi decisivi per dimostrare la colpevolezza
 * @param requiredEvidence quanti indizi decisivi servono per un'accusa fondata
 */
public record CaseFile(String id, String title, String intro, int timeLimit, String startLocation,
                       Map<String, Location> locations, Map<String, Person> people, Map<String, Clue> clues,
                       String culpritId, int requiredEvidence, Set<String> keyClues, String solution) {

    public CaseFile {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(intro, "intro");
        Objects.requireNonNull(solution, "solution");
        locations = Collections.unmodifiableMap(new LinkedHashMap<>(locations));
        people = Collections.unmodifiableMap(new LinkedHashMap<>(people));
        clues = Collections.unmodifiableMap(new LinkedHashMap<>(clues));
        keyClues = Collections.unmodifiableSet(new LinkedHashSet<>(keyClues));

        require(timeLimit > 0, "Il tempo disponibile deve essere positivo");
        require(locations.containsKey(startLocation), "Luogo iniziale inesistente: " + startLocation);
        for (Location location : locations.values()) {
            for (String exit : location.exits()) {
                require(locations.containsKey(exit), "Uscita inesistente " + exit + " da " + location.id());
            }
        }
        Set<String> grantedByTopics = new HashSet<>();
        for (Person person : people.values()) {
            require(locations.containsKey(person.locationId()), "Luogo inesistente per " + person.id());
            for (Topic topic : person.topics()) {
                requireClueOrNull(clues, topic.grantsClue());
                requireClueOrNull(clues, topic.requires());
                if (topic.grantsClue() != null) {
                    grantedByTopics.add(topic.grantsClue());
                }
            }
        }
        for (Clue clue : clues.values()) {
            require(clue.locationId() == null || locations.containsKey(clue.locationId()),
                    "Luogo inesistente per l'indizio " + clue.id());
            requireClueOrNull(clues, clue.requires());
            require(clue.locationId() != null || grantedByTopics.contains(clue.id()),
                    "L'indizio " + clue.id() + " non è ottenibile in nessun modo");
        }
        require(people.containsKey(culpritId), "Colpevole inesistente: " + culpritId);
        require(!keyClues.isEmpty() && clues.keySet().containsAll(keyClues), "Indizi decisivi non validi");
        require(requiredEvidence >= 1 && requiredEvidence <= keyClues.size(),
                "Il numero di prove richieste deve essere tra 1 e " + keyClues.size());
        Set<String> reachable = reachableFrom(startLocation, locations);
        require(reachable.containsAll(locations.keySet()), "Ci sono luoghi non raggiungibili dall'inizio");
    }

    private static Set<String> reachableFrom(String start, Map<String, Location> locations) {
        Set<String> seen = new HashSet<>(List.of(start));
        Deque<String> queue = new ArrayDeque<>(List.of(start));
        while (!queue.isEmpty()) {
            for (String next : locations.get(queue.poll()).exits()) {
                if (seen.add(next)) {
                    queue.add(next);
                }
            }
        }
        return seen;
    }

    private static void requireClueOrNull(Map<String, Clue> clues, String clueId) {
        require(clueId == null || clues.containsKey(clueId), "Indizio inesistente: " + clueId);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException("Caso non valido: " + message);
        }
    }
}
