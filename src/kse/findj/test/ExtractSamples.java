package kse.findj.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.semanticweb.owlapi.model.OWLClass;

import de.tudresden.inf.lat.jcel.core.graph.IntegerSubsumerGraph;

import kse.findj.reasoner.RuleBasedCELReasoner;
import kse.findj.util.ObjectIOUtil;
import kse.findj.util.SampleWriter;

public class ExtractSamples {
	
	private double sampleProportion = 0.01;
	
	private Random random = new Random();
	
	private String FILED = "Snomed_20090131";
	/*
	 * The ontology file name.
	 */
	private String ONTOLOGY = FILED + ".owl";
	/*
	 * The experiment directory.
	 */
	private String PATH = "D:\\VM\\findJ\\test\\" + FILED + "\\";
	
	private RuleBasedCELReasoner reasoner;
	
	private long usefulEntailments = 0;
	
	private long sampleSize = 0;
	
	private List<String[]> sampleList = new ArrayList<String[]>();
	
	public static void main(String[] args) {
		ExtractSamples extracter = new ExtractSamples();
		extracter.sampleEntailments();
	}
	
	public void sampleEntailments(){
		SampleWriter sampleWriter = new SampleWriter(PATH + "samples.txt"); 
		/*
		 * Call reasoner and do inference.
		 */
		reasoner = new RuleBasedCELReasoner(new File(ONTOLOGY));
		reasoner.doInference();
		
		/*
		 * Iterate the entailments.
		 */
		IntegerSubsumerGraph classGragh = reasoner.getClassGraph();		
		Map<Integer, OWLClass> classMap = reasoner.getClassMap();	
		Collection<Integer> subsumees = classGragh.getElements();
		for(Integer subClass : subsumees){
			Collection<Integer> subsumers = classGragh.getSubsumers(subClass);
			for(Integer supClass : subsumers){
				/*
				 * Do not take the cases with auxiliary concepts.
				 */
				if(classMap.get(subClass) == null
						|| classMap.get(supClass) == null){
					continue;
				}
				
				if(subClass.equals(0) || supClass.equals(1)) {
					continue;
				}
				
				if(subClass.equals(supClass)){
					continue;
				}
				
				usefulEntailments ++ ;
				
				if(takeIn()){
					String subURI = classMap.get(subClass).getIRI().toString();
					String supURI = classMap.get(supClass).getIRI().toString();					
					sampleWriter.write(sampleSize + "\t" +subURI + "\t" + supURI); 
					
					String[] sample = new String[3];
					sample[0] = sampleSize + "";
					sample[1] = subURI;
					sample[2] = supURI;
					sampleList.add(sample);
					
					sampleSize ++ ;
				}
			}
		}
		sampleWriter.write("sample size: " + sampleSize);
		sampleWriter.write("Useful entailments: " + usefulEntailments);
		sampleWriter.closeWriter();
		ObjectIOUtil.save(sampleList, PATH + FILED + "_samples");		
	}
	
	public boolean takeIn(){
		if(random.nextDouble() <= sampleProportion){
			return true;
		} else {
			return false;
		}
	}

	public long getUsefulEntailments() {
		return usefulEntailments;
	}

	public long getSampleSize() {
		return sampleSize;
	}

}
