package partie3_tp2_partie1.spoon;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.reflect.code.CtInvocation;

import java.io.File;
import java.util.*;

/**
 * Analyseur de graphe d'appel utilisant Spoon
 * Équivalent de CallGraphAnalyzer mais avec Spoon
 */
public class SpoonCallGraphAnalyzer {

    private String projectPath;
    private CtModel model;
    
    // Graphe d'appel : Map<"ClassName.methodName", Set<"CalledClass.calledMethod">>
    private Map<String, Set<String>> callGraph = new HashMap<>();
    
    // Informations sur les méthodes
    private Map<String, MethodInfo> methodInfoMap = new HashMap<>();
    
    // Toutes les classes du projet
    private Set<String> allClasses = new HashSet<>();

    public SpoonCallGraphAnalyzer(String projectPath) {
        this.projectPath = projectPath;
    }

    /**
     * Lance l'analyse du projet avec Spoon
     */
    public void analyze() {
        System.out.println("\n=== ANALYSE AVEC SPOON ===\n");
        System.out.println("📁 Analyse du projet : " + projectPath);

        try {
            // 1️⃣ Configuration de Spoon
            Launcher launcher = new Launcher();
            
            // Vérifier si le chemin existe
            File projectDir = new File(projectPath);
            if (!projectDir.exists()) {
                System.err.println("❌ Le chemin n'existe pas : " + projectPath);
                return;
            }
            
            launcher.addInputResource(projectPath);
            
            // Configuration de l'environnement Spoon
            launcher.getEnvironment().setNoClasspath(true); // Ne pas résoudre le classpath
            launcher.getEnvironment().setComplianceLevel(11); // Java 11+
            launcher.getEnvironment().setAutoImports(true);
            launcher.getEnvironment().setCommentEnabled(false);
            launcher.getEnvironment().setShouldCompile(false); // Ne pas compiler
            launcher.getEnvironment().setIgnoreDuplicateDeclarations(true);
            
            // Ignorer les fichiers module-info.java qui causent des problèmes
            launcher.getEnvironment().setIgnoreSyntaxErrors(true);
            
            // 2️⃣ Construction du modèle
            System.out.println("⏳ Construction du modèle Spoon...");
            model = launcher.buildModel();
            System.out.println("✅ Modèle construit !");

            // 3️⃣ Extraction des classes
            List<CtClass<?>> classes = model.getElements(new TypeFilter<>(CtClass.class));
            System.out.println("📊 Nombre de classes trouvées : " + classes.size());

            if (classes.isEmpty()) {
                System.out.println("⚠️ Aucune classe trouvée. Vérifiez le chemin du projet.");
                return;
            }

            // 4️⃣ Analyse de chaque classe
            for (CtClass<?> ctClass : classes) {
                try {
                    String className = ctClass.getSimpleName();
                    allClasses.add(className);
                    
                    // Analyser toutes les méthodes de la classe
                    Set<CtMethod<?>> methods = ctClass.getMethods();
                    for (CtMethod<?> method : methods) {
                        analyzeMethod(className, method);
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Erreur lors de l'analyse de la classe : " + e.getMessage());
                }
            }

            // 5️⃣ Afficher les résultats
            displayResults();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'analyse Spoon : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Analyse une méthode et extrait les appels qu'elle fait
     */
    private void analyzeMethod(String className, CtMethod<?> method) {
        try {
            String methodName = method.getSimpleName();
            String fullMethodName = className + "." + methodName;

            // Initialiser l'entrée dans le graphe
            callGraph.putIfAbsent(fullMethodName, new HashSet<>());

            // Stocker les infos de la méthode
            MethodInfo info = new MethodInfo();
            info.className = className;
            info.methodName = methodName;
            info.parameterCount = method.getParameters().size();
            methodInfoMap.put(fullMethodName, info);

            // Trouver tous les appels de méthode (invocations)
            List<CtInvocation<?>> invocations = method.getElements(new TypeFilter<>(CtInvocation.class));
            
            for (CtInvocation<?> invocation : invocations) {
                try {
                    // Récupérer le nom de la méthode appelée
                    String calledMethodName = invocation.getExecutable().getSimpleName();
                    
                    // Récupérer la classe de la méthode appelée
                    String calledClassName = null;
                    if (invocation.getExecutable().getDeclaringType() != null) {
                        calledClassName = invocation.getExecutable().getDeclaringType().getSimpleName();
                    }

                    // Ignorer les appels aux classes Java standard
                    if (calledClassName != null && !isJavaStandardClass(calledClassName)) {
                        String calledFullName = calledClassName + "." + calledMethodName;
                        callGraph.get(fullMethodName).add(calledFullName);
                    }
                } catch (Exception e) {
                    // Ignorer les erreurs de résolution
                }
            }
        } catch (Exception e) {
            // Ignorer les erreurs sur les méthodes problématiques
        }
    }

    /**
     * Vérifie si c'est une classe Java standard
     */
    private boolean isJavaStandardClass(String className) {
        return className.startsWith("java.") || 
               className.startsWith("javax.") ||
               className.startsWith("sun.") ||
               className.equals("System") ||
               className.equals("PrintStream") ||
               className.equals("String") ||
               className.equals("Object") ||
               className.equals("Math") ||
               className.equals("Integer") ||
               className.equals("Double");
    }

    /**
     * Affiche les résultats de l'analyse
     */
    private void displayResults() {
        System.out.println("\n========================================");
        System.out.println("     GRAPHE D'APPEL (Spoon)");
        System.out.println("========================================\n");

        // Trier les méthodes par nom de classe
        List<String> sortedMethods = new ArrayList<>(callGraph.keySet());
        Collections.sort(sortedMethods);

        for (String method : sortedMethods) {
            System.out.println("Méthode : " + method);
            Set<String> called = callGraph.get(method);
            
            if (called.isEmpty()) {
                System.out.println("  └─ (aucun appel)");
            } else {
                System.out.println("  Appelle :");
                for (String calledMethod : called) {
                    System.out.println("    └─ " + calledMethod);
                }
            }
            System.out.println();
        }

        // Statistiques
        System.out.println("========================================");
        System.out.println("     STATISTIQUES");
        System.out.println("========================================\n");
        
        System.out.println("Nombre de classes : " + allClasses.size());
        System.out.println("Nombre de méthodes : " + callGraph.size());
        
        int totalCalls = callGraph.values().stream().mapToInt(Set::size).sum();
        System.out.println("Nombre total d'appels : " + totalCalls);

        // Top méthodes qui appellent le plus
        System.out.println("\nTop 5 des méthodes qui appellent le plus :");
        callGraph.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
            .limit(5)
            .forEach(e -> System.out.println("  " + e.getKey() + " : " + e.getValue().size() + " appels"));
    }

    // === GETTERS ===

    public Map<String, Set<String>> getCallGraph() {
        return callGraph;
    }

    public Map<String, MethodInfo> getMethodInfoMap() {
        return methodInfoMap;
    }

    public Set<String> getAllClasses() {
        return allClasses;
    }

    public CtModel getModel() {
        return model;
    }

    // === CLASSE INTERNE ===

    public static class MethodInfo {
        public String className;
        public String methodName;
        public int parameterCount;

        @Override
        public String toString() {
            return className + "." + methodName + "(" + parameterCount + " params)";
        }
    }

    // === MAIN DE TEST ===

    public static void main(String[] args) {
        SpoonCallGraphAnalyzer analyzer = new SpoonCallGraphAnalyzer(
            "C:\\Users\\Jihen\\eclipse-workspace\\ProjectTest2"
        );
        analyzer.analyze();
    }
}