package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.plealog.genericapp.api.EZEnvironment;

public class DataBaseConfigReader {
	
	private static String password = null;
	private static String database = null;
	private static String user = null;
	private static String passwordFile = null;
	
	public DataBaseConfigReader(String packageName, String fileName){
		InputStream fis = null;
		Properties appProperties = new Properties();
    	try {
    		fis = new FileInputStream(fileName);
    		appProperties.load(fis);
			passwordFile = packageName + File.separator + appProperties.getProperty("section.a.config");			
			setParams();
		} catch (Exception e) {
			EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), e.toString());
		}
	}
	
	public void setParams() throws IOException {
		Properties dbConfigProperties = new Properties();
	    InputStream fis = new FileInputStream(passwordFile);
	    dbConfigProperties.load(fis);
    	password = dbConfigProperties.getProperty("database.password");
    	database = dbConfigProperties.getProperty("database.host");
    	user = dbConfigProperties.getProperty("database.user");
	}
	
	
	public static String getPassword() {
		return password;
	}
	
	public static String getName() {
		return user;
	}
	
	public static String getHost() {
		return database;
	}
}
