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

import java.awt.IllegalComponentStateException;
import java.util.ArrayList;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.model.ChartConstants;
import net.bioclipse.model.ChartDescriptor;
import net.bioclipse.chart.*;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import net.bioclipse.statistics.model.*;

public class UiManager implements IBioclipseManager {

    private static final Logger logger = Logger.getLogger(UiManager.class);

    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "chart";
    }
    
    public String say() {
        return "Hi!";
    }
   
    public void plot() {
        double[] x = {1, 4, 6, 8};
        double[] y = {12, 5, 1, 7};
        this.linePlot( x, y, "x-Label", "y-Label", "My title" );
    }
   
    /*
     *  Start of line plot methods
     */
    
    public void linePlot(IMatrixResource matrix) {
     // TODO Should it only plot the two first columns?
        if (matrix.getColumnCount() < 2)
            throw new IllegalComponentStateException("The matrix have to have at least two columns.");
        linePlot( matrix, 1, 2 );
    }
    

    public void linePlot(IMatrixResource matrix, int column1, int column2) {
        this.plot( matrix, column1, column2, "Line Plot", ChartConstants.LINE_PLOT );
    }
    
    public void linePlot(double[] xValues, double[] yValues) {
        this.plot( xValues, yValues, "The x-values", "The y-values", "Line Plot", ChartConstants.LINE_PLOT );
    }
    
    public void linePlot(ArrayList<Object> xValues, ArrayList<Object> yValues) 
            throws IllegalArgumentException {
        this.linePlot( xValues, yValues, "The x-values", "The y-values", "Line Plot" );
    }
    
    public void linePlot(ArrayList<Object> xValues, ArrayList<Object> yValues, 
                         String xLabel, String yLabel, String title) 
                                 throws IllegalArgumentException {
        double[] x = new double[xValues.size()];
        double[] y = new double[yValues.size()];
        
        int i = 0;
        for (Object obj : xValues) {
            if (obj instanceof Number )
                x[i] = (Double) obj;
            else
                throw new IllegalArgumentException( "You can only plot numbers!" );
            i++;
        }
        
        i = 0;
        for (Object obj : yValues) {
            if (obj instanceof Number )
                y[i] = (Double) obj;
            else
                throw new IllegalArgumentException( "You can only plot numbers!" );
            i++;
        }
        
        this.linePlot( x, y, xLabel, yLabel, title );
    }
    
    public void linePlot(double[] xValues, double[] yValues, String xLabel, String yLabel, String title) {
        this.plot( xValues, yValues, xLabel, yLabel, title, ChartConstants.LINE_PLOT );
    }
    
    /*
     *  Start of scatter plot methods
     */
    
    public void scatterPlot(IMatrixResource matrix) {
        // TODO Should it only plot the two first columns?
        if (matrix.getColumnCount() < 2)
            throw new IllegalComponentStateException("The matrix have to have at least two columns.");
        this.scatterPlot( matrix, 1, 2 );
    }
    
    public void scatterPlot(IMatrixResource matrix, int column1, int column2) {
        this.plot( matrix, column1, column2, "Scatter plot", ChartConstants.SCATTER_PLOT );
    }
    
    public void scatterPlot(double[] xValues, double[] yValues) {
        this.plot( xValues, yValues, "X-axis", "Y-axis", "Scatter plot", ChartConstants.SCATTER_PLOT );
    }
    
    public void scatterPlot(double[] xValues, double[] yValues, String xLabel, 
                           String yLabel, String title) {
        this.plot( xValues, yValues, xLabel, yLabel, title, ChartConstants.SCATTER_PLOT );
    }
    
    /* 
     * Start of the time series methods label
     */
    
    public void timeSeries(IMatrixResource matrix) {
        // TODO Should it only plot the two first columns?
        if (matrix.getColumnCount() < 2)
            throw new IllegalComponentStateException("The matrix have to have at least two columns.");
        this.timeSeries( matrix, 1, 2 );
    }
    
    public void timeSeries(IMatrixResource matrix, int column1, int column2) {
        this.plot( matrix, column1, column2, "Time series", ChartConstants.TIME_SERIES );
    }
    
    public void timeSeries(double[] xValues, double[] yValues) {
        this.plot( xValues, yValues, "X-axis", "Y-axis", "Time series", ChartConstants.TIME_SERIES );
    }
    
    public void timeSeries(double[] xValues, double[] yValues, String xLabel, 
                           String yLabel, String title) {
        this.plot( xValues, yValues, xLabel, yLabel, title, ChartConstants.TIME_SERIES );
    }
    
    /*
     * Start of the histogram methods
     */
    
    public void histogram(double[] values, int bins) {
        this.plotHistogram( values, bins, "", "", "Histogram" );
    }
    
    public void histogram(double[] values, int bins, String title) {
        this.plotHistogram( values, bins, "", "", title );
    }
    
    public void histogram(double[] values, int bins, String xLabel, String yLabel, String title ) {
        this.plotHistogram( values, bins, xLabel, yLabel, title );
    }
    
    /*
     * A test with the bar-plot function 
     */
    public void barPlot(IMatrixResource matrix, String [] seriesLableString,
                        String[] categoryLabels, String xLabel,
                        String yLabel, String title) {
       this.plotBarDiagram( matrix, seriesLableString, categoryLabels, xLabel, yLabel, title );
       
   }
    
    public void barPlot(IMatrixResource matrix, String xLabel, String yLabel,
                        String title) {
        this.plotBarDiagram( matrix, new String[0], new String[0], xLabel, yLabel, title );
        
    }
    
    public void barPlot(IMatrixResource matrix) {
       this.plotBarDiagram( matrix, new String[0], new String[0], "Columns", "Value", "Bar plot" );
       
   }
    
    /* 
     * Start of the internal plot methods
     */
    
    /**
     * Create a diagram from two columns of a matrix.
     * 
     * @param matrix The matrix with the data
     * @param column1 The column that contains the x-values
     * @param column2 The column that contains the y-values
     * @param title The title of the diagram
     * @param type The plot type (e.g <code>ChartConstants.LINE_PLOT</code> )
     */
    private void plot(IMatrixResource matrix, int column1, int column2, String title, int type) {
        int rows = matrix.getRowCount();
        double[] xValues = new double[rows];
        double[] yValues = new double[rows];
        String value; 
        for (int i =0; i < rows; i++) {
            value = matrix.get( i+1, column1 );
            try {
                xValues[i] = Double.parseDouble( value );
            } catch (NumberFormatException e) {
                logger.debug( e.getMessage() );
                System.out.println("Not a number at ("+i+", "+column1+"): "+value);
                xValues[i] = 0;
            }
            value = matrix.get( i+1, column2 );
            try {
                yValues[i] = Double.parseDouble( value );
            } catch (NumberFormatException e) {
                logger.debug( e.getMessage() );
                System.out.println("Not a numberat ("+i+", "+column1+"): "+value);
                yValues[i] = 0;
            }
        }
        if (matrix.hasColHeader())
            plot( xValues, yValues, matrix.getColumnName( column1 ),
                      matrix.getColumnName( column2 ), title, type );
        else
            plot( xValues, yValues, "X axis", "Y axis", title, type );
    }
    
    /**
     * An internal method that does the actual plotting for line plot, scatter
     * plot and time series.
     * @param xValues The X-values of the plot (or data values in time series)
     * @param yValues The Y-values of the plot (or time values in time series)
     * @param xLabel The X-label
     * @param yLabel The Y-label
     * @param title The title of the plot
     * @param type The plot type (e.g <code>ChartConstants.LINE_PLOT</code> )
     */
    private void plot(double[] xValues, double[] yValues, String xLabel, String yLabel, String title, int type) {
        final double[] x = xValues;
        final double[] y = yValues;
        final String x_label = xLabel;
        final String y_label = yLabel;
        final String finalTitle = title;
        final int chartType = type;
        int[] indexes = new int[x.length];
        for (int i = 0;i<x.length;i++)
            indexes[i]= i+1;
        final ChartDescriptor descriptor = new ChartDescriptor( null, indexes, type, x_label, y_label, null );
        descriptor.setSourceName( "JavaScript" );
        
        Display.getDefault().asyncExec(new Runnable() {
            
            @Override
            public void run() {
                switch (chartType) {
                    case ChartConstants.LINE_PLOT:
                        ChartUtils.linePlot( x, y, x_label, y_label, finalTitle,
                                             descriptor );
                        break;
                        
                    case ChartConstants.SCATTER_PLOT:
                        ChartUtils.scatterPlot( x, y, x_label, y_label, 
                                                finalTitle, descriptor );
                        break;
                    case ChartConstants.TIME_SERIES:
                        ChartUtils.timeSeries( x, y, x_label, x_label,
                                               finalTitle, descriptor );
                        break;
                }
            }
            
        });
    }
    
    
    private void plotHistogram(final double[] values, final int bins, 
                              final String xLabel, final String yLabel, 
                              final String title) {
        
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                ChartUtils.histogram( values, bins, xLabel, yLabel, title, null,
                                      null );
            }
            
        });
    }
      
    private void plotBarDiagram(IMatrixResource matrix, final String[] seriesLabels, 
                                final String[] categoryLabels, final String xLabel,
                                final String yLabel, final String title) {
        
        final double[][] dataValues = new double[matrix.getRowCount()][matrix.getColumnCount()];
        for (int r=0;r<matrix.getRowCount();r++) {
            for (int c=0;c<matrix.getColumnCount();c++) {
                try {         
                    dataValues[r][c] = Double.parseDouble( matrix.get( r+1, c+1 ));
                } catch (NumberFormatException e) {
                    dataValues[c][r] = Double.NaN;
                    logger.error( e );
                }
            }
        }
        
        final String[] series, categories;
        // Series = rows
        if (seriesLabels.length == 0 || seriesLabels == null) {      
            series = new String[matrix.getRowCount()];
            for (int i = 0;i<matrix.getRowCount();i++) {
                if (matrix.hasRowHeader())
                    series[i] = matrix.getRowName( i+1 );
                else
                    series[i]="Row" + i;
            }
        } else 
            series = seriesLabels;
        // category = columns
        if (categoryLabels.length == 0 || categoryLabels == null) {
            categories = new String[matrix.getColumnCount()];
            for (int i = 0;i<matrix.getColumnCount();i++) {
                if (matrix.hasColHeader())
                    categories[i] = matrix.getColumnName( i+1 );
                else
                    categories[i]="Column "+i;
            }
        } else
            categories = categoryLabels;
        final ChartDescriptor descriptor = new ChartDescriptor( null, null, ChartConstants.BAR_PLOT, xLabel, yLabel, null );
        descriptor.setSourceName( "JavaScript" );
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                ChartUtils.barPlot( dataValues, series, categories, xLabel, yLabel, title, descriptor );
            }
            
        });
    }
    
}
