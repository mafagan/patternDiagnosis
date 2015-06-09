package kse.findj.test.black;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kse.findj.module.KSEOWLOntology;
import kse.findj.util.LogWriter;
import kse.findj.util.ObjectIOUtil;

public class BlackboxSamplesTest {
	
	private static String FILED = "Snomed_20090131";
	/*
	 * The ontology file name.
	 */
	private static String ONTOLOGY = FILED + ".owl";
	/*
	 * The experiment directory.
	 */
	private static String PATH = "D:\\VM\\findJ\\test\\" + FILED + "\\";
	
	private static KSEOWLOntology kseOntology;
	
	public static void loadOntology(){
		kseOntology = new KSEOWLOntology(new File(ONTOLOGY));
	}
	
	public static void main(String[] args) {
		loadOntology();
		
		List<String[]> sampleList = (List<String[]>) ObjectIOUtil.load(PATH + FILED + "_samples");
		
		LogWriter logWriter = new LogWriter(PATH + FILED + "_balckbox_limit1000_results.txt");
			
		long avgComputingTime = 0;
		long avgExtractingTime = 0;
		long avgTests = 0;
		long maxTime = 0;
		String id = "";
		Map<Integer, Integer> justsCounter = new HashMap<Integer, Integer>();
		
		System.out.println("sample size: " + sampleList.size());
		int counter  = 0 ;
		for(String[] sample : sampleList){
			if(counter == 2000) break;
			BlackboxOneCase oneCase = 
					new BlackboxOneCase(
							sample[1], 
							sample[2],
							kseOntology);
			oneCase.doTest();
			
			System.out.println(sample[0] + " done.");
			
			logWriter.write(sample[0]+"> "
					+ "module extracting time: " + oneCase.getExtractingTime()
					+ "\t module size: " + oneCase.getModuleSize()
					+ "\t justification: " + oneCase.getNumOfJustifications()
					+ "\t SubsumptionTestNumber: " + oneCase.getSubsumptionTestNumber()
					+ "\t time: " + oneCase.getComputingTime());
			
			int justNum = oneCase.getNumOfJustifications();
			if(justsCounter.containsKey(justNum)){
				int currentNum = justsCounter.get(justNum);
				justsCounter.put(justNum, currentNum + 1);
			} else {
				justsCounter.put(justNum, 1);
			}
			
			avgComputingTime += oneCase.getComputingTime();
			avgExtractingTime += oneCase.getExtractingTime();
			avgTests += oneCase.getSubsumptionTestNumber();
			if(oneCase.getComputingTime() > maxTime){
				maxTime = oneCase.getComputingTime();
				id = sample[0];
			}
			counter ++ ;
		}
		
		logWriter.write("avgTests: " + (avgTests / counter));
		logWriter.write("avgExtractingTime: " + (avgExtractingTime / counter));
		logWriter.write("avgComputingTime: " + (avgComputingTime / counter));
		logWriter.write("worst case: " + id);
		for(Integer justNum : justsCounter.keySet()){
			int num = justsCounter.get(justNum);
			logWriter.write("just num: " + justNum + " count: " + num);
		}
		logWriter.closeWriter();
	}

}
