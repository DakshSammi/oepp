package edu.stanford.bmir.protege.examples.oquareMetrics;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.search.EntitySearcher;

import edu.stanford.bmir.protege.examples.view.MetricCalculator;
import edu.stanford.bmir.protege.examples.view.OntologyUtils;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;


public class RFCOntoCalculator implements MetricCalculator {

    @Override
    public double calculate(OWLOntology ontology) {

        double RFCOnto = 0;
        Set<OWLClass> classes = ontology.getClassesInSignature();

        Set<OWLClass> rootClasses = new TreeSet<OWLClass>();


        int subClassofAxiomCount = ontology.getAxiomCount(AxiomType.SUBCLASS_OF);

        int dataPropAssertionAxiomCount = ontology.getAxiomCount(AxiomType.DATA_PROPERTY_ASSERTION);

        int objectPropOnClasses = 0;

        for (OWLClass owlClass : classes) {

            Collection<OWLClass> superClassesofOwlClass = OntologyUtils
		    .classExpr2classes(EntitySearcher.getSuperClasses(owlClass, ontology));

            if ((superClassesofOwlClass.size() < 1 || superClassesofOwlClass.contains(new OWLDataFactoryImpl().getOWLThing())) && !owlClass.isOWLThing()) rootClasses.add(owlClass);

            for (OWLSubClassOfAxiom classExpr : ontology.getSubClassAxiomsForSubClass(owlClass)) {

                for (OWLEntity entity : classExpr.getSignature()) {
                    if (entity.isOWLObjectProperty()) {
                    objectPropOnClasses++;
                    }
            }
        }
    }


        RFCOnto = (double) ((subClassofAxiomCount)/(classes.size() - rootClasses.size()))*(dataPropAssertionAxiomCount+ objectPropOnClasses);

        return RFCOnto;
    }
}
