Consauto
========

Le but de cette aplication android est de pouvoir enregistrer et lister les dépenses liées à la voiture.
A l'heure actuelle, seules les dépenses de carburants sont gérées, mais à terme, on pourra enregistrer tout type de dépense (entretiens, assurance, ...)
Cela permettra de connaître le prix réel d'un kilomètre (tout frais compris)

Extrait du Manifest
-------------------

### Prérequis : 
> android:minSdkVersion="7"
> android:targetSdkVersion="16"

### Version :
> android:versionCode="1"
> android:versionName="1.0"
> ```php private static final int DATABASE_VERSION = 1; ```

### Autorisations : 
> aucune

Fonctionnalités
---------------

![Accueil `MainActivity`](screenshots/20131003_accueil.png)
![Saisir un plein `FaireLePleinActivity`](screenshots/20130928_ajouter.png)
![Liste des pleins enregistrés `ListeActivity`](screenshots/20131001_lister.png)
![Modifier un plein `FaireLePleinActivity`](screenshots/20130928_modifier.png)
![Graphiqe `GraphActivity`](screenshots/20131003_graphiques.png)