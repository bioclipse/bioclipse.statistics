/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ---------------------
 * DefaultXYDataset.java
 * ---------------------
 * (C) Copyright 2006, 2007, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DefaultXYDataset.java,v 1.1.2.5 2007/01/25 14:02:04 mungady Exp $
 *
 * Changes
 * -------
 * 06-Jul-2006 : Version 1 (DG);
 * 02-Nov-2006 : Fixed a problem with adding a new series with the same key
 *               as an existing series (see bug 1589392) (DG);
 * 25-Jan-2007 : Implemented PublicCloneable (DG);
 *
 */

package net.bioclipse.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.data.DomainOrder;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;

/**
 * A dataset used by JFreeChart to plot a Line Chart from BioClipse.

 */
public class PcmLineChartDataset extends AbstractXYDataset 
        implements XYDataset, PublicCloneable {

    /**
     * Storage for the series keys.  This list must be kept in sync with the
     * seriesList.
     */
    private List seriesKeys;
    
    /** 
     * Storage for the series in the dataset.  We use a list because the
     * order of the series is significant.  This list must be kept in sync 
     * with the seriesKeys list.
     */ 
    private List seriesList;
   
    /**The label of the x-axis*/
    private String xLabel;
    
    /**The label of the y-axis*/
    private String yLabel;
   
    /**The array containing the name of the observations*/
    private String [] nameOfObs;
    
    /**The label of the plot*/
    private String plotLabel;
    
    /**The statusarray, used for removing points from the dataset*/
    private ArrayList<Integer> statusArray;
    
    /**The number of datapoints*/ //Used by JFreeChart to determine how many measurement there is to be drawn
    private int dataPoints;
    
    /** The number of measurement*/
    private int numberMeasurement;
    
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
     * Creates a new <code>DefaultPCMXYDataset</code> instance, initially 
     * containing no data.
     */
    public PcmLineChartDataset() {
        this.seriesKeys = new java.util.ArrayList();
        this.seriesList = new java.util.ArrayList();    
    }
    
    /**
     * Returns the number of series in the dataset.
     *
     * @return The series count.
     */
    public int getSeriesCount() {
        return this.seriesList.size();
    }

    /**
     * Returns the key for a series.  
     *
     * @param series  the series index (in the range <code>0</code> to 
     *     <code>getSeriesCount() - 1</code>).
     *
     * @return The key for the series.
     * 
     * @throws IllegalArgumentException if <code>series</code> is not in the 
     *     specified range.
     */
    public Comparable getSeriesKey(int series) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return (Comparable) this.seriesKeys.get(series);
    }

    /**
     * Returns the index of the series with the specified key, or -1 if there 
     * is no such series in the dataset.
     * 
     * @param seriesKey  the series key (<code>null</code> permitted).
     * 
     * @return The index, or -1.
     */
    public int indexOf(Comparable seriesKey) {
        return this.seriesKeys.indexOf(seriesKey);
    }

    /**
     * Returns the order of the domain (x-) values in the dataset.  In this
     * implementation, we cannot guarantee that the x-values are ordered, so 
     * this method returns <code>DomainOrder.NONE</code>.
     * 
     * @return <code>DomainOrder.NONE</code>.
     */
    public DomainOrder getDomainOrder() {
        return DomainOrder.NONE;
    }

    /**
     * Returns the number of items in the specified series.
     * 
     * @param series  the series index (in the range <code>0</code> to 
     *     <code>getSeriesCount() - 1</code>).
     * 
     * @return The item count.
     * 
     * @throws IllegalArgumentException if <code>series</code> is not in the 
     *     specified range.
     */
    public int getItemCount(int series) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        double[][] seriesArray = (double[][]) this.seriesList.get(series);
        return seriesArray[0].length;
    }

    /**
     * Returns the x-value for an item within a series.
     * 
     * @param series  the series index (in the range <code>0</code> to 
     *     <code>getSeriesCount() - 1</code>).
     * @param item  the item index (in the range <code>0</code> to 
     *     <code>getItemCount(series)</code>).
     *     
     * @return The x-value.
     * 
     * @throws ArrayIndexOutOfBoundsException if <code>series</code> is not 
     *     within the specified range.
     * @throws ArrayIndexOutOfBoundsException if <code>item</code> is not 
     *     within the specified range.
     * 
     * @see #getX(int, int)
     */
    public double getXValue(int series, int item) {
        double[][] seriesData = (double[][]) this.seriesList.get(series);
        return seriesData[0][item];
    }

    /**
     * Returns the x-value for an item within a series.
     * 
     * @param series  the series index (in the range <code>0</code> to 
     *     <code>getSeriesCount() - 1</code>).
     * @param item  the item index (in the range <code>0</code> to 
     *     <code>getItemCount(series)</code>).
     *     
     * @return The x-value.
     * 
     * @throws ArrayIndexOutOfBoundsException if <code>series</code> is not 
     *     within the specified range.
     * @throws ArrayIndexOutOfBoundsException if <code>item</code> is not 
     *     within the specified range.
     * 
     * @see #getXValue(int, int)
     */
    public Number getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

    /**
     * Returns the y-value for an item within a series.
     * 
     * @param series  the series index (in the range <code>0</code> to 
     *     <code>getSeriesCount() - 1</code>).
     * @param item  the item index (in the range <code>0</code> to 
     *     <code>getItemCount(series)</code>).
     *     
     * @return The y-value.
     * 
     * @throws ArrayIndexOutOfBoundsException if <code>series</code> is not 
     *     within the specified range.
     * @throws ArrayIndexOutOfBoundsException if <code>item</code> is not 
     *     within the specified range.
     * 
     * @see #getY(int, int)
     */
    public double getYValue(int series, int item) {
        double[][] seriesData = (double[][]) this.seriesList.get(series);
        return seriesData[1][item];
    }

    /**
     * Returns the y-value for an item within a series.
     * 
     * @param series  the series index (in the range <code>0</code> to 
     *     <code>getSeriesCount() - 1</code>).
     * @param item  the item index (in the range <code>0</code> to 
     *     <code>getItemCount(series)</code>).
     *     
     * @return The y-value.
     * 
     * @throws ArrayIndexOutOfBoundsException if <code>series</code> is not 
     *     within the specified range.
     * @throws ArrayIndexOutOfBoundsException if <code>item</code> is not 
     *     within the specified range.
     *     
     * @see #getX(int, int)
     */
    public Number getY(int series, int item) {
        return new Double(getYValue(series, item));
    }

    /**
     * Adds a series or if a series with the same key already exists replaces
     * the data for that series, then sends a {@link DatasetChangeEvent} to 
     * all registered listeners.
     * 
     * @param seriesKey  the series key (<code>null</code> not permitted).
     * @param data  the data (must be an array with length 2, containing two 
     *     arrays of equal length, the first containing the x-values and the
     *     second containing the y-values). 
     */
    public void addSeries(Comparable seriesKey, double[][] data) {
        if (seriesKey == null) {
            throw new IllegalArgumentException(
                    "The 'seriesKey' cannot be null.");
        }
        if (data == null) {
            throw new IllegalArgumentException("The 'data' is null.");
        }
        if (data.length != 2) {
            throw new IllegalArgumentException(
                    "The 'data' array must have length == 2.");
        }
        if (data[0].length != data[1].length) {
            throw new IllegalArgumentException(
                "The 'data' array must contain two arrays with equal length.");
        }
        int seriesIndex = indexOf(seriesKey);
        if (seriesIndex == -1) {  // add a new series
            this.seriesKeys.add(seriesKey);
            this.seriesList.add(data);
        }
        else {  // replace an existing series
            this.seriesList.remove(seriesIndex);
            this.seriesList.add(seriesIndex, data);
        }
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    /**
     * Removes a series from the dataset, then sends a 
     * {@link DatasetChangeEvent} to all registered listeners.
     * 
     * @param seriesKey  the series key (<code>null</code> not permitted).
     * 
     */
    public void removeSeries(Comparable seriesKey) {
        int seriesIndex = indexOf(seriesKey);
        if (seriesIndex >= 0) {
            this.seriesKeys.remove(seriesIndex);
            this.seriesList.remove(seriesIndex);
            notifyListeners(new DatasetChangeEvent(this, this));
        }
    }
    
    /**
     * Tests this <code>DefaultXYDataset</code> instance for equality with an
     * arbitrary object.  This method returns <code>true</code> if and only if:
     * <ul>
     * <li><code>obj</code> is not <code>null</code>;</li>
     * <li><code>obj</code> is an instance of 
     *         <code>DefaultXYDataset</code>;</li>
     * <li>both datasets have the same number of series, each containing 
     *         exactly the same values.</li>
     * </ul>
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PcmLineChartDataset)) {
            return false;
        }
        PcmLineChartDataset that = (PcmLineChartDataset) obj;
        if (!this.seriesKeys.equals(that.seriesKeys)) {
            return false;
        }
        for (int i = 0; i < this.seriesList.size(); i++) {
            double[][] d1 = (double[][]) this.seriesList.get(i);
            double[][] d2 = (double[][]) that.seriesList.get(i);
            double[] d1x = d1[0];
            double[] d2x = d2[0];
            if (!Arrays.equals(d1x, d2x)) {
                return false;
            }
            double[] d1y = d1[1];
            double[] d2y = d2[1];            
            if (!Arrays.equals(d1y, d2y)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns a hash code for this instance.
     * 
     * @return A hash code.
     */
    public int hashCode() {
        int result;
        result = this.seriesKeys.hashCode();
        result = 29 * result + this.seriesList.hashCode();
        return result;
    }
    
    /**
     * Creates an independent copy of this dataset.
     * 
     * @return The cloned dataset.
     * 
     * @throws CloneNotSupportedException if there is a problem cloning the
     *     dataset (for instance, if a non-cloneable object is used for a
     *     series key).
     */
    public Object clone() throws CloneNotSupportedException {
        PcmLineChartDataset clone = (PcmLineChartDataset) super.clone();
        clone.seriesKeys = new java.util.ArrayList(this.seriesKeys);
        clone.seriesList = new ArrayList(this.seriesList.size());
        for (int i = 0; i < this.seriesList.size(); i++) {
            double[][] data = (double[][]) this.seriesList.get(i);
            double[] x = data[0];
            double[] y = data[1];
            double[] xx = new double[x.length];
            double[] yy = new double[y.length];
            System.arraycopy(x, 0, xx, 0, x.length);
            System.arraycopy(y, 0, yy, 0, y.length);
            clone.seriesList.add(i, new double[][] {xx, yy});
        }
        return clone;
    }
    /**
	 * Creates a dataset. Also makes the intervals for the plot
	 *
	 * @param Values		the values of the measurements
	 * @param nameOfObs		the name of the measurements
	 * @param xLabel		the label of the x-axis of the plot
	 * @param yLabel		the label of the y-axis of the plot
	 * @param legend		the legend of the plot
	 * @param plotLabel		the name of the label
	 * @param I				the statusarray of the plot
	 */
    
    public PcmLineChartDataset(double [][]values, String [] nameOfObs, String xLabel, String yLabel, String legend, String plotLabel, ArrayList<Integer> rI) {
        this.seriesKeys = new java.util.ArrayList();
        this.seriesList = new java.util.ArrayList();    
        this.addSeries(legend, values);
        this.nameOfObs = nameOfObs;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.plotLabel = plotLabel;
        this.statusArray = rI;
        double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		dataPoints = 1;
		
		numberMeasurement = nameOfObs.length;
				
	    for (int i=0; i<nameOfObs.length; i++) {
	        if (values[0][i] > maxX) {
	        	maxX = values[0][i];   // new maximum
	        }
	    }
	    
	    for (int i=0; i<nameOfObs.length; i++) {
	        if (values[0][i] < minX) {
	        	minX = values[0][i];   // new maximum
	        }
	    }
	    
	    for (int i=0; i<nameOfObs.length; i++) {
	        if (values[1][i] > maxY) {
	        	maxY = values[1][i];   // new maximum
	        }
	    }
	    
	    for (int i=0; i<nameOfObs.length; i++) {
	        if (values[1][i] < minY) {
	        	minY = values[1][i];   // new maximum
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
	 * Returns the label of x-axis
	 *
	 * @return the label of x-axis
	 */
    public String getXLabel(){
    	return this.xLabel;
    }
    /**
	 * Returns the label of y-axis
	 *
	 * @return the label of y-axis
	 */
    public String getYLabel(){
    	return this.yLabel;
    }
    /**
	 * Returns the name of the measurement
	 * @param item  the index (zero-based) of the required item.
	
	 * @return the name of the measurement
	 */
    public String getName(int item){
    	return this.nameOfObs[item];
    }
    /**
	 * Returns the label of the plot
	 *
	 * @return the label of the plot
	 */	
    public String getPlotLabel(){
    	return this.plotLabel;
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
