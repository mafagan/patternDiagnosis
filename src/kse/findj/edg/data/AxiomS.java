package kse.findj.edg.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class AxiomS implements MyAxiom, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int subClass;
	private final int superClass;
	private final String sha1;
	
	public AxiomS(int subCl, int superCl) {
		this.subClass = subCl;
		this.superClass = superCl;
		this.sha1 = SHA1Util.hex_sha1(
						this.subClass
						+ "_"
						+ this.superClass
						);
	}

	public int getSubClass() {
		return subClass;
	}

	public int getSuperClass() {
		return superClass;
	}

	public String getSHA1() {
		return sha1;
	}

	@Override
	public String toString() {
		return "AxiomS [subClass=" + subClass + ", superClass=" + superClass
				+ ", sha1=" + sha1 + "]";
	}
	
	public String getAxiomDescription(){
		return "AxiomS [" + subClass + "[=" + superClass + "]";
	}

	@Override
	public Set<Integer> getDomainElementSet() {
		// TODO Auto-generated method stub
		Set<Integer> res = new HashSet<Integer>();
		res.add(subClass);
		res.add(superClass);
		return res;
	}
		
}
