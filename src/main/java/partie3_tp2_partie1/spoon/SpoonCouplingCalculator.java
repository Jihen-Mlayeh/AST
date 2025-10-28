package partie3_tp2_partie1.spoon;

import java.util.*;

/**
 * Calculateur de couplage utilisant les données de SpoonCallGraphAnalyzer
 */
public class SpoonCouplingCalculator {

    private Map<String, Set<String>> callGraph;
    private Map<String, SpoonCallGraphAnalyzer.MethodInfo> methodInfoMap;
    private int totalRelations;
    private Map<String, Double> couplingMatrix = new HashMap<>();
    private Set<String> allClasses = new HashSet<>();

    public SpoonCouplingCalculator(SpoonCallGraphAnalyzer analyzer) {
        this.callGraph = analyzer.getCallGraph();
        this.methodInfoMap = analyzer.getMethodInfoMap();
        
        System.out.println("\n🔍 [SpoonCouplingCalculator] Initialisation...");
        System.out.println("   - callGraph size: " + callGraph.size());
        System.out.println("   - methodInfoMap size: " + methodInfoMap.size());
        
        extractAllClasses();
        System.out.println("   - Classes extraites: " + allClasses.size());
        System.out.println("   📋 Classes trouvées: " + allClasses);
        
        computeCouplingMatrix();
        this.totalRelations = computeTotalRelations();
        
        System.out.println("   - Total relations: " + totalRelations);
        System.out.println("   - Matrice de couplage: " + couplingMatrix.size() + " entrées");
    }

    /**
     * Calcule le couplage entre deux classes
     */
    public double getCoupling(String classA, String classB) {
        String key1 = classA + "->" + classB;
        String key2 = classB + "->" + classA;
        return couplingMatrix.getOrDefault(key1, 0.0) + couplingMatrix.getOrDefault(key2, 0.0);
    }

    /**
     * Retourne le nombre total de relations inter-classes
     */
    public int getTotalRelations() {
        return totalRelations;
    }

    /**
     * Retourne la matrice de couplage
     */
    public Map<String, Double> getCouplingMatrix() {
        return couplingMatrix;
    }

    /**
     * Retourne toutes les classes
     */
    public Set<String> getAllClasses() {
        return allClasses;
    }

    /**
     * Extrait toutes les classes depuis le graphe d'appel
     */
    private void extractAllClasses() {
        // Méthode 1 : depuis methodInfoMap
        for (String methodKey : methodInfoMap.keySet()) {
            if (methodKey != null && methodKey.contains(".")) {
                String className = methodKey.substring(0, methodKey.lastIndexOf('.'));
                if (!className.isEmpty() && !isJavaStandardClass(className)) {
                    allClasses.add(className);
                }
            }
        }
        
        // Méthode 2 : depuis callGraph
        for (String caller : callGraph.keySet()) {
            if (caller != null && caller.contains(".")) {
                String callerClass = caller.substring(0, caller.lastIndexOf('.'));
                if (!callerClass.isEmpty() && !isJavaStandardClass(callerClass)) {
                    allClasses.add(callerClass);
                }
            }
            
            Set<String> callees = callGraph.get(caller);
            if (callees != null) {
                for (String called : callees) {
                    if (called != null && called.contains(".")) {
                        String calledClass = called.substring(0, called.lastIndexOf('.'));
                        if (!calledClass.isEmpty() && !isJavaStandardClass(calledClass)) {
                            allClasses.add(calledClass);
                        }
                    }
                }
            }
        }
    }

    /**
     * Vérifie si c'est une classe Java standard
     */
    private boolean isJavaStandardClass(String className) {
        return className.startsWith("java.") || 
               className.startsWith("javax.") ||
               className.startsWith("sun.") ||
               className.equals("PrintStream") ||
               className.equals("System") ||
               className.equals("String");
    }

    /**
     * Calcule le nombre total de relations inter-classes
     */
    private int computeTotalRelations() {
        int total = 0;
        for (String caller : callGraph.keySet()) {
            if (caller == null || !caller.contains(".")) continue;
            
            String callerClass = caller.substring(0, caller.lastIndexOf('.'));
            if (isJavaStandardClass(callerClass)) continue;
            
            Set<String> callees = callGraph.get(caller);
            if (callees == null) continue;
            
            for (String called : callees) {
                if (called == null || !called.contains(".")) continue;
                
                String calledClass = called.substring(0, called.lastIndexOf('.'));
                if (isJavaStandardClass(calledClass)) continue;
                
                if (!callerClass.equals(calledClass)) {
                    total++;
                }
            }
        }
        return total;
    }

    /**
     * Construit la matrice de couplage normalisée
     */
    private void computeCouplingMatrix() {
        Map<String, Integer> rawCounts = new HashMap<>();
        int total = 0;

        for (String caller : callGraph.keySet()) {
            if (caller == null || !caller.contains(".")) continue;
            
            String callerClass = caller.substring(0, caller.lastIndexOf('.'));
            if (isJavaStandardClass(callerClass)) continue;
            
            Set<String> callees = callGraph.get(caller);
            if (callees == null) continue;
            
            for (String called : callees) {
                if (called == null || !called.contains(".")) continue;
                
                String calledClass = called.substring(0, called.lastIndexOf('.'));
                if (isJavaStandardClass(calledClass)) continue;
                
                if (!callerClass.equals(calledClass)) {
                    String key = callerClass + "->" + calledClass;
                    rawCounts.put(key, rawCounts.getOrDefault(key, 0) + 1);
                    total++;
                }
            }
        }

        // Normalisation
        if (total > 0) {
            for (Map.Entry<String, Integer> e : rawCounts.entrySet()) {
                couplingMatrix.put(e.getKey(), (double) e.getValue() / total);
            }
        }
        
        totalRelations = total;
        
        // Débogage : afficher quelques couplages
        System.out.println("   🔗 Couplages calculés:");
        int count = 0;
        for (Map.Entry<String, Double> entry : couplingMatrix.entrySet()) {
            System.out.println("      " + entry.getKey() + " = " + String.format("%.4f", entry.getValue()));
            count++;
            if (count >= 5) break;
        }
    }

    // === MAIN DE TEST ===

    public static void main(String[] args) {
        // Test du calculateur de couplage
        SpoonCallGraphAnalyzer analyzer = new SpoonCallGraphAnalyzer(
            "C:\\Users\\Jihen\\eclipse-workspace\\ProjectTest2"
        );
        analyzer.analyze();

        SpoonCouplingCalculator calc = new SpoonCouplingCalculator(analyzer);

        System.out.println("\n========================================");
        System.out.println("     TEST DU COUPLAGE");
        System.out.println("========================================\n");

        // Tester quelques couplages
        Set<String> classes = calc.getAllClasses();
        List<String> classList = new ArrayList<>(classes);
        
        if (classList.size() >= 2) {
            for (int i = 0; i < Math.min(3, classList.size()); i++) {
                for (int j = i + 1; j < Math.min(3, classList.size()); j++) {
                    String cls1 = classList.get(i);
                    String cls2 = classList.get(j);
                    double coupling = calc.getCoupling(cls1, cls2);
                    System.out.println("Couplage(" + cls1 + ", " + cls2 + ") = " + 
                                     String.format("%.4f", coupling));
                }
            }
        }
    }
}