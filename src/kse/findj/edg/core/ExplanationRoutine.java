package kse.findj.edg.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import kse.findj.edg.data.*;

public class ExplanationRoutine implements Runnable {
	
	private String sha1;
	
	private MasterRoutine masterRoutine;
	
	/**
	 * All the ancestors in the reasoning path 
	 * for each entailed nodes.
	 */
	private Map<Integer, Set<Integer>> entailedAxioms = null;
	
	/**
	 * The identifiers of the original axioms.
	 */
	private Set<Integer> originalAxioms = null;

	public ExplanationRoutine(MasterRoutine masterRoutine){
		this.masterRoutine = masterRoutine;
		this.entailedAxioms = new TreeMap<Integer, Set<Integer>>();
		this.originalAxioms = new TreeSet<Integer>();
	}
	
	@Override
	public void run() {
		
		BackTracker backTracker = new BackTracker(masterRoutine, this);
		backTracker.backtrack();
		Set<ExplanationRoutine> newRoutines = 
				backTracker.getNewRoutinesToBePutInRunningQueue();
		Set<ExplanationRoutine> replacedRoutines =
				backTracker.getReplacedRoutines();
		
		if(replacedRoutines.size() > 0){
			for(ExplanationRoutine repacedRoutine : replacedRoutines){
				masterRoutine.getRunningQueue().offer(repacedRoutine);
			}
		} else {		
			for(ExplanationRoutine newRoutine : newRoutines) {
				masterRoutine.getRunningQueue().offer(newRoutine);
			}	
		}
		
		this.removeMeFromUnifinishedSet();
		this.releaseMe();
	}
	
	private void releaseMe(){
		this.sha1 = null;
		this.entailedAxioms.clear();
		this.originalAxioms.clear();
		this.entailedAxioms = null;
		this.originalAxioms = null;
		this.masterRoutine = null;
		//System.gc();
	}
	
	public void addOriginalAxiomId(Integer originalAxiomId){
		this.originalAxioms.add(originalAxiomId);
	}
	
	public void addEntailedAxiomId(Integer entailedAxiomId){
		if(!entailedAxioms.containsKey(entailedAxiomId)){
			Set<Integer> ancestors = new HashSet<Integer>();
			this.entailedAxioms.put(entailedAxiomId, ancestors);
		}
	}
	
	public void setEntailedAxioms(
			Map<Integer, Set<Integer>> entailedAxioms) {
		this.entailedAxioms = entailedAxioms;
	}

	public void setOriginalAxioms(Set<Integer> originalAxioms) {
		this.originalAxioms = originalAxioms;
	}

	/**
	 * Compute the sha1 code for this routine.
	 */
	public String computeSHA1Code(){
		String identifier = "";
		
		for(Integer entailedAxiomID : entailedAxioms.keySet()) {
			identifier += entailedAxiomID + "-";
		}
		
		for(Integer originalAxiomID : originalAxioms) {
			identifier += originalAxiomID + "-";
		}
		
		identifier += "0";
		
		this.sha1 = SHA1Util.hex_sha1(identifier);
		
		return this.sha1;
	}
	
	private void removeMeFromUnifinishedSet(){
		this.masterRoutine.getUnfinishedRoutineSet().remove(this.sha1);
	}

	public Set<Integer> getOriginalAxioms() {
		return originalAxioms;
	}

	public Map<Integer, Set<Integer>> getEntailedAxioms() {
		return entailedAxioms;
	}
	
	public MasterRoutine getMasterRoutine(){
		return masterRoutine;
	}
	
	public String toString(){
		String str = "";
		
		str += "{";
		
		for(Integer entailedAxiomID : entailedAxioms.keySet()) {
			str += entailedAxiomID + ":";
			MyAxiom axiom = masterRoutine.getRecorder().getAxiomFromId(entailedAxiomID);
			str += axiom.getAxiomDescription() + " ";
		}
		
		str += "#";
		
		for(Integer originalAxiomID : originalAxioms) {
			str += originalAxiomID + ":";
			MyAxiom axiom = masterRoutine.getRecorder().getAxiomFromId(originalAxiomID);
			str += axiom.getAxiomDescription() + " ";
		}
		
		str += "}";
		
		//str += "\n" + originalAxioms + "\n";
		return str;
	}
	
}
