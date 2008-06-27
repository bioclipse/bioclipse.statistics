package net.bioclipse.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.JFreeChart;

/**
 * Manages all chart instances
 * @author Eskil Andersen
 *
 */
public class ChartManager 
{
	private HashMap<JFreeChart, ChartDescriptor> charts;
	private List<ChartModelListener> chartModelListeners;
	
	public ChartManager(){
		charts = new HashMap<JFreeChart, ChartDescriptor>();
	}
	
	public ChartDescriptor put(JFreeChart chart, ChartDescriptor descriptor){
		return charts.put(chart, descriptor);
	}
	
	public ChartDescriptor get(JFreeChart key)
	{
		return charts.get(key);
	}
	
	public void fireUpdated(){
		
		ChartModelEvent e = new ChartModelEvent(true);
		for (Iterator<ChartModelListener> iterator = chartModelListeners.iterator(); iterator.hasNext();) 
		{
			ChartModelListener listener = iterator.next();
			listener.handleChartModelEvent(e);			
		}
	}
	
	public void addListener(ChartModelListener listener){
		if( !chartModelListeners.contains(listener))
			chartModelListeners.add(listener);
	}
	
	public void removeListener(ChartModelListener listener){
		if( chartModelListeners.contains(listener))
			chartModelListeners.remove(listener);
	}
}
