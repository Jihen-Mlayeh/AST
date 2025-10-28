package partie3_tp2_partie1;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import partie2.CallGraphAnalyzer;

public class MainCouplingAnalysis {
    public static void main(String[] args) throws IOException {

        CallGraphAnalyzer analyzer = new CallGraphAnalyzer("C:\\Users\\Jihen\\eclipse-workspace\\ProjectTest2");
        analyzer.analyze();

        // Q1
        CouplingCalculator calculator = new CouplingCalculator(analyzer);
       
        
        // Q2
        CouplingGraphGenerator graphGen = new CouplingGraphGenerator(
                calculator.getCouplingMatrix().entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> (int) (e.getValue() * calculator.getTotalRelations())
                        )),
                calculator.getAllClasses(),
                calculator.getTotalRelations()
        );

        System.out.println("\nQ2) Graphe de couplage :");
        graphGen.generateCouplingGraph();
    }
}
