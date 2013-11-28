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
import java.util.ArrayList;
import java.util.List;

import net.bioclipse.chart.ChartConstants;
import net.bioclipse.chart.ChartDescriptorFactory;
import net.bioclipse.chart.ChartPoint;
import net.bioclipse.chart.IChartDescriptor;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

/**
 * Manages data about a chart. Where it comes from, which indices should be marked in its source
 * @author Eskil Andersen, Klas Jšnsson
 *
 */
public class ChartDescriptor implements IChartDescriptor, IAdaptable {
	private IEditorPart source;
	private int[] indices;
	private ChartConstants.plotTypes plotType;
	private String xLabel, yLabel;
	private Point[] originCells;
	private String sourceName;
	private IResource resource;
	private double[] xValues, yValues;
	private String chartTitle;
	private String[] itemLabels;
	
	public ChartDescriptor(IEditorPart source, ChartConstants.plotTypes plotType,
			String xLabel, double[] xValues, String yLabel, double[] yValues, 
			Point[] originCells, String ChartTitle) {
		super();
		this.source = source;
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
		
		this.indices = new int[originCells.length];
		int index = 0;
		for (Point p:originCells)
		    this.indices[index++] = p.y;
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
        if (index > 0 && index < yValues.length && plotType != ChartConstants.plotTypes.HISTOGRAM )
            return yValues[index];
        else
            return Double.NaN;
    }
	
	public IEditorPart getSource() {
		return source;
	}
	
	public ChartConstants.plotTypes getPlotType() {
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
	
	public void setItemLabels(String[] labels) {
	    if (labels.length == xValues.length)
	        this.itemLabels = labels;
	    else
	        throw new IllegalArgumentException( "The chart has to have as " +
	        		"many lables as it has points." );
	}
	
	public String getItemLabel(int index) {
	    if (hasItemLabels() && (index > 0 || index < itemLabels.length))
	        return itemLabels[index];
	    
	    throw new IllegalAccessError( "Cant find the item label." );
	}
	
	public void setItemLabel(int index, String label) {
        if (hasItemLabels() && (index > 0 || index < itemLabels.length))
            itemLabels[index] = label;
        
        throw new IllegalAccessError( "Cant find the item label." );
    }
	
	public String[] getItemLabels() {
	    if (hasItemLabels())
	        return itemLabels;
	    
	    return new String[0];
    }
	
	public boolean hasItemLabels() {
	    return (itemLabels != null);
	}
	
	public void removeItemLabels() {
	    itemLabels = null;
	}

    @Override
    public Object getAdapter( Class adapter ) {
        if (adapter == HistogramDiscriptor.class) {
            
            int size = xValues.length * 2 ;
            double[] values = new double[size];
            for (int i=0;i<xValues.length;i++) {
                values[i] = xValues[i];
                values[i+xValues.length] = yValues[i];
            }
            
            int bins;
            if (size < 6)
                bins = 3;
            else if (size < 20)
                bins = 5;
            else
                bins = 10;
            
            String label = xLabel.isEmpty() ? "" : xLabel;
            label = yLabel.isEmpty() ? "" : " and "+ yLabel;
            
            return ChartDescriptorFactory.histogramDescriptor( source, 
                                                               label, 
                                                               values, 
                                                               "", 
                                                               bins, 
                                                               originCells, 
                                                               chartTitle );
        }
        
        return null;
    }
    
    public List<ChartPoint> handleEvent( ISelection selection ) {
        return new ArrayList<ChartPoint>();
    }
}
