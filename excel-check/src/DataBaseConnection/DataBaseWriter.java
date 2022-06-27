package DataBaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import oracle.ucp.util.Pair;

public class DataBaseWriter{
	
	private String url = null;
	private String name = null;
	private String password = null;
	
	public void setDBParams(String url, String name, String password) {
		this.url = url;
		this.name = name;
		this.password = password;
	}
	
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

	public void write(ArrayList<Pair<String, String>> excelRecord, String tableName) throws SQLException {
		try (Connection conn = DriverManager.getConnection(url, name, password);
				Statement stat = conn.createStatement()){
			String updateString = makeSQLCommand(tableName, excelRecord);
			stat.executeUpdate(updateString);
		} catch(Exception ex){
            System.out.println("Connection failed...");
            System.out.println(ex.getMessage());
        }
	}
}
