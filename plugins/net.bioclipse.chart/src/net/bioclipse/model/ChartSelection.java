package net.bioclipse.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.model.PlotPointData;

import org.eclipse.jface.viewers.IStructuredSelection;

public class ChartSelection implements IStructuredSelection 
{
	private List<PlotPointData> points;
	
	public ChartSelection()
	{
		points = new ArrayList<PlotPointData>();
	}
	
	/**
	 * @param ppd ChartPoint to add to selection
	 */
	public void addPoint( PlotPointData ppd )
	{
		points.add(ppd);
	}
	
	/**
	 * @param ppd ChartPoint to remove from selection
	 */
	public void removePoint( PlotPointData ppd)
	{
		points.remove(ppd);
	}
	
	public boolean isEmpty() {
		return points.isEmpty();
	}

	public Object getFirstElement() {
		return points.get(0);
	}

	public Iterator<PlotPointData> iterator() {
		return points.iterator();
	}

	public int size() {
		return points.size();
	}

	public Object[] toArray() {
		return points.toArray();
	}

	public List<PlotPointData> toList() {
		return points;
	}

}
