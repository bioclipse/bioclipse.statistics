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
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.chart.*;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

public class ChartManager implements IBioclipseManager {

    private static final Logger logger = Logger.getLogger(ChartManager.class);

    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "chart";
    }
   
    /*
     *  Start of line plot methods
     */
    
    public void linePlot(double[] xValues, double[] yValues) {
        this.linePlot( xValues, yValues, "X-axis", "Y-axis", "Line Plot" );
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
        Point[] origin = new Point[0];
        IChartDescriptor descriptor = ChartDescriptorFactory.
                linePlotDescriptor( null, 
                                    xLabel, 
                                    xValues, 
                                    yLabel, 
                                    yValues, 
                                    origin, 
                                    title ); 

        this.plot( descriptor );
    }
    public void linePlot(double[] xValues, double[] yValues, String xLabel, String yLabel, String title, String[] labelItems) {
        Point[] origin = new Point[0];
        IChartDescriptor descriptor = ChartDescriptorFactory.
                linePlotDescriptor( null, 
                                    xLabel, 
                                    xValues, 
                                    yLabel, 
                                    yValues, 
                                    origin, 
                                    title );  

        descriptor.setItemLabels( labelItems );
        this.plot( descriptor );
    }
    
    /*
     *  Start of scatter plot methods
     */
       
    public void scatterPlot(double[] xValues, double[] yValues) {
        this.scatterPlot( xValues, yValues, "X-axis", "Y-axis", "Scatter plot", null );
    }
    
    public void scatterPlot(double[] xValues, double[] yValues, String xLabel, 
                           String yLabel, String title) {
        this.scatterPlot( xValues, yValues, xLabel, yLabel, title, null );
    }
    
    public void scatterPlot(double[] xValues, double[] yValues, String xLabel, 
                            String yLabel, String title, String[] labelItems) {
         Point[] origin = new Point[0];
         IChartDescriptor descriptor = ChartDescriptorFactory.
                 scatterPlotDescriptor( null, 
                                        xLabel, 
                                        xValues, 
                                        yLabel, 
                                        yValues, 
                                        origin, 
                                        title );

         descriptor.setItemLabels( labelItems );
         this.plot( descriptor );
     }
    
    /* 
     * Start of the time series methods label
     */
        
    public void timeSeries(double[] xValues, double[] yValues) {
        this.timeSeries( xValues, yValues, "X-axis", "Y-axis", "Time series");
    }
    
    public void timeSeries(double[] xValues, double[] yValues, String xLabel, 
                           String yLabel, String title) {
        Point[] origin = new Point[0];
        IChartDescriptor descriptor = ChartDescriptorFactory.
                timeSeriesDescriptor( null, xLabel, xValues, yLabel, yValues, origin, title );

        this.plot( descriptor );
    }
    
    public void timeSeries(double[] xValues, double[] yValues, String xLabel, 
                           String yLabel, String title, String[] itemLabels) {
        Point[] origin = new Point[0];
        IChartDescriptor descriptor = ChartDescriptorFactory.timeSeriesDescriptor( null, xLabel, xValues, yLabel, yValues, origin, title ); 

        descriptor.setItemLabels( itemLabels );
        this.plot( descriptor );
    }
    
    /*
     * Start of the histogram methods
     */
    
    public void histogram(double[] values, int bins) {
        this.histogram( values, bins, "Values", "", "Histogram", null );
    }
    
    public void histogram(double[] values, int bins, String title) {
        this.histogram( values, bins, "Values", "", title , null);
    }
    
    public void histogram(double[] values, int bins, String label ,String title, String[] itemLabels) {
        this.histogram( values, bins, label, "", title, itemLabels );
    }
    
    public void histogram(double[] values, int bins, String xLabel, String yLabel, String title, String[] itemLabels ) {
        Point[] origin = new Point[0];
        IChartDescriptor descriptor = ChartDescriptorFactory.histogramDescriptor( null, xLabel, values, yLabel, bins, origin, title ); 

        descriptor.setItemLabels( itemLabels );
        this.plot( descriptor );
    }
        
    /* 
     * Start of the internal plot methods
     */
      
    /**
     * An method that does the actual plotting for line plot, scatter
     * plot and time series. Also used when plotting from the molTableEditor.
     * 
     * @param ChartDescriptor The description of the chart to be plotted
     */
    public void plot(final IChartDescriptor chartDescriptor) {
        
        Display.getDefault().asyncExec(new Runnable() {
            
            public void run() {
                switch (chartDescriptor.getPlotType()) {
                    case LINE_PLOT:
                        ChartUtils.linePlot( chartDescriptor );
                        break;
                        
                    case SCATTER_PLOT:
                        ChartUtils.scatterPlot( chartDescriptor );
                        break;
                    case TIME_SERIES:
                        ChartUtils.timeSeries( chartDescriptor );
                        break;
                    case HISTOGRAM:
                        ChartUtils.histogram( chartDescriptor );
                        break;
                    default:
                        throw new IllegalArgumentException( "The plot type "+chartDescriptor.getPlotType().name()+" is not fully implemented yet." );
                        
                }
            }
            
        });
    }
         
    public IChartDescriptor getCharDescriptorOfActiveChart() {
        return ChartUtils.getChartDescriptor( ChartUtils.getActiveChart() );
    }
}
