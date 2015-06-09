package kse.findj.edg.core;

import java.util.Date;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kse.findj.edg.data.AxiomS;
import kse.findj.edg.data.ConcurrentHashSet;
import kse.findj.edg.data.MyAxiom;
import kse.findj.edg.data.MyAxiomRepository;

public class MasterRoutine extends Thread {
	
	/**
	 * The input, maybe an entailment subClass [=o supClass
	 */
	private Integer subClass;
	/**
	 * The input, maybe an entailment subClass [=o supClass
	 */
	private Integer supClass;
	private MyAxiom inputAxiom;
	private MyAxiomRepository index;
	private int threadNum;
	private long computingTime = 0;
	private MyAxiomRecorder recorder;
	
	/**
	 * This is a trace records to eliminate
	 * the duplicates.
	 */
	private Set<String> traceRecords;
	
	/**
	 * Running queue.
	 */
	private Queue<ExplanationRoutine> runningQueue;
	
	private Set<ExplanationRoutine> justifications;
	
	private Set<String> unfinishedRoutineSet;
	
	
	public MasterRoutine(MyAxiomRepository index, int threadNum, Integer subClass, Integer supClass){
		this.index = index;
		this.threadNum = threadNum;
		this.subClass = subClass;
		this.supClass = supClass;
		
		this.recorder = new MyAxiomRecorder();
		this.justifications = new ConcurrentHashSet<ExplanationRoutine>();
		this.traceRecords = new ConcurrentHashSet<String>();
		this.runningQueue = new ConcurrentLinkedQueue<ExplanationRoutine>();
		this.unfinishedRoutineSet = new ConcurrentHashSet<String>();
	}

	public void computeResult(){
		Date start = new Date();
		run();
		Date end = new Date();
		if(computingTime != -1)
			computingTime = end.getTime() - start.getTime();
		//System.out.println("Computing justifications: " + (end.getTime() - start.getTime()) + "ms.");		
	}
	
	@Override
	public void run() {
		Date start = new Date();
		
		ExplanationRoutine rootRoutine = new ExplanationRoutine(this);	
		inputAxiom = new AxiomS(this.subClass, this.supClass);
		recorder.addAxiom(inputAxiom);

		if(index.isInOriginalOntologyBySHA1(inputAxiom.getSHA1())
				|| this.subClass.equals(this.supClass)){
			rootRoutine.addOriginalAxiomId(
					recorder.getIdFromSHA1(inputAxiom.getSHA1(), inputAxiom));
			justifications.add(rootRoutine);
			return;
		}
		
		rootRoutine.addEntailedAxiomId(
				recorder.getIdFromSHA1(inputAxiom.getSHA1(), inputAxiom));
		String rootSha1 = rootRoutine.computeSHA1Code();
		traceRecords.add(rootSha1);
		unfinishedRoutineSet.add(rootSha1);
		runningQueue.offer(rootRoutine);
		ExecutorService threadPool =  Executors.newFixedThreadPool(threadNum);
		while(unfinishedRoutineSet.size() != 0){
			if(!runningQueue.isEmpty()){
				ExplanationRoutine routine = runningQueue.poll();
				threadPool.execute(routine);
			}
			
			Date currentTime = new Date();
			if((currentTime.getTime() - start.getTime())/1000 > 120){
				this.computingTime = -1;
				break;
			}
		}
		
		threadPool.shutdownNow();
	}

	public MyAxiomRecorder getRecorder() {
		return recorder;
	}

	public MyAxiomRepository getIndex() {
		return index;
	}

	public Set<String> getUnfinishedRoutineSet() {
		return unfinishedRoutineSet;
	}

	public Set<String> getTraceRecords() {
		return traceRecords;
	}

	public Queue<ExplanationRoutine> getRunningQueue() {
		return runningQueue;
	}

	public Set<ExplanationRoutine> getJustifications() {
		return justifications;
	}

	public long getComputingTime() {
		return computingTime;
	}

	public int numOfNodesExpanded(){
		return traceRecords.size();
	}
}
