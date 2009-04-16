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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bioclipse.r.NoRException;
import net.bioclipse.r.TextR;
import net.bioclipse.scripting.Hook;
import net.bioclipse.scripting.JsAction;
import net.bioclipse.scripting.JsThread;
import net.bioclipse.scripting.OutputProvider;
import net.bioclipse.scripting.ui.views.ScriptingConsoleView;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class RConsoleView extends ScriptingConsoleView {
    TextR env;

    private static JsThread jsThread
        = net.bioclipse.scripting.Activator.getDefault().JS_THREAD;
    private static Matcher jsBacktickMatcher;

    static {
        String noBackTick
          = "("                                   // capture into group:
              + "(?:"                               // either...
                + "[^`\'\"]+"                          // no backticks or quotes
                + "|"                                  // ...or...
                + "(?:\"(?:[^\"\\\\]|(?:\\\\.))*\")"   // a double quoted string
                + "|"                                  // ...or...
                + "(?:\'(?:[^\'\\\\]|(?:\\\\.))*\')"   // a single quoted string
              + ")*+"                               // zero or more of the above
            + ")";

        jsBacktickMatcher
          = Pattern.compile( noBackTick + "`" + noBackTick + "`" ).matcher("");
    }

    public RConsoleView() throws NoRException {
        env = new TextR(new OutputProvider() {
            @Override
            public void output( final String s ) {
                Display.getDefault().asyncExec( new Runnable() {
                    public void run() {
                        printMessage(s);
                    }
                });
            }
        });
        String loc=Platform.getLocation().toOSString();
        if (loc!=null)
            executeCommand( "setwd(\""+ loc +"\")");
    }

    @Override
    protected String executeCommand( String command ) {
        List<Object> al;
//        try {
            al = interpolateJsVariables(command);
//        }
//        catch (RuntimeException rte) {
//            return rte.toString();
//        }

        try {
            env.eval( al );
        } catch ( IOException e ) {
            printMessage(e.getMessage());
        }

        return null;
    }

    private List<Object> interpolateJsVariables(String command) {

        List<Object> al = new ArrayList<Object>();
        jsBacktickMatcher.reset(command);

        int e = 0;
        while(jsBacktickMatcher.find()){

            al.add(jsBacktickMatcher.group(1));
            final Object[] value = new Object[] { null };
            jsThread.enqueue(new JsAction(jsBacktickMatcher.group(2),
                    new Hook() {
                        public void run(Object result) {
                            synchronized(value) {
                              value[0] = result;
                              value.notify();
                            }
                        }
            }));

            // This sucks. Seriously.
            synchronized(value) {
                while (value[0] == null) {
                    try {
                        value.wait();
                    } catch (InterruptedException e1) {
                        return new ArrayList<Object>();
                    }
                }
            }

            al.add(value[0]);
            e = jsBacktickMatcher.end();
        }
        al.add(command.substring(e));

        return al;
    }

}
