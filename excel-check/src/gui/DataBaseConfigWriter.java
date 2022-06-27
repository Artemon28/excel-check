package gui;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import com.plealog.genericapp.api.EZEnvironment;

public class DataBaseConfigWriter {

	public DataBaseConfigWriter(String fileName, String packageName, String password, String user, String db) {
       	Properties appProperties = new Properties();
    	InputStream fis = null;
    	fis = this.getClass().getClassLoader().getResourceAsStream(fileName);
    	
    	try {
    		appProperties.load(fis);
			String passwordFile = appProperties.getProperty("section.a.config");
		    Properties dbConfigProperties = new Properties();
		    InputStream fis2 = this.getClass().getClassLoader().getResourceAsStream(packageName + "/conf/" + passwordFile);
		    dbConfigProperties.load(fis2);
		    dbConfigProperties.setProperty("database.password", password);
		    dbConfigProperties.setProperty("database.host", db);
		    dbConfigProperties.setProperty("database.user", user);
		} catch (Exception e) {
			EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), e.toString());
		}
	}
}
