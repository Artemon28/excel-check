package FileHandler;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.plealog.genericapp.api.EZEnvironment;

import DataBaseConnection.DataBaseWriter;
import FileHandler.CellCheck.CellCheck;
import FileHandler.CellCheck.LatinStringCheck;
import FileHandler.CellCheck.StringCellCheck;
import oracle.ucp.util.Pair;

/**
 * class for reading Defects template files
 * @author Chaykov Artemiy
 *
 */
public class DefectReader extends ExcelReaderAbstract {
	private File excelFile;
	private HashMap<String, String> columnMap = new HashMap<>();
	private String columnSettingsPath = "ExcelColumnSettings" + "/" + "DefectTypes.xml";
	
	public DefectReader(String fileName){
		excelFile = new File(fileName);
		columnMap = readColumnSettings(this.getClass().getClassLoader().getResourceAsStream(this.getClass().getPackage().getName() + "/" + columnSettingsPath));
	}

	/**
	 * reads basic excel book with only strings in cell
	 */
	public void read(DataBaseWriter dbw) throws IOException {
		FileInputStream file = new FileInputStream(excelFile);
		Workbook workbook = new XSSFWorkbook(file);
		for (int k = 1; k < workbook.getNumberOfSheets(); k++) {
			Sheet sheet = workbook.getSheetAt(k);
			Row headRow = sheet.getRow(0);
			String tableName = getTableName(sheet.getSheetName());
			if (tableName == null) {
				EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), "Unknown table for sheet " 
			+ sheet.getSheetName() + " , please check that the table name is written at the end of the line in brackets");
				continue;
			}
			if (headRow == null)
				continue;
			
			HashMap<Integer, String> blueColumns = new HashMap<>();
			blueColumns = getHeadMap(headRow);
			
			
			for (Row row : sheet) {
				if (row.getRowNum() == 0)
					continue;
				
				ArrayList<Pair<String, String>> rowList = new ArrayList<Pair<String, String>>();
			    for (Cell cell : row) {
					if (!blueColumns.containsKey(cell.getColumnIndex()) || cell == null)
						continue;
					if (!columnMap.containsKey(blueColumns.get(cell.getColumnIndex()))) {
            			EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), "no such column like: " +
            		blueColumns.get(cell.getColumnIndex()) +
            		" in the xml file. Please check name of column or add this column to xml file");
            			continue;
            		}
					
					switch (cell.getCellType()) {
		            case STRING:
		            	CellCheck check;
		            	if (columnMap.get(blueColumns.get(cell.getColumnIndex())).equals("LatinKey"))
		            		check = new LatinStringCheck();
		            	else
		            		check = new StringCellCheck();
		            	
		            	if(!check.checkCell(cell))
		            		continue;
		            	
		            	rowList.add(new Pair(blueColumns.get(cell.getColumnIndex()), cell.getRichStringCellValue().getString()));
		            	break;
		            default:
		            	EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), "unknown cell type: " + cell.getCellType() + " for Defects table");
		            	break;
					}
				}
			    if (!rowList.isEmpty())
					try {
						dbw.write(rowList, tableName);
					} catch (SQLException e) {
						EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), e.getMessage());
					}
			}
			workbook.close();
		}
	}
}
