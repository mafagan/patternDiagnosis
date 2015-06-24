package com.sysu.ss.exp;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kse.findj.edg.data.AxiomGCI0;
import kse.findj.edg.data.AxiomGCI1;
import kse.findj.edg.data.AxiomGCI2;
import kse.findj.edg.data.AxiomGCI3;
import kse.findj.edg.data.AxiomR;
import kse.findj.edg.data.AxiomRI2;
import kse.findj.edg.data.AxiomS;
import kse.findj.edg.data.MyAxiom;
import kse.findj.reasoner.RuleBasedCELReasoner;

import org.coode.owlapi.manchesterowlsyntax.OntologyAxiomPair;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;

import de.tudresden.inf.lat.jcel.coreontology.axiom.GCI0Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.GCI1Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.GCI2Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.GCI3Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.NormalizedIntegerAxiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.RI1Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.RI2Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.RI3Axiom;
import de.tudresden.inf.lat.jcel.coreontology.axiom.RangeAxiom;
import de.tudresden.inf.lat.jcel.owlapi.main.JcelReasoner;

public class PatternDiagnosis {
        private Set<MyAxiom> patternAxioms;
        private AxiomGCI0 observation;
        private File ontFile;
        private AxiomS subsumAxiomS;
        private PrintStream tt;
        
        public PatternDiagnosis(File ontFile, AxiomGCI0 observation, Set<MyAxiom> patternAxioms, AxiomS subsumAxiomS) {
                // TODO Auto-generated constructor stub
                this.ontFile = ontFile;
                this.observation = observation;
                this.patternAxioms = patternAxioms;
                this.subsumAxiomS = subsumAxiomS;
                this.tt = System.out;
        }
        
        public void generate() throws OWLOntologyCreationException{
//              Set<String> elementDomainSet = new HashSet<String>();
//              OWLOntologyManager inputOntologyManager = OWLManager.createOWLOntologyManager();
//              OWLOntologyManager outputOntologyManager = OWLManager.createOWLOntologyManager();
//
//              File ontoFile = new File("ontology/owltest.owl");
//              // Load your ontology.
//              OWLOntology ont = inputOntologyManager.loadOntologyFromOntologyDocument(ontoFile);
//              Set<OWLAxiom> axiomSet = ont.getAxioms();
//              //tt.println(axiomSet);
//              
//              Iterator<OWLAxiom> it = axiomSet.iterator();
//              
//              OWLSubClassOfAxiom axiom = (OWLSubClassOfAxiom)it.next();
//              OWLObjectIntersectionOf obAxiom = (OWLObjectIntersectionOf) axiom.getSubClass();
//              tt.println(obAxiom.getOperands());
//              tt.println(axiom.getSuperClass());
//
//              //tt.println(axiom.getNestedClassExpressions());
//              Set<OWLClassExpression> tmpClassExpression =  axiom.getNestedClassExpressions();
                
//              tt.println("\nexpression begin:");
//              Iterator<OWLClassExpression> iterator = tmpClassExpression.iterator();
//              while (iterator.hasNext()) {
//                      OWLClassExpression tmp = iterator.next();
//                      tt.println();
//              }
//              tt.println("expression end:\n");

                //tt.println(axiomSet);
                RuleBasedCELReasoner celReasoner = new RuleBasedCELReasoner(this.ontFile);
                
                Set<NormalizedIntegerAxiom> tAxiom = celReasoner.getNormalizedIntegerAxiomSet();
                Set<Integer> tboxClassDomainSet = new HashSet<Integer>();
                Set<Integer> tboxPropertyDomainSet = new HashSet<Integer>(); 
                
                Iterator<NormalizedIntegerAxiom> iterator = tAxiom.iterator();
                while (iterator.hasNext()) {
                        NormalizedIntegerAxiom tmpAxiom = iterator.next();
                        
                        if (tmpAxiom instanceof GCI0Axiom) {
                                GCI0Axiom temp = (GCI0Axiom)tmpAxiom;
                                tboxClassDomainSet.add(temp.getSubClass());
                                tboxClassDomainSet.add(temp.getSuperClass());
                        }else if (tmpAxiom instanceof GCI1Axiom) {
                                GCI1Axiom temp = (GCI1Axiom)tmpAxiom;
                                tboxClassDomainSet.add(temp.getLeftSubClass());
                                tboxClassDomainSet.add(temp.getRightSubClass());
                                tboxClassDomainSet.add(temp.getSuperClass());
                        }else if (tmpAxiom instanceof GCI2Axiom) {
                                GCI2Axiom temp = (GCI2Axiom)tmpAxiom;
                                tboxClassDomainSet.add(temp.getSubClass());
                                tboxClassDomainSet.add(temp.getClassInSuperClass());
                                tboxPropertyDomainSet.add(temp.getPropertyInSuperClass());
                                
                        }else if (tmpAxiom instanceof GCI3Axiom) {
                                GCI3Axiom temp = (GCI3Axiom)tmpAxiom;
                                tboxClassDomainSet.add(temp.getClassInSubClass());
                                tboxClassDomainSet.add(temp.getSuperClass());
                                tboxPropertyDomainSet.add(temp.getPropertyInSubClass());
                        }else if (tmpAxiom instanceof RI1Axiom) {
                                RI1Axiom temp = (RI1Axiom)tmpAxiom;
                                tboxPropertyDomainSet.add(temp.getSuperProperty());
                        }else if (tmpAxiom instanceof RI2Axiom) {
                                RI2Axiom temp = (RI2Axiom)tmpAxiom;
                                tboxPropertyDomainSet.add(temp.getSubProperty());
                                tboxPropertyDomainSet.add(temp.getSuperProperty());
                        }else if (tmpAxiom instanceof RI3Axiom) {
                                RI3Axiom temp = (RI3Axiom)tmpAxiom;
                                tboxPropertyDomainSet.add(temp.getLeftSubProperty());
                                tboxPropertyDomainSet.add(temp.getRightSubProperty());
                                tboxPropertyDomainSet.add(temp.getSuperProperty());
                                
                        }else if (tmpAxiom instanceof RangeAxiom) {
                                RangeAxiom temp = (RangeAxiom)tmpAxiom;
                                tboxPropertyDomainSet.add(temp.getProperty());
                                tboxPropertyDomainSet.add(temp.getRange());
                        }
                        
                
                        //elementDomain.addAll(tmpAxiom.getDomainElementSet());
                }
                
                tboxClassDomainSet.add(observation.getSubClass());
                tboxClassDomainSet.add(observation.getSuperClass());
                
                Set<Integer> patternClassDomainSet = new HashSet<Integer>();
                Set<Integer> patternPropertyDomainSet = new HashSet<Integer>();
                
                Iterator<MyAxiom> myAxiomIterator = patternAxioms.iterator();
                
                while(myAxiomIterator.hasNext()){
                	MyAxiom curAxiom = myAxiomIterator.next();
                	
                	if (curAxiom instanceof AxiomS) {
						AxiomS tmpAxiomS = (AxiomS) curAxiom;
						patternClassDomainSet.add(tmpAxiomS.getSubClass());
						patternClassDomainSet.add(tmpAxiomS.getSuperClass());
					}else if (curAxiom instanceof AxiomGCI0) {
						AxiomGCI0 tmpAxiomGCI0 = (AxiomGCI0) curAxiom;
						patternClassDomainSet.add(tmpAxiomGCI0.getSubClass());
						patternClassDomainSet.add(tmpAxiomGCI0.getSuperClass());
					}else if (curAxiom instanceof AxiomGCI1) {
						AxiomGCI1 tmpAxiomGCI1 = (AxiomGCI1) curAxiom;
						patternClassDomainSet.add(tmpAxiomGCI1.getLeftSubClass());
						patternClassDomainSet.add(tmpAxiomGCI1.getRightSubClass());
						patternClassDomainSet.add(tmpAxiomGCI1.getSuperClass());
					}else if (curAxiom instanceof AxiomGCI2) {
						AxiomGCI2 tmpAxiomGCI2 = (AxiomGCI2) curAxiom;
						patternClassDomainSet.add(tmpAxiomGCI2.getSubClass());
						patternClassDomainSet.add(tmpAxiomGCI2.getClassInSuperClass());
						patternPropertyDomainSet.add(tmpAxiomGCI2.getPropertyInSuperClass());
					}else if (curAxiom instanceof AxiomGCI3) {
						AxiomGCI3 tmpAxiomGCI3 = (AxiomGCI3) curAxiom;
						patternClassDomainSet.add(tmpAxiomGCI3.getClassInSubClass());
						patternPropertyDomainSet.add(tmpAxiomGCI3.getPropertyInSubClass());
						patternClassDomainSet.add(tmpAxiomGCI3.getSuperClass());
					}else if (curAxiom instanceof AxiomR) {
						AxiomR tmpAxiomR = (AxiomR) curAxiom;
						patternClassDomainSet.add(tmpAxiomR.getSubClass());
						patternPropertyDomainSet.add(tmpAxiomR.getPropertyInSuperClass());
						patternClassDomainSet.add(tmpAxiomR.getClassInSuperClass());
					}else if (curAxiom instanceof AxiomRI2) {
						AxiomRI2 tmpAxiomRI2 = (AxiomRI2) curAxiom;
						patternPropertyDomainSet.add(tmpAxiomRI2.getSubProperty());
						patternPropertyDomainSet.add(tmpAxiomRI2.getSuperProperty());
					}
                }
                
                
                
                //先添加父类元素以及子类元素的映射
                Map<Integer, Integer> classElementMap = new HashMap<Integer, Integer>();
                Map<Integer, Integer> propertyElementMap = new HashMap<Integer, Integer>();
                
                classElementMap.put(subsumAxiomS.getSubClass(), observation.getSubClass());
                classElementMap.put(subsumAxiomS.getSuperClass(), observation.getSubClass());
                
                tboxClassDomainSet.remove(observation.getSubClass());
                tboxClassDomainSet.remove(observation.getSuperClass());
                
                patternClassDomainSet.remove(subsumAxiomS.getSubClass());
                patternClassDomainSet.remove(subsumAxiomS.getSuperClass());
                
                //转化成List进行枚举
                ArrayList<Integer> tboxClassDomainList = new ArrayList<Integer>(tboxClassDomainSet);
                ArrayList<Integer> tboxPropertyDomainList = new ArrayList<Integer>(tboxPropertyDomainSet);
                
                ArrayList<Integer> patternClassDomainList = new ArrayList<Integer>(patternClassDomainSet);
                ArrayList<Integer> patternPropertyDomainList = new ArrayList<Integer>(patternPropertyDomainSet);
                
                
                //处理pattern的元素比tbox元素要多的情况
                
                if (tboxClassDomainSet.size() < patternClassDomainSet.size() || tboxPropertyDomainSet.size() < patternPropertyDomainList.size()) {
					//TODO
                	
				}
                
                Integer patternClassSizeInteger = patternClassDomainSet.size();
                Integer patternPropertySizeInteger = patternPropertyDomainSet.size();
                
                ElementData elementData = new ElementData();
                elementData.classMap = classElementMap;
                elementData.propertyMap = propertyElementMap;
                elementData.tboxClassDomainList = tboxClassDomainList;
                elementData.tboxPropertyDomainList = tboxPropertyDomainList;
                elementData.patternClassDomainList = patternClassDomainList;
                elementData.patternPropertyDomainList = patternPropertyDomainList;
                
                //classDomainPermutation(0, );
        }
        
        private void classDomainPermutation(Integer cur){
        	
        }
        
        private void propertyDomainPermutation(Integer cur){
        	
        }
}

class ElementData{
	public List<Integer> tboxClassDomainList;
	public List<Integer> tboxPropertyDomainList;
	public List<Integer> patternClassDomainList;
	public List<Integer> patternPropertyDomainList;
	
	public Map< Integer, Integer> classMap;
	public Map<Integer, Integer> propertyMap;
}
