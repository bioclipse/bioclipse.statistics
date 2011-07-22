/*******************************************************************************
 * Copyright (c) 2011  Egon Willighagen <egon.willighagen@gmail.com>
 * 					   Christian Ander  <christian.ander@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.r.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.NoSuchElementException;

import javax.security.auth.login.LoginException;

import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.r.RServiManager;
import net.bioclipse.statistics.model.IMatrixResource;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import de.walware.rj.data.RObject;
import de.walware.rj.data.RStore;
import de.walware.rj.servi.RServi;

public class RBusinessManager implements IBioclipseManager {
	
	private static final Logger logger = Logger.getLogger(RBusinessManager.class);
	private RServi  rservi;
	public  String  R_HOME;
	private String  status  = "";
	private Boolean working = true;
	private IPath	workspacePath;
	private static final String OS  = System.getProperty("os.name").toString();
	private RServiManager rsmanager = new RServiManager("Rconsole");
    public static String NEWLINE    = System.getProperty("line.separator");

	public RBusinessManager() throws LoginException, NoSuchElementException {	
	    logger.info("Starting R manager");
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root  = workspace.getRoot();
		workspacePath = root.getLocation();
		logger.debug("Bioclipse working directory: " + workspacePath.toString());
	    
	    R_HOME = System.getenv("R_HOME");
	    
	    logger.debug("R_HOME=" + R_HOME);
		try {
			R_HOME = checkR_HOME(R_HOME);		// chech if R_HOME is correct
			checkRdependencies();				// check if all plugins are installed in R
			// next, check if there are $HOME-based lib paths
			String userLibPath = checkUserLibDir();
			logger.debug("User path: " + userLibPath);
			rsmanager.setEmbedded(R_HOME, userLibPath);		// Start Rservi
		}
		catch (FileNotFoundException e) {
			working = false;
			status = e.getMessage();
		}
		catch (CoreException e) { 				// Catch rj startup error.
			working = false;
			status = e.getMessage();
		}
		if (working) {
			try {
				rservi = rsmanager.getRServi("Rconsole");
				initSession();
			}
			catch (CoreException e) { 
			working = false;
			status = e.getMessage();
			}
		}
		if (!working) logger.error(status);
	}
	
    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "r";
    }
    
    public String getStatus() {
    	return status;
    }
    
    public Boolean isWorking() {
    	return working;
    }

    /**
     * Run system commands
     */
    public boolean runCmd(String command) {
		logger.debug(command);
		StringBuilder s = new StringBuilder();
		String     line = null;
		boolean  result = false; // if command is successful
		try {
//TODO: ProcessBuilder might be better.
			Runtime rt = Runtime.getRuntime();
            Process pr;
//            ProcessBuilder pb;
            if (OS.startsWith("Mac"))
            	pr = rt.exec(new String[] { "bash", "-c", command });
            else if (OS.startsWith("Windows")) {
            	pr = rt.exec(command);
//            	pb = new ProcessBuilder(command);
//            	pr = pb.start();
            }
            else if (OS.startsWith("Linux"))
            	// TODO check if Linux command is working           
            	pr = rt.exec(new String[] { "sh", "-c", command });
            else
            	pr = rt.exec(command);
            int exitVal = pr.waitFor();
            
            if (exitVal != 0) {		// Command fail
            		BufferedReader input = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
            		
                s.append("ERROR: ");
                while((line=input.readLine()) != null) {
                    s.append(line);
                    s.append("\n");
                }
            }
            else {					// Command success
	            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
	
	            while((line=input.readLine()) != null) {
	                s.append(line);
	                s.append("\n");
	                result = true;
	            }
            }
        } catch(Exception e) {
        	working = false;
        	status = e.toString();
        	logger.error(status);
        }
        status=s.toString();
        logger.debug(status);
        return result;
	}
    
    private boolean runRCmd(String Rcommand) {
    	if (OS.startsWith("Windows"))
    		return runCmd(R_HOME + "\\bin\\" + Rcommand);
    	else return runCmd(Rcommand);
    }
    
	/**
	 * Check if all R dependencies are installed, such as "rj" and "rJava"
	 */
	private void checkRdependencies() throws FileNotFoundException {
    	if (!runRCmd("R -e \".find.package('rJava')\" -s")) {
    		logger.debug("Error: Package rJava not found.");
    		if (!runRCmd("R -e \"install.packages('rJava', repos='http://cran.stat.ucla.edu')\" -s")) {
    			status += "Error finding and installing rJava, use install.packages('rJava') within R";
    			logger.error(status);
    			throw new FileNotFoundException(status);
    		}
    		
    	}
    	if (!runRCmd("R -e \".find.package('rj')\" -s")) {
    		logger.debug("Error: Package rj not found.");
    		installRj();
    	} else {
    		runRCmd("R -e \"installed.packages()['rj','Version']\" -s");
    		if (!status.contains("0.5.5-4")) {
    			status += "Wrong 'rj' package installed, please install version 0.5.5-4";
    			logger.error(status);
    			if (runRCmd("R -e \"remove.packages('rj')\" -s"))
    				installRj();
    		}
    	}
    }

	private boolean installRj() throws FileNotFoundException {
		if (!runRCmd("R -e \"install.packages('rj', repos='http://download.walware.de/rj-0.5')\" -s")) {
			status += "Error installing rj-package, try manually from: http://www.walware.de/it/downloads/rj.mframe";
			logger.error("Error: Installation of rj failed.");
			throw new FileNotFoundException(status);
		}
		return working;
	}

	/**
	 * For some reason or another, on Linux, when booting R with StatET it does
	 * not see all the same lib paths as when booted from the command line.
	 * 
	 * Thus, this method runs <code>.libPatsh()</code> to detect a user dir
	 * lib path, by comparing given paths to the user.home Java property,
	 * and returns that value.
	 * 
	 * @return null, if no user.home-based lib path is found
	 */
	private String checkUserLibDir() {
		// user .libPaths() to list all paths known when R is run from the 
		// command line
    	if (!runRCmd("R -e \".libPaths()\" -s")) {
    		logger.error("Could not detect user lib path.");
    	} else {
    		// split on '"'. This gives irrelevant strings, but those will
    		// not match a user.home anyway
    		String[] parts = status.split("\\\"");
    		String userHome = System.getProperty("user.home");
    		logger.debug("user.home: " + userHome);
    		for (int i=0; i<parts.length; i++) {
    			String part = parts[i];
    			if (part != null && part.startsWith(userHome)) {
    				// OK, we found the first lib path in $HOME
    				return part;
    			}
    		}
    	}
		return null;
    }

//	Check if R_HOME is correctly set and tries to correct simple errors.
	public String checkR_HOME(String path) throws FileNotFoundException {
		Boolean trustRPath = false;
		if (OS.startsWith("Mac")) {
			if (R_HOME == null)			
				path = "/Library/Frameworks/R.framework/Resources";
			trustRPath = rExist(path + "/R");
		} else if (OS.startsWith("Windows")) {
			if (R_HOME == null) {
				path = RegQuery("HKLM\\SOFTWARE\\R-core\\R /v InstallPath");
				if (path == null)
					path = "";
			}
			trustRPath = rExist(path + "\\bin\\R.exe"); 
		} else if (OS.startsWith("Linux")) {
			trustRPath = rExist(path + "/bin/R");
//			link: /usr/bin/R -> /usr/lib/R/bin/R
//			no link: /usr/lib/R/R -> /usr/lib/R/bin/R 
//		    R_HOME is /usr/lib/R
		}
		if (!trustRPath)
			throw new FileNotFoundException("Incorrect R_HOME path: " + path);
		logger.debug("R_HOME = " + path);
		return path;
	}

	private Boolean rExist(String testPath) {
		File f = new File(testPath);
		return f.exists();
	}

	/**
	 * 	Extract registry keys from Windows OS
	 */
	private String RegQuery(String key) {

		final String REGQUERY_UTIL = "reg query ";
		final String REGSTR_TOKEN  = "REG_SZ";
		String QUERY = REGQUERY_UTIL + key;
		String result;

		final class StreamReader extends Thread {
			private InputStream is;
			private StringWriter sw;

			StreamReader(InputStream is) {
				this.is = is;
				sw = new StringWriter();
			}

			public void run() {
				try {
					int c;
					while ((c = is.read()) != -1)
						sw.write(c);
				}
				catch (IOException e) { ; }
			}

			String getResult() {
				return sw.toString();
			}
		}

		try {
			Process process = Runtime.getRuntime().exec(QUERY);
			StreamReader reader = new StreamReader(process.getInputStream());
			reader.start();
			process.waitFor();
			reader.join();
			result = reader.getResult();
			int p = result.indexOf(REGSTR_TOKEN);
			if (p == -1)
				result = null;
			result = result.substring(p + REGSTR_TOKEN.length()).trim();
		}
		catch (Exception e) {
			logger.debug(e.getMessage());
			result = null;
		}

		logger.debug(result);
		return result;
	}

	private void initSession() {
    	File file = new File(workspacePath.toString()+"/r");
		if (!file.exists())
			file.mkdir();
		eval("setwd(\""+file.getAbsolutePath()+"\")");
		status = "R workspace: " + eval("getwd()").substring(3);
		FilenameFilter filter = new FilenameFilter() {		// Filter out the R-session files
			@Override
			public boolean accept(File dir, String name) {
				logger.debug(name);
			return name.contains(".RData");
			}
		};
		// Show R sessionfiles for user
		String[] files = file.list(filter);
		for(int i=0; i<files.length; i++){
			status += NEWLINE + "Found R session: " + files[i];
		}
		status += NEWLINE + "Use load(\"file\") and save.image(\"file\")";
    }
    
    public String eval(String command) {
        logger.debug("R cmd: " + command);
        String returnVal = "R console is inactivated: " + status;
        if (working) {
	        if (command.startsWith("?"))
	        	returnVal = help(command.substring(1));
	        else try {
	        	RObject data = rservi.evalData("capture.output(print("+command+"))", null);	// capture.output(print( )) gives a string output from R, otherwise R objects. The extra pair of () is needed for the R function print to work properly.
	        	RStore rData = data.getData();
	        	StringBuilder builder = new StringBuilder();
	        	for(int i=0;i<rData.getLength();i++) {
	        		builder.append(rData.getChar(i));
	        	}
	        	returnVal = builder.toString();
	        }
	        catch (CoreException rError) {	// Catch R errors.
	        	returnVal = "Error: " + extractRError(rError.getMessage());
	        }
	        catch (Throwable error) {
	        	error.printStackTrace();
	        	returnVal = "Error: " + error.getMessage();
	        }
	        logger.debug(" -> " + returnVal);
        	}
        return returnVal;
        }

    /**
     * Opens help in browser
     */
    private String help(String command) {
    	eval("help("+ command +", help_type=\"html\")");
    	return "";
    	
//		TODO remove this if not used. To open an in-browser.
//    	BioclipsePlatformManager bioclipse = new BioclipsePlatformManager();
//    	try {
//			bioclipse.openURL(new URL(url));
//			return "";
//		} catch (MalformedURLException e) {
//			return e.getMessage();
//		} catch (BioclipseException e) {
//			 TODO Auto-generated catch block
//			e.printStackTrace();
//			return e.getMessage();
//		}
    }

    public String createMatrix(String varName, IMatrixResource matrixData) {
    	StringBuffer results = new StringBuffer();
    	results.append(eval(
    	    "connection <- " + "textConnection(\"" +
    	    matrixAsString(matrixData) + "\")"
    	));
    	results.append(eval(
    	    varName + " <- read.csv(connection)"
    	));
    	results.append(eval(
        	"close(connection)"
    	));
    	return results.toString();
    }
    
    private String matrixAsString(IMatrixResource matrix) {
		StringBuffer buffer = new StringBuffer();

		for (int col=1; col<=matrix.getColumnCount(); col++) {
			buffer.append(
				matrix.getColumnName(col) == null ?
				"X" + col : matrix.getColumnName(col)
			);
			if (col<matrix.getColumnCount()) {
				buffer.append(",");
			}
		}
		buffer.append("\n");
		
		for (int row=0; row<matrix.getRowCount(); row++) {
			if (matrix.getRowName(row+1) != null) {
				buffer.append(matrix.getRowName(row+1)).append(",");
			}
			
			for (int col=0; col<matrix.getColumnCount(); col++) {
				buffer.append(matrix.get(row+1, col+1));
				if (col<matrix.getColumnCount()-1) {
					buffer.append(",");
				}
			}
			buffer.append("\n");
		}
		return buffer.toString();
    }

    private String extractRError(String error) {
    	logger.debug("full error: " + error);
    	String result = error;
    	if (error.startsWith("Evaluation failed")) {
    		result = error.substring(error.indexOf(":")+1).trim();
    		int index;
    		if ((index=result.indexOf("):")) > 0) {
    			result = result.substring(index+2, result.lastIndexOf(">.")).trim();
    		}
    	}
    	return result;
    }
}
