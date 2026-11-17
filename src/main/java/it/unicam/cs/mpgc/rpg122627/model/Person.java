package it.unicam.cs.mpgc.rpg122627.model;

import java.util.List;
import java.util.Objects;

/** Un personaggio non giocante: sospettato o testimone. */
public record Person(String id, String name, String description, String locationId, List<Topic> topics) {

    public Person {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(locationId, "locationId");
        topics = List.copyOf(topics);
    }
}
