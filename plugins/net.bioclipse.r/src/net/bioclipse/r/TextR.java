/* *****************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.r;

import net.bioclipse.r.R;
import net.bioclipse.scripting.OutputProvider;
import net.bioclipse.ui.JsPluginable;

import java.util.*;
import java.io.*;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;


public class TextR extends R implements Runnable, JsPluginable {
    private Process rProcess;
    private OutputStream stdin;
    private InputStream stdout;
    private InputStream stderr;
    private OutputProvider outputProvider;
    private Thread thread;
    private byte[] outbuffer=new byte[256];
//    private byte[] errbuffer=new byte[256];
    
    public TextR(OutputProvider op) throws NoRException{
        Runtime r=Runtime.getRuntime();
        String[] rArgs=
            new String[]{"--vanilla", "--slave", "--no-save", "--no-restore-data"};
        Vector<String> rCommand=new Vector<String>(Arrays.asList(rArgs));
        
        if(System.getProperty("os.name").startsWith("Windows")){
            // Hello, Windows!
            // What is the switch to make R interactive on Windows?
            rCommand.add(0, "R");
            try{rProcess=r.exec(rCommand.toArray(rArgs));}
            catch(IOException ioe){
                throw new NoRException("The R interpreter could not be found.");
            }
            // Will it ever die? Who knows!? Answer: The Windows gurus.
        }
        else{
            // Everything else is Unix for now, and has pseudo terminals
            // OK, so here the arguments don't do anything. *** TODO.
            String ptyWrapper;
            try{
                String osPart=(System.getProperty("os.name") + "-" 
                        + System.getProperty("os.arch")).toLowerCase();
                
                //We can't have spaces in file name, so handle Mac OS X individually
                if (osPart.startsWith( "mac os x" ))
                    osPart="macosx-i386";

                ptyWrapper=FileLocator.toFileURL(
                        Platform.getBundle("net.bioclipse.r").getEntry("/native-bin")
                ).getFile()+"pty-"+ osPart;
            }
            catch(IOException ioe){
                throw new NoRException(ioe);
            }

            rCommand.add(0, ptyWrapper);
            try{rProcess=r.exec(rCommand.toArray(rArgs));}
            catch(IOException ioe){
                throw new NoRException("R is not supported on this platform: " 
                                       + ioe.getMessage());
            }
            // *** Add test to see if the wrapper execed R OK. Bah, how?
        }
        stdin=rProcess.getOutputStream();
        stdout=rProcess.getInputStream();
        stderr=rProcess.getErrorStream();

        outputProvider=op;
        thread=new Thread(this, "Interactive R");
        thread.start();
    }
    public void setOutputProvider(OutputProvider op) {
        outputProvider = op;
    }
    public void destroy(){
        rProcess.destroy();
    }
    public void eval(List<Object> l)throws IOException{
        String rCode=preParsedToR(l)+"\n";
        stdin.write(rCode.getBytes("ISO-8859-1"));
        stdin.flush();
    }
    public void run(){
        int read;
        while(true){
            try{
                read=stdout.read(outbuffer);
            }
            catch(IOException ioe){
// TODO: check conditions under which this occurs, possibly kill R here
                outputProvider.output(ioe.toString());
                return;
            }
            if(read==-1){
// TODO: wait for child? fuckit, zombies rule.
                outputProvider.output("R died\n");
                return;
            }
            try{
                outputProvider.output(new String(outbuffer, 0, read, "ISO-8859-1"));
            }
            catch(UnsupportedEncodingException uee){
                outputProvider.output("<Character encoding failed>");
            }
        }
            
    }
}
