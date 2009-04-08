/*******************************************************************************
 * Copyright (c) 2006-2009 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Egon Willighagen - core API and implementation
 *     Ola Spjuth - updated for Bioclipse 2.0
 *******************************************************************************/
package net.bioclipse.statistics;

import org.eclipse.ui.plugin.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;


/**
 * Adds matrix power to Bioclipse.
 * 
 * @author egonw, olas
 */
public class Activator extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID="net.bioclipse.statistics";
	
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
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	

}
