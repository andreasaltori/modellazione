# Il Caso di Villa Moretti – RPG investigativo

Progetto per l'esame di **Metodologie di Programmazione / Modellazione e Gestione della Conoscenza**
(UNICAM, AA 2025/26). Matricola: 122627. Package: `it.unicam.cs.mpgc.rpg122627`.

Gioco di ruolo investigativo in Java con interfaccia grafica Swing. Impersoni un investigatore:
esamini i luoghi, interroghi i sospettati, raccogli indizi con prove di abilità (d20 + abilità) e,
prima che il tempo finisca, accusi il colpevole presentando le prove. L'investigatore cresce di
livello e le partite si possono salvare e caricare.

## Requisiti

- JDK 21 o successivo (non servono altre installazioni: Gradle viene scaricato dal wrapper).

## Compilare ed eseguire

```bash
./gradlew build
./gradlew run
```

`./gradlew build` compila il progetto ed esegue i test automatici.
I salvataggi vengono scritti in `~/.rpg122627/saves/`.

## Documentazione

La documentazione completa (funzionalità, responsabilità delle classi, persistenza, estensioni e
dichiarazione dettagliata sull'uso dell'AI) è nella **Wiki** del repository.

## Dichiarazione sull'uso di strumenti di AI

Per la realizzazione di questo progetto è stato usato l'assistente AI **Claude (Anthropic)**. Claude ha
scritto il codice sorgente, i test automatici e la documentazione, seguendo le scelte indicate dallo
studente (genere investigativo, interfaccia Swing, persistenza in JSON senza librerie esterne) e i vincoli
della specifica d'esame. La dichiarazione dettagliata, con lo scopo di ogni uso, è nella pagina
*Dichiarazione uso AI* della Wiki.
