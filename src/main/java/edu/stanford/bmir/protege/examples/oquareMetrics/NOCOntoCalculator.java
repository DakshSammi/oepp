package edu.stanford.bmir.protege.examples.oquareMetrics;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import edu.stanford.bmir.protege.examples.view.MetricCalculator;
import edu.stanford.bmir.protege.examples.view.OntologyUtils;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class NOCOntoCalculator implements MetricCalculator {

    @Override
    public double calculate(OWLOntology ontology) {

        double NOCOnto = 0;
        Set<OWLClass> classes = ontology.getClassesInSignature();
        Set<OWLClass> rootClasses = new TreeSet<OWLClass>();

        int subClassofAxiomCount = ontology.getAxiomCount(AxiomType.SUBCLASS_OF);

        for (OWLClass cls : classes) {
            Collection<OWLClass> superClassesofOwlClass = OntologyUtils.classExpr2classes(EntitySearcher.getSuperClasses(cls, ontology));
            if ((superClassesofOwlClass.size() < 1 || superClassesofOwlClass.contains(new OWLDataFactoryImpl().getOWLThing())) && !cls.isOWLThing()) rootClasses.add(cls);
        }

        NOCOnto = (double) subClassofAxiomCount / (classes.size() - rootClasses.size());

        return NOCOnto;
    }
}