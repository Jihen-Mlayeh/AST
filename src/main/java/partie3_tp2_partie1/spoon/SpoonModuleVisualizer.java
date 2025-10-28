package partie3_tp2_partie1.spoon;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Interface graphique pour visualiser les modules identifiés par Spoon
 */
public class SpoonModuleVisualizer extends JFrame {

    private final List<List<String>> modules;

    public SpoonModuleVisualizer(List<List<String>> modules) {
        this.modules = modules;

        setTitle("Visualisation des Modules (Spoon)");
        setSize(650, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Titre avec logo Spoon
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        JLabel titleLabel = new JLabel("🥄 Modules Identifiés avec Spoon", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);

        // Panel principal pour les modules
        JPanel modulePanel = new JPanel();
        modulePanel.setLayout(new BoxLayout(modulePanel, BoxLayout.Y_AXIS));
        modulePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        modulePanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(modulePanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

        if (modules.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucun module identifié", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            emptyLabel.setForeground(new Color(231, 76, 60));
            modulePanel.add(emptyLabel);
        } else {
            // Couleurs pour différencier les modules
            Color[] moduleColors = {
                new Color(52, 152, 219),  // Bleu
                new Color(46, 204, 113),  // Vert
                new Color(155, 89, 182),  // Violet
                new Color(241, 196, 15),  // Jaune
                new Color(230, 126, 34),  // Orange
                new Color(26, 188, 156),  // Turquoise
                new Color(52, 73, 94),    // Gris bleuté
                new Color(192, 57, 43)    // Rouge
            };
            
            int i = 1;
            for (List<String> module : modules) {
                Color moduleColor = moduleColors[(i - 1) % moduleColors.length];
                
                // Panel pour chaque module
                JPanel singleModulePanel = new JPanel();
                singleModulePanel.setLayout(new BorderLayout());
                singleModulePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(moduleColor, 3),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                singleModulePanel.setBackground(new Color(236, 240, 241));
                singleModulePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

                // En-tête du module
                JPanel moduleHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
                moduleHeader.setBackground(moduleColor);
                
                JLabel moduleTitle = new JLabel("📦 Module " + i);
                moduleTitle.setFont(new Font("Arial", Font.BOLD, 15));
                moduleTitle.setForeground(Color.WHITE);
                
                JLabel classCount = new JLabel(" (" + module.size() + " classe" + 
                                             (module.size() > 1 ? "s" : "") + ")");
                classCount.setFont(new Font("Arial", Font.PLAIN, 13));
                classCount.setForeground(Color.WHITE);
                
                moduleHeader.add(moduleTitle);
                moduleHeader.add(classCount);
                singleModulePanel.add(moduleHeader, BorderLayout.NORTH);

                // Classes du module
                JTextArea area = new JTextArea();
                area.setEditable(false);
                area.setFont(new Font("Consolas", Font.PLAIN, 13));
                area.setLineWrap(true);
                area.setWrapStyleWord(true);
                area.setBackground(new Color(236, 240, 241));
                area.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                StringBuilder content = new StringBuilder();
                for (String className : module) {
                    content.append("  ✓ ").append(className).append("\n");
                }
                area.setText(content.toString());
                
                singleModulePanel.add(area, BorderLayout.CENTER);
                
                modulePanel.add(singleModulePanel);
                modulePanel.add(Box.createRigidArea(new Dimension(0, 12)));
                
                i++;
            }
        }

        add(scrollPane, BorderLayout.CENTER);

        // Statistiques en bas
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        statsPanel.setBackground(new Color(236, 240, 241));
        
        int totalClasses = modules.stream().mapToInt(List::size).sum();
        
        JLabel statsLabel = new JLabel(
            String.format("📊 %d module%s  •  %d classe%s analysée%s avec Spoon", 
                         modules.size(), 
                         modules.size() > 1 ? "s" : "",
                         totalClasses,
                         totalClasses > 1 ? "s" : "",
                         totalClasses > 1 ? "s" : "")
        );
        statsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        statsLabel.setForeground(new Color(52, 73, 94));
        statsPanel.add(statsLabel);
        
        add(statsPanel, BorderLayout.SOUTH);
    }

    public static void showModules(List<List<String>> modules) {
        SwingUtilities.invokeLater(() -> {
            SpoonModuleVisualizer frame = new SpoonModuleVisualizer(modules);
            frame.setVisible(true);
        });
    }

    /**
     * Point d'entrée principal
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  ANALYSE COMPLÈTE AVEC SPOON");
        System.out.println("========================================\n");

        String projectPath = "C:\\Users\\Jihen\\eclipse-workspace\\ProjectTest2";
        
        // 1️⃣ Analyse du graphe d'appel avec Spoon
        SpoonCallGraphAnalyzer analyzer = new SpoonCallGraphAnalyzer(projectPath);
        analyzer.analyze();
        
        // 2️⃣ Calcul du couplage
        SpoonCouplingCalculator calc = new SpoonCouplingCalculator(analyzer);

        // 3️⃣ Identification des modules
        double CP = 0.1;
        SpoonModuleIdentifier identifier = new SpoonModuleIdentifier(calc, CP);
        List<List<String>> modules = identifier.identifyModules();

        // 4️⃣ Afficher l'interface graphique
        System.out.println("\n🖥️ Ouverture de l'interface graphique Spoon...\n");
        SpoonModuleVisualizer.showModules(modules);
    }
}