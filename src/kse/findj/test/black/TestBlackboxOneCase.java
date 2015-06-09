package kse.findj.test.black;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

import kse.findj.blackbox.BlackJustFinder;
import kse.findj.module.ELModuleExtracter;
import kse.findj.module.KSEOWLOntology;

public class TestBlackboxOneCase {
	
	private String subClassURI;
	private String supClassURI;
	private KSEOWLOntology kseOntology;

	private int numOfJustifications;
	private long computingTime;
	private long extractingTime;
	private int moduleSize;
	private int subsumptionTestNumber;
	
	public TestBlackboxOneCase(String subClassURI, String supClassURI,
			KSEOWLOntology kseOntology) {
		super();
		this.subClassURI = subClassURI;
		this.supClassURI = supClassURI;
		this.kseOntology = kseOntology;
	}

	public void doTest(){
		/*
		 * Extract module.
		 */
		ELModuleExtracter moduleExtracter = new ELModuleExtracter();
		moduleExtracter.setOntology(kseOntology);
		
		//System.out.println("Extracting module...");
		//moduleExtracter.setSecondSignature(supClassURI);
		Set<OWLAxiom> module = 
				moduleExtracter.extractModule(subClassURI);
		System.out.println("Extracting done.");
		this.extractingTime = moduleExtracter.getExtractingTime();
		this.moduleSize = moduleExtracter.getModuleSize();
		
		//if(this.extractingTime == -1) return;
		
		/*
		 * Extract justifications using black box method.
		 */
		BlackJustFinder finder = new BlackJustFinder(module);
		
		/*
		 * Test hst method.
		 */
		Set<Set<OWLAxiom>> justs = finder.hstAllJusts(
				subClassURI, 
				supClassURI, 
				module,
				1000);
		
		this.computingTime = finder.getComputingTime();			
		this.subsumptionTestNumber = finder.getSubsumptionTestNumber();
		this.numOfJustifications = justs.size();
		
		//System.out.println("Subsumption check number: " + finder.getSubsumptionTestNumber());
		//System.out.println("Number of justs: " + justs.size());
		
	}

	public int getNumOfJustifications() {
		return numOfJustifications;
	}

	public long getComputingTime() {
		return computingTime;
	}

	public long getExtractingTime() {
		return extractingTime;
	}

	public int getModuleSize() {
		return moduleSize;
	}

	public int getSubsumptionTestNumber() {
		return subsumptionTestNumber;
	}

}
