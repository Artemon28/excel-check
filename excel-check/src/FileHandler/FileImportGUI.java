package FileHandler;

import java.io.File;

import javax.swing.JFrame;

import com.plealog.genericapp.api.file.EZFileFilter;
import com.plealog.genericapp.api.file.EZFileManager;
import com.plealog.genericapp.api.log.EZLogger;

public class FileImportGUI extends JFrame{
	
	public FileImportGUI() {
		File f;

        f = EZFileManager.chooseFileForOpenAction(null, new EZFileFilter("xlsx", "Excel file"));
        
        if (f != null){
        	  EZLogger.info("Chosen file is: " + f.getAbsolutePath());
        	}
	}
}
