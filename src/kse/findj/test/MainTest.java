package kse.findj.test;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import de.tudresden.inf.lat.jcel.core.graph.IntegerSubsumerGraph;
import de.tudresden.inf.lat.jcel.coreontology.axiom.NormalizedIntegerAxiom;
import kse.findj.edg.core.ExplanationRoutine;
import kse.findj.edg.core.MasterRoutine;
import kse.findj.edg.data.AxiomS;
import kse.findj.edg.data.MyAxiomRepository;
import kse.findj.edg.data.SHA1Util;
import kse.findj.reasoner.RuleBasedCELReasoner;

public class MainTest {
	
	private static String FILED = "ontall";
	/*
	 * The ontology file name.
	 */
	private static String ONTOLOGY = FILED + ".owl";
	/*
	 * The experiment directory.
	 */
	private static String PATH = "ontology/";
	/*
	 * The repository file.
	 */
	private static String REP = FILED + ".rep";
	/*
	 * The result file.
	 */
	//private static String RESULT = "result.txt";
	/*
	 * The log file.
	 */
	private static String LOG = "log.txt";
	
	private static RuleBasedCELReasoner reasoner;
	
	private static MyAxiomRepository repository;
	
	public static void classifyAndTransform(){
		/*
		 * Create a cel reasoner, get all entailments. 
		 */
		
		reasoner = new RuleBasedCELReasoner(new File(PATH + ONTOLOGY));
		reasoner.doInference();
		
		/*
		 * Transform these entailments to my own types,
		 * and write it to file system.
		 */
		repository = 
				new MyAxiomRepository(
						reasoner.getClassGraph(), 
						reasoner.getRelationSet(),
						reasoner.getNormalizedIntegerAxiomSet()
						);
		repository.createIndex();
		repository.writeMeOut(PATH + REP);
	}
		
	public static <E> void main(String[] args) {
		classifyAndTransform();
		
		
		
		Map<Integer, OWLClass> classMap = reasoner.getClassMap();
		
		System.out.println("\nClass map");
		for(Entry<Integer, OWLClass> entry : classMap.entrySet()){
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		
		System.out.println("\nClass graph:");
		System.out.println(reasoner.getClassGraph());
		
		System.out.println("\nAxiom Set");
		
//		Set<NormalizedIntegerAxiom> st = reasoner.getNormalizedIntegerAxiomSet();
//		Iterator stit = st.iterator();
//		while (stit.hasNext()) {
//			Object temp = stit.next();
//			System.out.println(temp + " ");
//		}
		
		IntegerSubsumerGraph classGragh = repository.getClassGraph();
//		System.out.println("\nheihei" + classGragh.getSubsumers(9));
//		System.out.println(repository.getNormalizedIntegerAxiomSet());
		Map<Integer, String> integerToAxiomHashMap = repository.NormalizedRIIntegerToAxiom();
		for(Entry<Integer, String> entry : integerToAxiomHashMap.entrySet()){
			System.out.println(entry.getKey() + " " + entry.getValue());
		}

		/* 
		 * 
		 * Create finding justification routine.
		 * Two mode to do computing:
		 * mode 0: To compute one axiom each time, and you
		 *         should input new axiom to continue
		 *         computing.
		 * mode 1: Compute all justifications for all
		 *         axioms. 
		 */
		int mode;
		System.out.println("\nInput mode numner:");
		Scanner in0 = new Scanner(System.in);
		mode = in0.nextInt();
		if(mode == 0){
			 /* 
			  * Configure the log file.
			  */
			while(true){
				int subClass, supClass;
				System.out.print("Input subClass: ");
				Scanner in1 = new Scanner(System.in);
				subClass = in1.nextInt();
				if(subClass == -1) break;
				System.out.print("Input supClass: ");
				Scanner in2 = new Scanner(System.in);
				supClass = in2.nextInt();
				
				MasterRoutine masterRoutine = new MasterRoutine(repository, 2, subClass, supClass);
				masterRoutine.computeResult();
				//System.out.println(masterRoutine.);
				
				Set<ExplanationRoutine> justifications = masterRoutine.getJustifications();
				
				System.out.println("Justification size: " + justifications.size());
				
				for(ExplanationRoutine expRoutine : justifications){
					
					System.out.println(expRoutine);
//					Set<Integer> justs = expRoutine.getOriginalAxioms();
//					System.out.print("{");
//					for(Integer just : justs){
//						System.out.print(just + ",");
//					}
//					System.out.print("}\n");
				}
				
				
			}
		} else if(mode == 1){
			Date start = new Date(); 
			
			int counter = 1;
			int originalCounter = 0;
			//LogWriter logWriter = new LogWriter(PATH + RESULT);
			Collection<Integer> subsumees = classGragh.getElements();
			for(Integer subClass : subsumees){
				Collection<Integer> subsumers = classGragh.getSubsumers(subClass);
				for(Integer supClass : subsumers){
					if(subClass.equals(10702)){
						System.out.println(counter ++ + ": " + subClass + " [= " + supClass);
					}
					
					if(classMap.get(subClass) == null
							|| classMap.get(supClass) == null){
						//System.out.println(counter ++ + ": " + subClass + " [= " + supClass + "*");
						continue;
					}
					
					if(subClass == 0 || supClass == 1) {
						originalCounter ++ ;
						//System.out.println(counter ++ + ": " + subClass + " [= " + supClass + "*");
						continue;
					}
					
					//System.out.println(counter ++ + ": " + subClass + " [= " + supClass);
					MasterRoutine masterRoutine = new MasterRoutine(repository, 2, subClass, supClass);
					masterRoutine.computeResult();
					
					
					
					if(masterRoutine.getJustifications().size() == 0)
						System.exit(-1);
					//logWriter.write(subClass + " [= " + supClass + "\t:" + masterRoutine.getComputingTime());	
				}
			}
			
			Date end = new Date(); 
			System.out.println("Computing time: " + (end.getTime() - start.getTime()) + "ms.");
			System.out.println("Original Count: " + originalCounter);
			//logWriter.closeWriter();
		}
	}

}
