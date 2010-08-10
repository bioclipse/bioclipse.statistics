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

public interface IMatrixResource {

    public abstract void set(int row, int col, double value);

    public abstract String get(int row, int col);

    public abstract int getColumnCount();

    public abstract int getRowCount();

    public abstract void setSize(int row, int col);

    public abstract boolean hasRowHeader();

    public abstract boolean hasColHeader();

    public abstract String getColumnName(int index);

    public abstract String getRowName(int index);

    public abstract void setColumnName(int index, String name);

    public abstract void setRowName(int index, String name);

    public abstract String asCSV();
}