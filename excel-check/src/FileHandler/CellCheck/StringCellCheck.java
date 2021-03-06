package FileHandler.CellCheck;

import org.apache.poi.ss.usermodel.Cell;

import com.plealog.genericapp.api.EZEnvironment;

/**
 * checking the correctness of filling in a cell with the String type
 * @author Chaykov Artemiy
 *
 */
public class StringCellCheck extends CellCheck{
	
	@Override
	public boolean checkCell(Cell cell) {
		String cellValue = cell.getRichStringCellValue().getString();
		String errorMessage = new String();
		if (!isRightLenght(cellValue)) {
			errorMessage += "Length of the cell is bigger than available\n";
		}
		if (!isExcessSpaces(cellValue)) {
			errorMessage += "Excess spaces in the front or in the end of the string\n";
		}
		if (!errorMessage.isEmpty()) {
			EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), errorMessage + "In the column: " + 
			(cell.getColumnIndex() + 1) + " in the row: " + (cell.getRowIndex() + 1));
			return false;
		}
		
		return true;
	}
}
