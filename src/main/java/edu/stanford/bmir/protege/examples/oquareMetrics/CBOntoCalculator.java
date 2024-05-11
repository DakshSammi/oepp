package edu.stanford.bmir.protege.examples.oquareMetrics;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLClass;
import edu.stanford.bmir.protege.examples.view.MetricCalculator;
import edu.stanford.bmir.protege.examples.view.OntologyUtils;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class CBOntoCalculator implements MetricCalculator {


    @Override
    public double calculate(OWLOntology ontology) {
        double CBOnto = 0;
        int superClassCount = 0;
        Set<OWLClass> classes = ontology.getClassesInSignature();
        Set<OWLClass> rootClasses = new TreeSet<OWLClass>();

        for (OWLClass cls : classes) {
            Collection<OWLClass> superClassesofOwlClass = OntologyUtils
		    .classExpr2classes(EntitySearcher.getSuperClasses(cls, ontology));

            superClassCount += superClassesofOwlClass.size();

            if ((superClassesofOwlClass.size() < 1 || superClassesofOwlClass.contains(new OWLDataFactoryImpl().getOWLThing())) && !cls.isOWLThing()) rootClasses.add(cls);
        }

        CBOnto = (double) superClassCount / (classes.size() - rootClasses.size());

        return CBOnto;
    }
}