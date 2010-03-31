/* *****************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.r;

import java.util.*;

public abstract class R{
    public void destroy(){
    }
    protected void finalize(){
        destroy();
    }
    static protected String preParsedToR(List<Object> l){
        String rString=new String();

        for(int i=0; i<l.size(); i++){
            Object o=l.get(i);
            if((i&1)==0)rString+=o;
            else{
                /* quote correctly */
                if(o instanceof String)rString+="\""+o+"\"";
                else if(o instanceof Byte)rString+=o.toString();
                else if(o instanceof Short)rString+=o.toString();
                else if(o instanceof Integer)rString+=o.toString();
                else if(o instanceof Long)rString+=o.toString();
                else if(o instanceof Boolean)rString+=((Boolean)o)?"TRUE":"FALSE";
            }
        }
        return rString;
    }
}
