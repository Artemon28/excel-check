package FileHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.plealog.genericapp.api.EZEnvironment;

import DataBaseConnection.DataBaseWriter;

public abstract class ExcelReaderAbstract {
	
	public abstract void read(DataBaseWriter dbw) throws IOException;
	
	protected HashMap<String, String> readColumnSettings(InputStream path) {
		HashMap<String, String> columnMap = new HashMap<>();
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
		return columnMap;
	}
	
	protected HashMap<Integer, String> getHeadMap(Row headRow){
		HashMap<Integer, String> blueColumns = new HashMap<>();
		for (Cell cell : headRow) {
			CellStyle headStyle = cell.getCellStyle();
			String blueColor = "org.apache.poi.xssf.usermodel.XSSFColor@c6292f46";
			if (headStyle.getFillForegroundColorColor() == null)
				break;
			if (headStyle.getFillForegroundColorColor().toString().equals(blueColor))
				blueColumns.put(cell.getColumnIndex(), 
						getColumnName(cell.getRichStringCellValue().getString().replaceAll("\\s+","")));
		}
		return blueColumns;
	}
	
	protected String readCell(Cell cell) {
		return null;
	}
	
	protected String getColumnName(String cellStr) {
		int slashIndex = cellStr.lastIndexOf("/");
		String columnName = cellStr.substring(slashIndex + 1);
		return columnName.replaceAll("\\s+","");
	}
	
	protected String getTableName(String sheetName){
		int openBracketIndex = sheetName.lastIndexOf("(");
		int closeBracketIndex = sheetName.lastIndexOf(")");
		if (openBracketIndex == -1 || closeBracketIndex == -1) {
			return null;
		}
		String tablename = sheetName.substring(openBracketIndex + 1, closeBracketIndex);
		return tablename.replaceAll("\\s+","");
	}
}
