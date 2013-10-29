/* ***************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.model;

import java.io.FileNotFoundException;

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

/**
 * Manages data about a chart. Where it comes from, which indices should be marked in its source
 * @author Eskil Andersen, Klas Jšnsson
 *
 */
public class ChartDescriptor {
	private IEditorPart source;
	private int[] indices;
	private int plotType;
	private String xLabel, yLabel;
	private Point[] originCells;
	private String sourceName;
	private IResource resource;
	private double[] xValues, yValues;
	private String chartTitle;
	private int bins = 0;
	
	public ChartDescriptor(IEditorPart source, int[] indices, int plotType,
			String xLabel, double[] xValues, String yLabel, double[] yValues, 
			Point[] originCells, String ChartTitle) {
		super();
		this.source = source;
		this.indices = indices;
		this.plotType = plotType;
		this.xLabel = xLabel;
		this.xValues = xValues;
		this.yLabel = yLabel;
		this.yValues = yValues;
		this.originCells = originCells;
		if (source != null) {
		    IFileEditorInput input = (IFileEditorInput) source.getEditorInput().getAdapter( IFileEditorInput.class );
		    if (input != null)
		        resource = input.getFile();
		    sourceName = source.getTitle();
		    
		} else
		    sourceName = "Unknown";
		this.chartTitle = ChartTitle;
	}

	/**
	 * For histograms.
	 * @param source
	 * @param indices
	 * @param xLabel
	 * @param xValues
	 * @param yLabel
	 * @param bins
	 * @param originCells
	 * @param ChartTitle
	 */
	public ChartDescriptor(IEditorPart source, int[] indices, 
	                       String xLabel, double[] values, String yLable, int bins, 
	                       Point[] originCells, String ChartTitle) {
	    this( source, indices, ChartConstants.HISTOGRAM, xLabel, values, yLable, new double[0], originCells, ChartTitle );
	    this.bins = bins;
	}
	
	public String getTitle() {
	    return chartTitle;
	}
	
	public String getXLabel() {
		return xLabel;
	}

	public double[] getXValues() {
	    return xValues;
	}
	
	/**
	 * Get a specific x-value. Returns <code>Double.NaN</code> if the index 
	 * don't have a correspondent x-value. 
	 * 
	 * @param index Index of the wanted x-value
	 * @return The wanted x-value or Double.NaN
	 */
	public double getXValue(int index) {
	    if (index > 0 && index < xValues.length)
	        return xValues[index];
	    else
	        return Double.NaN;
	}
	
	public String getYLabel() {
		return yLabel;
	}

	public double[] getYValues() {
        return yValues;
    }
    
    /**
     * Get a specific y-value. Returns <code>Double.NaN</code> if the index 
     * don't have a correspondent y-value or called when the plot is a 
     * histogram.
     * 
     * @param index Index of the wanted y-value
     * @return The wanted y-value or Double.NaN
     */
    public double getYValue(int index) {
        if (index > 0 && index < yValues.length && plotType != ChartConstants.HISTOGRAM )
            return yValues[index];
        else
            return Double.NaN;
    }
	
	public IEditorPart getSource() {
		return source;
	}
	
	public int getPlotType() {
		return plotType;
	}

	/**
	 * 
	 * @return The indices of the columns from where the data was collected (i.e. MatrixEditor)
	 */
	public int[] getSourceIndices() {
		return indices;
	}
	
	/**
	 * Returns the coordinates of the cells from where the data where collected.
	 * 
	 * @return An array with the cells coordinates
	 */
	public Point[] getOrigenCells() {
	    return originCells;
	}
	
	public String getSourceName() {
	    return sourceName;
	}
	
	public void setSourceName(String name) {
	    sourceName = name;
	}

	/**
	 * This method returns the resource (e.g. file) associated with the chart, 
	 * of this chart descriptor, if no resource is associated with it it throws
	 * an error.
	 *   
	 * @return The resource that are associated with this chart descriptor
	 * @throws FileNotFoundException If no resource is associated with this
	 *     chart
	 */
	public IResource getResource() throws FileNotFoundException {
	    if (resource == null)
	        throw new FileNotFoundException( "There's no known resource for " +
	        		"this chart" );
	    
	    return resource;
	}
	
	/**
	 * Associates an resource (e.g. a file) with the chart of this chart 
	 * descriptor.
	 * 
	 * @param file The resource to be associated with this chart descriptor
	 */
	public void setResource(IResource file) {
	    this.resource = file;
	}
	
	public int getNumberOfBins() {
	    if (plotType == ChartConstants.HISTOGRAM)
	        return bins;   
	    else 
	        throw new IllegalAccessError( "This is only for the histograms" );
	}
}
