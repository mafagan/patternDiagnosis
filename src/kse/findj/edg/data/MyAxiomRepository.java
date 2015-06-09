package kse.findj.edg.data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kse.findj.util.ObjectIOUtil;

import de.tudresden.inf.lat.jcel.core.graph.IntegerRelationMapImpl;
import de.tudresden.inf.lat.jcel.core.graph.IntegerSubsumerGraph;
import de.tudresden.inf.lat.jcel.coreontology.axiom.GCI0Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.GCI1Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.GCI2Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.GCI3Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.NormalizedIntegerAxiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.RI2Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.RI3Axiom;

public class MyAxiomRepository implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IntegerSubsumerGraph classGraph = null;
	private IntegerRelationMapImpl relationSet = null;
	private Set<NormalizedIntegerAxiom> normalizedIntegerAxiomSet = null;
	
	private final Map<String, Integer> normalizedGCISHA1ToInteger = new HashMap<String, Integer>();
	private final Map<Integer, String> normalizedGCIIntegerToSHA1 = new HashMap<Integer, String>();
	private final Map<String, Integer> normalizedRISHA1ToInteger = new HashMap<String, Integer>();
	private final Map<Integer, String> normalizedRIIntegerToSHA1 = new HashMap<Integer, String>();
	
	/**
	 * added by Huaguan, Ma 
	 */
	private final Map<Integer, String> normalizedGCIIntegerToAxiom;
	
	
	private final Map<Integer, Set<Integer>> gci0Sup = new HashMap<Integer, Set<Integer>>();
	private final Map<Integer, Set<GCI1Axiom>> gci1Sup = new HashMap<Integer, Set<GCI1Axiom>>();
	private final Map<Integer, Map<Integer, Set<Integer>>> gci2Sup = new HashMap<Integer, Map<Integer,Set<Integer>>>();
	private final Map<Integer, Map<Integer, Set<Integer>>> gci3Sup = new HashMap<Integer, Map<Integer,Set<Integer>>>();
	private final Map<Integer, Set<Integer>> ri2Sup = new HashMap<Integer, Set<Integer>>();
	private final Map<Integer, Set<RI3Axiom>> ri3Sup = new HashMap<Integer, Set<RI3Axiom>>();
	
	public MyAxiomRepository(
			IntegerSubsumerGraph classGraph,
			IntegerRelationMapImpl relationSet,
			Set<NormalizedIntegerAxiom> normalizedIntegerAxiomSet){
		this.classGraph = classGraph;
		this.relationSet = relationSet;
		this.normalizedIntegerAxiomSet = normalizedIntegerAxiomSet;
		this.normalizedGCIIntegerToAxiom = new HashMap<Integer, String>();
	}
	
	public void writeMeOut(String fileURL){
		ObjectIOUtil.save(this, fileURL);
	}
	
	public static MyAxiomRepository readIn(String fileURL){
		return (MyAxiomRepository)ObjectIOUtil.load(fileURL);
	}
	
	public void createIndex(){
		Date start = new Date();
		transNormalizedAxioms();
		Date end = new Date();
		System.out.println("Creating index in: " + (end.getTime() - start.getTime()) + " ms.");
	}
	
	public boolean isInOriginalOntologyBySHA1(String sha1){
		if(normalizedGCISHA1ToInteger.containsKey(sha1)
				|| normalizedRISHA1ToInteger.containsKey(sha1)){
			return true;
		} else {
			return false;
		}
	}
	
	private void transNormalizedAxioms(){
		int id = 0;
		
		for(NormalizedIntegerAxiom celAxiom : normalizedIntegerAxiomSet){			
			if(celAxiom instanceof GCI0Axiom) {
				String sha1 = AxiomUtil.computeSHA1(((GCI0Axiom) celAxiom).getSubClass(),
						((GCI0Axiom) celAxiom).getSuperClass());
				normalizedGCISHA1ToInteger.put(sha1, id);
				normalizedGCIIntegerToSHA1.put(id, sha1);
				addAxiomGCI0Index(((GCI0Axiom) celAxiom).getSubClass(),
						((GCI0Axiom) celAxiom).getSuperClass());
				
				normalizedGCIIntegerToAxiom.put(id, celAxiom.toString());
			
			} else if(celAxiom instanceof GCI1Axiom){				
				String sha1 = AxiomUtil.computeSHA1(
						((GCI1Axiom) celAxiom).getLeftSubClass(),
						((GCI1Axiom) celAxiom).getRightSubClass(),
						((GCI1Axiom) celAxiom).getSuperClass());
				normalizedGCISHA1ToInteger.put(sha1, id);
				normalizedGCIIntegerToSHA1.put(id, sha1);			
				addAxiomGCI1Index(
						((GCI1Axiom) celAxiom).getSuperClass(),
						((GCI1Axiom) celAxiom));
				
				normalizedGCIIntegerToAxiom.put(id, celAxiom.toString());
				
			} else if(celAxiom instanceof GCI2Axiom){
				String sha1 = AxiomUtil.computeSHA1(
						((GCI2Axiom) celAxiom).getSubClass(),
						((GCI2Axiom) celAxiom).getPropertyInSuperClass(),
						((GCI2Axiom) celAxiom).getClassInSuperClass());
				normalizedGCISHA1ToInteger.put(sha1, id);
				normalizedGCIIntegerToSHA1.put(id, sha1);	
				addAxiomGCI2Index(
						((GCI2Axiom) celAxiom).getSubClass(),
						((GCI2Axiom) celAxiom).getPropertyInSuperClass(),
						((GCI2Axiom) celAxiom).getClassInSuperClass());
				
				normalizedGCIIntegerToAxiom.put(id, celAxiom.toString());
			} else if(celAxiom instanceof GCI3Axiom){
				String sha1 = AxiomUtil.computeSHA1(
						((GCI3Axiom) celAxiom).getPropertyInSubClass(),
						((GCI3Axiom) celAxiom).getClassInSubClass(),
						((GCI3Axiom) celAxiom).getSuperClass());
				normalizedGCISHA1ToInteger.put(sha1, id);
				normalizedGCIIntegerToSHA1.put(id, sha1);
				addAxiomGCI3Index(
						((GCI3Axiom) celAxiom).getPropertyInSubClass(),
						((GCI3Axiom) celAxiom).getClassInSubClass(),
						((GCI3Axiom) celAxiom).getSuperClass());
				
				normalizedGCIIntegerToAxiom.put(id, celAxiom.toString());
				
			} else if(celAxiom instanceof RI2Axiom){
				String sha1 = AxiomUtil.computeSHA1(
						((RI2Axiom) celAxiom).getSubProperty(), 
						((RI2Axiom) celAxiom).getSuperProperty());
				normalizedRISHA1ToInteger.put(sha1, id);
				normalizedRIIntegerToSHA1.put(id, sha1);
				addAxiomRI2Index(
						((RI2Axiom) celAxiom).getSubProperty(), 
						((RI2Axiom) celAxiom).getSuperProperty());
				
				normalizedGCIIntegerToAxiom.put(id, celAxiom.toString());
				
			} else if(celAxiom instanceof RI3Axiom){
				String sha1 = AxiomUtil.computeSHA1(
						((RI3Axiom) celAxiom).getLeftSubProperty(),
						((RI3Axiom) celAxiom).getRightSubProperty(),
						((RI3Axiom) celAxiom).getSuperProperty());
				normalizedRISHA1ToInteger.put(sha1, id);
				normalizedRIIntegerToSHA1.put(id, sha1);
				addAxiomRI3Index(
						((RI3Axiom) celAxiom).getSuperProperty(),
						((RI3Axiom) celAxiom));
				
				normalizedGCIIntegerToAxiom.put(id, celAxiom.toString());
			}	
			id ++ ;
		}
	}
	
	private void addAxiomRI3Index(Integer supRole, RI3Axiom axiom){
		if(ri3Sup.containsKey(supRole)){
			Set<RI3Axiom> axioms = ri3Sup.get(supRole);
			axioms.add(axiom);
		}else {
			Set<RI3Axiom> axioms = new HashSet<RI3Axiom>();
			axioms.add(axiom);
			ri3Sup.put(supRole, axioms);
		}
	}
	
	private void addAxiomRI2Index(Integer subRole, Integer supRole){
		if(ri2Sup.containsKey(supRole)){
			Set<Integer> subRoles = ri2Sup.get(supRole);
			subRoles.add(subRole);
		} else {
			Set<Integer> subRoles = new HashSet<Integer>();
			subRoles.add(subRole);
			ri2Sup.put(supRole, subRoles);
		}
	}
	
	private void addAxiomGCI3Index( Integer role, Integer subClass, Integer supClass){
		if(gci3Sup.containsKey(supClass)){
			Map<Integer, Set<Integer>> subRole = gci3Sup.get(supClass);
			if(subRole.containsKey(subClass)){
				Set<Integer> roles = subRole.get(subClass);
				roles.add(role);
			} else {
				Set<Integer> roles = new HashSet<Integer>();
				roles.add(role);
				subRole.put(subClass, roles);
			}
		} else {
			Map<Integer, Set<Integer>> subRole = new HashMap<Integer, Set<Integer>>();
			Set<Integer> roles = new HashSet<Integer>();
			roles.add(role);
			subRole.put(subClass, roles);
			gci3Sup.put(supClass, subRole);
		}
	}
	
	private void addAxiomGCI2Index(Integer subClass, Integer role, Integer supClass){
		if(gci2Sup.containsKey(supClass)){
			Map<Integer, Set<Integer>> subRole = gci2Sup.get(supClass);
			if(subRole.containsKey(subClass)){
				Set<Integer> roles = subRole.get(subClass);
				roles.add(role);
			} else {
				Set<Integer> roles = new HashSet<Integer>();
				roles.add(role);
				subRole.put(subClass, roles);
			}
		} else {
			Map<Integer, Set<Integer>> subRole = new HashMap<Integer, Set<Integer>>();
			Set<Integer> roles = new HashSet<Integer>();
			roles.add(role);
			subRole.put(subClass, roles);
			gci2Sup.put(supClass, subRole);
		}
	}
	
	private void addAxiomGCI1Index(Integer supClass, GCI1Axiom axiom){
		if(gci1Sup.containsKey(supClass)){
			Set<GCI1Axiom> axioms = gci1Sup.get(supClass);
			axioms.add(axiom);
		} else {
			Set<GCI1Axiom> axioms = new HashSet<GCI1Axiom>();
			axioms.add(axiom);
			gci1Sup.put(supClass, axioms);
		}
	}
	
	private void addAxiomGCI0Index(Integer subClass, Integer supClass){
		if(gci0Sup.containsKey(supClass)){
			Set<Integer> subClasses = gci0Sup.get(supClass);
			subClasses.add(subClass);
		} else {
			Set<Integer> subClasses = new HashSet<Integer>();
			subClasses.add(subClass);
			gci0Sup.put(supClass, subClasses);
		}
	}

	public IntegerSubsumerGraph getClassGraph() {
		return classGraph;
	}

	public Map<Integer, Set<Integer>> getGci0Sup() {
		return gci0Sup;
	}

	public Map<Integer, Set<GCI1Axiom>> getGci1Sup() {
		return gci1Sup;
	}

	public Map<Integer, Map<Integer, Set<Integer>>> getGci3Sup() {
		return gci3Sup;
	}

	public IntegerRelationMapImpl getRelationSet() {
		return relationSet;
	}

	public Map<Integer, Map<Integer, Set<Integer>>> getGci2Sup() {
		return gci2Sup;
	}

	public Map<Integer, Set<Integer>> getRi2Sup() {
		return ri2Sup;
	}

	public Map<Integer, Set<RI3Axiom>> getRi3Sup() {
		return ri3Sup;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Set<NormalizedIntegerAxiom> getNormalizedIntegerAxiomSet() {
		return normalizedIntegerAxiomSet;
	}

	public Map<String, Integer> getNormalizedGCISHA1ToInteger() {
		return normalizedGCISHA1ToInteger;
	}

	public Map<Integer, String> getNormalizedGCIIntegerToSHA1() {
		return normalizedGCIIntegerToSHA1;
	}

	public Map<String, Integer> getNormalizedRISHA1ToInteger() {
		return normalizedRISHA1ToInteger;
	}

	public Map<Integer, String> getNormalizedRIIntegerToSHA1() {
		return normalizedRIIntegerToSHA1;
	}
	
	public Map<Integer, String> NormalizedRIIntegerToAxiom(){
		return normalizedGCIIntegerToAxiom;
	}
}