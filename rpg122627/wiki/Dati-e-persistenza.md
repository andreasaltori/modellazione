# Dati e persistenza

## Organizzazione dei dati

I dati si dividono in due gruppi, che si trovano in posti diversi:

| Gruppo | Contenuto | Dove sta | Cambia durante la partita? |
| --- | --- | --- | --- |
| **Caso** (`CaseFile`) | Luoghi, persone, domande, indizi, soluzione, tempo disponibile | File JSON nelle risorse (`src/main/resources/cases/`) | No |
| **Stato** (`GameState`) | Investigatore, luogo, tempo rimasto, indizi trovati, domande risolte, stato | In memoria; salvato in `~/.rpg122627/saves/` | Sì |

Il salvataggio contiene solo lo stato e l'identificatore del caso: non copia i dati statici, quindi
resta piccolo e un caso corretto o ampliato continua a funzionare con i vecchi salvataggi
(purché gli identificatori esistano ancora; altrimenti il caricamento segnala l'errore).

## Formato dei casi

`cases/index.json` elenca gli identificatori; `cases/<id>.json` descrive un caso:

```
{ "id", "title", "intro", "timeLimit", "startLocation", "culprit", "requiredEvidence",
  "keyClues": [...], "solution",
  "locations": [ { "id", "name", "description", "exits": [...] } ],
  "clues":     [ { "id", "title", "description", "hint", "location", "skill", "difficulty", "requires" } ],
  "people":    [ { "id", "name", "description", "location",
                   "topics": [ { "id", "question", "answer", "skill", "difficulty", "grants", "requires" } ] } ] }
```

Un indizio con `location` si trova esaminando quel luogo; un indizio senza `location` si ottiene solo
tramite il campo `grants` di una domanda. `requires` sblocca una domanda o una ricerca solo dopo aver
trovato un altro indizio.

Il costruttore di `CaseFile` rifiuta i casi incoerenti: riferimenti a elementi inesistenti, luoghi non
raggiungibili, indizi non ottenibili, numero di prove richieste impossibile.

## Formato del salvataggio

`~/.rpg122627/saves/partita.json`:

```
{ "caseId", "playerName", "level", "experience",
  "skills": { "PERCEPTION", "LOGIC", "EMPATHY" },
  "location", "timeLeft", "clues": [...], "topics": [...], "status" }
```

## Come è garantita la persistenza

- **Nessuna libreria esterna**: la classe `Json` legge e scrive il formato con il solo JDK.
- **Scrittura sicura**: `FileSaveRepository` scrive prima in un file temporaneo e poi lo sposta sopra il
  salvataggio, così un errore a metà non lo danneggia.
- **Validazione in lettura**: un salvataggio danneggiato o incoerente con il caso produce un messaggio
  d'errore nella finestra, senza interrompere il programma.
- **Nomi di slot controllati**: solo lettere, numeri, `-` e `_`, per impedire percorsi fuori dalla cartella.
- **Astrazione**: il resto del programma conosce solo le interfacce `SaveRepository` e `CaseRepository`.
