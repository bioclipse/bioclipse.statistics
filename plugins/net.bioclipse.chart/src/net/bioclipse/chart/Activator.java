/* ***************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.chart;


import net.bioclipse.chart.ui.business.IChartManager;
import net.bioclipse.chart.ui.business.IJavaChartManager;
import net.bioclipse.chart.ui.business.IJavaScriptChartManager;

import org.apache.log4j.Logger;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "net.bioclipse.chart";
    // Trackers for getting the managers
    private ServiceTracker javaFinderTracker;
    private ServiceTracker jsFinderTracker;
    // The shared instance
    private static Activator plugin;

    private static final Logger logger = Logger.getLogger(Activator.class);

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        logger.debug("Starting chart plugin");
        javaFinderTracker
        = new ServiceTracker( context,
                              IJavaChartManager.class.getName(),
                              null );

        javaFinderTracker.open();
        jsFinderTracker
        = new ServiceTracker( context,
                              IJavaScriptChartManager.class.getName(),
                              null );

        jsFinderTracker.open();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
        logger.debug("Stopping chart plugin");
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    public IChartManager getJavaChartManager() {
        IChartManager manager = null;
        try {
            manager = (IChartManager)
                    javaFinderTracker.waitForService(1000*10);
        }
        catch (InterruptedException e) {
            throw new IllegalStateException(
                                        "Could not get the Java ChartManager",
                                        e );
        }
        if (manager == null) {
            throw new IllegalStateException(
                    "Could not get the Java ChartManager");
        }
        return manager;
    }

    public IJavaScriptChartManager getJavaScripChartManager() {
        IJavaScriptChartManager manager = null;
        try {
            manager = (IJavaScriptChartManager)
                    jsFinderTracker.waitForService(1000*10);
        }
        catch (InterruptedException e) {
            throw new IllegalStateException(
                                  "Could not get the JavaScript ChartManager",
                                  e );
        }
        if (manager == null) {
            throw new IllegalStateException(
                    "Could not get the JavaScript ChartManager");
        }
        return manager;
    }
}

