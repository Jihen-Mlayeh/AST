# 📊 TP N°2 - Analyse de Code et Identification de Modules

## 🎯 Objectifs du Projet

Ce projet implémente une suite d'outils permettant de :
- **Analyser** la structure d'un projet Java à partir des graphes d'appel  
- **Calculer** les métriques de couplage entre classes  
- **Identifier** automatiquement les modules par **clustering hiérarchique**  
- **Comparer** deux approches : **JavaParser** et **Spoon**

## Structure du Projet

```
tp_ast/
├── src/main/java/
│   ├── partie2/                          # Exercice 1 - Partie 1
│   │   ├── CallGraphAnalyzer.java        # Analyse du graphe d'appel
│   │   └── MethodCallVisitor.java        # Visiteur AST JavaParser
│   │
│   ├── partie3_tp2_partie1/              # Exercice 1 - Partie 2
│   │   ├── CouplingCalculator.java       # Calcul du couplage
│   │   ├── CouplingGraphGenerator.java   # Génération graphe DOT
│   │   └── MainCouplingAnalysis.java     # Point d'entrée Ex1
│   │
│   ├── partie3_tp2_partie1/graph/        # Visualisation graphe
│   │   ├── ClassCouplingData.java        # Données couplage
│   │   ├── CouplingRelation.java         # Relation entre classes
│   │   └── GraphVisualizer.java          # Interface Swing
│   │
│   ├── partie3_tp2_partie1/module/       # Exercice 2
│   │   ├── ModuleIdentifier.java         # Clustering hiérarchique
│   │   └── ModuleVisualizer.java         # Interface modules
│   │
│   └── partie3_tp2_partie1/spoon/        # Exercice 3
│       ├── SpoonCallGraphAnalyzer.java   # Analyse avec Spoon
│       ├── SpoonCouplingCalculator.java  # Couplage avec Spoon
│       ├── SpoonModuleIdentifier.java    # Clustering avec Spoon
│       ├── SpoonModuleVisualizer.java    # Interface Spoon
│       └── MainSpoonAnalysis.java        # Menu interactif
│
├── ProjectTest2/                         # Projet de test
│   ├── calcul/
│   │   ├── Calculator.java
│   │   ├── AdvancedCalculator.java
│   │   └── MathUtils.java
│   ├── main/
│   │   └── Main.java
│   ├── report/
│   │   ├── Report.java
│   │   ├── CSVExporter.java
│   │   └── PDFExporter.java
│   └── user/
│       ├── User.java
│       ├── Account.java
│       └── Profile.java
│
├── pom.xml                               # Dépendances Maven
├── README.md                             # Ce fichier
└── Rapport_TP2.pdf                       # Rapport LaTeX compilé
```

---

## 🚀 Installation

### Prérequis
- **Java JDK 11+**
- **Maven**
- IDE : Eclipse ou IntelliJ IDEA
### Dépendances

#### Option 1 : Maven (Recommandé)

Ajoutez dans votre `pom.xml` :

```xml
<dependencies>
    <!-- JavaParser -->
    <dependency>
        <groupId>com.github.javaparser</groupId>
        <artifactId>javaparser-core</artifactId>
        <version>3.24.2</version>
    </dependency>
    
    <!-- Spoon -->
    <dependency>
        <groupId>fr.inria.gforge.spoon</groupId>
        <artifactId>spoon-core</artifactId>
        <version>10.4.2</version>
    </dependency>
</dependencies>
```

Puis : `mvn clean install`

#### Option 2 : JARs manuels

1. Téléchargez les JARs depuis Maven Central
2. Dans Eclipse : **Build Path** → **Add External JARs**

---

## 📖 Guide d'Utilisation

### Exercice 1 : Graphe d'Appel et Couplage

#### Étape 1 : Analyser le graphe d'appel

```bash
# Lancer l'analyseur
java partie2.CallGraphAnalyzer
```

**Paramètre à modifier :** Chemin du projet dans `CallGraphAnalyzer.java` ligne 15

```java
String projectPath = "C:\\chemin\\vers\\ProjectTest2";
```

**Sortie attendue :**
- Liste de toutes les méthodes
- Appels détectés pour chaque méthode
- Statistiques (nombre de classes, méthodes, appels)

#### Étape 2 : Calculer le couplage

```bash
# Lancer l'analyse de couplage
java partie3_tp2_partie1.MainCouplingAnalysis
```

**Sortie attendue :**
- Matrice de couplage normalisée
- Graphe DOT pour visualisation avec Graphviz

#### Étape 3 : Visualiser le graphe

```bash
# Lancer l'interface graphique
java partie3_tp2_partie1.graph.GraphVisualizer
```

**Résultat :** Fenêtre Swing avec graphe circulaire interactif

---

### Exercice 2 : Identification de Modules

```bash
# Lancer l'identification et visualisation
java partie3_tp2_partie1.module.ModuleVisualizer
```

**Paramètres configurables :**

```java
// Dans ModuleVisualizer.java
double CP = 0.1; // Seuil de couplage (ligne 91)
```

**Recommandations :**
- `CP = 0.05` : Plus de fusions, moins de modules
- `CP = 0.1` : Équilibré (recommandé)
- `CP = 0.15` : Moins de fusions, plus de modules

**Sortie attendue :**
- Console : Processus de clustering détaillé
- Interface : Modules avec classes regroupées

---

### Exercice 3 : Analyse avec Spoon

```bash
# Lancer le menu interactif
java partie3_tp2_partie1.spoon.MainSpoonAnalysis

---

👩‍💻 Auteure

Jihen Mlayeh
Master 2 — Génie Logiciel
Faculté des Sciences de Montpellier
📄 Documentation

➡️ Le fichier Rapport_TP2.pdf contient les explications détaillées, les résultats et les captures d’exécution.
