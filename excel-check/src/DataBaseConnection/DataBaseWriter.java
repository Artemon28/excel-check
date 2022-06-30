package DataBaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.plealog.genericapp.api.EZEnvironment;

import oracle.ucp.util.Pair;

/**
 * class for filling the database
 * @author Chaykov Artemiy
 *
 */
public class DataBaseWriter{
	
	private String url = null;
	private String name = null;
	private String password = null;
	
	/**
	 * set params to get access to Database
	 * @param url
	 * @param name
	 * @param password
	 */
	public void setDBParams(String url, String name, String password) {
		this.url = url;
		this.name = name;
		this.password = password;
	}
	
	/**
	 * create String format SQL command to JDBC to adding all values of the one row from excel
	 * @param tableName
	 * @param excelRecord
	 * @return String - sql command
	 */
	private String makeSQLCommand(String tableName, ArrayList<Pair<String, String>> excelRecord) {
		String updateString ="INSERT INTO TMP_" + tableName.toUpperCase() + " (";
		for (Pair<String, String> cell: excelRecord) {
			updateString += cell.get1st() + ", ";
		}
		updateString = updateString.substring(0, updateString.length() - 2) + ") Values (";
		for (Pair<String, String> cell: excelRecord) {
			updateString += "'" + cell.get2nd().toString() + "', ";
		}
		updateString = updateString.substring(0, updateString.length() - 2) + ")";
		return updateString;
	}

	/**
	 * write to database values of the one row in database
	 * @param excelRecord pairs of the db column and value for it
	 * @param tableName
	 * @throws SQLException
	 */
	public void write(ArrayList<Pair<String, String>> excelRecord, String tableName) throws SQLException {
		try (Connection conn = DriverManager.getConnection(url, name, password);
				Statement stat = conn.createStatement()){
			String updateString = makeSQLCommand(tableName, excelRecord);
			stat.executeUpdate(updateString);
		} catch(Exception ex){
			EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), ex.getMessage());
        }
	}
}
