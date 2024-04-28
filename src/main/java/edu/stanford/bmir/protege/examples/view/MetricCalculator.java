// package edu.stanford.bmir.protege.examples.view;

// import org.semanticweb.owlapi.model.OWLOntology;
// import org.semanticweb.owlapi.model.*;
// import java.util.Set;

// public class MetricCalculator {

//     public OWLOntology activeOntology;
//     public Set<OWLClass> classes;


//     public double calculateWMCOnto(OWLOntology ontology) {
//         int sumPropertiesAndRelationships = 0;
//         int sumPropertiesWMC = 0;
//         int sumRelationshipsWMC = 0;
//             sumPropertiesWMC = activeOntology.getObjectPropertiesInSignature().size()
//                     + activeOntology.getDataPropertiesInSignature().size();
//                     sumRelationshipsWMC = activeOntology.getObjectPropertiesInSignature().size();
//             sumPropertiesAndRelationships += sumPropertiesWMC + sumRelationshipsWMC;
//         double WMCOnto = (double) sumPropertiesAndRelationships / classes.size();

//         return WMCOnto;
//     }
// }


package edu.stanford.bmir.protege.examples.view;

import org.semanticweb.owlapi.model.OWLOntology;

public interface MetricCalculator {
    double calculate(OWLOntology ontology);
}