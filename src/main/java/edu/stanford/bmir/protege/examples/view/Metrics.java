package edu.stanford.bmir.protege.examples.view;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import java.awt.Desktop;
import java.net.URI;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.*;

import edu.stanford.bmir.protege.examples.oquareMetrics.ANOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.AROntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.CBOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.CROntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.DITOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.INROntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.LCOMOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.WMCOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.NACOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.NOCOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.NOMOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.POntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.PROntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.RFCOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.RROntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.TMOntoCalculator;
// import edu.stanford.bmir.protege.examples.oquareMetrics.Modularity;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Metrics extends JPanel {
    private JTable metricsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton = new JButton("Refresh");
    private JButton helpButton = new JButton("Help"); // Create the help button
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
        this.metricCalculators = Arrays.asList(new WMCOntoCalculator(), new ANOntoCalculator(), new AROntoCalculator(), new CBOntoCalculator(), new CROntoCalculator(), new DITOntoCalculator(), new INROntoCalculator(), new LCOMOntoCalculator(), new NACOntoCalculator(), new NOCOntoCalculator(), new NOMOntoCalculator(), new POntoCalculator(), new PROntoCalculator(), new RFCOntoCalculator(), new RROntoCalculator(), new TMOntoCalculator());

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
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("https://oepp-docs.vercel.app/"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupTable() {

        tableModel = new DefaultTableModel(new Object[]{"Metrics", "Computed values"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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
            else if(metricCalculators.get(i) instanceof AROntoCalculator) {
                displayName = "AROnto";
            }
            else if(metricCalculators.get(i) instanceof CBOntoCalculator) {
                displayName = "CBOnto";
            }
            else if(metricCalculators.get(i) instanceof CROntoCalculator) {
                displayName = "CROnto";
            }
            else if (metricCalculators.get(i) instanceof DITOntoCalculator) {
                displayName = "DITOnto";
            }
            else if (metricCalculators.get(i) instanceof INROntoCalculator) {
                displayName = "INROnto";
            }
            else if (metricCalculators.get(i) instanceof LCOMOntoCalculator) {
                displayName = "LCOMOnto";
            }
            else if (metricCalculators.get(i) instanceof NACOntoCalculator) {
                displayName = "NACOnto";
            }
            else if (metricCalculators.get(i) instanceof NOCOntoCalculator) {
                displayName = "NOCOnto";
            }
            else if (metricCalculators.get(i) instanceof NOMOntoCalculator) {
                displayName = "NOMOnto";
            }
            else if (metricCalculators.get(i) instanceof POntoCalculator) {
                displayName = "POnto";
            }
            else if (metricCalculators.get(i) instanceof PROntoCalculator) {
                displayName = "PROnto";
            }
            else if (metricCalculators.get(i) instanceof RFCOntoCalculator) {
                displayName = "RFCOnto";
            }

            else if (metricCalculators.get(i) instanceof TMOntoCalculator) {
                displayName = "TMOnto";
            }

            else if (metricCalculators.get(i) instanceof RROntoCalculator) {
                displayName = "RROnto";
            }

            tableModel.addRow(new Object[]{displayName, metricValues.get(i)});
        }
    }

private class MetricCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (column == 1)
        {
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
                if (value > 8) return Color.RED;
                else if (value >= 6 && value < 8) return Color.PINK;
                else if (value >= 4 && value < 6) return Color.ORANGE;
                else if (value >= 2 && value < 4) return Color.YELLOW;
                else if (value < 2) return Color.GREEN;
                break;

            case "ANOnto":
                if (value > 0.8) return Color.GREEN;
                else if (value > 0.6 && value <= 0.8) return Color.YELLOW;
                else if (value > 0.4 && value <= 0.6) return Color.ORANGE;
                else if (value >= 0.2 && value <= 0.4) return Color.PINK;
                else if (value < 0.2) return Color.RED;
                break;

            case "AROnto":
                if (value > 0.8) return Color.GREEN;
                else if (value > 0.6 && value <= 0.8) return Color.YELLOW;
                else if (value > 0.4 && value <= 0.6) return Color.ORANGE;
                else if (value >= 0.2 && value <= 0.4) return Color.PINK;
                else if (value < 0.2) return Color.RED;
                break;

            case "CBOnto":
                if (value >= 1 && value <= 3) return Color.GREEN;
                else if (value > 3 && value <= 6) return Color.YELLOW;
                else if (value > 6 && value <= 8) return Color.ORANGE;
                else if (value > 8 && value <= 12) return Color.PINK;
                else if (value > 12) return Color.RED;

            case "CROnto":
                if (value > 0.8) return Color.GREEN;
                else if (value > 0.6 && value <= 0.8) return Color.YELLOW;
                else if (value > 0.4 && value <= 0.6) return Color.ORANGE;
                else if (value >= 0.2 && value <= 0.4) return Color.PINK;
                else if (value < 0.2) return Color.RED;
                break;

            case "DITOnto":
                if (value >= 1 && value <= 2) return Color.GREEN;
                else if (value > 2 && value <= 4) return Color.YELLOW;
                else if (value > 4 && value <= 6) return Color.ORANGE;
                else if (value > 6 && value <= 8) return Color.PINK;
                else if (value > 8) return Color.RED;
                break;

            case "INROnto":
                if (value > 0.8) return Color.GREEN;
                else if (value > 0.6 && value <= 0.8) return Color.YELLOW;
                else if (value > 0.4 && value <= 0.6) return Color.ORANGE;
                else if (value >= 0.2 && value <= 0.4) return Color.PINK;
                else if (value < 0.2) return Color.RED;
                break;

            case "NACOnto":
                if (value >= 1 && value <= 2) return Color.GREEN;
                else if (value > 2 && value <= 4) return Color.YELLOW;
                else if (value > 4 && value <= 6) return Color.ORANGE;
                else if (value > 6 && value <= 8) return Color.PINK;
                else if (value > 8) return Color.RED;
                break;

            case "NOCOnto":
                if (value >= 1 && value <= 2) return Color.GREEN;
                else if (value > 2 && value <= 4) return Color.YELLOW;
                else if (value > 4 && value <= 6) return Color.ORANGE;
                else if (value > 6 && value <= 8) return Color.PINK;
                else if (value > 8) return Color.RED;
                break;

            case "NOMOnto":
                if (value <= 2) return Color.GREEN;
                else if (value > 2 && value <= 4) return Color.YELLOW;
                else if (value > 4 && value <= 6) return Color.ORANGE;
                else if (value > 6 && value <= 8) return Color.PINK;
                else if (value > 8) return Color.RED;
                break;

            case "POnto":
                if (value > 0.8) return Color.GREEN;
                else if (value > 0.6 && value <= 0.8) return Color.YELLOW;
                else if (value > 0.4 && value <= 0.6) return Color.ORANGE;
                else if (value >= 0.2 && value <= 0.4) return Color.PINK;
                else if (value < 0.2) return Color.RED;
                break;

            case "PROnto":
                if (value > 0.8) return Color.GREEN;
                else if (value > 0.6 && value <= 0.8) return Color.YELLOW;
                else if (value > 0.4 && value <= 0.6) return Color.ORANGE;
                else if (value >= 0.2 && value <= 0.4) return Color.PINK;
                else if (value < 0.2) return Color.RED;
                break;

            case "RFCOnto":
                if (value >= 1 && value <= 3) return Color.GREEN;
                else if (value > 3 && value <= 6) return Color.YELLOW;
                else if (value > 6 && value <= 8) return Color.ORANGE;
                else if (value > 8 && value <= 12) return Color.PINK;
                else if (value > 12) return Color.RED;

            case "RROnto":
                if (value > 0.8) return Color.GREEN;
                else if (value > 0.6 && value <= 0.8) return Color.YELLOW;
                else if (value > 0.4 && value <= 0.6) return Color.ORANGE;
                else if (value >= 0.2 && value <= 0.4) return Color.PINK;
                else if (value < 0.2) return Color.RED;
                break;

            case "TMOnto":
                if (value >= 1 && value <= 2) return Color.GREEN;
                else if (value > 2 && value <= 4) return Color.YELLOW;
                else if (value > 4 && value <= 6) return Color.ORANGE;
                else if (value > 6 && value <= 8) return Color.PINK;
                else if (value > 8) return Color.RED;
                break;

            // case "ModularityScore":
            //     if(value > 2 && value <= 5) return Color.GREEN;
            //     else if(value > 5 && value <= 10) return Color.YELLOW;
            //     else if(value > 10 && value <= 14) return Color.ORANGE;
            //     else if(value > 14 && value <= 20) return Color.PINK;
            //     else if(value > 20) return Color.RED;

            // case "Reusability":
            //     if(value <= 10)return Color.GREEN;
            //     else if(value > 10 && value <= 20) return Color.YELLOW;
            //     else if(value > 20 && value <= 28) return Color.ORANGE;
            //     else if(value > 28 && value <= 40) return Color.PINK;
            //     else if(value > 40) return Color.RED;

            // case "Analysability":
            //     if(value <= 14) return Color.GREEN;
            //     else if(value > 14 && value <= 28) return Color.YELLOW;
            //     else if(value > 28 && value <= 40) return Color.ORANGE;
            //     else if(value > 40 && value <= 56) return Color.PINK;
            //     else if(value > 56) return Color.RED;

            // case "Modular Stability":
            //     if(value <= 12) return Color.GREEN;
            //     else if(value > 12 && value <= 24) return Color.YELLOW;
            //     else if(value > 24 && value <= 34) return Color.ORANGE;
            //     else if(value > 34 && value <= 48) return Color.PINK;
            //     else if(value > 48) return Color.RED;

            // case "Testability":
            //     if(value <= 14) return Color.GREEN;
            //     else if(value > 14 && value <= 28) return Color.YELLOW;
            //     else if(value > 28 && value <= 34) return Color.ORANGE;
            //     else if(value > 34 && value <= 56) return Color.PINK;
            //     else if(value > 56) return Color.RED;

            // case "Overall Score":
            //     if(value <= 66) return Color.GREEN;
            //     else if(value > 66 && value <= 142) return Color.YELLOW;
            //     else if(value > 142 && value <= 196) return Color.ORANGE;
            //     else if(value > 196 && value <= 284) return Color.PINK;
            //     else if(value > 284) return Color.RED;

        }
        return Color.WHITE;
    }
}

}
