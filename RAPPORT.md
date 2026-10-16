# Visualiser le Web FR

## Rapport de conception et état du prototype

Cette version reprend et clarifie le rapport de travail initial intitulé « Scénario 4 : visualiser le Web FR ». Elle distingue les objectifs étudiés, les solutions envisagées et l’implémentation présente dans le dépôt. Le code applicatif n’a pas été modifié pour cette reprise documentaire.

## 1. Problématique

Comment explorer un ensemble de sites web français, conserver les liens entre eux et rendre leurs relations compréhensibles ?

L’objectif initial était de préparer deux restitutions : une carte des relations entre sites et un classement selon un indicateur de liens entrants ou sortants. Le rapport envisageait de démarrer à partir de 100 sites populaires en France. Cette sélection et ce classement ne sont pas implémentés dans le code public.

Le prototype se concentre sur la récupération de pages, l’extraction de liens et une première organisation concurrente du traitement.

### Définir le périmètre

Le filtre `.fr` constitue une approximation technique : il ne couvre pas tous les sites français ou francophones, et un domaine en `.fr` ne suffit pas à caractériser la langue de son contenu. Une collecte partielle ne représente donc pas l’ensemble du web français.

Le terme « redirection » du rapport initial désignait souvent un lien entre pages. Il faut distinguer :

- **Lien hypertexte** : référence d’une page source vers une autre URL.
- **Redirection HTTP** : réponse du serveur orientant la requête vers une autre adresse.

Le prototype recherche des chaînes ressemblant à des URL dans le contenu reçu. Il ne construit pas un relevé des chaînes de redirections HTTP.

## 2. Questions de conception

Le rapport initial organise le travail autour des questions suivantes, reformulées ici :

1. Comment conserver la mémoire des sites visités et de leurs liaisons ?
2. Quelle méthode utiliser pour parcourir le web ?
3. Quel espace mémoire prévoir pour les informations collectées ?
4. Comment extraire les URL et éviter les visites répétées ?
5. Comment répartir le travail entre plusieurs machines ?

Ces questions sont issues du rapport fourni ; le dépôt ne contient pas d’énoncé pédagogique séparé.

## 3. Données et restitutions envisagées

| Élément | Description |
| --- | --- |
| Entrées | Une ou plusieurs URL de départ et un périmètre d’exploration |
| Pages | URL, domaine, état de visite et éventuelle erreur de récupération |
| Relations | URL source et URL cible de chaque lien |
| Indicateurs envisagés | Nombre de liens entrants et sortants, fréquence de découverte |
| Restitutions envisagées | Graphe des relations et tableau de classement |

Une URL de page et un nom de domaine représentent des niveaux différents. La normalisation et le choix du niveau d’agrégation doivent précéder le calcul d’indicateurs. Un simple nombre de liens n’est pas une mesure de fréquentation ni un score PageRank.

### Stockage étudié

Le rapport envisage une base relationnelle, avec des identifiants pour les pages ou domaines et une table de relations source-cible. Une file de travail persistante permettrait ensuite de reprendre une collecte interrompue et de partager le travail.

Il s’agit d’une proposition d’architecture. Le prototype public utilise des collections Java en mémoire et une écriture de fichiers ; aucune intégration SQL ni file distribuée n’y est présente.

## 4. Parcours envisagé et prévention des cycles

Le principe étudié est un parcours avec une file d’URL à explorer :

```text
Ajouter les URL de départ dans la file et l’ensemble des URL connues
Tant que la file contient des éléments et que les limites ne sont pas atteintes :
    Retirer une URL de la file
    Récupérer sa page et extraire ses liens
    Pour chaque lien accepté :
        Enregistrer la relation source-cible
        Si la cible est nouvelle :
            La marquer comme connue et l’ajouter à la file
```

Ce pseudocode décrit la conception visée. Il ne reproduit pas le fonctionnement complet de la version concurrente actuelle.

Conserver un ensemble d’URL connues permet d’éviter de revisiter sans cesse un cycle tel que A → B → A. En concurrence, la vérification et l’ajout doivent former une opération cohérente entre travailleurs. Il faut aussi borner le nombre de pages, la durée ou la profondeur du parcours : l’absence de cycle ne garantit pas qu’une exploration du web se termine rapidement.

## 5. Implémentation publique

| Classe | Responsabilité observée |
| --- | --- |
| `Controleur` | Initialise les collections, une URL de départ et deux exécuteurs : deux threads pour le crawl, un pour le traitement des résultats |
| `Crawler` | Lit les réponses HTTP avec `HttpURLConnection` et cherche des URL via une expression régulière |
| `TriUrl` | Consomme les résultats, alimente une table en mémoire et écrit des fichiers construits manuellement au format JSON |

Le contrôleur démarre actuellement sur `https://www.lemonde.fr/`. Le rapport initial présentait aussi un exemple séquentiel démarrant sur une page Wikipédia. Ce sont deux versions distinctes : l’extrait du rapport ne doit pas être pris pour le point d’entrée du dépôt.

Le code ajoute les liens découverts dans une table, mais ne les réinjecte pas dans la file `sitesAExplorer`. Le parcours récursif de toutes les pages découvertes n’est donc pas réalisé par cette version.

### Extraction et affichage

Le filtrage repose sur une expression régulière cherchant des URL en `.fr`. Cette approche ne remplace pas l’analyse des attributs de liens HTML : elle peut manquer les liens relatifs et sélectionner des chaînes qui ne sont pas des liens navigables.

Le rapport envisageait un navigateur sans interface pour les pages nécessitant JavaScript, et une requête HTTP simple dans les autres cas. Le dépôt utilise uniquement des requêtes HTTP : Selenium, le rendu JavaScript et la carte interactive restent des pistes de conception.

## 6. Complexité, mémoire et performance

Le rapport initial contenait des estimations de complexité et de taille d’objets qui ne suffisent pas à établir un résultat général. La taille réelle d’une collection ou d’une chaîne dépend notamment de la JVM, de sa configuration et des données. Aucun chiffre fixe en octets n’est retenu ici comme mesure.

Pour un parcours correctement dédupliqué, on peut raisonner sur un graphe fini de **V pages** et **E liens**, déjà extrait : avec un ensemble à accès moyen constant, sa traversée est de l’ordre de **O(V + E)**. Ce modèle exclut les téléchargements, l’analyse des contenus et les écritures sur disque. Il ne constitue pas une garantie de complexité du prototype actuel.

Une recherche par `contains` dans une liste parcourt potentiellement la liste entière. Le choix d’une `LinkedList` ne rend donc pas automatiquement efficace la détection des doublons. Dans le prototype concurrent, les écritures répétées de l’ensemble des résultats peuvent aussi devenir coûteuses.

### Mesures à réaliser

- Pages récupérées et nouvelles URL acceptées par minute.
- Temps réseau, temps d’extraction et temps d’écriture.
- Taille de la file, mémoire utilisée, erreurs et doublons.
- Comportement avec un puis plusieurs travailleurs sur le même corpus borné.

Aucun benchmark ni gain chiffré de parallélisation n’est fourni. Une augmentation du nombre de travailleurs peut accroître les ressources consommées ; l’effet énergétique n’a pas été mesuré.

## 7. Validation et limites actuelles

La compilation des trois classes principales a été vérifiée avec **JDK 17**, avec des avertissements de typage dans `Crawler`. Le crawl réseau n’a pas été exécuté lors de la revue documentaire.

Les principales limites observées sont :

- Une condition de soumission des tâches qui peut alimenter continuellement les exécuteurs.
- L’attente de terminaison placée avant l’appel à `shutdown()` et une boucle permanente dans `TriUrl`.
- Des collections partagées dont tous les accès ne sont pas synchronisés.
- L’absence de remise en file des liens découverts pour poursuivre le parcours.
- Une extraction par expression régulière et une sérialisation JSON manuelle à fiabiliser.

Le prototype n’est donc pas présenté comme un outil de collecte finalisé.

## 8. Évolutions envisagées

1. Stabiliser le parcours, la déduplication et les conditions d’arrêt.
2. Utiliser des échanges sûrs entre travailleurs et maîtriser le nombre de tâches en attente.
3. Normaliser les URL, analyser les liens HTML et utiliser une sérialisation JSON dédiée.
4. Rendre la collecte configurable et respecter le périmètre et les limites de débit définis.
5. Ajouter la persistance et des mesures reproductibles sur un corpus de test.
6. Construire une visualisation, puis étudier une file partagée pour plusieurs machines.

La reprise après incident, les journaux, le suivi des ressources et la répartition du travail sont des prolongements possibles vers des problématiques d’exploitation et de cloud. Ils restent des perspectives, pas des fonctionnalités réalisées.

## Bilan

Ce projet permet d’étudier la transformation d’un besoin de visualisation en pipeline de collecte : récupération HTTP, extraction, stockage des relations et coordination de traitements. Le rapport formalise cette réflexion et les écarts restant à combler entre la conception et le prototype.

[Retour à la présentation du dépôt](README.md)
