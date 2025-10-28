package partie3_tp2_partie1.module;

import partie3_tp2_partie1.CouplingCalculator;
import partie2.CallGraphAnalyzer;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class ModuleVisualizer extends JFrame {

    private final List<List<String>> modules;

    public ModuleVisualizer(List<List<String>> modules) {
        this.modules = modules;

        setTitle("Visualisation des Modules");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Titre
        JLabel titleLabel = new JLabel("Modules Identifiés", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Panel principal pour les modules
        JPanel modulePanel = new JPanel();
        modulePanel.setLayout(new BoxLayout(modulePanel, BoxLayout.Y_AXIS));
        modulePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(modulePanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        if (modules.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucun module identifié", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.RED);
            modulePanel.add(emptyLabel);
        } else {
            int i = 1;
            for (List<String> module : modules) {
                // Panel pour chaque module
                JPanel singleModulePanel = new JPanel();
                singleModulePanel.setLayout(new BorderLayout());
                singleModulePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                singleModulePanel.setBackground(new Color(240, 248, 255));
                singleModulePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

                // Titre du module
                JLabel moduleTitle = new JLabel("Module " + i);
                moduleTitle.setFont(new Font("Arial", Font.BOLD, 14));
                moduleTitle.setForeground(new Color(70, 130, 180));
                singleModulePanel.add(moduleTitle, BorderLayout.NORTH);

                // Classes du module
                JTextArea area = new JTextArea();
                area.setEditable(false);
                area.setFont(new Font("Monospaced", Font.PLAIN, 12));
                area.setLineWrap(true);
                area.setWrapStyleWord(true);
                area.setBackground(new Color(240, 248, 255));
                
                StringBuilder content = new StringBuilder();
                for (String className : module) {
                    content.append("  • ").append(className).append("\n");
                }
                area.setText(content.toString());
                
                singleModulePanel.add(area, BorderLayout.CENTER);
                
                modulePanel.add(singleModulePanel);
                modulePanel.add(Box.createRigidArea(new Dimension(0, 10)));
                
                i++;
            }
        }

        add(scrollPane, BorderLayout.CENTER);

        // Statistiques
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistiques"));
        statsPanel.setBackground(new Color(245, 245, 245));
        
        int totalClasses = modules.stream().mapToInt(List::size).sum();
        JLabel statsLabel = new JLabel(
            String.format("Nombre de modules : %d  |  Nombre total de classes : %d", 
                         modules.size(), totalClasses)
        );
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statsPanel.add(statsLabel);
        
        add(statsPanel, BorderLayout.SOUTH);
    }

    public static void showModules(List<List<String>> modules) {
        SwingUtilities.invokeLater(() -> {
            ModuleVisualizer frame = new ModuleVisualizer(modules);
            frame.setVisible(true);
        });
    }

    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("  DÉMARRAGE DE L'ANALYSE DES MODULES");
        System.out.println("========================================\n");

        // 1️⃣ Analyse du projet
        String projectPath = "C:\\Users\\Jihen\\eclipse-workspace\\ProjectTest2";
        System.out.println("📁 Chemin du projet : " + projectPath);
        
        CallGraphAnalyzer analyzer = new CallGraphAnalyzer(projectPath);
        
        System.out.println("\n⏳ Analyse du graphe d'appel en cours...");
        analyzer.analyze();
        System.out.println("✅ Analyse terminée !\n");
        
        // 2️⃣ Créer le calculateur de couplage
        System.out.println("🔗 Création du calculateur de couplage...");
        CouplingCalculator calc = new CouplingCalculator(analyzer);
        
        // DÉBOGAGE : Afficher toutes les classes trouvées
        Set<String> allClasses = calc.getAllClasses();
        System.out.println("📊 Nombre de classes trouvées : " + allClasses.size());
        System.out.println("📋 Liste des classes :");
        for (String cls : allClasses) {
            System.out.println("   • " + cls);
        }
        
        // DÉBOGAGE : Afficher quelques valeurs de couplage
        System.out.println("\n🔍 Échantillon de valeurs de couplage :");
        int count = 0;
        for (String cls1 : allClasses) {
            for (String cls2 : allClasses) {
                if (!cls1.equals(cls2)) {
                    double coupling = calc.getCoupling(cls1, cls2);
                    if (coupling > 0) {
                        System.out.println("   " + cls1 + " <-> " + cls2 + " = " + coupling);
                        count++;
                        if (count >= 5) break;
                    }
                }
            }
            if (count >= 5) break;
        }
        
        if (count == 0) {
            System.out.println("   ⚠️ ATTENTION : Aucun couplage non-nul détecté !");
        }

        // 3️⃣ Identifier les modules avec un seuil CP = 0.1
        double CP = 0.1;
        System.out.println("\n🎯 Seuil de couplage (CP) : " + CP);
        System.out.println("⏳ Identification des modules en cours...");
        
        ModuleIdentifier identifier = new ModuleIdentifier(calc, CP);
        List<List<String>> modules = identifier.identifyModules();
        
        System.out.println("✅ Identification terminée !\n");

        // Afficher dans la console
        System.out.println("========================================");
        System.out.println("       MODULES IDENTIFIÉS");
        System.out.println("========================================\n");
        
        if (modules.isEmpty()) {
            System.out.println("⚠️ AUCUN MODULE IDENTIFIÉ !");
            System.out.println("Causes possibles :");
            System.out.println("  - Aucune classe trouvée dans le projet");
            System.out.println("  - Le seuil CP est trop élevé");
            System.out.println("  - Problème dans l'analyse du graphe d'appel");
        } else {
            int i = 1;
            for (List<String> module : modules) {
                System.out.println("Module " + i + " (" + module.size() + " classe(s)) :");
                for (String className : module) {
                    System.out.println("  • " + className);
                }
                System.out.println();
                i++;
            }
        }

        // 4️⃣ Afficher l'interface graphique
        System.out.println("🖥️ Ouverture de l'interface graphique...\n");
        ModuleVisualizer.showModules(modules);
    }
}