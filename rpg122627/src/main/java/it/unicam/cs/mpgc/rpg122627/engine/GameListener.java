package it.unicam.cs.mpgc.rpg122627.engine;

/**
 * Osservatore degli eventi di gioco (pattern Observer). Ogni front-end (desktop, mobile, web)
 * può registrarne uno su una {@link GameSession} per reagire alle azioni.
 */
@FunctionalInterface
public interface GameListener {

    void onActionPerformed(Action action, ActionResult result);
}
