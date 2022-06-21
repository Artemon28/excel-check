package gui;

import FileHandler.DefectReader;
import FileHandler.DowntimesReader;
import FileHandler.ExcelReader;
import FileHandler.FileImportGUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.plealog.genericapp.api.EZApplicationBranding;
import com.plealog.genericapp.api.EZEnvironment;
import com.plealog.genericapp.api.EZGenericApplication;
import com.plealog.genericapp.api.EZSplashScreen;
import com.plealog.genericapp.api.EZUIStarterListener;
import com.plealog.genericapp.api.file.EZFileUtils;

public class Main {
  public static void main(String[] args) {
    EZGenericApplication.initialize("Excel checking");

    EZApplicationBranding.setAppName("Excel checking");
    EZApplicationBranding.setAppVersion("1.0");
    EZApplicationBranding.setProviderName("Chaykov Artemiy");
    
    EZEnvironment.addResourceLocator(Main.class);
    ResourceBundle rb = ResourceBundle.getBundle(Main.class.getPackage().getName()+".menu"); 
    EZEnvironment.setUserDefinedActionsResourceBundle(rb);
    EZEnvironment.getActionsManager().addActionMenuListener(new MyActionManager());
    EZEnvironment.setUIStarterListener(new MyStarterListener());
    
    String confPath;
    try {
		confPath = EZFileUtils.terminatePath(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent());
		confPath += "conf";
	    confPath += File.separator;
	    confPath += "editor.desc";
	    EZEnvironment.setPreferencesConfigurationFile(confPath);
	} catch (URISyntaxException e) {
		e.printStackTrace();
	}
    EZGenericApplication.startApplication(args);
  }

  private static class MyStarterListener implements EZUIStarterListener{
    private EZSplashScreen splash;
    @Override
    public Component getApplicationComponent() {

      JPanel      mainPanel = new JPanel(new BorderLayout());
      JTabbedPane tabPanel = new JTabbedPane();

      tabPanel.add("My First Component", new JPanel());

      mainPanel.add(tabPanel, BorderLayout.CENTER);
      return mainPanel;
    }

    @Override
    public boolean isAboutToQuit() {
    	String packageName = this.getClass().getPackage().getName();
    	File sourceFolder = new File(EZEnvironment.getPreferencesConfigurationFile());
		String packageName2 = sourceFolder.getParent();
    	Properties prop1 = new Properties();
    	Properties prop4 = new Properties();
    	InputStream fis1 = null;
    	InputStream fis4 = null;
    	fis1 = this.getClass().getClassLoader().getResourceAsStream(packageName + "/conf/editor.desc");
    	
    	try {
    		fis4 = new FileInputStream(EZEnvironment.getPreferencesConfigurationFile());
			prop4.load(fis4);
			String passwordFile = prop4.getProperty("section.a.config");
		    Properties ppp4 = new Properties();
		    InputStream fis3 = new FileInputStream(packageName2 + "\\" + passwordFile);
		    ppp4.load(fis3);
	    	String password = ppp4.getProperty("database.password");
	    	String database = ppp4.getProperty("database.host");
	    	String user = ppp4.getProperty("database.user");
	    	
	    	prop1.load(fis1);
			String passwordFile4 = prop1.getProperty("section.a.config");
		    Properties ppp1 = new Properties();
		    InputStream fis2 = this.getClass().getClassLoader().getResourceAsStream(packageName + "/conf/" + passwordFile4);
		    ppp1.load(fis2);
	    	ppp1.setProperty("database.password", password);
	    	ppp1.setProperty("database.host", database);
	    	ppp1.setProperty("database.user", user);
		} catch (Exception e) {
			EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), e.toString());
			return true;
		}
    	return true;
    }

    @Override
    public void postStart() {
    }

    @Override
    public void preStart() {
    	InputStream fis4 = null;

    	fis4 = this.getClass().getClassLoader().getResourceAsStream(EZEnvironment.getPreferencesConfigurationFile());
    	
		File sourceFolder = new File(EZEnvironment.getPreferencesConfigurationFile());
		
		File tSourceFolder = new File(sourceFolder.getParent());
		if (!tSourceFolder.exists()) {
			tSourceFolder.mkdir();
		}
		
		String targetSsourceFolder = sourceFolder.getParent();
    	Path destDir = Paths.get(targetSsourceFolder);
    	if (fis4 == null) {   		
    		CodeSource src = this.getClass().getProtectionDomain().getCodeSource();
    		if (src != null) {
				URL jar = src.getLocation();
				ZipInputStream zip;
				try {
					zip = new ZipInputStream(jar.openStream());
					while(true) {
		    		    ZipEntry e = zip.getNextEntry();
		    		    if (e == null)
		    		      break;		    		    
		    		    String name = e.getName();
		    		    if (name.startsWith("gui/conf/")) {
		    		    	File file = new File(name);
		    		    	if (name.endsWith(".config") && (new File(destDir.resolve(file.getName()).toString()).exists()))
		    		    		continue;
		    		    	if (!name.equals("gui/conf/")) {
		    		    		Files.copy(this.getClass().getClassLoader().getResourceAsStream(file.toPath().toString().replace('\\', '/')), destDir.resolve(file.getName().replace('\\', '/')), StandardCopyOption.REPLACE_EXISTING);
		    		    	}
		    		    }
		    		  }
				} catch (Exception e1) {
					EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), e1.toString());
				}
    		} 
    		else {
    			EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), "Wrong url with src");
    		}
    	}
    	Properties prop = new Properties();	
    	try {
    		fis4 = new FileInputStream(EZEnvironment.getPreferencesConfigurationFile());
    	    prop.load(fis4);
    	    String passwordFile = prop.getProperty("section.a.config");
    	    Properties ppp = new Properties();
    	    InputStream fis2 = new FileInputStream(sourceFolder.getParent() + File.separator + passwordFile);
    	    ppp.load(fis2);
	    	String password = ppp.getProperty("database.password");
	    	String database = ppp.getProperty("database.host");
	    	
    	    while (password == null || password.isEmpty() || database == null || database.isEmpty()) {
    	    	EZEnvironment.getActionsManager().getDefaultActionHandler().handlePreferences();
    	    	Properties props = new Properties();
    	    	InputStream fis3 = new FileInputStream(sourceFolder.getParent() + File.separator + passwordFile);
    	    	props.load(fis3);
    	    	password = props.getProperty("database.password");
    	    	database = props.getProperty("database.host");
    	    	if (password == null || password.isEmpty() || database == null || database.isEmpty()) {
    	    		EZEnvironment.displayWarnMessage(EZEnvironment.getParentFrame(), "Please, enter database host and password");
    	    	}
    	    }
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), ex.getMessage());
    	}
    }
    @Override
    public void frameDisplayed() {
    }
  }
  private static class MyActionManager implements PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent event) {
    	
      if (event.getPropertyName().equals("FileImport")){
  		FileImportGUI fileImportGUI = new FileImportGUI();
  		fileImportGUI.dispose();
  		fileImportGUI.revalidate();
      }
      else if (event.getPropertyName().equals("Defect")) {
    	  FileImportGUI fileImportGUI = new FileImportGUI();
    	  ExcelReader excelReader = new DefectReader(fileImportGUI.getFileName());
    	  try {
			excelReader.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
      }
      else if (event.getPropertyName().equals("Downtimes")) {
    	  FileImportGUI fileImportGUI = new FileImportGUI();
    	  ExcelReader excelReader = new DowntimesReader(fileImportGUI.getFileName());
    	  try {
			excelReader.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
      }
      else if (event.getPropertyName().equals("ExitApp")){
        EZEnvironment.getActionsManager().getDefaultActionHandler().handleExit();
      }
      else if (event.getPropertyName().equals("AboutApp")){
        EZEnvironment.getActionsManager().getDefaultActionHandler().handleAbout();
      }
      else if (event.getPropertyName().equals("Preferences")){
        EZEnvironment.getActionsManager().getDefaultActionHandler().handlePreferences();
      }
    }

  }
}
