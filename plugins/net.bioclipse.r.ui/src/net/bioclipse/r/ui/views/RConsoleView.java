/* *****************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.r.ui.views;

import net.bioclipse.r.Rengine;
import net.bioclipse.scripting.ui.views.ScriptingConsoleView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RConsoleView extends ScriptingConsoleView {

    private Rengine re;
    final Logger logger = LoggerFactory.getLogger(RConsoleView.class);

    public RConsoleView() {
        BioclipseRLoopCallback lc = new BioclipseRLoopCallback(this);
        logger.info("RConsole: Starting R..");
        logger.debug("RConsole: R_HOME =" + System.getenv("R_HOME"));
        logger.debug("RConsole: java.library.path =" + System.getProperty("java.library.path"));

        re = new Rengine(new String[] {"--vanilla"}, false, lc);
    	  if (!org.rosuda.JRI.Rengine.versionCheck()) {
    	      logger.error("Rengine: Version mismatch, java files don't match library version.");
    	  }
    	  lc.doWrite = true;   // enable output to R-consoleView. 
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
            try { returnVal = re.evalCommand(command); }
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
