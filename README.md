# webFr — exploration du web français en Java

Un prototype de collecte de liens web, conçu comme une première étape vers la cartographie du web français.

**Java · HTTP · Multithreading · Collections · Export de données**

## Objectif

Explorer des pages à partir d’une URL de départ, extraire des liens vers des domaines en `.fr` et conserver les relations découvertes pour préparer leur analyse. Le dépôt contient la partie exploration et collecte ; aucune interface de visualisation de graphe n’est présente.

## Fonctionnement du prototype

1. `Controleur` initialise une file d’URL et deux groupes de threads.
2. `Crawler` récupère le contenu des pages via HTTP et recherche des liens avec une expression régulière.
3. `TriUrl` traite les liens collectés et écrit des fichiers de sortie au format JSON construit manuellement.

L’URL initiale est définie dans le code. Un exemple de sortie est conservé dans `mapFr/mapFr/urlsVisitees0.json`.

## Organisation

| Chemin | Contenu |
| --- | --- |
| `mapFr/mapFr/src/Controleur.java` | Point d’entrée et orchestration |
| `mapFr/mapFr/src/Crawler.java` | Récupération HTTP et extraction de liens |
| `mapFr/mapFr/src/TriUrl.java` | Traitement des résultats et écriture des fichiers |
| `mapFr/Test/` | Expérimentations séparées sur les threads et le partage de ressources |

## Compilation

Depuis la racine du dépôt :

```sh
javac -encoding UTF-8 -d out mapFr/mapFr/src/Controleur.java mapFr/mapFr/src/Crawler.java mapFr/mapFr/src/TriUrl.java
```

Compilation vérifiée avec **JDK 17**, avec des avertissements de typage dans `Crawler`. Aucune bibliothèque externe n’est nécessaire pour ces trois classes.

## État actuel et exécution

Le prototype nécessite encore un travail sur la coordination des threads et la condition d’arrêt. Le code attend notamment la terminaison des exécuteurs avant de demander leur arrêt, et le traitement des résultats comporte une boucle permanente. Il peut donc continuer indéfiniment et consommer des ressources. L’exécution réseau n’a pas été testée dans cette passe ; aucune commande de collecte n’est proposée comme démonstration validée.

L’extraction par expression régulière et la construction manuelle du JSON sont également des limites du prototype. Ce dépôt montre une exploration des requêtes HTTP, des collections et de la concurrence, plutôt qu’un outil de crawling finalisé.

## Consignes du projet

Aucun énoncé ni cahier des charges n’est fourni dans le dépôt. Le README interne de `mapFr/mapFr/` est un fichier générique de démarrage Java pour VS Code, pas une consigne pédagogique.
