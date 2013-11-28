/* ***************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.statistics.model;

public abstract class MatrixImplementationResource implements IMatrixImplementationResource {

	public Object getAdapter(Class adapter) {
		return null;
	}

	public boolean testAttribute(Object target, String name, String value) {
		return false;
	}

	/* TODO The following methods are not implemented in the JamaMatrix, if it's
	 * to be used it should implement these methods*/
	public void moveRowHeaderToColumn(int index) throws IllegalAccessException {
	    throw new IllegalAccessException( "Not implemented for this matrix type" );
	}
	
	public void setRowAsColumnHeader(int index) throws IllegalAccessException {
	    throw new IllegalAccessException( "Not implemented for this matrix type" );
	}
	
	public void moveColumnHeaderToRow(int index) throws IllegalAccessException {
	    throw new IllegalAccessException( "Not implemented for this matrix type" );
	}
	
	public void setColumnAsRowHeader(int index) throws IllegalAccessException {
	    throw new IllegalAccessException( "Not implemented for this matrix type" );
	}
}
