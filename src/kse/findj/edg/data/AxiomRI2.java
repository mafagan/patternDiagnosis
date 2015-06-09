package kse.findj.edg.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Axiom of the form:
 * <ul>
 * <li>r &#8849; s</li>
 * </ul>
 * 
 */
public class AxiomRI2  implements MyAxiom, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int subProperty;
	private final int superProperty;
	private final String sha1;

	/**
	 * Constructs a new axiom RI-2.
	 * 
	 * @param leftProp
	 *            object property identifier for the left-hand part of the axiom
	 * @param rightProp
	 *            object property identifier for the right-hand part of the
	 *            axiom
	 */
	public AxiomRI2(int leftProp, int rightProp) {
		this.subProperty = leftProp;
		this.superProperty = rightProp;
		this.sha1 = SHA1Util.hex_sha1(
				this.subProperty 
				+ "_"
				+ this.superProperty
				);
	}

	public int getSubProperty() {
		return subProperty;
	}

	public int getSuperProperty() {
		return superProperty;
	}

	public String getSHA1() {
		return sha1;
	}

	@Override
	public String toString() {
		return "AxiomRI2 [subProperty=" + subProperty + ", superProperty="
				+ superProperty + ", sha1=" + sha1 + "]";
	}
	
	public String getAxiomDescription(){
		return "AxiomRI2 [" + subProperty + "[=" + superProperty + "]";
	}

	@Override
	public Set<Integer> getDomainElementSet() {
		// TODO Auto-generated method stub
		Set<Integer> res = new HashSet<Integer>();
		res.add(subProperty);
		res.add(superProperty);
		return res;
	}
	
}
