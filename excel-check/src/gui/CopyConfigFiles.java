package gui;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.plealog.genericapp.api.EZEnvironment;

public class CopyConfigFiles {
	public CopyConfigFiles(String fromFilename, CodeSource toFolderSource) {
		InputStream fis4 = null;
		String packageName = this.getClass().getPackageName();

    	fis4 = this.getClass().getClassLoader().getResourceAsStream(fromFilename);
    	
		File sourceFile = new File(fromFilename);
		String sourceFolderName = sourceFile.getParent();
		
		File sourceFolder = new File(sourceFolderName);
		if (!sourceFolder.exists()) {
			sourceFolder.mkdir();
		}
		
		
    	Path destDir = Paths.get(sourceFolderName);
    	if (fis4 == null) {   		
    		CodeSource src = toFolderSource;
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
		    		    if (name.startsWith(packageName + File.separator + "conf" + File.separator)) {
		    		    	File file = new File(name);
		    		    	if (name.endsWith(".config") && (new File(destDir.resolve(file.getName()).toString()).exists()))
		    		    		continue;
		    		    	if (!name.equals(packageName + File.separator + "conf" + File.separator)) {
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
	}
}
