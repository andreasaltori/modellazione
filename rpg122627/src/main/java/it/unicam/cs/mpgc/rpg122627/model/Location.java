package it.unicam.cs.mpgc.rpg122627.model;

import java.util.List;
import java.util.Objects;

/** Un luogo del caso, collegato ad altri luoghi tramite le uscite. */
public record Location(String id, String name, String description, List<String> exits) {

    public Location {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(description, "description");
        exits = List.copyOf(exits);
    }
}
