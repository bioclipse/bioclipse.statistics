/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.r.ui.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.bioclipse.r.NoRException;
import net.bioclipse.r.TextR;
import net.bioclipse.scripting.OutputProvider;
import net.bioclipse.scripting.ui.views.ScriptingConsoleView;

public class RConsoleView extends ScriptingConsoleView {
    TextR env;

    public RConsoleView() throws NoRException {
        env = new TextR(new OutputProvider() {
            @Override
            public void output( String s ) {
                printMessage(s);
            }
        });
    }

    @Override
    protected String executeCommand( String command ) {
        ArrayList<Object> l = new ArrayList<Object>();
        l.add( command );
        try {
            env.eval( l );
        } catch ( IOException e ) {
            printMessage(e.getMessage());
        }
        return null;
    }
}
