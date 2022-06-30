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

/**
 * Abstract class of reading excel files
 * @author los28
 *
 */
public abstract class ExcelReaderAbstract {
	
	/**
	 * read file into the database
	 * @param dbw 
	 * @throws IOException
	 */
	public abstract void read(DataBaseWriter dbw) throws IOException;
	
	/**
	 * prepare column settings before reading for one sheet of excel book
	 * @param path
	 * @return
	 */
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
	
	/**
	 * Further reading of the columns with only blue header
	 * @param headRow
	 * @return map with pairs number of column and the type of words in it
	 */
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
	
	/**
	 * parser of the column name
	 * @param cellStr
	 * @return return name of the column in database. It should be written after slash /
	 */
	protected String getColumnName(String cellStr) {
		int slashIndex = cellStr.lastIndexOf("/");
		String columnName = cellStr.substring(slashIndex + 1);
		return columnName.replaceAll("\\s+","");
	}
	
	/**
	 * parser of the table name
	 * @param sheetName
	 * @return return name of the table name. It should be written in the brackets ()
	 */
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
