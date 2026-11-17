# Dichiarazione sull'uso di strumenti di Intelligenza Artificiale

In conformità alle linee guida del corso, si dichiara che durante la realizzazione del progetto sono stati impiegati strumenti di Intelligenza Artificiale generativa (LLM) esclusivamente con **ruolo di supporto e affiancamento allo sviluppo**.

L'ideazione del dominio applicativo, l'analisi dei requisiti, la progettazione dell'architettura software, la suddivisione delle responsabilità tra i package e la validazione finale del codice sono state svolte e supervisionate interamente dall'autore del progetto. Nessun output generato automaticamente è stato integrato senza prima essere analizzato, compreso, adattato al contesto del progetto e verificato tramite compilazione ed esecuzione dei test.

## Ambiti e scopi di utilizzo

| Ambito | Scopo dell'utilizzo | Intervento dell'autore |
| :--- | :--- | :--- |
| **Progettazione e principi SOLID** | Brainstorming iniziale sulla separazione tra logica di dominio (`model`, `engine`), persistenza (`persistence`) e presentazione (`ui`), e verifica della coerenza rispetto ai principi SOLID. | Definizione delle interfacce, delle gerarchie di azioni e del flusso di gioco effettivo. |
| **Scrittura del codice e refactoring** | Supporto nella stesura di codice boilerplate, suggerimenti per migliorare la pulizia stilistica e refactoring di metodi complessi per mantenerli coesi. | Scrittura della logica centrale, adattamento al dominio investigativo e controllo puntuale su ogni classe. |
| **Build, dipendenze e debugging** | Diagnostica di errori di compilazione, configurazione degli script Gradle (`build.gradle.kts`) e risoluzione di problemi legati al classpath o ai permessi del wrapper. | Test diretto della build (`./gradlew build` e `./gradlew run`) e verifica della portabilità. |
| **Testing** | Suggerimento di casi limite (*edge case*) da coprire nelle suite di test JUnit per `model`, `engine` e `persistence`. | Implementazione, esecuzione e verifica delle asserzioni nei test unitari. |
| **Documentazione** | Revisione linguistica e strutturale dei testi per `README.md` e pagine della Wiki GitHub, oltre all'assistenza sulle procedure di pubblicazione su Git/GitHub. | Fornitura di tutti i contenuti tecnici e verifica della corrispondenza tra documentazione e codice reale. |
