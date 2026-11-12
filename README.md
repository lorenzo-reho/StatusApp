# StatusApp

Applicazione mobile Android per i tecnici sul campo che consente di rilevare dispositivi Bluetooth Low Energy nelle vicinanze, monitorarne lo stato in tempo reale (batteria, firmware, errori) e registrare note di intervento su cloud.

## Funzionalità Principali
* **Autenticazione Tecnico:** Accesso tramite email e password.
* **Scansione e Connessione BLE:** Rilevazione dei dispositivi nelle vicinanze (BLE simulato nel codice) e connessione al singolo dispositivo.
* **Monitoraggio in Tempo Reale:** Monitoraggio livello batteria, versione firmware ed eventuali codici di errore.
* **Gestione Interventi su Cloud:** Salvataggio degli interventi su database cloud (Firebase Firestore) con istantanea dello stato del dispositivo.
* **Storico e Dettagli:** Visualizzazione degli interventi passati e dei relativi dettagli.

## Prerequisiti

* **Android Studio**
* **Java Development Kit** 11
* **Android SDK**:
  * **Compile SDK:** API 37
  * **Target SDK:** API 36
  * **Minimum SDK:** API 24

