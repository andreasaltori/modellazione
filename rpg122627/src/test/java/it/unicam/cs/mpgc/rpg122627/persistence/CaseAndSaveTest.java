package it.unicam.cs.mpgc.rpg122627.persistence;

import it.unicam.cs.mpgc.rpg122627.engine.GameSession;
import it.unicam.cs.mpgc.rpg122627.engine.GameState;
import it.unicam.cs.mpgc.rpg122627.engine.GameStatus;
import it.unicam.cs.mpgc.rpg122627.engine.action.ExamineAction;
import it.unicam.cs.mpgc.rpg122627.engine.action.InterrogateAction;
import it.unicam.cs.mpgc.rpg122627.engine.action.TravelAction;
import it.unicam.cs.mpgc.rpg122627.model.CaseFile;
import it.unicam.cs.mpgc.rpg122627.model.Investigator;
import it.unicam.cs.mpgc.rpg122627.model.Skill;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CaseAndSaveTest {

    private static final String TINY_CASE = """
            {"id":"t","title":"T","intro":"I","timeLimit":5,"startLocation":"a","culprit":"p",
             "requiredEvidence":1,"keyClues":["c"],"solution":"S",
             "locations":[{"id":"a","name":"A","description":"d","exits":[]}],
             "clues":[{"id":"c","title":"C","description":"d","hint":"h","location":"a","skill":"logic","difficulty":5}],
             "people":[{"id":"p","name":"P","description":"d","location":"a","topics":[]}]}
            """;

    @Test
    void builtInCaseIsListedAndLoadable() throws IOException {
        ClasspathCaseRepository repository = new ClasspathCaseRepository();
        assertTrue(repository.availableCaseIds().contains("villa-moretti"));
        CaseFile caseFile = repository.load("villa-moretti");
        assertEquals("ferri", caseFile.culpritId());
        assertTrue(caseFile.keyClues().size() >= caseFile.requiredEvidence());
    }

    @Test
    void unknownCaseIsReportedAsIoError() {
        assertThrows(IOException.class, () -> new ClasspathCaseRepository().load("non-esiste"));
    }

    @Test
    void minimalCaseParses() {
        assertEquals("t", CaseFileParser.parse(TINY_CASE).id());
    }

    @Test
    void caseWithDanglingReferenceIsRejected() {
        String broken = TINY_CASE.replace("\"culprit\":\"p\"", "\"culprit\":\"nessuno\"");
        assertThrows(IllegalArgumentException.class, () -> CaseFileParser.parse(broken));
    }

    @Test
    void caseWithUnreachableLocationIsRejected() {
        String broken = TINY_CASE.replace("\"exits\":[]}]", "\"exits\":[]},{\"id\":\"b\",\"name\":\"B\",\"description\":\"d\",\"exits\":[]}]");
        assertThrows(IllegalArgumentException.class, () -> CaseFileParser.parse(broken));
    }

    @Test
    void caseWithUnobtainableClueIsRejected() {
        String broken = TINY_CASE.replace("\"clues\":[", "\"clues\":[{\"id\":\"orfano\",\"title\":\"O\",\"description\":\"d\"},");
        assertThrows(IllegalArgumentException.class, () -> CaseFileParser.parse(broken));
    }

    @Test
    void saveAndLoadRoundTripPreservesTheGame() throws IOException {
        CaseFile caseFile = new ClasspathCaseRepository().load("villa-moretti");
        GameSession session = GameSession.newGame(caseFile, Investigator.create("Marco", Skill.PERCEPTION), sides -> sides);
        session.perform(new TravelAction("studio"));
        session.perform(new ExamineAction("bicchiere"));
        session.perform(new TravelAction("atrio"));
        session.perform(new InterrogateAction("ferri", "ferri-orario"));

        Path directory = Files.createTempDirectory("rpg122627-test");
        FileSaveRepository repository = new FileSaveRepository(directory);
        repository.save("slot1", SaveData.from(caseFile.id(), session.state()));

        Optional<SaveData> loaded = repository.load("slot1");
        assertTrue(loaded.isPresent());
        GameState restored = loaded.get().toState();
        GameState original = session.state();
        assertEquals(original.location(), restored.location());
        assertEquals(original.timeLeft(), restored.timeLeft());
        assertEquals(original.foundClues(), restored.foundClues());
        assertEquals(original.resolvedTopics(), restored.resolvedTopics());
        assertEquals(original.status(), restored.status());
        assertEquals(original.investigator().name(), restored.investigator().name());
        assertEquals(original.investigator().level(), restored.investigator().level());
        assertEquals(original.investigator().experience(), restored.investigator().experience());
        assertEquals(original.investigator().skills(), restored.investigator().skills());
        assertEquals(GameStatus.IN_PROGRESS, GameSession.restore(caseFile, restored, sides -> 1).state().status());
    }

    @Test
    void loadingMissingSlotGivesEmpty() throws IOException {
        Path directory = Files.createTempDirectory("rpg122627-test");
        assertTrue(new FileSaveRepository(directory).load("vuoto").isEmpty());
    }

    @Test
    void corruptedSaveIsReportedAsIoError() throws IOException {
        Path directory = Files.createTempDirectory("rpg122627-test");
        Files.writeString(directory.resolve("rotto.json"), "{ non json");
        assertThrows(IOException.class, () -> new FileSaveRepository(directory).load("rotto"));
    }

    @Test
    void slotNamesCannotEscapeTheDirectory() throws IOException {
        Path directory = Files.createTempDirectory("rpg122627-test");
        assertThrows(IllegalArgumentException.class, () -> new FileSaveRepository(directory).load("../fuori"));
    }
}
