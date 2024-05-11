package edu.stanford.bmir.protege.examples.view;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

// To be fixed in the code later on when the class is implemented

public class OntologyUtils {

    public static Collection<OWLClass> classExpr2classes(Collection<OWLClassExpression> classExpressions) {
	Set<OWLClass> classes = new TreeSet<OWLClass>();
	for (OWLClassExpression classExpression : classExpressions) {
	    if(classExpression instanceof OWLClass)
		classes.addAll(classExpression.getClassesInSignature());
	}
	return classes;
    }

    public static Set<OWLClass> getLeafClasses(OWLOntology ontology, Set<OWLClass> classes) {
        return classes.stream()
                .filter(cls -> ontology.getSubClassAxiomsForSuperClass(cls).isEmpty())
                .collect(Collectors.toSet());
    }


    public static int getPathLengthToRoot(OWLClass cls, OWLOntology ontology) {
        int pathLength = cls.isOWLThing() ? 0 : 1;
        while (!cls.isOWLThing()) {
            Set<OWLClass> superClasses = ontology.getSubClassAxiomsForSubClass(cls).stream()
                    .map(OWLSubClassOfAxiom::getSuperClass)
                    .filter(OWLClassExpression::isClassExpressionLiteral)
                    .map(OWLClassExpression::asOWLClass)
                    .collect(Collectors.toSet());
            if (superClasses.isEmpty()) {
                break;
            }
            cls = superClasses.iterator().next();
            pathLength++;
        }
        return pathLength;

    }


}