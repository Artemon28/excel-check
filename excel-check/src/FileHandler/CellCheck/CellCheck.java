package FileHandler.CellCheck;

import org.apache.poi.ss.usermodel.Cell;

public abstract class CellCheck {
	static int MAX_DB__FIELD_LENGTH = 128;
	
	public abstract boolean checkCell(Cell cell);

	protected boolean isRightLenght(String str) {
		if (str.length() > MAX_DB__FIELD_LENGTH) {
			return false;
		}
		return true;
	}
	
	protected boolean isExcessSpaces(String str) {
		if (str.charAt(0) == ' ' || str.charAt(str.length() - 1) == ' ')
			return false;
		return true;
	}
	
	protected boolean isCyrillicInLatin(String str) {
		for (int i = 0; i < str.length(); i++) {
	        if (Character.UnicodeBlock.of(str.charAt(i)).equals(Character.UnicodeBlock.CYRILLIC))
	            return false;
	    }
		return true;
	}
}
