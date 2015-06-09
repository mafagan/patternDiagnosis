package kse.findj.edg.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Axiom of the form:
 * <ul>
 * <li>r &#8728; s &#8849; t</li>
 * </ul>
 * 
 */
public class AxiomRI3  implements MyAxiom, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int leftSubProperty;
	private final int rightSubProperty;
	private final int superProperty;
	private final String sha1;

	/**
	 * Constructs a new RI-3 axiom
	 * 
	 * @param leftLeftProp
	 *            object property identifier for the left-hand object property
	 *            on the composition
	 * @param leftRightProp
	 *            object property identifier for the right-hand object property
	 *            on the composition
	 * @param rightProp
	 *            object property identifier for super object property
	 */
	public AxiomRI3(int leftLeftProp, int leftRightProp, int rightProp) {
		this.leftSubProperty = leftLeftProp;
		this.rightSubProperty = leftRightProp;
		this.superProperty = rightProp;
		this.sha1 = 
				SHA1Util.hex_sha1(
				this.leftSubProperty
				+ "_"
				+ this.rightSubProperty
				+ "_"
				+ this.superProperty
				);
	}

	public int getLeftSubProperty() {
		return leftSubProperty;
	}

	public int getRightSubProperty() {
		return rightSubProperty;
	}

	public int getSuperProperty() {
		return superProperty;
	}

	public String getSHA1() {
		return sha1;
	}

	@Override
	public String toString() {
		return "AxiomRI3 [leftSubProperty=" + leftSubProperty
				+ ", rightSubProperty=" + rightSubProperty + ", superProperty="
				+ superProperty + ", sha1=" + sha1 + "]";
	}

	public String getAxiomDescription(){
		return "AxiomRI3 [" + leftSubProperty + "o" + rightSubProperty + "[=" + superProperty + "]";
	}

	@Override
	public Set<Integer> getDomainElementSet() {
		// TODO Auto-generated method stub
		Set<Integer> res = new HashSet<Integer>();
		res.add(leftSubProperty);
		res.add(rightSubProperty);
		res.add(superProperty);
		return res;
	}
	
}
