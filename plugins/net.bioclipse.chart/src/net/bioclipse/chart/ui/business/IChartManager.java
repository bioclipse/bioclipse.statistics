/*******************************************************************************
 * Copyright (c) 2012  Klas Jšnsson <klas.joensson@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.chart.ui.business;

import java.util.ArrayList;

import net.bioclipse.chart.IChartDescriptor;
import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.managers.business.IBioclipseManager;

@PublishedClass(
    value="A manager for creating charts from different datasets."
)
public interface IChartManager extends IBioclipseManager {
           
    @Recorded
    @PublishedMethod(methodSummary = "Plot a line plot that plots the x-values as a function of the y-values.",
            params="double[] xValues, double[] yValues")
    public void linePlot(double[] xValues, double[] yValues);
    
    @Recorded
    @PublishedMethod(methodSummary = "Plot a line plot that plots the x-values as a function of the y-values.",
            params="ArrayList<Object> xValues, ArrayList<Object> yValues")
    public void linePlot(ArrayList<Object> xValues, ArrayList<Object> yValues) 
            throws IllegalArgumentException;
    
    @Recorded
    @PublishedMethod(methodSummary = "Plot a line plot that plots the x-values as a function of the y-values and let the user set the labels and title.",
            params="ArrayList<Object> xValues, ArrayList<Object> yValues, String xLabel, String yLabel, String title")
    public void linePlot(ArrayList<Object> xValues, ArrayList<Object> yValues, 
                         String xLabel, String yLabel, String title) 
                                 throws IllegalArgumentException;
    @Recorded
    @PublishedMethod(methodSummary = "Plot a line plot that plots the x-values as a function of the y-values and let the user set the labels and title.",
                params="double[] xValues, double[] yValues, String xLabel, String yLabel, String title")
    public void linePlot(double[] xValues, double[] yValues, String xLabel, 
                         String yLabel, String title);
    
    @Recorded
    @PublishedMethod(methodSummary = "Makes a time series that plots the x-values as a function of the y-values.",
            params="double[] xValues, double[] yValues")
    public void timeSeries(double[] xValues, double[] yValues);
    
    @Recorded
    @PublishedMethod(methodSummary = "Makes a time series that plots the x-values as a function of the y-values and let the user set the labels and title.",
                params="double[] xValues, double[] yValues, String xLabel, String yLabel, String title")
    public void timeSeries(double[] xValues, double[] yValues, String xLabel, 
                           String yLabel, String title);
    @Recorded
    @PublishedMethod(methodSummary = "Creates a histogram.",
            params="double[] values, int bins")
    public void histogram(double[] values, int bins);
    
    @Recorded
    @PublishedMethod(methodSummary = "Creates a histogram with a title",
            params="double[] values, int bins, String title")
    public void histogram(double[] values, int bins, String title);
    
    @Recorded
    @PublishedMethod(methodSummary = "Creates a histogram with a title and labels.",
            params="double[] values, int bins")
    public void histogram(double[] values, int bins, String xLabel, String yLabel, String title );
        
    @Recorded
    @PublishedMethod(methodSummary = "Returns the chart descriptor of the active chart" )
    public IChartDescriptor getCharDescriptorOfActiveChart();
    
    public void plot(final IChartDescriptor chartDescriptor);
}
