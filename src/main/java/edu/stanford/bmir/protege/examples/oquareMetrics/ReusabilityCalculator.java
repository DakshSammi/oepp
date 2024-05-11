package edu.stanford.bmir.protege.examples.oquareMetrics;

import org.semanticweb.owlapi.model.OWLOntology;
import edu.stanford.bmir.protege.examples.view.MetricCalculator;

public class ReusabilityCalculator implements MetricCalculator {

    @Override
    public double calculate(OWLOntology ontology) {
        // Instantiate the calculators for the metrics involved in calculating Reusability
        WMCOntoCalculator wmCOntoCalculator = new WMCOntoCalculator();
        DITOntoCalculator ditOntoCalculator = new DITOntoCalculator();
        RFCOntoCalculator rfcOntoCalculator = new RFCOntoCalculator();
        NOMOntoCalculator nomOntoCalculator = new NOMOntoCalculator();
        CBOntoCalculator cbOntoCalculator = new CBOntoCalculator();
        NOCOntoCalculator nocOntoCalculator = new NOCOntoCalculator();

        // Calculate the scores for each metric
        double WMCOntoScore = wmCOntoCalculator.calculate(ontology);
        double DITOntoScore = ditOntoCalculator.calculate(ontology);
        double RFCOntoScore = rfcOntoCalculator.calculate(ontology);
        double NOMOntoScore = nomOntoCalculator.calculate(ontology);
        double CBOntoScore = cbOntoCalculator.calculate(ontology);
        double NOCOntoScore = nocOntoCalculator.calculate(ontology);

        // Calculate Reusability score by adding the scores of WMCOnto, DITOnto, RFCOnto, NOMOnto, CBOnto and subtracting NOCOnto
        double reusabilityScore = WMCOntoScore + DITOntoScore + RFCOntoScore + NOMOntoScore + CBOntoScore - NOCOntoScore;

        return reusabilityScore;
    }
}
