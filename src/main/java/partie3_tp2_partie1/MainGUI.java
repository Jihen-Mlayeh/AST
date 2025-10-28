package partie3_tp2_partie1;

import partie2.CallGraphAnalyzer;
import partie3_tp2_partie1.graph.ClassCouplingData;
import partie3_tp2_partie1.graph.GraphVisualizer;

import java.util.*;

public class MainGUI {
    public static void main(String[] args) throws Exception {
        // 1️⃣ Analyser le projet test
        CallGraphAnalyzer analyzer = new CallGraphAnalyzer("C:\\Users\\Jihen\\eclipse-workspace\\ProjectTest2");
        analyzer.analyze();

        // 2️⃣ Calculer le couplage entre les classes
        CouplingCalculator calculator = new CouplingCalculator(analyzer);

        // 3️⃣ Préparer les classes et la matrice de couplage
        List<String> allClasses = new ArrayList<>(calculator.getAllClasses());
        Map<String, Map<String, Integer>> couplingMatrix = new HashMap<>();

        for (String clsA : allClasses) {
            Map<String, Integer> row = new HashMap<>();
            for (String clsB : allClasses) {
                if (!clsA.equals(clsB)) {
                    double score = calculator.getCoupling(clsA, clsB);
                    if (score > 0) {
                        // Convertir le score en int pour la matrice (nombre de relations)
                        row.put(clsB, (int) (score * calculator.getTotalRelations()));
                    }
                }
            }
            couplingMatrix.put(clsA, row);
        }

        // 4️⃣ Créer l'objet ClassCouplingData
        ClassCouplingData data = new ClassCouplingData(allClasses, couplingMatrix);

        // 5️⃣ Afficher le graphe via l'interface graphique
        GraphVisualizer.show(data);
    }
}
