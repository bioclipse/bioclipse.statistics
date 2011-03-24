/* *****************************************************************************
 *Copyright (c) 2011 Christian Ander & The Bioclipse Team with others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.r.ui.views;

import java.util.NoSuchElementException;

import javax.security.auth.login.LoginException;

import de.walware.rj.servi.RServi;
import de.walware.rj.data.RObject;
import de.walware.rj.data.RStore;
import net.bioclipse.r.RServiManager;
import org.eclipse.core.runtime.CoreException;

import net.bioclipse.scripting.ui.views.ScriptingConsoleView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RConsoleView extends ScriptingConsoleView {

    private RServi rs;
    public String R_home;
    private RServiManager rm = new RServiManager("app");
    final Logger logger = LoggerFactory.getLogger(RConsoleView.class);

    public RConsoleView() throws LoginException, NoSuchElementException, CoreException {
        logger.info("RConsole: Starting R..");
        R_home = System.getenv("R_HOME");
        logger.debug("RConsole: R_HOME =" + R_home);
//        String URL = "rmi://127.0.0.1/rservi-pool";
        rm.setEmbedded(R_home);
        rs = rm.getRServi("task");
    }

    @Override
    protected String executeCommand( String command ) {
        echoCommand(command);
        logger.debug("R cmd: " + command);
        String returnVal;
        try {
        	RObject data = rs.evalData("capture.output(print("+command+"))",null);	// capture.output(print( )) gives a string output from R, otherwise R objects.
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
        printMessage(returnVal);
        return returnVal;
    }
    
    private String extractRError(String error) {
    	logger.debug("full error:" + error);
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

    protected void waitUntilCommandFinished() {
        // Don't know if there's a way to sensibly implement this method for R.
    }

    void echoCommand(final String command) {
        printMessage(NEWLINE + "> " + command + NEWLINE);
    }
}
