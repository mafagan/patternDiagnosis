package kse.findj.reasoner;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import de.tudresden.inf.lat.jcel.core.algorithm.cel.CelExtendedOntology;
import de.tudresden.inf.lat.jcel.coreontology.axiom.NormalizedIntegerAxiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.NormalizedIntegerAxiomFactory;
import de.tudresden.inf.lat.jcel.coreontology.datatype.IntegerEntityManager;
import de.tudresden.inf.lat.jcel.ontology.axiom.complex.ComplexIntegerAxiom;
import de.tudresden.inf.lat.jcel.ontology.axiom.extension.IntegerOntologyObjectFactory;
import de.tudresden.inf.lat.jcel.ontology.axiom.extension.IntegerOntologyObjectFactoryImpl;
import de.tudresden.inf.lat.jcel.ontology.normalization.OntologyNormalizer;
import de.tudresden.inf.lat.jcel.owlapi.main.JcelReasoner;
import de.tudresden.inf.lat.jcel.owlapi.translator.Translator;

/**
 * Preprocess the input ontology, use owlapi interfaces,
 * and normalize it using jCel.
 * 
 * @author Zhangquan Zhou
 *
 */
public class OntologyPreprocessor {

	private File ontologyFile = null;
	private JcelReasoner reasoner = null;
	private OWLDataFactory dataFactory = null;
	private Set<OWLAxiom> axiomSet = null;
	private Set<OWLAxiom> extractAxiomSet = null;
	private Set<NormalizedIntegerAxiom> normalizedAxiomSet = null;
	private CelExtendedOntology extendedOntology = null;
	private OWLOntology owlOntology = null;
	private Set<Integer> originalClassSet = null;
	private Set<Integer> originalObjectPropertySet = null;
	private Set<Integer> normalizedClassSet = null;
	private Set<Integer> normalizedObjectPropertySet = null;
	private Translator translator = null;
	private IntegerOntologyObjectFactory integerOntologyObjectFactory = null;
	private IntegerEntityManager integerEntityManager = null;
	private NormalizedIntegerAxiomFactory normalizedIntegerAxiomFactory = null;
	private Set<ComplexIntegerAxiom> complexIntegerAxiomSet = null;

	//This class is to normalize the input ontology.
	public OntologyPreprocessor(String ontology) {
		ontologyFile = new File(ontology);
		loadOntology();
		extractAxiom();
		translateToComplexAxiom();
		originalSignatures();
		normalizeComplexIntegerAxiom();
		normalizedSignatures();
	}
	
	public Set<Integer> getNormalizedClassSet() {
		return normalizedClassSet;
	}

	public Set<Integer> getNormalizedObjectPropertySet() {
		return normalizedObjectPropertySet;
	}

	public Set<Integer> getOriginalClassSet() {
		return originalClassSet;
	}

	public Set<Integer> getOriginalObjectPropertySet() {
		return originalObjectPropertySet;
	}
	
	public OWLOntology getOwlOntology() {
		return owlOntology;
	}
	
	public Translator getTranslator() {
		return translator;
	}
	
	public IntegerEntityManager getIntegerEntityManager() {
		return integerEntityManager;
	}

	public NormalizedIntegerAxiomFactory getNormalizedIntegerAxiomFactory() {
		return normalizedIntegerAxiomFactory;
	}

	private void normalizedSignatures(){
		normalizedClassSet = new HashSet<Integer>();
		normalizedObjectPropertySet = new HashSet<Integer>();		
		for(NormalizedIntegerAxiom axiom : normalizedAxiomSet){
			normalizedClassSet.addAll(axiom.getClassesInSignature());
			normalizedObjectPropertySet.addAll(axiom
					.getObjectPropertiesInSignature());
		}
		normalizedClassSet.add(IntegerEntityManager.bottomClassId);
		normalizedClassSet.add(IntegerEntityManager.topClassId);
		normalizedObjectPropertySet
				.add(IntegerEntityManager.bottomObjectPropertyId);
		normalizedObjectPropertySet.add(IntegerEntityManager.topObjectPropertyId);
	}

	private void originalSignatures(){
		originalClassSet = new HashSet<Integer>();
		originalObjectPropertySet = new HashSet<Integer>();
		for (ComplexIntegerAxiom axiom : complexIntegerAxiomSet) {
			originalClassSet.addAll(axiom.getClassesInSignature());
			originalObjectPropertySet.addAll(axiom
					.getObjectPropertiesInSignature());
		}
		originalClassSet.add(IntegerEntityManager.bottomClassId);
		originalClassSet.add(IntegerEntityManager.topClassId);
		originalObjectPropertySet
				.add(IntegerEntityManager.bottomObjectPropertyId);
		originalObjectPropertySet.add(IntegerEntityManager.topObjectPropertyId);
	}

	private void extractAxiom() {
		axiomSet = reasoner.getRootOntology().getAxioms();
		extractAxiomSet = new TreeSet<OWLAxiom>();
		for (OWLAxiom axiom : axiomSet) {
			// System.out.println(axiom);
			// /* OWLClassAssertionAxiom
			// * OWLDataPropertyAssertionAxiom
			// * OWLDeclarationAxiom
			// * OWLDisjointClassesAxiom
			// * OWLEquivalentClassesAxiom
			// * OWLEquivalentObjectPropertiesAxiom
			// * OWLFunctionalObjectPropertyAxiom
			// * OWLInverseFunctionalObjectPropertyAxiom
			// * OWLInverseObjectPropertiesAxiom
			// * OWLNegativeObjectPropertyAssertionAxiom
			// * OWLObjectPropertyAssertionAxiom
			// * OWLObjectPropertyDomainAxiom
			// * OWLObjectPropertyRangeAxiom
			// * OWLReflexiveObjectPropertyAxiom
			// * OWLSubClassOfAxiom
			// * OWLSubObjectPropertyOfAxiom
			// * OWLSubPropertyChainOfAxiom
			// * OWLTransitiveObjectPropertyAxiom
			// */
			if (axiom instanceof OWLDeclarationAxiom) {
				extractAxiomSet.add(axiom);
			} else if (axiom instanceof OWLDisjointClassesAxiom) {
				extractAxiomSet.add(axiom);
			} else if (axiom instanceof OWLEquivalentClassesAxiom) {
				extractAxiomSet.add(axiom);
			} else if (axiom instanceof OWLEquivalentObjectPropertiesAxiom) {
				extractAxiomSet.add(axiom);
			} else if (axiom instanceof OWLObjectPropertyDomainAxiom) {
				extractAxiomSet.add(axiom);
			} else if (axiom instanceof OWLObjectPropertyRangeAxiom) {
				extractAxiomSet.add(axiom);
			} else if (axiom instanceof OWLSubClassOfAxiom) {
				extractAxiomSet.add(axiom);
			} else if (axiom instanceof OWLSubObjectPropertyOfAxiom) {
				extractAxiomSet.add(axiom);
			} else if (axiom instanceof OWLSubPropertyChainOfAxiom) {
				extractAxiomSet.add(axiom);
			} else if (axiom instanceof OWLTransitiveObjectPropertyAxiom) {
				extractAxiomSet.add(axiom);
			}
		}
	}

	public Set<OWLAxiom> getAxiomSet() {
		return axiomSet;
	}

	public Set<ComplexIntegerAxiom> getComplexIntegerAxiomSet() {
		return complexIntegerAxiomSet;
	}
	public Set<OWLAxiom> getExtractAxiomSet() {
		return extractAxiomSet;
	}

	public Set<NormalizedIntegerAxiom> getNormalizedAxiomSet() {
		return normalizedAxiomSet;
	}

	public File getOntologyFile() {
		return ontologyFile;
	}

	/**
	 * Load the target ontology.
	 */
	private void loadOntology() {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		try {
			owlOntology = manager
					.loadOntologyFromOntologyDocument(ontologyFile);
			reasoner = new JcelReasoner(owlOntology, false);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		dataFactory = new OWLDataFactoryImpl();

	}

	private void normalizeComplexIntegerAxiom() {
		OntologyNormalizer normalizer = new OntologyNormalizer();
		Set<NormalizedIntegerAxiom> normalizedIntegerAxiomSet = normalizer
				.normalize(complexIntegerAxiomSet, integerOntologyObjectFactory);

		normalizedAxiomSet = new HashSet<NormalizedIntegerAxiom>();
		normalizedAxiomSet.addAll(normalizedIntegerAxiomSet);

		extendedOntology = new CelExtendedOntology();
		extendedOntology.load(normalizedAxiomSet);
	}
	
	private void translateToComplexAxiom() {
		integerOntologyObjectFactory = new IntegerOntologyObjectFactoryImpl();
		integerEntityManager = integerOntologyObjectFactory.getEntityManager();
		normalizedIntegerAxiomFactory = integerOntologyObjectFactory.getNormalizedAxiomFactory();
		translator = new Translator(dataFactory, integerOntologyObjectFactory);
		translator.getTranslationRepository().addAxiomEntities(owlOntology);
		complexIntegerAxiomSet = translator.translateSA(extractAxiomSet);
	}
	
}
