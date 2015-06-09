package kse.findj.edg.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Axiom of the form:
 * <ul>
 * <li>A<sub>1</sub> &#8851; A<sub>2</sub> &#8849; B</li>
 * </ul>
 * 
 */
public class AxiomGCI1 implements MyAxiom, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int leftSubClass;
	private final int rightSubClass;
	private final int superClass;
	private final String sha1;

	/**
	 * Constructs a new GCI-1 axiom.
	 * 
	 * @param leftSubCl
	 *            left subclass in the axiom
	 * @param rightSubCl
	 *            right subclass in the axiom
	 * @param rightCl
	 *            superclass in the axiom
	 */
	public AxiomGCI1(int leftSubCl, int rightSubCl, int rightCl) {
		this.leftSubClass = leftSubCl;
		this.rightSubClass = rightSubCl;
		this.superClass = rightCl;
		this.sha1 = SHA1Util.hex_sha1(
				this.leftSubClass
				+ "_"
				+ this.rightSubClass 
				+ "_"
				+ this.superClass
				);
	}

	public int getLeftSubClass() {
		return leftSubClass;
	}

	public int getRightSubClass() {
		return rightSubClass;
	}

	public int getSuperClass() {
		return superClass;
	}

	public String getSHA1() {
		return sha1;
	}

	@Override
	public String toString() {
		return "AxiomGCI1 [leftSubClass=" + leftSubClass + ", rightSubClass="
				+ rightSubClass + ", superClass=" + superClass + ", sha1="
				+ sha1 + "]";
	}
	
	public String getAxiomDescription(){
		return "AxiomGCI1 [" + leftSubClass + "^" + rightSubClass + "[=" + superClass + "]";
	}

	@Override
	public Set<Integer> getDomainElementSet() {
		// TODO Auto-generated method stub
		Set<Integer> res = new HashSet<Integer>();
		res.add(leftSubClass);
		res.add(rightSubClass);
		res.add(superClass);
		return res;
	}
	
}
