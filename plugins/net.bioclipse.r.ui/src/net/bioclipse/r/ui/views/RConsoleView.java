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
import net.bioclipse.r.business.RThread;
import net.bioclipse.r.ui.util.RunUtil;
import net.bioclipse.scripting.Hook;
import net.bioclipse.scripting.ScriptAction;
import net.bioclipse.scripting.ui.views.ScriptingConsoleView;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RConsoleView extends ScriptingConsoleView {

   final Logger logger = LoggerFactory.getLogger(RConsoleView.class);
   private IRBusinessManager r;
   private RThread rThread;

   public RConsoleView() {
	   logger.info("Starting R console UI");
   }

   @Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		getRBusinessManager();
		if (r == null)
			getRBusinessManager();
		if (!r.getRightRVersion()){
			getSite().getShell().getDisplay().asyncExec
		    (new Runnable() {
		        public void run() {
		            MessageDialog.openError(getSite().getShell(),
		            		"Incompatible R version","Runnig R within Bioclipse requires R version 2.13, or later!");
		        }
		    });
		}

	}

/*
 * Execute the R command typed in the R console
 * uses the evalCommand method
 */
    @Override
    protected String executeCommand( String command ) {
    	command = RunUtil.parseCommand(command);
		if (command.contains("?") || command.contains("help") || command.contains("install.packages"))
    		    evalCommand(command, false);
        	else
        		evalCommand(command, true);
    	return "";
    }

    public void execEditorInput(String command) {
    	executeCommand(command);
    }

    /*
     * method that calls r.eval and prints the command and the output
     */
    protected void evalCommand(final String command, boolean quotes) {
        
        String input = command;
        if (quotes) {
            input = "eval(parse(text =\""
                     + command.replace("\"", "\\\"")
                     + "\"))";
        }

        
        
        if (rThread == null || !rThread.isAlive() ) {
            rThread = new RThread();
            rThread.start();
        }
        rThread.enqueue( new ScriptAction(input, 
                             new Hook() {
                                public void run( final Object result ) {
                                	if (!command.contentEquals("save.image(\".RData\")")) {
                                		echoCommand( command );
                                	}
                                }
                             },
                             new Hook() {
                                public void run( final Object result ) {
                            		if (command.contentEquals("save.image(\".RData\")") && result.toString().length() == 0) {
                                		printMessage(NEWLINE + "R Session saved");
                                	} else {
                                		printMessage(NEWLINE + result);
                                	}
                                }
                             }) );
    }

    public void saveSession() {
    	evalCommand("save.image(\".RData\")", false);
    }

    private void getRBusinessManager() {
    	try {
    		r = Activator.getDefault().getJavaRBusinessManager();
    		printMessage(r.getStatus());
    		logger.debug(r.getStatus());
    	}
    	catch (IllegalStateException e) {
    		printMessage("Waiting for JavaRBusinessManager.");
    		logger.debug(e.getMessage());
    	}
    }
    
    private void echoCommand(final String command) {
        final String[] cmd = RunUtil.breakCommand(command);
//        Display.getDefault().asyncExec( new Runnable() {
//            public void run() {
                for (String c : cmd) {
                    printMessage(NEWLINE + "> " + c);
                }
//            }
//        });
    }
    
    @Override
    protected String interceptDroppedString( String s ) {

        String result = super.interceptDroppedString( s );
        if ( s.charAt( 0 ) == '\\' || s.charAt( 0 ) == '/' ) {
            return result.substring( 1 );
        }
        return result;
    }
}