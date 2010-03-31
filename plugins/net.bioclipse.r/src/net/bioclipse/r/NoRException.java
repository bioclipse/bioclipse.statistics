/* *****************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.r;

public class NoRException extends Exception {
    private String message;
    public NoRException(String m){
        message=m;
    }
    public NoRException(Throwable c){
        initCause(c);
        message=(c==null)?null:c.toString();
    }
    public NoRException(String m, Throwable c){
        message=m;
        initCause(c);
    }
    public String toString(){
        return message;
    }
}
