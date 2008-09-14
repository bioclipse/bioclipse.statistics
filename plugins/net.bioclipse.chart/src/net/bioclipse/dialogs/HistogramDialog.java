/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.dialogs;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.IEditorPart;

import net.bioclipse.chart.ChartUtils;

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
public class HistogramDialog extends org.eclipse.swt.widgets.Dialog {

	private Shell dialogShell;
	private Label chartNameLabel;
	private Text yAxisText;
	private Label binsLabel;
	private Button cancelButton;
	private Button okButton;
	private Text binsText;
	private Label yAxisLabel;
	private Text xAxisText;
	private Label xAxisLabel;
	private Text chartNameText;
	private double[] values;

	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Dialog inside a new Shell.
	*/
	public static void main(String[] args) {
		try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			HistogramDialog inst = new HistogramDialog(shell, SWT.NULL, new double[0]);
			inst.open(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HistogramDialog(Shell parent, int style, double[] values) {
		super(parent, style);
		this.values = values;
	}

	public void open(final IEditorPart dataSource) {
		try {
			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

			dialogShell.setLayout(new FormLayout());
			dialogShell.layout();
			dialogShell.pack();			
			dialogShell.setSize(359, 189);
			{
				cancelButton = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
				cancelButton.setText("Cancel");
				FormData cancelButtonLData = new FormData();
				cancelButtonLData.width = 76;
				cancelButtonLData.height = 28;
				cancelButtonLData.left =  new FormAttachment(0, 1000, 215);
				cancelButtonLData.top =  new FormAttachment(0, 1000, 135);
				cancelButton.setLayoutData(cancelButtonLData);
				cancelButton.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
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
				okButtonLData.left =  new FormAttachment(0, 1000, 287);
				okButtonLData.top =  new FormAttachment(0, 1000, 135);
				okButton.setLayoutData(okButtonLData);
				okButton.addSelectionListener(new SelectionAdapter() 
				{

					@Override
					public void widgetSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						super.widgetSelected(e);
						ChartUtils.histogram(values, Integer.parseInt(binsText.getText()),
								xAxisText.getText(), yAxisText.getText(), chartNameText.getText(), dataSource);
						dialogShell.close();
					}
					
				});
			}
			{
				binsText = new Text(dialogShell, SWT.NONE);
				binsText.setText("10");
				FormData binsTextLData = new FormData();
				binsTextLData.width = 108;
				binsTextLData.height = 13;
				binsTextLData.left =  new FormAttachment(0, 1000, 95);
				binsTextLData.top =  new FormAttachment(0, 1000, 89);
				binsText.setLayoutData(binsTextLData);
			}
			{
				binsLabel = new Label(dialogShell, SWT.NONE);
				binsLabel.setText("Bins: ");
				FormData binsLabelLData = new FormData();
				binsLabelLData.width = 30;
				binsLabelLData.height = 12;
				binsLabelLData.left =  new FormAttachment(0, 1000, 12);
				binsLabelLData.top =  new FormAttachment(0, 1000, 89);
				binsLabel.setLayoutData(binsLabelLData);
			}
			{
				yAxisText = new Text(dialogShell, SWT.NONE);
				yAxisText.setText("Y Axis");
				FormData yAxisTextLData = new FormData();
				yAxisTextLData.width = 108;
				yAxisTextLData.height = 13;
				yAxisTextLData.left =  new FormAttachment(0, 1000, 95);
				yAxisTextLData.top =  new FormAttachment(0, 1000, 62);
				yAxisText.setLayoutData(yAxisTextLData);
			}
			{
				yAxisLabel = new Label(dialogShell, SWT.NONE);
				yAxisLabel.setText("Y Label: ");
				FormData yAxisLabelLData = new FormData();
				yAxisLabelLData.width = 46;
				yAxisLabelLData.height = 13;
				yAxisLabelLData.left =  new FormAttachment(0, 1000, 12);
				yAxisLabelLData.top =  new FormAttachment(0, 1000, 62);
				yAxisLabel.setLayoutData(yAxisLabelLData);
			}
			{
				xAxisText = new Text(dialogShell, SWT.NONE);
				xAxisText.setText("X Axis");
				FormData xAxisTextLData = new FormData();
				xAxisTextLData.width = 108;
				xAxisTextLData.height = 13;
				xAxisTextLData.left =  new FormAttachment(0, 1000, 95);
				xAxisTextLData.top =  new FormAttachment(0, 1000, 37);
				xAxisText.setLayoutData(xAxisTextLData);
			}
			{
				xAxisLabel = new Label(dialogShell, SWT.NONE);
				xAxisLabel.setText("X Axis: ");
				FormData xAxisLabelLData = new FormData();
				xAxisLabelLData.width = 41;
				xAxisLabelLData.height = 13;
				xAxisLabelLData.left =  new FormAttachment(0, 1000, 12);
				xAxisLabelLData.top =  new FormAttachment(0, 1000, 37);
				xAxisLabel.setLayoutData(xAxisLabelLData);
			}
			{
				chartNameText = new Text(dialogShell, SWT.NONE);
				chartNameText.setText("My Chart");
				FormData chartNameTextLData = new FormData();
				chartNameTextLData.width = 108;
				chartNameTextLData.height = 13;
				chartNameTextLData.left =  new FormAttachment(0, 1000, 95);
				chartNameTextLData.top =  new FormAttachment(0, 1000, 12);
				chartNameText.setLayoutData(chartNameTextLData);
			}
			{
				chartNameLabel = new Label(dialogShell, SWT.NONE);
				chartNameLabel.setText("Chart Name: ");
				FormData chartNameLabelLData = new FormData();
				chartNameLabelLData.width = 71;
				chartNameLabelLData.height = 13;
				chartNameLabelLData.left =  new FormAttachment(0, 1000, 12);
				chartNameLabelLData.top =  new FormAttachment(0, 1000, 12);
				chartNameLabel.setLayoutData(chartNameLabelLData);
			}
			dialogShell.setLocation(getParent().toDisplay(100, 100));
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
