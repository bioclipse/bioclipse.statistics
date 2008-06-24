/*******************************************************************************
 * Copyright (c) 2006 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Egon Willighagen - core API and implementation
 *******************************************************************************/
package net.bioclipse.plugins;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.tools.logging.PluginLogManager;

/**
 * Adds matrix power to Bioclipse.
 * 
 * @author egonw
 */
public class Bc_jamaPlugin extends AbstractUIPlugin {
	
	private final String PLUGIN_ID="net.bioclipse.statistics.jama";
	private final String LOG_PROPERTIES_FILE="logger.properties";
	private PluginLogManager logManager;
	
	//The shared instance.
	private static Bc_jamaPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public Bc_jamaPlugin() {
		plugin = this;
	}
	
	public static PluginLogManager getLogManager() {
		return getDefault().logManager; 
	}
	
	
	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		// New logger with com.tools.logging
		configureLogger();
		
		System.out.println("Starting jama plugin...");
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
	public static Bc_jamaPlugin getDefault() {
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
		return AbstractUIPlugin.imageDescriptorFromPlugin("net.bioclipse.statistics.jama", path);
	}
	
	private void configureLogger() {

		try {
			URL url = Platform.getBundle(PLUGIN_ID).getEntry("/" + LOG_PROPERTIES_FILE);
			
			InputStream propertiesInputStream = url.openStream();
			
			
			if (propertiesInputStream != null) {
				Properties props = new Properties();
				props.load(propertiesInputStream);
				propertiesInputStream.close();
				this.logManager = new PluginLogManager(this, props);
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
