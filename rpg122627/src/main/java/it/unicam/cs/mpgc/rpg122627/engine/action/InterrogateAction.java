package it.unicam.cs.mpgc.rpg122627.engine.action;

import it.unicam.cs.mpgc.rpg122627.engine.Action;
import it.unicam.cs.mpgc.rpg122627.engine.ActionResult;
import it.unicam.cs.mpgc.rpg122627.engine.GameSession;
import it.unicam.cs.mpgc.rpg122627.engine.SkillCheck;
import it.unicam.cs.mpgc.rpg122627.model.CaseFile;
import it.unicam.cs.mpgc.rpg122627.model.Clue;
import it.unicam.cs.mpgc.rpg122627.model.Person;
import it.unicam.cs.mpgc.rpg122627.model.Topic;

import java.util.Optional;

/**
 * Domanda a una persona presente nel luogo corrente. Costa 1 unità di tempo; se la prova fallisce
 * si può riprovare più tardi.
 */
public record InterrogateAction(String personId, String topicId) implements Action {

    private static final int TIME_COST = 1;
    private static final int EXPERIENCE = 2;

    @Override
    public String describe(CaseFile caseFile) {
        Person person = caseFile.people().get(personId);
        Topic topic = findTopic(person).orElseThrow();
        return "Chiedi a " + person.name() + ": " + topic.question() + " (" + topic.skill().label() + ")";
    }

    @Override
    public ActionResult execute(GameSession session) {
        Person person = session.caseFile().people().get(personId);
        Optional<Topic> found = person == null ? Optional.empty() : findTopic(person);
        boolean available = found.isPresent()
                && session.state().location().equals(person.locationId())
                && !session.state().isTopicResolved(topicId)
                && session.isUnlocked(found.get().requires());
        if (!available) {
            return ActionResult.failure("Non puoi fare questa domanda in questo momento.");
        }
        Topic topic = found.get();
        session.spendTime(TIME_COST);
        SkillCheck check = session.check(topic.skill(), topic.difficulty());
        if (!check.success()) {
            return ActionResult.failure(person.name() + " evita la domanda e non ottieni nulla. " + check.describe());
        }
        session.state().resolveTopic(topicId);
        ActionResult result = ActionResult.ok(person.name() + ": «" + topic.answer() + "» " + check.describe());
        if (topic.grantsClue() != null && !session.state().hasClue(topic.grantsClue())) {
            Clue clue = session.caseFile().clues().get(topic.grantsClue());
            session.state().addClue(clue.id());
            result = result.and("Nuovo indizio: " + clue.title() + ". " + clue.description());
        }
        return result.and(session.award(topic.skill(), EXPERIENCE));
    }

    private Optional<Topic> findTopic(Person person) {
        return person.topics().stream().filter(topic -> topic.id().equals(topicId)).findFirst();
    }
}
