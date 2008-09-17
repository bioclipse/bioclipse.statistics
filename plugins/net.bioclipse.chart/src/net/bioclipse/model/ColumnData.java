/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.model;

import java.util.Vector;

import org.eclipse.ui.IEditorPart;

/**
 * @author EskilA
 * Used by ChartDialog to represent columns in a spreadsheet
 */
public class ColumnData
{
	private String colLabel;
	private String rowLabel;
	public String getRowLabel() {
		return rowLabel;
	}

	public void setRowLabel(String rowLabel) {
		this.rowLabel = rowLabel;
	}

	private Vector<Cell> data;
	private IEditorPart dataSource;

	public ColumnData()
	{
		data = new Vector<Cell>();
		colLabel = "";
	}
	
	public ColumnData(String colLabel)
	{
		data = new Vector<Cell>();
		this.colLabel = colLabel;
	}
	
	public ColumnData(String colLabel, String rowLabel){
		this.colLabel = colLabel;
		this.rowLabel = rowLabel;
		data = new Vector<Cell>();
	}

	public String getLabel() {
		return colLabel;
	}

	public void setLabel(String label) {
		this.colLabel = label;
	}
	
	public double[] getValues()
	{
//		Double[] values = (Double[])data.toArray();
//		double[] doubleValues = new double[values.length];
//		for( int i = 0; i < values.length; i++)
//		{
//			doubleValues[i] = values[i].doubleValue();
//		}
//		return doubleValues;
		double[] doubleValues = new double[data.size()];
		for( int i = 0; i < data.size(); i++)
		{
			doubleValues[i] = ((Cell)data.get(i)).getValue();
		}
		return doubleValues;
	}
	
	public int[] getIndices()
	{
		int[] indices = new int[data.size()];
		for( int i = 0; i < data.size(); i++)
		{
			indices[i] = ((Cell)data.get(i)).getRowIndex();
		}
		return indices;
	}
	
	/**
	 * 
	 * @param source the editor from where the data originates
	 */
	public void setDataSource(IEditorPart source){
		dataSource = source;
	}
	
	
	/**Add a double value to the column
	 * @param value
	 */
	public void add(double value, int row)
	{
		data.add(new Cell(value,row));
	}

	@Override
	public boolean equals(Object obj) {
		if( !(obj instanceof String) )
			return false;
		String compLabel = (String)obj;
		if( compLabel.equals(this.colLabel))
			return true;
		return false;
	}

	/**
	 * 
	 * @return The editor from where the data originates
	 */
	public IEditorPart getDataSource() {
		return dataSource;
	}	
}
