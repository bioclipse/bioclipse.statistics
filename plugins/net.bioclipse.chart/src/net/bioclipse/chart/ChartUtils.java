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
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import net.bioclipse.chart.events.CellChangedEvent;
import net.bioclipse.chart.events.CellData;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.model.BoxPlotDescriptor;
import net.bioclipse.model.ChartManager;
import net.bioclipse.model.ChartModelListener;
import net.bioclipse.model.ChartSelection;
import net.bioclipse.model.HistogramDiscriptor;
import net.bioclipse.model.PcmLineChartDataset;
import net.bioclipse.plugins.views.ChartView;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

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
	private static ChartManager chartManager = new ChartManager();
	
	/**
	 * Displays data in a line plot
	 * 
	 * @param ChartDescriptor The description of the plot
	 */
	public static void linePlot( IChartDescriptor descriptor )
	{
		setupData(descriptor);

		PcmLineChartDataset dataset = new PcmLineChartDataset(values, 
		                                                      nameOfObs, 
		                                                      descriptor.getXLabel(), 
		                                                      descriptor.getYLabel(), 
		                                                      "", 
		                                                      descriptor.getTitle(), 
		                                                      null);
		chart = ChartFactory.createXYLineChart( descriptor.getTitle(), 
		                                        descriptor.getXLabel(), 
		                                        descriptor.getYLabel(), 
		                                        dataset, 
		                                        PlotOrientation.VERTICAL, 
		                                        true, 
		                                        true , 
		                                        false);
		
		chartManager.put(chart, descriptor);
		ChartViewMouseListener l = new ChartViewMouseListener( view, descriptor );
		
		view.display( chart, l );
	}
	
	public static void updateSelection(ChartSelection cs)
	{
		view.setSelection(cs);
	}
	
	/**
	 * Displays data in a scatter plot
	 * 
	 * @param ChartDescriptor The description of the plot
	 */
	public static void scatterPlot( IChartDescriptor descriptor )
	{
		setupData(descriptor);
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries(1, values);
		
		chart = ChartFactory.createScatterPlot(descriptor.getTitle(), 
		                                       descriptor.getXLabel(), 
		                                       descriptor.getYLabel(),
		                                       dataset,
		                                       PlotOrientation.VERTICAL,
		                                       false,
		                                       true,
		                                       false);
        
		chartManager.put(chart, descriptor);
		ChartViewMouseListener l = new ChartViewMouseListener( view, descriptor );
	
		view.display( chart, l );
		
	}

	/**
	 * Displays histogram of the values in ChartView
	 * 
	 * @param ChartDescriptor The description of the plot
	 */
	public static void histogram( IChartDescriptor descriptor ) throws IllegalArgumentException
	{
	    final HistogramDiscriptor desc;
	    if (descriptor instanceof HistogramDiscriptor) {
	        desc = (HistogramDiscriptor) descriptor;

	        HistogramDataset histogramData = new HistogramDataset();
	        histogramData.addSeries(1, descriptor.getXValues(), desc.getNumberOfBins());
	        chart = ChartFactory.createHistogram(
	                                             descriptor.getTitle(),
	                                             descriptor.getXLabel(), 
	                                             descriptor.getYLabel(), 
	                                             histogramData, 
	                                             PlotOrientation.VERTICAL, 
	                                             false, 
	                                             false, 
	                                             false
	                );

	        setupData( descriptor );

	        XYPlot plot = chart.getXYPlot();
	        XYItemRenderer r = plot.getRenderer();
	        if (r instanceof XYBarRenderer) {
	            XYBarRenderer renderer = (XYBarRenderer) r;
	            renderer.setBaseToolTipGenerator( new XYToolTipGenerator() {

	                public String generateToolTip( XYDataset dataset, int series, int item ) {
	                    String tooltip = "";
	                    if (dataset instanceof IntervalXYDataset) {
	                        if (desc.hasItemLabels())
	                            tooltip = desc.getItemLabel( item );
	                        else {
	                        IntervalXYDataset barDataset = (IntervalXYDataset) dataset;
	                        tooltip = "["+barDataset.getStartXValue( series, item )+", "+barDataset.getEndXValue( series, item )+"]";
	                        }
	                    }

	                    return tooltip;
	                }
	            } );
	            renderer.setBaseItemLabelGenerator( new XYItemLabelGenerator() {

	                public String generateLabel( XYDataset dataset, int series, int item ) {
	                    String label = "";
	                    if (dataset instanceof IntervalXYDataset) {
	                        if (desc.hasItemLabels())
	                            label = desc.getItemLabel( item );
	                        else {
	                        IntervalXYDataset barDataset = (IntervalXYDataset) dataset;
	                        label = "["+barDataset.getStartXValue( series, item )+", "+barDataset.getEndXValue( series, item )+"]";
	                        }
	                    }
	                    
	                    return label;
	                }
	            } );
	        }
	        chartManager.put(chart, descriptor);
	        ChartViewMouseListener l = new ChartViewMouseListener( view, descriptor );

	        view.display( chart, l );

	    } else
	        throw new IllegalArgumentException("The chart descriptor has to " +
	        		"be a HistogramDescriptor when plotting a histogram.");
	}

	/**
	 * Sets up common data
	 * 
	 * @param ChartDescriptor The description of the plot
	 */
	private static void setupData( IChartDescriptor descriptor )
	{
		values = new double[2][];
		values[0] = descriptor.getXValues();
		values[1] = descriptor.getYValues();
			
		view = null;

		nameOfObs = new String[values[0].length];
		for( int i = 0; i < values[0].length; i++)
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
	 * @param ChartDescriptor The description of the plot
	 */
	public static void timeSeries( IChartDescriptor descriptor )
	{
		setupData(descriptor);
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries(1, values);
		chart = ChartFactory.createTimeSeriesChart(descriptor.getTitle(),
		                                           descriptor.getXLabel(),
		                                           descriptor.getYLabel(),
		                                           (XYDataset) dataset,
		                                           false,
		                                           true,
		                                           false);
		
		chartManager.put(chart, descriptor);
		ChartViewMouseListener l = new ChartViewMouseListener( view, descriptor );
		
		view.display( chart, l );

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
	// TODO This should also adapt to the new use of ChartDescriptor, or be removed...
	public static void barPlot(double[][] dataValues, String[] seriesLabels, String[] categoryLabels, String xLabel,
	                           String yLabel, String title, IChartDescriptor descriptor){
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

	public static void boxPlot(IChartDescriptor descriptor) {
	    
	    setupData( descriptor );
	    
	    if (descriptor instanceof BoxPlotDescriptor) {
	        BoxPlotDescriptor bpd = (BoxPlotDescriptor) descriptor;

	        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
	        
	        for (int i=0;i<bpd.getNumberOfColumns();i++) {
	            dataset.add( bpd.getColumn( i ), bpd.getYLabel(), bpd.getItemLabel( i ) );
	        }
	       
	        chart = ChartFactory.createBoxAndWhiskerChart(  bpd.getTitle(), 
	                                                       "", 
	                                                       "", 
	                                                       dataset, 
	                                                       false );

	        CategoryPlot plot = (CategoryPlot) chart.getPlot();
	        BoxAndWhiskerRenderer r = (BoxAndWhiskerRenderer) plot.getRenderer();
	        r.setMaximumBarWidth( 0.3 );
	        
	        chartManager.put(chart, descriptor);
	        ChartViewMouseListener l = new ChartViewMouseListener( view, descriptor );

	        view.display( chart, l );
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
	
	//These methods delegate to the model
	//TODO:  Write general Interface/Abstract class for model so its not hardwired to ChartManager
	public static IChartDescriptor getChartDescriptor(JFreeChart key) {
		return chartManager.get(key);
	}

	public static JFreeChart getActiveChart() {
		return chartManager.getActiveChart();
	}

	public static IChartDescriptor put(JFreeChart chart, IChartDescriptor descriptor) {
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

	public static IChartDescriptor remove(Object arg0) {
		return chartManager.remove(arg0);
	}

	public static Collection<IChartDescriptor> values() {
		return chartManager.values();
	}

	public static Set<JFreeChart> keySet() {
		return chartManager.keySet();
	}

	public static void handleCellChangeEvent(CellChangedEvent e) {

		Set<JFreeChart> keySet = chartManager.keySet();
		Iterator<JFreeChart> iterator = keySet.iterator();
		while( iterator.hasNext()){
			JFreeChart chart = iterator.next();
			
			IChartDescriptor desc = chartManager.get(chart);
			
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
    
    public static <T extends IBioclipseManager> T getManager(Class<T> clazz) {
        Bundle bundle = FrameworkUtil.getBundle( ChartUtils.class );
        BundleContext context = bundle.getBundleContext();
        ServiceReference<T> sRef = context.getServiceReference( clazz );
        if(sRef!=null) {
            T manager = context.getService( sRef );
            if(manager!=null){
                return manager;
            }

        }
        throw new IllegalStateException("Could not get the "+clazz.getName());
    }
}
