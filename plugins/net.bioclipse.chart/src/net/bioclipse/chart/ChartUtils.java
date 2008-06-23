package net.bioclipse.chart;

import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import net.bioclipse.chart.events.CellData;
import net.bioclipse.chart.events.CellSelection;
import net.bioclipse.model.ChartConstants;
import net.bioclipse.model.ChartSelection;
import net.bioclipse.model.PcmLineChartDataset;
import net.bioclipse.model.PlotPointData;
import net.bioclipse.plugins.views.ChartView;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * This is a utility class with static methods for plotting data on the chart plugins
 * chart view. Charts generated with these methods are displayed in ChartView
 * 
 * @see net.bioclipse.plugins.views.ChartView
 * @author EskilA
 *
 */
public class ChartUtils
{
	private static JFreeChart chart;
	private final static String CHART_VIEW_ID ="net.bioclipse.plugins.views.ChartView";
	private static Composite chartComposite;
	private static double[][] values;
	private static ChartView view;
	private static String[] nameOfObs;
	private static Frame frame;
	private static Composite composite;
	private static String xColumn, yColumn;
	private static ChartSelection cs;
	private static int currentPlotType = -1;
	private static final boolean IS_MACOS = System.getProperty("os.name").contains("Mac");
	private static int[] indices;
	
	/**
	 * Displays data in a line plot
	 * 
	 * @param xValues x values of points
	 * @param yValues y values of points
	 * @param xLabel X axis label
	 * @param yLabel Y axis label
	 * @param title plot title
	 */
	public static void linePlot( double[] xValues, double[] yValues,
			String xLabel, String yLabel, String title )
	{
		setupData(xValues, yValues, xLabel, yLabel, title);


		PcmLineChartDataset dataset = new PcmLineChartDataset(values, nameOfObs, xLabel, yLabel, "", title, null);
		System.out.println("in chartutils.scatterplot");
		chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, dataset, PlotOrientation.VERTICAL, true, true , false);
		display( ChartConstants.LINE_PLOT );
		ChartUtils.currentPlotType = ChartConstants.LINE_PLOT;
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
	
	//Utility method that different plot functions use to display their plots in ChartView
	private static void display( final int plotType )
	{
		chartComposite = view.getParent();		

		//Clear chartComposite of all old controls
		Control[] children = chartComposite.getChildren();		
		for( int i = 0; i<children.length;i++)
		{
			children[i].dispose();
		}

		composite = new Composite(chartComposite, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		frame = SWT_AWT.new_Frame(composite);

		final ChartPanel chartPanel = new ChartPanel(chart);
		
		SwingUtilities.invokeLater(new Runnable()
		{

			public void run() {
				// TODO Auto-generated method stub
				frame.removeAll();
				frame.add(chartPanel);
				frame.setVisible(true);

				if( plotType == ChartConstants.SCATTER_PLOT )
				{
					//Set the scatter plot renderer to our custom ScatterPlotRenderer
					XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
					final ScatterPlotRenderer renderer = new ScatterPlotRenderer( false, true );
					plot.setRenderer(renderer);
					
					//Listens for mouseclicks on points
					PlotMouseHandler pmh = new PlotMouseHandler(chartPanel, renderer);
					
					if( IS_MACOS )
					{
						frame.addMouseListener(pmh);
					}
					else
					{
						chartPanel.addMouseListener(pmh);
					}					
				}
			}
		});
		chartComposite.forceFocus();
		chartComposite.layout();
	}
	
	//Used to take mouse input and mark points that have been clicked on
	private static class PlotMouseHandler extends MouseAdapter
	{
		private ChartPanel chartPanel;
		private ScatterPlotRenderer renderer;
		
		public PlotMouseHandler( ChartPanel chartPanel, ScatterPlotRenderer renderer  )
		{
			this.chartPanel = chartPanel;
			this.renderer = renderer;
		}
		
		public void mouseClicked(MouseEvent me) {
			
			Point2D p = chartPanel.translateScreenToJava2D(new Point(me.getX(), me.getY()));
			
			// now convert the Java2D coordinate to axis coordinates...
			XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
			ChartRenderingInfo info = chartPanel.getChartRenderingInfo();
			Rectangle2D dataArea = info.getPlotInfo().getDataArea();
			Number xx = plot.getDomainAxis().java2DToValue(p.getX(), dataArea, 
					plot.getDomainAxisEdge());
			Number yy = plot.getRangeAxis().java2DToValue(p.getY(), dataArea, 
					plot.getRangeAxisEdge());

			//Find the selected point in the dataset
			//If shift is down, save old selections
			if( !me.isShiftDown())
				cs = new ChartSelection();
			for (int j=0; j<plot.getDataset().getItemCount(plot.getDataset().getSeriesCount()-1);j++)
			{
				for (int i=0; i<plot.getDataset().getSeriesCount();i++){
					Number xK = plot.getDataset().getX(i,j);
					Number yK = plot.getDataset().getY(i,j);
					Number xKCheck = xK.doubleValue()-xx.doubleValue();
					Number yKCheck = yK.doubleValue()-yy.doubleValue();
					Number xxCheck = xKCheck.doubleValue()*xKCheck.doubleValue();
					Number yyCheck = yKCheck.doubleValue()*yKCheck.doubleValue();
					if ( Math.sqrt(xxCheck.doubleValue()) <= 0.1  && Math.sqrt(yyCheck.doubleValue()) <= 0.1){
//						System.out.println("Mitt i prick!");
//						System.out.println(plot.getDataset().getX(i,j) +","+ plot.getDataset().getY(i,j));			
						//Create a new selection
						PlotPointData cp = new PlotPointData(indices[j],xColumn,yColumn);
						cs.addPoint(cp);
						if( !me.isShiftDown() )
							renderer.clearMarkedPoints();
						renderer.addMarkedPoint(i, j);
						chart.plotChanged(new PlotChangeEvent(plot));

					}
				}
			}
			
			view.setSelection(cs);
		}	
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
		display( ChartConstants.SCATTER_PLOT );
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
	 */
	public static void scatterPlot(double[] xValues, double[] yValues,
			String xLabel, String yLabel, String title, int[] indices)
	{
		setupData(xValues, yValues, xLabel, yLabel, title);
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries(1, values);
		chart = ChartFactory.createScatterPlot(title, xLabel, yLabel, dataset, PlotOrientation.VERTICAL, false, false,false);
		display( ChartConstants.SCATTER_PLOT );
		ChartUtils.currentPlotType = ChartConstants.SCATTER_PLOT;
		ChartUtils.indices = indices;
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
		display( ChartConstants.HISTOGRAM );
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
			// TODO Auto-generated catch block
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
		chartComposite = view.getParent();

		display( ChartConstants.TIME_SERIES );
		ChartUtils.currentPlotType = ChartConstants.TIME_SERIES;
	}

	/**
	 * Utility method for converting JFreeChart to an image so that SWT can display it
	 * @param parent used for color correction 
	 * @param chart the chart to be made into an image
	 * @param width image width
	 * @param height image height
	 * @return SWT Image of the chart 
	 */
	private static Image createChartImage(Composite parent, JFreeChart chart,
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
	
	public static void saveImagePNG(String path) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(path);
		ChartUtilities.writeChartAsPNG(fos, chart, 640, 480);
		fos.close();
	}
	
	public static void saveImageJPG(String path) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(path);
		ChartUtilities.writeChartAsJPEG(fos, chart, 640, 480);
		fos.close();
	}
	
	/**
	 * Save image to svg format
	 * @param path the path including filename where the image is to be stored
	 */
	public static void saveImageSVG( String path )
	{
		//First check that we have a valid chart
		if( chart != null )
		{
			//Create DOM objects
			DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
			Document document = domImpl.createDocument(null, "svg", null);

			//Create an instance of the SVG generator
			SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

			//Setting the precision apparently avoids a NullPointerException in Batik 1.5
			svgGenerator.getGeneratorContext().setPrecision(6);

			//Render chart into SVG Graphics2D impl.
			chart.draw(svgGenerator, new Rectangle2D.Double(0,0,400,300),null);
			
			//Write to file
			boolean useCSS = true;
			try {
				Writer out = new OutputStreamWriter(
						new FileOutputStream(new File(path)), "UTF-8");
				svgGenerator.stream(out,useCSS);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (SVGGraphics2DIOException e) {
				e.printStackTrace();
			}
		}

	}
}