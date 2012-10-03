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

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.statistics.model.IMatrixResource;

@PublishedClass(
    value="A manager for creating charts from different datasets."
)
public interface IUiManager extends IBioclipseManager {
    
    @Recorded
    @PublishedMethod(methodSummary = "Says Hi")
    public String say();
    
    @Recorded
    @PublishedMethod(methodSummary = "Create a plot out of five values for testing")
    public void plot();
    
    @Recorded
    @PublishedMethod(methodSummary = "Makes a line plot from a matrix.",
            params="IMatrixResource matrix")
    public void linePlot(IMatrixResource matrix);
    
    @Recorded
    @PublishedMethod(methodSummary = "Makes a line plot with help of two columns in a matrix.",
            params="IMatrixResource matrix, int column1, int column2")
    public void linePlot(IMatrixResource matrix, int column1, int column2);
    
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
    @PublishedMethod(methodSummary = "Makes a scatter plot from a matrix.",
            params="IMatrixResource matrix")
    public void scatterPlot(IMatrixResource matrix);
    
    @Recorded
    @PublishedMethod(methodSummary = "Makes a Scatter plot with help of two columns in a matrix.",
            params="IMatrixResource matrix, int column1, int column2")
    public void scatterPlot(IMatrixResource matrix, int column1, int column2);
    
    @Recorded
    @PublishedMethod(methodSummary = "Makes a scatter plot that plots the x-values as a function of the y-values.",
            params="double[] xValues, double[] yValues")
    public void scatterPlot(double[] xValues, double[] yValues);
    
    @Recorded
    @PublishedMethod(methodSummary = "Makes a scatter plot that plots the x-values as a function of the y-values and let the user set the labels and title.",
                params="double[] xValues, double[] yValues, String xLabel, String yLabel, String title")
    public void scatterPlot(double[] xValues, double[] yValues, String xLabel, 
                            String yLabel, String title);
    @Recorded
    @PublishedMethod(methodSummary = "Makes a time series from a matrix.",
            params="IMatrixResource matrix")
    public void timeSeries(IMatrixResource matrix);
    
    @Recorded
    @PublishedMethod(methodSummary = "Makes a time series with help of two columns in a matrix.",
            params="IMatrixResource matrix, int column1, int column2")
    public void timeSeries(IMatrixResource matrix, int column1, int column2);
    
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
    @PublishedMethod(methodSummary = "Creates a bar plot with labels and title from a matrix.",
            params="IMatrixResource matrix, String [] seriesLableString, String[] categoryLabels, String xLabel, String yLabel, String title")
    public void barPlot(IMatrixResource matrix, String [] seriesLableString,
                        String[] categoryLabels, String xLabel,
                        String yLabel, String title);
    
    @Recorded
    @PublishedMethod(methodSummary = "Creates a bar plot with labels on the x- and y-axis and title from a matrix, if the matrix has row and/or column labels they are used as series-/categories-labels.",
            params="IMatrixResource matrix, String xLabel, String yLabel, String title")
    public void barPlot(IMatrixResource matrix, String xLabel, String yLabel,
                        String title);
    
    @Recorded
    @PublishedMethod(methodSummary = "Creates a bar plot from a matrix, if the matrix has row and/or column labels they are used as series-/categories-labels.",
            params="IMatrixResource matrix")
    public void barPlot(IMatrixResource matrix);
}
