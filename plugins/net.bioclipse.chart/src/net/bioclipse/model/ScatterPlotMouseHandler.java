/**
 * 
 */
package net.bioclipse.model;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import net.bioclipse.chart.ChartUtils;
import net.bioclipse.chart.ScatterPlotRenderer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 * Handles clicks on scatter plots
 * @author Eskil Andersen
 *
 */
public class ScatterPlotMouseHandler extends MouseAdapter
{
	private ChartPanel chartPanel;
	private ScatterPlotRenderer renderer;
	private ChartSelection cs;
//	private JFreeChart chart;
	
	public ScatterPlotMouseHandler(  )
	{
	}
	
	public void mouseClicked(MouseEvent me) {
		Point2D p = null;
		Frame sourceFrame;
		ChartDescriptor cd = null;
		int[] indices = null;
		JFreeChart selectedChart = null;
		
		if( me.getSource() instanceof Frame){
			sourceFrame = (Frame)me.getSource();
			
			Component[] components = sourceFrame.getComponents();
			
			boolean foundChartPanel = false;
			for (Component component : components) {
				if( component instanceof ChartPanel ){
					chartPanel = (ChartPanel) component;
					p = chartPanel.translateScreenToJava2D(new Point(me.getX(), me.getY()));
					selectedChart = chartPanel.getChart();
					foundChartPanel = true;
					break;
				}
			}
			assert foundChartPanel : "The source of the event does not contain a ChartPanel";

			cd = ChartUtils.getChartDescriptor(selectedChart);
			indices = cd.getSourceIndices();
		}
		
		XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
		
		XYItemRenderer plotRenderer = plot.getRenderer();
		
		if( !(plotRenderer instanceof ScatterPlotRenderer) )
		{
			throw new IllegalStateException("Charts using ScatterPlotMouseHandler must use ScatterPlotRenderer as their renderer");
		}
		renderer = (ScatterPlotRenderer) plot.getRenderer();
		
		// now convert the Java2D coordinate to axis coordinates...
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
				//Check distance from click and point, don't want to mark points that are too far from the click
				if ( Math.sqrt(xxCheck.doubleValue()) <= 0.1  && Math.sqrt(yyCheck.doubleValue()) <= 0.1){
					//Create a new selection
					PlotPointData cp = new PlotPointData(indices[j],cd.getXLabel(),cd.getYLabel());
					cs.addPoint(cp);
					if( !me.isShiftDown() )
						renderer.clearMarkedPoints();
					renderer.addMarkedPoint(i, j);
					selectedChart.plotChanged(new PlotChangeEvent(plot));

				}
			}
		}
		cs.setDescriptor(cd);
		ChartUtils.updateSelection(cs);
	}	
}