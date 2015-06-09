package com.sysu.ss.exp;

import java.awt.geom.Path2D;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kse.findj.edg.core.ExplanationRoutine;
import kse.findj.edg.core.MasterRoutine;
import kse.findj.edg.data.AxiomGCI0;
import kse.findj.edg.data.AxiomGCI1;
import kse.findj.edg.data.AxiomGCI2;
import kse.findj.edg.data.AxiomGCI3;
import kse.findj.edg.data.AxiomR;
import kse.findj.edg.data.AxiomRI2;
import kse.findj.edg.data.AxiomRI3;
import kse.findj.edg.data.AxiomS;
import kse.findj.edg.data.MyAxiom;
import kse.findj.edg.data.MyAxiomRepository;
import kse.findj.reasoner.RuleBasedCELReasoner;

import org.apache.log4j.PatternLayout;
import org.hamcrest.core.Is;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
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


import de.tudresden.inf.lat.jcel.core.graph.IntegerSubsumerGraph;

public class SubsumptionPatternGenerater {
	
	private File ontoFile;
	
	private File outputFile;
	
	private HashMap<OWLClass, HashSet<OWLClass>> classHierarchyMap = new HashMap<OWLClass, HashSet<OWLClass>>();;
	
	public SubsumptionPatternGenerater(String path, String outFilePath){
		ontoFile = new File(path);
		outputFile = new File(outFilePath);
	}
	
	public void generate() throws OWLOntologyCreationException, OWLOntologyStorageException{
		OWLOntologyManager inputOntologyManager = OWLManager.createOWLOntologyManager();
		OWLOntologyManager outputOntologyManager = OWLManager.createOWLOntologyManager();

		// Load your ontology.
		OWLOntology ont = inputOntologyManager.loadOntologyFromOntologyDocument(ontoFile);

		// Create an ELK reasoner.
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ont);

		// Classify the ontology.
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		// To generate an inferred ontology we use implementations of
		// inferred axiom generators
		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		gens.add(new InferredSubClassAxiomGenerator());
		//gens.add(new InferredClassAssertionAxiomGenerator());
		//gens.add(new InferredEquivalentClassAxiomGenerator());

	
		OWLDataFactory dataFactory = inputOntologyManager.getOWLDataFactory();
//		OWLClass newName = dataFactory.getOWLClass(IRI.create("http://www.semanticweb.org/winter/ontologies/2015/4/untitled-ontology-11#D"));
//		
//		NodeSet<OWLClass> reSet = reasoner.getSubClasses(newName, true);
//		Iterator<Node<OWLClass>> tt = reSet.iterator();
//		while (tt.hasNext()) {
//			System.out.println(tt.next());
//		}
		
		// Put the inferred axioms into a fresh empty ontology.
		OWLOntology infOnt = outputOntologyManager.createOntology();
		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
		iog.fillOntology(outputOntologyManager, infOnt);

//		outputOntologyManager.saveOntology(infOnt, new OWLFunctionalSyntaxOntologyFormat(), IRI.create((new File("/Users/winter/output.owl").toURI())));
		
		// Terminate the worker threads used by the reasoner.
		reasoner.dispose();
		
		
		Set<OWLAxiom> axioms = infOnt.getAxioms();
		Iterator<OWLAxiom> iterator = axioms.iterator();
		
		while (iterator.hasNext()) {
			OWLSubClassOfAxiom subAxiom = (OWLSubClassOfAxiom) iterator.next();
			
			//System.out.println(subAxiom.getSubClass().asOWLClass());
			System.out.println(subAxiom);
			
			Set<OWLClass> subClassSet = classHierarchyMap.get(subAxiom.getSuperClass().asOWLClass());
			
			if (subClassSet == null) {
				classHierarchyMap.put(subAxiom.getSuperClass().asOWLClass(), new HashSet<OWLClass>());
			}
			classHierarchyMap.get(subAxiom.getSuperClass()).add(subAxiom.getSubClass().asOWLClass());
		}
		
		RuleBasedCELReasoner celReasoner = new RuleBasedCELReasoner(ontoFile);
		celReasoner.doInference();
		
		MyAxiomRepository repository = new MyAxiomRepository(celReasoner.getClassGraph(), celReasoner.getRelationSet(), celReasoner.getNormalizedIntegerAxiomSet());
		repository.createIndex();
		
		Map<Integer, OWLClass> classMap = celReasoner.getClassMap();
		
		Iterator<Integer> classMapIterator = classMap.keySet().iterator();
		while (classMapIterator.hasNext()) {
			Integer keyInteger = classMapIterator.next();
			System.out.println(keyInteger + " " + classMap.get(keyInteger));
		}
		
		IntegerSubsumerGraph classGragh = repository.getClassGraph();
		
		Set<ExplanationRoutine> patternSet = new HashSet<ExplanationRoutine>();
		
		Collection<Integer> elements = classGragh.getElements();
		
		Iterator<Integer> elementIterator = elements.iterator();
		
		//System.out.println(elements);
		System.out.println("\nJustifications:\n");
		while (elementIterator.hasNext()) {
			Integer curElement = elementIterator.next();
			/*
			 *  skip "owl:thing" and "owl:nothing"
			 */
			if (curElement == 0 || curElement == 1) continue; 
			Collection<Integer> superCollection = classGragh.getSubsumers(curElement);
			
			//System.out.println(curElement + "\n" + superCollection + "\n");
			Iterator<Integer> superIterator = superCollection.iterator();
			
			while (superIterator.hasNext()) {
				Integer superInteger = superIterator.next();
				
				/*
				 *  skip pattern like "A[=A"
				 */
				if(superInteger == curElement) continue;
				
				if (superInteger == 0 || superInteger == 1) continue; 
				
				System.out.println(curElement + " SubClassOf " + superInteger);

				Set<ExplanationRoutine> tempSet = getJustification(repository, curElement, superInteger);
				
				Iterator<ExplanationRoutine> subsumIterator = tempSet.iterator();
				
				while(subsumIterator.hasNext()){
					System.out.println(subsumIterator.next());
				}
				System.out.print("\n");
				
				patternSet.addAll(tempSet);
			}
		}
		
		System.out.println("\nCandidate patterns:\n");
		Iterator patternIterator = patternSet.iterator();
		while (patternIterator.hasNext()) {
			ExplanationRoutine exp = (ExplanationRoutine) patternIterator.next();
			//System.out.println(exp.getClass().toString());
			System.out.println(exp + "\n");
			
		}
		
		/*
		 *  ExplanationRoutine: candidate pattern
		 *  Integer: pattern count
		 */
		Map<ExplanationRoutine, Integer> resMap = new HashMap<ExplanationRoutine, Integer>();
		
		List<ExplanationRoutine> pattenList = new ArrayList<ExplanationRoutine>(patternSet);
		
		for (int i = 0; i < pattenList.size(); i++) {
			for (int j = i+1; j < pattenList.size(); j++) {
				System.out.println("Comparing:\n" + pattenList.get(i) + "\n" + pattenList.get(j) );
				if (this.isExplanationRoutineEqual(pattenList.get(i), pattenList.get(j))) {
					System.out.println("Equal\n");
					if (resMap.containsKey(pattenList.get(i))) {
						resMap.put(pattenList.get(i), resMap.get(pattenList.get(i))+1);

						pattenList.remove(j);
						j = j - 1;
					}else {
						resMap.put(pattenList.get(i), 2);
						pattenList.remove(j);
						j = j - 1;
					}
				}else {
					System.out.println("Not equal:\n");
					if (!resMap.containsKey(pattenList.get(i))) {
						resMap.put(pattenList.get(i), 1);
					}
					
				}
				
			}
		}
		
		/*
		 * Do not miss the last element
		 * 
		 */
		if (!resMap.containsKey(pattenList.get(pattenList.size()-1))) {
			resMap.put(pattenList.get(pattenList.size()-1), 1);
		}
		
		//System.out.println("\nResult pattern supports:");
		
		try {
			FileWriter writer = new FileWriter(outputFile, true);
			Iterator<ExplanationRoutine> resMapIterator = resMap.keySet().iterator();
			while (resMapIterator.hasNext()) {
				ExplanationRoutine mapKey = resMapIterator.next();
				
				//System.out.println();
				
				writer.write(mapKey + ": " + resMap.get(mapKey) + "\n");
			}
			writer.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public Set<ExplanationRoutine> getJustification(MyAxiomRepository repository, Integer subClass, Integer superClass){
		MasterRoutine masterRoutine = new MasterRoutine(repository, 2, subClass, superClass);
		
		masterRoutine.computeResult();
		
		Set<ExplanationRoutine> justifications = masterRoutine.getJustifications();
		
		return justifications;
	}
	
	public boolean isExplanationRoutineEqual(ExplanationRoutine a, ExplanationRoutine b){
		
		List<Integer> patternAList = new ArrayList<Integer>(a.getOriginalAxioms());
		List<Integer> patternBList = new ArrayList<Integer>(b.getOriginalAxioms());
		
		if (patternAList.size() != patternBList.size()) return false;
		
		Map<String, Set<Integer>> axiomTypeMapA = new HashMap<String, Set<Integer>>();
		Map<String, Set<Integer>> axiomTypeMapB = new HashMap<String, Set<Integer>>();
		
		MasterRoutine mrA = a.getMasterRoutine();
		MasterRoutine mrB = b.getMasterRoutine();
		
		Set<Integer> domainAIntegers = new HashSet<Integer>();
		Set<Integer> domainBIntegers = new HashSet<Integer>();
		
		for(Integer axiomID : patternAList){
			MyAxiom axiom = mrA.getRecorder().getAxiomFromId(axiomID);
			domainAIntegers.addAll(axiom.getDomainElementSet());
			
			String className = axiom.getClass().toString();
			
			if(axiomTypeMapA.containsKey(className)) axiomTypeMapA.get(className).add(axiomID);
			else{
				axiomTypeMapA.put(className, new HashSet<Integer>());
				axiomTypeMapA.get(className).add(axiomID);
			}
		}
		
		for(Integer axiomID : patternBList){
			MyAxiom axiom = mrB.getRecorder().getAxiomFromId(axiomID);
			domainBIntegers.addAll(axiom.getDomainElementSet());
			
			String className = axiom.getClass().toString();
			
			if(axiomTypeMapB.containsKey(className)) axiomTypeMapB.get(className).add(axiomID);
			else{
				axiomTypeMapB.put(className, new HashSet<Integer>());
				axiomTypeMapB.get(className).add(axiomID);
			}
		}
		
		/* key set compare */
		if (!axiomTypeMapA.keySet().equals(axiomTypeMapB.keySet())) return false;
		
		/* value size compare */
		Iterator<String> keyIterator = axiomTypeMapA.keySet().iterator();
		
		while(keyIterator.hasNext()){
			String curKey = keyIterator.next();
			if (axiomTypeMapA.get(curKey).size() != axiomTypeMapB.get(curKey).size()) return false;
		}
		
		/* element domain complare */
		if (domainAIntegers.size() != domainBIntegers.size()) return false;
		
		List<Integer> domainAList = new ArrayList<Integer>(domainAIntegers);
		List<Integer> domainBList = new ArrayList<Integer>(domainBIntegers);
		
		SimilarPattern similarPattern = new SimilarPattern();
		similarPattern.domainAList = domainAList;
		similarPattern.domainBList = domainBList;
		similarPattern.axiomTypeMapA = axiomTypeMapA;
		similarPattern.axiomTypeMapB = axiomTypeMapB;
		similarPattern.masterRoutineA = a.getMasterRoutine();
		similarPattern.masterRoutineB = b.getMasterRoutine();
		
		
		return domainPermutation(0, similarPattern);
	}
	
	
	private boolean domainPermutation(int cur, SimilarPattern similarPattern){
		
		if(cur >= similarPattern.domainAList.size()){
			Map<Integer, Integer> elementMap = new HashMap<Integer, Integer>();
			
			for (int i = 0; i < similarPattern.domainAList.size(); i++) {
				elementMap.put(similarPattern.domainBList.get(i), similarPattern.domainAList.get(i));
			}
			
			Iterator<String> axiomTypeMapIterator = similarPattern.axiomTypeMapA.keySet().iterator();
			while (axiomTypeMapIterator.hasNext()) {
				String className = axiomTypeMapIterator.next();
				Set<Integer> similarAxiomsA = similarPattern.axiomTypeMapA.get(className);
				Set<Integer> similarAxiomsB = similarPattern.axiomTypeMapB.get(className);
				
				List<MyAxiom> axiomAList = new ArrayList<MyAxiom>();				
				Iterator<Integer> saIterator = similarAxiomsA.iterator();
				
				while (saIterator.hasNext()) {
					Integer axiomID = saIterator.next();
					axiomAList.add(similarPattern.masterRoutineA.getRecorder().getAxiomFromId(axiomID));
				}
				
				List<MyAxiom> axiomBList = new ArrayList<MyAxiom>();
				saIterator = similarAxiomsB.iterator();
				
				while (saIterator.hasNext()) {
					Integer axiomID = saIterator.next();
					MyAxiom originalAxiom = similarPattern.masterRoutineB.getRecorder().getAxiomFromId(axiomID);
					
					if (originalAxiom instanceof AxiomGCI0) {
						AxiomGCI0 axiomGCI0 = (AxiomGCI0) originalAxiom;
						Integer subInteger = elementMap.get(axiomGCI0.getSubClass());
						Integer supInteger = elementMap.get(axiomGCI0.getSuperClass());
						
						AxiomGCI0 mapAxiomGCI0 = new AxiomGCI0(subInteger, supInteger);
						axiomBList.add(mapAxiomGCI0);
					}else if (originalAxiom instanceof AxiomGCI1) {
						AxiomGCI1 axiomGCI1 = (AxiomGCI1) originalAxiom;
						Integer sublInteger = elementMap.get(axiomGCI1.getLeftSubClass());
						Integer subrInteger = elementMap.get(axiomGCI1.getRightSubClass());
						Integer supInteger = elementMap.get(axiomGCI1.getSuperClass());
						
						AxiomGCI1 mapAxiomGCI1 = new AxiomGCI1(sublInteger, subrInteger, supInteger);
						axiomBList.add(mapAxiomGCI1);
					}else if (originalAxiom instanceof AxiomGCI2) {
						AxiomGCI2 axiomGCI2 = (AxiomGCI2) originalAxiom;
						Integer subInteger = elementMap.get(axiomGCI2.getSubClass());
						Integer propInteger = elementMap.get(axiomGCI2.getPropertyInSuperClass());
						Integer supInteger = elementMap.get(axiomGCI2.getClassInSuperClass());
						
						AxiomGCI2 mapAxiomGCI2 = new AxiomGCI2(subInteger, propInteger, supInteger);
						axiomBList.add(mapAxiomGCI2);
					}else if (originalAxiom instanceof AxiomGCI3) {
						AxiomGCI3 axiomGCI3 = (AxiomGCI3) originalAxiom;
						Integer subInteger = elementMap.get(axiomGCI3.getClassInSubClass());
						Integer propInteger = elementMap.get(axiomGCI3.getPropertyInSubClass());
						Integer supInteger = elementMap.get(axiomGCI3.getSuperClass());
						
						AxiomGCI3 mapAxiomGCI3 = new AxiomGCI3(propInteger, subInteger, supInteger);
						axiomBList.add(mapAxiomGCI3);
					}else if (originalAxiom instanceof AxiomR) {
						AxiomR axiomR = (AxiomR) originalAxiom;
						Integer subInteger = elementMap.get(axiomR.getSubClass());
						Integer propInteger = elementMap.get(axiomR.getPropertyInSuperClass());
						Integer supInteger = elementMap.get(axiomR.getClassInSuperClass());
						
						AxiomR mapAxiomR = new AxiomR(subInteger, propInteger, supInteger);
						axiomBList.add(mapAxiomR);
					}else if (originalAxiom instanceof AxiomRI2) {
						AxiomRI2 axiomRI2 = (AxiomRI2) originalAxiom;
						Integer subPropInteger = elementMap.get(axiomRI2.getSubProperty());
						Integer supPropInteger = elementMap.get(axiomRI2.getSuperProperty());
						
						AxiomRI2 mapAxiomRI2 = new AxiomRI2(subPropInteger, supPropInteger);
						axiomBList.add(mapAxiomRI2);
					}else if (originalAxiom instanceof AxiomRI3){
						AxiomRI3 axiomRI3 = (AxiomRI3) originalAxiom;
						Integer sublPropInteger = elementMap.get(axiomRI3.getLeftSubProperty());
						Integer subrPropInteger = elementMap.get(axiomRI3.getRightSubProperty());
						Integer supPropInteger = elementMap.get(axiomRI3.getSuperProperty());
						
						AxiomRI3 mapAxiomRI3 = new AxiomRI3(sublPropInteger, subrPropInteger, supPropInteger);
						axiomBList.add(mapAxiomRI3);
					}else if (originalAxiom instanceof AxiomS) {
						AxiomS axiomS = (AxiomS) originalAxiom;
						Integer subInteger = elementMap.get(axiomS.getSubClass());
						Integer supInteger = elementMap.get(axiomS.getSuperClass());
						
						AxiomS mapAxiomS = new AxiomS(subInteger, supInteger);
						axiomBList.add(mapAxiomS);
					}
					
				}
				
				boolean res = this.axiomMapPermutation(0, axiomAList, axiomBList);
				
				
			}
			
			return true;
		}else{
			for(int i = cur; i < similarPattern.domainBList.size(); i++){
				Integer temp = similarPattern.domainBList.get(i);
				similarPattern.domainBList.set(i, similarPattern.domainBList.get(cur));
				similarPattern.domainBList.set(cur, temp);
				
				boolean tempRes = domainPermutation(cur+1, similarPattern);
				
				if (tempRes) return true;
				
				temp = similarPattern.domainBList.get(i);
				similarPattern.domainBList.set(i, similarPattern.domainBList.get(cur));
				similarPattern.domainBList.set(cur, temp);

			}
		}
		
		return false;
	}
	
	private boolean axiomMapPermutation(int cur, List<MyAxiom> axiomAList, List<MyAxiom> axiomBList){
		
		if(cur >= axiomAList.size()){
			for (int i = 0; i < axiomAList.size(); i++) {
				if (!axiomAList.get(i).toString().equals(axiomBList.get(i).toString())) return false;
			}
			
			return true;
		}else{
			for (int i = cur; i < axiomAList.size(); i++) {
				MyAxiom axiomTemp = axiomBList.get(i);
				axiomBList.set(i, axiomBList.get(cur));
				axiomBList.set(cur, axiomTemp);
				
				boolean tempRes = axiomMapPermutation(cur+1, axiomAList, axiomBList);
				
				if (tempRes) return true;
				
				axiomTemp = axiomAList.get(i);
				axiomBList.set(i, axiomBList.get(cur));
				axiomBList.set(cur, axiomTemp);
			}
		}
		return false;
	}
}

class SimilarPattern{
	public MasterRoutine masterRoutineA;
	public MasterRoutine masterRoutineB;

	public List<Integer> domainAList;
	public List<Integer> domainBList;
	
	public Map<Integer, Integer> elementMap;
	public Map<String, Set<Integer>> axiomTypeMapA;
	public Map<String, Set<Integer>> axiomTypeMapB;
}