package it.unicam.cs.mpgc.rpg122627.engine;

/**
 * Esito di un'azione: se è riuscita e il testo narrativo da mostrare.
 */
public record ActionResult(boolean success, String message) {

    public static ActionResult ok(String message) {
        return new ActionResult(true, message);
    }

    public static ActionResult failure(String message) {
        return new ActionResult(false, message);
    }

    /** Restituisce un nuovo esito con una riga di testo in più (ignorata se vuota). */
    public ActionResult and(String extraLine) {
        if (extraLine == null || extraLine.isBlank()) {
            return this;
        }
        return new ActionResult(success, message + "\n" + extraLine);
    }
}
