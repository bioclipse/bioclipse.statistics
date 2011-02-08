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

public class RConsoleView extends ScriptingConsoleView {

	private Rengine re;

    public RConsoleView() {
        BioclipseRLoopCallback lc = new BioclipseRLoopCallback(this);
        re = new Rengine(new String[] {"--vanilla"}, false, lc);
    	  if (!org.rosuda.JRI.Rengine.versionCheck()) {
    	      System.err.println("Rengine: Version mismatch, java files don't match library version.");
    	  }
    	  lc.doWrite = true;   // enable verbose output from R. 
    }

    @Override
    protected String executeCommand( String command ) {
        echoCommand(command);
        System.out.println("R cmd: " + command);
        String returnVal;

        if (command.equals("q()") || command.equals("quit()") ) { returnVal = "Cannot quit R from here"; }
        else {
            try { returnVal = re.evalCommand(command); }
            catch (Throwable error) {
    	      error.printStackTrace();
    	      return "Error: " + error.getMessage();
            }
        }
        System.out.println(" -> " + returnVal);
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
