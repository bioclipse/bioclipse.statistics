/* *****************************************************************************
 *Copyright (c) 2011 christian.ander@gmail.com & stephan.wahlbrink@walware.de
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

package net.bioclipse.r;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;

import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import de.walware.ecommons.net.RMIRegistry;
import de.walware.ecommons.net.RMIUtil;
import de.walware.rj.RjException;
//import de.walware.rj.eclient.graphics.ERGraphicFactory;  // Not want graphics at this time
import de.walware.rj.rsetups.RSetup;
import de.walware.rj.rsetups.RSetupUtil;
//import de.walware.rj.server.RjsComConfig;
import de.walware.rj.servi.RServi;
import de.walware.rj.servi.RServiUtil;
import de.walware.rj.servi.pool.EmbeddedRServiManager;
import de.walware.rj.servi.pool.RServiImplE;
import de.walware.rj.servi.pool.RServiNodeConfig;
import de.walware.rj.servi.pool.RServiNodeFactory;

public class RServiManager {
	
	private static final Logger logger = Logger.getLogger(RServiManager.class);
	
	{
	logger.info("Starting R-servi Manager");
    logger.debug("R_HOME=" + System.getenv("R_HOME"));
	}
	
	private static final int EMBEDDED = 1;
	private static final int POOL = 2;
	private static final int RSETUP = 3;
	
	private static class Config {
		private int mode;
		private String address;
	}
	
	private String name;
	private Config config = new Config();
	
	private EmbeddedRServiManager embeddedR;
	
	private ISchedulingRule schedulingRule = new ISchedulingRule() {
		public boolean contains(final ISchedulingRule rule) {
			return (rule == this);
		}
		public boolean isConflicting(final ISchedulingRule rule) {
			// if concurrent remote instances are desired, return false here
			return (rule == this);
		}
	};

//  Commentated out the graphicparts (cande)	
//	public RServiManager(final String appId, final ERGraphicFactory graphicFactory) {
	public RServiManager(final String appId) {
	    this.name = appId;
		
//	    RjsComConfig.setProperty("rj.servi.graphicFactory", graphicFactory);
	}
	
//	Check if R_HOME is correctly set and tries to correct simple errors.
	public String checkRPath(String path) throws FileNotFoundException {
		Boolean trustRPath = false;
		String OS = System.getProperty("os.name").toString();
		if (OS.startsWith("Mac")) {
			if (!path.endsWith("/"))
				path += "/";
			trustRPath = rExist(path + "R");
		} else if (OS.startsWith("Windows")) {
			if (!path.endsWith("\\"))
				path += "\\";
			if (rExist(path + "bin\\R.exe"))
				trustRPath = true;
			else if (rExist(path + "R.exe"))
				path = path.substring(0, path.indexOf("bin\\"));
				logger.info("R_HOME path corrected, removed bin\\ from path.");
				trustRPath = true;
		} else if (OS.startsWith("Linux")) {
			if (!path.endsWith("/"))
				path += "/";
			trustRPath = rExist(path + "bin/R");
//			link: /usr/bin/R -> /usr/lib/R/bin/R
//			no link: /usr/lib/R/R -> /usr/lib/R/bin/R 
//		    R_HOME is /usr/lib/R
		}
		if (!trustRPath)
			throw new FileNotFoundException("Incorrect R_HOME path: " + path);
		logger.debug("New path: " + path);
		return path;
	}
	
	private Boolean rExist(String testPath) {
		File f = new File(testPath);
		return f.exists();
	}
	
	public ISchedulingRule getSchedulingRule() {
		return schedulingRule;
	}

	
	public void setEmbedded(final String rHome) throws CoreException {
			logger.debug("Using path: " + rHome);
			final Config config = new Config();
			config.mode = EMBEDDED;
			config.address = rHome;
			this.config = config;
		
			final RServiNodeConfig rConfig = new RServiNodeConfig();
			rConfig.setRHome(rHome);
			rConfig.setEnableVerbose(true);
		
			startEmbedded(rConfig);
	}
	
	public void setPool(final String poolAddress) {
		final Config config = new Config();
		config.mode = POOL;
		config.address = poolAddress;
		this.config = config;
	}
	
	public void setRSetup(final String setupId) throws CoreException {
		final Config config = new Config();
		config.mode = RSETUP;
		config.address = setupId;
		this.config = config;
		
		final RSetup setup = RSetupUtil.loadSetup(setupId, null);
		if (setup == null) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "No R setup with specified id found."));
		}
		
		final RServiNodeConfig rConfig = new RServiNodeConfig();
		rConfig.setRHome(setup.getRHome());
		setLibs(setup.getRLibsSite(), rConfig, "R_LIBS_SITE");
		setLibs(setup.getRLibs(), rConfig, "R_LIBS");
		setLibs(setup.getRLibsUser(), rConfig, "R_LIBS_USER");
		rConfig.setEnableVerbose(true);
		
		startEmbedded(rConfig);
	}
	
	private void setLibs(final List<String> locations, final RServiNodeConfig rConfig, final String varName) {
		if (locations != null && locations.size() > 0) {
			final StringBuilder sb = new StringBuilder(locations.get(0));
			for (int i = 0; i < locations.size(); i++) {
				sb.append(File.pathSeparatorChar);
				sb.append(locations.get(i));
			}
			rConfig.getEnvironmentVariables().put(varName, sb.toString());
		}
	}
	
	private void startEmbedded(final RServiNodeConfig rConfig) throws CoreException {
		try {
			if (System.getSecurityManager() == null) {
				if (System.getProperty("java.security.policy") == null) {
					final String policyFile = RServiImplE.getLocalhostPolicyFile();
					System.setProperty("java.security.policy", policyFile);
				}
				System.setSecurityManager(new SecurityManager());
			}
			
			RMIUtil.INSTANCE.setEmbeddedPrivateMode(true);
			final RMIRegistry registry = RMIUtil.INSTANCE.getEmbeddedPrivateRegistry();
			final RServiNodeFactory nodeFactory = RServiImplE.createLocalhostNodeFactory(this.name, registry);
			nodeFactory.setConfig(rConfig);
			
			final EmbeddedRServiManager newEmbeddedR = RServiImplE.createEmbeddedRServi(this.name, registry, nodeFactory);
			newEmbeddedR.start();
			if (embeddedR != null) {
				embeddedR.stop();
				embeddedR = null;
			}
			embeddedR = newEmbeddedR;
		}
		catch (final RjException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Embedded R instance could not created.", e));
		}
	}
	
	
	public RServi getRServi(final String task) throws CoreException {
		final Config config = this.config;
		final String key = name + "-" + task;
		
		try {
			switch (config.mode) {
			case EMBEDDED:
			case RSETUP:
				return RServiUtil.getRServi(embeddedR, key);
			case POOL:
				return RServiUtil.getRServi(config.address, key);
			}
		}
		catch (final CoreException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "R not available, please check the configuration.", e));
		}
		catch (final LoginException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "R not available, please check the configuration.", e));
		}
		catch (final NoSuchElementException e) {
			throw new CoreException(new Status(IStatus.INFO, Activator.PLUGIN_ID, "R currently not available, please try again later.", e));
		}
		throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "R is not configured, please check the configuration."));
	}
	
}
