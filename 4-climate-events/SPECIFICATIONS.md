# 🌪️ Dataset 4 : Climate Events - Événements climatiques

## 📋 Description

Données sur les catastrophes naturelles et événements climatiques extrêmes à travers le monde.

## 📊 Structure des données

### Fichiers fournis

- **data_clean.json** : 100 événements, données parfaites
- **data_dirty.json** : 500 événements avec :
  - Champs manquants (country, damage, name)
  - Valeurs négatives (casualties, affected)
  - Severité hors limites (< 1 ou > 5)
  - Années impossibles (< 1900 ou > 2025)
  - Types d'événements invalides
  - Doublons (même ID)
- **data_large.json** : 10 000 entrées (historique étendu)

## 🎯 Outputs attendus

### 1. Statistiques générales

**Fichier** : `results.json`

```json
{
  "statistics": {
    "total_events_parsed": ...,
    "total_events_valid": ...,
    "parsing_errors": ...,
    "duplicates_removed": ...
  },
  "events_by_type": {
    "Hurricane": ...,
    "Flood": ...,
    "Earthquake": ...,
    "Wildfire": ...,
    "Drought": ...,
    "Tornado": ...
  },
  "events_by_severity": {
    "1": ...,
    "2": ...,
    "3": ...,
    "4": ...,
    "5": ...
  },
  "deadliest_events": [
    {
      "name": "...",
      "type": "...",
      "casualties": ...,
      "year": ...
    }
  ],
  "most_expensive_events": [...],
  "events_by_year": {
    "2020": ...,
    "2021": ...,
    "2022": ...,
    "2023": ...,
    "2024": ...
  },
  "events_by_country": {
    "United States": ...,
    "China": ...,
    "India": ...,
    "Japan": ...,
    "Philippines": ...
  },
  "total_casualties": ...,
  "total_affected": ...,
  "total_damage": ...,
  "average_casualties_by_type": {
    "Earthquake": ...,
    "Hurricane": ...,
    "Flood": ...,
    "Wildfire": ...,
    "Tornado": ...,
    "Drought": ...
  },
  "most_affected_regions": [
    {"region": "...", "events": ...}
  ]
}
```

### 2. Rapport texte

**Fichier** : `report.txt`

```
===============================================
   RAPPORT D'ANALYSE - ÉVÉNEMENTS CLIMATIQUES
===============================================

📊 STATISTIQUES DE PARSING
---------------------------
- Entrées totales lues      : ...
- Entrées valides           : ...
- Erreurs de parsing        : ...
- Doublons supprimés        : ...

🌍 RÉPARTITION PAR TYPE
------------------------
- Inondations (Flood)       : ... événements
- Ouragans (Hurricane)      : ... événements
- Séismes (Earthquake)      : ... événements
- Feux de forêt (Wildfire)  : ... événements
- Sécheresses (Drought)     : ... événements
- Tornades (Tornado)        : ... événements

⚠️  RÉPARTITION PAR SÉVÉRITÉ
-----------------------------
- Niveau 1 (Faible)         : ... événements
- Niveau 2 (Modéré)         : ... événements
- Niveau 3 (Important)      : ... événements
- Niveau 4 (Grave)          : ... événements
- Niveau 5 (Catastrophique) : ... événements

☠️  TOP 10 - LES PLUS MEURTRIERS
---------------------------------
1. ...                      : ... victimes
2. ...                      : ... victimes
...

💰 TOP 10 - LES PLUS COÛTEUX
-----------------------------
1. ...                      : ... M$
2. ...                      : ... M$
...

📅 ÉVOLUTION PAR ANNÉE (5 dernières)
-------------------------------------
- 2020                      : ... événements
- 2021                      : ... événements
- 2022                      : ... événements
- 2023                      : ... événements
- 2024                      : ... événements

🗺️  TOP 10 - PAYS LES PLUS TOUCHÉS
------------------------------------
1. ...                      : ... événements
2. ...                      : ... événements
...

📊 IMPACT GLOBAL
-----------------
- Total victimes            : ... personnes
- Total personnes affectées : ... personnes
- Coût total des dégâts     : ... M$

📈 MOYENNES PAR TYPE D'ÉVÉNEMENT
---------------------------------
VICTIMES MOYENNES :
- Séismes (Earthquake)      : ... victimes
- Sécheresses (Drought)     : ... victimes
- Ouragans (Hurricane)      : ... victimes
- Inondations (Flood)       : ... victimes
- Feux (Wildfire)           : ... victimes
- Tornades (Tornado)        : ... victimes

🌍 TOP 5 - RÉGIONS LES PLUS TOUCHÉES
--------------------------------------
1. ...                      : ... événements
2. ...                      : ... événements
...

⏱️  PERFORMANCE
---------------
- Temps de traitement       : ... secondes
- Entrées/seconde           : ...

===============================================
```

## 🔧 Transformations à implémenter

### Obligatoires

1. **Parsing et validation**
   - Parser les 3 fichiers
   - Valider : year entre 1900 et 2025, severity entre 1 et 5, casualties >= 0
   - Types valides : "Hurricane", "Flood", "Earthquake", "Wildfire", "Drought", "Tornado"
   - Supprimer les doublons (par ID)

2. **Top 10**
   - Top 10 événements les plus meurtriers
   - Top 10 événements les plus coûteux (filtrer ceux qui ont un damage)

3. **Agrégations**
   - Compter événements par type
   - Compter événements par sévérité
   - Compter événements par année (5 dernières années)
   - Compter événements par pays (top 10)

4. **Statistiques globales**
   - Total des victimes
   - Total des personnes affectées
   - Coût total des dégâts
   - Victimes moyennes par type d'événement

### Bonus (optionnel)

5. **Analyse temporelle**
   - Tendance : nombre d'événements par décennie
   - Événements de sévérité 5 par décennie

6. **Événements nommés**
   - Liste des événements majeurs ayant un nom
   - Statistiques sur les ouragans nommés uniquement

7. **Zones à risque**
   - Régions avec le plus d'événements
   - Type d'événement le plus fréquent par région

## 💡 Conseil - Mesure de performance

```scala
val start = System.currentTimeMillis()
// ... traitement
val duration = (System.currentTimeMillis() - start) / 1000.0
println(f"Traitement effectué en $duration%.3f secondes")
```

## ✅ Critères de réussite

- ✅ Les 3 fichiers sont parsés sans erreur fatale
- ✅ Les types d'événements sont normalisés
- ✅ Les données invalides sont filtrées (sévérité hors limites, années impossibles)
- ✅ Les 10 statistiques obligatoires sont présentes
- ✅ `results.json` est valide et bien formaté
- ✅ `report.txt` est lisible et structuré
- ✅ Le traitement de `data_large.json` prend < 10 secondes



