package edu.stanford.bmir.protege.examples.oquareMetrics;

import edu.stanford.bmir.protege.examples.view.MetricCalculator;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntology;

public class ANOntoCalculator implements MetricCalculator {

    @Override
    public double calculate(OWLOntology ontology) {
        Set<OWLAnnotationProperty> annotationProperties = ontology.getAnnotationPropertiesInSignature();
        int sumAnnotationProperties = annotationProperties.size();
        return sumAnnotationProperties;
    }
}
