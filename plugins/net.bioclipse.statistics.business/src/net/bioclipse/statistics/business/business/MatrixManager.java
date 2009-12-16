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
import net.bioclipse.statistics.model.IMatrixImplementationResource;
import net.bioclipse.model.JamaMatrix;

public class MatrixManager implements IBioclipseManager {

    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "matrix";
    }

    public IMatrixImplementationResource create(
            String valueSequence, int ncol) {
        StringTokenizer tokenizer = new StringTokenizer(valueSequence);
        int tokenCount = tokenizer.countTokens();
        double ceil = Math.ceil((double)tokenCount/(double)ncol);
        int nrow = (int)ceil;
        
        IMatrixImplementationResource matrix =
            new JamaMatrix().getInstance(nrow, ncol);
        
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

}
