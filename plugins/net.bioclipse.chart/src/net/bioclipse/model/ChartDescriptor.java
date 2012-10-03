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
	private double[] extraData;
	
	public ChartDescriptor(IEditorPart source, int[] indices, int plotType,
			String xLabel, String yLabel, Point[] originCells) {
		super();
		this.source = source;
		this.indices = indices;
		this.plotType = plotType;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.originCells = originCells;
		if (source != null) {
		    IFileEditorInput input = (IFileEditorInput) source.getEditorInput().getAdapter( IFileEditorInput.class );
		    if (input != null)
		        resource = input.getFile();
		    sourceName = source.getTitle();
		    
		} else
		    sourceName = "Unknown";
		
	}

	public String getXLabel() {
		return xLabel;
	}

	public String getYLabel() {
		return yLabel;
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
	
	/**
	 * Make it possibly to send some values that can't be easy obtained 
	 * otherwise. E.g. the masses of the molecules that due to restriction can't
	 * be calculated everywhere.
	 * 
	 * @param values An array with the values
	 */
	public void addExtraData(double[] values) {
	    this.extraData = values;
	}
	
	/**
	 * Gets value <code>index</code> from the array of extra data.
	 * 
	 * @param index The index of the wanted value of the data array
	 * @return The wanted value or <code>Double.NaN</code> if it by some reason 
	 * couldn't determine it    
	 */
	public double getExtraData(int index) {
	    if (extraData == null || index >= extraData.length)
	        return Double.NaN;
	    
	    return extraData[index];
	}
	
}
