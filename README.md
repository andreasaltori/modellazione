# Investigatore – RPG Investigativo (`rpg122627`)

Applicativo Java sviluppato per l'esame di **Metodologie di Programmazione / Modellazione e Gestione della Conoscenza (AA 2025/26)**.

Il progetto implementa un Gioco di Ruolo investigativo in cui il giocatore veste i panni di un investigatore impegnato a esplorare ambientazioni, esaminare indizi, interrogare sospettati tramite prove di abilità (*Skill Check*), gestire il tempo a disposizione e formulare l'accusa finale (caso *Villa Moretti*). L'architettura separa nettamente modello di dominio (`model`), motore di gioco (`engine`), persistenza su file JSON (`persistence`) e interfaccia grafica Swing (`ui`), garantendo estendibilità e facilità di porting su altri dispositivi.

## Requisiti ed Esecuzione

* **Java Development Kit (JDK):** versione 17 o superiore (`options.release = 17`)
* **Build tool:** Gradle (incluso tramite Gradle Wrapper, non è richiesta alcuna installazione globale)

Clonare il repository, spostarsi nella directory principale ed eseguire i seguenti comandi:

**Linux / macOS:**
```bash
./gradlew build
./gradlew run
```

**Windows (Prompt dei comandi / PowerShell):**
```bat
gradlew.bat build
gradlew.bat run
```

## Documentazione (Wiki)

La documentazione completa del progetto — comprensiva di descrizione delle funzionalità, responsabilità individuate, dettaglio delle classi e interfacce sviluppate, organizzazione dei dati e persistenza, meccanismi di estendibilità e dichiarazione dettagliata sull'uso dell'IA — è disponibile nella **[Wiki di questo repository](../../wiki)**.

## Dichiarazione sull'uso di strumenti di Intelligenza Artificiale

Nello sviluppo di questo progetto sono stati utilizzati strumenti di Intelligenza Artificiale generativa come supporto alla produttività, in particolare per il confronto sulle scelte architetturali, la scrittura di porzioni di codice ripetitivo, la risoluzione di errori di compilazione/configurazione Gradle e la stesura dei test unitari. Ogni suggerimento è stato revisionato, adattato e integrato manualmente; la progettazione complessiva, la logica applicativa e la verifica del rispetto dei requisiti restano interamente a cura dell'autore.
