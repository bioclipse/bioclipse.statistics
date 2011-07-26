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


//import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * The Activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The shared instance
    private static Activator plugin;

    // Trackers for getting the managers
    private ServiceTracker javaFinderTracker;
    private ServiceTracker jsFinderTracker;
    public static String R_HOME = "";

    public Activator() {
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        javaFinderTracker
            = new ServiceTracker( context,
                                  IJavaRBusinessManager.class.getName(),
                                  null );

        javaFinderTracker.open();
        jsFinderTracker
            = new ServiceTracker( context,
                                  IJavaScriptRBusinessManager.class.getName(),
                                  null );

        jsFinderTracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    public IRBusinessManager getJavaRBusinessManager() {
        IRBusinessManager manager = null;
        try {
            manager = (IRBusinessManager)
                      javaFinderTracker.waitForService(1000*10);
        }
        catch (InterruptedException e) {
            throw new IllegalStateException(
                          "Could not get the Java RBusinessManager",
                          e );
        }
        if (manager == null) {
            throw new IllegalStateException(
                          "Could not get the Java RBusinessManager");
        }
        return manager;
    }

    public IJavaScriptRBusinessManager getJavaScriptRBusinessManager() {
        IJavaScriptRBusinessManager manager = null;
        try {
            manager = (IJavaScriptRBusinessManager)
                      jsFinderTracker.waitForService(1000*60);
        }
        catch (InterruptedException e) {
            throw new IllegalStateException(
                          "Could not get the JavaScript RBusinessManager",
                          e );
        }
        if (manager == null) {
            throw new IllegalStateException(
                          "Could not get the JavaScript RBusinessManager");
        }
        return manager;
    }
}
