/* *****************************************************************************
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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.statistics.model.IMatrixResource;

@PublishedClass(
    value="Manager to assist working with Bioclipse Matrix domain " +
    		"objects."
)
public interface IMatrixManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod(methodSummary=
        "Creates a new matrix domain object, staring from a sequence " +
        "of values and a given number of columns.",
        params="String valueSequence, int ncol"
    )
    public IMatrixResource create(
        String valueSequence, int ncol);

    @Recorded
    @PublishedMethod(methodSummary=
        "Creates a new matrix domain object, starting from a array of " +
        "double arrays."
    )
    public IMatrixResource create(double[][] values);

    @Recorded
    @PublishedMethod(methodSummary=
        "Creates a new, empty matrix domain object."
    )
    public IMatrixResource empty();

    @Recorded
    @PublishedMethod(
    	params="IMatrixResource matrix, List<String> values",
    	methodSummary="Adds a new row to the existing matrix. Throws an exception " +
    		"when the number of values is incompatible with the number of columns " +
    		"of the existing matrix." 
    )
    public IMatrixResource addRow(IMatrixResource matrix, List<Double> values)
    throws BioclipseException;

    @Recorded
    @PublishedMethod(methodSummary=
        "Sets the column labels of the matrix.",
        params="IMatrixResource matrix, String[] names"
    )
    public void setColumnLabels(IMatrixResource matrix, String[] names);

    @Recorded
    @PublishedMethod(methodSummary=
        "Sets the row labels of the matrix.",
        params="IMatrixResource matrix, String[] names"
    )
    public void setRowLabels(IMatrixResource matrix, String[] names);
    
    @Recorded
    @PublishedMethod(
    	params="IMatrixResource matrix, String fileName",
    	methodSummary="Saves a matrix as CSV file."
    )
    public void saveAsCSV(IMatrixResource matrix, String fileName)
    throws InvocationTargetException, BioclipseException, CoreException;
    
}
