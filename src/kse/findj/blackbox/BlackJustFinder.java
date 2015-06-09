package kse.findj.blackbox;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kse.findj.reasoner.JCELOWLOntologyReasoner;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class BlackJustFinder {
	
	private Map<Integer, OWLAxiom> axiomIndex;
	private Map<OWLAxiom, Integer> axiomInvIndex;
	private int subsumptionTestNumber = 0;
	private int limit = 10;
	private boolean terminate = false;
	private long computingTime = 0;
	
	private Date start = null;
	
	public BlackJustFinder(Set<OWLAxiom> module){
		this.axiomIndex = new HashMap<Integer, OWLAxiom>();
		this.axiomInvIndex = new HashMap<OWLAxiom, Integer>();
		createIndex(module);
	}

	private void createIndex(Set<OWLAxiom> module){
		int id = 0;
		for(OWLAxiom axiom : module){
			axiomIndex.put(id, axiom);
			axiomInvIndex.put(axiom, id);
			id ++ ;
		}
	}
	
	public Set<Set<OWLAxiom>> hstAllJusts(
			String subClass,
			String superClass,
			Set<OWLAxiom> ontology,
			int limit){
		this.limit = limit;
		this.terminate = false;
		this.subsumptionTestNumber = 0;
		
		this.start = new Date();
		
		Set<Integer> axiomIds = new HashSet<Integer>();
		for(OWLAxiom axiom : ontology) {
			Integer axiomId = axiomInvIndex.get(axiom);
			axiomIds.add(axiomId);
		}
		
		Set<Set<Integer>> justsInInteger = hstAllJusts(
				subClass, 
				superClass, 
				axiomIds, 
				axiomIndex);
		
		Set<Set<OWLAxiom>> justsInOWLAxiom = new HashSet<Set<OWLAxiom>>();
		for(Set<Integer> justInInteger : justsInInteger){
			Set<OWLAxiom> justInOWLAxiom = new HashSet<OWLAxiom>();
			for(Integer axiomId : justInInteger){
				justInOWLAxiom.add(axiomIndex.get(axiomId));
			}
			justsInOWLAxiom.add(justInOWLAxiom);
		}
		
		Date end = new Date();
		if(computingTime != -1){
			computingTime = end.getTime() - start.getTime();
		}
		
		return justsInOWLAxiom;
	}
	
	public Set<OWLAxiom> naiveOneJust(
			String subClass,
			String superClass,
			Set<OWLAxiom> ontology){
		this.subsumptionTestNumber = 0;
		
		Set<Integer> axiomIds = new HashSet<Integer>();
		for(OWLAxiom axiom : ontology) {
			Integer axiomId = axiomInvIndex.get(axiom);
			axiomIds.add(axiomId);
		}
		
		Set<Integer> justInInteger = naiveOneJust(
				subClass, 
				superClass, 
				axiomIds, 
				axiomIndex);
		
		Set<OWLAxiom> justInOWLAxiom = new HashSet<OWLAxiom>();
		for(Integer axiomId : justInInteger){
			justInOWLAxiom.add(axiomIndex.get(axiomId));
		}
		
		return justInOWLAxiom;
	}
	
	private Set<Set<Integer>> hstAllJusts(String subClass,
			String superClass,
			Set<Integer> axiomSet,
			Map<Integer, OWLAxiom> axiomIndex){
		Set<Set<Integer>> justifications = new HashSet<Set<Integer>>();
		Set<Set<Integer>> hittingSets = new HashSet<Set<Integer>>();
		
		Set<Integer> minA = naiveOneJust(
				subClass, superClass, axiomSet, axiomIndex);
		if(minA.size() != 0)
			justifications.add(minA);
			
		for(Integer axiom : minA){
			Set<Integer> onto = new HashSet<Integer>();
			Set<Integer> path = new HashSet<Integer>();
			path.add(axiom);
			onto.addAll(axiomSet);
			onto.remove(axiom);
			expandHst(
					subClass, 
					superClass, 
					onto, 
					path, 
					justifications, 
					hittingSets, 
					axiomIndex);
		}
		
		return justifications;
	}
	
	private void expandHst(
			String subClass,
			String superClass,
			Set<Integer> onto,
			Set<Integer> path,
			Set<Set<Integer>> justifications,
			Set<Set<Integer>> hittingSets,
			Map<Integer, OWLAxiom> axiomIndex){
		if(terminate) return;
		
		Date currentTime = new Date();
		if((currentTime.getTime() - start.getTime())/1000 > 300){
			computingTime = -1;
			//System.out.println((currentTime.getTime() - start.getTime())/1000);
			return;
		}
		/*
		 * early path termination.
		 */
		for(Set<Integer> hittingSet : hittingSets){	
			
			if(path.containsAll(hittingSet)){
				return;
			}
			
			if(hittingSet.containsAll(path)){
				return;
			}
		}
		
		Set<Integer> minA = new HashSet<Integer>();
		
		boolean disjoint = true;
		for(Set<Integer> justification : justifications){	
			for(Integer edage : path){
				if(justification.contains(edage)){
					disjoint = false;
					break;
				}
			}
			
			if(disjoint) {
				minA.addAll(justification);
				break;
			}
		}
		
		if(!disjoint) {
			minA = naiveOneJust(subClass, superClass, onto, axiomIndex);
		}
		
		if(minA.size() != 0) {
			justifications.add(minA);
			if(justifications.size() == limit){
				this.terminate = true;
				return;
			}
			
			for(Integer axiom : minA){
				Set<Integer> newOnto = new HashSet<Integer>();
				Set<Integer> newPath = new HashSet<Integer>();
				newOnto.addAll(onto);
				newOnto.remove(axiom);
				newPath.addAll(path);
				newPath.add(axiom);
				expandHst(
						subClass, 
						superClass, 
						newOnto, 
						newPath, 
						justifications, 
						hittingSets, 
						axiomIndex);
			}
		} else {
			hittingSets.add(path);
		}
	}
	
	private Set<Integer> naiveOneJust(
			String subClass,
			String superClass,
			Set<Integer> axiomSet,
			Map<Integer, OWLAxiom> axiomIndex){
		Set<Integer> justification = new HashSet<Integer>();
		if(!subsumptionCheck(subClass, superClass, 
				axiomSet, axiomIndex)){
			return justification;
		}
		
		justification.addAll(axiomSet);	
		for(Integer axiomId : axiomSet){
			justification.remove(axiomId);
			if(!subsumptionCheck(subClass, superClass, 
					justification, axiomIndex)){
				justification.add(axiomId);
			}
		}
		
		return justification;
	}
	
	private boolean subsumptionCheck(
			String subClass,
			String superClass,
			Set<Integer> axiomSet,
			Map<Integer, OWLAxiom> axiomIndex){
		subsumptionTestNumber ++ ;
		
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for(Integer axiomId : axiomSet){
			axioms.add(axiomIndex.get(axiomId));
		}
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();		
		OWLOntology ontology = null;
		try {
			ontology = manager.createOntology(axioms);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		
		JCELOWLOntologyReasoner reasoner = new JCELOWLOntologyReasoner(ontology);
		reasoner.doInference();
		if(reasoner.subsumptionCheck(subClass, superClass)){
			return true;
		} else {
			return false;
		}
	}

	public int getSubsumptionTestNumber() {
		return subsumptionTestNumber;
	}

	
	public long getComputingTime() {
		return computingTime;
	}

	
}
