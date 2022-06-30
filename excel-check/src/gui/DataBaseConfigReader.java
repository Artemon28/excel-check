package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.plealog.genericapp.api.EZEnvironment;
/**
 * class that reads data to access the Database and get params of it
 * @author Chaykov Artemiy
 *
 */
public class DataBaseConfigReader {
	
	private static String password = null;
	private static String database = null;
	private static String user = null;
	private static String passwordFile = null;
	
	/**
	 * reads data from configuration files
	 * @param packageName name of folder with config files
	 * @param fileName name of main config file editor.desc
	 */
	public DataBaseConfigReader(String packageName, String fileName){
		InputStream fis = null;
		Properties appProperties = new Properties();
    	try {
    		fis = new FileInputStream(fileName);
    		appProperties.load(fis);
    		/**
    		 * in section.a.config file with data to DB
    		 */
			passwordFile = packageName + File.separator + appProperties.getProperty("section.a.config");			
			setParams();
		} catch (Exception e) {
			EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), e.toString());
		}
	}
	
	/**
	 * get params from config files with DB data
	 * @throws IOException
	 */
	public void setParams() throws IOException {
		Properties dbConfigProperties = new Properties();
	    InputStream fis = new FileInputStream(passwordFile);
	    dbConfigProperties.load(fis);
    	password = dbConfigProperties.getProperty("database.password");
    	database = dbConfigProperties.getProperty("database.host");
    	user = dbConfigProperties.getProperty("database.user");
	}
	
	
	/**
	 * @return password of database
	 */
	public static String getPassword() {
		return password;
	}
	
	/**
	 * @return user of database
	 */
	public static String getName() {
		return user;
	}
	
	/**
	 * @return host of database
	 */
	public static String getHost() {
		return database;
	}
}
