/* *****************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
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
        logger.debug("RConsole: java.library.path =" + System.getProperty("java.library.path"));
//        String URL = "rmi://127.0.0.1/rservi-pool";
        rm.setEmbedded(R_home);
        rs = rm.getRServi("task");
    }

    @Override
    protected String executeCommand( String command ) {
        echoCommand(command);
        logger.debug("R cmd: " + command);
        String returnVal;

        if (command.equals("q()") || command.equals("quit()") )
            returnVal = "Cannot quit R from here";
        else if (System.getenv("R_HOME") == null)   // check if R_HOME is set to avoid crash.
            returnVal = "R_HOME not set."; 
        else {
            try {
                RObject data = rs.evalData(command, null);
                returnVal = "Success";              // Todo: Use RObject to extract information
            }
            catch (Throwable error) {
    	      error.printStackTrace();
    	      return "Error: " + error.getMessage();
            }
        }
        logger.debug(" -> " + returnVal);
        printMessage(returnVal);
        return returnVal;
    }

    protected void waitUntilCommandFinished() {
        // Don't know if there's a way to sensibly implement this method for R.
    }

    void echoCommand(final String command) {
        printMessage(NEWLINE + "> " + command + NEWLINE);
    }
}
