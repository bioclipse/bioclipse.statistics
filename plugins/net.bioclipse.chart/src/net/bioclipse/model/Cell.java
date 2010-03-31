/* ***************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

/**
 * 
 */
package net.bioclipse.model;

/**
 * @author EskilA
 *
 */
public class Cell 
{
	private double value;
	private int rowIndex;
	
	public Cell( double value, int row )
	{
		this.value = value;
		this.rowIndex = row;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	
	
}
