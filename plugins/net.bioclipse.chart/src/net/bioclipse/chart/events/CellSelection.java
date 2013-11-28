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
package net.bioclipse.chart.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;

/** Contains a selection of cells from MatrixGridEditor to be passed
 *  to ISelectionListeners  
 * @author EskilA
 *
 */
public class CellSelection implements IStructuredSelection {
	
	private List<CellData> cells;
	private Object source;
	
	
	/**
	 * 
	 * @return The source editor/view of the cell selection
	 */
	public Object getSource() {
		return source;
	}


	/**
	 * 
	 * @param source The source editor/view of the cell selection
	 */
	public void setSource(Object source) {
		this.source = source;
	}



	public CellSelection()
	{
		cells = new ArrayList<CellData>();
	}	
	
	
	
	/**
	 * @param cp ChartPoint to add to selection
	 */
	public void addCell( CellData cd )
	{
		cells.add(cd);
	}
	
	/**
	 * 
	 * @return all column names from the selection
	 */
	public List<String> getColNames(){
		Iterator<CellData> i = cells.iterator();
		List<String> colNames = new ArrayList<String>();
		String colName;
		
		while(i.hasNext() )
		{
			colName = i.next().getColName();
			if( !colNames.contains(colName))
				colNames.add(colName);
		}
		
		return colNames;
	}
	
	/**
	 * @param cp ChartPoint to remove from selection
	 */
	public void removeCell( CellData cd)
	{
		cells.remove(cd);
	}
	
	public boolean isEmpty() {
		return cells.isEmpty();
	}

	public Object getFirstElement() {
		return cells.get(0);
	}

	public Iterator<CellData> iterator() {
		return cells.iterator();
	}

	public int size() {
		return cells.size();
	}

	public Object[] toArray() {
		return cells.toArray();
	}

	public List<CellData> toList() {
		return cells;
	}

}
