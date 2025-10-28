package partie3_tp2_partie1.graph;

public class CouplingRelation {
    private final String sourceClass;
    private final String targetClass;
    private final double couplingScore;

    public CouplingRelation(String sourceClass, String targetClass, double couplingScore) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.couplingScore = couplingScore;
    }

    public String getSourceClass() {
        return sourceClass;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public double getCouplingScore() {
        return couplingScore;
    }
}
