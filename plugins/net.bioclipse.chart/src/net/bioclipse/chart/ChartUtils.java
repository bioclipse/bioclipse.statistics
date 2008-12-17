/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/
package net.bioclipse.chart;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.bioclipse.chart.events.CellChangeListener;
import net.bioclipse.chart.events.CellChangeProvider;
import net.bioclipse.chart.events.CellChangedEvent;
import net.bioclipse.chart.events.CellData;
import net.bioclipse.chart.events.CellSelection;
import net.bioclipse.model.ChartConstants;
import net.bioclipse.model.ChartDescriptor;
import net.bioclipse.model.ChartManager;
import net.bioclipse.model.ChartModelListener;
import net.bioclipse.model.ChartSelection;
import net.bioclipse.model.PcmLineChartDataset;
import net.bioclipse.plugins.views.ChartView;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
/**
 * This is a utility class with static methods for plotting data on the chart plugins
 * chart view. Charts generated with these methods are displayed in ChartView
 * 
 * This is the controller of the model and view
 * 
 * @see net.bioclipse.plugins.views.ChartView
 * @author EskilA
 *
 */
public class ChartUtils
{
        public static JFreeChart chart;
        private final static String CHART_VIEW_ID ="net.bioclipse.plugins.views.ChartView";
        private static double[][] values;
        private static ChartView view;
        private static String[] nameOfObs;
        static String yColumn;
        static String xColumn;
        static ChartSelection cs;
        private static int currentPlotType = -1;
        static int[] indices;
        private static ChartManager chartManager = new ChartManager();
        private static List<CellChangeProvider> providers = new ArrayList<CellChangeProvider>();
        private static List<CellChangeListener> listeners = new ArrayList<CellChangeListener>();
        /**
         * Displays data in a line plot
         * 
         * @param xValues x values of points
         * @param yValues y values of points
         * @param xLabel X axis label
         * @param yLabel Y axis label
         * @param title Chart title
         * @param analysisMatrixEditor 
         * @param indices 
         */
        public static void linePlot( double[] xValues, double[] yValues,
                        String xLabel, String yLabel, String title, int[] indices, IEditorPart dataSource )
        {
                setupData(xValues, yValues, xLabel, yLabel, title);
                PcmLineChartDataset dataset = new PcmLineChartDataset(values, nameOfObs, xLabel, yLabel, "", title, null);
                chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, dataset, PlotOrientation.VERTICAL, true, true , false);
                ChartDescriptor cd = new ChartDescriptor(dataSource,indices,ChartConstants.LINE_PLOT,xLabel,yLabel);
                chartManager.put(chart, cd);
                view.display( chart );
        }
        public static void updateSelection(ChartSelection cs)
        {
                view.setSelection(cs);
        }
        /** If you want to map points on the plot to cells in a spreadsheet you need to
         *  set the columns from where you got your data
         * 
         * @param xColumn the label of the xColumn in the spreadsheet
         * @param yColumn the label of the yColumn in the spreadsheet
         * 
         * @throws IllegalArgumentException if xColumn or yColumn equals null
         */
        public static void setDataColumns(String xColumn, String yColumn) throws IllegalArgumentException
        {
                if( xColumn == null || yColumn == null)
                        throw new IllegalArgumentException("xColumn or yColumn can not be null");
                ChartUtils.xColumn = xColumn;
                ChartUtils.yColumn = yColumn;
        }
        /**
         * Marks a plotted point
         * @param series
         * @param index
         * @deprecated
         */
        public static void markPoints( CellSelection cs )
        {
                if( chart != null && ChartUtils.currentPlotType == ChartConstants.SCATTER_PLOT )
                {
                        ScatterPlotRenderer renderer = (ScatterPlotRenderer) chart.getXYPlot().getRenderer();
                        Iterator<CellData> iter = cs.iterator();
                        renderer.clearMarkedPoints();
                        while( iter.hasNext() )
                        {
                                CellData cd = iter.next();
                                if( cd.getColName().equals(ChartUtils.xColumn) || cd.getColName().equals(ChartUtils.yColumn) )
                                {	
                                        renderer.addMarkedPoint(0, cd.getRowIndex());
                                        chart.plotChanged(new PlotChangeEvent(chart.getPlot()));
                                }
                        }
                }
        }
        /**
         * Displays data in a scatter plot
         * 
         * @param xValues x values of points
         * @param yValues y values of points
         * @param xLabel X axis label
         * @param yLabel Y axis label
         * @param title plot title
         */
        public static void scatterPlot(double[] xValues, double[] yValues,
                        String xLabel, String yLabel, String title)
        {
                setupData(xValues, yValues, xLabel, yLabel, title);
                DefaultXYDataset dataset = new DefaultXYDataset();
                dataset.addSeries(1, values);
                chart = ChartFactory.createScatterPlot(title, xLabel, yLabel, dataset, PlotOrientation.VERTICAL, false, false,false);
                view.display( chart );
                ChartUtils.currentPlotType = ChartConstants.SCATTER_PLOT;
        }
        /**
         * Displays data in a scatter plot
         * 
         * @param xValues x values of points
         * @param yValues y values of points
         * @param xLabel X axis label
         * @param yLabel Y axis label
         * @param title plot title
         * @param indices 
         * @param dataSource The editor from which the charts data comes from, used so indices are mapped to the right editor 
         */
        public static void scatterPlot(double[] xValues, double[] yValues,
                        String xLabel, String yLabel, String title, int[] indices, IEditorPart dataSource)
        {
                setupData(xValues, yValues, xLabel, yLabel, title);
                DefaultXYDataset dataset = new DefaultXYDataset();
                dataset.addSeries(1, values);
                chart = ChartFactory.createScatterPlot(title, xLabel, yLabel, dataset, PlotOrientation.VERTICAL, false, false,false);
                ChartDescriptor descriptor = new ChartDescriptor(dataSource,indices,ChartConstants.SCATTER_PLOT,xLabel,yLabel);
                chartManager.put(chart, descriptor);
                view.display( chart );
        }
        /**
         * Displays histogram of the values in ChartView
         * 
         * @param values Data values
         * @param bins Number of bins to use
         * @param xLabel X axis label
         * @param yLabel Y axis label
         * @param title Histogram title
         */
        public static void histogram(double[] values, int bins,
                        String xLabel, String yLabel, String title, IEditorPart dataSource)
        {
                setupData(values, null, xLabel, yLabel, title);
                HistogramDataset histogramData = new HistogramDataset();
                histogramData.addSeries(1, values, bins);
                chart = ChartFactory.createHistogram(
                                title,
                                xLabel, 
                                yLabel, 
                                histogramData, 
                                PlotOrientation.VERTICAL, 
                                false, 
                                false, 
                                false
                );
                ChartDescriptor descriptor = new ChartDescriptor(dataSource, null, ChartConstants.HISTOGRAM, xLabel, yLabel);
                chartManager.put(chart, descriptor);
                view.display( chart );
                ChartUtils.currentPlotType = ChartConstants.HISTOGRAM;
        }
        /**
         * Sets up common data
         * @param xValues   
         * @param yValues
         * @param xLabel X axis label
         * @param yLabel Y axis label
         * @param title Chart title
         */
        private static void setupData( double[] xValues, double[] yValues,
                        String xLabel, String yLabel, String title )
        {
                values = new double[2][];
                values[0] = xValues;
                values[1] = yValues;
                view = null;
                nameOfObs = new String[xValues.length];
                for( int i = 0; i < xValues.length; i++)
                {
                        nameOfObs[i] = ""+i;
                }
                try {
                        view = (ChartView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(CHART_VIEW_ID);
                } catch (PartInitException e) {
                        e.printStackTrace();
                }
        }
        /**
         * Plot time series
         * 
         * @param dataValues
         * @param timeValues
         * @param xLabel X axis label
         * @param yLabel Y axis label
         * @param title Chart title
         * @param dataSource 
         * @param indices 
         */
        public static void timeSeries(double[] dataValues, double[] timeValues, String xLabel, String yLabel, String title, int[] indices, IEditorPart dataSource)
        {
                setupData(dataValues, timeValues, xLabel, yLabel, title);
                DefaultXYDataset dataset = new DefaultXYDataset();
                dataset.addSeries(1, values);
                chart = ChartFactory.createTimeSeriesChart(title, xLabel, yLabel, (XYDataset)dataset, false, false, false);
                ChartDescriptor descriptor = new ChartDescriptor(dataSource, indices, ChartConstants.TIME_SERIES, xLabel, yLabel);
                chartManager.put(chart, descriptor);
                view.display( chart );
                ChartUtils.currentPlotType = ChartConstants.TIME_SERIES;
        }
        /**
         * Creates a bar plot and displays it in ChartView
         * 
         * @param dataValues MxN matrix containing series x categories
         * @param seriesLabels text labels for the series
         * @param categoryLabels text labels for the categories
         * @param xLabel Domain label
         * @param yLabel Range label
         * @param title Title of the plot
         */
        public static void barPlot(double[][] dataValues, String[] seriesLabels, String[] categoryLabels, String xLabel, String yLabel, String title){
                DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
                //Load data into the data set
                for( int i = 0; i < dataValues.length; i++)
                {
                        for(int j=0;j<dataValues[i].length;j++){
                                dataSet.addValue(dataValues[i][j], i < seriesLabels.length ? seriesLabels[i] : "N/A",
                                                j < categoryLabels.length ? categoryLabels[j] : "N/A");
                        }
                }
                //Use the chart factory to set up our bar chart
                JFreeChart chart = ChartFactory.createBarChart(
                                title,
                                xLabel,
                                yLabel,
                                dataSet,
                                PlotOrientation.VERTICAL,
                                true, //Legend
                                true, //Tooltips
                                false); //URLS
                view.display(chart);
        }
        /**
         * Utility method for converting JFreeChart to an image
         * @param parent used for color correction 
         * @param chart the chart to be made into an image
         * @param width image width
         * @param height image height
         * @return SWT Image of the chart 
         */
        public static Image createChartImage(Composite parent, JFreeChart chart,
                        int width, int height)
        {
                // Color adjustment
                Color swtBackground = parent.getBackground();
                java.awt.Color awtBackground = new java.awt.Color( swtBackground.getRed(),
                                swtBackground.getGreen(),
                                swtBackground.getRed());
                chart.setBackgroundPaint(awtBackground);
                // Draw the chart in an AWT buffered image
                BufferedImage bufferedImage
                = chart.createBufferedImage(width, height, null);
                // Get the data buffer of the image
                DataBuffer buffer = bufferedImage.getRaster().getDataBuffer();
                DataBufferInt intBuffer = (DataBufferInt) buffer;
                // Copy the data from the AWT buffer to a SWT buffer
                PaletteData paletteData = new PaletteData(0x00FF0000, 0x0000FF00, 0x000000FF);
                ImageData imageData = new ImageData(width, height, 32, paletteData);
                for (int bank = 0; bank < intBuffer.getNumBanks(); bank++) {
                        int[] bankData = intBuffer.getData(bank);
                        imageData.setPixels(0, bank, bankData.length, bankData, 0);     
                }
                // Create an SWT image
                return new Image(parent.getDisplay(), imageData);
        }
        public static void registerCellChangeProvider( CellChangeProvider provider )
        {
                if( !providers.contains(provider))
                {
                        providers.add(provider);
                }
        }
        public static void registerCellChangeListener( CellChangeListener listener )
        {
                if( !listeners.contains(listener) )
                {
                        listeners.add(listener);
                }
        }
        //These methods delegate to the model
        //TODO:  Write general Interface/Abstract class for model so its not hardwired to ChartManager
        public static ChartDescriptor getChartDescriptor(JFreeChart key) {
                return chartManager.get(key);
        }
        public static JFreeChart getActiveChart() {
                return chartManager.getActiveChart();
        }
        public static ChartDescriptor put(JFreeChart chart, ChartDescriptor descriptor) {
                return chartManager.put(chart, descriptor);
        }
        public static void setActiveChart(JFreeChart chart) {
                chartManager.setActiveChart(chart);
        }
        public static void addListener(ChartModelListener listener) {
                chartManager.addListener(listener);
        }
        public static void removeListener(ChartModelListener listener) {
                chartManager.removeListener(listener);
        }
        public static ChartDescriptor remove(Object arg0) {
                return chartManager.remove(arg0);
        }
        public static Collection<ChartDescriptor> values() {
                return chartManager.values();
        }
        public static Set<JFreeChart> keySet() {
                return chartManager.keySet();
        }
        public static void handleCellChangeEvent(CellChangedEvent e) {
//		Iterator<CellChangeListener> iterator = listeners.iterator();
//		while( iterator.hasNext() )
//		{
//			CellChangeListener listener = iterator.next();
//			listener.handleCellChangeEvent(e);
//		}
                Set<JFreeChart> keySet = chartManager.keySet();
                Iterator<JFreeChart> iterator = keySet.iterator();
                while( iterator.hasNext()){
                        JFreeChart chart = iterator.next();
                        ChartDescriptor desc = chartManager.get(chart);
                        String domainLabel = desc.getXLabel();
                        String rangeLabel = desc.getYLabel();
                        CellData data = e.getCellData();
                        if( domainLabel.equals(data.getColName()) || rangeLabel.equals(data.getColName()))
                        {
                                DefaultXYDataset dataset = (DefaultXYDataset) chart.getXYPlot().getDataset();
                                int itemCount = dataset.getItemCount(0);
                                double[] yValues = new double[itemCount];
                                double[] xValues = new double[itemCount];
                                for( int i=0;i<itemCount;i++)
                                {
                                        double x = dataset.getXValue(0, i);
                                        double y = dataset.getYValue(0, i);
                                        xValues[i]=x;
                                        yValues[i]=y;
                                }
                                int indices[] = desc.getSourceIndices();
                                if( domainLabel.equals(e.getCellData().getColName())){
                                        int rowIndex = data.getRowIndex();
                                        for(int i=0;i<indices.length;i++){
                                                if(indices[i] == rowIndex){
                                                        xValues[i]=data.getValue();
                                                }
                                        }
                                }
                                else if( rangeLabel.equals(e.getCellData().getColName()) ){
                                        int rowIndex = data.getRowIndex();
                                        for(int i=0;i<indices.length;i++){
                                                if( indices[i] == rowIndex){
                                                        yValues[i]=data.getValue();
                                                }
                                        }
                                }
                                double[][] chartData = new double[2][];
                                chartData[0] = xValues;
                                chartData[1] = yValues;
                                dataset.getSeriesKey(0);
                                Comparable seriesKey = dataset.getSeriesKey(0);
                                dataset.removeSeries(seriesKey);
                                dataset.addSeries(seriesKey, chartData);
                        }
                }
        }
}