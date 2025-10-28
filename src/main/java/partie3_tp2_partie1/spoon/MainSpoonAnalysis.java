package partie3_tp2_partie1.spoon;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Point d'entrée principal pour l'Exercice 3
 * Comparaison entre l'approche manuelle et l'approche Spoon
 */
public class MainSpoonAnalysis {

    public static void main(String[] args) {
    	Scanner scanner = new Scanner(System.in);
    	scanner.useLocale(Locale.US); // Force le point comme séparateur décimal

        
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  EXERCICE 3 : Analyse de Code avec SPOON                  ║");
        System.out.println("║  Identification de Modules via Clustering Hiérarchique    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        // Configuration
        String projectPath = "C:\\Users\\Jihen\\eclipse-workspace\\ProjectTest2";
        double CP = 0.1; // Seuil de couplage
        
        System.out.println("📁 Projet à analyser : " + projectPath);
        System.out.println("🎯 Seuil de couplage (CP) : " + CP);
        System.out.println();
        
        // Menu interactif
        boolean running = true;
        while (running) {
            System.out.println("\n╔═══════════════════════════════════╗");
            System.out.println("║          MENU PRINCIPAL           ║");
            System.out.println("╚═══════════════════════════════════╝");
            System.out.println("1. 📊 Exercice 1 : Graphe d'appel avec Spoon");
            System.out.println("2. 🔗 Exercice 1 : Calcul du couplage avec Spoon");
            System.out.println("3. 📦 Exercice 2 : Identification de modules avec Spoon");
            System.out.println("4. 🖼️  Exercice 2 : Visualisation des modules (GUI)");
            System.out.println("5. 🚀 Exécuter tout (Analyse complète)");
            System.out.println("6. ⚙️  Changer le seuil CP");
            System.out.println("0. ❌ Quitter");
            System.out.print("\n➤ Votre choix : ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consommer le retour à la ligne
            
            switch (choice) {
                case 1:
                    exercice1GrapheAppel(projectPath);
                    break;
                    
                case 2:
                    exercice1Couplage(projectPath);
                    break;
                    
                case 3:
                    exercice2Modules(projectPath, CP);
                    break;
                    
                case 4:
                    exercice2Visualisation(projectPath, CP);
                    break;
                    
                case 5:
                    analyseComplete(projectPath, CP);
                    break;
                    
                case 6:
                    System.out.print("\n🎯 Nouveau seuil CP (ex: 0.1) : ");
                    CP = scanner.nextDouble();
                    System.out.println("✅ Seuil CP mis à jour : " + CP);
                    break;
                    
                case 0:
                    running = false;
                    System.out.println("\n👋 Au revoir !");
                    break;
                    
                default:
                    System.out.println("\n❌ Choix invalide !");
            }
        }
        
        scanner.close();
    }

    /**
     * Exercice 1 : Analyse du graphe d'appel
     */
    private static void exercice1GrapheAppel(String projectPath) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  EXERCICE 1 : GRAPHE D'APPEL AVEC SPOON");
        System.out.println("=".repeat(60));
        
        SpoonCallGraphAnalyzer analyzer = new SpoonCallGraphAnalyzer(projectPath);
        analyzer.analyze();
    }

    /**
     * Exercice 1 : Calcul du couplage
     */
    private static void exercice1Couplage(String projectPath) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  EXERCICE 1 : CALCUL DU COUPLAGE AVEC SPOON");
        System.out.println("=".repeat(60));
        
        SpoonCallGraphAnalyzer analyzer = new SpoonCallGraphAnalyzer(projectPath);
        analyzer.analyze();
        
        SpoonCouplingCalculator calc = new SpoonCouplingCalculator(analyzer);
        
        System.out.println("\n📊 Matrice de couplage complète :");
        System.out.println("-".repeat(60));
        
        calc.getCouplingMatrix().entrySet().stream()
            .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
            .forEach(entry -> {
                System.out.println(String.format("  %-40s : %.4f", 
                    entry.getKey(), entry.getValue()));
            });
    }

    /**
     * Exercice 2 : Identification des modules
     */
    private static void exercice2Modules(String projectPath, double CP) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  EXERCICE 2 : IDENTIFICATION DE MODULES AVEC SPOON");
        System.out.println("=".repeat(60));
        
        SpoonCallGraphAnalyzer analyzer = new SpoonCallGraphAnalyzer(projectPath);
        analyzer.analyze();
        
        SpoonCouplingCalculator calc = new SpoonCouplingCalculator(analyzer);
        
        SpoonModuleIdentifier identifier = new SpoonModuleIdentifier(calc, CP);
        List<List<String>> modules = identifier.identifyModules();
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  RÉSULTAT : MODULES IDENTIFIÉS");
        System.out.println("=".repeat(60) + "\n");
        
        int i = 1;
        for (List<String> module : modules) {
            System.out.println("📦 Module " + i + " (" + module.size() + " classe(s)) :");
            for (String className : module) {
                System.out.println("   • " + className);
            }
            System.out.println();
            i++;
        }
    }

    /**
     * Exercice 2 : Visualisation graphique
     */
    private static void exercice2Visualisation(String projectPath, double CP) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  EXERCICE 2 : VISUALISATION DES MODULES");
        System.out.println("=".repeat(60));
        
        SpoonCallGraphAnalyzer analyzer = new SpoonCallGraphAnalyzer(projectPath);
        analyzer.analyze();
        
        SpoonCouplingCalculator calc = new SpoonCouplingCalculator(analyzer);
        
        SpoonModuleIdentifier identifier = new SpoonModuleIdentifier(calc, CP);
        List<List<String>> modules = identifier.identifyModules();
        
        System.out.println("\n🖥️ Ouverture de l'interface graphique...");
        SpoonModuleVisualizer.showModules(modules);
    }

    /**
     * Analyse complète : tous les exercices en une fois
     */
    private static void analyseComplete(String projectPath, double CP) {
        System.out.println("\n" + "╔" + "═".repeat(58) + "╗");
        System.out.println("║" + " ".repeat(15) + "ANALYSE COMPLÈTE AVEC SPOON" + " ".repeat(16) + "║");
        System.out.println("╚" + "═".repeat(58) + "╝\n");
        
        // Étape 1 : Graphe d'appel
        System.out.println("▶ Étape 1/3 : Analyse du graphe d'appel...");
        SpoonCallGraphAnalyzer analyzer = new SpoonCallGraphAnalyzer(projectPath);
        analyzer.analyze();
        
        // Étape 2 : Couplage
        System.out.println("\n▶ Étape 2/3 : Calcul du couplage...");
        SpoonCouplingCalculator calc = new SpoonCouplingCalculator(analyzer);
        
        // Étape 3 : Modules
        System.out.println("\n▶ Étape 3/3 : Identification des modules...");
        SpoonModuleIdentifier identifier = new SpoonModuleIdentifier(calc, CP);
        List<List<String>> modules = identifier.identifyModules();
        
        // Résumé final
        System.out.println("\n" + "╔" + "═".repeat(58) + "╗");
        System.out.println("║" + " ".repeat(20) + "RÉSUMÉ FINAL" + " ".repeat(26) + "║");
        System.out.println("╚" + "═".repeat(58) + "╝\n");
        
        System.out.println("✅ Classes analysées : " + calc.getAllClasses().size());
        System.out.println("✅ Relations inter-classes : " + calc.getTotalRelations());
        System.out.println("✅ Modules identifiés : " + modules.size());
        System.out.println("✅ Seuil CP utilisé : " + CP);
        
        System.out.println("\n📦 Modules détaillés :");
        int i = 1;
        for (List<String> module : modules) {
            System.out.println("  Module " + i + " : " + module);
            i++;
        }
        
        // Ouvrir l'interface graphique
        System.out.println("\n🖥️ Ouverture de l'interface graphique...");
        SpoonModuleVisualizer.showModules(modules);
    }
}