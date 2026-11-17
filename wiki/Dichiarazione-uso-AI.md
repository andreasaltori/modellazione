# Dichiarazione dettagliata di uso di strumenti di AI

## Strumento usato

**Claude** (Anthropic), assistente AI conversazionale con accesso a un ambiente di sviluppo
(compilazione, esecuzione, test) usato in una sessione di lavoro con lo studente.

## Che cosa ha fatto l'AI e con quale scopo

| Attività | Scopo |
| --- | --- |
| Scrittura del codice sorgente Java (modello, motore, persistenza, interfaccia Swing) | Realizzare l'applicazione rispettando i vincoli della specifica |
| Scrittura del file del caso investigativo (trama, luoghi, indizi, sospettati) | Fornire un caso giocabile |
| Scrittura dei test automatici JUnit | Verificare le regole del gioco, la lettura/scrittura JSON e i salvataggi |
| Configurazione di Gradle e del wrapper | Rendere il progetto compilabile ed eseguibile con `./gradlew build` e `./gradlew run` |
| Scrittura di README e pagine della Wiki | Documentare funzionalità, responsabilità, persistenza ed estensioni |
| Verifica della conformità alla specifica | Controllare package, struttura del repository e documentazione richiesti |
| Prove di funzionamento | Compilazione, esecuzione dei test, avvio dell'interfaccia in un ambiente virtuale e simulazione di partite per tarare il tempo disponibile |

## Che cosa ha deciso lo studente

- Il tipo di gioco: un RPG investigativo.
- L'uso di Swing per l'interfaccia grafica.
- La persistenza in JSON senza librerie esterne.

## Verifiche e contributo personale dello studente

> **Da completare a cura dello studente**, descrivendo con precisione ciò che ha fatto
> personalmente (ad esempio: lettura e comprensione del codice, modifiche apportate, prove eseguite sul
> proprio computer, scelte di design discusse con l'AI). Questa sezione non è stata compilata dall'AI
> perché riguarda attività dello studente di cui l'AI non ha conoscenza.

## Limiti dichiarati delle verifiche eseguite dall'AI

- La compilazione, i test e il funzionamento dell'interfaccia sono stati provati in un ambiente Linux
  senza accesso alla rete esterna. Per lo stesso motivo il wrapper `./gradlew` non è stato eseguito per
  intero in quell'ambiente, e i test sono stati eseguiti con un sostituto minimale dell'API di JUnit
  invece che con JUnit stesso. La prova con `./gradlew build` e `./gradlew run` completa va fatta
  sul computer dello studente.
