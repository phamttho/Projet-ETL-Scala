# Projet ETL Scala — Countries (Pays du monde)

## Thi Tho PHAM - Yann NZELONG

## Objectif

Construire un pipeline **ETL** complet en Scala :

- **Extract** : lire et parser des fichiers **JSON** avec **Circe**
- **Transform** : calculer des statistiques avec les **HOF** (`map`, `filter`, `groupBy`, `fold`, etc.) et les **monades** (`Option`, `Try`, `Either`)
- **Load** : exporter les résultats dans :
  - `output/results.json`
  - `output/report.txt`

Le dataset utilisé est **Dataset 1 : Countries** (données géographiques, démographiques et économiques).

## Structure du projet

Projet-ETL-Scala/
├── build.sbt
├── data/
│   ├── data_clean.json
│   ├── data_dirty.json
│   ├── data_large.json
│   └── SPECIFICATIONS.md
├── src/main/scala/
│   ├── Country.scala
│   ├── DataLoader.scala
│   ├── DataValidator.scala
│   ├── StatsCalculator.scala
│   ├── ReportGenerator.scala
│   └── Main.scala
└── output/
├── results.json
└── report.txt


---

## Exécution du projet

### Lancer le programme

À la racine du projet :

```bash
sbt run
```

--- 

## Validation et qualité des données

Le pipeline applique les règles suivantes :

Parsing JSON sécurisé avec Circe

Validation des champs :

- `Parsing JSON sécurisé avec Circe`

- `Validation des champs :`

    - `population > 0`

    - `area > 0`

    - `code non vide`

- `Suppression des doublons (clé : code)`

- `Comptage :`

    - `nombre total de pays lus`

    - `nombre de pays valides`

    - `erreurs de parsing`

    - `doublons supprimés`

Les erreurs sont gérées via Either afin de ne pas interrompre le pipeline.

## Performance

Le temps de traitement est mesuré avec :

- System.currentTimeMillis() dans Main

- affichage final en secondes

- objectif conseillé : < 10 secondes sur data_large.json (machine moderne).

## Choix techniques

- Case class Country : modèle immuable

- Either pour la gestion des erreurs sans interrompre le pipeline

- Fonctions d’ordre supérieur pour tous les calculs

- Séparation claire des responsabilités :

    - DataLoader : lecture & parsing

    - DataValidator : validation & nettoyage

    - StatsCalculator : calculs statistiques

    - ReportGenerator : génération des outputs

## Tests 

``` bash
sbt test
```