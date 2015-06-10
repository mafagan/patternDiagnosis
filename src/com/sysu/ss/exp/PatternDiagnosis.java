package com.sysu.ss.exp;

import java.io.File;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.OntologyAxiomPair;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class PatternDiagnosis {
	private Set<OWLAxiom> patternAxioms;
	private OWLAxiom observation;
	private OWLOntology ontology;
	private PrintStream tt;
	
	public PatternDiagnosis(OWLOntology ontology, OWLAxiom observation, Set<OWLAxiom> patternAxioms) {
		// TODO Auto-generated constructor stub
		this.ontology = ontology;
		this.observation = observation;
		this.patternAxioms = patternAxioms;
		this.tt = System.out;
	}
	
	public void generate() throws OWLOntologyCreationException{
		Set<String> elementDomainSet = new HashSet<String>();
		OWLOntologyManager inputOntologyManager = OWLManager.createOWLOntologyManager();
		OWLOntologyManager outputOntologyManager = OWLManager.createOWLOntologyManager();

		File ontoFile = new File("ontology/owltest.owl");
		// Load your ontology.
		OWLOntology ont = inputOntologyManager.loadOntologyFromOntologyDocument(ontoFile);
		Set<OWLAxiom> axiomSet = ont.getAxioms();
		//tt.println(axiomSet);
		
		Iterator<OWLAxiom> it = axiomSet.iterator();
		
		OWLAxiom axiom = it.next();
		tt.println(axiom);

		//tt.println(axiom.getNestedClassExpressions());
		Set<OWLClassExpression> tmpClassExpression =  axiom.getNestedClassExpressions();
		
		tt.println("\nexpression begin:");
		Iterator<OWLClassExpression> iterator = tmpClassExpression.iterator();
		while (iterator.hasNext()) {
			OWLClassExpression tmp = iterator.next();
			tt.println(tmp);
		}
		tt.println("expression end:\n");

		//tt.println(axiomSet);
	}
}
