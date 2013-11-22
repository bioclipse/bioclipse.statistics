/* ***************************************************************************
 * Copyright (c) 2012 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/
package net.bioclipse.chart;

import net.bioclipse.model.ChartSelectionItem;
import net.bioclipse.plugins.views.ChartView;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.UIPlugin;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.entity.ChartEntity;

/**
 * 
 * @author Klas Jšnsson (klas.joensson@gmail.com), aka konditorn
 *
 */
public class ChartViewMouseListener implements ChartMouseListener {

    private ChartView myView;
    private IChartDescriptor chartDescriptor;
    
    public ChartViewMouseListener (ChartView view, IChartDescriptor chartDescriptor) {
        this.chartDescriptor = chartDescriptor;
        myView = view;
    }
    
    public void chartMouseClicked( ChartMouseEvent arg0 ) {

        ChartEntity ce = arg0.getEntity();
        if (ce == null)
            /* If the ChartEntity is null it's probably 'cos the user 
             * clicked somewhere in the diagram where there's no data, e.g. 
             * outside a bar in a bar-plot. */ 
            return;
        final ChartSelectionItem selectionItem = new ChartSelectionItem( ce, chartDescriptor );
        // TODO Still need the run method 'cos some listener does SWT-stuff in a bad way
        final ChartEntity fce = ce;
        Runnable r = new Runnable() {
            public void run() {
                myView.setSelection( new ChartSelectionItem( fce, chartDescriptor ) );
                myView.setSelection( new StructuredSelection(selectionItem) );
            }
        };
        Display fDisplay = UIPlugin.getDefault().getWorkbench().getDisplay();
        fDisplay.asyncExec(r);
        
    }

    public void chartMouseMoved( ChartMouseEvent arg0 ) { 
        int button = arg0.getTrigger().getButton();
        if (button != 0)
            System.out.println( "Button: "+button );
    }

}
