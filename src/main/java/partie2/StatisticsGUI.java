package partie2;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsGUI extends JFrame {
    
    private StatisticsAnalyzer analyzer;
    private JTabbedPane tabbedPane;
    private JTextField projectPathField;
    
    // Palette de couleurs moderne
    private static final Color PRIMARY = new Color(41, 128, 185);
    private static final Color SUCCESS = new Color(39, 174, 96);
    private static final Color DANGER = new Color(231, 76, 60);
    private static final Color WARNING = new Color(243, 156, 18);
    private static final Color INFO = new Color(52, 152, 219);
    private static final Color PURPLE = new Color(155, 89, 182);
    private static final Color TEAL = new Color(26, 188, 156);
    private static final Color DARK = new Color(52, 73, 94);
    private static final Color LIGHT_BG = new Color(236, 240, 241);
    
    public StatisticsGUI() {
        setTitle("Analyseur Statistique de Projet Java - Version Moderne");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Forcer le rendu correct des composants
            UIManager.put("Table.gridColor", new Color(200, 200, 200));
            UIManager.put("TableHeader.background", Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(LIGHT_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Onglets
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setEnabled(false);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Titre compact
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 3));
        titlePanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("ANALYSEUR DE CODE JAVA");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(DARK);
        
        JLabel subtitleLabel = new JLabel("Analyse statique complete de votre projet");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        // Sélection projet compacte
        JPanel selectionPanel = new JPanel(new BorderLayout(10, 8));
        selectionPanel.setBackground(Color.WHITE);
        
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY, 2),
            " Selection du Projet ",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11),
            PRIMARY
        );
        selectionPanel.setBorder(BorderFactory.createCompoundBorder(
            border,
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(Color.WHITE);
        
        projectPathField = new JTextField("C:\\Users\\Jihen\\eclipse-workspace\\ProjetTest");
        projectPathField.setFont(new Font("Consolas", Font.PLAIN, 12));
        projectPathField.setPreferredSize(new Dimension(0, 32));
        projectPathField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        
        JButton browseButton = createModernButton("Parcourir...", WARNING, 110, 32);
        browseButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        browseButton.addActionListener(e -> browseProject());
        
        inputPanel.add(projectPathField, BorderLayout.CENTER);
        inputPanel.add(browseButton, BorderLayout.EAST);
        
        JButton analyzeButton = createModernButton("LANCER L'ANALYSE", SUCCESS, 180, 38);
        analyzeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        analyzeButton.addActionListener(e -> analyzeProject());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(analyzeButton);
        
        selectionPanel.add(inputPanel, BorderLayout.CENTER);
        selectionPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(selectionPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createModernButton(String text, Color color, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
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
    
    private void browseProject() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Selectionner le dossier du projet");
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            projectPathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void analyzeProject() {
        String projectPath = projectPathField.getText().trim();
        
        if (projectPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez selectionner un chemin de projet valide", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog loadingDialog = createLoadingDialog();
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                analyzer = new StatisticsAnalyzer(projectPath);
                analyzer.analyze();
                return null;
            }
            
            @Override
            protected void done() {
                loadingDialog.dispose();
                try {
                    get();
                    displayResults();
                    tabbedPane.setEnabled(true);
                    JOptionPane.showMessageDialog(StatisticsGUI.this,
                        "Analyse terminee avec succes !",
                        "Succes",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StatisticsGUI.this,
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
        
        JLabel label = new JLabel("Analyse du projet en cours...");
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
        tabbedPane.removeAll();
        
        List<ClassMetrics> metrics = analyzer.getClassMetricsList();
        
        tabbedPane.addTab("[1] Vue Globale", createOverviewPanel(metrics));
        tabbedPane.addTab("[2] Top Methodes", createTopMethodsPanel(metrics));
        tabbedPane.addTab("[3] Top Attributs", createTopFieldsPanel(metrics));
        tabbedPane.addTab("[4] Exceptionnelles", createCommonClassesPanel(metrics));
        tabbedPane.addTab("[5] Toutes Classes", createClassDetailsPanel(metrics));
        tabbedPane.addTab("[6] Methodes Longues", createLongMethodsPanel(metrics));
    }
    
    private JPanel createOverviewPanel(List<ClassMetrics> metrics) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(LIGHT_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("VUE D'ENSEMBLE DU PROJET");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(DARK);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 20, 20));
        gridPanel.setBackground(LIGHT_BG);
        
        // Calculs
        int totalClasses = metrics.size();
        int totalLines = metrics.stream().mapToInt(c -> c.linesOfCode).sum();
        int totalMethods = metrics.stream().mapToInt(c -> c.methodCount).sum();
        long packageCount = metrics.stream().map(c -> c.packageName).distinct().count();
        
        double avgMethods = metrics.stream().mapToInt(c -> c.methodCount).average().orElse(0.0);
        
        List<Integer> allMethodLines = metrics.stream()
            .flatMap(c -> c.methodLines.stream())
            .collect(Collectors.toList());
        double avgLinesPerMethod = allMethodLines.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
        
        double avgFields = metrics.stream().mapToInt(c -> c.fieldCount).average().orElse(0.0);
        
        int maxParams = metrics.stream()
            .flatMap(c -> c.methodParameters.stream())
            .max(Integer::compare)
            .orElse(0);
        
        // Cartes
        addStatCard(gridPanel, "Classes", String.valueOf(totalClasses), INFO);
        addStatCard(gridPanel, "Lignes", String.valueOf(totalLines), DANGER);
        addStatCard(gridPanel, "Methodes", String.valueOf(totalMethods), SUCCESS);
        addStatCard(gridPanel, "Packages", String.valueOf(packageCount), WARNING);
        addStatCard(gridPanel, "Moy. Meth/Classe", String.format("%.2f", avgMethods), PURPLE);
        addStatCard(gridPanel, "Moy. Lignes/Meth", String.format("%.2f", avgLinesPerMethod), TEAL);
        addStatCard(gridPanel, "Moy. Attr/Classe", String.format("%.2f", avgFields), DARK);
        addStatCard(gridPanel, "Max Parametres", String.valueOf(maxParams), new Color(230, 126, 34));
        
        JPanel infoCard = new JPanel(new BorderLayout(10, 10));
        infoCard.setBackground(Color.WHITE);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INFO, 3),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel infoLabel = new JLabel("<html><b>Information:</b><br>Ces statistiques donnent une vue d'ensemble de la qualite et de la complexite de votre code.</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoCard.add(infoLabel);
        
        gridPanel.add(infoCard);
        
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private void addStatCard(JPanel panel, String title, String value, Color color) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 3),
            BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));
        
        // Titre
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(new Color(127, 140, 141));
        
        // Valeur
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(color);
        
        card.add(titleLabel);
        card.add(valueLabel);
        
        panel.add(card);
    }
    
    private JPanel createTopMethodsPanel(List<ClassMetrics> metrics) {
        List<ClassMetrics> topMethods = metrics.stream()
            .sorted((a, b) -> Integer.compare(b.methodCount, a.methodCount))
            .limit(Math.max(1, metrics.size() / 10))
            .collect(Collectors.toList());
        
        return createTablePanel(
            "Top 10% des classes avec le plus de methodes",
            new String[]{"Rang", "Classe", "Package", "Methodes"},
            topMethods,
            cm -> new Object[]{0, cm.className, cm.packageName, cm.methodCount},
            SUCCESS
        );
    }
    
    private JPanel createTopFieldsPanel(List<ClassMetrics> metrics) {
        List<ClassMetrics> topFields = metrics.stream()
            .sorted((a, b) -> Integer.compare(b.fieldCount, a.fieldCount))
            .limit(Math.max(1, metrics.size() / 10))
            .collect(Collectors.toList());
        
        return createTablePanel(
            "Top 10% des classes avec le plus d'attributs",
            new String[]{"Rang", "Classe", "Package", "Attributs"},
            topFields,
            cm -> new Object[]{0, cm.className, cm.packageName, cm.fieldCount},
            INFO
        );
    }
    
    private JPanel createTablePanel(String title, String[] columns, 
            List<ClassMetrics> data,
            java.util.function.Function<ClassMetrics, Object[]> rowMapper,
            Color headerColor) {
        
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        int rank = 1;
        for (ClassMetrics cm : data) {
            Object[] row = rowMapper.apply(cm);
            row[0] = rank++;
            model.addRow(row);
        }
        
        JTable table = createStyledTable(model, headerColor);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTable createStyledTable(DefaultTableModel model, Color headerColor) {
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(new Color(200, 200, 200));
        table.setSelectionBackground(new Color(52, 152, 219, 80));
        table.setSelectionForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        table.setIntercellSpacing(new Dimension(2, 2));
        table.setOpaque(true);
        table.setFillsViewportHeight(true);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(headerColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        header.setOpaque(true);
        
        // Renderer personnalisé pour forcer l'opacité du header
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                label.setBackground(headerColor);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Arial", Font.BOLD, 14));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                label.setOpaque(true);
                return label;
            }
        });
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            if (table.getColumnCount() > 3) {
                table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            }
        }
        
        return table;
    }
    
    private JPanel createCommonClassesPanel(List<ClassMetrics> metrics) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Classes dans les deux Top 10%");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(DARK);
        
        JLabel subtitleLabel = new JLabel("Classes qui excellent a la fois en nombre de methodes ET d'attributs");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        headerPanel.setBackground(LIGHT_BG);
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        List<ClassMetrics> topMethods = metrics.stream()
            .sorted((a, b) -> Integer.compare(b.methodCount, a.methodCount))
            .limit(Math.max(1, metrics.size() / 10))
            .collect(Collectors.toList());
        
        List<ClassMetrics> topFields = metrics.stream()
            .sorted((a, b) -> Integer.compare(b.fieldCount, a.fieldCount))
            .limit(Math.max(1, metrics.size() / 10))
            .collect(Collectors.toList());
        
        Set<String> topMethodNames = topMethods.stream()
            .map(c -> c.className).collect(Collectors.toSet());
        Set<String> topFieldNames = topFields.stream()
            .map(c -> c.className).collect(Collectors.toSet());
        topMethodNames.retainAll(topFieldNames);
        
        if (topMethodNames.isEmpty()) {
            JPanel noDataPanel = new JPanel(new GridBagLayout());
            noDataPanel.setBackground(Color.WHITE);
            noDataPanel.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
            
            JLabel noDataLabel = new JLabel("Aucune classe ne fait partie des deux categories");
            noDataLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            noDataLabel.setForeground(new Color(127, 140, 141));
            noDataPanel.add(noDataLabel);
            
            panel.add(noDataPanel, BorderLayout.CENTER);
        } else {
            String[] columns = {"Classe", "Package", "Methodes", "Attributs"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            for (String className : topMethodNames) {
                ClassMetrics cm = metrics.stream()
                    .filter(c -> c.className.equals(className))
                    .findFirst().orElse(null);
                if (cm != null) {
                    model.addRow(new Object[]{cm.className, cm.packageName, cm.methodCount, cm.fieldCount});
                }
            }
            
            JTable table = createStyledTable(model, DANGER);
            
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
            scrollPane.getViewport().setBackground(Color.WHITE);
            panel.add(scrollPane, BorderLayout.CENTER);
        }
        
        return panel;
    }
    
    private JPanel createClassDetailsPanel(List<ClassMetrics> metrics) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Details de toutes les classes (tableau triable)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Classe", "Package", "Methodes", "Attributs", "Lignes"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (ClassMetrics cm : metrics) {
            model.addRow(new Object[]{
                cm.className, cm.packageName, cm.methodCount, cm.fieldCount, cm.linesOfCode
            });
        }
        
        JTable table = createStyledTable(model, DARK);
        table.setAutoCreateRowSorter(true);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createLongMethodsPanel(List<ClassMetrics> metrics) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Top 10% des methodes les plus longues");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Classe", "Methode", "Lignes"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (ClassMetrics cm : metrics) {
            if (cm.methods.isEmpty()) continue;
            
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < cm.methods.size(); i++) {
                indices.add(i);
            }
            indices.sort((a, b) -> Integer.compare(cm.methodLines.get(b), cm.methodLines.get(a)));
            
            int limit = Math.max(1, cm.methods.size() / 10);
            for (int i = 0; i < Math.min(limit, indices.size()); i++) {
                int idx = indices.get(i);
                model.addRow(new Object[]{
                    cm.className,
                    cm.methods.get(idx).getName().toString(),
                    cm.methodLines.get(idx)
                });
            }
        }
        
        JTable table = createStyledTable(model, TEAL);
        table.setAutoCreateRowSorter(true);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    public static void main(String[] args) {
        // Forcer l'encodage UTF-8
        System.setProperty("file.encoding", "UTF-8");
        
        SwingUtilities.invokeLater(() -> {
            StatisticsGUI gui = new StatisticsGUI();
            gui.setVisible(true);
        });
    }
}