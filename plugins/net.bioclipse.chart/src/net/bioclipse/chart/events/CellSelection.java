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
