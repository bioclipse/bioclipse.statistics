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

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
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
        "of values and a given number of columns."
    )
    public IMatrixResource create(
        String valueSequence, int ncol);

}
