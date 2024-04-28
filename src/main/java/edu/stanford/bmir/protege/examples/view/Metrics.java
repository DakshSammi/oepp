package edu.stanford.bmir.protege.examples.view;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
// import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.*;

import edu.stanford.bmir.protege.examples.oquareMetrics.ANOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.WMCOntoCalculator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Metrics extends JPanel {
    private JTable metricsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton = new JButton("Refresh");
    private JButton helpButton = new JButton("Help"); // Create the Help button
    private JLabel titleLabel = new JLabel("OEPP - Metrics Dashboard", SwingConstants.CENTER);

    private OWLModelManager modelManager;
    private List<MetricCalculator> metricCalculators;

    private ActionListener refreshAction = e -> recalculate();
    private ActionListener helpAction = e -> showHelpDialog();

    private OWLModelManagerListener modelListener = event -> {
        if (event.getType() == EventType.ACTIVE_ONTOLOGY_CHANGED) {
            recalculate();
        }
    };

     public Metrics(OWLModelManager modelManager){
        this.modelManager = modelManager;
        this.metricCalculators = Arrays.asList(new WMCOntoCalculator(), new ANOntoCalculator());

        setupTitle();
        setupTable();
        setupBottomPane();

        recalculate();

        refreshButton.addActionListener(refreshAction);
        helpButton.addActionListener(helpAction);
        modelManager.addListener(modelListener);

        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(new JScrollPane(metricsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.add(refreshButton);
        buttonPanel.add(helpButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    private void setupTitle() {
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);
    }

    private void setupBottomPane() {
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        refreshButton.setFont(buttonFont);
        helpButton.setFont(buttonFont);

        Insets buttonMargins = new Insets(5, 15, 5, 15);
        refreshButton.setMargin(buttonMargins);
        helpButton.setMargin(buttonMargins);
    }

    private void showHelpDialog() {
        JDialog helpDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Help");
        JTextPane helpTextPane = new JTextPane();
        helpTextPane.setContentType("text/html");
        helpTextPane.setEditable(false);

        String htmlContent = "<html><body>"
                + "<h2>Metrics Dashboard Help</h2>"
                + "<p>Here you can put the help content in HTML format.</p>"
                + "</body></html>";
        helpTextPane.setText(htmlContent);

        JScrollPane scrollPane = new JScrollPane(helpTextPane);
        helpDialog.add(scrollPane);

        helpDialog.setSize(new Dimension(600, 400));
        helpDialog.setLocationRelativeTo(null);
        helpDialog.setVisible(true);
    }

    private void setupTable() {

        tableModel = new DefaultTableModel(new Object[]{"Metrics", "Computed values"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // disable cell editing
            }
        };
        metricsTable = new JTable(tableModel);
        metricsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        metricsTable.setRowHeight(metricsTable.getRowHeight() + 10);
        metricsTable.setDefaultRenderer(Object.class, new MetricCellRenderer());

        Font headerFont = new Font("Arial", Font.BOLD, 16);
        JTableHeader header = metricsTable.getTableHeader();
        header.setFont(headerFont);
        header.setPreferredSize(new Dimension(100, 30));
    }

    public void dispose() {
        modelManager.removeListener(modelListener);
        refreshButton.removeActionListener(refreshAction);
    }

   private void recalculate() {
    OWLOntology activeOntology = modelManager.getActiveOntology();

    ExecutorService executorService = Executors.newFixedThreadPool(metricCalculators.size());
    List<Future<Double>> metricFutures = new ArrayList<>();

    for (MetricCalculator calculator : metricCalculators) {
        Future<Double> metricFuture = executorService.submit(() -> calculator.calculate(activeOntology));
        metricFutures.add(metricFuture);
    }

    executorService.shutdown();

    List<Double> metricValues = new ArrayList<>();
    for (Future<Double> metricFuture : metricFutures) {
        try {
            metricValues.add(metricFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
        updateTable(metricValues);
    }

    private void updateTable(List<Double> metricValues) {
        tableModel.setRowCount(0);
        for (int i = 0; i < metricValues.size(); i++) {
            String displayName = "";
            if(metricCalculators.get(i) instanceof WMCOntoCalculator) {
                displayName = "WMCOnto";
            } else if(metricCalculators.get(i) instanceof ANOntoCalculator) {
                displayName = "ANOnto";
            }
            tableModel.addRow(new Object[]{displayName, metricValues.get(i)});
        }
    }

private class MetricCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (column == 1) { // Apply color coding only to the value column
            String metricName = (String) table.getValueAt(row, 0);
            Double metricValue = (Double) value;
            c.setBackground(getColorForMetric(metricName, metricValue));
            c.setForeground(Color.BLACK);
        } else {
            c.setBackground(Color.WHITE);
            c.setForeground(Color.BLACK);
        }
        return c;
    }

    private Color getColorForMetric(String metricName, Double value) {
        switch (metricName) {
            case "WMCOnto":
                if (value < 0.2) return Color.RED;
                else if (value > 0.2) return Color.YELLOW;
                else if (value > 0.8) return Color.GREEN;
                break;
            case "ANOnto":
                if (value > 0.5) return Color.GREEN;
                else if (value < 0.1) return Color.RED; // Example condition
                break;
        }
        return Color.WHITE;
    }
}

}
