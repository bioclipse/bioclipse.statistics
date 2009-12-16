/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.statistics.business.business;

import java.util.StringTokenizer;

import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.statistics.model.IMatrixResource;
import net.bioclipse.statistics.model.MatrixResource;

import org.eclipse.ui.IFileEditorInput;

public class MatrixManager implements IBioclipseManager {

    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "matrix";
    }

    public IMatrixResource create(
            String valueSequence, int ncol) {
        StringTokenizer tokenizer = new StringTokenizer(valueSequence);
        int tokenCount = tokenizer.countTokens();
        double ceil = Math.ceil((double)tokenCount/(double)ncol);
        int nrow = (int)ceil;

        IMatrixResource matrix =
            new MatrixResource("", (IFileEditorInput)null);
        matrix.setSize(nrow, ncol);

        int rowCount = 0;
        int colCount = 0;
        while (tokenizer.hasMoreTokens()) {
            double value = Double.NaN;
            try {
                value = Double.parseDouble(tokenizer.nextToken());
            } catch (NumberFormatException exception) {}
            try {
                matrix.set(rowCount+1, colCount+1, value);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            colCount++;
            if (colCount == ncol) {
                colCount = 0;
                rowCount++;
            }
        }
        
        return matrix;
    }
    
    public IMatrixResource create(double[][] values) {
        IMatrixResource matrix =
            new MatrixResource("", (IFileEditorInput)null);
        int ncol = values.length;
        int nrow = values[0].length;
        matrix.setSize(ncol, nrow);
        
        for (int row=0; row<nrow; row++) {
            for (int col=0; col<ncol; col++) {
                matrix.set(row+1, col+1, values[row][col]);
            }
        }
        return matrix;
    }

}
