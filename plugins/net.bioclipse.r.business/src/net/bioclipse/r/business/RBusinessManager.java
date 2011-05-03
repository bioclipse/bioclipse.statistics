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

import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import javax.security.auth.login.LoginException;

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
	private RServiManager rsmanager = new RServiManager("app");
	
	public RBusinessManager() throws LoginException, NoSuchElementException {	
	    logger.info("Starting R manager");
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
				rservi = rsmanager.getRServi("task");
//				rservi.evalData("session.save()", null);
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				IPath location = root.getLocation();
				logger.debug(location.toString());
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
    
    public String eval(String command) {
        logger.debug("R cmd: " + command);
        String returnVal;
        try {
        	RObject data = rservi.evalData("capture.output(print(("+command+")))", null);	// capture.output(print( )) gives a string output from R, otherwise R objects. The extra pair of () is needed for the R function print to work properly.
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

    private String extractRjError(String error) {
    	String newline = System.getProperty("line.separator");
    	error = error.substring(error.indexOf("JR library path:"));
    	error = error.replaceFirst(newline, "");
    	error = error.substring(0, error.indexOf(newline)).trim();
    	error = "Path to rj package not found." + newline + error;
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
