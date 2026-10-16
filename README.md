# webFr — exploration du web français en Java

Un prototype de collecte de liens web, conçu comme une première étape vers la cartographie du web français.

**Java · HTTP · Multithreading · Collections · Export de données**

**Scénario étudié : « Visualiser le Web FR »** · [Lire le rapport de conception](RAPPORT.md)

## Objectif

Explorer des pages à partir d’une URL de départ, extraire des liens vers des domaines en `.fr` et conserver les relations découvertes pour préparer leur analyse. Le dépôt contient la partie exploration et collecte ; aucune interface de visualisation de graphe n’est présente.

Le rapport initial envisageait une carte des relations et un classement des sites, avec un départ à partir de 100 sites populaires en France. Le prototype public démarre sur une seule URL définie dans le code ; la sélection des 100 sites et le classement ne sont pas implémentés. Le filtre `.fr` est un périmètre technique, pas une représentation exhaustive du web français.

## Du rapport au prototype

| Sujet | Conception étudiée | Présent dans le dépôt |
| --- | --- | --- |
| Collecte | Parcours de pages et prévention des cycles | Requêtes HTTP et extraction par expression régulière |
| Stockage | Base relationnelle pour les pages et leurs liens | Collections en mémoire et fichiers de sortie |
| Parallélisation | Plusieurs travailleurs, puis plusieurs machines | Deux groupes de threads dans un processus Java |
| Restitution | Carte et classement des sites | Aucune visualisation ni classement |
| Évaluation | Débit, mémoire et coût des traitements | Pas de benchmark fourni |

Le [rapport repris](RAPPORT.md) présente les questions du scénario, les choix étudiés, les limites et les perspectives. Il distingue les liens hypertextes des redirections HTTP et précise les hypothèses de complexité.

## Fonctionnement du prototype

1. `Controleur` initialise une file d’URL et deux groupes de threads.
2. `Crawler` récupère le contenu des pages via HTTP et recherche des liens avec une expression régulière.
3. `TriUrl` traite les liens collectés et écrit des fichiers de sortie au format JSON construit manuellement.

L’URL initiale est définie dans le code. Un exemple de sortie est conservé dans `mapFr/mapFr/urlsVisitees0.json`.

Dans cette version, les liens découverts ne sont pas remis dans la file des pages à explorer : le parcours de proche en proche reste à compléter. L’extrait séquentiel du rapport initial et le prototype concurrent du dépôt sont deux versions distinctes.

## Organisation

| Chemin | Contenu |
| --- | --- |
| `mapFr/mapFr/src/Controleur.java` | Point d’entrée et orchestration |
| `mapFr/mapFr/src/Crawler.java` | Récupération HTTP et extraction de liens |
| `mapFr/mapFr/src/TriUrl.java` | Traitement des résultats et écriture des fichiers |
| `mapFr/Test/` | Expérimentations séparées sur les threads et le partage de ressources |
| `RAPPORT.md` | Rapport de conception repris à partir du document de travail initial |

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

Le rapport initial présente le scénario « Visualiser le Web FR » et cinq questions de travail : conservation des relations, méthode de parcours, mémoire nécessaire, filtrage et déduplication des URL, puis exécution sur plusieurs machines. Elles sont reprises et reformulées dans [le rapport](RAPPORT.md#2-questions-de-conception).

Le document est un rapport de travail, pas un énoncé pédagogique séparé. Le README interne de `mapFr/mapFr/` reste un fichier générique de démarrage Java pour VS Code.
