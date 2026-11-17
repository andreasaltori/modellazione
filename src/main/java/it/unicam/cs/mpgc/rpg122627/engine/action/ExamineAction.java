package it.unicam.cs.mpgc.rpg122627.engine.action;

import it.unicam.cs.mpgc.rpg122627.engine.Action;
import it.unicam.cs.mpgc.rpg122627.engine.ActionResult;
import it.unicam.cs.mpgc.rpg122627.engine.GameSession;
import it.unicam.cs.mpgc.rpg122627.engine.SkillCheck;
import it.unicam.cs.mpgc.rpg122627.model.CaseFile;
import it.unicam.cs.mpgc.rpg122627.model.Clue;

/**
 * Esame di un dettaglio del luogo corrente alla ricerca di un indizio. Costa 1 unità di tempo,
 * anche se la prova fallisce (si può riprovare).
 */
public record ExamineAction(String clueId) implements Action {

    private static final int TIME_COST = 1;
    private static final int EXPERIENCE = 3;

    @Override
    public String describe(CaseFile caseFile) {
        Clue clue = caseFile.clues().get(clueId);
        return "Esamina: " + clue.hint() + " (" + clue.skill().label() + ")";
    }

    @Override
    public ActionResult execute(GameSession session) {
        Clue clue = session.caseFile().clues().get(clueId);
        boolean available = clue != null
                && session.state().location().equals(clue.locationId())
                && !session.state().hasClue(clueId)
                && session.isUnlocked(clue.requires());
        if (!available) {
            return ActionResult.failure("Qui non c'è nulla del genere da esaminare.");
        }
        session.spendTime(TIME_COST);
        SkillCheck check = session.check(clue.skill(), clue.difficulty());
        if (!check.success()) {
            return ActionResult.failure("Osservi con attenzione, ma non ricavi nulla di utile. " + check.describe());
        }
        session.state().addClue(clueId);
        return ActionResult.ok("Nuovo indizio: " + clue.title() + ". " + clue.description() + " " + check.describe())
                .and(session.award(clue.skill(), EXPERIENCE));
    }
}
