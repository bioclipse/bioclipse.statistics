/* ***************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.dialogs;

import java.util.Arrays;
import java.util.Vector;

import net.bioclipse.chart.ChartConstants;
import net.bioclipse.chart.ChartDescriptorFactory;
import net.bioclipse.chart.ChartUtils;
import net.bioclipse.chart.IChartDescriptor;
import net.bioclipse.chart.ui.business.IChartManager;
import net.bioclipse.chart.ui.business.IJavaChartManager;
import net.bioclipse.model.ColumnData;

import org.apache.log4j.Logger;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class ChartDialog extends org.eclipse.swt.widgets.Dialog {

	private Shell dialogShell;
	private Text yAxisText;
	private Combo yValuesCombo;
	private Label yValuesLabel;
	private Combo xValuesCombo;
	private Label xValuesLabel;
	private Label label1;
	private Button cancelButton;
	private Button okButton;
	private Label yAxisLabel;
	private Text xAxisText;
	private Text chartText;
	private Label xAxisLabel;
	private Label nameLabel;
	private String[] items;
	private Vector<ColumnData> columns;
	private ChartConstants.plotTypes diagramType;
	private Label plotTypeLabel;
//	private Button rowRadioButton;
//	private Button colRadioButton;
	private Combo plotTypeCombo;
	private Label seperator;
	private boolean isPlotTypeEnabled;
	private IEditorPart dataSource;
	private Point[] cellselection;
	private Logger logger = Logger.getLogger( this.getClass() );
	
	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Dialog inside a new Shell.
	*/
	public static void main(String[] args) {
	    try {
	        Display display = Display.getDefault();
	        Shell shell = new Shell(display);
	        ChartDialog inst = new ChartDialog(shell, SWT.NULL,ChartConstants.plotTypes.LINE_PLOT,null, true, null, null);
	        inst.open();
	    } catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ChartDialog(Shell parent, int style, ChartConstants.plotTypes diagramType, Vector<ColumnData> columns,
	                   boolean enablePlotType, IEditorPart dataSource, Point[] originCells) throws IllegalArgumentException
	{
		super(parent, style);
		items = new String[columns.size()];
		for( int i=0;i < columns.size();i++)
		{
			items[i] = ((ColumnData)columns.get(i)).getLabel();
		}
		if( items.length < 2 )
			throw new IllegalArgumentException("More than one column must be selected");
		Arrays.sort(items);
		this.columns = columns;
		this.isPlotTypeEnabled = enablePlotType;
		this.diagramType = diagramType;
		this.dataSource = dataSource;
		this.cellselection = originCells;
	}

	public void open() {
		try {
			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

			dialogShell.setLayout(new FormLayout());
			dialogShell.layout();
			dialogShell.pack();			
			dialogShell.setSize(434, 322);
			dialogShell.setText("Chart Dialog");
			/* There function are to set if the data series should be picked 
			 * from the columns or rows. But as it is now the plotting in BC 
			 * does not support plotting for more than one serie per diagram.
			 * If this is implemented this buttons might be of use, so for now 
			 * they are just out comment. */
//			{
//				rowRadioButton = new Button(dialogShell, SWT.RADIO | SWT.LEFT);
//				FormData rowButtonLData = new FormData();
//				rowButtonLData.width = 217;
//				rowButtonLData.height = 20;
//				rowButtonLData.left =  new FormAttachment(0, 1000, 57);
//				rowButtonLData.top =  new FormAttachment(0, 1000, 176);
//				rowRadioButton.setLayoutData(rowButtonLData);
//				rowRadioButton.setText("Data series in rows");
//				rowRadioButton.addSelectionListener(new SelectionAdapter() {
//
//					@Override
//					public void widgetSelected(SelectionEvent e) {
//						// TODO Auto-generated method stub
//						super.widgetSelected(e);
//					}
//					
//				});
//			}
//			{
//				colRadioButton = new Button(dialogShell, SWT.RADIO | SWT.LEFT);
//				FormData colRadioButtonLData = new FormData();
//				colRadioButtonLData.width = 217;
//				colRadioButtonLData.height = 20;
//				colRadioButtonLData.left =  new FormAttachment(0, 1000, 57);
//				colRadioButtonLData.top =  new FormAttachment(0, 1000, 157);
//				colRadioButton.setLayoutData(colRadioButtonLData);
//				colRadioButton.setText("Data series in columns");
//				colRadioButton.addSelectionListener(new SelectionAdapter() {
//				});
//				colRadioButton.setSelection(true);
				
//			}
			{
				plotTypeCombo = new Combo(dialogShell, SWT.DROP_DOWN | SWT.READ_ONLY);
				plotTypeCombo.add("Scatter Plot");
				plotTypeCombo.add("Line Plot");
				plotTypeCombo.add("Time Series Plot");
				if( diagramType == ChartConstants.plotTypes.PLOT_MENU || 
				        diagramType == ChartConstants.plotTypes.SCATTER_PLOT)
				{
					plotTypeCombo.select(0);
				}
				else if( diagramType == ChartConstants.plotTypes.LINE_PLOT ){
					plotTypeCombo.select(1);
				}
				else if( diagramType == ChartConstants.plotTypes.TIME_SERIES ){
					plotTypeCombo.select(2);
				}
				FormData plotTypeComboLData = new FormData();
				plotTypeComboLData.width = 143;
				plotTypeComboLData.height = 25;
				plotTypeComboLData.left =  new FormAttachment(0, 1000, 143);
				plotTypeComboLData.top =  new FormAttachment(0, 1000, 13);
				plotTypeCombo.setLayoutData(plotTypeComboLData);
				
				plotTypeCombo.setEnabled(isPlotTypeEnabled);
				plotTypeCombo.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {

						super.widgetSelected(e);
						String text = ((Combo)e.getSource()).getText();
						if( text.equals("Scatter Plot")){
							ChartDialog.this.diagramType = ChartConstants.plotTypes.SCATTER_PLOT;
						}
						else if (text.equals("Line Plot")) 
						{
							ChartDialog.this.diagramType = ChartConstants.plotTypes.LINE_PLOT;
						} else if( text.equals("Time Series Plot")) 
						{
							ChartDialog.this.diagramType = ChartConstants.plotTypes.TIME_SERIES;
						}
					}
					
				});
			}
			{
				seperator = new Label(dialogShell, SWT.SEPARATOR);
				FormData seperatorLData = new FormData();
				seperatorLData.width = 400;
				seperatorLData.height = 13;
				seperatorLData.left =  new FormAttachment(0, 1000, 12);
				seperatorLData.top =  new FormAttachment(0, 1000, 43);
				seperator.setLayoutData(seperatorLData);
				seperator.setText("separator1");
			}
			{
				plotTypeLabel = new Label(dialogShell, SWT.NONE);
				plotTypeLabel.setText("Type of plot:");
				FormData plotTypeLabelLData = new FormData();
				plotTypeLabelLData.width = 81;
				plotTypeLabelLData.height = 19;
				plotTypeLabelLData.left =  new FormAttachment(0, 1000, 62);
				plotTypeLabelLData.top =  new FormAttachment(0, 1000, 18);
				plotTypeLabel.setLayoutData(plotTypeLabelLData);
			}
			{
				yValuesCombo = new Combo(dialogShell, SWT.READ_ONLY | SWT.DROP_DOWN);
				FormData yValuesComboLData = new FormData();
				yValuesComboLData.width = 140;
				yValuesComboLData.height = 28;
				yValuesComboLData.left =  new FormAttachment(0, 1000, 141);
				yValuesComboLData.top =  new FormAttachment(0, 1000, 236);
				yValuesCombo.setLayoutData(yValuesComboLData);
				yValuesCombo.setItems(items);
				yValuesCombo.select(1);
				yValuesCombo.addSelectionListener(new ValidSelectionAdapter());
			}
			{
				yValuesLabel = new Label(dialogShell, SWT.NONE);
				yValuesLabel.setText("Y Values:");
				FormData yValuesLabelLData = new FormData();
				yValuesLabelLData.width = 56;
				yValuesLabelLData.height = 21;
				yValuesLabelLData.left =  new FormAttachment(0, 1000, 57);
				yValuesLabelLData.top =  new FormAttachment(0, 1000, 243);
				yValuesLabel.setLayoutData(yValuesLabelLData);
				yValuesLabel.setAlignment(SWT.CENTER);
			}
			{
				yAxisLabel = new Label(dialogShell, SWT.NONE);
				yAxisLabel.setText("Y Axis label: ");
				FormData yAxisLabelLData = new FormData();
				yAxisLabelLData.left =  new FormAttachment(0, 1000, 62);
				yAxisLabelLData.top =  new FormAttachment(0, 1000, 118);
				yAxisLabelLData.width = 77;
				yAxisLabelLData.height = 21;
				yAxisLabel.setLayoutData(yAxisLabelLData);
			}
			{
				yAxisText = new Text(dialogShell, SWT.NONE);
				yAxisText.setText("Y Axis");
				FormData yAxisTextLData = new FormData();
				yAxisTextLData.left =  new FormAttachment(0, 1000, 146);
				yAxisTextLData.top =  new FormAttachment(0, 1000, 118);
				yAxisTextLData.width = 138;
				yAxisTextLData.height = 19;
				yAxisText.setLayoutData(yAxisTextLData);
			}
			{
				xAxisText = new Text(dialogShell, SWT.NONE);
				xAxisText.setText("X Axis");
				FormData xAxisTextLData = new FormData();
				xAxisTextLData.left =  new FormAttachment(0, 1000, 146);
				xAxisTextLData.top =  new FormAttachment(0, 1000, 90);
				xAxisTextLData.width = 138;
				xAxisTextLData.height = 19;
				xAxisText.setLayoutData(xAxisTextLData);
			}
			{
				chartText = new Text(dialogShell, SWT.NONE);
				chartText.setText("My Chart");
				FormData chartTextLData = new FormData();
				chartTextLData.left =  new FormAttachment(0, 1000, 146);
				chartTextLData.top =  new FormAttachment(0, 1000, 62);
				chartTextLData.width = 138;
				chartTextLData.height = 19;
				chartText.setLayoutData(chartTextLData);
			}
			{
				xAxisLabel = new Label(dialogShell, SWT.NONE);
				xAxisLabel.setText("X Axis label: ");
				FormData xAxisLabelLData = new FormData();
				xAxisLabelLData.left =  new FormAttachment(0, 1000, 62);
				xAxisLabelLData.top =  new FormAttachment(0, 1000, 90);
				xAxisLabelLData.width = 77;
				xAxisLabelLData.height = 21;
				xAxisLabel.setLayoutData(xAxisLabelLData);
			}
			{
				nameLabel = new Label(dialogShell, SWT.NONE);
				FormData nameLabelLData = new FormData();
				nameLabelLData.left =  new FormAttachment(0, 1000, 62);
				nameLabelLData.top =  new FormAttachment(0, 1000, 62);
				nameLabelLData.width = 77;
				nameLabelLData.height = 21;
				nameLabel.setLayoutData(nameLabelLData);
				nameLabel.setText("Chart Name:");
			}
			{
				xValuesCombo = new Combo(dialogShell, SWT.READ_ONLY | SWT.DROP_DOWN);
				FormData xValuesComboLData = new FormData();
				xValuesComboLData.width = 140;
				xValuesComboLData.height = 28;
				xValuesComboLData.left =  new FormAttachment(0, 1000, 141);
				xValuesComboLData.top =  new FormAttachment(0, 1000, 202);
				xValuesCombo.setLayoutData(xValuesComboLData);
				xValuesCombo.setItems(items);
				xValuesCombo.select(0);
				xValuesCombo.addSelectionListener( new ValidSelectionAdapter() );
			}
			
			
			
			{
				xValuesLabel = new Label(dialogShell, SWT.NONE);
				xValuesLabel.setText("X Values: ");
				FormData xValuesLabelLData = new FormData();
				xValuesLabelLData.width = 56;
				xValuesLabelLData.height = 28;
				xValuesLabelLData.left =  new FormAttachment(0, 1000, 57);
				xValuesLabelLData.top =  new FormAttachment(0, 1000, 208);
				xValuesLabel.setLayoutData(xValuesLabelLData);
				// Commented out: not sure if it's used, and Eclipse 3.2 becomes sad -- CM
//				xValuesLabel.setDragDetect(false);
				xValuesLabel.setAlignment(SWT.CENTER);
			}
			{
				label1 = new Label(dialogShell, SWT.SEPARATOR);
				label1.setText("label1");
				FormData label1LData = new FormData();
				label1LData.width = 399;
				label1LData.height = 14;
				label1LData.left =  new FormAttachment(0, 1000, 13);
				label1LData.top =  new FormAttachment(0, 1000, 146);
				label1.setLayoutData(label1LData);
			}
			{
				cancelButton = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
				cancelButton.setText("Cancel");
				FormData cancelButtonLData = new FormData();
				cancelButtonLData.width = 77;
				cancelButtonLData.height = 28;
				cancelButtonLData.left =  new FormAttachment(0, 1000, 287);
				cancelButtonLData.top =  new FormAttachment(0, 1000, 260);
				cancelButton.setLayoutData(cancelButtonLData);
				cancelButton.addSelectionListener(new SelectionAdapter() 
				{

					@Override
					public void widgetSelected(SelectionEvent e) {
						super.widgetSelected(e);
						dialogShell.close();
					}
					
				});
			}
			{
				okButton = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
				okButton.setText("OK");
				FormData okButtonLData = new FormData();
				okButtonLData.width = 56;
				okButtonLData.height = 28;
				okButtonLData.left =  new FormAttachment(0, 1000, 366);
				okButtonLData.top =  new FormAttachment(0, 1000, 260);
				okButton.setLayoutData(okButtonLData);
				
				okButton.addSelectionListener(new SelectionAdapter() {
	
				    @Override
				    public void widgetSelected(SelectionEvent e) {
				        super.widgetSelected(e);
				        String xColumn = xValuesCombo.getText();
				        String yColumn = yValuesCombo.getText();
				        int xIndex = Arrays.binarySearch(items, xColumn);
				        int yIndex = Arrays.binarySearch(items, yColumn);

				        double[] xValues = ((ColumnData)columns.get(xIndex)).getValues();
				        double[] yValues = ((ColumnData)columns.get(yIndex)).getValues();
				        if (dataSource == null)
				            dataSource = ((ColumnData)columns.get(yIndex)).getDataSource();

				        IChartDescriptor descriptor;
				        IChartManager chart = ChartUtils.getManager( IJavaChartManager.class );
				        switch( diagramType )
				        {
				            case SCATTER_PLOT:
				                descriptor = ChartDescriptorFactory.scatterPlotDescriptor( dataSource, xColumn, xValues, yColumn, yValues, cellselection, chartText.getText() );
				                break;
				            case LINE_PLOT:
				                descriptor = ChartDescriptorFactory.linePlotDescriptor( dataSource, xColumn, xValues, yColumn, yValues, cellselection, chartText.getText() );
				                break;
				            case TIME_SERIES:
				                descriptor = ChartDescriptorFactory.timeSeriesDescriptor( dataSource, xColumn, xValues, yColumn, yValues, cellselection, chartText.getText() );
				                break;
				            default: 
				                throw new IllegalArgumentException("Illegal value for diagramType, value was" + diagramType ); 
				        }
				        chart.plot( descriptor );
				        dialogShell.close();
				    }

				});
			}
			dialogShell.setLocation(getParent().toDisplay(100, 100));
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
		    logger.error( "Falied to open the chart dialog: "+e.getMessage() );
		}
	}
	
	private class ValidSelectionAdapter extends SelectionAdapter
	{
		@Override
		public void widgetSelected(SelectionEvent e) {
			super.widgetSelected(e);
			String xColumn = xValuesCombo.getText();
			String yColumn = yValuesCombo.getText();
			int xIndex = Arrays.binarySearch(items, xColumn);
			int yIndex = Arrays.binarySearch(items, yColumn);

			double[] xValues = ((ColumnData)columns.get(xIndex)).getValues();
			double[] yValues = ((ColumnData)columns.get(yIndex)).getValues();
			if( xValues.length != yValues.length )
			{
				okButton.setEnabled(false);
			}
			else
			{
				okButton.setEnabled(true);
			}
		}
	}
}
