package it.unicam.cs.mpgc.rpg122627.engine;

import it.unicam.cs.mpgc.rpg122627.engine.action.DefaultActionProviders;
import it.unicam.cs.mpgc.rpg122627.model.CaseFile;
import it.unicam.cs.mpgc.rpg122627.model.Clue;
import it.unicam.cs.mpgc.rpg122627.model.Investigator;
import it.unicam.cs.mpgc.rpg122627.model.Location;
import it.unicam.cs.mpgc.rpg122627.model.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Facciata del motore di gioco: unisce il caso (dati statici), lo stato (dati variabili) e i dadi.
 * Le interfacce utente parlano solo con questa classe: chiedono le azioni disponibili e le eseguono.
 */
public final class GameSession {

    private static final String TIMEOUT_MESSAGE =
            "Il tempo a disposizione è finito: il colpevole resta impunito. Il caso viene archiviato.";

    private final CaseFile caseFile;
    private final GameState state;
    private final Dice dice;
    private final List<ActionProvider> providers;
    private final List<GameListener> listeners = new ArrayList<>();

    private GameSession(CaseFile caseFile, GameState state, Dice dice, List<ActionProvider> providers) {
        this.caseFile = Objects.requireNonNull(caseFile, "caseFile");
        this.state = Objects.requireNonNull(state, "state");
        this.dice = Objects.requireNonNull(dice, "dice");
        this.providers = List.copyOf(providers);
    }

    /** Inizia una nuova partita sul caso indicato, con le azioni standard. */
    public static GameSession newGame(CaseFile caseFile, Investigator investigator, Dice dice) {
        return newGame(caseFile, investigator, dice, DefaultActionProviders.standard());
    }

    /** Inizia una nuova partita con un insieme personalizzato di provider di azioni. */
    public static GameSession newGame(CaseFile caseFile, Investigator investigator, Dice dice,
                                      List<ActionProvider> providers) {
        GameState state = new GameState(investigator, caseFile.startLocation(), caseFile.timeLimit(),
                List.of(), List.of(), GameStatus.IN_PROGRESS);
        return new GameSession(caseFile, state, dice, providers);
    }

    /** Riprende una partita salvata, verificando che lo stato sia coerente col caso. */
    public static GameSession restore(CaseFile caseFile, GameState state, Dice dice) {
        if (!caseFile.locations().containsKey(state.location())) {
            throw new IllegalArgumentException("Il salvataggio fa riferimento a un luogo inesistente");
        }
        if (!caseFile.clues().keySet().containsAll(state.foundClues())) {
            throw new IllegalArgumentException("Il salvataggio contiene indizi sconosciuti");
        }
        return new GameSession(caseFile, state, dice, DefaultActionProviders.standard());
    }

    public void addListener(GameListener listener) {
        listeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public CaseFile caseFile() {
        return caseFile;
    }

    public GameState state() {
        return state;
    }

    public Location currentLocation() {
        return caseFile.locations().get(state.location());
    }

    public boolean isOver() {
        return state.status() != GameStatus.IN_PROGRESS;
    }

    /** Indizi trovati finora, nell'ordine di scoperta. */
    public List<Clue> foundClues() {
        return state.foundClues().stream().map(id -> caseFile.clues().get(id)).toList();
    }

    /**
     * Tutte le azioni oggi disponibili, raccolte dai provider (esclusa l'accusa, che richiede
     * parametri scelti dal giocatore).
     */
    public List<Action> availableActions() {
        if (isOver()) {
            return List.of();
        }
        List<Action> actions = new ArrayList<>();
        for (ActionProvider provider : providers) {
            actions.addAll(provider.actionsFor(this));
        }
        return actions;
    }

    /**
     * Esegue un'azione, applica la scadenza del tempo e notifica gli osservatori.
     */
    public ActionResult perform(Action action) {
        Objects.requireNonNull(action, "action");
        if (isOver()) {
            return ActionResult.failure("La partita è già conclusa.");
        }
        ActionResult result = action.execute(this);
        if (state.status() == GameStatus.IN_PROGRESS && state.timeLeft() <= 0) {
            state.setStatus(GameStatus.LOST);
            result = result.and(TIMEOUT_MESSAGE);
        }
        ActionResult finalResult = result;
        listeners.forEach(listener -> listener.onActionPerformed(action, finalResult));
        return finalResult;
    }

    // ---- servizi usati dalle azioni -------------------------------------------------------

    /** Prova di abilità dell'investigatore contro una difficoltà. */
    public SkillCheck check(Skill skill, int difficulty) {
        return SkillCheck.attempt(dice, state.investigator().skill(skill), difficulty);
    }

    public void spendTime(int amount) {
        state.spendTime(amount);
    }

    /** Assegna esperienza; restituisce un messaggio se si sale di livello, altrimenti stringa vuota. */
    public String award(Skill skill, int experience) {
        Investigator investigator = state.investigator();
        if (investigator.gainExperience(skill, experience)) {
            return "Sali al livello " + investigator.level() + ": " + skill.label()
                    + " sale a " + investigator.skill(skill) + ".";
        }
        return "";
    }

    /** Vero se la condizione di sblocco è vuota oppure l'indizio richiesto è già noto. */
    public boolean isUnlocked(String requiredClueId) {
        return requiredClueId == null || state.hasClue(requiredClueId);
    }
}
