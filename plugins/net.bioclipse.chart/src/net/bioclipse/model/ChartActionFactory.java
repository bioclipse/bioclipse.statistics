/* ***************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.model;


/**
 * Interface for creating actions pertaining to charts
 * @author Eskil Andersen
 *
 */
public interface ChartActionFactory 
{
	public ChartAction createExportSvgAction();
	public ChartAction createExtportJpegAction();
	public ChartAction createExportPngAction();
}
