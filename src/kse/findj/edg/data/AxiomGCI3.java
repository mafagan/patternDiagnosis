package kse.findj.edg.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
* Axiom of the form:
* <ul>
* <li>&exist; r <i>.</i> A &#8849; B</li>
* </ul>
* 
*/
public class AxiomGCI3 implements MyAxiom, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int propertyInSubClass;
	private final int classInSubClass;
	private final int superClass;
	private final String sha1;
	
	/**
	 * Constructs a new GCI-3 axiom.
	 * 
	 * @param leftProp
	 *            object property identifier for the left-hand part
	 * @param leftCl
	 *            class identifier for the left-hand part
	 * @param rightCl
	 *            superclass identifier
	 */
	public AxiomGCI3(int leftProp, int leftCl, int rightCl) {
		this.classInSubClass = leftCl;
		this.propertyInSubClass = leftProp;
		this.superClass = rightCl;
		this.sha1 = SHA1Util.hex_sha1(
				this.propertyInSubClass
				+ "_"
				+this.classInSubClass
				+ "_"
				+this.superClass
				);
	}

	public int getClassInSubClass() {
		return classInSubClass;
	}

	public int getPropertyInSubClass() {
		return propertyInSubClass;
	}

	public int getSuperClass() {
		return superClass;
	}

	public String getSHA1() {
		return sha1;
	}

	@Override
	public String toString() {
		return "AxiomGCI3 [propertyInSubClass=" + propertyInSubClass
				+ ", classInSubClass=" + classInSubClass + ", superClass="
				+ superClass + ", sha1=" + sha1 + "]";
	}
	
	public String getAxiomDescription(){
		return "AxiomGCI3 [" + propertyInSubClass + "." + classInSubClass + "[=" + superClass + "]";
	}

	@Override
	public Set<Integer> getDomainElementSet() {
		// TODO Auto-generated method stub
		Set<Integer> res = new HashSet<Integer>();
		res.add(propertyInSubClass);
		res.add(classInSubClass);
		res.add(superClass);
		return res;
	}
	
}
