package test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.plealog.genericapp.api.EZApplicationBranding;
import com.plealog.genericapp.api.EZEnvironment;
import com.plealog.genericapp.api.EZGenericApplication;
import com.plealog.genericapp.api.EZSplashScreen;
import com.plealog.genericapp.api.EZSplashScreenFactory;
import com.plealog.genericapp.api.EZUIStarterListener;
import com.plealog.genericapp.api.file.EZFileManager;
import com.plealog.genericapp.api.file.EZFileUtils;
import com.plealog.genericapp.api.log.EZLogger;

public class Main {
  public static void main(String[] args) {
    EZGenericApplication.initialize("Excel checking");

    EZApplicationBranding.setAppName("Excel checking");
    EZApplicationBranding.setAppVersion("1.0");
    EZApplicationBranding.setProviderName("Chaykov Artemiy");

    //We tell JRE where to locate resources... (always use a class located in the same
    //package as the resources)
    
    EZEnvironment.addResourceLocator(Main.class);
    
    String cl = Main.class.getPackage().getName();

    //we load are ResourceBundle containing the main menu declarations
    ResourceBundle rb = ResourceBundle.getBundle(Main.class.getPackage().getName()+".menu"); 
    EZEnvironment.setUserDefinedActionsResourceBundle(rb);

    //install the action manager listener to easily handle actions
    EZEnvironment.getActionsManager().addActionMenuListener(new MyActionManager());

    //Add a listener to application startup cycle (see below)
    EZEnvironment.setUIStarterListener(new MyStarterListener());

    //We setup the Preferences Dialogue Box
    String confPath = EZFileUtils.terminatePath(System.getProperty("user.dir"));
    confPath += "conf";
    confPath += File.separator;
    confPath += "editor.desc";
    EZEnvironment.setPreferencesConfigurationFile(confPath);

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
    }
    @Override
    public void frameDisplayed() {
    }
  }
  /*
   * Show how to work with generic action (see ui.properties for the definition of ActionFileOpen)*/
  private static class MyActionManager implements PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent event) {
      if (event.getPropertyName().equals("FileOpen")){
        File f;

        f = EZFileManager.chooseDirectory();
        if (f!=null)
          EZLogger.info("Chosen file is: "+f.getAbsolutePath());
      }
      //The framework provides default behavior for Exit, About and Preferences.
      //Override them by using your own dedicated code if default behavior does not
      //meet your needs.
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
