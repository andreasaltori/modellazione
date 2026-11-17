# Responsabilità e classi

Il progetto è diviso in quattro strati. Le dipendenze vanno in una sola direzione: l'interfaccia dipende
dal motore, il motore dal modello; il modello non dipende da nessuno. La persistenza usa motore e modello.

```
ui  ──►  engine  ──►  model
 └──────► persistence ──►┘
```

Tutte le classi sono nel package `it.unicam.cs.mpgc.rpg122627` e nei suoi sotto-package.

## Principi di progetto

- **Responsabilità singola**: ogni classe ha un solo motivo per cambiare (es. `Json` sa solo leggere e scrivere JSON; `CaseFileParser` conosce solo il formato dei file dei casi; `SaveData` solo il formato del salvataggio).
- **Aperto/chiuso**: nuove azioni di gioco si aggiungono con `Action` + `ActionProvider`, senza modificare `GameSession` né l'interfaccia.
- **Inversione delle dipendenze**: il motore dipende da astrazioni (`Dice`, `Action`, `GameListener`); l'interfaccia dipende da `CaseRepository` e `SaveRepository`, non dalle implementazioni.
- **Segregazione delle interfacce**: interfacce piccole, con un solo metodo ciascuna (`Dice`, `GameListener`, `ActionProvider`).
- **Immutabilità dei dati statici**: modello del caso fatto di `record` immutabili; solo `GameState` e `Investigator` cambiano durante la partita.

## Package `model` – i dati del caso e del personaggio

| Classe | Responsabilità |
| --- | --- |
| `Skill` (enum) | Le tre abilità e il loro nome da mostrare. |
| `Investigator` | Nome, livello, esperienza e abilità del giocatore; contiene la regola di avanzamento di livello. |
| `Clue` (record) | Un indizio: titolo, descrizione, dove si trova, abilità e difficoltà per trovarlo, eventuale prerequisito. |
| `Topic` (record) | Un argomento di conversazione: domanda, risposta, prova richiesta, indizio concesso, prerequisito. |
| `Person` (record) | Un personaggio non giocante con i suoi argomenti. |
| `Location` (record) | Un luogo con le sue uscite. |
| `CaseFile` (record) | Il caso completo. Il costruttore ne garantisce la coerenza: riferimenti esistenti, luoghi raggiungibili, indizi ottenibili, soluzione dimostrabile. |

## Package `engine` – le regole

| Classe / interfaccia | Responsabilità |
| --- | --- |
| `GameSession` | Facciata del motore. Unisce caso, stato, dadi; elenca le azioni disponibili, le esegue, applica la scadenza del tempo, notifica gli osservatori. |
| `GameState` | Lo stato variabile (luogo, tempo, indizi, domande risolte, stato). È tutto ciò che si salva. |
| `GameStatus` (enum) | `IN_PROGRESS`, `WON`, `LOST`. |
| `Action` (interfaccia) | Un'azione del giocatore (pattern Command): descrizione ed esecuzione. |
| `ActionProvider` (interfaccia) | Produce le azioni di un tipo disponibili ora. |
| `ActionResult` (record) | Esito di un'azione: riuscita e testo da mostrare. |
| `GameListener` (interfaccia) | Osservatore degli esiti (pattern Observer): ogni interfaccia utente se ne registra uno. |
| `Dice` (interfaccia) / `RandomDice` | Sorgente di casualità; l'astrazione permette dadi truccati nei test. |
| `SkillCheck` (record) | Una prova d20 + abilità contro difficoltà. |

### `engine.action` – le azioni

| Classe | Responsabilità |
| --- | --- |
| `TravelAction` | Spostamento in un luogo adiacente. |
| `ExamineAction` | Ricerca di un indizio nel luogo corrente. |
| `InterrogateAction` | Domanda a una persona presente. |
| `AccuseAction` | Accusa finale con i tre esiti possibili. |
| `DefaultActionProviders` | I provider delle azioni standard (vai, esamina, chiedi). |

## Package `persistence` – dati e salvataggi

| Classe / interfaccia | Responsabilità |
| --- | --- |
| `Json` | Lettura e scrittura JSON con il solo JDK, più accessori tipizzati. |
| `CaseRepository` (interfaccia) | Sorgente dei casi. |
| `ClasspathCaseRepository` | Legge i casi dalle risorse del programma. |
| `CaseFileParser` | Trasforma il JSON di un caso in un `CaseFile`. |
| `SaveRepository` (interfaccia) | Archivio dei salvataggi, a slot. |
| `FileSaveRepository` | Salva ogni slot come file JSON, con scrittura sicura. |
| `SaveData` (record) | Fotografia serializzabile della partita; converte da/verso `GameState`. |

## Package `ui` e classe `Main`

| Classe | Responsabilità |
| --- | --- |
| `MainWindow` | Finestra Swing: mostra stato, diario, taccuino e un pulsante per ogni azione disponibile. Non contiene regole di gioco. |
| `AccuseDialog` | Finestra per scegliere il sospettato e le prove. |
| `Main` | Collega le implementazioni concrete e avvia la finestra. |

## Test

Nella cartella `src/test` (JUnit 5): prove di abilità, avanzamento di livello, flusso completo del
gioco (vittoria, sconfitta, sblocco di domande, scadenza del tempo, accuse), lettura e scrittura JSON,
validazione dei casi e salvataggio/caricamento.
