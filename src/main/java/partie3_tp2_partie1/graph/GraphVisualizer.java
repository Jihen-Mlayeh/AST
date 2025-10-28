package partie3_tp2_partie1.graph;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;



public class GraphVisualizer extends JFrame {

    private final ClassCouplingData data;

    public GraphVisualizer(ClassCouplingData data) {
        this.data = data;
        setTitle("Visualisation du Graphe de Couplage");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new GraphPanel());
    }

    class GraphPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Position des classes en cercle
            List<String> classes = new ArrayList<>(data.getAllClasses());
            int n = classes.size();
            int radius = 200;
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            Map<String, Point2D> positions = new HashMap<>();
            for (int i = 0; i < n; i++) {
                double angle = 2 * Math.PI * i / n;
                double x = centerX + radius * Math.cos(angle);
                double y = centerY + radius * Math.sin(angle);
                positions.put(classes.get(i), new Point2D.Double(x, y));
            }

            // Dessiner les arêtes
            for (CouplingRelation r : data.getRelations()) {
                Point2D start = positions.get(r.getSourceClass());
                Point2D end = positions.get(r.getTargetClass());
                g2.setStroke(new BasicStroke((float)(1 + r.getCouplingScore() * 5)));
                g2.setColor(Color.BLUE);
                g2.drawLine((int)start.getX(), (int)start.getY(), (int)end.getX(), (int)end.getY());
            }

            // Dessiner les nœuds
            for (String cls : classes) {
                Point2D p = positions.get(cls);
                g2.setColor(Color.RED);
                int size = 30;
                g2.fillOval((int)p.getX() - size/2, (int)p.getY() - size/2, size, size);
                g2.setColor(Color.BLACK);
                g2.drawString(cls, (int)p.getX() - size/2, (int)p.getY() - size/2 - 5);
            }
        }
    }

    public static void show(ClassCouplingData data) {
        SwingUtilities.invokeLater(() -> {
            GraphVisualizer frame = new GraphVisualizer(data);
            frame.setVisible(true);
        });
    }
}
