package com.sysu.ss.exp;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;


public class main {
	
	/**
	 *  Exp directory path
	 */
	private static String PATH = "ontology/";
	
	private static String FILE = "3ont.owl";
	
	private static String RES = "res.txt";
	/**
	 * @param args
	 * @throws OWLOntologyCreationException 
	 * @throws OWLOntologyStorageException 
	 */
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
		SubsumptionPatternGenerater patternGenerater = new SubsumptionPatternGenerater(PATH + FILE, PATH + RES);
		patternGenerater.nomalizeTest();
//		patternGenerater.generate();
		
//		OWLOntologyManager inputOntologyManager = OWLManager.createOWLOntologyManager();
//		OWLOntology ont = inputOntologyManager.loadOntologyFromOntologyDocument(new File(PATH+FILE));
//
//		PatternDiagnosis patDia = new PatternDiagnosis(new File(PATH+FILE), null, null);
//		patDia.generate();
	}

}
