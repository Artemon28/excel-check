package test;

import FileHandler.FileImportGUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;

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

    //We tell JRE where to locate resources... (always use a class located in the same
    //package as the resources)
    
    EZEnvironment.addResourceLocator(Main.class);

    //we load are ResourceBundle containing the main menu declarations
    ResourceBundle rb = ResourceBundle.getBundle(Main.class.getPackage().getName()+".menu"); 
    EZEnvironment.setUserDefinedActionsResourceBundle(rb);

    //install the action manager listener to easily handle actions
    EZEnvironment.getActionsManager().addActionMenuListener(new MyActionManager());

    //Add a listener to application startup cycle (see below)
    EZEnvironment.setUIStarterListener(new MyStarterListener());

    //We setup the Preferences Dialogue Box
    String confPath;
	try {
		confPath = EZFileUtils.terminatePath(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent());
		confPath += "conf";
	    confPath += File.separator;
	    confPath += "editor.desc";
	    EZEnvironment.displayWarnMessage(EZEnvironment.getParentFrame(), confPath);
	    EZEnvironment.setPreferencesConfigurationFile(confPath);
	} catch (URISyntaxException e) {
		e.printStackTrace();
	}
    //Start the application
    EZGenericApplication.startApplication(args);
  }

  private static class MyStarterListener implements EZUIStarterListener{
    private EZSplashScreen splash;
    @Override
    public Component getApplicationComponent() {
      //This method is called by the framework to obtain the UI main component to be
      //displayed in the main frame.

      JPanel      mainPanel = new JPanel(new BorderLayout());
      JTabbedPane tabPanel = new JTabbedPane();

      tabPanel.add("My First Component", new JPanel());

      mainPanel.add(tabPanel, BorderLayout.CENTER);
      return mainPanel;
    }

    @Override
    public boolean isAboutToQuit() {
      //You can add some code to figure out if application can exit.

      //Return false to prevent application from exiting (e.g. a background task is still running).
      //Return true otherwise.

      //Do not add a Quit dialogue box to ask user confirmation: the framework already does that
      //for you.
      return true;
    }

    @Override
    public void postStart() {
    }

    @Override
    public void preStart() {
    	Properties prop = new Properties();
    	Path fileName = Paths.get(EZEnvironment.getPreferencesConfigurationFile());
    	try (FileInputStream fis = new FileInputStream(EZEnvironment.getPreferencesConfigurationFile())) {
    	    prop.load(fis);
    	    String passwordFile = prop.getProperty("section.a.config");
    	    Properties ppp = new Properties();
	    	ppp.load(new FileInputStream(fileName.getParent().toString() + File.separator + passwordFile));
	    	String password = ppp.getProperty("database.password");
	    	String database = ppp.getProperty("database.host");
	    	
    	    while (password == null || password.isEmpty() || database == null || database.isEmpty()) {
    	    	EZEnvironment.getActionsManager().getDefaultActionHandler().handlePreferences();
    	    	Properties props = new Properties();
    	    	props.load(new FileInputStream(new File(fileName.getParent().toString() + "\\" + passwordFile)));
    	    	password = props.getProperty("database.password");
    	    	database = props.getProperty("database.host");
    	    	if (password == null || password.isEmpty() || database == null || database.isEmpty()) {
    	    		EZEnvironment.displayWarnMessage(EZEnvironment.getParentFrame(), "Please, enter database host and password");
    	    	}
    	    }
    	} catch (FileNotFoundException ex) {
    		ex.printStackTrace();
    	} catch (IOException ex) {
    		ex.printStackTrace();
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
