# Funzionalità

## Il gioco

Il giocatore crea un investigatore (nome e una specializzazione) e indaga su un caso. Non ci sono
combattimenti: l'azione sta nelle prove di abilità, nella scelta di dove spendere il tempo e nel
collegare gli indizi.

### Personaggio

- Tre abilità: **Percezione**, **Logica**, **Empatia**. Valore iniziale 2; la specializzazione scelta
  parte con +2.
- Ogni successo dà punti esperienza; ogni 5×livello PE si sale di livello e l'abilità usata aumenta di 1.

### Azioni

Tutte le azioni, tranne l'accusa, costano 1 unità di tempo (anche se la prova fallisce: si può riprovare).

| Azione | Cosa fa |
| --- | --- |
| Vai | Ci si sposta in un luogo adiacente. |
| Esamina | Cerca un indizio nel luogo corrente: prova di abilità contro una difficoltà. |
| Chiedi | Pone una domanda a una persona presente: prova di abilità. Alcune domande si sbloccano solo dopo aver trovato un certo indizio. |
| Accusa | Sceglie un sospettato e gli indizi a sostegno (vedi sotto). |

### Prove di abilità

Si tira un d20, si somma il valore dell'abilità e si confronta con la difficoltà: il risultato
maggiore o uguale è un successo. L'esito è mostrato nel diario, ad esempio `[d20: 12 + 3 = 15 contro 13]`.

### Accusa e fine partita

- **Sospettato sbagliato**: la partita è persa e viene rivelata la soluzione.
- **Sospettato giusto con almeno 3 indizi decisivi tra quelli presentati**: il caso è risolto.
- **Sospettato giusto con prove insufficienti**: l'accusa cade e si perdono 3 unità di tempo.
- **Tempo esaurito**: la partita è persa.

Nel caso incluso ci sono anche indizi che portano fuori strada (la nipote, il socio in affari).

### Interfaccia

Finestra Swing con barra di stato (luogo, tempo, livello, abilità), un **Diario** con la narrazione, il
**Taccuino degli indizi** e un pannello di pulsanti che mostra solo le azioni oggi possibili.

### Salvataggio

Dal menu *Partita* si può iniziare una nuova partita, salvare e caricare. Il salvataggio contiene stato
dell'investigatore, luogo, tempo rimasto, indizi trovati e domande già risolte.
