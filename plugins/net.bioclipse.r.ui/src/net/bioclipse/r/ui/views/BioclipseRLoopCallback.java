/* *****************************************************************************
 *Copyright (c) 2011 Christian Ander (christian.ander@eximius.se) and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.bioclipse.org
 *
 *******************************************************************************/
package net.bioclipse.r.ui.views;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Ander
 * Class to get R messages to Bioclipse R-console
 */

public class BioclipseRLoopCallback implements RMainLoopCallbacks {

    final Logger logger = LoggerFactory.getLogger(BioclipseRLoopCallback.class);
    private RConsoleView rConsoleView;
    public boolean doWrite = false;       // To prevent R-startup messages while R-console window does not yet exist.

    public BioclipseRLoopCallback(RConsoleView rConsoleView) {
        this.rConsoleView=rConsoleView;   // for access to printMessage(text) method.
    }

    @Override
    public void rWriteConsole( Rengine re, String text, int oType ) {
        if (doWrite) {
            logger.debug("R writes console: " + text);
            rConsoleView.printMessage(text);
        }
    }

    @Override
    public void rBusy( Rengine re, int which ) {
        logger.info("rBusy("+which+")");
    }

    @Override
    public String rReadConsole( Rengine re, String prompt, int addToHistory ) {
        return null;
    }

    @Override
    public void rShowMessage( Rengine re, String message ) {
        logger.info("R says: " + message);
    }

    @Override
    public String rChooseFile( Rengine re, int newFile ) {
        logger.info("rChooseFile from Loopback");
        return null;
    }

    @Override
    public void rFlushConsole( Rengine re ) {
        // TODO Auto-generated method stub
    }

    @Override
    public void rSaveHistory( Rengine re, String filename ) {
        // TODO Auto-generated method stub
    }

    @Override
    public void rLoadHistory( Rengine re, String filename ) {
        // TODO Auto-generated method stub
    }

}
