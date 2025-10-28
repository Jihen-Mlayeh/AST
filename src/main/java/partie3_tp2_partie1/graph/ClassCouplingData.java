package partie3_tp2_partie1.graph;

import java.util.*;

public class ClassCouplingData {

    private final List<String> allClasses;
    private final List<CouplingRelation> relations;

    public ClassCouplingData() {
        this.allClasses = new ArrayList<>();
        this.relations = new ArrayList<>();
    }

    // ✅ Nouveau constructeur qui prend liste de classes et matrice
    public ClassCouplingData(List<String> allClasses, Map<String, Map<String, Integer>> matrix) {
        this.allClasses = new ArrayList<>(allClasses);
        this.relations = new ArrayList<>();
        for (String src : matrix.keySet()) {
            Map<String, Integer> row = matrix.get(src);
            for (String tgt : row.keySet()) {
                int val = row.get(tgt);
                if (val > 0) {
                    relations.add(new CouplingRelation(src, tgt, val));
                }
            }
        }
    }

    public List<String> getAllClasses() {
        return allClasses;
    }

    public List<CouplingRelation> getRelations() {
        return relations;
    }

    public int getTotalRelations() {
        return relations.size();
    }

    // Méthode pour ajouter manuellement une relation si besoin
    public void addRelation(String source, String target, double score) {
        relations.add(new CouplingRelation(source, target, score));
        if (!allClasses.contains(source)) allClasses.add(source);
        if (!allClasses.contains(target)) allClasses.add(target);
    }
}
