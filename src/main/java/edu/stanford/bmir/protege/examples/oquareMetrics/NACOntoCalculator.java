package edu.stanford.bmir.protege.examples.oquareMetrics;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import edu.stanford.bmir.protege.examples.view.MetricCalculator;
import edu.stanford.bmir.protege.examples.view.OntologyUtils;

public class NACOntoCalculator implements MetricCalculator {

    @Override
    public double calculate(OWLOntology ontology) {
        double NACOnto = 0;
        Set<OWLClass> leafClasses = new TreeSet<OWLClass>();
        Set<OWLClass> classes = ontology.getClassesInSignature();

        for (OWLClass owlClass : classes) {
        Collection<OWLClassExpression> subClassExpr = EntitySearcher.getSubClasses(owlClass, ontology);
	    Collection<OWLClass> subClasses = OntologyUtils.classExpr2classes(subClassExpr);

        if (subClasses.size() < 1) {
            leafClasses.add(owlClass);}
        }

        int superClassesOfLeafClasses = 0;
	    for (OWLClass owlClass : leafClasses) {
	        Collection<OWLClassExpression> superClassOfLeafClass = EntitySearcher.getSuperClasses(owlClass, ontology);
	        superClassesOfLeafClasses += OntologyUtils.classExpr2classes(superClassOfLeafClass).size();
	    }

        if (!classes.isEmpty()) {
            NACOnto = (double) superClassesOfLeafClasses / leafClasses.size() ;
        }

        return NACOnto;

    }

}
