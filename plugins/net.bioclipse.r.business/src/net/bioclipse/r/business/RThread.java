/* Copyright (c) 2012  Jonathan Alvarsson <jonalv@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 */
package net.bioclipse.r.business;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.managers.MonitorContainer;
import net.bioclipse.scripting.Hook;
import net.bioclipse.scripting.ScriptAction;
import net.bioclipse.scripting.ScriptingThread;


public class RThread extends ScriptingThread {
    private final static Object semaphore = new Object();
    private static volatile boolean firstTime = true;
    
    private LinkedList<ScriptAction> actions = new LinkedList<ScriptAction>();
    
    private static final Logger logger = Logger.getLogger( RThread.class );
    private static boolean busy;
    private IRBusinessManager r = Activator.getDefault()
                                           .getJavaRBusinessManager();
    
    public void run() {
        while (true) {
            final ScriptAction[] nextAction = new ScriptAction[1];
            synchronized (actions) {
                try {
                    while ( actions.isEmpty() )
                        actions.wait();
                        
                }
                catch (InterruptedException e) {
                    break;
                }
                nextAction[0] = actions.removeFirst();
            }
            final Object[] result = new Object[1];
            busy = true;
            final Boolean[] running = { true };
            final Boolean[] monitorIsSet = { false };
            final IProgressMonitor[] monitor = { new NullProgressMonitor() };
            
            Job job = new Job("R-script") {
                @Override
                protected IStatus run( IProgressMonitor pm ) {
                    pm.beginTask( "Running R",
                                  IProgressMonitor.UNKNOWN );
                    monitor[0] = pm;
                    nextAction[0].runPreCommandHook();
                    synchronized ( monitorIsSet ) {
                        monitorIsSet[0] = true;
                        monitorIsSet.notifyAll();
                    }
                    synchronized ( running ) {
                        while ( running[0] ) {
                            try {
                                running.wait(500);
                                if ( pm.isCanceled() && running[0] ) {
                                    running.wait(5000);
                                    if (!running[0]) {
                                        break;
                                    }
                                    // Here we are gonna stop this thread dead
                                    // It is evil but we actually think we 
                                    // wanna do it.
                                    RThread.this.stop();
                                    running[0] = false;
                                    if (firstTime) {
                                        popUpWarning();
                                        firstTime = false;
                                    }
                                    return Status.CANCEL_STATUS;
                                }
                            }
                            catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                    return Status.OK_STATUS;
                }
            };
            job.setUser( true );
            job.schedule();
            synchronized ( monitorIsSet ) {
                while ( !monitorIsSet[0] ) {
                    try {
                        monitorIsSet.wait();
                    }
                    catch (InterruptedException e) {
                        break;
                    }
                }
            }
            try {
                result[0] = r.eval( nextAction[0].getCommand() );
            }
            catch (Throwable t) {
                LogUtils.debugTrace( logger, t );
                result[0] = t;
            }
            synchronized ( running ) {
                running[0] = false;
                running.notifyAll();
            }
            try {
                job.join();
            }
            catch ( InterruptedException e) {
                e.printStackTrace();
            }
            busy=false;
            synchronized ( semaphore ) {
                semaphore.notifyAll();
            }
            nextAction[0].runPostCommandHook( result[0] );
        }
    }
    
    public synchronized void enqueue(ScriptAction action) {
        synchronized (actions) {
            actions.addLast( action );
            actions.notifyAll();
        }
    }
    
    public static synchronized boolean isBusy() {
        return busy;
    }
    
    public void enqueue(String command) {
        enqueue( new ScriptAction( command,
                     new Hook() { public void run(Object s) {} } ) );
    }
    
    private void popUpWarning() {
        firstTime = false;
        Display.getDefault().asyncExec( new Runnable() {
            public void run() {
                MessageDialog.openWarning(
                  PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow().getShell(),
                  "Restart recommended after cancelling R script",
                  "The cancelling of the running script may have " +
                  "left your data in an inconsistent state, depending upon " +
                  "what the script execution was working on. " +
                  "You are recommended to restart Bioclipse." );
            }
        } );
    }
    
    public static void waitUntilNotBusy() {
        while (busy) {
            try {
                synchronized (semaphore ) {
                    semaphore.wait(30*1000);
                }
            }
            catch (InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }
}
