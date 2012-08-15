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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

import net.bioclipse.business.BioclipsePlatformManager;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.util.FileUtil;
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
	private boolean rightRVersion = true;
    public static String NEWLINE    = System.getProperty("line.separator");
    public static String cmdparser    = "(;?\r?\n|;)";
    public static final String fileseparator = java.io.File.separator;

	public RBusinessManager() throws LoginException, NoSuchElementException {	
	    logger.info("Starting R manager");
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root  = workspace.getRoot();
		workspacePath = root.getLocation();
		logger.debug("Bioclipse working directory: " + workspacePath.toString());
	    
        //Read R_HOME from prefs
        R_HOME
            = Activator.getDefault().getPreferenceStore()
                       .getString( net.bioclipse.r.business
                                   .Activator.PREF_R_HOME );
        
	    logger.debug("Pref R_home: "+ R_HOME);
	    boolean emptyRhome=false;
	    if (R_HOME.isEmpty()) {
	    	emptyRhome=true;
	    	R_HOME = System.getenv("R_HOME");
	    }
	    
	    logger.debug("R_HOME=" + R_HOME);
		try {
			R_HOME = checkR_HOME(R_HOME);		// check if R_HOME is correct
			checkRdependencies();				// check if we run right R version and all plug-ins are installed in R
			// next, check if there are $HOME-based lib paths
			String userLibPath = checkUserLibDir();
			logger.debug("User path: " + userLibPath);
			rsmanager.setEmbedded(R_HOME, userLibPath);		// Start Rservi

			//If the preference was empty, we have discovered a new RHOME
			if (emptyRhome){

				//Set default preference
				Activator.getDefault().getPreferenceStore()
	            .setDefault(net.bioclipse.r.business
	                        .Activator.PREF_R_HOME, R_HOME);

				//Set current preference
				Activator.getDefault().getPreferenceStore()
	            .setValue(net.bioclipse.r.business
	                        .Activator.PREF_R_HOME, R_HOME);

			}
		}
		catch (FileNotFoundException e) {
			working = false;
			status = e.getMessage();
		}
		catch (CoreException e) { 				// Catch rj startup error.
			working = false;
			status = e.getMessage();
		} catch (BioclipseException e) {
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
			Runtime rt = Runtime.getRuntime();
            Process pr;
            if (OS.startsWith("Mac"))
            	pr = rt.exec(new String[] { "bash", "-c", command });
            else if (OS.startsWith("Windows")) {
            	pr = rt.exec(command);
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
	            }
	            result = true; // R CMD INSTALL --no-test-load package.tar.gz does not return output that can be gotten by getInputStream
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
	 * Check if R version is above 2.12.9
	 * and if all R dependencies are installed, such as "rj", "rJava", and "bc2r"
	 * @throws BioclipseException
	 */
	private void checkRdependencies() throws FileNotFoundException, BioclipseException {
    	runRCmd("R -e \"getRversion()\" -s");
    	int st = compare(status.substring(5, (status.length() - 2)), "2.12.9");
		if (st < 0) {
			rightRVersion = false;
			throw new BioclipseException("Incompatible R version, Runnig R within Bioclipse requires R version 2.13, or later!");
    	}
		logger.debug(status);
    	if (!runRCmd("R -e \".find.package('rJava')\" -s")) {
    		logger.debug("Error: Package rJava not found.");
    		if (!runRCmd("R -e \"install.packages('rJava', repos='http://cran.stat.ucla.edu')\" -s")) {
    			status += "Error finding and installing rJava, use install.packages('rJava') within R and reboot Bioclipse afterwards";
    			logger.error(status);
    			throw new FileNotFoundException(status);
    		}

    	}
    	if (!runRCmd("R -e \".find.package('rj')\" -s")) {
    		logger.debug("Error: Package rj not found.");
    		installRj();
    	} else {
    		runRCmd("R -e \"installed.packages()['rj','Version']\" -s");
    		if (!status.contains("1.0")) {
    			status += "Wrong 'rj' package installed, please install version 1.0";
    			logger.error(status);
    			if (runRCmd("R -e \"remove.packages('rj')\" -s"))
    				installRj();
    		}
    	}
    	if (!runRCmd("R -e \".find.package('bc2r')\" -s")) {
    		String rPluginPath = null;
    		logger.debug("Error: Package bc2r not found.");
    		try {
				rPluginPath = FileUtil.getFilePath("bc2r_1.0.tar.gz", "net.bioclipse.r.business");
				if (OS.startsWith("Windows")) {
					rPluginPath = rPluginPath.substring(1).replace(fileseparator, "/");
				}
				logger.debug(rPluginPath);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.debug(e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.debug(e.getMessage());
			}
    		if (!runRCmd("R -e \"install.packages('" + rPluginPath + "', repos= NULL, type='source')\" -s")) {
    			status += "Error finding and installing bc2r package";
    			logger.error(status);
    			throw new FileNotFoundException(status);
    		}

    	}
    }

	public boolean getRightRVersion(){
		return this.rightRVersion;
	}

	private boolean installRj() throws FileNotFoundException {
		if (!runRCmd("R -e \"install.packages('rj', repos='http://download.walware.de/rj-1.0')\" -s")) {
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
    			// The replacement for front-slashes for windows systems.
    			if (part != null && part.startsWith(userHome) || part.replace("/", "\\").startsWith(userHome)) {
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
			if(!rExist(R_HOME + "/R"))
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
    	File file = new File(workspacePath.toString());
		if (!file.exists())
			file.mkdir();
		//Load the bc2r package
		eval("library(bc2r)");
		// Java crashes when setting working directory with "\" in windows
		eval("setwd(\""+file.getAbsolutePath().replace(fileseparator, "/")+"\")");
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
			status += NEWLINE + "Loaded R session: " + files[i];
			eval("load(\"" + files[i] + "\")");
		}
		status += NEWLINE + "Use load(\"file\") and save.image(\"file\") to manage your R sessions";
		if (OS.startsWith("Mac")) {	// the default plotting device on Mac(Quartz) is not working good with StatET
			eval("options(device='x11')");
		}
    }
    
    public String eval(String command) {
        logger.debug("R cmd: " + command);
        String returnVal = "R console is inactivated: " + status;
        if (working) {
        	if (command.contains("install.packages") && OS.startsWith("Mac")) {
        		int i = command.lastIndexOf(")");
        		StringBuilder cmdDefMirror = new StringBuilder(command.substring(0, i));
        		cmdDefMirror.append(", repos=\"http://cran.us.r-project.org\")");
        		command = cmdDefMirror.toString();
        	}
        	if (command.startsWith("?") && !command.contains("??"))
        		returnVal = help(command);
        	if (command.contains("help.search") || command.contains("??"))
        		returnVal = "help.search() and ?? searching is currently not supported in Bioclipse-R!";
        	else if (command.startsWith("help") && !command.contains("help.start"))
        		returnVal = help(command);
	        else if (command.contains("quartz"))
	        	returnVal = "quartz() is currently disabled for stability reasons" + NEWLINE + "Please use X11 for plotting!";
	        else if (command.contains("chooseCRANmirror") && OS.startsWith("Mac"))
	        	returnVal = "ChooseCRANmirror is not available on Mac OS X.";
	        else try {
	        	RObject data = rservi.evalData("capture.output("+command+")", null);	// capture.output(print( )) gives a string output from R, otherwise R objects. The extra pair of () is needed for the R function print to work properly.
	        	RStore rData = data.getData();
	        	StringBuilder builder = new StringBuilder();
	        	int n = rData.getLength();
	        	for(int i=0;i<n;i++) {
	        		builder.append(rData.getChar(i));
	        		if (i+1 < n)
	        			builder.append(NEWLINE);
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
	        logger.debug(" -> "+ NEWLINE + returnVal);
        	}
        return returnVal;
    }

    public String ls() {
    	return eval("ls()");
    }

    /**
     * Opens help in browser
     */
    private String help(String command) {
    	String url = null;
    	if (command.startsWith("?")) {
    		command = command.substring(1);
    	} else {
    		if (command.contains("(\"")) {
    			command = command.substring(command.indexOf("(\"") + 2, command.length() - 3);
    		} else {
    			command = command.substring(command.indexOf("(") + 1, command.length() - 2);
    		}
    	}
    	url = eval("getHelpAddress(help("+ command +", help_type=\"html\"))");
    	url = url.substring(5, url.length()-1);
    	logger.debug("URL is " + url);
    	
    	BioclipsePlatformManager bioclipse = new BioclipsePlatformManager();
    	try {
			bioclipse.openURL(new URL(url));
			return "";
		} catch (MalformedURLException e) {
			return e.getMessage();
		} catch (BioclipseException e) {
//			 TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
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

    private static int compare(String v1, String v2) {
        String s1 = normalisedVersion(v1);
        String s2 = normalisedVersion(v2);
        int cmp = s1.compareTo(s2);
        String cmpStr = cmp < 0 ? "<" : cmp > 0 ? ">" : "==";
        System.out.printf("'%s' %s '%s'%n", v1, cmpStr, v2);
        return cmp;
    }

    public static String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }

    public static String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }
}
