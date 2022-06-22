package FileHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.plealog.genericapp.api.EZEnvironment;

import FileHandler.CellCheck.CellCheck;
import FileHandler.CellCheck.StringCellCheck;

public class DowntimesReader implements ExcelReader{
	File excelFile;
	public DowntimesReader(String fileName){
		excelFile = new File(fileName);
	}

	@Override
	public void read() throws IOException {
		
		FileInputStream file = new FileInputStream(excelFile);
		Workbook workbook = new XSSFWorkbook(file);
		for (int k = 1; k < workbook.getNumberOfSheets(); k++) {
			Sheet sheet = workbook.getSheetAt(k);
			HashSet<Integer> blueColumns = new HashSet<>();
			Row headRow = sheet.getRow(0);
			if (headRow == null)
				continue;
			for (Cell cell : headRow) {
				CellStyle headStyle = cell.getCellStyle();
				if (headStyle.getFillForegroundColorColor() == null)
					break;
				if (headStyle.getFillForegroundColorColor().toString().equals("org.apache.poi.xssf.usermodel.XSSFColor@c6292f46"))
					blueColumns.add(cell.getColumnIndex());
			}
			for (Row row : sheet) {
				if (row.getRowNum() == 0)
					continue;
				StringBuilder strRow = new StringBuilder();
			    for (Cell cell : row) {
					if (!blueColumns.contains(cell.getColumnIndex()))
						continue;
					if (cell == null)
						continue;
					switch (cell.getCellType()) {
		            case STRING:
		            	CellCheck check = new StringCellCheck();
		            	if(!check.checkCell(cell))
		            		continue;
		            	strRow.append(cell.getRichStringCellValue().getString());
		            	strRow.append(';');
		            	break;
		            case NUMERIC:
		            	if (DateUtil.isCellDateFormatted(cell)) {
		            		strRow.append(cell.getDateCellValue());
			            	strRow.append(';');
		            	} else {
		            		strRow.append(cell.getNumericCellValue());
			            	strRow.append(';');
		            	}
		            	break;
		            case BOOLEAN:
		            	cell.getBooleanCellValue();
		            	break;
		            case FORMULA:
		            	cell.getCellFormula();
		            	break;
					}
				}
			    if (!strRow.isEmpty())
			    	EZEnvironment.displayInfoMessage(EZEnvironment.getParentFrame(), strRow.toString());
			}
			workbook.close();
		}
	}
}
