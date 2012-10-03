package net.bioclipse.chart.ui.business;

import java.util.Set;

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
