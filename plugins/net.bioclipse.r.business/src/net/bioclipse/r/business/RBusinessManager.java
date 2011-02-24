/*******************************************************************************
 * Copyright (c) 2011  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.r.business;

import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.r.Rengine;

import org.apache.log4j.Logger;

public class RBusinessManager implements IBioclipseManager {
	
	private static final Logger logger = Logger.getLogger(RBusinessManager.class);

	private Rengine rEngine;
	
	{
	    logger.debug("Starting R..");
	    logger.debug("R_HOME =" + System.getenv("R_HOME"));
	    logger.debug("java.library.path =" + System.getProperty("java.library.path"));
	    rEngine = new Rengine(new String[] {"--vanilla"}, false, null);
	}
	
    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "r";
    }

    public String eval(String command) {
        logger.debug("R cmd: " + command);
         // get the Rengine.
    	try {
    		String returnVal = rEngine.evalCommand(command);
    		logger.debug(" -> " + returnVal);
    		return returnVal;
    	} catch (Throwable error) {
    		logger.debug(
    			"Error while evaluating R command: " + error.getMessage(), error );
    		return "Error: " + error.getMessage();
    	}
    }
}
