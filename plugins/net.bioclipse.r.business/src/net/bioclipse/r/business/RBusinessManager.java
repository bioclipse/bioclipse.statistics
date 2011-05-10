/*******************************************************************************
 * Copyright (c) 2011  Egon Willighagen <egon.willighagen@gmail.com>
 * 					   Christian Ander  <christian.ander@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.r.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import javax.security.auth.login.LoginException;
import javax.swing.filechooser.FileFilter;

import net.bioclipse.business.BioclipsePlatformManager;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;
import org.apache.log4j.Logger;

import de.walware.rj.servi.RServi;
import de.walware.rj.data.RObject;
import de.walware.rj.data.RStore;
import net.bioclipse.r.RServiManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IWorkspace;

public class RBusinessManager implements IBioclipseManager {
	
	private static final Logger logger = Logger.getLogger(RBusinessManager.class);
	private RServi  rservi;
	public  String  R_HOME;
	private String  status  = "";
	private Boolean working = true;
	private IPath	workspacePath;
	private RServiManager rsmanager = new RServiManager("Rconsole");
    public static String NEWLINE = System.getProperty("line.separator");

	
	public RBusinessManager() throws LoginException, NoSuchElementException {	
	    logger.info("Starting R manager");
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		workspacePath = root.getLocation();
		logger.debug("Bioclipse workingdirectory: " + workspacePath.toString());
	    
	    R_HOME = System.getenv("R_HOME");
	    logger.debug("R_HOME=" + R_HOME);
		try {
			R_HOME = rsmanager.checkRPath(R_HOME);
			rsmanager.setEmbedded(R_HOME);
		}
		catch (FileNotFoundException e) {
			working = false;
			status = e.getMessage();
		}
		catch (CoreException e) { // Catch rj startup error.
			working = false;
			status = extractRjError(e.getCause().getCause().getMessage());
		}
		if (working) {
			try {
				rservi = rsmanager.getRServi("Rconsole");
				initSession();
			}
			catch (CoreException e) { 
			working = false;
			status = e.getMessage();
			}
		}
		if (!working) logger.error(status);
	}
	
    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "r";
    }
    
    public String getStatus() {
    	return status;
    }
    
    public Boolean isWorking() {
    	return working;
    }
    
    private void initSession() {
    	File file = new File(workspacePath.toString()+"/r");
		if (!file.exists())
			file.mkdir();
		eval("setwd(\""+file.getAbsolutePath()+"\")");
		status = "R workspace: " + eval("getwd()").substring(3);
		eval("x11()");
		FilenameFilter filter = new FilenameFilter() {		// Filter out the R-session files
			@Override
			public boolean accept(File dir, String name) {
				logger.debug(name);
			return name.contains(".RData");
			}
		};
		// Show R sessionfiles for user
		String[] files = file.list(filter);
		for(int i=0; i<files.length; i++){
			status += NEWLINE + "Found R session: " + files[i];
		}
		status += NEWLINE + "Use load(\"file\") and save.image(\"file\")";
    }
    
    public String eval(String command) {
        logger.debug("R cmd: " + command);
        String returnVal;
        if (command.startsWith("?"))
        	returnVal = help(command.substring(1));
        else try {
        	RObject data = rservi.evalData("capture.output(print("+command+"))", null);	// capture.output(print( )) gives a string output from R, otherwise R objects. The extra pair of () is needed for the R function print to work properly.
        	RStore rData = data.getData();
        	StringBuilder builder = new StringBuilder();
        	for(int i=0;i<rData.getLength();i++) {
        		builder.append(rData.getChar(i));
        	}
        	returnVal = builder.toString();
        }
        catch (CoreException rError) {	// Catch R errors.
        	returnVal = "Error: " + extractRError(rError.getMessage());
        }
        catch (Throwable error) {
        	error.printStackTrace();
        	returnVal = "Error: " + error.getMessage();
        }
        logger.debug(" -> " + returnVal);
        return returnVal;
        }

    private String help(String url) {
    	BioclipsePlatformManager bioclipse = new BioclipsePlatformManager();
    	try {
			bioclipse.openURL(new URL(url));
			return "";
		} catch (MalformedURLException e) {
			return e.getMessage();
		} catch (BioclipseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
    }
    
    private String extractRjError(String error) {
    	error = error.substring(error.indexOf("JR library path:"));
    	error = error.replaceFirst(NEWLINE, "");
    	error = error.substring(0, error.indexOf(NEWLINE)).trim();
    	error = "Path to rj package not found." + NEWLINE + error;
    	return error;
    }

    private String extractRError(String error) {
    	logger.debug("full error: " + error);
    	String result = error;
    	if (error.startsWith("Evaluation failed")) {
    		result = error.substring(error.indexOf(":")+1).trim();
    		int index;
    		if ((index=result.indexOf("):")) > 0) {
    			result = result.substring(index+2, result.lastIndexOf(">.")).trim();
    		}
    	}
    	return result;
    }
}
