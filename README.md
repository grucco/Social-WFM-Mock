# Social WFM mock

# Prefazione
Questo documento vuole essere una rapida guida per lo sviluppatore che dovrà iniziare a lavorare sul Social WFM. Tutte le informazioni funzionali e i dettagli architetturali dei sistemi coinvolti sono ampiamente descritti nella documentazione di progetto (BBP, documenti di architettura, manuali utente, documentazione dei servizi, documenti di cutover ecc..)

# Rete e servizi
L'applicazione si interfaccia in Http con i seguenti sistemi:
* SAP Mobile Platform
* SAP JAM
* Web Services sviluppati in Python 

## SAP Mobile Platform
Middleware che espone i servizi OData on-premise per:
* Login (prima fase)
* Ordini di lavoro (home page)
* Servizi SAP HR - Employee Self Service 
  

**NOTA:** I servizi sopra indicati sono raggiungibili soltanto da rete APN TIM opportunamente configurata sui tablet di ACEA. Può essere utile per lo sviluppatore impostare un tablet in tethering e condividere la rete APN con il PC per poter testare i servizi dal proprio ambiente di sviluppo (es. client REST, test automatici di integrazione ecc.)

## SAP JAM
Servizi Cloud di SAP JAM per le seguenti funzionalità:
* Login (seconda fase)
* Notifiche Social
* Contatti
* Gruppi
* Bacheca personale
* Post social su impianti e oggetti tecnici di SAP PM

Tutti i servizi vengono aceduti in OAuth1.0. Nella fase di login relativa al social Network l'utente autorizza il Social WFM ad utilizzare il suo profilo JAM (OAuth).

**NOTA:** L'accesso a questi servizi è garantito da qualunque rete con accesso ad Internet pubblica. Non è necessario essere in rete APN

## Web Services sviluppati in Python
Evolutive successive hanno previsto l'integrazione dei servizi di Survey e Concorsi all'interno dell'applicazione. 

**NOTA:** L'accesso a questi servizi è garantito da qualunque rete con accesso ad Internet pubblica. Non è necessario essere in rete APN

# Struttura del codice
## Networking
La libreria utilizzata per il networking è Koush-Ion. Il package dove risiede tutta la logica di networking è **it.acea.android.socialwfm.http** e classi principali al suo interno sono
* **HttpClientRequest:** Contiene gli endpoint per i servizi di SAP JAM, ordini di lavoro, survey e concorsi; nonché la logica di strutturazione degli header e gestione dei cookies
* **EssClient:** Contiene gli endpoint di accesso ai servizi di SAP HR - Employee Self Service;nonché la logica di strutturazione degli header e gestione dei cookies per queste nuove chiamate

All'interno del package sono presenti tutte le classi di modello costruite a partire dalle response Http.
## Database
Il database utilizzato è Realm. E' stato utilizzato nella prima fase di progetto per il caching degli ordini di lavoro e delle notifiche dal social network. Nella seconda fase è stato poi abbandonato perchè ritenuto inutile ai fini della funzionalità.

## Background Service
L'applicazione utilizza un service di background `it.acea.android.socialwfm.service.UpdateSchedulerJob` per leggere in polling gli ordini di lavoro dalla mobile platform e le notifiche da SAP JAM. Se nuovi oggetti sono disponibili vengono scatenati degli eventi nell'applicazione che informano i componenti interessati. I componenti che reagisono a tali eventi sono quelli della home page: Mappa e badge notifiche
## Activity principali
Tutti i componenti di interfaccia grafica sono stati inseriti nel package **it.acea.android.socialwfm.app.ui**.
Le activities principali al suo interno sono:
*  **SplashActivity.java:** All'accesso dell'utente verifica nelle shared preferences che l'utente sia correttamente registrato sui sistemi (SMP registration code, token OAuth di SAP JAM)
*  **MainActivity.java:** Contiene la mappa con gli ordini di lavoro e gestisce la toolbar con notifiche, settings e ricerca
*  **AuthenticatedWFMUser:** Effettua il login sui sistemi on premise (mobile platform). Questo è il primo step di login
*  **OAuthActivity.java:** Permette all'utente di loggarsi su JAM ed eseguire l'OAuth. Questo è il secondo step di login
*  **SecondOAuthActivity.java:** Continua il processo iniziato da OAuthActivity
*  **SearchActivity.java:** Implementa la ricerca nel social network
*  **ConcorsiActivity.java:** Mostra una webview che permette all'utente di partecipare ad un concorso
*  **SurveyActivity.java:** Mostra una webview che permette all'utente di partecipare ad una survey


