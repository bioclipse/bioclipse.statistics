/*******************************************************************************
 * Copyright (c) 2006, 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Egon Willighagen - core API and implementation
 *******************************************************************************/
package net.bioclipse.statistics;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;
/**
 * Adds matrix power to Bioclipse.
 * 
 * @author egonw
 */
public class Activator extends AbstractUIPlugin {
        private final String PLUGIN_ID="net.bioclipse.statistics";
        private final String LOG_PROPERTIES_FILE="logger.properties";
        //The shared instance.
        private static Activator plugin;
        /**
         * The constructor.
         */
        public Activator() {
                plugin = this;
        }
        /**
         * This method is called upon plug-in activation
         */
        public void start(BundleContext context) throws Exception {
                super.start(context);
                // New logger with com.tools.logging
                configureLogger();
                System.out.println("Starting statistics plugin...");
        }
        /**
         * This method is called when the plug-in is stopped
         */
        public void stop(BundleContext context) throws Exception {
                super.stop(context);
                plugin = null;
        }
        /**
         * Returns the shared instance.
         */
        public static Activator getDefault() {
                return plugin;
        }
        /**
         * Returns an image descriptor for the image file at the given
         * plug-in relative path.
         *
         * @param path the path
         * @return the image descriptor
         */
        public static ImageDescriptor getImageDescriptor(String path) {
                return AbstractUIPlugin.imageDescriptorFromPlugin("net.bioclipse.statistics", path);
        }
        private void configureLogger() {
                try {
                        URL url = Platform.getBundle(PLUGIN_ID).getEntry("/" + LOG_PROPERTIES_FILE);
                        InputStream propertiesInputStream = url.openStream();
                        if (propertiesInputStream != null) {
                                Properties props = new Properties();
                                props.load(propertiesInputStream);
                                propertiesInputStream.close();
                        }	
                } 
                catch (Exception e) {
                        String message = "Error while initializing log properties." + 
                        e.getMessage();
                        System.err.println(message);
                        throw new RuntimeException(
                                        "Error while initializing log properties.",e);
                }         
        }	
}
