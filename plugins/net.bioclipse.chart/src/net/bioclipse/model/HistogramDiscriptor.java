/* ***************************************************************************
 * Copyright (c) 2013 Bioclipse Project
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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import net.bioclipse.chart.ChartConstants;
import net.bioclipse.chart.ChartPoint;
import net.bioclipse.chart.IChartDescriptor;

/**
 * Manages data about a histogram. Where it comes from, which indices should be 
 * marked in its source.
 * 
 * @author Klas Jšnsson
 *
 */
public class HistogramDiscriptor implements IChartDescriptor, IAdaptable {

    private IEditorPart source;
    private int[] indices;
    private String xLabel, yLabel;
    private Point[] originCells;
    private String sourceName;
    private IResource resource;
    private double[] xValues, yValues;
    private String chartTitle;
    private int bins;
    private String[] itemLabels;
    
    public HistogramDiscriptor(IEditorPart source, 
                           String xLabel, double[] values, String yLable, int bins, 
                           Point[] originCells, String ChartTitle) {
        this.source = source;
        this.xLabel = xLabel;
        this.xValues = values;
        this.yLabel = yLable;
        this.bins = bins;
        this.originCells = originCells;
        this.chartTitle = ChartTitle;
        if (source != null) {
            IFileEditorInput input = (IFileEditorInput) source.getEditorInput().getAdapter( IFileEditorInput.class );
            if (input != null)
                resource = input.getFile();
            sourceName = source.getTitle();
            
        } else
            sourceName = "Unknown";
        
        this.indices = new int[originCells.length];
        int index = 0;
        for (Point p:originCells)
            this.indices[index++] = p.y;
        
        calculateYvalues();
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

    public double getXValue( int index ) {
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

    public double getYValue( int index ) {
        if (index > 0 && index < yValues.length  )
            return yValues[index];
        else
            return Double.NaN;
    }

    public IEditorPart getSource() {
        return source;
    }

    public ChartConstants.plotTypes getPlotType() {
        return ChartConstants.plotTypes.HISTOGRAM;
    }

    public int[] getSourceIndices() {
        return indices;
    }

    public Point[] getOrigenCells() {
        return originCells;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName( String name ) {
        this.sourceName = name;
    }

    public IResource getResource() throws FileNotFoundException {
        return resource;
    }

    public void setResource( IResource file ) {
        resource = file;
        sourceName = file.getName();
    }

    public int getNumberOfBins() throws IllegalAccessError {
        return bins;
    }

    public void setItemLabels( String[] labels )
                                             throws IllegalArgumentException {
        if (labels.length == xValues.length)
            this.itemLabels = labels;
        else
            throw new IllegalArgumentException( "The chart has to have as " +
                    "many lables as it has points." );
    }

    public String getItemLabel( int index ) throws IllegalAccessError {
        if (hasItemLabels() && (index > 0 || index < itemLabels.length))
            return itemLabels[index];
        
        throw new IllegalAccessError( "Cant find the item label." );
    }

    public void setItemLabel( int index, String label )
                                                   throws IllegalAccessError {

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

    private void calculateYvalues() {
        this.yValues = new double[bins];
        double max = Double.NEGATIVE_INFINITY, min = Double.POSITIVE_INFINITY;
        double range;
        
        for (int i = 0;i<xValues.length;i++) {
            min = xValues[i] < min ? xValues[i] : min;
            max = xValues[i] > max ? xValues[i] : max;
        }
        if (min < 0 && max > 0) {
            range = (max + Math.abs( min ))/bins;
        } else {
            range = (Math.abs( max )-Math.abs( min ))/bins;
        }
        
        double binMax = min+range;
        //To get the lowest value in to the first bin, we start just below it...
        double binMin=min-1;
        for (int i=0;i<bins;i++) {
            
            for (double value:xValues)
                if (value>binMin && value<=binMax)
                    yValues[i]++;
            binMin = binMax;
            binMax += range;  
        }
    }

    public Object getAdapter( Class adapter ) {
        if (adapter == ChartDescriptor.class)
            return new ChartDescriptor( source,
                                        ChartConstants.plotTypes.SCATTER_PLOT, 
                                        xLabel, xValues, yLabel, yValues, 
                                        originCells, chartTitle );
        return null;
    }
    
    public List<ChartPoint> handleEvent( ISelection selection ) {
        return new ArrayList<ChartPoint>();
    }

    @Override
    public <T> T getAdapter( int index, Class<T> clazz ) {
        return null;
    }

    @Override
    public boolean hasToolTips() {
        return hasItemLabels();
    }

    @Override
    public String getToolTip( int index ) {
        return getItemLabel( index );
    }
}
