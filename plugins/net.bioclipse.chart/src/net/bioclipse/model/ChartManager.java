/* ***************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.bioclipse.chart.IChartDescriptor;

import org.jfree.chart.JFreeChart;

/**
 * Manages all chart instances
 * @author Eskil Andersen, Klas Jšnsson
 *
 */
public class ChartManager 
{
	private HashMap<JFreeChart, IChartDescriptor> charts;
	private List<ChartModelListener> chartModelListeners;
	private JFreeChart activeChart;
	
	public ChartManager(){
		charts = new HashMap<JFreeChart, IChartDescriptor>();
		chartModelListeners = new ArrayList<ChartModelListener>();
	}
	
	public IChartDescriptor put(JFreeChart chart, IChartDescriptor descriptor){
		return charts.put(chart, descriptor);
	}
	
	public IChartDescriptor get(JFreeChart key)
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
	 * 
	 * 
	 * @param chart the chart to be set as active or null if no chart is active, for example when no charts exists at all
	 */
	public void setActiveChart(JFreeChart chart)
	{
		if( !charts.containsKey(chart) && chart != null)
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

	public IChartDescriptor remove(Object arg0) {
		if( activeChart == arg0 )
		{
			setActiveChart(null);
		}
		
		IChartDescriptor descriptor = charts.remove(arg0);
		if( charts.isEmpty() )
			setActiveChart(null);

		return descriptor;
	}
	
	public Collection<IChartDescriptor> values() {
		return charts.values();
	}

	public Set<JFreeChart> keySet() {
		return charts.keySet();
	}

}
