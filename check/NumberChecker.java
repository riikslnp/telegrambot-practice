package check;


/**
 * Checks if a String has only number chars
 */
public class NumberChecker {

	/**
	 * Returns false if the String has other chars than numbers when converted to a char array, true if String only has numbers
	 * @param str String that is checked
	 * @return boolean
	 */
	public static boolean isNumbers(String str) {
		boolean value = false;
		char[] array=str.toCharArray();
		char[] numArray= {'0','1','2','3','4','5','6','7','8','9'};
		
		for(int k=0;k<array.length;k++) {
			 for(int j=0;j<numArray.length;j++) {
				 if(numArray[j]==array[k]) value=true;
			 }
			 if(!value) return value;
			 else if(k!=array.length-1) value=false; 
		}
		
		return value;
	}
	
}
