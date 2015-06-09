package kse.findj.module;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;

public class KSEOWLOntology {
	
	private OWLOntology ontology = null;
	private Set<OWLAxiom> extractAxiomSet = null;
	/**
	 * Map<Fragment, Set<Axiom>>
	 */
	private Map<String, Set<OWLAxiom>> lefthandIndex = null;
	
	public KSEOWLOntology(File ontology){
		lefthandIndex = new HashMap<String, Set<OWLAxiom>>();
		loadOntology(ontology);
		extractAxioms();
	}
	
	public static void main(String[] args) {
		new KSEOWLOntology(new File("not-galen.owl"));
	}
	
	private void addIndex(OWLAxiom axiom){
		Set<Set<OWLEntity>> eneties = getEntitiesFromLeftHandOfAxiom(axiom);
		for(Set<OWLEntity> entitySet : eneties){
			for(OWLEntity entity : entitySet)
				addItem(entity, axiom);
		}
	}
	
	private void addItem(OWLEntity entity, OWLAxiom axiom){
		if(lefthandIndex.containsKey(entity.getIRI().toString())){
			lefthandIndex.get(entity.getIRI().toString()).add(axiom);
		} else {
			Set<OWLAxiom> axiomSet = new HashSet<OWLAxiom>();
			axiomSet.add(axiom);
			lefthandIndex.put(entity.getIRI().toString(), axiomSet);
		}
	}
	
	private void loadOntology(File ontologyFile){
		Date start = new Date();
		try {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();	
			ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		Date end = new Date();
		System.out.println("Load time: " + (end.getTime() - start.getTime()) + " ms.");
	}
	
	private void extractAxioms(){
		extractAxiomSet = new TreeSet<OWLAxiom>();
		for (OWLAxiom axiom : ontology.getAxioms()) {
			addIndex(axiom);
			if (axiom instanceof OWLDeclarationAxiom) {			
				extractAxiomSet.add(axiom);
			} else if (axiom instanceof OWLEquivalentClassesAxiom) {
				extractAxiomSet.add(axiom);
			} else if (axiom instanceof OWLEquivalentObjectPropertiesAxiom) {				
				extractAxiomSet.add(axiom);
			} else if (axiom instanceof OWLSubClassOfAxiom) {
				extractAxiomSet.add(axiom);
			} else if (axiom instanceof OWLSubObjectPropertyOfAxiom) {
				extractAxiomSet.add(axiom);
			} else if (axiom instanceof OWLSubPropertyChainOfAxiom) {
				extractAxiomSet.add(axiom);
			} else if (axiom instanceof OWLTransitiveObjectPropertyAxiom) {	
				extractAxiomSet.add(axiom);
			}
		}
		System.out.println("Extracted axioms: " + extractAxiomSet.size() + " .");
	}

	private Set<Set<OWLEntity>> getEntitiesFromLeftHandOfAxiom(OWLAxiom axiom){
		Set<Set<OWLEntity>> entitySets = new HashSet<Set<OWLEntity>>();
		if (axiom instanceof OWLSubClassOfAxiom) {
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			OWLSubClassOfAxiom subClassOfAxiom = (OWLSubClassOfAxiom) axiom;
			OWLClassExpression classExpression = subClassOfAxiom.getSubClass();
			Set<OWLEntity> eneties = classExpression.getSignature();		
			entitySet.addAll(eneties);
			entitySets.add(entitySet);
		} else if (axiom instanceof OWLEquivalentClassesAxiom) {
			OWLEquivalentClassesAxiom equivalentClassesAxiom = (OWLEquivalentClassesAxiom) axiom;
			Set<OWLClassExpression> classExpressions = equivalentClassesAxiom.getClassExpressions();
			for(OWLClassExpression classExpression : classExpressions){
				Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
				Set<OWLEntity> eneties = classExpression.getSignature();		
				entitySet.addAll(eneties);
				entitySets.add(entitySet);
			}		
		} else if (axiom instanceof OWLEquivalentObjectPropertiesAxiom) {				
			OWLEquivalentObjectPropertiesAxiom equivalentObjectPropertiesAxiom = (OWLEquivalentObjectPropertiesAxiom) axiom;
			Set<OWLObjectPropertyExpression> objpExpressions = equivalentObjectPropertiesAxiom.getProperties();
			for(OWLObjectPropertyExpression objpExpression : objpExpressions){
				Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
				Set<OWLEntity> eneties = objpExpression.getSignature();		
				entitySet.addAll(eneties);
				entitySets.add(entitySet);
			}	
		} else if (axiom instanceof OWLSubObjectPropertyOfAxiom) {
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			OWLSubObjectPropertyOfAxiom subObjectPropertyOfAxiom = (OWLSubObjectPropertyOfAxiom) axiom;
			OWLObjectPropertyExpression objectPropertyExpression = subObjectPropertyOfAxiom.getSubProperty();
			Set<OWLEntity> eneties = objectPropertyExpression.getSignature();		
			entitySet.addAll(eneties);
			entitySets.add(entitySet);
		} else if (axiom instanceof OWLSubPropertyChainOfAxiom) {
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			OWLSubPropertyChainOfAxiom subPropertyChainOfAxiom = (OWLSubPropertyChainOfAxiom) axiom;
			List<OWLObjectPropertyExpression> objectPropertyExpressions = subPropertyChainOfAxiom.getPropertyChain();	
			for(OWLObjectPropertyExpression objectPropertyExpression : objectPropertyExpressions){
				Set<OWLEntity> eneties = objectPropertyExpression.getSignature();		
				entitySet.addAll(eneties);
			}
			entitySets.add(entitySet);
		} else if (axiom instanceof OWLTransitiveObjectPropertyAxiom) {
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			OWLTransitiveObjectPropertyAxiom transitiveObjectPropertyAxiom = (OWLTransitiveObjectPropertyAxiom) axiom;
			OWLObjectPropertyExpression objectPropertyExpression = transitiveObjectPropertyAxiom.getProperty();	
			Set<OWLEntity> eneties = objectPropertyExpression.getSignature();		
			entitySet.addAll(eneties);
			entitySets.add(entitySet);
		}
		return entitySets;
	}
	
	private Set<Set<OWLEntity>> getEntitiesFromRightHandOfAxiom(OWLAxiom axiom){
		Set<Set<OWLEntity>> entitySets = new HashSet<Set<OWLEntity>>();
		if (axiom instanceof OWLSubClassOfAxiom) {
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			OWLSubClassOfAxiom subClassOfAxiom = (OWLSubClassOfAxiom) axiom;
			OWLClassExpression classExpression = subClassOfAxiom.getSuperClass();
			Set<OWLEntity> eneties = classExpression.getSignature();		
			entitySet.addAll(eneties);
			entitySets.add(entitySet);
		} else if (axiom instanceof OWLEquivalentClassesAxiom) {		
			OWLEquivalentClassesAxiom equivalentClassesAxiom = (OWLEquivalentClassesAxiom) axiom;
			Set<OWLClassExpression> classExpressions = equivalentClassesAxiom.getClassExpressions();
			for(OWLClassExpression classExpression : classExpressions){
				Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
				Set<OWLEntity> eneties = classExpression.getSignature();		
				entitySet.addAll(eneties);
				entitySets.add(entitySet);
			}		
		} else if (axiom instanceof OWLEquivalentObjectPropertiesAxiom) {				
			OWLEquivalentObjectPropertiesAxiom equivalentObjectPropertiesAxiom = (OWLEquivalentObjectPropertiesAxiom) axiom;
			Set<OWLObjectPropertyExpression> objpExpressions = equivalentObjectPropertiesAxiom.getProperties();
			for(OWLObjectPropertyExpression objpExpression : objpExpressions){
				Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
				Set<OWLEntity> eneties = objpExpression.getSignature();		
				entitySet.addAll(eneties);
				entitySets.add(entitySet);
			}	
		} else if (axiom instanceof OWLSubObjectPropertyOfAxiom) {
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			OWLSubObjectPropertyOfAxiom subObjectPropertyOfAxiom = (OWLSubObjectPropertyOfAxiom) axiom;
			OWLObjectPropertyExpression objectPropertyExpression = subObjectPropertyOfAxiom.getSuperProperty();
			Set<OWLEntity> eneties = objectPropertyExpression.getSignature();		
			entitySet.addAll(eneties);
			entitySets.add(entitySet);
		} else if (axiom instanceof OWLSubPropertyChainOfAxiom) {
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			OWLSubPropertyChainOfAxiom subPropertyChainOfAxiom = (OWLSubPropertyChainOfAxiom) axiom;
			OWLObjectPropertyExpression objectPropertyExpression = subPropertyChainOfAxiom.getSuperProperty();
			Set<OWLEntity> eneties = objectPropertyExpression.getSignature();		
			entitySet.addAll(eneties);	
			entitySets.add(entitySet);
		} else if (axiom instanceof OWLTransitiveObjectPropertyAxiom) {
			Set<OWLEntity> entitySet = new HashSet<OWLEntity>();
			OWLTransitiveObjectPropertyAxiom transitiveObjectPropertyAxiom = (OWLTransitiveObjectPropertyAxiom) axiom;
			OWLObjectPropertyExpression objectPropertyExpression = transitiveObjectPropertyAxiom.getProperty();	
			Set<OWLEntity> eneties = objectPropertyExpression.getSignature();		
			entitySet.addAll(eneties);
			entitySets.add(entitySet);
		}
		return entitySets;
	}
	
	public Set<OWLAxiom> getExtractAxiomSet() {
		return extractAxiomSet;
	}

	public Map<String, Set<OWLAxiom>> getLefthandIndex() {
		return lefthandIndex;
	}
	
	public Set<Set<String>> getSignatureFromLeftHandOfAxiom(OWLAxiom axiom){
		Set<Set<String>> signature = new HashSet<Set<String>>();
		Set<Set<OWLEntity>> eneties = getEntitiesFromLeftHandOfAxiom(axiom);
		for(Set<OWLEntity> entitySet : eneties){
			Set<String> signatureSet = new HashSet<String>();
			for(OWLEntity entity : entitySet){
				signatureSet.add(entity.getIRI().toString());
			}
			signature.add(signatureSet);
		}
		return signature;
	}
	
	public Set<String> getSignatureFromRightHandOfAxiom(OWLAxiom axiom){
		Set<String> signature = new HashSet<String>();
		Set<Set<OWLEntity>> eneties = getEntitiesFromRightHandOfAxiom(axiom);
		for(Set<OWLEntity> entitySet : eneties){
			for(OWLEntity entity : entitySet){
				signature.add(entity.getIRI().toString());
			}
		}
		return signature;
	}
	
	public Set<String> getSignature(OWLAxiom axiom){
		Set<Set<OWLEntity>> entities = getEntitiesFromLeftHandOfAxiom(axiom);
		entities.addAll(getEntitiesFromRightHandOfAxiom(axiom));
		Set<String> signature = new HashSet<String>();
		for(Set<OWLEntity> entitySet : entities){
			for(OWLEntity entity : entitySet)
				signature.add(entity.getIRI().toString());
		}
		return signature;
	}
	
	public Set<OWLAxiom> getActiveAxioms(String signature){
		if(lefthandIndex.containsKey(signature)){
			return lefthandIndex.get(signature);
		} else {
			return null;
		}
	}
}
