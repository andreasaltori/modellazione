package it.unicam.cs.mpgc.rpg122627.engine;

import java.util.List;

/**
 * Produce le azioni di un certo tipo disponibili nella situazione corrente. Una nuova funzionalità
 * (ad esempio "analizzare un indizio in laboratorio") si aggiunge scrivendo una {@link Action} e un
 * provider, senza modificare {@link GameSession} né le interfacce utente (principio aperto/chiuso).
 */
@FunctionalInterface
public interface ActionProvider {

    List<Action> actionsFor(GameSession session);
}
