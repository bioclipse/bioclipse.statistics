/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/
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
