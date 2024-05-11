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
import edu.stanford.bmir.protege.examples.oquareMetrics.AROntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.CBOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.CROntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.DITOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.INROntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.WMCOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.LCOMOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.NACOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.NOCOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.NOMOntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.POntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.PROntoCalculator;
import edu.stanford.bmir.protege.examples.oquareMetrics.RFCOntoCalculator;

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
        this.metricCalculators = Arrays.asList(new WMCOntoCalculator(), new ANOntoCalculator(), new AROntoCalculator(), new CBOntoCalculator(), new CROntoCalculator(), new DITOntoCalculator(), new INROntoCalculator(), new LCOMOntoCalculator(), new NACOntoCalculator(), new NOCOntoCalculator(), new NOMOntoCalculator(), new POntoCalculator(), new PROntoCalculator(), new RFCOntoCalculator());

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
                + "<p>This help section provides detailed information about the OQuaRE metrics used to evaluate the quality of ontologies. Each metric is calculated based on specific ontology elements and contributes to a comprehensive understanding of the ontology's characteristics.</p>"
                + "<h3>Ontology Quality Metrics</h3>"
                + "<ul>"
                + "<li><strong>Lack of Cohesion in Methods (LCOMOnto):</strong> Measures the semantic and conceptual relatedness of classes in an ontology. A lower score indicates better cohesion.</li>"
                + "<li><strong>Weighted Method Count (WMCOnto):</strong> Calculates the mean number of properties and relationships per class. Higher values may indicate greater complexity.</li>"
                + "<li><strong>Depth of Subsumption Hierarchy (DITOnto):</strong> Measures the length of the longest path from the root class to a leaf class. Deeper hierarchies may be more complex.</li>"
                + "<li><strong>Number of Ancestor Classes (NACOnto):</strong> Calculates the mean number of ancestor classes per leaf class. Reflects the inheritance hierarchy.</li>"
                + "<li><strong>Number of Children (NOCOnto):</strong> Measures the mean number of direct subclasses per class. Indicates the breadth of the ontology hierarchy.</li>"
                + "<li><strong>Coupling Between Objects (CBOOnto):</strong> Quantifies the number of related classes. Lower coupling is often desirable for maintainability.</li>"
                + "<li><strong>Response for a Class (RFCOnto):</strong> Measures the number of properties directly accessible from each class. Reflects class complexity.</li>"
                + "<li><strong>Number of Properties (NOMOnto):</strong> Calculates the average number of properties per class. Provides insights into property richness.</li>"
                + "<li><strong>Properties Richness (PROnto):</strong> Evaluates the number of properties defined relative to the total number of relationships and properties.</li>"
                + "<li><strong>Relationship Richness (RROnto):</strong> The number of usages of object and data properties divided by the number of subclass relationships and properties.</li>"
                + "<li><strong>Attribute Richness (AROnto):</strong> Measures the mean number of attributes per class. Reflects attribute complexity and richness.</li>"
                + "<li><strong>Relationships per Class (INROnto):</strong> Calculates the mean number of relationships per class. Indicates relationship complexity.</li>"
                + "<li><strong>Class Richness (CROnto):</strong> Evaluates the mean number of instances per class. Reflects instance diversity and abundance.</li>"
                + "<li><strong>Ancestors per Class (POnto):</strong> Evaluates the number of ancestors per class by dividing the number of superclasses per each class.</li>"
                + "<li><strong>Annotation Richness (ANOnto):</strong> Measures the mean number of annotations per class. Reflects the extent of metadata associated with classes.</li>"
                + "<li><strong>Tangledness (TMOnto):</strong> Evaluates the mean number of parents per class, considering multiple inheritance. Reflects class hierarchy complexity.</li>"
                + "</ul>"
                + "<h3>Interpreting Scores</h3>"
                + "<p>Scores for each metric can be interpreted as follows:</p>"
                + "<ul>"
                // Include an image showing best to worst score for each metric
                + "</ul>"
                + "<h3>Calculating Subcharacteristics</h3>"
                + "<p>Each metric contributes to one or more quality subcharacteristics, which are used to evaluate the overall quality of an ontology. The subcharacteristics include:</p>"
                + "<ul>"
                + "<li><strong>Modularity:</strong> Measures the extent to which an ontology can be divided into smaller, independent modules.</li>"
                + "<li><strong>Reusability:</strong> Reflects the extent to which ontology components can be reused in other contexts.</li>"
                + "<li><strong>Analyzability:</strong> Indicates the ease with which an ontology can be analyzed and understood.</li>"
                + "<li><strong>Testability:</strong> Reflects the ease with which an ontology can be tested and validated.</li>"
                + "<li><strong>Modular Stability:</strong> Measures the extent to which an ontology's modules remain stable over time.</li>"
                + "<h3>Interpreting Scores for Subcharacterisitics</h3>"
                + "<p>Scores for each subcharacteristic can be interpreted as follows:</p>"
                + "<ul>"
                // Include an image showing best to worst score for each subcharacteristic
                + "</ul>"
                // Tell about overall score
                //Include an image showing best to worst score for overall quality
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
                if (value >= 0.8) return Color.GREEN;
                else if (value > 0.2) return Color.YELLOW;
                else if (value < 0.2) return Color.RED;
                break;

            case "AROnto":
                if (value > 0.8) return Color.GREEN;
                else if (value > 0.2) return Color.YELLOW;
                else if (value < 0.2) return Color.RED;
                break;

            case "CBOnto":
                if (value >= 1 && value <= 3) return Color.GREEN;
                else if (value > 3) return Color.YELLOW;
                else if (value <= 12) return Color.YELLOW;
                else if (value > 12) return Color.RED;
                break;

            case "CROnto":
                if (value > 0.8) return Color.GREEN;
                else if (value > 0.2) return Color.YELLOW;
                else if (value < 0.2) return Color.RED;
                break;

            case "DITOnto":
                if (value >= 1 && value <= 2) return Color.GREEN;
                else if (value > 2 && value < 8) return Color.YELLOW;
                else if (value >= 8) return Color.RED;
                break;

            case "INROnto":
                if (value > 0.8) return Color.GREEN;
                else if (value > 0.2) return Color.YELLOW;
                else if (value < 0.2) return Color.RED;
                break;

            case "NACOnto":
                if (value >= 1 && value <= 2) return Color.GREEN;
                else if (value > 2 && value < 8) return Color.YELLOW;
                else if (value >= 8) return Color.RED;
                break;

            case "NOCOnto":
                if (value >= 1 && value <= 2) return Color.GREEN;
                else if (value > 2 && value < 8) return Color.YELLOW;
                else if (value >= 8) return Color.RED;
                break;

            case "NOMOnto":
                if (value >= 1 && value <= 2) return Color.GREEN;
                else if (value > 2 && value < 8) return Color.YELLOW;
                else if (value >= 8) return Color.RED;
                break;

            case "POnto":
                if (value > 0.8) return Color.GREEN;
                else if (value > 0.2) return Color.YELLOW;
                else if (value < 0.2) return Color.RED;
                break;

            case "PROnto":
                if (value > 0.8) return Color.GREEN;
                else if (value > 0.2) return Color.YELLOW;
                else if (value < 0.2) return Color.RED;
                break;

            case "RFCOnto":
                if (value >= 1 && value <= 2) return Color.GREEN;
                else if (value > 2 && value < 8) return Color.YELLOW;
                else if (value >= 8) return Color.RED;
                break;
        }
        return Color.WHITE;
    }
}

}
