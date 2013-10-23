/* ***************************************************************************
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

import org.apache.log4j.Logger;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
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
			String xLabel, String yLabel, String title, ChartDescriptor descriptor )
	{
		setupData(xValues, yValues, xLabel, yLabel, title);

		PcmLineChartDataset dataset = new PcmLineChartDataset(values, nameOfObs, xLabel, yLabel, "", title, null);
		chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, dataset, PlotOrientation.VERTICAL, true, true , false);
		
//		XYPlot plot = chart.getXYPlot();
//		XYItemRenderer r = plot.getRenderer();
//		if (r instanceof XYLineAndShapeRenderer) {
//		    XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
//		    renderer.setBaseToolTipGenerator( plotToolTipGenerator );
//		    renderer.setBaseItemLabelGenerator( new StandardXYItemLabelGenerator() );
//		    ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
//		    Command command = service.getCommand("net.bioclipse.chart.ui.pointLabelsButton");
//		    State state = command.getState("org.eclipse.ui.commands.toggleState");
//		    if (state!=null)
//		        renderer.setBaseItemLabelsVisible( (Boolean) state.getValue() ); 
//		}
		
		chartManager.put(chart, descriptor);
		ChartViewMouseListener l = new ChartViewMouseListener( view, descriptor );
		
		view.display( chart, l );
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
//	public static void scatterPlot(double[] xValues, double[] yValues,
//			String xLabel, String yLabel, String title, ChartDescriptor descriptor)
//	{
//		setupData(xValues, yValues, xLabel, yLabel, title);
//		DefaultXYDataset dataset = new DefaultXYDataset();
//		dataset.addSeries(1, values);
//		chart = ChartFactory.createScatterPlot(title, xLabel, yLabel, dataset, PlotOrientation.VERTICAL, false, false,false);
//	
//		ChartViewMouseListener l = new ChartViewMouseListener( view, descriptor );
//
//		view.display( chart, l );
//		ChartUtils.currentPlotType = ChartConstants.SCATTER_PLOT;
//	}
	
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
			String xLabel, String yLabel, String title, ChartDescriptor descriptor)
	{
		setupData(xValues, yValues, xLabel, yLabel, title);
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries(1, values);
		
		chart = ChartFactory.createScatterPlot(title, xLabel, yLabel, dataset, PlotOrientation.VERTICAL, false, true, false);
        
		chartManager.put(chart, descriptor);
		ChartViewMouseListener l = new ChartViewMouseListener( view, descriptor );
	
		view.display( chart, l );
		
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
	public static void histogram(double[] values, int bins, String xLabel, 
	           String yLabel, String title, IEditorPart dataSource, Point[] originCells)
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
		
		ChartDescriptor descriptor = new ChartDescriptor(dataSource, null, ChartConstants.HISTOGRAM, xLabel, yLabel, originCells);
		XYPlot plot = chart.getXYPlot();
		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYBarRenderer) {
		    XYBarRenderer renderer = (XYBarRenderer) r;
		    renderer.setBaseToolTipGenerator( new XYToolTipGenerator() {

		        public String generateToolTip( XYDataset dataset, int series, int item ) {
		            String tooltip = "";
		            if (dataset instanceof IntervalXYDataset) {
		                IntervalXYDataset barDataset = (IntervalXYDataset) dataset;
		                tooltip = "["+barDataset.getStartXValue( series, item )+", "+barDataset.getEndXValue( series, item )+"]";
		            }
		            
		            return tooltip;
		        }
		    } );
		    renderer.setBaseItemLabelGenerator( new XYItemLabelGenerator() {
                
                public String generateLabel( XYDataset dataset, int series, int item ) {
                    String label = "";
                    if (dataset instanceof IntervalXYDataset) {
                        IntervalXYDataset barDataset = (IntervalXYDataset) dataset;
                        label = "["+barDataset.getStartXValue( series, item )+", "+barDataset.getEndXValue( series, item )+"]";
                    }
                    return label;
                }
            } );
		}
		chartManager.put(chart, descriptor);
		ChartViewMouseListener l = new ChartViewMouseListener( view, descriptor );
		
		view.display( chart, l );
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
		    Logger.getLogger( ChartUtils.class ).error( "Could not create " +
		    		"the chart view" + e.getMessage() );
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
	public static void timeSeries(double[] dataValues, double[] timeValues, 
	                              String xLabel, String yLabel, String title, 
	                              ChartDescriptor descriptor)
	{
		setupData(dataValues, timeValues, xLabel, yLabel, title);
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries(1, values);
		chart = ChartFactory.createTimeSeriesChart(title, xLabel, yLabel, (XYDataset)dataset, false, true, false);
//        XYPlot plot = chart.getXYPlot();
//        XYItemRenderer r = plot.getRenderer();
//        if (r instanceof XYLineAndShapeRenderer) {
//            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
//            renderer.setBaseToolTipGenerator( plotToolTipGenerator );
//            renderer.setBaseItemLabelGenerator( new StandardXYItemLabelGenerator() );
//        }
		chartManager.put(chart, descriptor);
		ChartViewMouseListener l = new ChartViewMouseListener( view, descriptor );
		
		view.display( chart, l );
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
	public static void barPlot(double[][] dataValues, String[] seriesLabels, String[] categoryLabels, String xLabel,
	                           String yLabel, String title, ChartDescriptor descriptor){
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
		chart = ChartFactory.createBarChart(
				title,
				xLabel,
				yLabel,
				dataSet,
				PlotOrientation.VERTICAL,
				true, //Legend
				true, //Tooltips
				false); //URLS
				
		chartManager.put(chart, descriptor);
		try {
            view = (ChartView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(CHART_VIEW_ID);
            ChartViewMouseListener l = new ChartViewMouseListener( view, descriptor );
            view.display(chart, l);
        } catch (PartInitException e) {
            Logger.getLogger( ChartUtils.class ).error( "Could not create " +
                    "the chart view" + e.getMessage() );
        }
		
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
						    try {
						        xValues[i]=Double.parseDouble(data.getValue());
						    } catch (NumberFormatException pe) {
						        xValues[i] = Double.NaN;
						    }
						}
					}
				}
				else if( rangeLabel.equals(e.getCellData().getColName()) ){
				    int rowIndex = data.getRowIndex();
				    for(int i=0;i<indices.length;i++){
				        if( indices[i] == rowIndex){
				            try {
				                yValues[i]=Double.parseDouble(data.getValue());
				            } catch (NumberFormatException pe) {
				                xValues[i] = Double.NaN;
				            }
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
	
	public static Set<JFreeChart> getCharts() {
        return chartManager.keySet();
    }
	
	public static XYToolTipGenerator plotToolTipGenerator = new XYToolTipGenerator() {
        
        public String generateToolTip( XYDataset dataset, int series, int item ) {
            return dataset.getY( series, item ).toString();
        }
    }; 
}
