package kse.findj.test.edg;

import kse.findj.edg.core.MasterRoutine;
import kse.findj.edg.data.MyAxiomRepository;
import kse.findj.reasoner.RuleBasedCELReasoner;

public class EDGOneCase {
	
	private String subClassURI;
	private String supClassURI;
	private int threadNum;
	private MyAxiomRepository reporsitory;
	private RuleBasedCELReasoner reasoner;
	private int numOfJustifications;
	private long time;
	private int numOfnodesExpanded;
		
	public EDGOneCase(
			String subClassURI, String supClassURI,
			int threadNum, RuleBasedCELReasoner reasoner, MyAxiomRepository reporsitory) {
		super();
		this.reasoner = reasoner;
		this.subClassURI = subClassURI;
		this.supClassURI = supClassURI;
		this.threadNum = threadNum;
		this.reporsitory = reporsitory;
	}

	public void doTest(){
		Integer subClass = reasoner.getIntegerClass(subClassURI);
		Integer supClass = reasoner.getIntegerClass(supClassURI);
		
		MasterRoutine masterRoutine = 
				new MasterRoutine(reporsitory, threadNum, subClass, supClass);		
		masterRoutine.computeResult();
		
		this.numOfJustifications = masterRoutine.getJustifications().size();
		this.time = masterRoutine.getComputingTime();
		this.numOfnodesExpanded = masterRoutine.numOfNodesExpanded();
	}

	public int getNumOfJustifications() {
		return numOfJustifications;
	}

	public long getTime() {
		return time;
	}

	public int getNumOfnodesExpanded() {
		return numOfnodesExpanded;
	}
	
}
