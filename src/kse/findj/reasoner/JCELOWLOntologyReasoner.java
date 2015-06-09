package kse.findj.reasoner;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import de.tudresden.inf.lat.jcel.core.algorithm.rulebased.RuleBasedProcessor;
import de.tudresden.inf.lat.jcel.core.graph.IntegerRelationMapImpl;
import de.tudresden.inf.lat.jcel.core.graph.IntegerSubsumerGraph;
import de.tudresden.inf.lat.jcel.coreontology.axiom.NormalizedIntegerAxiom;
import de.tudresden.inf.lat.jcel.owlapi.main.JcelReasoner;
import de.tudresden.inf.lat.jcel.owlapi.translator.TranslationRepository;
import de.tudresden.inf.lat.jcel.reasoner.main.RuleBasedReasoner;

public class JCELOWLOntologyReasoner {
	
	private RuleBasedReasoner ruleBasedReasoner = null;
	private Set<NormalizedIntegerAxiom> normalizedIntegerAxiomSet = null;
	private Set<Integer> originalClasses = null;
	private Set<Integer> originalProperties = null;
	private Map<Integer , OWLClass> classMap = null;                    //整数概念映射
	private Map<OWLClass, Integer> classInvMap = null;
	private Map<String, Integer> classInvMapInString = null;
	private Map<Integer, OWLObjectProperty> objectPropertyMap;          //整数属性映射
	private Map<OWLObjectProperty, Integer> objectPropertyInvMap;
	private IntegerSubsumerGraph classGraph = null;
	private IntegerRelationMapImpl relationSet = null;
	
	public JCELOWLOntologyReasoner(OWLOntology ontology){
		//Date start = new Date();

		JcelReasoner reasoner = new JcelReasoner(ontology, false);
		ruleBasedReasoner = (RuleBasedReasoner) reasoner.getReasoner(); 
			
		normalizedIntegerAxiomSet = ruleBasedReasoner.getNormalizedIntegerAxiomSet();
		originalClasses = ruleBasedReasoner.getOriginalClassSet();
		originalProperties = ruleBasedReasoner.getOriginalObjectPropertySet();
		TranslationRepository translatorReposity = reasoner.getTranslator().getTranslationRepository();
		classMap = translatorReposity.getClassMap();
		classInvMap = translatorReposity.getClassInvMap();
		objectPropertyMap = translatorReposity.getObjectPropertyMap();
		objectPropertyInvMap = translatorReposity.getObjectPropertyInvMap();
		
		Set<OWLClass> classes = classInvMap.keySet();
		classInvMapInString = new HashMap<String, Integer>();
		for(OWLClass owlClass : classes){
			classInvMapInString.put(owlClass.getIRI().toString(), classInvMap.get(owlClass));
		}
				
		//Date end = new Date();
		//System.out.println("Loading and normalizing time: " + (end.getTime() - start.getTime()) / 1000 + "s.");	
	}
	
	public Integer getIntegerClass(String uri){
		return classInvMapInString.get(uri);
	}
	
	public void doInference(){
		System.out.println("Reasoning start.");
		Date start = new Date();
		ruleBasedReasoner.classify();
		Date end = new Date();
		System.out.println("Reasoning done.");
		System.out.println("Reasoning cost time: " + (end.getTime() - start.getTime()) / 1000 + "s.");
		
		this.classGraph = ((RuleBasedProcessor)this.ruleBasedReasoner.getProcessor()).getClassGraph();
		this.relationSet = ((RuleBasedProcessor)this.ruleBasedReasoner.getProcessor()).getRelationSet();
	}

	public boolean subsumptionCheck(String subClass, String superClass){
		Integer subCls = classInvMapInString.get(subClass);
		Integer superCls = classInvMapInString.get(superClass);
		
		if(subCls == null || superCls == null)
			return false;
		
		Collection<Integer> elements = classGraph.getElements();
		if(elements.contains(subCls)){
			Collection<Integer> superClasses = classGraph.getSubsumers(subCls);
			if(superClasses.contains(superCls)){
				return true;
			}
		}
		
		return false;
	}
	
	public Set<NormalizedIntegerAxiom> getNormalizedIntegerAxiomSet() {
		return normalizedIntegerAxiomSet;
	}

	public Set<Integer> getOriginalClasses() {
		return originalClasses;
	}

	public Set<Integer> getOriginalProperties() {
		return originalProperties;
	}

	public Map<Integer, OWLClass> getClassMap() {
		return classMap;
	}

	public Map<OWLClass, Integer> getClassInvMap() {
		return classInvMap;
	}

	public Map<Integer, OWLObjectProperty> getObjectPropertyMap() {
		return objectPropertyMap;
	}

	public Map<OWLObjectProperty, Integer> getObjectPropertyInvMap() {
		return objectPropertyInvMap;
	}

	public IntegerSubsumerGraph getClassGraph() {
		return classGraph;
	}

	public IntegerRelationMapImpl getRelationSet() {
		return relationSet;
	}

}
