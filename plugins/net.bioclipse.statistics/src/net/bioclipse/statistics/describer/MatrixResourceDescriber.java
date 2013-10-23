/*******************************************************************************
 * Copyright (c) 2013  Klas Jšnsson <klas.joensson@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.statistics.describer;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.statistics.model.IMatrixResource;
import net.bioclipse.ui.business.describer.IBioObjectDescriber;


public class MatrixResourceDescriber implements IBioObjectDescriber {

    public MatrixResourceDescriber() {
        
    }
    
    public String getPreferredEditorID( IBioObject object )
                                                           throws BioclipseException {

        if (object instanceof IMatrixResource) {
            String [] possibleEditorIDs = 
                    ((IMatrixResource) object).getEditorIDs();
            /* Let's assume that the first in the array is the one that is 
             * preferred.*/
            return possibleEditorIDs[0];
        }
        return null;
    }

}
