package kse.findj.edg.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.tudresden.inf.lat.jcel.core.graph.IntegerRelationMap;
import de.tudresden.inf.lat.jcel.core.graph.IntegerSubsumerGraph;
import de.tudresden.inf.lat.jcel.coreontology.axiom.GCI1Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.RI3Axiom;

import kse.findj.edg.data.AxiomGCI0;
import kse.findj.edg.data.AxiomGCI1;
import kse.findj.edg.data.AxiomGCI3;
import kse.findj.edg.data.AxiomR;
import kse.findj.edg.data.AxiomRI2;
import kse.findj.edg.data.AxiomRI3;
import kse.findj.edg.data.AxiomS;
import kse.findj.edg.data.MyAxiom;
import kse.findj.edg.data.MyAxiomRepository;

public class BackTracker {
	
	private MasterRoutine masterRoutine;
	//private ExplanationRoutine expRoutine;
	private MyAxiomRepository index;
	private MyAxiomRecorder recorder;
	
	private Integer leftmostAxiomID;
	private MyAxiom leftmostAxiom;
	private Set<Integer> ancestors;
	
	private Set<Integer> originalAxiomsToExpand;
	private Map<Integer, Set<Integer>> entailedAxiomsToExpand;
	
	private Set<ExplanationRoutine> replacedRoutines;
	private Set<ExplanationRoutine> newRoutinesToBePutInRunningQueue;
	
	public BackTracker(MasterRoutine masterRoutine, ExplanationRoutine expRoutine){
		this.masterRoutine = masterRoutine;
		//this.expRoutine = expRoutine;
		this.index = masterRoutine.getIndex();
		this.recorder = masterRoutine.getRecorder();
		this.replacedRoutines = new HashSet<ExplanationRoutine>();
		this.newRoutinesToBePutInRunningQueue = new HashSet<ExplanationRoutine>();
		this.originalAxiomsToExpand = expRoutine.getOriginalAxioms();
		this.entailedAxiomsToExpand = expRoutine.getEntailedAxioms();
		
		/*
		 * Get the leftmost entailed axiom which is used
		 * to expand the tree.
		 */
		for(Integer axiomID : this.entailedAxiomsToExpand.keySet()){
			this.leftmostAxiomID = axiomID;
			break;
		}
		
		/*
		 * The axiom used to expand.
		 */
		this.leftmostAxiom = recorder.getAxiomFromId(leftmostAxiomID);
		this.ancestors = this.entailedAxiomsToExpand.get(leftmostAxiomID);
		if(this.ancestors == null) this.ancestors = new HashSet<Integer>();
	
	}
	
	public Set<ExplanationRoutine> getReplacedRoutines() {
		return replacedRoutines;
	}

	public Set<ExplanationRoutine> getNewRoutinesToBePutInRunningQueue() {
		return newRoutinesToBePutInRunningQueue;
	}

	public void backtrack(){
		if(leftmostAxiom instanceof AxiomS){
			/*
			 * look back c[=d
			 */
			Integer c = ((AxiomS)leftmostAxiom).getSubClass();
			Integer d = ((AxiomS)leftmostAxiom).getSuperClass();
			
			lookBackR0(c, d);
			lookBackR1(c, d);
			lookBackR3(c, d);
		
		} else if(leftmostAxiom instanceof AxiomR){
			/*
			 * look back c [= -] r.d
			 */
			Integer c = ((AxiomR)leftmostAxiom).getSubClass();
			Integer r = ((AxiomR)leftmostAxiom).getPropertyInSuperClass();
			Integer d = ((AxiomR)leftmostAxiom).getClassInSuperClass();
			
			lookBackR2(c, r, d);
			lookBackR4(c, r, d);
			lookBackR5(c, r, d);			
		}		
	}
	
	private void expandNewBranch(MyAxiom... axioms){
		Integer[] axiomIDs = new Integer[axioms.length];
		int offset = 0;
		for(MyAxiom axiom : axioms){
			recorder.addAxiom(axiom);
			Integer id = recorder.getIdFromSHA1(axiom.getSHA1(), axiom);
			axiomIDs[offset ++] = id;
		}
		expandNewBranch(axiomIDs);
	}
	
	private void expandNewBranch(Integer... axiomIDs){
		/*
		 * If one of the axioms to be expanded is
		 * one of my ancestors or it is just me,
		 * cut this branch.
		 */
		if(!toCreateBranch(axiomIDs)) return;
		
		/*
		 * Or use these axioms to expand. 
		 */
		Set<Integer> originalAxiomIDs = new TreeSet<Integer>();
		originalAxiomIDs.addAll(originalAxiomsToExpand);
		Map<Integer, Set<Integer>> entailedAxiomIDs = new TreeMap<Integer, Set<Integer>>();
		entailedAxiomIDs.putAll(entailedAxiomsToExpand);
		entailedAxiomIDs.remove(leftmostAxiomID);
				
		for(Integer axiomID : axiomIDs) {
			MyAxiom axiom = recorder.getAxiomFromId(axiomID);
			
			boolean isInOriginalOntology = false;
			if(index.isInOriginalOntologyBySHA1(recorder.getSha1FromId(axiomID))){
				isInOriginalOntology = true;
			
			} else if(axiom instanceof AxiomS){
				Integer subClass = ((AxiomS)axiom).getSubClass();
				Integer supClass = ((AxiomS)axiom).getSuperClass();
				if(subClass.equals(supClass)){
					isInOriginalOntology = true;
				} else if(subClass.equals(0) || supClass.equals(1)){
					isInOriginalOntology = true;
				}
			}
			
			if(isInOriginalOntology){
				originalAxiomIDs.add(axiomID);
				
			} else {
				Set<Integer> myAncestors = new HashSet<Integer>();
				myAncestors.addAll(ancestors);
				myAncestors.add(leftmostAxiomID);
				
				if(entailedAxiomIDs.containsKey(axiomID)){
					Set<Integer> previousAncestors = entailedAxiomIDs.get(axiomID);
					myAncestors.addAll(previousAncestors);
					entailedAxiomIDs.remove(axiomID);
				}
				
				entailedAxiomIDs.put(axiomID, myAncestors);
			}
		}
		
		ExplanationRoutine newRoutine = new ExplanationRoutine(masterRoutine);
		newRoutine.setOriginalAxioms(originalAxiomIDs);
		newRoutine.setEntailedAxioms(entailedAxiomIDs);
		String sha1 = newRoutine.computeSHA1Code();
		
		/*
		 * Check if the original sets include
		 * one justification.
		 */
		Set<ExplanationRoutine> justifications = masterRoutine.getJustifications();
		for(ExplanationRoutine justification : justifications){
			Set<Integer> axiomSet = justification.getOriginalAxioms();
			if(originalAxiomIDs.containsAll(axiomSet)){
				return;
			}
		}
				
		/*
		 * If this node is in the tree, drop it.
		 */
		if(!masterRoutine.getTraceRecords().contains(sha1)){
			if(newRoutine.getEntailedAxioms().size() == 0){
				masterRoutine.getTraceRecords().add(sha1);
				masterRoutine.getJustifications().add(newRoutine);
							
			} else {
				/*
				 * A cutting strategy, if the expanded axiom can be entailed from
				 * others, it should be removed, and cannot be used
				 * to expand.
				 */
				if(newRoutine.getOriginalAxioms().size() == originalAxiomsToExpand.size()
						 && newRoutine.getEntailedAxioms().size() < entailedAxiomsToExpand.size()){
					replacedRoutines.add(newRoutine);
				} else {
					newRoutinesToBePutInRunningQueue.add(newRoutine);
				}
				
				masterRoutine.getTraceRecords().add(sha1);
				masterRoutine.getUnfinishedRoutineSet().add(sha1);
			}
		}
	}
	
	/**
	 * 1)
	 * If one of my super routines denotes this new created axiom,
	 * we should replace this way with the other sub sets of this super routine.
	 * 2)
     * If this new created axiom is myself, 
     * this look-back way should be cut off.
	 * 
	 */
	private boolean toCreateBranch(Integer... subAxiomIDs){
		
		for(Integer subAxiomID : subAxiomIDs){		
			if(ancestors.contains(subAxiomID)){
				return false;
			} else if(leftmostAxiomID.equals(subAxiomID)){
				return false;
			}		
		}
		
		return true;
	}
	
	private void lookBackR0(Integer c, Integer d){
		
		Map<Integer, Set<Integer>> gci0Sup = index.getGci0Sup();
		IntegerSubsumerGraph classGraph = index.getClassGraph();
		
		if(gci0Sup.containsKey(d)){
			Set<Integer> axiomGCI0s = gci0Sup.get(d);
			Collection<Integer> superClassesOfc = classGraph.getSubsumers(c);
			
			for(Integer subClass : axiomGCI0s){	
				if(superClassesOfc.contains(subClass)) {
					
					AxiomGCI0 gci0Axiom = new AxiomGCI0(subClass, d);
					AxiomS cSuba = new AxiomS(c, subClass);
					expandNewBranch(gci0Axiom,cSuba);
				}
			}			
		}		
	}
	
	private void lookBackR1(Integer c, Integer d){
		
		Map<Integer, Set<GCI1Axiom>> gci1Sup = index.getGci1Sup();
		IntegerSubsumerGraph classGraph = index.getClassGraph();
		
		// If there exists GCI1 axioms with d as super class. 
		if(gci1Sup.containsKey(d)){
			
			//get all GCI1 axioms with d as super class.
			Set<GCI1Axiom> axiomGCI1s = gci1Sup.get(d);
			
			//get all S members with c as its subClass.
			Collection<Integer> superClassesOfc = classGraph.getSubsumers(c);
			
			//Iterate every GCI1 axiom.
			for(GCI1Axiom axiomGCI1 : axiomGCI1s){
				Integer a1 = axiomGCI1.getLeftSubClass();
				Integer a2 = axiomGCI1.getRightSubClass();
				AxiomGCI1 axiomGCI1v2 = new AxiomGCI1(a1, a2, d);
				
				//If a1 and a2 are both super classes of c,
				//look back.
				if(superClassesOfc.contains(a1) &&
						superClassesOfc.contains(a2)) {
					AxiomS cSuba1 = new AxiomS(c, a1);
					AxiomS cSuba2 = new AxiomS(c, a2);
					
					expandNewBranch(
							axiomGCI1v2,
							cSuba1,
							cSuba2);
				}
			}			
		}		
	}
	
	private void lookBackR3(Integer c, Integer d){
		
		Map<Integer, Map<Integer, Set<Integer>>> gci3Sup = index.getGci3Sup();
		IntegerSubsumerGraph classGraph = index.getClassGraph();
		IntegerRelationMap relationSet = index.getRelationSet();
		Collection<Integer> rs = relationSet.getRelationsByFirst(c);
		
		if(gci3Sup.containsKey(d)){
			Map<Integer, Set<Integer>> subRole = gci3Sup.get(d);
			Set<Integer> subClasses = subRole.keySet();
			
			for(Integer r : rs){
				Collection<Integer> ys = relationSet.getByFirst(r, c);
				if(ys != null){			
					for(Integer y : ys){
						Collection<Integer> as = classGraph.getSubsumers(y);
						if(as != null){
							for(Integer a : as){						
								if(subClasses.contains(a)){
									Set<Integer> roles = subRole.get(a);
									if(roles.contains(r)){
										AxiomGCI3 gci3Axiom = new AxiomGCI3(r, a, d);
										AxiomR axiomR = new AxiomR(c, r, y);
										AxiomS axiomS = new AxiomS(y, a);
										
										expandNewBranch(
												gci3Axiom,
												axiomR,
												axiomS);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void lookBackR2(
			Integer c,
			Integer r,
			Integer d
			){
		
		IntegerSubsumerGraph classGraph = index.getClassGraph();
		Map<Integer, Map<Integer, Set<Integer>>> rSup = index.getGci2Sup();
		
		if(classGraph.getElements().contains(c)){
			Collection<Integer> supClasses = classGraph.getSubsumers(c);
			for(Integer y : supClasses){
				if(rSup.containsKey(d)){
					Map<Integer, Set<Integer>> subRole = rSup.get(d);
					if(subRole.containsKey(y)){
						Set<Integer> roles = subRole.get(y);
						if(roles.contains(r)){
							AxiomS axiomS = new AxiomS(c, y);
							AxiomR axiomR = new AxiomR(y, r, d);
							
							expandNewBranch(
									axiomS,
									axiomR);

						}
					}
				}
			}
		}		
	}
	
	private void lookBackR4(
			Integer c,
			Integer r,
			Integer d
			){
		
		Map<Integer, Set<Integer>> ri2 = index.getRi2Sup();
		IntegerRelationMap relationSet = index.getRelationSet();
		Collection<Integer> ss = relationSet.getRelationsByFirst(c);
		
		if(ri2.containsKey(r)){
			Set<Integer> subRoles = ri2.get(r);
			for(Integer s : ss){
				if(subRoles.contains(s)){
					Collection<Integer> ds = relationSet.getByFirst(s, c);
					if(ds != null){
						if(ds.contains(d)){
							AxiomRI2 axiomRI2 = new AxiomRI2(s, r);
							AxiomR axiomR = new AxiomR(c, s, d);
							
							expandNewBranch(
									axiomRI2,
									axiomR);
							
						}
					}		
				}
			}
		}		
	}
	
	private void lookBackR5(
			Integer c,
			Integer r,
			Integer d){

		Map<Integer, Set<RI3Axiom>> ri3Sup = index.getRi3Sup();
		IntegerRelationMap relationSet = index.getRelationSet();
		
		if(ri3Sup.containsKey(r)){
			Set<RI3Axiom> axiomSet = ri3Sup.get(r);
			for(RI3Axiom axiom : axiomSet){
				Integer s = axiom.getLeftSubProperty();
				Integer t = axiom.getRightSubProperty();
				
				Collection<Integer> ys = relationSet.getByFirst(s, c);
				Collection<Integer> yys = relationSet.getBySecond(t, d);
				
				if(ys != null && yys != null){
					for(Integer y : ys){
						if(yys.contains(y)){
							AxiomRI3 axiomRI3 = new AxiomRI3(s, t, r);
							AxiomR axiomR1 = new AxiomR(c, s, y);
							AxiomR axiomR2 = new AxiomR(y, t, d);

							expandNewBranch(
									axiomRI3,
									axiomR1,
									axiomR2);
							
						}
					}
				}
			}
		}
	}
}
