package edu.stanford.bmir.protege.examples.view;

import org.semanticweb.owlapi.model.OWLOntology;

public interface MetricCalculator {
    double calculate(OWLOntology ontology);
}