/*******************************************************************************
 * Copyright (c) 2013  Klas Jšnsson <klas.joensson@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.chart.ui.business;

import net.bioclipse.chart.ChartUtils;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;



public class PointLabelHandler extends AbstractHandler {

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException {
        
            JFreeChart activeChart = ChartUtils.getActiveChart();
            Plot plot = activeChart.getPlot();
            
            if (plot instanceof XYPlot) {
                XYItemRenderer renderer = ((XYPlot) plot).getRenderer();
                renderer.setBaseItemLabelsVisible( !renderer.getBaseItemLabelsVisible() );
            }

        return null;
    }

}
