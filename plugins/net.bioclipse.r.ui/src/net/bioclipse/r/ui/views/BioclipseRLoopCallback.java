/* *****************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.r.ui.views;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

/**
 * @author Christian Ander
 * Class to get R messages to Bioclipse R-console
 */

public class BioclipseRLoopCallback implements RMainLoopCallbacks {

    private RConsoleView rConsoleView;
    public boolean doWrite = false;       // To prevent R-startup messages while R-console window does not yet exist.

    public BioclipseRLoopCallback(RConsoleView rConsoleView) {
        this.rConsoleView=rConsoleView;   // for access to printMessage(text) method.
    }

    @Override
    public void rWriteConsole( Rengine re, String text, int oType ) {
        if (doWrite) {
            System.out.println("R writes console: " + text);
            rConsoleView.printMessage(text);
        }
    }

    @Override
    public void rBusy( Rengine re, int which ) {

        // TODO Auto-generated method stub

    }

    @Override
    public String rReadConsole( Rengine re, String prompt, int addToHistory ) {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void rShowMessage( Rengine re, String message ) {

        System.out.println("R says: " + message);

    }

    @Override
    public String rChooseFile( Rengine re, int newFile ) {

        // TODO Auto-generated method stub
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
