/* *****************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.r.ui.views;

import net.bioclipse.scripting.ui.views.ScriptingConsoleView;

import org.rosuda.JRI.Rengine;

public class RConsoleView extends ScriptingConsoleView {

	private Rengine re;

    public RConsoleView() {
    	re = new Rengine(new String[] {"--vanilla"}, false, null);
    }

    @Override
    protected String executeCommand( String command ) {
    	echoCommand(command);
    	System.out.println("R cmd: " + command);
    	try {
    		String returnVal = re.eval(command).toString();
    		System.out.println(" -> " + returnVal);
    		printMessage(returnVal);
    		return returnVal;
    	} catch (Throwable error) {
    		error.printStackTrace();
    		return "Error: " + error.getMessage();
    	}
    }

    protected void waitUntilCommandFinished() {
        // Don't know if there's a way to sensibly implement this method for R.
    }

    private void echoCommand(final String command) {
        printMessage(NEWLINE + "> " + command + NEWLINE);
    }
}
