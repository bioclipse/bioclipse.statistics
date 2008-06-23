/*******************************************************************************
 * Copyright (c) 2005 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth - core API and implementation
 *******************************************************************************/

package net.bioclipse.plugins;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import net.bioclipse.BioclipsePlugin;
import net.bioclipse.interfaces.IBioclipsePlugin;

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

import com.tools.logging.PluginLogManager;

/**
 * Chart plugin for drawing charts, histograms, etc.
 * 
 * 
 * @author ola
 *
 */
public class ChartPlugin extends BioclipsePlugin implements IBioclipsePlugin {

	private final String PLUGIN_ID="net.bioclipse.chart";
	private final String LOG_PROPERTIES_FILE="logger.properties";
	private PluginLogManager logManager;
	
	//The shared instance.
	private static ChartPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public ChartPlugin() {
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
		
		//New logger with com.tools.logging
		configureLogger();
		
//		configureExternalLogger();
		System.out.println("Starting Chart plugin...");
		
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
	public static ChartPlugin getDefault() {
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
		return AbstractUIPlugin.imageDescriptorFromPlugin("net.bioclipse.plugins.chart", path);
	}
	
	private void configureLogger() {

		try {
			URL url = Platform.getBundle(PLUGIN_ID).getEntry("/" + LOG_PROPERTIES_FILE);
			
			System.out.println("Chart plugin logging using properties file: " + url.toExternalForm());
			InputStream propertiesInputStream = url.openStream();
			
			
			if (propertiesInputStream != null) {
				Properties props = new Properties();
				props.load(propertiesInputStream);
				propertiesInputStream.close();
				this.logManager = new PluginLogManager(this, props);
//				this.logManager.hookPlugin(
//				TestPlugin.getDefault().getBundle().getSymbolicName(),
//				TestPlugin.getDefault().getLog()); 
			}	
		} 
		catch (Exception e) {
			String message = "Error while initializing log properties." + 
			e.getMessage();
			System.out.println(message);
			throw new RuntimeException(
					"Error while initializing log properties.",e);
		}         
	}

	public String getOPEN_PERSP_FOR_RES() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getOpenPerspectivePreference() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setOpenPerspectivePreference(int value) {
		// TODO Auto-generated method stub
		
	}	
	

}
