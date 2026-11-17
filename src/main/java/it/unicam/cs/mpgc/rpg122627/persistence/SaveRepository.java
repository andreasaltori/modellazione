package it.unicam.cs.mpgc.rpg122627.persistence;

import java.io.IOException;
import java.util.Optional;

/**
 * Archivio dei salvataggi, organizzato in "slot" con nome. Le implementazioni possono usare file,
 * database o servizi remoti senza che il motore di gioco se ne accorga.
 */
public interface SaveRepository {

    void save(String slot, SaveData data) throws IOException;

    /** Carica uno slot; vuoto se non esiste. */
    Optional<SaveData> load(String slot) throws IOException;
}
