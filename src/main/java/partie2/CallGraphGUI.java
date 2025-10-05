package partie2;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.util.*;

public class CallGraphGUI extends JFrame {
    
    private CallGraphAnalyzer analyzer;
    private JTextField projectPathField;
    private GraphPanel graphPanel;
    private JTextArea infoArea;
    
    private static final Color PRIMARY = new Color(41, 128, 185);
    private static final Color SUCCESS = new Color(39, 174, 96);
    private static final Color DANGER = new Color(231, 76, 60);
    private static final Color WARNING = new Color(243, 156, 18);
    private static final Color LIGHT_BG = new Color(236, 240, 241);
    private static final Color DARK = new Color(52, 73, 94);
    
    public CallGraphGUI() {
        setTitle("Graphe d'Appel de Méthodes - Visualisation");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(LIGHT_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Center : Graphe + Info
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(1000);
        splitPane.setBackground(LIGHT_BG);
        
        graphPanel = new GraphPanel();
        JScrollPane graphScroll = new JScrollPane(graphPanel);
        graphScroll.setBorder(createTitledBorder("Graphe d'Appel", PRIMARY));
        splitPane.setLeftComponent(graphScroll);
        
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        infoArea.setBackground(Color.WHITE);
        JScrollPane infoScroll = new JScrollPane(infoArea);
        infoScroll.setBorder(createTitledBorder("Informations", SUCCESS));
        splitPane.setRightComponent(infoScroll);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("ANALYSEUR DE GRAPHE D'APPEL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(DARK);
        
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(Color.WHITE);
        
        projectPathField = new JTextField("C:\\Users\\Jihen\\eclipse-workspace\\ProjetTest");
        projectPathField.setFont(new Font("Consolas", Font.PLAIN, 12));
        projectPathField.setPreferredSize(new Dimension(0, 32));
        projectPathField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        
        JButton browseButton = createButton("Parcourir", WARNING, 110, 32);
        browseButton.addActionListener(e -> browseProject());
        
        JButton analyzeButton = createButton("ANALYSER", SUCCESS, 150, 32);
        analyzeButton.addActionListener(e -> analyzeProject());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(browseButton);
        buttonPanel.add(analyzeButton);
        
        inputPanel.add(projectPathField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createButton(String text, Color color, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setPreferredSize(new Dimension(width, height));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private TitledBorder createTitledBorder(String title, Color color) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(color, 2),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13),
            color
        );
    }
    
    private void browseProject() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Sélectionner le projet");
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            projectPathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void analyzeProject() {
        String projectPath = projectPathField.getText().trim();
        
        if (projectPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un projet valide", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog loadingDialog = createLoadingDialog();
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                analyzer = new CallGraphAnalyzer(projectPath);
                analyzer.analyze();
                return null;
            }
            
            @Override
            protected void done() {
                loadingDialog.dispose();
                try {
                    get();
                    displayResults();
                    JOptionPane.showMessageDialog(CallGraphGUI.this,
                        "Analyse terminée avec succès !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CallGraphGUI.this,
                        "Erreur lors de l'analyse : " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
        loadingDialog.setVisible(true);
    }
    
    private JDialog createLoadingDialog() {
        JDialog dialog = new JDialog(this, "Analyse en cours", true);
        dialog.setUndecorated(true);
        
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY, 3),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        
        JLabel label = new JLabel("Construction du graphe d'appel...");
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(350, 30));
        progressBar.setForeground(PRIMARY);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        
        dialog.add(panel);
        dialog.setSize(450, 140);
        dialog.setLocationRelativeTo(this);
        
        return dialog;
    }
    
    private void displayResults() {
        Map<String, Set<String>> callGraph = analyzer.getCallGraph();
        graphPanel.setCallGraph(callGraph);
        graphPanel.repaint();
        
        // Afficher les informations
        StringBuilder info = new StringBuilder();
        info.append("=== STATISTIQUES DU GRAPHE ===\n\n");
        
        int totalMethods = callGraph.size();
        int totalCalls = callGraph.values().stream()
            .mapToInt(Set::size)
            .sum();
        
        info.append("Nombre de méthodes : ").append(totalMethods).append("\n");
        info.append("Nombre d'appels : ").append(totalCalls).append("\n\n");
        
        info.append("=== DÉTAILS DES APPELS ===\n\n");
        
        for (Map.Entry<String, Set<String>> entry : callGraph.entrySet()) {
            info.append("• ").append(entry.getKey()).append("\n");
            if (entry.getValue().isEmpty()) {
                info.append("  └─ (aucun appel)\n");
            } else {
                for (String callee : entry.getValue()) {
                    info.append("  └─ ").append(callee).append("\n");
                }
            }
            info.append("\n");
        }
        
        infoArea.setText(info.toString());
        infoArea.setCaretPosition(0);
    }
    
    // Panel pour dessiner le graphe
    private class GraphPanel extends JPanel {
        private Map<String, Set<String>> callGraph = new HashMap<>();
        private Map<String, Point> nodePositions = new HashMap<>();
        
        public GraphPanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(2000, 2000));
        }
        
        public void setCallGraph(Map<String, Set<String>> callGraph) {
            this.callGraph = callGraph;
            calculateNodePositions();
        }
        
        private void calculateNodePositions() {
            nodePositions.clear();
            
            int x = 50;
            int y = 50;
            int verticalSpacing = 100;
            int horizontalSpacing = 300;
            int nodesPerColumn = 10;
            int nodeCount = 0;
            
            for (String method : callGraph.keySet()) {
                if (nodeCount > 0 && nodeCount % nodesPerColumn == 0) {
                    x += horizontalSpacing;
                    y = 50;
                }
                
                nodePositions.put(method, new Point(x, y));
                y += verticalSpacing;
                nodeCount++;
            }
            
            // Ajuster la taille du panel
            int maxX = nodePositions.values().stream()
                .mapToInt(p -> p.x)
                .max()
                .orElse(1000) + 300;
            int maxY = nodePositions.values().stream()
                .mapToInt(p -> p.y)
                .max()
                .orElse(1000) + 100;
            
            setPreferredSize(new Dimension(maxX, maxY));
            revalidate();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Dessiner les flèches
            g2d.setColor(new Color(100, 100, 100, 150));
            g2d.setStroke(new BasicStroke(1.5f));
            
            for (Map.Entry<String, Set<String>> entry : callGraph.entrySet()) {
                Point from = nodePositions.get(entry.getKey());
                if (from == null) continue;
                
                for (String callee : entry.getValue()) {
                    Point to = nodePositions.get(callee);
                    if (to != null) {
                        drawArrow(g2d, from.x + 60, from.y + 15, to.x, to.y + 15);
                    }
                }
            }
            
            // Dessiner les nœuds
            FontMetrics fm = g2d.getFontMetrics();
            for (Map.Entry<String, Point> entry : nodePositions.entrySet()) {
                String method = entry.getKey();
                Point pos = entry.getValue();
                
                // Déterminer la couleur selon le nombre d'appels
                int callCount = callGraph.get(method).size();
                Color nodeColor;
                if (callCount == 0) {
                    nodeColor = new Color(189, 195, 199);
                } else if (callCount <= 2) {
                    nodeColor = SUCCESS;
                } else if (callCount <= 5) {
                    nodeColor = WARNING;
                } else {
                    nodeColor = DANGER;
                }
                
                // Dessiner le rectangle
                g2d.setColor(nodeColor);
                g2d.fillRoundRect(pos.x, pos.y, 120, 30, 10, 10);
                
                // Dessiner le texte
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 11));
                
                String displayText = method;
                if (fm.stringWidth(displayText) > 110) {
                    displayText = method.substring(0, Math.min(15, method.length())) + "...";
                }
                
                int textX = pos.x + (120 - fm.stringWidth(displayText)) / 2;
                int textY = pos.y + 20;
                g2d.drawString(displayText, textX, textY);
            }
        }
        
        private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
            g2d.drawLine(x1, y1, x2, y2);
            
            // Dessiner la pointe de flèche
            double angle = Math.atan2(y2 - y1, x2 - x1);
            int arrowSize = 8;
            
            int x3 = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
            int y3 = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));
            int x4 = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
            int y4 = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));
            
            g2d.fillPolygon(new int[]{x2, x3, x4}, new int[]{y2, y3, y4}, 3);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CallGraphGUI gui = new CallGraphGUI();
            gui.setVisible(true);
        });
    }
}