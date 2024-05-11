package edu.stanford.bmir.protege.examples.oquareMetrics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.bmir.protege.examples.view.MetricCalculator;

public class LCOMOntoCalculator implements MetricCalculator {

    private static final Logger log = LoggerFactory.getLogger(LCOMOntoCalculator.class);
        @Override
        public double calculate(OWLOntology ontology) {
            Set<OWLClass> leafClasses = findLeafClasses(ontology);
            int sumOfPathLengths = 0;
            for (OWLClass leafClass : leafClasses) {
                int pathLength = calculatePathLength(leafClass, ontology);
                sumOfPathLengths += pathLength;
            }
            log.info("Sum of path lengths: " + sumOfPathLengths + ", number of leaf classes: " + leafClasses.size());
            return (double) sumOfPathLengths / leafClasses.size();

        }

        private Set<OWLClass> findLeafClasses(OWLOntology ontology) {
            Set<OWLClass> leafClasses = new HashSet<>();

            for (OWLClass owlClass : ontology.getClassesInSignature()) {

                Collection<OWLClassExpression> subClasses = EntitySearcher.getSubClasses(owlClass, ontology);

                if (subClasses.isEmpty()) {
                    leafClasses.add(owlClass);
                }
            }
            return leafClasses;
        }

        private int calculatePathLength(OWLClass leafClass, OWLOntology ontology) {
            int pathLength = 0;

            OWLClass currentClass = leafClass;
            while (!currentClass.isOWLThing()) {

                pathLength++;
                Collection<OWLClassExpression> superClasses = EntitySearcher.getSuperClasses(currentClass, ontology);

                if (!superClasses.isEmpty()) {
                    currentClass = superClasses.iterator().next().asOWLClass();
                } else {
                    break;
                }
            }
            return pathLength;
        }



    }


