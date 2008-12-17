/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/
/**
 * 
 */
package net.bioclipse.chart;
import java.awt.Paint;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import net.bioclipse.model.ChartSelection;
import net.bioclipse.model.PlotPointData;
import java.awt.Point;
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
         * Marks a point for highlighting
         * @param series series in the dataset
         * @param index index of the selected point
         * @return <code>true</code> if the point was marked, <code>false</code> otherwise, for example if the point was marked already.
         * @see XYDataSet
         */
        public boolean addMarkedPoint( int index, int series )
        {
                Point p = new Point(index,series);
                if( !markedPoints.contains(p)){
                        markedPoints.add(p);
                        return true;
                }
                return false;
        }
        public boolean addMarkedPoint( Point p )
        {
                if( !markedPoints.contains(p)){
                        markedPoints.add(p);
                        return true;
                }
                return false;
        }
        /**
         * 
         * @param p point to unmark
         * @return true if point was removed from collection, false if point was not in collection
         */
        public boolean removeMarkedPoint(Point p){
                if( markedPoints.contains(p)){
                        markedPoints.remove(p);
                        return true;
                }
                return false;
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
                        if( p != null && p.x == column && p.y == row)
                        {
                                return Color.orange;
                        }
                }
                return super.getItemPaint(row, column);
        }
}
