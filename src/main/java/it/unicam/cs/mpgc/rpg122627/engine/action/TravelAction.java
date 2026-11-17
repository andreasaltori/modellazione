package it.unicam.cs.mpgc.rpg122627.engine.action;

import it.unicam.cs.mpgc.rpg122627.engine.Action;
import it.unicam.cs.mpgc.rpg122627.engine.ActionResult;
import it.unicam.cs.mpgc.rpg122627.engine.GameSession;
import it.unicam.cs.mpgc.rpg122627.model.CaseFile;
import it.unicam.cs.mpgc.rpg122627.model.Location;

/** Spostamento verso un luogo adiacente. Costa 1 unità di tempo. */
public record TravelAction(String destinationId) implements Action {

    private static final int TIME_COST = 1;

    @Override
    public String describe(CaseFile caseFile) {
        return "Vai: " + caseFile.locations().get(destinationId).name();
    }

    @Override
    public ActionResult execute(GameSession session) {
        if (!session.currentLocation().exits().contains(destinationId)) {
            return ActionResult.failure("Da qui non puoi raggiungere quel luogo.");
        }
        session.spendTime(TIME_COST);
        session.state().moveTo(destinationId);
        Location destination = session.currentLocation();
        return ActionResult.ok("Ti sposti: " + destination.name() + ". " + destination.description());
    }
}
