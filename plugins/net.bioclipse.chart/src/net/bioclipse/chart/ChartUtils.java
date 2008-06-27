package net.bioclipse.chart;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.util.Iterator;

import net.bioclipse.chart.events.CellData;
import net.bioclipse.chart.events.CellSelection;
import net.bioclipse.model.ChartConstants;
import net.bioclipse.model.ChartDescriptor;
import net.bioclipse.model.ChartManager;
import net.bioclipse.model.ChartSelection;
import net.bioclipse.model.ColumnData;
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
	
	/**
	 * Displays data in a line plot
	 * 
	 * @param xValues x values of points
	 * @param yValues y values of points
	 * @param xLabel X axis label
	 * @param yLabel Y axis label
	 * @param title Chart title
	 */
	public static void linePlot( double[] xValues, double[] yValues,
			String xLabel, String yLabel, String title )
	{
		setupData(xValues, yValues, xLabel, yLabel, title);

		PcmLineChartDataset dataset = new PcmLineChartDataset(values, nameOfObs, xLabel, yLabel, "", title, null);
		chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, dataset, PlotOrientation.VERTICAL, true, true , false);
		
		ChartDescriptor cd = new ChartDescriptor(null,null,ChartConstants.LINE_PLOT,xLabel,yLabel);
		
		view.display( chart );
	}
	
	public static ChartDescriptor getChartDescriptor(JFreeChart key)
	{
		return chartManager.get(key);
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
	 */
	@SuppressWarnings("unchecked")
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
					renderer.addMarkedPoint(0, cd.getRow());
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
			String xLabel, String yLabel, String title)
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
	 */
	public static void timeSeries(double[] dataValues, double[] timeValues, String xLabel, String yLabel, String title)
	{
		setupData(dataValues, timeValues, xLabel, yLabel, title);
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries(1, values);
		chart = ChartFactory.createTimeSeriesChart(title, xLabel, yLabel, (XYDataset)dataset, false, false, false);

		view.display( chart );
		ChartUtils.currentPlotType = ChartConstants.TIME_SERIES;
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
}