package it.unicam.cs.mpgc.rpg122627.engine;

import it.unicam.cs.mpgc.rpg122627.model.CaseFile;

/**
 * Un'azione che il giocatore può compiere (pattern Command). Per aggiungere una nuova azione
 * basta implementare questa interfaccia e proporla da {@link GameSession#availableActions()}:
 * l'interfaccia grafica non va modificata.
 */
public interface Action {

    /** Testo con cui l'azione viene proposta al giocatore. */
    String describe(CaseFile caseFile);

    /** Esegue l'azione modificando la sessione. Richiamata solo da {@link GameSession#perform(Action)}. */
    ActionResult execute(GameSession session);
}
