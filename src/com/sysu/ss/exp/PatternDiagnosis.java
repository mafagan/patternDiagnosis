package com.sysu.ss.exp;

import java.io.File;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import kse.findj.edg.data.AxiomGCI0;
import kse.findj.edg.data.MyAxiom;
import kse.findj.reasoner.RuleBasedCELReasoner;

import org.coode.owlapi.manchesterowlsyntax.OntologyAxiomPair;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;

import de.tudresden.inf.lat.jcel.coreontology.axiom.GCI0Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.GCI1Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.GCI2Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.GCI3Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.NormalizedIntegerAxiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.RI1Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.RI2Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.RI3Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.RangeAxiom;
import de.tudresden.inf.lat.jcel.owlapi.main.JcelReasoner;

public class PatternDiagnosis {
	private Set<MyAxiom> patternAxioms;
	private AxiomGCI0 observation;
	private File ontFile;
	private PrintStream tt;
	
	public PatternDiagnosis(File ontFile, AxiomGCI0 observation, Set<MyAxiom> patternAxioms) {
		// TODO Auto-generated constructor stub
		this.ontFile = ontFile;
		this.observation = observation;
		this.patternAxioms = patternAxioms;
		this.tt = System.out;
	}
	
	public void generate() throws OWLOntologyCreationException{
//		Set<String> elementDomainSet = new HashSet<String>();
//		OWLOntologyManager inputOntologyManager = OWLManager.createOWLOntologyManager();
//		OWLOntologyManager outputOntologyManager = OWLManager.createOWLOntologyManager();
//
//		File ontoFile = new File("ontology/owltest.owl");
//		// Load your ontology.
//		OWLOntology ont = inputOntologyManager.loadOntologyFromOntologyDocument(ontoFile);
//		Set<OWLAxiom> axiomSet = ont.getAxioms();
//		//tt.println(axiomSet);
//		
//		Iterator<OWLAxiom> it = axiomSet.iterator();
//		
//		OWLSubClassOfAxiom axiom = (OWLSubClassOfAxiom)it.next();
//		OWLObjectIntersectionOf obAxiom = (OWLObjectIntersectionOf) axiom.getSubClass();
//		tt.println(obAxiom.getOperands());
//		tt.println(axiom.getSuperClass());
//
//		//tt.println(axiom.getNestedClassExpressions());
//		Set<OWLClassExpression> tmpClassExpression =  axiom.getNestedClassExpressions();
		
//		tt.println("\nexpression begin:");
//		Iterator<OWLClassExpression> iterator = tmpClassExpression.iterator();
//		while (iterator.hasNext()) {
//			OWLClassExpression tmp = iterator.next();
//			tt.println();
//		}
//		tt.println("expression end:\n");

		//tt.println(axiomSet);
		RuleBasedCELReasoner celReasoner = new RuleBasedCELReasoner(this.ontFile);
		
		Set<NormalizedIntegerAxiom> tAxiom = celReasoner.getNormalizedIntegerAxiomSet();
		Set<Integer> classDomainSet = new HashSet<Integer>();
		Set<Integer> propertyDomainSet = new HashSet<Integer>(); 
		
		Iterator<NormalizedIntegerAxiom> iterator = tAxiom.iterator();
		while (iterator.hasNext()) {
			NormalizedIntegerAxiom tmpAxiom = iterator.next();
			
			if (tmpAxiom instanceof GCI0Axiom) {
				GCI0Axiom temp = (GCI0Axiom)tmpAxiom;
				classDomainSet.add(temp.getSubClass());
				classDomainSet.add(temp.getSuperClass());
			}else if (tmpAxiom instanceof GCI1Axiom) {
				GCI1Axiom temp = (GCI1Axiom)tmpAxiom;
				classDomainSet.add(temp.getLeftSubClass());
				classDomainSet.add(temp.getRightSubClass());
				classDomainSet.add(temp.getSuperClass());
			}else if (tmpAxiom instanceof GCI2Axiom) {
				GCI2Axiom temp = (GCI2Axiom)tmpAxiom;
				classDomainSet.add(temp.getSubClass());
				classDomainSet.add(temp.getClassInSuperClass());
				propertyDomainSet.add(temp.getPropertyInSuperClass());
				
			}else if (tmpAxiom instanceof GCI3Axiom) {
				GCI3Axiom temp = (GCI3Axiom)tmpAxiom;
				classDomainSet.add(temp.getClassInSubClass());
				classDomainSet.add(temp.getSuperClass());
				propertyDomainSet.add(temp.getPropertyInSubClass());
			}else if (tmpAxiom instanceof RI1Axiom) {
				RI1Axiom temp = (RI1Axiom)tmpAxiom;
				propertyDomainSet.add(temp.getSuperProperty());
			}else if (tmpAxiom instanceof RI2Axiom) {
				RI2Axiom temp = (RI2Axiom)tmpAxiom;
				propertyDomainSet.add(temp.getSubProperty());
				propertyDomainSet.add(temp.getSuperProperty());
			}else if (tmpAxiom instanceof RI3Axiom) {
				RI3Axiom temp = (RI3Axiom)tmpAxiom;
				propertyDomainSet.add(temp.getLeftSubProperty());
				propertyDomainSet.add(temp.getRightSubProperty());
				propertyDomainSet.add(temp.getSuperProperty());
				
			}else if (tmpAxiom instanceof RangeAxiom) {
				RangeAxiom temp = (RangeAxiom)tmpAxiom;
				propertyDomainSet.add(temp.getProperty());
				propertyDomainSet.add(temp.getRange());
			}
			
		
			//elementDomain.addAll(tmpAxiom.getDomainElementSet());
		}
		System.out.println(propertyDomainSet);
	}
}
