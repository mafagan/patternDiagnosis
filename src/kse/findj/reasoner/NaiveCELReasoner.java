package kse.findj.reasoner;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import de.tudresden.inf.lat.jcel.core.algorithm.cel.CelProcessor;
import de.tudresden.inf.lat.jcel.core.graph.IntegerRelationMapImpl;
import de.tudresden.inf.lat.jcel.core.graph.IntegerSubsumerGraph;
import de.tudresden.inf.lat.jcel.coreontology.axiom.NormalizedIntegerAxiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.NormalizedIntegerAxiomFactory;
import de.tudresden.inf.lat.jcel.coreontology.datatype.IntegerEntityManager;
import de.tudresden.inf.lat.jcel.owlapi.translator.TranslationRepository;

public class NaiveCELReasoner {
	
	private OntologyPreprocessor ontoProcessor;
	private CelProcessor processor;
	private Set<NormalizedIntegerAxiom> normalizedIntegerAxiomSet = null;
	private Set<Integer> originalClasses = null;
	private Set<Integer> originalProperties = null;
	private Map<Integer , OWLClass> classMap;                                //整数概念映射
	private Map<OWLClass, Integer> classInvMap = null;
	private Map<Integer, OWLObjectProperty> objectPropertyMap;   //整数属性映射
	private Map<OWLObjectProperty, Integer> objectPropertyInvMap;
	private IntegerSubsumerGraph classGraph = null;
	private IntegerRelationMapImpl relationSet = null;
	
	public NaiveCELReasoner(String fileURL){
		Date start = new Date();
		this.ontoProcessor = new OntologyPreprocessor(fileURL);
		this.normalizedIntegerAxiomSet = ontoProcessor.getNormalizedAxiomSet();	
		this.originalClasses = ontoProcessor.getOriginalClassSet();
		this.originalProperties = ontoProcessor.getOriginalObjectPropertySet();
		IntegerEntityManager integerEntityManager = ontoProcessor.getIntegerEntityManager();
		NormalizedIntegerAxiomFactory normailzedIntegerAxiomFactory = ontoProcessor.getNormalizedIntegerAxiomFactory();
		TranslationRepository translatorReposity = ontoProcessor.getTranslator().getTranslationRepository();
		this.classMap = translatorReposity.getClassMap();
		this.classInvMap = translatorReposity.getClassInvMap();
		this.objectPropertyMap = translatorReposity.getObjectPropertyMap();
		this.objectPropertyInvMap = translatorReposity.getObjectPropertyInvMap();
		Date end = new Date();
		System.out.println("Loading and normalizing time: " + (end.getTime() - start.getTime()) / 1000 + "s.");
		this.processor = new CelProcessor(originalProperties,originalClasses,
				normalizedIntegerAxiomSet,normailzedIntegerAxiomFactory, integerEntityManager);
	}
	
	public void doInference(){
		System.out.println("Reasoning start.");
		Date start = new Date();
		while (processor.isReady() != true){
			processor.process();	
		}
		Date end = new Date();
		System.out.println("Reasoning done.");
		System.out.println("Reasoning cost time: " + (end.getTime() - start.getTime()) / 1000 + "s.");
		
		this.classGraph = this.processor.getClassGraph();
		this.relationSet = this.processor.getRelationSet();
	}

	public OntologyPreprocessor getOntoProcessor() {
		return ontoProcessor;
	}

	public CelProcessor getProcessor() {
		return processor;
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
