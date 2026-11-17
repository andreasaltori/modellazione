# Estendere il progetto

Il progetto è pensato per crescere senza riscrivere le parti esistenti.

## Nuovi casi

Basta aggiungere `src/main/resources/cases/<id>.json` nel formato descritto in
[Dati e persistenza](Dati-e-persistenza) e inserire l'identificatore in `cases/index.json`. Il
costruttore di `CaseFile` controlla subito la coerenza del nuovo caso. Non serve modificare il codice.

## Nuove azioni (ad esempio "analizza un indizio in laboratorio")

1. Scrivere una classe che implementa `Action` (descrizione ed esecuzione).
2. Scrivere un `ActionProvider` che decide quando l'azione è disponibile.
3. Passare il provider a `GameSession.newGame(caso, investigatore, dadi, providers)`.

`GameSession` e l'interfaccia grafica non cambiano: l'interfaccia mostra tutte le azioni restituite
dalla sessione. Un test (`customActionProviderExtendsTheGameWithoutChangingTheEngine`) dimostra il meccanismo.

## Nuove interfacce (mobile, web, riga di comando)

Il motore non dipende da Swing. Un nuovo front-end usa solo `GameSession`: chiede `availableActions()`,
esegue `perform(action)`, legge `state()` e si registra con `addListener(...)` per ricevere gli esiti.

## Nuove modalità di persistenza (database, cloud)

Si implementa `SaveRepository` (e, se i casi arrivano da altrove, `CaseRepository`) e la si passa a
`MainWindow` in `Main`. Il formato di scambio resta `SaveData`.

## Nuove regole di gioco

- Nuove abilità: aggiungere un valore a `Skill` (poi usarlo nei file dei casi).
- Nuove regole per le prove: si modifica solo `SkillCheck`.
- Dadi diversi o deterministici: nuova implementazione di `Dice`.
