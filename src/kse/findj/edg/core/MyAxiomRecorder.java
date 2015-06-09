package kse.findj.edg.core;

import java.util.HashMap;
import java.util.Map;

import kse.findj.edg.data.MyAxiom;

public class MyAxiomRecorder {
	
	private int idAllocator = 0;
	private Map<Integer, MyAxiom> idToAxiom;
	private Map<String, Integer> sha1ToId;
	
	public MyAxiomRecorder(){
		this.idToAxiom = new HashMap<Integer, MyAxiom>();
		this.sha1ToId = new HashMap<String, Integer>();
	}
	
	public void addAxiom(MyAxiom axiom){
		String sha1 = axiom.getSHA1();
		synchronized (this) {
			if(!sha1ToId.containsKey(sha1)){
				sha1ToId.put(sha1, idAllocator);
				idToAxiom.put(idAllocator, axiom);
				idAllocator ++ ;
			}
		}		
	}
	
	public Integer getIdFromSHA1(String sha1, MyAxiom axiom){
		if(sha1ToId.containsKey(sha1)){
			return sha1ToId.get(sha1);
		} else {
			addAxiom(axiom);
			return sha1ToId.get(sha1);
		}		
	}
	
	public MyAxiom getAxiomFromId(Integer id){
		return idToAxiom.get(id);
	}
	
	public String getSha1FromId(Integer id){
		return idToAxiom.get(id).getSHA1();
	}

}
