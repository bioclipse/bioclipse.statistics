/* *****************************************************************************
 *Copyright (c) 2011 stephan.wahlbrink@walware.de and christian.ander@gmail.com
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

package net.bioclipse.r;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import de.walware.ecommons.net.RMIRegistry;
import de.walware.ecommons.net.RMIUtil;
import de.walware.rj.RjException;
import de.walware.rj.rsetups.RSetup;
import de.walware.rj.rsetups.RSetupUtil;
import de.walware.rj.servi.RServi;
import de.walware.rj.servi.RServiUtil;
import de.walware.rj.servi.pool.EmbeddedRServiManager;
import de.walware.rj.servi.pool.RServiImplE;
import de.walware.rj.servi.pool.RServiNodeConfig;
import de.walware.rj.servi.pool.RServiNodeFactory;

/*
 * Disabled graphic parts to reduce dependencies.
 *
import de.walware.rj.server.RjsComConfig;
import de.walware.rj.server.client.RClientGraphicFactory;
import de.walware.rj.eclient.graphics.comclient.ERClientGraphicActionsFactory;
import de.walware.rj.servi.internal.rcpdemo.Activator; */

import org.apache.log4j.Logger;

public class RServiManager {

	private static final Logger logger = Logger.getLogger(RServiManager.class);
	
	{
		logger.info("Starting R-servi Manager");
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

/*
 * this is the original constructor with graphic elements.
 *
	public RServiManager(final String appId, final RClientGraphicFactory graphicFactory) {
		this.name = appId;

		RjsComConfig.setProperty("rj.servi.graphicFactory", graphicFactory);
		RjsComConfig.setProperty("rj.servi.comClientGraphicActionsFactory",
		new ERClientGraphicActionsFactory() );
	}*/

	public RServiManager(final String appId) {
	    this.name = appId;
	}
	
	public ISchedulingRule getSchedulingRule() {
		return schedulingRule;
	}

	public void setEmbedded(final String rHome) throws CoreException {
		setEmbedded(rHome, null);
	}

	public void setEmbedded(final String rHome, final String userLibPath) throws CoreException {
		final Config config = new Config();
		logger.debug("Using R_HOME: " + rHome);
		config.mode = EMBEDDED;
		config.address = rHome;
		this.config = config;

		final RServiNodeConfig rConfig = new RServiNodeConfig();
		rConfig.setRHome(rHome);
		rConfig.setEnableVerbose(true);
		rConfig.setJavaArgs(""); // remove "-server" flag from the java command
		rConfig.setNodeArgs("-plugins=swt"); // solves the plotting issue on Windows
		// if a user lib path was defined, set that as environment variable
		// in the embedded 
		if (userLibPath != null)
			rConfig.getEnvironmentVariables().put("R_LIBS_USER", userLibPath);

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
			final RMIRegistry registry = RMIUtil.INSTANCE.getEmbeddedPrivateRegistry(new NullProgressMonitor());
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