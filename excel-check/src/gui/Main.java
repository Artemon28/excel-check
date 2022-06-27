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

import DataBaseConnection.DataBaseWriter;

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
		String envPackageName = sourceFolder.getParent();
    	DataBaseConfigReader dbConfigReader = new DataBaseConfigReader(envPackageName, EZEnvironment.getPreferencesConfigurationFile());
    	
    	DataBaseConfigWriter dbConfigWriter = new DataBaseConfigWriter(packageName + "/conf/editor.desc", packageName, 
    			DataBaseConfigReader.getPassword(), DataBaseConfigReader.getName(), DataBaseConfigReader.getHost());
    	
    	return true;
    }

    @Override
    public void postStart() {
    }

    @Override
    public void preStart() {
    	
    	CopyConfigFiles ccf = new CopyConfigFiles(EZEnvironment.getPreferencesConfigurationFile(), this.getClass().getProtectionDomain().getCodeSource());
    	File sourceFolder = new File(EZEnvironment.getPreferencesConfigurationFile());    	
    	
    	DataBaseConfigReader dbReader = new DataBaseConfigReader(sourceFolder.getParent(), 
    			EZEnvironment.getPreferencesConfigurationFile());
    	try {
    	    while (DataBaseConfigReader.getPassword() == null || DataBaseConfigReader.getPassword().isEmpty() || DataBaseConfigReader.getHost() == null ||
    	    		DataBaseConfigReader.getHost().isEmpty() || DataBaseConfigReader.getName() == null || DataBaseConfigReader.getName().isEmpty()) {
    	    	
    	    	EZEnvironment.getActionsManager().getDefaultActionHandler().handlePreferences();
    	    	dbReader.setParams();
    	    	if (DataBaseConfigReader.getPassword() == null || DataBaseConfigReader.getPassword().isEmpty() || DataBaseConfigReader.getHost() == null ||
        	    		DataBaseConfigReader.getHost().isEmpty() || DataBaseConfigReader.getName() == null || DataBaseConfigReader.getName().isEmpty()) {
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
    	  DataBaseWriter dataBaseWriter = new DataBaseWriter();
    	  dataBaseWriter.setDBParams(DataBaseConfigReader.getHost(), DataBaseConfigReader.getName(), DataBaseConfigReader.getPassword());
    	  try {
			excelReader.read(dataBaseWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
      }
      else if (event.getPropertyName().equals("Downtimes")) {
    	  FileImportGUI fileImportGUI = new FileImportGUI();
    	  ExcelReader excelReader = new DowntimesReader(fileImportGUI.getFileName());
    	  DataBaseWriter dataBaseWriter = new DataBaseWriter();
    	  dataBaseWriter.setDBParams(DataBaseConfigReader.getHost(), DataBaseConfigReader.getName(), DataBaseConfigReader.getPassword());
    	  try {
			excelReader.read(dataBaseWriter);
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
