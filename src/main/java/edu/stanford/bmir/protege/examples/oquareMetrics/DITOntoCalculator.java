package edu.stanford.bmir.protege.examples.oquareMetrics;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import edu.stanford.bmir.protege.examples.view.MetricCalculator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DITOntoCalculator implements MetricCalculator {

    @Override
    public double calculate(OWLOntology ontology) {
        Set<OWLClass> rootClasses = getRootClasses(ontology);
        int maxDepth = 0;

        for (OWLClass rootClass : rootClasses) {
            int depth = calculateMaxDepth(ontology, rootClass, new HashSet<>());
            maxDepth = Math.max(maxDepth, depth);
        }

        return maxDepth;
    }

    private Set<OWLClass> getRootClasses(OWLOntology ontology) {
        Set<OWLClass> rootClasses = new HashSet<>();
        for (OWLClass owlClass : ontology.getClassesInSignature()) {
            if (EntitySearcher.getSuperClasses(owlClass, ontology).isEmpty()) {
                rootClasses.add(owlClass);
            }
        }
        return rootClasses;
    }

    private int calculateMaxDepth(OWLOntology ontology, OWLClass owlClass, Set<OWLClass> visited) {
        if (visited.contains(owlClass)) {
            return 0;
        }
        visited.add(owlClass);

        int maxDepth = 0;
        Collection<OWLClass> subClasses = EntitySearcher.getSubClasses(owlClass, ontology).stream()
                .filter(subClass -> subClass instanceof OWLClass)
                .map(subClass -> (OWLClass) subClass)
                .collect(java.util.stream.Collectors.toSet());

        for (OWLClass subClass : subClasses) {
            int depth = calculateMaxDepth(ontology, subClass, visited);
            maxDepth = Math.max(maxDepth, depth);
        }

        return maxDepth + 1;
    }
}