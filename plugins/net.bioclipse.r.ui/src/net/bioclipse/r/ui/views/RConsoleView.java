/* *****************************************************************************
 *Copyright (c) 2011 Christian Ander & The Bioclipse Team with others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contact: http://www.bioclipse.net/
 *******************************************************************************/
package net.bioclipse.r.ui.views;
import net.bioclipse.r.business.Activator;
import net.bioclipse.r.business.IRBusinessManager;
import net.bioclipse.scripting.ui.views.ScriptingConsoleView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RConsoleView extends ScriptingConsoleView {

   final Logger logger = LoggerFactory.getLogger(RConsoleView.class);
   private IRBusinessManager r;

    public RConsoleView() {
        logger.info("Starting R console UI");
    }

    @Override
    protected String executeCommand( String command ) {
    	String returnVal = "";
    	if (r == null)
    		getRBusinessManager();
    	echoCommand(command);
    	if (r.isWorking()) {
    		returnVal = r.eval(command);
    	} else
    		returnVal = "R console is inactivated: " + r.getStatus();
    	printMessage(returnVal);
    	return returnVal;
    }

    private void getRBusinessManager() {
    	r = Activator.getDefault().getJavaRBusinessManager();
    	printMessage(r.getStatus());
    }
    
    protected void waitUntilCommandFinished() {
        // Don't know if there's a way to sensibly implement this method for R.
    }

    void echoCommand(final String command) {
        printMessage(NEWLINE + "> " + command + NEWLINE);
    }
}