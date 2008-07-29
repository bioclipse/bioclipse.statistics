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
package net.bioclipse.model;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.event.MouseInputAdapter;

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
public class ScatterPlotMouseHandler extends MouseInputAdapter
{
	private ScatterPlotRenderer renderer;
	private ChartSelection currentSelection, mouseDragSelection;
	private int startX, startY;
	private int lastX, lastY;
	private Rectangle drawRect;
	
	public ScatterPlotMouseHandler(  )
	{
		lastX = 0;
		lastY = 0;
		startX = 0;
		startY = 0;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		
		ChartPanel chartPanel = getChartPanel(e);
		JFreeChart selectedChart = chartPanel.getChart();
		ChartDescriptor cd = ChartUtils.getChartDescriptor(selectedChart);
		int[] indices = cd.getSourceIndices();
		
		XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
		
		//Create double buffer
		Image buffer = chartPanel.createImage(chartPanel.getWidth(), chartPanel.getHeight());
		Graphics bufferGraphics = buffer.getGraphics();
		chartPanel.paint(bufferGraphics);
		
		if( lastX == 0 && lastY == 0)
		{
			lastX = e.getX();
			lastY = e.getY();
		}
		
		drawRect = new Rectangle();
		int x1 = Math.min(Math.min(e.getX(), lastX), startX);
		int y1 = Math.min(Math.min(e.getY(), lastY), startY);
		int x2 = Math.max(Math.max(e.getX(), lastX), startX);
		int y2 = Math.max(Math.max(e.getY(), lastY), startY);
		
		drawRect.x = x1;
		drawRect.y = y1;
		drawRect.width = x2 - drawRect.x;
		drawRect.height = y2 - drawRect.y;
		
		
		//Create a clipping rectangle
		Rectangle clipRect = new Rectangle(drawRect.x -100, drawRect.y -100, drawRect.width +200, drawRect.height +200);
		
		
		//Check for selected points
		for (int j=0; j<plot.getDataset().getItemCount(plot.getDataset().getSeriesCount()-1);j++)
		{
			for (int i=0; i<plot.getDataset().getSeriesCount();i++){
				Number xK = plot.getDataset().getX(i,j);
				Number yK = plot.getDataset().getY(i,j);
				Point2D datasetPoint2D = new Point2D.Double(domainValueTo2D(chartPanel, plot, xK.doubleValue()),rangeValueTo2D(chartPanel, plot, yK.doubleValue()));
				
				if(drawRect.contains(datasetPoint2D) ){
					PlotPointData cp = new PlotPointData(indices[j],cd.getXLabel(),cd.getYLabel());
					boolean pointAdded = mouseDragSelection.addPoint(cp);
					if( pointAdded ){
						((ScatterPlotRenderer) plot.getRenderer()).addMarkedPoint(j, i);
						selectedChart.plotChanged(new PlotChangeEvent(plot));
					}
				}
				else if( !mouseDragSelection.isEmpty()){
					PlotPointData cp = new PlotPointData(indices[j],cd.getXLabel(),cd.getYLabel());
					boolean pointRemoved = mouseDragSelection.removePoint(cp);
					if( pointRemoved ){
						((ScatterPlotRenderer) plot.getRenderer()).removeMarkedPoint(new Point(j,i));
						selectedChart.plotChanged(new PlotChangeEvent(plot));
					}
				}
			}
		}
		
		Iterator<PlotPointData> iterator = currentSelection.iterator();
		while( iterator.hasNext()){
			PlotPointData next = iterator.next();
			Point dataPoint = next.getDataPoint();
			((ScatterPlotRenderer) plot.getRenderer()).addMarkedPoint(dataPoint);
		}
		
		lastX = e.getX();
		lastY = e.getY();
		
		Graphics graphics = chartPanel.getGraphics();
		graphics.setClip(clipRect);
		
		//Draw selection rectangle
		bufferGraphics.drawRect(drawRect.x, drawRect.y, drawRect.width, drawRect.height);
		
		graphics.drawImage(buffer, 0, 0, chartPanel.getWidth(), chartPanel.getHeight(), null);
	}
	
	
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		ChartPanel chartPanel = getChartPanel(e);
		startX = e.getX();
		startY = e.getY();
		
		if( !e.isShiftDown())
		{
			((ScatterPlotRenderer)((XYPlot)chartPanel.getChart().getPlot()).getRenderer()).clearMarkedPoints();
			currentSelection = new ChartSelection();
			chartPanel.getChart().plotChanged(new PlotChangeEvent(chartPanel.getChart().getPlot()));
		} else{
			((ScatterPlotRenderer)((XYPlot)chartPanel.getChart().getPlot()).getRenderer()).removeMarkedPoint(null);
		}
		if( currentSelection == null){
			currentSelection = new ChartSelection();
		}

		mouseDragSelection = new ChartSelection();
		currentSelection.setDescriptor(ChartUtils.getChartDescriptor(chartPanel.getChart()));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		super.mouseReleased(e);
		startX = 0;
		startY = 0;
		lastX = 0;
		lastY = 0;
		ChartPanel chartPanel = this.getChartPanel(e);
		chartPanel.repaint();
		
		currentSelection.addAll(mouseDragSelection);
		ChartUtils.updateSelection(currentSelection);
		
	}
	
	private Number getDomainX(ChartPanel chartPanel, XYPlot plot, Point2D mousePoint )
	{
		ChartRenderingInfo info = chartPanel.getChartRenderingInfo();
		Rectangle2D dataArea = info.getPlotInfo().getDataArea();
		Number x = plot.getDomainAxis().java2DToValue(mousePoint.getX(), dataArea, 
				plot.getDomainAxisEdge());
		
		return x;
	}
	
	private Number getRangeY(ChartPanel chartPanel, XYPlot plot, Point2D mousePoint )
	{
		ChartRenderingInfo info = chartPanel.getChartRenderingInfo();
		Rectangle2D dataArea = info.getPlotInfo().getDataArea();
		Number y = plot.getRangeAxis().java2DToValue(mousePoint.getY(), dataArea, 
				plot.getRangeAxisEdge());
		
		return y;
	}
	
	private double domainValueTo2D(ChartPanel chartPanel, XYPlot plot, double value)
	{
		ChartRenderingInfo info = chartPanel.getChartRenderingInfo();
		Rectangle2D dataArea = info.getPlotInfo().getDataArea();
		Number x = plot.getDomainAxis().valueToJava2D(value, dataArea, 
				plot.getDomainAxisEdge());
		
		return x.doubleValue();
	}
	
	private double rangeValueTo2D(ChartPanel chartPanel, XYPlot plot, double value){
		ChartRenderingInfo info = chartPanel.getChartRenderingInfo();
		Rectangle2D dataArea = info.getPlotInfo().getDataArea();
		Number y = plot.getRangeAxis().valueToJava2D(value, dataArea, 
				plot.getRangeAxisEdge());
		
		return y.doubleValue();
	}
	

	
	private ChartPanel getChartPanel(MouseEvent me)
	{
		Point2D p = null;
		Frame sourceFrame;
		ChartPanel selectedPanel = null;
		
		if( me.getSource() instanceof Frame){
			sourceFrame = (Frame)me.getSource();
			
			Component[] components = sourceFrame.getComponents();
			
			boolean foundChartPanel = false;
			for (Component component : components) {
				if( component instanceof ChartPanel ){
					selectedPanel = (ChartPanel) component;
//					selectedChart = chartPanel.getChart();
					foundChartPanel = true;
					break;
				}
			}
			assert foundChartPanel : "The source Frame of the event does not contain a ChartPanel";
		} else if( me.getSource() instanceof ChartPanel){
			selectedPanel = (ChartPanel) me.getSource();
		}
		else{
			//Can't throw checked exception because the methods that use getChart doesn't throw Exception in their signatures
			throw new RuntimeException("The source of any mouse event on ScatterPlotMouseHandler should" +
					" be either Frame or ChartPanel");
		}
		return selectedPanel;
	}

	public void mouseClicked(MouseEvent me) {
		Point2D p = null;
		ChartDescriptor cd = null;
		int[] indices = null;
		JFreeChart selectedChart = null;
		
		ChartPanel chartPanel = getChartPanel(me);
		p = chartPanel.translateScreenToJava2D(new Point(me.getX(), me.getY()));
		selectedChart = chartPanel.getChart();

		cd = ChartUtils.getChartDescriptor(selectedChart);
		indices = cd.getSourceIndices();

		XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
		
		XYItemRenderer plotRenderer = plot.getRenderer();
		
		if( !(plotRenderer instanceof ScatterPlotRenderer) )
		{
			throw new IllegalStateException("Charts using ScatterPlotMouseHandler must use ScatterPlotRenderer as their renderer");
		}
		renderer = (ScatterPlotRenderer) plot.getRenderer();
		
		// now convert the Java2D coordinate to axis coordinates...
		Number xx = getDomainX(chartPanel, plot, p);
		Number yy = getRangeY(chartPanel, plot, p);

		//Find the selected point in the dataset
		//If shift is down, save old selections
		if( !me.isShiftDown() || currentSelection == null)
		{
			currentSelection = new ChartSelection();
		}
		
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
					PlotPointData cp = new PlotPointData(indices[j],cd.getXLabel(), cd.getYLabel());
					cp.setDataPoint(j, i);
					currentSelection.addPoint(cp);
					if( !me.isShiftDown() )
						renderer.clearMarkedPoints();
					renderer.addMarkedPoint(j, i);
					selectedChart.plotChanged(new PlotChangeEvent(plot));

				}
			}
		}
		currentSelection.setDescriptor(cd);
		ChartUtils.updateSelection(currentSelection);
	}
}