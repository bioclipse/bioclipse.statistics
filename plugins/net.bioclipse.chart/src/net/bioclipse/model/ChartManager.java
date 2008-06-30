package net.bioclipse.model;

import java.util.ArrayList;
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
	private JFreeChart activeChart;
	
	public ChartManager(){
		charts = new HashMap<JFreeChart, ChartDescriptor>();
		chartModelListeners = new ArrayList<ChartModelListener>();
	}
	
	public ChartDescriptor put(JFreeChart chart, ChartDescriptor descriptor){
		return charts.put(chart, descriptor);
	}
	
	public ChartDescriptor get(JFreeChart key)
	{
		return charts.get(key);
	}
	
	public void fireUpdated(ChartModelEvent e){
		
		
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
	
	/**
	 * Sets the chart that's currently in focus.
	 * This is usable for actions that for example export a chart to an image who
	 * must know which chart is in focus
	 * @param chart
	 */
	public void setActiveChart(JFreeChart chart)
	{
		if( !charts.containsKey(chart))
		{
			throw new IllegalArgumentException("this ChartManager does not contain the argument chart, " +
					"please add it to this ChartManager first");
		}
		activeChart = chart;
		ChartModelEvent e = new ChartModelEvent(ChartEventType.ACTIVE_CHART_CHANGED);
		fireUpdated(e);
	}
	
	public JFreeChart getActiveChart(){
		return activeChart;
	}
}
