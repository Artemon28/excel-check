package FileHandler.CellCheck;

import org.apache.poi.ss.usermodel.Cell;
/**
 * checking the correctness of filling in a cell
 * @author los28
 *
 */
public abstract class CellCheck {
	static int MAX_DB__FIELD_LENGTH = 128;
	
	public abstract boolean checkCell(Cell cell);

	/**
	 * checking the maximum string length
	 * @param str
	 * @return return true if String smaller or equals of the allowed size in the database
	 */
	protected boolean isRightLenght(String str) {
		if (str.length() > MAX_DB__FIELD_LENGTH) {
			return false;
		}
		return true;
	}
	
	/**
	 * checking if in the cell is excess spaces before and after String
	 * @param str
	 * @return
	 */
	protected boolean isExcessSpaces(String str) {
		if (str.charAt(0) == ' ' || str.charAt(str.length() - 1) == ' ')
			return false;
		return true;
	}
	
	/**
	 * Checking if there are Cyrillic letter in Latin key word
	 * @param str
	 * @return
	 */
	protected boolean isCyrillicInLatin(String str) {
		for (int i = 0; i < str.length(); i++) {
	        if (Character.UnicodeBlock.of(str.charAt(i)).equals(Character.UnicodeBlock.CYRILLIC))
	            return false;
	    }
		return true;
	}
}
