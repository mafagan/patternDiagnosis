package kse.findj.module;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import kse.findj.reasoner.JCELOWLOntologyReasoner;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class ELModuleExtracter {
	
	private KSEOWLOntology kseOntology = null;
	private long extractingTime = 0;
	private int moduleSize = 0;
	
	public static void main(String[] args) throws OWLOntologyCreationException {
		ELModuleExtracter moduleExtracter = new ELModuleExtracter();
		moduleExtracter.prepareOntology(new File("not-galen.owl"));
		Set<OWLAxiom> module = moduleExtracter.extractModule("http://www.co-ode.org/ontologies/galen#Coagulase");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();		
		OWLOntology ontology = manager.createOntology(module);
		JCELOWLOntologyReasoner reasoner = new JCELOWLOntologyReasoner(ontology);
		reasoner.doInference();
	}
	
	public void setOntology(KSEOWLOntology ontology){
		this.kseOntology = ontology;
	}
	
	public void prepareOntology(File ontologyFile){
		kseOntology = new KSEOWLOntology(ontologyFile);
	}
	
	/**
	 * Extract module according to input signature.
	 * @param signature input signature
	 * @return the extracted axiom set
	 */
	public Set<OWLAxiom> extractModule(String signature){
		Date start = new Date();
		/*
		 * The axiom set extracted.
		 */
		Set<OWLAxiom> moduleOntology = new HashSet<OWLAxiom>();
		/*
		 * The initial input signature.
		 */
		Set<String> initSig = new HashSet<String>();
		initSig.add(signature);
		
		/*
		 * The queue used to record the active axioms.
		 */
		Queue<OWLAxiom> queue = new LinkedList<OWLAxiom>();
		scanActiveAxiomsAndOfferToQueue(initSig, moduleOntology, queue);
		
		/*
		 * The compared signatures used to compared with
		 * the left-hand signatures of each active axiom.
		 */
		Set<String> comparedSigs = new HashSet<String>();
		comparedSigs.add(signature);
		while(!queue.isEmpty()){
			OWLAxiom axiom = queue.poll();
			
			/*
			 * Get the left-hand signatures from the axiom.
			 */
			Set<Set<String>> axiomLefthandSig = kseOntology.getSignatureFromLeftHandOfAxiom(axiom);
			boolean include = false;
			for(Set<String> leftHandSig : axiomLefthandSig){
				if(comparedSigs.containsAll(leftHandSig)){
					include = true;
					break;
				}
			}
			
			if(include){
				moduleOntology.add(axiom);
				
				/*
				 * Get the right-hand signatures from the axiom.
				 */
				Set<String> axiomRighthandSig = kseOntology.getSignatureFromRightHandOfAxiom(axiom);
			
				for(String sig : axiomRighthandSig){
					if(!comparedSigs.contains(sig)){
						comparedSigs.add(sig);
					}
				}
				
				scanActiveAxiomsAndOfferToQueue(axiomRighthandSig, moduleOntology, queue);				
			}
		}
		
		Date end = new Date();
		//System.out.println("Extracting module for: " + signature);
		//System.out.println("Extracting time: " + (end.getTime() - start.getTime()) + " ms.");
		//System.out.println("Module size: " + moduleOntology.size());
		if(this.extractingTime != -1) this.extractingTime = end.getTime() - start.getTime();
		this.moduleSize = moduleOntology.size();
		
		return moduleOntology;
	}
	
	private void scanActiveAxiomsAndOfferToQueue(
			Set<String> signatures, 
			Set<OWLAxiom> moduleOntology,
			Queue<OWLAxiom> queue){
		
		/*
		 * Get all active axioms according to
		 * the signatures.
		 */
		Set<OWLAxiom> activeAxioms = new HashSet<OWLAxiom>();
		for(String signature : signatures){
			Set<OWLAxiom> activeAxiomSet = kseOntology.getActiveAxioms(signature);
			if(activeAxiomSet != null){
				activeAxioms.addAll(activeAxiomSet);
			}
		}
		
		/*
		 * Offer the axiom which is not in module
		 * to queue.
		 */
		for(OWLAxiom activeAxiom : activeAxioms){
			if(!moduleOntology.contains(activeAxiom)){
				queue.offer(activeAxiom);
			}
		}
	}

	public long getExtractingTime() {
		return extractingTime;
	}

	public int getModuleSize() {
		return moduleSize;
	}

}
