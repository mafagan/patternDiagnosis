package kse.findj.edg.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Axiom of the form:
 * <ul>
 * <li>A &#8849; &exist; r <i>.</i> B</li>
 * </ul>
 * 
 */
public class AxiomGCI2 implements MyAxiom, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int subClass;
	private final int propertyInSuperClass;
	private final int classInSuperClass;
	private final String sha1;
	
	/**
	 * Constructs a new GCI-2 axiom.
	 * 
	 * @param leftCl
	 *            subclass identifier
	 * @param rightProp
	 *            object property identifier
	 * @param rightCl
	 *            class identifier for the right-hand part
	 */
	public AxiomGCI2(int leftCl, int rightProp, int rightCl) {
		this.subClass = leftCl;
		this.propertyInSuperClass = rightProp;
		this.classInSuperClass = rightCl;
		this.sha1 = SHA1Util.hex_sha1(
				this.subClass
				+ "_"
				+ this.propertyInSuperClass
				+ "_"
				+ this.classInSuperClass
				);
	}

	public int getClassInSuperClass() {
		return classInSuperClass;
	}

	public int getPropertyInSuperClass() {
		return propertyInSuperClass;
	}

	public int getSubClass() {
		return subClass;
	}

	public String getSHA1() {
		return sha1;
	}

	@Override
	public String toString() {
		return "AxiomGCI2 [subClass=" + subClass + ", propertyInSuperClass="
				+ propertyInSuperClass + ", classInSuperClass="
				+ classInSuperClass + ", sha1=" + sha1 + "]";
	}
	
	public String getAxiomDescription(){
		return "AxiomGCI2 [" + subClass + "[=-]" + propertyInSuperClass + "." + classInSuperClass + "]";
	}

	@Override
	public Set<Integer> getDomainElementSet() {
		// TODO Auto-generated method stub
		Set<Integer> res = new HashSet<Integer>();
		res.add(subClass);
		res.add(propertyInSuperClass);
		res.add(classInSuperClass);
		return res;
	}
	
}
