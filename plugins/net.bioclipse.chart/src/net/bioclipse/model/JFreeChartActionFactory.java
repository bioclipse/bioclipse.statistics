/* ***************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.model;

import java.io.IOException;
import java.util.Set;

import net.bioclipse.chart.ChartUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 * Creates actions that works with JFreeChart
 * @author Eskil Andersen, Klas Jšnsson
 *
 */
public class JFreeChartActionFactory implements ChartActionFactory {
    
	public ChartAction createExportPngAction() {
		//Create action for saving charts in png format
		ChartAction saveImageActionPNG = new ChartAction(){
			private JFreeChart activeChart;
			public void run()
			{
				FileDialog dialog = 
					new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						SWT.SAVE);
				dialog.setFileName("Image.png");
				String path = dialog.open();
				
				if(path == null)
					return;
				
				try {
					ImageWriter.saveImagePNG(path, activeChart);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void handleChartModelEvent(ChartModelEvent e) {
				if( e.getEventType() == ChartEventType.ACTIVE_CHART_CHANGED){
					activeChart = ChartUtils.getActiveChart();
				}
			}
		};
		saveImageActionPNG.setText("Export as PNG Image");
		saveImageActionPNG.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		saveImageActionPNG.setToolTipText("Export the chart as a PNG image");
		
		return saveImageActionPNG;
	}

	public ChartAction createExportSvgAction() {
		ChartAction saveImageActionSVG = new ChartAction() {
			private JFreeChart activeChart;
			public void run()
			{
				FileDialog dialog = 
					new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						SWT.SAVE);
				dialog.setFileName("Image.svg");
				String path = dialog.open();
				
				if(path == null)
					return;
				
//				System.out.println(path);
				ImageWriter.saveImageSVG(path, activeChart);
			}

			public void handleChartModelEvent(ChartModelEvent e) {
				if( e.getEventType() == ChartEventType.ACTIVE_CHART_CHANGED){
					activeChart = ChartUtils.getActiveChart();
				}
			}
		};
		saveImageActionSVG.setText("Export as SVG Image");
		saveImageActionSVG.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		saveImageActionSVG.setToolTipText("Export the chart as a SVG image");
		
		return saveImageActionSVG;
	}

	public ChartAction createExtportJpegAction() {
		//Create action for saving charts in JPEG format
		ChartAction saveImageActionJPG = new ChartAction(){
			private JFreeChart activeChart;
			public void run()
			{
				FileDialog dialog = 
					new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						SWT.SAVE);
				dialog.setFileName("Image.jpg");
				String path = dialog.open();
				
				if(path == null)
					return;
				
				try {
					ImageWriter.saveImageJPG(path, activeChart);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void handleChartModelEvent(ChartModelEvent e) {
				if( e.getEventType() == ChartEventType.ACTIVE_CHART_CHANGED){
					activeChart = ChartUtils.getActiveChart();
				}
			}
		};
		saveImageActionJPG.setText("Export as JPG Image");
		saveImageActionJPG.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		saveImageActionJPG.setToolTipText("Export the chart as a JPG image");
		
		return saveImageActionJPG;
	}

	public ChartAction createPointLabelsAction() {
	    //Create action for showing or hiding point labels.
	    /* TODO This is the old way to do this, this should be handle via 
	     * plugin.xml. But I can't figure out the locationURI for adding it to
	     * the coolbar of the chart view.*/
	    ChartAction showHidePointLabels = new ChartAction("Point labels", Action.AS_CHECK_BOX){
	        private JFreeChart activeChart;
	        public void run() {
	            Set<JFreeChart> allCharts = ChartUtils.getCharts();    
	            for (JFreeChart chart:allCharts) {
	                Plot plot = chart.getPlot();
	                if (plot instanceof XYPlot) {
	                    XYItemRenderer renderer = ((XYPlot) plot).getRenderer();
	                    renderer.setBaseItemLabelsVisible( !renderer.getBaseItemLabelsVisible() );
	                }
	                
	            }
	        }

	        public void handleChartModelEvent(ChartModelEvent e) {
	            if( e.getEventType() == ChartEventType.ACTIVE_CHART_CHANGED){
	                activeChart = ChartUtils.getActiveChart();
	            }
	        }
	    };
	    showHidePointLabels.setToolTipText( "Show/hide point labels" );
	    showHidePointLabels.setImageDescriptor( ImageDescriptor
	                                            .createFromFile(this.getClass(),
	                                                            "pointLabels.png") );	    

	    return showHidePointLabels;
	}

	public ChartAction createZoomSelectAction() {
        //Create action for switching between zoom-mode and select-mode.
        /* TODO This is the old way to do this, this should be handle via 
         * plugin.xml. But I can't figure out the locationURI for adding it to
         * the coolbar of the chart view.*/
	    ChartAction zoomSelect = new ChartAction("Click for zoom-mode", Action.AS_CHECK_BOX ){
	        public void run() {
	            ChartDescriptor cd = ChartUtils.getChartDescriptor(ChartUtils.getActiveChart());
	                if (!this.isChecked() && cd.getPlotType() != ChartConstants.HISTOGRAM) {
	                    this.setText( "Select" );
	                    this.setToolTipText( "Click for zoom-mode" );
	                    this.setImageDescriptor( ImageDescriptor
	                                             .createFromFile(this.getClass(),
	                                                     "18721marquee_wires16.gif") );
	                } else {
	                    this.setText( "Zoom" );
	                    this.setToolTipText( "Click for selection-mode" );
	                    this.setImageDescriptor( ImageDescriptor
	                                             .createFromFile(this.getClass(),
	                                                     "13991find.gif") );
	                    if (cd.getPlotType() == ChartConstants.HISTOGRAM) {
	                        this.setChecked( true );
	                        this.setEnabled( false ); 
	                    }
	                }

	        }

            public void handleChartModelEvent( ChartModelEvent e ) {
                if( e.getEventType() == ChartEventType.ACTIVE_CHART_CHANGED){
                    ChartDescriptor cd = ChartUtils.getChartDescriptor(ChartUtils.getActiveChart());
                    
                    if (cd != null ) {
                        if(cd.getPlotType() == ChartConstants.HISTOGRAM) {
                            this.setText( "Zoom" );
                            this.setToolTipText( "Click for selection-mode" );
                            this.setImageDescriptor( ImageDescriptor
                                                     .createFromFile(this.getClass(),
                                                             "13991find.gif") );
                            this.setChecked( true );
                            this.setEnabled( false );
                        } else{
                            this.setEnabled( true );
                        }

                    } else
                        this.setEnabled( false );

                }
            }

  
        };
        
        zoomSelect.setImageDescriptor( ImageDescriptor
                                                .createFromFile(this.getClass(),
                                                                "18721marquee_wires16.gif") );       

        return zoomSelect;
    }
}
