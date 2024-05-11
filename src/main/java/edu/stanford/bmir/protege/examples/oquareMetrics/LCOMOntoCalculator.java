package edu.stanford.bmir.protege.examples.oquareMetrics;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import edu.stanford.bmir.protege.examples.view.MetricCalculator;

import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LCOMOntoCalculator implements MetricCalculator {

    @Override
    public double calculate(OWLOntology ontology) {

        final Logger log = LoggerFactory.getLogger(LCOMOntoCalculator.class);

        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);


        double LCOMOnto = 0.0;
        Set<OWLClass> classes = ontology.getClassesInSignature();
        int totalPathLength = 0;
        int totalLeaves = 0;

        // Find all leaf classes
        Set<OWLClass> leafClasses = classes.stream()
            .filter(cls -> EntitySearcher.getSubClasses(cls, ontology).stream().noneMatch(sub -> !sub.isAnonymous()))
            .collect(Collectors.toSet());

        // Calculate the sum of path lengths and the total number of leaves
        for (OWLClass leaf : leafClasses) {
            int pathLength = getPathLength(leaf, ontology, reasoner);
            totalPathLength += pathLength;
            totalLeaves++;
        }

        if (totalLeaves != 0) { // Avoid division by zero
            LCOMOnto = (double) totalPathLength / totalLeaves-1;
        }

        reasoner.dispose();

        log.info("LCOM Onto: " + LCOMOnto + " (totalPathLength: " + totalPathLength + ", totalLeaves: " + totalLeaves + ")");

        return LCOMOnto;
    }

    private int getPathLength(OWLClass cls, OWLOntology ontology, OWLReasoner reasoner) {
        // This method should calculate the length of the path from the root to the class
        Set<OWLClass> visited = new HashSet<>();
        return getPathLengthHelper(cls, ontology, reasoner, visited);
    }

    private int getPathLengthHelper(OWLClass cls, OWLOntology ontology, OWLReasoner reasoner, Set<OWLClass> visited) {
        int length = 0;
        visited.add(cls);
        Set<OWLClass> superClasses = reasoner.getSuperClasses(cls, true).getFlattened();
        for (OWLClass superClass : superClasses) {
            if (!visited.contains(superClass)) {
                length = 1 + getPathLengthHelper(superClass, ontology, reasoner, visited);
            }
        }
        // If there are no superclasses, this is the root
        return length;
    }
}
