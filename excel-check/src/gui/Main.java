package gui;

import FileHandler.DefectReader;

import FileHandler.DowntimesReader;
import FileHandler.ExcelReaderAbstract;
import FileHandler.FileImportGUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.plealog.genericapp.api.EZApplicationBranding;
import com.plealog.genericapp.api.EZEnvironment;
import com.plealog.genericapp.api.EZGenericApplication;
import com.plealog.genericapp.api.EZSplashScreen;
import com.plealog.genericapp.api.EZUIStarterListener;
import com.plealog.genericapp.api.file.EZFileUtils;

import DataBaseConnection.DataBaseWriter;

/**
 * Main class of the project from where starts application.
 * @author Chaykov Artemiy
 *
 */

public class Main {
	
  /**
   * in public static void main the gui part of the jgaf framework is launched
   * @param args
   */
  public static void main(String[] args) {
    EZGenericApplication.initialize("Excel checking");

    EZApplicationBranding.setAppName("Excel checking");
    EZApplicationBranding.setAppVersion("1.0");
    EZApplicationBranding.setProviderName("Chaykov Artemiy");
    
    EZEnvironment.addResourceLocator(Main.class);
    /**
     * specify the configuration file for the menu bar
     */
    ResourceBundle rb = ResourceBundle.getBundle(Main.class.getPackage().getName()+".menu");
    EZEnvironment.setUserDefinedActionsResourceBundle(rb);
    EZEnvironment.getActionsManager().addActionMenuListener(new MyActionManager());
    EZEnvironment.setUIStarterListener(new MyStarterListener());
    /**
     * specify the configuration file for the PREFERENCES API
     */
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
    /**
     * start application
     */
    EZGenericApplication.startApplication(args);
  }

  private static class MyStarterListener implements EZUIStarterListener{
    private EZSplashScreen splash;
    /**
     * the main panel and components when launching the application
     */
    @Override
    public Component getApplicationComponent() {

      JPanel      mainPanel = new JPanel(new BorderLayout());
      JTabbedPane tabPanel = new JTabbedPane();

      tabPanel.add("My First Component", new JPanel());

      mainPanel.add(tabPanel, BorderLayout.CENTER);
      return mainPanel;
    }

    /**
     * actions when exiting the program.
     * Return false to prevent application from exiting (e.g. a background task is still running).
     * Return true otherwise.
     * 
     */
    @Override
    public boolean isAboutToQuit() {    	
    	return true;
    }

    @Override
    public void postStart() {
    }

    /**
     * before start app, copies the configuration files to the folder where the application is located
     * then Check if user entered data to access the database
     */
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

	/**
	 * menu with actions for every button
	 */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
    	
      if (event.getPropertyName().equals("FileImport")){
  		FileImportGUI fileImportGUI = new FileImportGUI();  		
  		fileImportGUI.dispose();
  		fileImportGUI.revalidate();
      }
      /**
       * processing an excel file of Defects and writing it to the database
       */
      else if (event.getPropertyName().equals("Defect")) {
    	  FileImportGUI fileImportGUI = new FileImportGUI();
    	  ExcelReaderAbstract excelReader = new DefectReader(fileImportGUI.getFileName());
    	  DataBaseWriter dataBaseWriter = new DataBaseWriter();
    	  dataBaseWriter.setDBParams(DataBaseConfigReader.getHost(), DataBaseConfigReader.getName(), DataBaseConfigReader.getPassword());
    	  try {
			excelReader.read(dataBaseWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
      }
      /**
       * processing an excel file of Downtimes and writing it to the database
       */
      else if (event.getPropertyName().equals("Downtimes")) {
    	  FileImportGUI fileImportGUI = new FileImportGUI();
    	  ExcelReaderAbstract excelReader = new DowntimesReader(fileImportGUI.getFileName());
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
      /**
       * Preferences with data to access the DataBase
       */
      else if (event.getPropertyName().equals("Preferences")){
        EZEnvironment.getActionsManager().getDefaultActionHandler().handlePreferences();
      }
    }

  }
}
