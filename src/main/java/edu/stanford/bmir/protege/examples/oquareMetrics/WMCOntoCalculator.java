package edu.stanford.bmir.protege.examples.oquareMetrics;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import edu.stanford.bmir.protege.examples.view.MetricCalculator;
import java.util.Set;


public class WMCOntoCalculator implements MetricCalculator {
    @Override
    public double calculate(OWLOntology ontology) {
        Set<OWLClass> classes = ontology.getClassesInSignature();
        int sumPropertiesAndRelationships = 0;
        int sumPropertiesWMC = 0;
        int sumRelationshipsWMC = 0;
        sumPropertiesWMC = ontology.getObjectPropertiesInSignature().size()
                + ontology.getDataPropertiesInSignature().size();
        sumRelationshipsWMC = ontology.getObjectPropertiesInSignature().size();
        sumPropertiesAndRelationships += sumPropertiesWMC + sumRelationshipsWMC;

        double WMCOnto = (double) sumPropertiesAndRelationships / classes.size();
        return WMCOnto;
    }


}