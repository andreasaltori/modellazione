package it.unicam.cs.mpgc.rpg122627.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * Salva ogni slot come file JSON ({@code <slot>.json}) in una cartella. La scrittura passa da un
 * file temporaneo, così un errore a metà non rovina un salvataggio precedente.
 */
public final class FileSaveRepository implements SaveRepository {

    private final Path directory;

    public FileSaveRepository(Path directory) {
        this.directory = directory;
    }

    /** Cartella predefinita: {@code ~/.rpg122627/saves}. */
    public static Path defaultDirectory() {
        return Path.of(System.getProperty("user.home"), ".rpg122627", "saves");
    }

    @Override
    public void save(String slot, SaveData data) throws IOException {
        Path target = fileFor(slot);
        Files.createDirectories(directory);
        Path temporary = directory.resolve(slot + ".json.tmp");
        Files.writeString(temporary, Json.write(data.toJson()), StandardCharsets.UTF_8);
        Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public Optional<SaveData> load(String slot) throws IOException {
        Path source = fileFor(slot);
        if (!Files.exists(source)) {
            return Optional.empty();
        }
        try {
            return Optional.of(SaveData.fromJson(Json.object(Json.parse(Files.readString(source, StandardCharsets.UTF_8)))));
        } catch (IllegalArgumentException e) {
            throw new IOException("Salvataggio danneggiato: " + e.getMessage(), e);
        }
    }

    private Path fileFor(String slot) {
        if (!slot.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException("Nome di slot non valido: " + slot);
        }
        return directory.resolve(slot + ".json");
    }
}
