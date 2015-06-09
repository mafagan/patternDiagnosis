package kse.findj.edg.data;

import java.util.Set;

public interface MyAxiom {
	
	public static final int ENTAILED = 0;
	public static final int ORIGINAL = 0;

	public String getSHA1();
	public String getAxiomDescription();
	public Set<Integer> getDomainElementSet();
}
