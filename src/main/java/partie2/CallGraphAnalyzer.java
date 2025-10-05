package partie2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import fr.montpellier.m2gl.tp_ast.MethodDeclarationVisitor;
import fr.montpellier.m2gl.tp_ast.MethodInvocationVisitor;
import fr.montpellier.m2gl.tp_ast.TypeDeclarationVisitor;

public class CallGraphAnalyzer {
    
    private String projectPath;
    private String projectSourcePath;
    
    // Structure du graphe d'appel
    // Key: "ClassName.methodName" -> Value: Liste des méthodes appelées
    private Map<String, Set<String>> callGraph = new LinkedHashMap<>();
    
    // Informations sur les méthodes
    private Map<String, MethodInfo> methodInfoMap = new LinkedHashMap<>();
    
    public CallGraphAnalyzer(String projectPath) {
        this.projectPath = projectPath;
        this.projectSourcePath = projectPath + "/src";
    }
    
    public void analyze() throws IOException {
        List<File> javaFiles = listJavaFiles(new File(projectSourcePath));
        
        System.out.println("=== ANALYSE DU GRAPHE D'APPEL ===\n");
        System.out.println("Fichiers Java trouvés : " + javaFiles.size() + "\n");
        
        // Première passe : collecter toutes les méthodes
        for (File file : javaFiles) {
            collectMethods(file);
        }
        
        // Deuxième passe : construire le graphe d'appel
        for (File file : javaFiles) {
            analyzeMethodCalls(file);
        }
        
        // Afficher le graphe
        printCallGraph();
    }
    
    private void collectMethods(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()), "UTF-8");
        CompilationUnit cu = parse(content.toCharArray());
        
        TypeDeclarationVisitor typeVisitor = new TypeDeclarationVisitor();
        cu.accept(typeVisitor);
        
        for (TypeDeclaration type : typeVisitor.getTypes()) {
            String className = type.getName().toString();
            String packageName = cu.getPackage() != null ? 
                cu.getPackage().getName().toString() : "default";
            
            MethodDeclarationVisitor methodVisitor = new MethodDeclarationVisitor();
            type.accept(methodVisitor);
            
            for (MethodDeclaration method : methodVisitor.getMethods()) {
                String methodKey = className + "." + method.getName().toString();
                
                MethodInfo info = new MethodInfo();
                info.className = className;
                info.methodName = method.getName().toString();
                info.packageName = packageName;
                info.returnType = method.getReturnType2() != null ? 
                    method.getReturnType2().toString() : "void";
                info.parameters = method.parameters().size();
                
                methodInfoMap.put(methodKey, info);
                callGraph.put(methodKey, new HashSet<>());
            }
        }
    }
    
    private void analyzeMethodCalls(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()), "UTF-8");
        CompilationUnit cu = parse(content.toCharArray());
        
        TypeDeclarationVisitor typeVisitor = new TypeDeclarationVisitor();
        cu.accept(typeVisitor);
        
        for (TypeDeclaration type : typeVisitor.getTypes()) {
            String className = type.getName().toString();
            
            MethodDeclarationVisitor methodVisitor = new MethodDeclarationVisitor();
            type.accept(methodVisitor);
            
            for (MethodDeclaration method : methodVisitor.getMethods()) {
                String callerKey = className + "." + method.getName().toString();
                
                MethodInvocationVisitor invocationVisitor = new MethodInvocationVisitor();
                method.accept(invocationVisitor);
                
                for (MethodInvocation invocation : invocationVisitor.getMethods()) {
                    String calledMethod = invocation.getName().toString();
                    
                    // Essayer de déterminer la classe de l'objet receveur
                    String receiverType = "Unknown";
                    if (invocation.getExpression() != null) {
                        ITypeBinding typeBinding = invocation.getExpression().resolveTypeBinding();
                        if (typeBinding != null) {
                            receiverType = typeBinding.getName();
                        } else {
                            // Fallback : utiliser le nom de l'expression
                            receiverType = invocation.getExpression().toString();
                        }
                    }
                    
                    String calledKey = receiverType + "." + calledMethod;
                    
                    // Ajouter l'appel au graphe
                    callGraph.get(callerKey).add(calledKey);
                }
            }
        }
    }
    
    private void printCallGraph() {
        System.out.println("========================================");
        System.out.println("     GRAPHE D'APPEL DE MÉTHODES");
        System.out.println("========================================\n");
        
        for (Map.Entry<String, Set<String>> entry : callGraph.entrySet()) {
            String caller = entry.getKey();
            Set<String> callees = entry.getValue();
            
            System.out.println("Méthode : " + caller);
            
            if (callees.isEmpty()) {
                System.out.println("  └─ (aucun appel)");
            } else {
                System.out.println("  Appelle :");
                for (String callee : callees) {
                    System.out.println("    └─ " + callee);
                }
            }
            System.out.println();
        }
        
        // Statistiques
        System.out.println("========================================");
        System.out.println("     STATISTIQUES DU GRAPHE");
        System.out.println("========================================\n");
        
        int totalMethods = callGraph.size();
        int totalCalls = callGraph.values().stream()
            .mapToInt(Set::size)
            .sum();
        
        System.out.println("Nombre total de méthodes : " + totalMethods);
        System.out.println("Nombre total d'appels : " + totalCalls);
        
        // Méthodes les plus appelantes
        System.out.println("\nTop 5 des méthodes qui appellent le plus :");
        callGraph.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
            .limit(5)
            .forEach(e -> System.out.println("  " + e.getKey() + " : " + e.getValue().size() + " appels"));
    }
    
    public Map<String, Set<String>> getCallGraph() {
        return callGraph;
    }
    
    public Map<String, MethodInfo> getMethodInfoMap() {
        return methodInfoMap;
    }
    
    private List<File> listJavaFiles(File folder) {
        List<File> javaFiles = new ArrayList<>();
        if (folder.listFiles() == null) return javaFiles;
        
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                javaFiles.addAll(listJavaFiles(file));
            } else if (file.getName().endsWith(".java")) {
                javaFiles.add(file);
            }
        }
        return javaFiles;
    }
    
    private CompilationUnit parse(char[] source) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(source);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        
        Map<String, String> options = JavaCore.getOptions();
        parser.setCompilerOptions(options);
        parser.setUnitName("");
        
        String[] sources = { projectSourcePath };
        String[] classpath = {};
        
        parser.setEnvironment(classpath, sources, new String[] { "UTF-8" }, true);
        
        return (CompilationUnit) parser.createAST(null);
    }
    
    // Classe interne pour stocker les infos de méthode
    public static class MethodInfo {
        public String className;
        public String methodName;
        public String packageName;
        public String returnType;
        public int parameters;
    }
    
    public static void main(String[] args) throws IOException {
        String projectPath = "C:\\Users\\Jihen\\eclipse-workspace\\ProjetTest";
        
        CallGraphAnalyzer analyzer = new CallGraphAnalyzer(projectPath);
        analyzer.analyze();
    }
}