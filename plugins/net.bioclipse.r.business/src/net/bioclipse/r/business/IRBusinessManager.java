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

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.statistics.model.IMatrixResource;

@PublishedClass(
    value="Bioclipse manager to interact with R."
)
public interface IRBusinessManager extends IBioclipseManager {

	@Recorded
    @PublishedMethod(
        methodSummary = "Evaluates a R command.",
        params = "String command"
    )
    public String  eval(String command);

	public String  getStatus();

	public Boolean isWorking();

	@Recorded
    @PublishedMethod(
        methodSummary = "Creates a new matrix object with the given variable" +
        		"name, from the given data.",
        params = "String varName, IMatrixResource matrixData"
    )
	public String createMatrix(String varName, IMatrixResource matrixData);

	@Recorded
    @PublishedMethod(
        methodSummary = "Lists all variables defined in the running R " +
        		"environment."
    )
	public String ls();

	@Recorded
	@PublishedMethod(
			methodSummary = "Passes the selection to eval"
	)
	public void evalSnippet(String seltext);
}
