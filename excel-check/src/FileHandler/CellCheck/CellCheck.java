package FileHandler.CellCheck;

import org.apache.poi.ss.usermodel.Cell;

public interface CellCheck {
	static int MAX_DB__FIELD_LENGTH = 128;
	
	public boolean checkCell(Cell cell);

	default boolean isRightLenght(String str) {
		if (str.length() > MAX_DB__FIELD_LENGTH) {
			return false;
		}
		return true;
	}
	
	default boolean isExcessSpaces(String str) {
		if (str.charAt(0) == ' ' || str.charAt(str.length() - 1) == ' ')
			return false;
		return true;
	}
	
	default boolean isCyrillicInLatin(String str) {
		for (int i = 0; i < str.length(); i++) {
	        if (Character.UnicodeBlock.of(str.charAt(i)).equals(Character.UnicodeBlock.CYRILLIC))
	            return false;
	    }
		return true;
	}
}
