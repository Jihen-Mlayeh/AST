package partie2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import fr.montpellier.m2gl.tp_ast.MethodDeclarationVisitor;
import fr.montpellier.m2gl.tp_ast.TypeDeclarationVisitor;

public class StatisticsAnalyzer {
    
    private String projectPath;
    private String projectSourcePath;
    private List<ClassMetrics> classMetricsList = new ArrayList<>();
    
    public StatisticsAnalyzer(String projectPath) {
        this.projectPath = projectPath;
        this.projectSourcePath = projectPath + "/src";
    }
    
    public void analyze() throws IOException {
        List<File> javaFiles = listJavaFiles(new File(projectSourcePath));
        
        System.out.println("=== ANALYSE STATIQUE DU PROJET ===\n");
        System.out.println("Fichiers trouvés : " + javaFiles.size() + "\n");
        
        // Analyser chaque fichier
        for (File file : javaFiles) {
            analyzeFile(file);
        }
        
        // Afficher les statistiques
        printStatistics();
    }
    private void analyzeFile(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()), "UTF-8");
        CompilationUnit cu = parse(content.toCharArray());
        
        TypeDeclarationVisitor typeVisitor = new TypeDeclarationVisitor();
        cu.accept(typeVisitor);
        
        for (TypeDeclaration type : typeVisitor.getTypes()) {
            ClassMetrics metrics = new ClassMetrics();
            metrics.className = type.getName().toString();
            metrics.packageName = cu.getPackage() != null ? 
                cu.getPackage().getName().toString() : "default";
            
            // Compter méthodes
            MethodDeclarationVisitor methodVisitor = new MethodDeclarationVisitor();
            type.accept(methodVisitor);
            metrics.methodCount = methodVisitor.getMethods().size();
            metrics.methods = methodVisitor.getMethods();
            
            // Compter attributs
            metrics.fieldCount = type.getFields().length;
            
            // Compter lignes de code - CORRECTION ICI
            try {
                int startLine = cu.getLineNumber(type.getStartPosition());
                int endLine = cu.getLineNumber(type.getStartPosition() + type.getLength() - 1);
                
                // Vérifier que les valeurs sont valides
                if (startLine > 0 && endLine > 0 && endLine >= startLine) {
                    metrics.linesOfCode = endLine - startLine + 1;
                } else {
                    // Méthode alternative : compter les lignes dans le contenu
                    String typeContent = content.substring(
                        type.getStartPosition(), 
                        type.getStartPosition() + type.getLength()
                    );
                    metrics.linesOfCode = typeContent.split("\r\n|\r|\n").length;
                }
            } catch (Exception e) {
                // En cas d'erreur, utiliser une méthode alternative
                String typeContent = content.substring(
                    type.getStartPosition(), 
                    type.getStartPosition() + type.getLength()
                );
                metrics.linesOfCode = typeContent.split("\r\n|\r|\n").length;
            }
            
            // Lignes par méthode
            for (MethodDeclaration method : methodVisitor.getMethods()) {
                try {
                    int methodStart = cu.getLineNumber(method.getStartPosition());
                    int methodEnd = cu.getLineNumber(method.getStartPosition() + method.getLength() - 1);
                    
                    if (methodStart > 0 && methodEnd > 0 && methodEnd >= methodStart) {
                        metrics.methodLines.add(methodEnd - methodStart + 1);
                    } else {
                        // Méthode alternative
                        String methodContent = content.substring(
                            method.getStartPosition(),
                            method.getStartPosition() + method.getLength()
                        );
                        metrics.methodLines.add(methodContent.split("\r\n|\r|\n").length);
                    }
                } catch (Exception e) {
                    metrics.methodLines.add(1); // Valeur par défaut
                }
                
                metrics.methodParameters.add(method.parameters().size());
            }
            
            classMetricsList.add(metrics);
        }
    }
    
    private void printStatistics() {
        System.out.println("========================================");
        System.out.println("     STATISTIQUES DU PROJET");
        System.out.println("========================================\n");
        
        // Q1: Nombre de classes
        System.out.println("1. Nombre de classes : " + classMetricsList.size());
        
        // Q2: Nombre total de lignes de code
        int totalLines = classMetricsList.stream()
            .mapToInt(c -> c.linesOfCode)
            .sum();
        System.out.println("2. Nombre total de lignes : " + totalLines);
        
        // Q3: Nombre total de méthodes
        int totalMethods = classMetricsList.stream()
            .mapToInt(c -> c.methodCount)
            .sum();
        System.out.println("3. Nombre total de méthodes : " + totalMethods);
        
        // Q4: Nombre de packages
        long packageCount = classMetricsList.stream()
            .map(c -> c.packageName)
            .distinct()
            .count();
        System.out.println("4. Nombre de packages : " + packageCount);
        
        // Q5: Moyenne méthodes par classe
        double avgMethods = classMetricsList.stream()
            .mapToInt(c -> c.methodCount)
            .average()
            .orElse(0.0);
        System.out.printf("5. Moyenne méthodes/classe : %.2f\n", avgMethods);
        
        // Q6: Moyenne lignes par méthode
        List<Integer> allMethodLines = classMetricsList.stream()
            .flatMap(c -> c.methodLines.stream())
            .collect(Collectors.toList());
        double avgLinesPerMethod = allMethodLines.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
        System.out.printf("6. Moyenne lignes/méthode : %.2f\n", avgLinesPerMethod);
        
        // Q7: Moyenne attributs par classe
        double avgFields = classMetricsList.stream()
            .mapToInt(c -> c.fieldCount)
            .average()
            .orElse(0.0);
        System.out.printf("7. Moyenne attributs/classe : %.2f\n", avgFields);
        
        // Q8: Top 10% classes avec plus de méthodes
        System.out.println("\n8. Top 10% classes (plus de méthodes) :");
        List<ClassMetrics> topMethods = classMetricsList.stream()
            .sorted((a, b) -> Integer.compare(b.methodCount, a.methodCount))
            .limit(Math.max(1, classMetricsList.size() / 10))
            .collect(Collectors.toList());
        for (ClassMetrics cm : topMethods) {
            System.out.printf("   - %s : %d méthodes\n", cm.className, cm.methodCount);
        }
        
        // Q9: Top 10% classes avec plus d'attributs
        System.out.println("\n9. Top 10% classes (plus d'attributs) :");
        List<ClassMetrics> topFields = classMetricsList.stream()
            .sorted((a, b) -> Integer.compare(b.fieldCount, a.fieldCount))
            .limit(Math.max(1, classMetricsList.size() / 10))
            .collect(Collectors.toList());
        for (ClassMetrics cm : topFields) {
            System.out.printf("   - %s : %d attributs\n", cm.className, cm.fieldCount);
        }
        
        // Q10: Classes dans les deux catégories
        System.out.println("\n10. Classes dans les deux top 10% :");
        Set<String> topMethodNames = topMethods.stream()
            .map(c -> c.className)
            .collect(Collectors.toSet());
        Set<String> topFieldNames = topFields.stream()
            .map(c -> c.className)
            .collect(Collectors.toSet());
        topMethodNames.retainAll(topFieldNames);
        if (topMethodNames.isEmpty()) {
            System.out.println("   Aucune classe dans les deux catégories");
        } else {
            topMethodNames.forEach(name -> System.out.println("   - " + name));
        }
        
        // Q11: Classes avec plus de X méthodes (X=5 par défaut)
        int threshold = 5;
        System.out.println("\n11. Classes avec plus de " + threshold + " méthodes :");
        classMetricsList.stream()
            .filter(c -> c.methodCount > threshold)
            .forEach(c -> System.out.printf("   - %s : %d méthodes\n", 
                c.className, c.methodCount));
        
        // Q12: Top 10% méthodes avec plus de lignes (par classe)
        System.out.println("\n12. Top 10% méthodes (plus de lignes) par classe :");
        for (ClassMetrics cm : classMetricsList) {
            if (cm.methods.isEmpty()) continue;
            
            List<MethodDeclaration> sortedMethods = cm.methods.stream()
                .sorted((a, b) -> {
                    int linesA = cm.methodLines.get(cm.methods.indexOf(a));
                    int linesB = cm.methodLines.get(cm.methods.indexOf(b));
                    return Integer.compare(linesB, linesA);
                })
                .limit(Math.max(1, cm.methods.size() / 10))
                .collect(Collectors.toList());
            
            System.out.println("   Classe " + cm.className + " :");
            for (MethodDeclaration method : sortedMethods) {
                int index = cm.methods.indexOf(method);
                System.out.printf("      - %s() : %d lignes\n", 
                    method.getName(), cm.methodLines.get(index));
            }
        }
        
        // Q13: Nombre max de paramètres
        int maxParams = classMetricsList.stream()
            .flatMap(c -> c.methodParameters.stream())
            .max(Integer::compare)
            .orElse(0);
        System.out.println("\n13. Nombre max de paramètres : " + maxParams);
        
        System.out.println("\n========================================\n");
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
        parser.setResolveBindings(false);
        
        Map<String, String> options = JavaCore.getOptions();
        parser.setCompilerOptions(options);
        
        return (CompilationUnit) parser.createAST(null);
    }

public List<ClassMetrics> getClassMetricsList() {
    return classMetricsList;
}
    
    public static void main(String[] args) throws IOException {
        String projectPath = "C:\\Users\\Jihen\\eclipse-workspace\\ProjetTest";
        
        StatisticsAnalyzer analyzer = new StatisticsAnalyzer(projectPath);
        analyzer.analyze();
    }
}