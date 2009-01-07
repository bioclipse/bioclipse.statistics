/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.model;


import java.util.ArrayList;

import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 * PCM XY-Dataset
 * 
 * Used for plots in JFreeChart which needs a XY-Dataset as input.
 * 
 */
public class PcmXYDataset extends AbstractXYDataset 
implements XYDataset, DomainInfo, RangeInfo{
	
	
	private static final long serialVersionUID = 6621523236504527078L;

	/** The values of the measurements. */
	private double[][] xValues;

	/** The y values. */
	private double[][] yValues;

	/** The status for the measurement, in the plot or not*/
	private ArrayList<Integer> statusArray;

	/** The name of the measurement, "mätdatanamn"*/
	private String [] nameOfObs;
	
	/** The label of the window */
	private String name;
	
	/** The label of the plot */
	private String plotLabel;
	
	/** The label of the x-axis */
	private String xLabel;
	
	/** The label of the y-axis */
	private String yLabel;

	/** The number of datapoints. Tells JFreeChart how many points there is to be plotted*/
	public int numberMeasurement;

	/** The number of items. */
	private int dataPoints;

//	The following is used by JFreeChart to determine how big the plot will be
	
	/** The minimum domain value. */
	private Number domainMin;

	/** The maximum domain value. */
	private Number domainMax;

	/** The minimum range value. */
	private Number rangeMin;

	/** The maximum range value. */
	private Number rangeMax;

	/** The range of the domain. */
	private Range domainRange;

	/** The range. */
	private Range range;

	/**
	 * Creates a dataset. Also makes the intervals for the plot
	 *
	 * @param xValues		the xValues of the measurements
	 * @param yValues		the yValues of the measurements
	 * @param nameOfObs		the name of the measurements
	 * @param xLabel		the label of the x-axis of the plot
	 * @param yLabel		the label of the y-axis of the plot
	 * @param plotLabel		the name of the label
	 * @param I				the statusarray of the plot
	 */
	public PcmXYDataset(double [][] xValues, double [][] yValues, String[] nameOfObs, String xLabel, String yLabel, String plotLabel, ArrayList<Integer> I) {

		int observations = nameOfObs.length;
		this.xValues = xValues;
		this.yValues = yValues;
		this.statusArray = I;

		this.nameOfObs = nameOfObs;
		this.plotLabel = plotLabel;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		dataPoints = 1;
		
		numberMeasurement = observations; 
				
	    for (int i=0; i<xValues.length; i++) {
	        if (xValues[i][0] > maxX) {
	        	maxX = xValues[i][0];   // new maximum
	        }
	    }
	    
	    for (int i=0; i<xValues.length; i++) {
	        if (xValues[i][0] < minX) {
	        	minX = xValues[i][0];   // new maximum
	        }
	    }
	    
	    for (int i=0; i<yValues.length; i++) {
	        if (yValues[i][0] > maxY) {
	        	maxY = yValues[i][0];   // new maximum
	        }
	    }
	    
	    for (int i=0; i<yValues.length; i++) {
	        if (yValues[i][0] < minY) {
	        	minY = yValues[i][0];   // new maximum
	        }
	    }
		this.domainMin = new Double(minX);
		this.domainMax = new Double(maxX);
		this.domainRange = new Range(minX, maxX);
		this.rangeMin = new Double(minY);
		this.rangeMax = new Double(maxY);
		this.range = new Range(minY, maxY);
	}
	/**
	 * Sets the status of the measurement in the statusarray, either 0 or 1.
	 * @param nummer		the number of the 
	 * @param newStatus		the new status, either 0 or 1
	 */
	public void setStatus(int nummer, int newStatus){
		statusArray.set(nummer, newStatus);
	}
	/**
	 * Returns the value of the statusarray, either 0 or 1.
	 * @param measurement
	 * @return the status of the measurement
	 */
	
	public int getStatus(int measurement){
		return statusArray.get(measurement);
	}
	/**
	 * Returns the statusarray which is used for the update of the chart when points are deselected
	 * 
	 * @return the statusarray
	 */
	public ArrayList<Integer> getStatusArray(){
		return this.statusArray;
	}
	/**
	 * Returns the x-value for the specified series and item.  Series are numbered 0, 1, ...
	 *
	 * @param series  the index (zero-based) of the series.
	 * @param item  the index (zero-based) of the required item.
	 *
	 * @return the x-value for the specified series and item.
	 */
	public Number getX(int measurement, int dataPoints) {
		return this.xValues[measurement][dataPoints];
	}
	
	/**
	 * Returns the y-value for the specified series and item.  Series are numbered 0, 1, ...
	 *
	 * @param series  the index (zero-based) of the series.
	 * @param item  the index (zero-based) of the required item.
	 *
	 * @return  the y-value for the specified series and item.
	 */
	public Number getY(int series, int item) {
		return this.yValues[series][item];
	}
	
	/**
	 * Returns the number of series in the dataset.
	 *
	 * @return the series count.
	 */
	
	/**
	 * Returns the x-values-array
	 *
	 * @return the x-values-array
	 */
	public double [][] getXValues(){
		return this.xValues;
	}
	/**
	 * Returns the y-values-array
	 *
	 * @return the y-values-array
	 */
	public double [][] getYValues(){
		return this.yValues;
	}
	/**
	 * Returns the name of observations-array
	 *
	 * @return the name of observations-array
	 */
	public String [] getNameOfObs(){
		return this.nameOfObs;
	}
	/**
	 * Returns the number of
	 *
	 * @return the number of
	 */	
	public int getSeriesCount() {
		return this.numberMeasurement;
	}
	/**
	 * Returns the name of the measurement
	 * @param series  the index (zero-based) of the series.
	 * @param item  the index (zero-based) of the required item.
	
	 * @return the name of the measurement
	 */
	public String getName(int item, int series){
		return this.nameOfObs[item];
	}
	/**
	 * Returns the label of the plot
	 *
	 * @return the label of the plot
	 */	
	public String getPlotLabel() {
		return this.plotLabel;
	}
	/**
	 * Returns the label of x-axis
	 *
	 * @return the label of x-axis
	 */
	public String getXLabel() {
		return this.xLabel;
	}
	/**
	 * Returns the label of y-axis
	 *
	 * @return the label of y-axis
	 */
	public String getYLabel() {
		return this.yLabel;
	}
	/**
	 * Returns the key for the series.
	 *
	 * @param series  the index (zero-based) of the series.
	 *
	 * @return The key for the series.
	 */
	public Comparable getSeriesKey(int measurement) {
		return "Sample " + measurement;//här ska det läggas till +1 så det blir lite finare i legend
	}

	/**
	 * Returns the number of items in the specified series.
	 *
	 * @param series  the index (zero-based) of the series.
	 *
	 * @return the number of items in the specified series.
	 */
	public int getItemCount(int measurement) {
		return this.dataPoints;
	}

	/**
	 * Returns the minimum domain value.
	 *
	 * @return The minimum domain value.
	 */
	public double getDomainLowerBound() {
		return this.domainMin.doubleValue();
	}

	/**
	 * Returns the lower bound for the domain.
	 * 
	 * @param includeInterval  include the x-interval?
	 * 
	 * @return The lower bound.
	 */
	public double getDomainLowerBound(boolean includeInterval) {
		return this.domainMin.doubleValue();
	}

	/**
	 * Returns the maximum domain value.
	 *
	 * @return The maximum domain value.
	 */
	public double getDomainUpperBound() {
		return this.domainMax.doubleValue();
	}

	/**
	 * Returns the upper bound for the domain.
	 * 
	 * @param includeInterval  include the x-interval?
	 * 
	 * @return The upper bound.
	 */
	public double getDomainUpperBound(boolean includeInterval) {
		return this.domainMax.doubleValue();
	}

	/**
	 * Returns the range of values in the domain.
	 *
	 * @return the range.
	 */
	public Range getDomainBounds() {
		return this.domainRange;
	}

	/**
	 * Returns the bounds for the domain.
	 * 
	 * @param includeInterval  include the x-interval?
	 * 
	 * @return The bounds.
	 */
	public Range getDomainBounds(boolean includeInterval) {
		return this.domainRange;
	}

	/**
	 * Returns the range of values in the domain.
	 *
	 * @return the range.
	 */
	public Range getDomainRange() {
		return this.domainRange;
	}

	/**
	 * Returns the minimum range value.
	 *
	 * @return The minimum range value.
	 */
	public double getRangeLowerBound() {
		return this.rangeMin.doubleValue();
	}

	/**
	 * Returns the lower bound for the range.
	 * 
	 * @param includeInterval  include the y-interval?
	 * 
	 * @return The lower bound.
	 */
	public double getRangeLowerBound(boolean includeInterval) {
		return this.rangeMin.doubleValue();
	}

	/**
	 * Returns the maximum range value.
	 *
	 * @return The maximum range value.
	 */
	public double getRangeUpperBound() {
		return this.rangeMax.doubleValue();
	}

	/**
	 * Returns the upper bound for the range.
	 * 
	 * @param includeInterval  include the y-interval?
	 * 
	 * @return The upper bound.
	 */
	public double getRangeUpperBound(boolean includeInterval) {
		return this.rangeMax.doubleValue();
	}

	/**
	 * Returns the range of values in the range (y-values).
	 *
	 * @param includeInterval  include the y-interval?
	 * 
	 * @return The range.
	 */
	public Range getRangeBounds(boolean includeInterval) {
		return this.range;
	}

	/**
	 * Returns the range of y-values.
	 * 
	 * @return The range.
	 */
	public Range getValueRange() {
		return this.range;
	}

	/**
	 * Returns the minimum domain value.
	 * 
	 * @return The minimum domain value.
	 */
	public Number getMinimumDomainValue() {
		return this.domainMin;
	}

	/**
	 * Returns the maximum domain value.
	 * 
	 * @return The maximum domain value.
	 */
	public Number getMaximumDomainValue() {
		return this.domainMax;
	}

	/**
	 * Returns the minimum range value.
	 * 
	 * @return The minimum range value.
	 */
	public Number getMinimumRangeValue() {
		return this.domainMin;
	}

	/**
	 * Returns the maximum range value.
	 * 
	 * @return The maximum range value.
	 */
	public Number getMaximumRangeValue() {
		return this.domainMax;
	}
}

