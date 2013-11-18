/* ***************************************************************************
 * Copyright (c) 2013 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/
package net.bioclipse.chart;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.ui.sdfeditor.editor.MolTableSelection;
import net.bioclipse.cdk.ui.sdfeditor.editor.MoleculeTableViewer.MolTableElement;
import net.bioclipse.core.domain.IMolecule.Property;
import net.bioclipse.model.ChartAction;
import net.bioclipse.model.ChartConstants;
import net.bioclipse.model.ChartDescriptor;
import net.bioclipse.model.ScatterPlotMouseHandler;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 * A modification of the the chart view in JFreeChart to adapt it to the needs 
 * of Bioclipse.
 * 
 * @author Klas Jšnsson (klas.joensson@gmail.com)
 *
 */
public class BioclipseChartPanel extends ChartPanel implements
        ISelectionListener {

    private static final long serialVersionUID = -5751734175128962492L;
    private ScatterPlotMouseHandler smh;
    private ChartAction zoomSelectAction;
    
    // Variables for customize the chart panel
    private final static int MINIMUM_DRAW_WIDTH = 200;
    private final static int MINIMUM_DRAW_HIGHT = 100;
    private final static boolean ENABLE_POPERTIES = true;
    private final static boolean ENABLE_SAVE = true;
    private final static boolean ENABLE_PRINT = true;
    private final static boolean ENABLE_ZOOM = true;
    private final static boolean ENABLE_TOOLTIPS = true;
    
    /**
     * The constructor, it needs the chart that are to be displayed and the
     * action that are responsibly for handle the zoom- and selection-operations
     * in the chart.
     * 
     * @param chart The chart to be viewed
     * @param ca The class handling the zoom/selection actions
     */
    public BioclipseChartPanel(JFreeChart chart, ChartAction ca) {
        super(chart, ChartPanel.DEFAULT_WIDTH, ChartPanel.DEFAULT_HEIGHT,
              MINIMUM_DRAW_WIDTH, MINIMUM_DRAW_HIGHT,
              ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH,
              ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT,
              ChartPanel.DEFAULT_BUFFER_USED, ENABLE_POPERTIES, ENABLE_SAVE,
              ENABLE_PRINT, ENABLE_ZOOM, ENABLE_TOOLTIPS );
        zoomSelectAction = ca;
        smh = new ScatterPlotMouseHandler();
        
    }
    
    public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
        
        if ( part instanceof IEditorPart )
            if ( selection instanceof IStructuredSelection) {
                
                IStructuredSelection sel = (IStructuredSelection) selection;             
                XYPlot plot = (XYPlot) getChart().getPlot();
                XYItemRenderer plotRenderer = plot.getRenderer();
                ScatterPlotRenderer renderer = null;
                if (plotRenderer instanceof ScatterPlotRenderer) {
                    renderer = (ScatterPlotRenderer) plot.getRenderer();
                }
                String xLabel = plot.getDomainAxis().getLabel();
                String yLabel = plot.getRangeAxis().getLabel();
                if (renderer != null) {
                    if (sel instanceof MolTableSelection) {
                        MolTableSelection mtSel = (MolTableSelection) sel;
                        List<Double> xValues = getValues( mtSel, xLabel );
                        List<Double> yValues = getValues( mtSel, yLabel );
                        renderer.clearMarkedPoints();
                        for (int i = 0;i<xValues.size();i++) 
                            selectPoints( xValues.get( i ), yValues.get( i ), plot, renderer );

                    } else {
                        double xValue, yValue;
                        Iterator itr = sel.iterator();
                        while (itr.hasNext()) {
                            Object obj = itr.next();
                            if (obj instanceof MolTableElement) {
                                MolTableElement element = (MolTableElement) obj;

                                xValue = getValue( element, xLabel );
                                yValue = getValue( element, yLabel );
                                renderer.clearMarkedPoints();
                                selectPoints( xValue, yValue, plot, renderer );

                            }

                        }
                    }

                } 
            }
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (zoomSelectAction.isChecked() || e.isPopupTrigger())
            super.mousePressed( e );
        else
            smh.mousePressed( e );
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        if (zoomSelectAction.isChecked())
            super.mouseDragged( e );
        else
            smh.mouseDragged( e );
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        if (zoomSelectAction.isChecked()) 
            super.mouseReleased( e );
        else 
            smh.mouseReleased( e );
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        ChartDescriptor cd = ChartUtils.getChartDescriptor(ChartUtils.getActiveChart());
        if (cd != null && cd.getPlotType() == ChartConstants.HISTOGRAM)
            super.mouseClicked( e );
        else
            smh.mouseClicked( e );
        
    }
    
    /**
     * Gets the value of a specific property from a selection in a 
     * MolTableViewer.
     * 
     * @param element The selected element, i.e. row
     * @param property The name of the property
     * @return The value that the selected property has in this element, or 
     *      <code>Double.NaN</code> if it by some reason couldn't determine it
     */
    private double getValue(MolTableElement element, String property ) {
        double value = Double.NaN;

        if (property.equals( ChartConstants.ROW_NUMBER ))
            value = element.getIndex() + 1;
        else if (property.equals( ChartConstants.MOL_MASS )) {
            ChartDescriptor cd = ChartUtils.getChartDescriptor(getChart());
            if (cd != null) {
                if (cd.getXLabel().equals( ChartConstants.MOL_MASS ))
                    value = cd.getXValue( element.getIndex() );
                else if (cd.getYLabel().equals( ChartConstants.MOL_MASS ))
                    value = cd.getYValue( element.getIndex() );
                else
                    value = Double.NaN;
            } else 
                value = Double.NaN;
        } else {
            ICDKMolecule mol = (ICDKMolecule) element.getAdapter( ICDKMolecule.class );
            value = getValue( mol, property );
        }
        return value;
    }
    
    /**
     * Gets the value of a specific property from a molecule.
     * 
     * @param mol The molecule
     * @param property The name of the property
     * @return The value of the selected property of this molecule, or 
     *      <code>Double.NaN</code> if it by some reason couldn't determine it
     */
    private double getValue(ICDKMolecule mol, String property) {
        double value = Double.NaN;
        try {
            Object obj = mol.getProperty( property, Property.USE_CACHED_OR_CALCULATED );
            value = Double.parseDouble( obj.toString() );
        } catch (NumberFormatException e){
            value = Double.NaN;
        } catch (NullPointerException e) {
            value = Double.NaN;
        }
        
        return value;
    }

    /**
     * The same as <code>getValue(MolTableElement, String )</code>, but for a 
     * selection of several rows.
     *  
     * @param mtSel The selection containing the rows selected
     * @param property The name of the property
     * @return A <code>List</code> with the values of the selected property of 
     *  this molecule, or <code>Double.NaN</code> if it by some reason couldn't 
     *  determine it
     */
    private List<Double> getValues(MolTableSelection mtSel, String property) {
        List<Double> values = new ArrayList<Double>(mtSel.size());
        if (property.equals( ChartConstants.ROW_NUMBER ))
            for(Integer i:mtSel.getSelectedRows())
                values.add( i.doubleValue() + 1 );
        else if (property.equals( ChartConstants.MOL_MASS )) {
            ChartDescriptor cd = ChartUtils.getChartDescriptor(getChart());
            for(Integer i:mtSel.getSelectedRows()) {
                if (cd != null) {
                    if (cd.getXLabel().equals( ChartConstants.MOL_MASS ))
                        values.add( cd.getXValue( i ) );
                    else if (cd.getYLabel().equals( ChartConstants.MOL_MASS ))
                        values.add(  cd.getYValue( i ) );
                    else
                        values.add( Double.NaN );
                } else {
                    values.add( Double.NaN );
                }
            }
        } else {
            ICDKMolecule mol;
            Iterator<ICDKMolecule> mtSelItr = mtSel.iterator();
            while (mtSelItr.hasNext()) {
                mol = (ICDKMolecule) mtSelItr.next();
                values.add( getValue( mol, property ) );
            }
            mol = (ICDKMolecule) mtSelItr.next();
            values.add( getValue( mol, property ) );
        }
        return values;
    }
    
    /**
     * Select the point in the plot that corresponds to the values.
     * 
     * @param xValue The x-value of the point to be selected
     * @param yValue The y-value of the point to be selected
     * @param plot The plot where to make the selection
     * @param renderer The render of the plot
     */
    private void selectPoints(double xValue, double yValue, XYPlot plot, ScatterPlotRenderer renderer) {
        Number xK, yK;
        if (xValue != Double.NaN && xValue != Double.NaN ) {
            for (int j=0; j<plot.getDataset().getItemCount(plot.getDataset().getSeriesCount()-1);j++) {
                for (int i=0; i<plot.getDataset().getSeriesCount();i++) {
                    xK = plot.getDataset().getX(i,j);
                    yK = plot.getDataset().getY(i,j);
                    if (xValue == xK.doubleValue() && yValue == yK.doubleValue()) {     
                        renderer.addMarkedPoint(j, i);
                    }

                }
            }
        }
        getChart().plotChanged( new PlotChangeEvent(plot) );
    }
}
