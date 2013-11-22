/* *****************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 *******************************************************************************/
package net.bioclipse.statistics.model;

import org.eclipse.core.runtime.IPath;

import net.bioclipse.chart.IChartDescriptor;
import net.bioclipse.core.domain.IBioObject;

public interface IMatrixResource extends IBioObject {

    public abstract void set(int row, int col, double value);
    
    public abstract void set(int row, int col, String value);
    
    public abstract String get(int row, int col);

    public abstract int getColumnCount();

    public abstract int getRowCount();

    public abstract void setSize(int row, int col);
    
    public void triangularMatrix(int size, boolean lowerTriangular, boolean symmetric); 
    
    public abstract boolean hasRowHeader();

    public abstract boolean hasColHeader();

    public abstract String getColumnName(int index);

    public abstract String getRowName(int index);

    public abstract void setColumnName(int index, String name);

    public abstract void setRowName(int index, String name);

    public abstract String asCSV();
    
    public abstract String[] getEditorIDs();
    
    public void moveRowHeaderToColumn(int index) throws IllegalAccessException;

    public void setRowAsColumnHeader(int index) throws IllegalAccessException;

    public void moveColumnHeaderToRow(int index) throws IllegalAccessException;

    public void setColumnAsRowHeader(int index) throws IllegalAccessException;
    
    public boolean saveAs(IPath path);
    
    public boolean parseResource();
    
    public boolean parseString(String matrixStr, int columns);
    
    public boolean parseString(String matrixStr, int columns, String regex);
    
    public boolean parseCSVString(String matrixStr, int columns);
    
    public IChartDescriptor plotAsScatterPlot(String title, int xColumn, int yColumn);

    public IChartDescriptor plotAsLinePlot(String title, int xColumn, int yColumn);
    
    public IChartDescriptor plotAsTimeSerie(String title, int xColumn, int yColumn);
    
    public IChartDescriptor plotAsHistogram(String title, int column, int bins);
    
    public IChartDescriptor plotAsHistogram(String title, int[] columns, int bins);
    
}
