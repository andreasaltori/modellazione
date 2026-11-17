package it.unicam.cs.mpgc.rpg122627.persistence;

import it.unicam.cs.mpgc.rpg122627.model.CaseFile;

import java.io.IOException;
import java.util.List;

/**
 * Sorgente dei casi giocabili. Le implementazioni possono leggere da risorse interne, dal disco,
 * da un server: il resto del programma non cambia.
 */
public interface CaseRepository {

    /** Identificatori dei casi disponibili. */
    List<String> availableCaseIds() throws IOException;

    /** Carica un caso dato il suo identificatore. */
    CaseFile load(String caseId) throws IOException;
}
