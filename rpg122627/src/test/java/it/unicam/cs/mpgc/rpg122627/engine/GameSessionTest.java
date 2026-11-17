package it.unicam.cs.mpgc.rpg122627.engine;

import it.unicam.cs.mpgc.rpg122627.engine.action.AccuseAction;
import it.unicam.cs.mpgc.rpg122627.engine.action.ExamineAction;
import it.unicam.cs.mpgc.rpg122627.engine.action.InterrogateAction;
import it.unicam.cs.mpgc.rpg122627.engine.action.TravelAction;
import it.unicam.cs.mpgc.rpg122627.model.CaseFile;
import it.unicam.cs.mpgc.rpg122627.model.Investigator;
import it.unicam.cs.mpgc.rpg122627.model.Skill;
import it.unicam.cs.mpgc.rpg122627.persistence.ClasspathCaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSessionTest {

    private static final Dice ALWAYS_SUCCEED = sides -> sides;
    private static final Dice ALWAYS_FAIL = sides -> 1;

    private CaseFile caseFile;

    @BeforeEach
    void loadCase() throws IOException {
        caseFile = new ClasspathCaseRepository().load("villa-moretti");
    }

    private GameSession newSession(Dice dice) {
        return GameSession.newGame(caseFile, Investigator.create("Test", Skill.LOGIC), dice);
    }

    @Test
    void travelMovesAndCostsTime() {
        GameSession session = newSession(ALWAYS_SUCCEED);
        ActionResult result = session.perform(new TravelAction("studio"));
        assertTrue(result.success());
        assertEquals("studio", session.state().location());
        assertEquals(caseFile.timeLimit() - 1, session.state().timeLeft());
    }

    @Test
    void travelToNonAdjacentLocationIsRejectedWithoutCost() {
        GameSession session = newSession(ALWAYS_SUCCEED);
        ActionResult result = session.perform(new TravelAction("cucina"));
        assertFalse(result.success());
        assertEquals("atrio", session.state().location());
        assertEquals(caseFile.timeLimit(), session.state().timeLeft());
    }

    @Test
    void successfulExamineAddsClueAndExperience() {
        GameSession session = newSession(ALWAYS_SUCCEED);
        session.perform(new TravelAction("studio"));
        ActionResult result = session.perform(new ExamineAction("bicchiere"));
        assertTrue(result.success());
        assertTrue(session.state().hasClue("bicchiere"));
        assertEquals(3, session.state().investigator().experience());
    }

    @Test
    void failedExamineCostsTimeButAllowsRetry() {
        GameSession session = newSession(ALWAYS_FAIL);
        session.perform(new TravelAction("studio"));
        ActionResult result = session.perform(new ExamineAction("bicchiere"));
        assertFalse(result.success());
        assertFalse(session.state().hasClue("bicchiere"));
        assertEquals(caseFile.timeLimit() - 2, session.state().timeLeft());
        assertTrue(session.availableActions().contains(new ExamineAction("bicchiere")));
    }

    @Test
    void examineOfClueInAnotherLocationIsRejected() {
        GameSession session = newSession(ALWAYS_SUCCEED);
        ActionResult result = session.perform(new ExamineAction("bicchiere"));
        assertFalse(result.success());
        assertEquals(caseFile.timeLimit(), session.state().timeLeft());
    }

    @Test
    void lockedTopicAppearsOnlyAfterRequiredClue() {
        GameSession session = newSession(ALWAYS_SUCCEED);
        InterrogateAction confront = new InterrogateAction("ferri", "ferri-bugia");
        assertFalse(session.availableActions().contains(confront));
        assertFalse(session.perform(confront).success());

        session.perform(new TravelAction("salotto"));
        session.perform(new TravelAction("cucina"));
        session.perform(new InterrogateAction("elena", "elena-sera"));
        session.perform(new TravelAction("salotto"));
        session.perform(new TravelAction("atrio"));
        assertTrue(session.availableActions().contains(confront));
        assertTrue(session.perform(confront).success());
        assertTrue(session.state().hasClue("contraddizione"));
    }

    @Test
    void resolvedTopicIsNotOfferedAgain() {
        GameSession session = newSession(ALWAYS_SUCCEED);
        InterrogateAction question = new InterrogateAction("ferri", "ferri-orario");
        assertTrue(session.perform(question).success());
        assertFalse(session.availableActions().contains(question));
    }

    @Test
    void timeRunningOutLosesTheGame() {
        GameSession session = newSession(ALWAYS_FAIL);
        for (int i = 0; i < caseFile.timeLimit(); i++) {
            session.perform(new TravelAction(i % 2 == 0 ? "studio" : "atrio"));
        }
        assertEquals(GameStatus.LOST, session.state().status());
        assertTrue(session.isOver());
        assertTrue(session.availableActions().isEmpty());
        assertFalse(session.perform(new TravelAction("atrio")).success());
    }

    @Test
    void correctAccusationWithEnoughEvidenceWins() {
        GameSession session = newSession(ALWAYS_SUCCEED);
        session.perform(new TravelAction("studio"));
        session.perform(new ExamineAction("bicchiere"));
        session.perform(new ExamineAction("registro"));
        session.perform(new TravelAction("atrio"));
        session.perform(new TravelAction("salotto"));
        session.perform(new ExamineAction("digossina"));

        ActionResult result = session.perform(new AccuseAction("ferri", Set.of("bicchiere", "registro", "digossina")));
        assertTrue(result.success());
        assertEquals(GameStatus.WON, session.state().status());
    }

    @Test
    void correctAccusationWithTooFewDecisiveCluesDoesNotEndTheGame() {
        GameSession session = newSession(ALWAYS_SUCCEED);
        session.perform(new TravelAction("studio"));
        session.perform(new ExamineAction("bicchiere"));
        int timeBefore = session.state().timeLeft();

        ActionResult result = session.perform(new AccuseAction("ferri", Set.of("bicchiere")));
        assertFalse(result.success());
        assertEquals(GameStatus.IN_PROGRESS, session.state().status());
        assertTrue(session.state().timeLeft() < timeBefore);
    }

    @Test
    void wrongSuspectLosesTheGame() {
        GameSession session = newSession(ALWAYS_SUCCEED);
        ActionResult result = session.perform(new AccuseAction("giulia", Set.of()));
        assertFalse(result.success());
        assertEquals(GameStatus.LOST, session.state().status());
    }

    @Test
    void accusationWithUnknownCluesIsRejected() {
        GameSession session = newSession(ALWAYS_SUCCEED);
        ActionResult result = session.perform(new AccuseAction("ferri", Set.of("registro")));
        assertFalse(result.success());
        assertEquals(GameStatus.IN_PROGRESS, session.state().status());
    }

    @Test
    void customActionProviderExtendsTheGameWithoutChangingTheEngine() {
        Action rest = new Action() {
            @Override
            public String describe(CaseFile caseFile) {
                return "Riposa";
            }

            @Override
            public ActionResult execute(GameSession session) {
                session.spendTime(1);
                return ActionResult.ok("Ti riposi.");
            }
        };
        GameSession session = GameSession.newGame(caseFile, Investigator.create("Test", Skill.LOGIC), ALWAYS_SUCCEED,
                List.of(s -> List.of(rest)));
        assertEquals(List.of(rest), session.availableActions());
        assertTrue(session.perform(rest).success());
        assertEquals(caseFile.timeLimit() - 1, session.state().timeLeft());
    }

    @Test
    void listenersAreNotifiedOfEveryAction() {
        GameSession session = newSession(ALWAYS_SUCCEED);
        List<ActionResult> received = new ArrayList<>();
        session.addListener((action, result) -> received.add(result));
        session.perform(new TravelAction("studio"));
        session.perform(new TravelAction("cucina"));
        assertEquals(2, received.size());
    }
}
