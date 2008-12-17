/*******************************************************************************
 * Copyright (c) 2006, 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Egon Willighagen - core API and implementation
 *******************************************************************************/
package net.bioclipse.model;
import java.util.ArrayList;
import net.bioclipse.statistics.model.IMatrixImplementationResource;
import net.bioclipse.statistics.model.MatrixImplementationResource;
import Jama.Matrix;
public class JamaMatrix extends MatrixImplementationResource {
        private Matrix matrix = null;
        private boolean hasRowHeader, hasColHeader;
        //Column and row headers
        private ArrayList<String> colIds;
        private ArrayList<String> rowIds;
        public JamaMatrix() {
                this(0,0);
        }
        private JamaMatrix(int rows, int cols) {
                matrix = new Matrix(rows, cols);
                colIds = new ArrayList<String>(rows);
                rowIds = new ArrayList<String>(cols);
        }
        public double get(int row, int col) throws Exception {
                if (row > getRowCount()) 
                        throw new ArrayIndexOutOfBoundsException("Matrix does not have so many rows!");
                if (col > getColumnCount()) 
                        throw new ArrayIndexOutOfBoundsException("Matrix does not have so many columns!");
                return matrix.get(row-1, col-1);
        }
        public void set(int row, int col, double value) throws Exception {
                if (row < 1)
                        throw new ArrayIndexOutOfBoundsException(
                                "Index must start at 1. Found: " + row
                        );
                if (row > matrix.getRowDimension())
                        throw new ArrayIndexOutOfBoundsException(
                                "Matrix does not have so many rows! RowCount: " + getRowCount() +
                                " given row: " + row
                        );
                if (col < 1)
                        throw new ArrayIndexOutOfBoundsException(
                                "Index must start at 1. Found: " + col
                        );
                if (col > matrix.getColumnDimension())
                        throw new ArrayIndexOutOfBoundsException(
                                "Matrix does not have so many columns! ColumnCount: " + getColumnCount() +
                                " given column: " + col
                        );
                matrix.set(row-1, col-1, value);
        }
        public int getColumnCount() throws Exception {
                return matrix.getColumnDimension();
        }
        public int getRowCount() throws Exception {
                return matrix.getRowDimension();
        }
        public IMatrixImplementationResource getInstance(int rows, int cols) {
                JamaMatrix newInstance = new JamaMatrix(rows, cols);
                // copy column and row labels
                newInstance.hasColHeader = this.hasColHeader;
                newInstance.hasRowHeader = this.hasRowHeader;
                newInstance.colIds = this.colIds;
                newInstance.rowIds = this.rowIds;
                return newInstance;
        }
        public String getRowName( int index ) {
                if( !rowIds.isEmpty() && index<=rowIds.size())
                        return rowIds.get(index-1);
                return null;
        }
        public String getColumnName( int index ) {
                if( !colIds.isEmpty() && index<=colIds.size())
                        return colIds.get(index-1);
                return null;
        }
        public boolean hasColHeader() {
                return hasColHeader;
        }
        public boolean hasRowHeader() {
                return hasRowHeader;
        }
        public void setColumnName( int index, String name ) {
                hasColHeader = true;
                if (colIds == null) {
                        colIds = new ArrayList<String>();
                }
                colIds.add(index-1, name);
        }
        public void setRowName( int index, String name ) {
                hasRowHeader = true;
                if (rowIds == null) {
                        rowIds = new ArrayList<String>();
                }
                rowIds.add(index-1, name);
        }
}
