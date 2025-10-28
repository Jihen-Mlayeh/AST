package partie3_tp2_partie1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import partie2.CallGraphAnalyzer;

public class CouplingCalculator {

    private Map<String, Set<String>> callGraph;
    private Map<String, CallGraphAnalyzer.MethodInfo> methodInfoMap;
    private int totalRelations;
    private Map<String, Double> couplingMatrix = new HashMap<>();
    private Set<String> allClasses = new HashSet<>();

    public CouplingCalculator(CallGraphAnalyzer analyzer) {
        this.callGraph = analyzer.getCallGraph();
        this.methodInfoMap = analyzer.getMethodInfoMap();
        
        // Débogage
        System.out.println("🔍 [CouplingCalculator] Initialisation...");
        System.out.println("   - callGraph size: " + callGraph.size());
        System.out.println("   - methodInfoMap size: " + methodInfoMap.size());
        
        extractAllClasses();
        System.out.println("   - Classes extraites: " + allClasses.size());
        
        computeCouplingMatrix();
        this.totalRelations = computeTotalRelations();
        
        System.out.println("   - Total relations: " + totalRelations);
        System.out.println("   - Matrice de couplage: " + couplingMatrix.size() + " entrées");
    }

    // Q1 : calculer le couplage entre 2 classes
    public double getCoupling(String classA, String classB) {
        String key1 = classA + "->" + classB;
        String key2 = classB + "->" + classA;
        double val = couplingMatrix.getOrDefault(key1, 0.0) + couplingMatrix.getOrDefault(key2, 0.0);
        return val;
    }

    // Total de relations inter-classes
    public int getTotalRelations() {
        return totalRelations;
    }

    // Renvoie la matrice de couplage (clé=classeA->classeB, valeur=couplage)
    public Map<String, Double> getCouplingMatrix() {
        return couplingMatrix;
    }

    // Renvoie toutes les classes
    public Set<String> getAllClasses() {
        return allClasses;
    }

    // Extraire toutes les classes - VERSION ROBUSTE
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
        
        // Méthode 2 : depuis callGraph (au cas où methodInfoMap serait incomplet)
        for (String caller : callGraph.keySet()) {
            if (caller != null && caller.contains(".")) {
                String callerClass = caller.substring(0, caller.lastIndexOf('.'));
                if (!callerClass.isEmpty() && !isJavaStandardClass(callerClass)) {
                    allClasses.add(callerClass);
                }
            }
            
            // Aussi les classes appelées
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
        
        System.out.println("   📋 Classes trouvées: " + allClasses);
    }

    // Vérifier si c'est une classe Java standard (à ignorer)
    private boolean isJavaStandardClass(String className) {
        return className.startsWith("java.") || 
               className.startsWith("javax.") ||
               className.startsWith("sun.") ||
               className.equals("PrintStream");
    }

    // Calcul du nombre total de relations inter-classes
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

    // Construire la matrice de couplage normalisée
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
            System.out.println("      " + entry.getKey() + " = " + entry.getValue());
            count++;
            if (count >= 5) break;
        }
    }
}