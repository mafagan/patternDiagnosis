package kse.findj.test.edg;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kse.findj.edg.data.MyAxiomRepository;
import kse.findj.reasoner.RuleBasedCELReasoner;
import kse.findj.util.LogWriter;
import kse.findj.util.ObjectIOUtil;

public class EDGSamplesTest {
	
	private static String FILED = "Snomed_20130131";
	/*
	 * The ontology file name.
	 */
	private static String ONTOLOGY = FILED + ".owl";
	/*
	 * The experiment directory.
	 */
	private static String PATH = "D:\\VM\\findJ\\test\\" + FILED + "\\";
	/*
	 * The repository file.
	 */
	private static String REP = FILED + ".rep";
	
	private static int threadNum = 8;
	/*
	 * The result file.
	 */
	//private static String RESULT = "result.txt";
	/*
	 * The log file.
	 */
	//private static String LOG = "log.txt";
	
	private static RuleBasedCELReasoner reasoner;
	
	private static MyAxiomRepository reporsitory;
	
	public static void classifyAndTransform(){
		/*
		 * Create a cel reasoner, get all entailments. 
		 */
		reasoner = new RuleBasedCELReasoner(new File(ONTOLOGY));
		reasoner.doInference();
		
		/*
		 * Transform these entailments to my own types,
		 * and write it to file system.
		 */
		reporsitory = 
				new MyAxiomRepository(
						reasoner.getClassGraph(), 
						reasoner.getRelationSet(),
						reasoner.getNormalizedIntegerAxiomSet()
						);
		reporsitory.createIndex();
		reporsitory.writeMeOut(PATH + REP);
	}
	
	public static void main(String[] args) {
		//classifyAndTransform();
		
		reporsitory = MyAxiomRepository.readIn(PATH + REP);
		reasoner = new RuleBasedCELReasoner(new File(ONTOLOGY));
		
		List<String[]> sampleList = (List<String[]>) ObjectIOUtil.load(PATH + FILED + "_samples");
		
		LogWriter logWriter = new LogWriter(PATH + FILED + "_glassbox_results_8_thread.txt");
		
		long avgTime = 0;
		long avgNodes = 0;
		long maxTime = 0;
		String id = "";
		Map<Integer, Integer> justsCounter = new HashMap<Integer, Integer>();
		
		int counter = 0;
		System.out.println("sample size: " + sampleList.size());
		for(String[] sample : sampleList){
			if(counter == 2000) break;
			
			EDGOneCase oneCase = new EDGOneCase(
					sample[1], 
					sample[2],
					threadNum, reasoner, reporsitory);
			oneCase.doTest();
			
			System.out.println(sample[0] + " done.");
			logWriter.write(sample[0]+"> "
					+ "justification: " + oneCase.getNumOfJustifications()
					+ "\t nodes: " + oneCase.getNumOfnodesExpanded()
					+ "\t time: " + oneCase.getTime());
			
			int justNum = oneCase.getNumOfJustifications();
			if(justsCounter.containsKey(justNum)){
				int currentNum = justsCounter.get(justNum);
				justsCounter.put(justNum, currentNum + 1);
			} else {
				justsCounter.put(justNum, 1);
			}
			
			avgTime += oneCase.getTime();
			avgNodes += oneCase.getNumOfnodesExpanded();
			if(oneCase.getTime() > maxTime){
				maxTime = oneCase.getTime();
				id = sample[0];
			}
			counter ++ ;
		}
		
		logWriter.write("avgNodes: " + (avgNodes / counter));
		logWriter.write("avgTime: " + (avgTime / counter));
		logWriter.write("worst case: " + id);
		for(Integer justNum : justsCounter.keySet()){
			int num = justsCounter.get(justNum);
			logWriter.write("just num: " + justNum + " count: " + num);
		}
		logWriter.closeWriter();
	}

}
