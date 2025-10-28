package partie3_tp2_partie1.spoon;

import java.util.*;

/**
 * Identification de modules via clustering hiérarchique basé sur le couplage
 * Version utilisant SpoonCouplingCalculator
 */
public class SpoonModuleIdentifier {

    private final SpoonCouplingCalculator calculator;
    private final double CP; // Coupling threshold

    public SpoonModuleIdentifier(SpoonCouplingCalculator calculator, double CP) {
        this.calculator = calculator;
        this.CP = CP;
    }

    /**
     * Identifie les modules à partir des classes et de leur couplage
     * @return Liste de modules, chaque module est une List de classes
     */
    public List<List<String>> identifyModules() {
        Set<String> classes = calculator.getAllClasses();
        int maxModules = Math.max(1, classes.size() / 2);

        System.out.println("\n🎯 Paramètres du clustering (Spoon) :");
        System.out.println("   - Nombre de classes : " + classes.size());
        System.out.println("   - Maximum de modules (M/2) : " + maxModules);
        System.out.println("   - Seuil de couplage (CP) : " + CP);

        // Chaque classe est initialement un cluster
        List<Set<String>> clusters = new ArrayList<>();
        for (String cls : classes) {
            Set<String> cluster = new HashSet<>();
            cluster.add(cls);
            clusters.add(cluster);
        }

        System.out.println("\n📊 Démarrage du clustering hiérarchique...");
        int iteration = 1;

        // Fusion hiérarchique basée sur le couplage
        while (clusters.size() > 1) {
            double maxCoupling = -1;
            int mergeA = -1, mergeB = -1;

            // Trouver les deux clusters les plus couplés
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double avgCoupling = averageCoupling(clusters.get(i), clusters.get(j));
                    if (avgCoupling > maxCoupling) {
                        maxCoupling = avgCoupling;
                        mergeA = i;
                        mergeB = j;
                    }
                }
            }

            // Conditions d'arrêt :
            // 1. Aucun couplage ne dépasse le seuil CP
            // 2. ET on a atteint ou est en dessous de M/2 modules
            if (maxCoupling < CP && clusters.size() <= maxModules) {
                System.out.println("\n⛔ Arrêt du clustering :");
                System.out.println("   - Couplage max = " + String.format("%.4f", maxCoupling) + " < CP = " + CP);
                System.out.println("   - Nombre de modules = " + clusters.size() + " <= " + maxModules);
                break;
            }

            // Si on a dépassé M/2, continuer à fusionner même si couplage < CP
            if (clusters.size() > maxModules) {
                System.out.println("🔄 Itération " + iteration + " (FORCÉE - dépassement M/2) :");
            } else if (maxCoupling >= CP) {
                System.out.println("🔄 Itération " + iteration + " :");
            } else {
                // On est à M/2 ou moins ET maxCoupling < CP : on s'arrête
                System.out.println("\n✅ Clustering terminé (contrainte M/2 respectée)");
                break;
            }

            System.out.println("   - Fusion : " + clusters.get(mergeA) + " + " + clusters.get(mergeB));
            System.out.println("   - Couplage moyen : " + String.format("%.4f", maxCoupling));
            System.out.println("   - Clusters restants : " + clusters.size() + " → " + (clusters.size() - 1));

            // Fusionner les clusters
            Set<String> merged = new HashSet<>();
            merged.addAll(clusters.get(mergeA));
            merged.addAll(clusters.get(mergeB));

            // Retirer les anciens clusters en commençant par l'indice le plus grand
            if (mergeA > mergeB) {
                clusters.remove(mergeA);
                clusters.remove(mergeB);
            } else {
                clusters.remove(mergeB);
                clusters.remove(mergeA);
            }

            clusters.add(merged);
            iteration++;
        }

        System.out.println("\n✅ Résultat final : " + clusters.size() + " modules");

        // Convertir Set<String> en List<String> pour correspondre à la signature attendue
        List<List<String>> modules = new ArrayList<>();
        for (Set<String> cluster : clusters) {
            List<String> module = new ArrayList<>(cluster);
            Collections.sort(module); // tri pour lisibilité
            modules.add(module);
        }

        // Vérifier les contraintes
        System.out.println("\n📋 Vérification des contraintes :");
        System.out.println("   ✅ Nombre de modules ≤ M/2 : " + modules.size() + " ≤ " + maxModules);
        
        // Vérifier le couplage moyen de chaque module
        for (int i = 0; i < modules.size(); i++) {
            List<String> module = modules.get(i);
            if (module.size() > 1) {
                double avgCoupling = averageCouplingInModule(module);
                String status = avgCoupling >= CP ? "✅" : "⚠️";
                System.out.println("   " + status + " Module " + (i+1) + " : couplage moyen = " + 
                                 String.format("%.4f", avgCoupling) + 
                                 (avgCoupling >= CP ? " ≥ " : " < ") + CP);
            }
        }

        return modules;
    }

    /**
     * Calcul du couplage moyen entre deux clusters
     */
    private double averageCoupling(Set<String> cluster1, Set<String> cluster2) {
        double total = 0;
        int count = 0;

        for (String clsA : cluster1) {
            for (String clsB : cluster2) {
                if (!clsA.equals(clsB)) {
                    total += calculator.getCoupling(clsA, clsB);
                    count++;
                }
            }
        }

        return count > 0 ? total / count : 0.0;
    }

    /**
     * Calcul du couplage moyen à l'intérieur d'un module
     */
    private double averageCouplingInModule(List<String> module) {
        if (module.size() < 2) return 0.0;
        
        double total = 0;
        int count = 0;

        for (int i = 0; i < module.size(); i++) {
            for (int j = i + 1; j < module.size(); j++) {
                total += calculator.getCoupling(module.get(i), module.get(j));
                count++;
            }
        }

        return count > 0 ? total / count : 0.0;
    }

    /**
     * Exemple d'utilisation
     */
    public static void main(String[] args) {
        // Analyse avec Spoon
        SpoonCallGraphAnalyzer analyzer = new SpoonCallGraphAnalyzer(
            "C:\\Users\\Jihen\\eclipse-workspace\\ProjectTest2"
        );
        analyzer.analyze();

        // Calcul du couplage
        SpoonCouplingCalculator calc = new SpoonCouplingCalculator(analyzer);

        // Identification des modules
        SpoonModuleIdentifier identifier = new SpoonModuleIdentifier(calc, 0.1); // CP = 0.1
        List<List<String>> modules = identifier.identifyModules();

        System.out.println("\n========================================");
        System.out.println("     MODULES IDENTIFIÉS (Spoon)");
        System.out.println("========================================\n");
        
        int i = 1;
        for (List<String> module : modules) {
            System.out.println("Module " + i + " (" + module.size() + " classe(s)) : " + module);
            i++;
        }
    }
}