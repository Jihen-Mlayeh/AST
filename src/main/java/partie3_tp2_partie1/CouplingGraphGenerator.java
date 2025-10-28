package partie3_tp2_partie1;

import java.util.Map;
import java.util.Set;

public class CouplingGraphGenerator {

    private Map<String, Integer> couplingMatrixInt;
    private Set<String> allClasses;
    private int totalRelations;

    public CouplingGraphGenerator(Map<String, Integer> couplingMatrixInt, Set<String> allClasses, int totalRelations) {
        this.couplingMatrixInt = couplingMatrixInt;
        this.allClasses = allClasses;
        this.totalRelations = totalRelations;
    }

    public void generateCouplingGraph() {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph ClassCoupling {\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  node [shape=box, style=filled, fillcolor=lightblue];\n\n");

        for (String cls : allClasses) {
            dot.append("  \"" + cls + "\";\n");
        }

        for (Map.Entry<String, Integer> entry : couplingMatrixInt.entrySet()) {
            String[] classes = entry.getKey().split("->");
            String classA = classes[0];
            String classB = classes[1];
            int relations = entry.getValue();
            double coupling = (double) relations / totalRelations;
            double penwidth = 1 + coupling * 10;
            dot.append(String.format("  \"%s\" -> \"%s\" [label=\"%d (%.2f%%)\", penwidth=%.2f];\n",
                    classA, classB, relations, coupling * 100, penwidth));
        }

        dot.append("}\n");
        System.out.println(dot.toString());
    }
}
