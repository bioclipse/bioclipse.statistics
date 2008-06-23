/**
 * 
 */
package net.bioclipse.chart;

import java.awt.Paint;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.graphics.Point;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;


/** ScatterPlotRenderer is responsible for rendering scatter plots in ChartUtils
 * It's function is to highlight points selected by the user
 * @author EskilA
 *
 */
public class ScatterPlotRenderer extends XYLineAndShapeRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3905406232146503788L;
	private List<Point> markedPoints;
	
	/**
	 * Adds marks a point for highlighting
	 * @param series series in the dataset
	 * @param index index of the selected point
	 * @see XYDataSet
	 */
	public void addMarkedPoint( int series, int index )
	{
		Point p = new Point(index,series);
		markedPoints.add(p);
	}
	
	/**
	 * Removes all marked points
	 */
	public void clearMarkedPoints()
	{
		markedPoints.clear();
	}
	
	private void initialize()
	{
		markedPoints = new Vector<Point>();
	}
	
	public ScatterPlotRenderer()
	{
		super(true, true);
		initialize();
	}
	
	public ScatterPlotRenderer( boolean lines, boolean shapes )
	{
		super( lines, shapes );
		initialize();
	}
	
	@Override
	public Paint getItemPaint(int row, int column) {
		Iterator<Point> iter = markedPoints.iterator();
		while( iter.hasNext())
		{
			Point p = iter.next();
			if( p.x == column && p.y == row)
				return Color.orange;
		}
		
		return super.getItemPaint(row, column);
	}

}
