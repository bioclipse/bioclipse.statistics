package net.bioclipse.chart;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

import net.bioclipse.model.BoxPlotDescriptor;
import net.bioclipse.model.ChartDescriptor;
import net.bioclipse.model.HistogramDiscriptor;


public class ChartDescriptorFactory {
    
    public ChartDescriptorFactory() {   }
    
    /**
     * Creates a <code>IChartDescriptor</code> for a scatter plot. I.e. a class 
     * that contains the information that are needed to create this chart type.
     * 
     * @param source The source of the data, i.e. the editor it came from 
     * @param indices The original rows of the data
     * @param xLabel The label on the x-axis
     * @param xValues The x-values 
     * @param yLabel The label on the y-axis
     * @param yValues The y-values 
     * @param originCells The cells that the data came from
     * @param ChartTitle The title of the chart
     * @return The description of the chart
     */
    public static IChartDescriptor scatterPlotDescriptor(IEditorPart source, 
                                                         String xLabel,
                                                         double[] xValues,
                                                         String yLabel,
                                                         double[] yValues, 
                                                         Point[] originCells,
                                                         String ChartTitle) {

        return new ChartDescriptor( source, ChartConstants.plotTypes.SCATTER_PLOT, 
                                    xLabel, xValues, yLabel, yValues, 
                                    originCells, ChartTitle );   
    }

    /**
     * Creates a <code>IChartDescriptor</code> for a line plot. I.e. a class 
     * that contains the information that are needed to create this chart type.
     * 
     * @param source The source of the data, i.e. the editor it came from 
     * @param indices The original rows of the data
     * @param xLabel The label on the x-axis
     * @param xValues The x-values 
     * @param yLabel The label on the y-axis
     * @param yValues The y-values 
     * @param originCells The cells that the data came from
     * @param ChartTitle The title of the chart
     * @return The description of the chart
     */
    public static IChartDescriptor linePlotDescriptor(IEditorPart source,
                                                      String xLabel, 
                                                      double[] xValues, 
                                                      String yLabel, 
                                                      double[] yValues, 
                                                      Point[] originCells,
                                                      String ChartTitle) {

        return new ChartDescriptor( source, ChartConstants.plotTypes.LINE_PLOT, 
                                    xLabel, xValues, yLabel, yValues, 
                                    originCells, ChartTitle );   
    }

    /**
     * Creates a <code>IChartDescriptor</code> for a time series. I.e. a class 
     * that contains the information that are needed to create this chart type.
     * 
     * @param source The source of the data, i.e. the editor it came from 
     * @param indices The original rows of the data
     * @param xLabel The label on the x-axis
     * @param xValues The x-values 
     * @param yLabel The label on the y-axis
     * @param yValues The y-values 
     * @param originCells The cells that the data came from
     * @param ChartTitle The title of the chart
     * @return The description of the chart
     */
    public static IChartDescriptor timeSeriesDescriptor(IEditorPart source,
                                                        String xLabel, 
                                                        double[] xValues, 
                                                        String yLabel, 
                                                        double[] yValues, 
                                                        Point[] originCells, 
                                                        String ChartTitle) {

        return new ChartDescriptor( source, ChartConstants.plotTypes.TIME_SERIES, 
                                    xLabel, xValues, yLabel, yValues,
                                    originCells, ChartTitle );   
    }
    
    /**
     * Creates a <code>IChartDescriptor</code> for a histogram. I.e. a class 
     * that contains the information that are needed to create this chart type. 
     * 
     *  @param source The source of the data, i.e. the editor it came from 
     * @param indices The original rows of the data
     * @param xLabel The label on the x-axis
     * @param values The values to be sorted in to bins
     * @param yLable The label on the y-axis
     * @param bins The number of bins
     * @param originCells The cells that the data came from
     * @param ChartTitle The title of the chart
     * @return The description of the histogram
     */
    public static IChartDescriptor histogramDescriptor(IEditorPart source,
                                                       String xLabel, 
                                                       double[] values, 
                                                       String yLable, 
                                                       int bins, 
                                                       Point[] originCells, 
                                                       String ChartTitle) {

        return new HistogramDiscriptor( source, xLabel, values, yLable, bins, 
                                        originCells, ChartTitle );
    }
    
    public static IChartDescriptor boxPlotDescriptor(IEditorPart source,
                                                        String[] itemLabels, 
                                                        double[][] values, 
                                                        Point[] originCells, 
                                                        String ChartTitle) {

        return new BoxPlotDescriptor( source, itemLabels, values, originCells, ChartTitle ); 
    }
}
