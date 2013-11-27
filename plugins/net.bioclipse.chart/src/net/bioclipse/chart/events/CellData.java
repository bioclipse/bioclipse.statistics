/* ***************************************************************************
 * Copyright (c) 2008, 2013 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

/**
 * 
 */
package net.bioclipse.chart.events;

/**
 * @author EskilA, Klas Jšnsson
 *
 */
public class CellData 
{
	//Name of the column containing the selected cell
	private String colName;
	private String rowName;
	private int col;
	//Row of the selected cell
	private int row;
	//Value of the selected cell
	private String value;
	
	public CellData()
	{
		
	}
	
	public CellData( String colName, int row, String value )
	{
		this.colName = colName;
		this.row = row;
		this.value = value;
	}
	
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public int getRowIndex() {
		return row;
	}
	public void setRowIndex(int row) {
		this.row = row;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public void setRowName(String rowName) {
		this.rowName = rowName;
	}

	public String getRowName() {
		return rowName;
	}

	public void setColIndex(int colIndex) {
		this.col = colIndex;
	}

	public int getColIndex() {
		return col;
	}
}
