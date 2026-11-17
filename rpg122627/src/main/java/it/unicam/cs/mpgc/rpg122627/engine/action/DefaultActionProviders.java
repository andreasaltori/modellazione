package it.unicam.cs.mpgc.rpg122627.engine.action;

import it.unicam.cs.mpgc.rpg122627.engine.Action;
import it.unicam.cs.mpgc.rpg122627.engine.ActionProvider;
import it.unicam.cs.mpgc.rpg122627.engine.GameSession;
import it.unicam.cs.mpgc.rpg122627.model.Clue;
import it.unicam.cs.mpgc.rpg122627.model.Location;
import it.unicam.cs.mpgc.rpg122627.model.Person;
import it.unicam.cs.mpgc.rpg122627.model.Topic;

import java.util.ArrayList;
import java.util.List;

/** I provider delle azioni standard del gioco: spostarsi, esaminare, interrogare. */
public final class DefaultActionProviders {

    private DefaultActionProviders() {
    }

    /** I provider standard, nell'ordine in cui le azioni vengono proposte al giocatore. */
    public static List<ActionProvider> standard() {
        return List.of(DefaultActionProviders::travel, DefaultActionProviders::examine,
                DefaultActionProviders::interrogate);
    }

    private static List<Action> travel(GameSession session) {
        List<Action> actions = new ArrayList<>();
        for (String exit : session.currentLocation().exits()) {
            actions.add(new TravelAction(exit));
        }
        return actions;
    }

    private static List<Action> examine(GameSession session) {
        List<Action> actions = new ArrayList<>();
        Location here = session.currentLocation();
        for (Clue clue : session.caseFile().clues().values()) {
            if (here.id().equals(clue.locationId()) && !session.state().hasClue(clue.id())
                    && session.isUnlocked(clue.requires())) {
                actions.add(new ExamineAction(clue.id()));
            }
        }
        return actions;
    }

    private static List<Action> interrogate(GameSession session) {
        List<Action> actions = new ArrayList<>();
        Location here = session.currentLocation();
        for (Person person : session.caseFile().people().values()) {
            if (!here.id().equals(person.locationId())) {
                continue;
            }
            for (Topic topic : person.topics()) {
                if (!session.state().isTopicResolved(topic.id()) && session.isUnlocked(topic.requires())) {
                    actions.add(new InterrogateAction(person.id(), topic.id()));
                }
            }
        }
        return actions;
    }
}
