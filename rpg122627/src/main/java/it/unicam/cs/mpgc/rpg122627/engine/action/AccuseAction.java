package it.unicam.cs.mpgc.rpg122627.engine.action;

import it.unicam.cs.mpgc.rpg122627.engine.Action;
import it.unicam.cs.mpgc.rpg122627.engine.ActionResult;
import it.unicam.cs.mpgc.rpg122627.engine.GameSession;
import it.unicam.cs.mpgc.rpg122627.engine.GameStatus;
import it.unicam.cs.mpgc.rpg122627.model.CaseFile;
import it.unicam.cs.mpgc.rpg122627.model.Person;

import java.util.Set;

/**
 * Accusa formale di un sospettato, sostenuta dagli indizi scelti dal giocatore.
 * <ul>
 *   <li>sospettato sbagliato: la partita è persa;</li>
 *   <li>sospettato giusto con prove sufficienti: la partita è vinta;</li>
 *   <li>sospettato giusto ma prove insufficienti: l'accusa cade e si perde tempo.</li>
 * </ul>
 */
public record AccuseAction(String suspectId, Set<String> evidence) implements Action {

    private static final int INSUFFICIENT_EVIDENCE_PENALTY = 3;

    public AccuseAction {
        evidence = Set.copyOf(evidence);
    }

    @Override
    public String describe(CaseFile caseFile) {
        return "Accusa " + caseFile.people().get(suspectId).name();
    }

    @Override
    public ActionResult execute(GameSession session) {
        CaseFile caseFile = session.caseFile();
        Person suspect = caseFile.people().get(suspectId);
        if (suspect == null || !session.state().foundClues().containsAll(evidence)) {
            return ActionResult.failure("Accusa non valida: sospettato o prove sconosciuti.");
        }
        if (!suspectId.equals(caseFile.culpritId())) {
            session.state().setStatus(GameStatus.LOST);
            return ActionResult.failure("Accusi " + suspect.name() + ", ma è innocente. Il vero colpevole sfugge alla giustizia.\n"
                    + caseFile.solution());
        }
        long decisive = evidence.stream().filter(caseFile.keyClues()::contains).count();
        if (decisive < caseFile.requiredEvidence()) {
            session.spendTime(INSUFFICIENT_EVIDENCE_PENALTY);
            return ActionResult.failure("Hai il sospettato giusto, ma le prove non reggono in tribunale: servono almeno "
                    + caseFile.requiredEvidence() + " indizi decisivi, ne hai presentati " + decisive
                    + ". Torna a cercare altre prove.");
        }
        session.state().setStatus(GameStatus.WON);
        return ActionResult.ok("Caso risolto! " + suspect.name() + " viene arrestato.\n" + caseFile.solution());
    }
}
