package net.bioclipse.model;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.jfree.chart.JFreeChart;

/**
 * Adds two methods to CTabItem to get the chart associated with this tab
 * @author Eskil Andersen
 *
 */
public class JFreeChartTab extends CTabItem {
	private JFreeChart chart;
	
	public JFreeChartTab(CTabFolder parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

	public JFreeChartTab(CTabFolder parent, int style, int index) {
		super(parent, style, index);
		// TODO Auto-generated constructor stub
	}

	public JFreeChart getChart() {
		return chart;
	}

	public void setChart(JFreeChart chart) {
		this.chart = chart;
	}
}
