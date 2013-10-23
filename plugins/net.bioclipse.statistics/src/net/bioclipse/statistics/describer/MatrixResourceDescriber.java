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
