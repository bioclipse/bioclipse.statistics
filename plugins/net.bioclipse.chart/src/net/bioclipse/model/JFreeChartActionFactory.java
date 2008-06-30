package net.bioclipse.model;

import java.io.IOException;

import net.bioclipse.chart.ChartUtils;
import net.bioclipse.plugins.views.ChartView;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.jfree.chart.JFreeChart;

/**
 * Creates actions that works with JFreeChart
 * @author Eskil Andersen
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

}
