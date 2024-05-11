package edu.stanford.bmir.protege.examples.oquareMetrics;

import org.semanticweb.owlapi.model.OWLOntology;
import edu.stanford.bmir.protege.examples.view.MetricCalculator;

public class Modularity implements MetricCalculator {

    @Override
    public double calculate(OWLOntology ontology) {
        WMCOntoCalculator wmc = new WMCOntoCalculator();
        CBOntoCalculator cbo = new CBOntoCalculator();

        double modularity = wmc.calculate(ontology) + cbo.calculate(ontology);

        return modularity;
    }
}
