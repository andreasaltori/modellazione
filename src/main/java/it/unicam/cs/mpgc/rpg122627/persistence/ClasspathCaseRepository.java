package it.unicam.cs.mpgc.rpg122627.persistence;

import it.unicam.cs.mpgc.rpg122627.model.CaseFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Legge i casi dalle risorse del programma: l'elenco in {@code /cases/index.json} e ogni caso in
 * {@code /cases/<id>.json}.
 */
public final class ClasspathCaseRepository implements CaseRepository {

    private static final String BASE = "/cases/";

    @Override
    public List<String> availableCaseIds() throws IOException {
        try {
            return Json.array(Json.parse(read("index"))).stream().map(String::valueOf).toList();
        } catch (IllegalArgumentException e) {
            throw new IOException("Indice dei casi non valido", e);
        }
    }

    @Override
    public CaseFile load(String caseId) throws IOException {
        if (!caseId.matches("[A-Za-z0-9_-]+")) {
            throw new IOException("Identificatore di caso non valido: " + caseId);
        }
        try {
            return CaseFileParser.parse(read(caseId));
        } catch (IllegalArgumentException e) {
            throw new IOException("Il caso " + caseId + " non è valido: " + e.getMessage(), e);
        }
    }

    private String read(String name) throws IOException {
        try (InputStream in = ClasspathCaseRepository.class.getResourceAsStream(BASE + name + ".json")) {
            if (in == null) {
                throw new IOException("Risorsa non trovata: " + BASE + name + ".json");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
