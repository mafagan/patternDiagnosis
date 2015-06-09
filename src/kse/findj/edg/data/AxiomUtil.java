package kse.findj.edg.data;

public class AxiomUtil {
	
	/**
	 * Compute SHA1 for list of entities.
	 * @param entities
	 * @return
	 */
	public static String computeSHA1(Integer... entities){
		int length = entities.length;
		String str = "";
		for(int i = 0; i < length; i++) {
			if(i == length - 1){
				str += entities[i] + "";
			} else {
				str += entities[i] + "_";
			}
		}
		return SHA1Util.hex_sha1(str);
	}

}
