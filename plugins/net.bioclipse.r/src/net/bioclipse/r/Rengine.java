package net.bioclipse.r;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RMainLoopCallbacks;

/**
 * Simply class proxy to not have to export the org.rosuda.JRI package.
 */
public class Rengine extends org.rosuda.JRI.Rengine {
    
	public Rengine(String[] args, boolean runMainLoop, RMainLoopCallbacks initialCallbacks) {
		super(args, runMainLoop, initialCallbacks);
	}
	
	public synchronized String evalCommand(String s) {
	    REXP result = super.eval(s, true);
	    if (result != null) { return result.toString(); }
	    else { return (""); }
	}

}
