package edu.stanford.bmir.protege.examples.oquareMetrics;
import edu.stanford.bmir.protege.examples.view.MetricCalculator;
import java.util.Set;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;


public class ANOntoCalculator implements MetricCalculator {
    @Override
    public double calculate(OWLOntology ontology) {
        double ANOnto = 0;
        Set<OWLClass> classes = ontology.getClassesInSignature();
        int generalAnnotationAxiomCount =  ontology.getAnnotations().size();
        Set<OWLAnnotationAssertionAxiom> annotationAxioms = ontology.getAxioms(AxiomType.ANNOTATION_ASSERTION);
        if (!classes.isEmpty()) {
            ANOnto = (double) (generalAnnotationAxiomCount + annotationAxioms.size()) / classes.size();
        }
        return ANOnto;
    }
}
