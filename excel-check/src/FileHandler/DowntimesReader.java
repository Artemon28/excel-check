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

public class DowntimesReader implements ExcelReader {
	private File excelFile;
	private HashMap<String, String> columnMap = new HashMap<>();
	private String columnSettingsPath = "ExcelColumnSettings" + "/" + "DowntimesTypes.xml";
	
	public DowntimesReader(String fileName){
		excelFile = new File(fileName);
		EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), this.getClass().getPackage().getName() + File.separator + columnSettingsPath);
		readColumnSettings(this.getClass().getClassLoader().getResourceAsStream(this.getClass().getPackage().getName() + "/" + columnSettingsPath));
	}
	
	private void readColumnSettings(InputStream path) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(path);
			doc.getDocumentElement().normalize();
			NodeList list = doc.getDocumentElement().getChildNodes();
			for (int temp = 0; temp < list.getLength(); temp++) {
				Node node = list.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
		            Element element = (Element) node;
		            String name = element.getElementsByTagName("name").item(0).getTextContent();
		            String type = element.getElementsByTagName("type").item(0).getTextContent();
		            columnMap.put(name, type);
		        }
			}
		} catch (Exception e) {
			EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), e.getMessage());
		}
	}
	
	@Override
	public void read(DataBaseWriter dbw) throws IOException {
		
		FileInputStream file = new FileInputStream(excelFile);
		Workbook workbook = new XSSFWorkbook(file);
		for (int k = 1; k < workbook.getNumberOfSheets(); k++) {
			Sheet sheet = workbook.getSheetAt(k);
			HashMap<Integer, String> blueColumns = new HashMap<>();
			Row headRow = sheet.getRow(0);
			String tableName = getTableName(sheet.getSheetName());
			
			if (tableName == null) {
				EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), "Unknown table for sheet " 
			+ sheet.getSheetName() + " , please check that the table name is written at the end of the line in brackets");
				continue;
			}
			if (headRow == null)
				continue;
			
			for (Cell cell : headRow) {
				CellStyle headStyle = cell.getCellStyle();
				String blueColor = "org.apache.poi.xssf.usermodel.XSSFColor@c6292f46";
				if (headStyle.getFillForegroundColorColor() == null)
					break;
				if (headStyle.getFillForegroundColorColor().toString().equals(blueColor))
					blueColumns.put(cell.getColumnIndex(), 
							getColumnName(cell.getRichStringCellValue().getString().replaceAll("\\s+","")));
			}
			for (Row row : sheet) {
				if (row.getRowNum() == 0)
					continue;
				ArrayList<Pair<String, String>> rowList = new ArrayList<Pair<String, String>>();
			    for (Cell cell : row) {
					if (!blueColumns.containsKey(cell.getColumnIndex()))
						continue;
					if (cell == null)
						continue;
					switch (cell.getCellType()) {
		            case STRING:
		            	CellCheck check;
	            		if (!columnMap.containsKey(blueColumns.get(cell.getColumnIndex()))) {
	            			EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), "no such column like: " +
	            		blueColumns.get(cell.getColumnIndex()) +
	            		" in the xml file. Please check name of column or add this column to xml file");
	            			break;
	            		}
	            		
		            	if (columnMap.get(blueColumns.get(cell.getColumnIndex())).equals("LatinKey")) {
		            		check = new LatinStringCheck();
		            	}
		            		
		            	else
		            		check = new StringCellCheck();
		            	if(!check.checkCell(cell))
		            		continue;
		            	rowList.add(new Pair(blueColumns.get(cell.getColumnIndex()), cell.getRichStringCellValue().getString()));
		            	break;
		            case NUMERIC:
		            	if (DateUtil.isCellDateFormatted(cell)) {
			            	rowList.add(new Pair(blueColumns.get(cell.getColumnIndex()), cell.getDateCellValue()));
		            	} else {
		            		rowList.add(new Pair(blueColumns.get(cell.getColumnIndex()), cell.getNumericCellValue()));
		            	}
		            	break;
		            case BOOLEAN:
		            	cell.getBooleanCellValue();
		            	EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), "unknown type BOOLEAN");
		            	break;
		            case FORMULA:
		            	cell.getCellFormula();
		            	EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), "unknown type FORMULA");
		            	break;
		            default:
		            	EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), "unknown type");
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
	
	private String getColumnName(String cellStr) {
		int slashIndex = cellStr.lastIndexOf("/");
		String columnName = cellStr.substring(slashIndex + 1);
		return columnName.replaceAll("\\s+","");
	}
	
	private String getTableName(String sheetName){
		int openBracketIndex = sheetName.lastIndexOf("(");
		int closeBracketIndex = sheetName.lastIndexOf(")");
		if (openBracketIndex == -1 || closeBracketIndex == -1) {
			return null;
		}
		String tablename = sheetName.substring(openBracketIndex + 1, closeBracketIndex);
		return tablename.replaceAll("\\s+","");
	}
}
