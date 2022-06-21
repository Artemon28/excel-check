package FileHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
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
		Sheet sheet = workbook.getSheetAt(2);
		
		int rowNum = sheet.getLastRowNum();
		int columnNum = sheet.getRow(0).getPhysicalNumberOfCells();
		
		for (int i = 0; i < columnNum; i++) {
			StringBuilder strColumn = new StringBuilder();
			Cell headCell = sheet.getRow(0).getCell(0);
			CellStyle headStyle = headCell.getCellStyle();
			short blueColorIndex = 64;
			if (headStyle.getFillBackgroundColor() != blueColorIndex)
				continue;
			
			for (int j = 0; j <= rowNum; j++){
				Cell cell = sheet.getRow(j).getCell(i);
				switch (cell.getCellType()) {
	            case STRING:
	            	strColumn.append(cell.getRichStringCellValue().getString());
	            	CellCheck check = new StringCellCheck();
	            	check.checkCell(cell);
	            	strColumn.append(';');
	            	break;
	            case NUMERIC:
	            	if (DateUtil.isCellDateFormatted(cell)) {
	            	    cell.getDateCellValue();
	            	} else {
	            	    cell.getNumericCellValue();
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
			EZEnvironment.displayInfoMessage(EZEnvironment.getParentFrame(), strColumn.toString());
		}
		workbook.close();
	}
}
