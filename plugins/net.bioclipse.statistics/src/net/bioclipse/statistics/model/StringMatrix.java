package net.bioclipse.statistics.model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.eclipse.ui.activities.NotDefinedException;
/* A wrapper class for the internal matrix, contains the column and row headers
 * and hide that the matrix counts its elements form zero, instead of one. */ 
public class StringMatrix extends MatrixImplementationResource {

    private Matrix matrix = null;
    private boolean hasRowHeader, hasColHeader, hasResponseColumn; 
    private int responseColumnIndex;
    private Logger logger = Logger.getLogger( StringMatrix.class.toString() );
    
    //Column and row headers
    private ArrayList<String> colIds;
    private ArrayList<String> rowIds;
    private ArrayList<String> responseColumn;
    // Used to preserve all col-headers when using row-headers
    private String topLeftElement = "";
 // Used to preserve all row-headers when using col-headers
//    private String topElement = "";
    /**
     * A simple constructor for creating an empty matrix.
     */
    public StringMatrix() {
        this(0,0);
    }
    
    /**
     * A constructor for creating a rows x columns matrix.
     *  
     * @param rows Number of rows in the matrix
     * @param columns Number of columns in the matrix.
     */
    private StringMatrix(int rows, int columns) {
        matrix = new Matrix(rows, columns);
        colIds = new ArrayList<String>(columns);
        rowIds = new ArrayList<String>(rows);
        responseColumn = new ArrayList<String>(rows);
        hasColHeader = false;
        hasRowHeader = false;
        hasResponseColumn = false;
    }
    
    /**
     * A constructor for creating a rows x columns triangular matrix, which can
     * be symmetric.
     *  
     * @param rows Number of rows in the matrix
     * @param columns Number of columns in the matrix.
     * @param lowerTriangular True if it should be that, false if it's a 
     * upper triangular matrix.
     * @param Whether the matrix is to be considered to be symmetric or not
     */
    private StringMatrix(int rows, int columns, boolean lowerTriangular, 
                         boolean symmetric ) {
        matrix = new Matrix(rows, columns, lowerTriangular, symmetric);
        colIds = new ArrayList<String>(columns);
        rowIds = new ArrayList<String>(rows);
        responseColumn = new ArrayList<String>(rows);
        hasColHeader = false;
        hasRowHeader = false;
        hasResponseColumn = false;
    }
    
    /**
     * Makes the matrix class a singleton. Cos' the 
     * IMatrixImplementationResource demands that... 
     * 
     * @param rows Number of rows in the matrix
     * @param columns Number of columns in the matrix.
     * @return A rows x columns matrix
     */
    public IMatrixImplementationResource getInstance( int rows, int cols ) {
        StringMatrix newInstance =  new StringMatrix( rows, cols );
        // copy column and row labels
        newInstance.hasColHeader = this.hasColHeader;
        newInstance.hasRowHeader = this.hasRowHeader;
        newInstance.colIds = this.colIds;
        newInstance.rowIds = this.rowIds;
        return (IMatrixImplementationResource) newInstance;
    }
    
    /**
     * Makes the matrix class a singleton. Cos' the 
     * IMatrixImplementationResource demands that... 
     * 
     * @param rows Number of rows in the matrix
     * @param columns Number of columns in the matrix.
     * @return A rows x columns matrix
     */
    public IMatrixImplementationResource getInstance( int rows, int cols, 
                                                      boolean lowerTriangular, 
                                                      boolean symmetric ) {
        StringMatrix newInstance =  new StringMatrix( rows, cols, 
                                                      lowerTriangular, 
                                                      symmetric );
        // copy column and row labels
        newInstance.hasColHeader = this.hasColHeader;
        newInstance.hasRowHeader = this.hasRowHeader;
        newInstance.colIds = this.colIds;
        newInstance.rowIds = this.rowIds;
        return (IMatrixImplementationResource) newInstance;
    }
    
    /**
     * Get an element from the matrix. The enumeration of the rows and starts 
     * columns on one.
     * 
     * @param row The row number of the element
     * @param col The column number of the element
     * @return A {@link String} with the contents of the element
     * @throws Exception If trying to get an element outside the matrix, i.e. if
     * <code>1 <= row >= number of rows</code> or <code>1 <= col >= number of 
     * columns</code>
     */
    public String get( int row, int col ) throws Exception {
        return matrix.get( row-1, col-1 );
    }
    
    /**
     * Sets a value to an element in the matrix. The enumeration of the rows 
     * and columns starts on one.
     * 
     * @param row The row number of the element
     * @param col The column number of the element
     * @param value A <code>String</code> with value to be stored in the element
     * @throws Exception If trying to get an element outside the matrix, i.e. if
     * <code>row < 1 || row > number of rows || col < 1 || col > number of 
     * columns</code>
     */
    public void set( int row, int col, String value ) throws Exception {
        matrix.set( row-1, col-1, value );
    }
    
    /**
     * Sets a value to an element in the matrix. The enumeration of the rows 
     * and columns starts on one.
     * 
     * @param row The row number of the element
     * @param col The column number of the element
     * @param value A <code>double</code> with value to be stored in the 
     *      element. The double will be parsed to a string
     * @throws Exception If trying to get an element outside the matrix, i.e. if
     * <code>row < 1 || row > number of rows || col < 1 || col > number of 
     * columns</code>
     */
    public void set( int row, int col, double value ) throws Exception {
        matrix.set( row-1, col-1, Double.toString( value ) );
    }
    
    /* 
     * Row headers methods 
     */
    
    /**
     * Method to check if any row has a header.
     * 
     * @return <code>True</code> if any row has a header
     */
    public boolean hasRowHeader() {
        return hasRowHeader;
    }
    
    /**
     * Sets the header for a specific row.
     * 
     * @param index Number of the row
     * @param name The header, i.e. the name of the row
     * @throws IndexOutOfBoundsException If the index is outside the row, i.e. 
     * if <code>row < 1 || row > number of rows</code>
     */
    public void setRowName( int index, String name ) 
            throws IndexOutOfBoundsException {
        if (matrix.getRowDimension() >= index)
            try {
                rowIds.set( index-1, name );
            } catch( RuntimeException e) {
                // Ending up here means that this rowId hasen't been set before
                rowIds.add( index-1, name );
            }
        else
            throw new IndexOutOfBoundsException( "The matrix only has " + 
                    matrix.getRowDimension() + " rows." );
        
        hasRowHeader = true;
    }
    
    /**
     * Sets/changes name to all rows. If an element of the array in the 
     * argument  is empty, then the name of that row is not set/changed.
     * 
     * @param names An array with the names of the rows
     * @throws IndexOutOfBoundsException If <code>names</code> don't have the 
     * same number of elements as the matrix has rows.   
     */
    /* JavaSript uses arrays, so let's send in an array...*/
    public void setRowNames( String[] names ) throws IndexOutOfBoundsException {
        // (names.length != rowIds.size() && !rowIds.isEmpty())
        try {
            if (names.length != getRowCount() && !rowIds.isEmpty())
                throw new IndexOutOfBoundsException( "The inserted row must " +
                        "have the same number of elemnt as the rows has: " + 
                        names.length + "!=" + getRowCount() );
        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        int i = 0;
        if (hasColHeader) 
            i=1;
        
        for (; i < names.length; i++) 
            if (!names[i].isEmpty())
                if (!hasRowHeader)
                    rowIds.add( names[i] );
                else
                    rowIds.set( i, names[i] );
        
        hasRowHeader = true;
    }
    
    /**
     * Gets the name of a row.
     *  
     * @param index The number of the row
     * @return A <code>String</code> with the name of the row
     * @throws IndexOutOfBoundsException If the index is outside the row, i.e. 
     * if <code>row < 1 || row > number of rows</code>
     */
    public String getRowName( int index ) throws IndexOutOfBoundsException {
        if (index <= matrix.getRowDimension() && index > 0 )
            if (index > rowIds.size())
                return "";
            else 
                return rowIds.get( index-1 );
        else
            throw new IndexOutOfBoundsException( "The matrix only has " + 
                    matrix.getRowDimension() + " rows." );
    }
    
    /**
     * Gets the index of the row with the name <code>rowName</code>.
     * @param rowName The name of the row
     * @return The number of the row.
     * @throws NoSuchElementException If now row has the name <code>rowName</code>
     */
    public int getRowIndex(String rowName) throws NoSuchElementException {
        if (!rowIds.contains( rowName ))
            throw new NoSuchElementException( "There is no row named: " + 
                    rowName );
        
        return rowIds.indexOf( rowName );
    }
    
    /**
     * Removes the row header and put it in the column <code>index</code>. If 
     * <code>index</code> is set to <code>null</code> the row header will be 
     * removed.
     * 
     * @param index Where to put the header column
     * @throws IllegalAccessException If the matrix don't have a row header
     */
    @Override
    public void moveRowHeaderToColumn(int index) throws IllegalAccessException {
        if (!hasRowHeader)
            // TODO Or should I just let it be and do nothing?
            throw new IllegalAccessException( "The matrix do not has any row " +
                    "header." );
        
        if (hasColHeader) {
            colIds.add( index-1, topLeftElement );
            topLeftElement = "";
        }
        
        addColumn( index, rowIds.toArray( new String[rowIds.size()]) );
        rowIds = new ArrayList<String>();
        hasRowHeader = false;
    }
    
    /* 
     * Row methods 
     */
    /**
     * Gets the number of rows in the matrix.
     * 
     * @return Returns the number of rows in the matrix
     * @throws Exception
     */
    public int getRowCount() throws Exception {
        return matrix.getRowDimension();
    }
    
    /**
     * Get the contents of the row with number <code>row</code>. If the row has 
     * a header it's in the first element.
     * @param row The index of the wanted row
     * @return An <code>String[]</code> with the values of the row
     * @throws IndexOutOfBoundsException If the index is outside the row, i.e. 
     * if <code>row < 1 || row > number of rows</code>
     */
    /* JavaSript uses arrays, so let's return an array...*/
    public String[] getRow(int row) {
        return getRow( row, hasRowHeader );
    }
    
    /**
     * Gets the content of the row with the name <code>rowName>/code>. If the 
     * row has a header it's in the first element.
     * @param rowName The name of the wanted row
     * @return An <code>String[]</code> with the values of the row
     * @throws NoSuchElementException If the matrix don't have a row with that 
     * name 
     */
    /* JavaSript uses arrays, so let's return an array...*/
    public String[] getRow(String rowName) throws NoSuchElementException {
        if (!rowIds.contains( rowName ))
            throw new NoSuchElementException( "There is no row named: " + 
                    rowName );
        
        int row = rowIds.indexOf( rowName );
        return getRow( row, true );
    }
    
    /**
     * Get the contents of the row with number <code>row</code>.
     * @param row The index of the wanted row
     * @param includeRowName <code>True</code> if the header should be included
     * @return An <code>String[]</code> with the values of the row
     * @throws IndexOutOfBoundsException If the index is lager than the number 
     * of rows, i.e. if <code>row < 1 || row > number of rows</code>
     */
    /* JavaSript uses arrays, so let's return an array...*/
    public String[] getRow(int row, boolean includeRowName)  
            throws IndexOutOfBoundsException {
        List<String> matrixRow = matrix.getRow( row - 1 );
        
        if (hasRowHeader && includeRowName) 
            matrixRow.add( 0, getRowName( row - 1 ) );
        
        return matrixRow.toArray( new String[matrixRow.size()] );
    }
    
    /**
     * Adds an empty row to the end of the matrix. 
     */
    public void IncreaseMatrixWithOneRow() {
        matrix.addRow();
        rowIds.add( "" );
    }
    
    /**
     * Adds a row to the end of the matrix.
     * @param row An array with the values of the row.
     * @throws Exception If the length of the argument array isn't the same as 
     * the number of columns in the matrix.
     * 
     */
    /* JavaSript uses arrays, so let's send in an array...*/
    public void addRow(String[] row) throws Exception {
        addRow( getRowCount(), row );
    }
    
    /**
     * Adds a new row at the position <code>row</code> and shifts the rows with 
     * that index or higher one position up.<br/>
     * If <code>row</code> is lager than the number of rows in the matrix it's 
     * added as the last row.<br/>
     * If the array <code>row</code> is one lager than the number of columns 
     * in the matrix the first element is considered to be the name of the row. 
     * @param index The index where the new row should be
     * @param row An array with the values of the new row
     * @throws IndexOutOfBoundsException If the array <code>row</code> is to 
     * large, i.e. if the length of <code>row</code> is lager than the number 
     * of columns + 1
     */
    /* JavaSript uses arrays, so let's send in an array...*/
    public void addRow(int index, String[] row) throws IndexOutOfBoundsException {
        ArrayList<String> newRow = new ArrayList<String>();
        int i = 0;
        if (row.length == matrix.getColumnDimension()+1) {
            i = 1;
            rowIds.add( index-1, row[0] );
            hasColHeader = true;
        }           
            
        for (;i<row.length;i++)
            newRow.add( row[i] );
        
        matrix.addRow( index-1, newRow );
        
//        if (matrix.getRowDimension() > rowIds.size())
//            rowIds.add( "" );
           
    }
    
    /**
     * Sets the row <code>index</code> as (new) column header.
     * 
     * @param index The index of the row to become new header
     * @throws IllegalAccessException If the matrix already has a column header
     */
    @Override
    public void setRowAsColumnHeader(int index) throws IllegalAccessException {
        if (hasColHeader)
            // TODO Or should I just put the old header on the line where the new header was?
            throw new IllegalAccessException( "The matrix already has a " +
            		"header." );
        
        String[] cHeader = removeRow( index-1 );
        if (hasRowHeader)         
            topLeftElement = cHeader[0];

        setColumnNames( cHeader );
    }
    
    /**
     * Removes the row with the name <code>rowName</code>
     * @param rowName The name of the row to be removed
     * @return The removed row
     * @throws NoSuchElementException If the matrix don't have a row with that 
     * name 
     */
    /* JavaSript uses arrays, so let's return an array...*/
    public String[] removeRow(String rowName) throws NoSuchElementException {        
        return removeRow( getRowIndex( rowName ) );
    }
    
    /**
     * Removes the row with the index <code>row</code>.
     * @param row The index of the row to be removed
     * @return The removed row
     * @throws IndexOutOfBoundsException If the index is lager than the number 
     * of rows, i.e. if <code>row < 1 || row > number of rows</code>
     */
    /* JavaSript uses arrays, so let's return an array...*/
    public String[] removeRow(int row) throws IndexOutOfBoundsException {
        ArrayList<String> oldRow = matrix.removeRow( row );
        
        if (hasRowHeader)
            oldRow.add( 0, rowIds.remove( row ) );
        
        return oldRow.toArray( new String[oldRow.size()] );
        
    }
    
    /* 
     * Column headers methods
     */
    
    /**
     * Method to check if any column has a header.
     * 
     * @return <code>True</code> if any row has a header
     */
    public boolean hasColHeader() {
        return hasColHeader;
    }
    
    /**
     * Sets/changes name for all columns. If an element of the array in the 
     * argument is empty, then the name of that column is not set/changed. 
     * 
     * @param names An array with the names of the columns
     * @throws IndexOutOfBoundsException If <code>names</code> don't have the 
     * same number of elements as the matrix has columns.   
     */
    public void setColumnName( int index, String name ) 
            throws IndexOutOfBoundsException {
        if (matrix.getColumnDimension() >= index) {
            try {
                colIds.set( index-1, name );
            } catch( RuntimeException e) {
                // Ending up here means that this colId hasen't been set before
                colIds.add( index-1, name );
            }
            hasColHeader = true;
        } else
            throw new IndexOutOfBoundsException( "The matrix only has " + 
                    matrix.getColumnDimension() + " columns." );
    }

    /**
     * Gets the name of a column.
     *  
     * @param index The index of the column
     * @return A <code>String</code> with the name of the row
     * @throws IndexOutOfBoundsException If the index is outside the column,
     *  i.e. if <code>index < 1 || index > number of columns</code>
     */
    public String getColumnName( int index ) {  
        if (!hasColHeader)
            throw new IllegalAccessError( "The matrix has no header" );
        if (index <= matrix.getColumnDimension() && index > 0 )
            return colIds.get( index-1 );
        else
            throw new IndexOutOfBoundsException( "The matrix only has " + 
                    matrix.getColumnDimension() + " columns. " + index );
    }
    
    /**
     * Sets/changes name to all columns. If an element of the array in the 
     * argument is empty, then the name of that column is not set/changed.
     * 
     * @param names An array with the names of the columns
     * @throws IndexOutOfBoundsException If <code>names</code> don't have the 
     * same number of elements as the matrix has columns.   
     */
    /* JavaSript uses arrays, so let's send in an array...*/
    public void setColumnNames( String[] names ) {
        if (names.length != colIds.size() && !colIds.isEmpty() )
            throw new IndexOutOfBoundsException( "The inserted row must " +
                    "have the same number of elemnt as the columns has: " + 
                    names.length + "!=" + colIds.size() );

        int i = 0;
        if (hasRowHeader)
            i = 1;
        
        for (; i < names.length; i++) 
            if (!names[i].isEmpty())
                if (!hasColHeader)
                    colIds.add( names[i] );
                else
                    colIds.set( i, names[i] );

        hasColHeader = true;
    }
        
    /**
     * Gets the index of the column with the name <code>columnName</code>.
     * @param columnName The name of the column
     * @return The number of the column.
     * @throws NoSuchElementException If now column has the name 
     * <code>columnName</code>
     */
    public int getColumnIndex(String columnName) {
        if (!colIds.contains( columnName ))
            throw new NoSuchElementException( "There is no column named: " + 
                    columnName );
        
        return colIds.indexOf( columnName );
    }
    
    /**
     * Removes the column header and put it in the row <code>index</code>. If 
     * <code> index</code> is set to <code>null</code> the column header will be 
     * removed.
     * 
     * @param index Where to put the header row
     * @throws IllegalAccessException If the matrix don't have a column header
     */
    @Override
    public void moveColumnHeaderToRow(int index) throws IllegalAccessException {
        if (!hasColHeader)
            // TODO Or should I just let it be and do nothing?
            throw new IllegalAccessException( "The matrix do not has any " +
                    "column header." );
        
        if (hasRowHeader && !topLeftElement.isEmpty()) {
            rowIds.add( index-1, topLeftElement );
            topLeftElement = "";
        }
        
        addRow( index, colIds.toArray( new String[colIds.size()] ) );
        colIds = new ArrayList<String>();
        hasColHeader = false;
    }
    
    /*
     *  Column methods
     */
    /**
     * Gets the number of columns in the matrix.
     * 
     * @return Returns the number of columns in the matrix
     * @throws Exception
     */
    public int getColumnCount() throws Exception {
        return matrix.getColumnDimension();
    }
    
    /**
     * Get the contents of the column with number <code>column</code>. If the 
     * column has a header it's in the first element.
     * @param column The index of the wanted column
     * @return An <code>String[]</code> with the values of the column 
     * @throws IndexOutOfBoundsException If the index is outside the column,
     *  i.e. if <code>column < 1 || column > number of rows</code>
     */
    /* JavaSript uses arrays, so let's return an array...*/
    public String[] getColumn(int column) {
        return getColumn( column, hasColHeader );
    }
    
    /**
     * Gets the content of the row with the name <code>columnName>/code>. If the 
     * row has a header it's in the first element.
     * @param columnName The name of the wanted column
     * @return An <code>String[]</code> with the values of the column
     * @throws NoSuchElementException If the matrix don't have a column with 
     * that name 
     */
    /* JavaSript uses arrays, so let's return an array...*/
    public String[] getColumn(String columnName) {
        if (!colIds.contains( columnName ))
            throw new NoSuchElementException( "There is no column named: " + 
                    columnName );
        
        int column = colIds.indexOf( columnName );
        return getColumn( column, hasColHeader );
    }
    
    /**
     * Get the contents of the column with number <code>column</code>.
     * @param column The index of the wanted column
     * @param includeColumnName <code>True</code> if the header should be included
     * @return An <code>String[]</code> with the values of the column
     * @throws IndexOutOfBoundsException If the index is lager than the number 
     * of columns, i.e. if <code>column < 1 || column > number of columns</code>
     */
    /* JavaSript uses arrays, so let's return an array...*/
    public String[] getColumn(int column, boolean includeColumnName) {
        List<String> matrixColumn = matrix.getColumn( column - 1 );
        
        if (hasColHeader && includeColumnName) 
            matrixColumn.add( 0, getColumnName( column - 1 ) );
        
        return matrixColumn.toArray( new String[matrixColumn.size()] );
    }
    
    /**
     * Adds an empty column to the end of the matrix. 
     */
    public void IncreaseMatrixWithOneColumn() {
        matrix.addColumn();
        colIds.add( "" );
    }
    
    /**
     * Adds a columns to the end of the matrix.
     * @param column An array with the values of the column.
     * @throws Exception If the length of the argument array isn't the same as 
     * the number of rows in the matrix.
     * 
     */
    /* JavaSript uses arrays, so let's send in an array...*/
    public void addColumn(String[] column) throws Exception {
        addColumn( getColumnCount(), column );
    }
    
    /**
     * Adds a new column at the <code>index</code> and shifts the rows with that
     *  index and higher one position up.<br/>
     * If the <code>index</code> is lager than the number of columns in the 
     * matrix it's added as the last column.<br/>
     * If the array <code>column</code> is one lager than the number of columns 
     * in the matrix then first element is considered to be the name of the row. 
     * @param index The index of the new column
     * @param column An array with the values of the new column
     * @throws IndexOutOfBoundsException If the array <code>column</code> is to 
     * large, i.e. if the length of columns is lager than the number of columns 
     * + 1
     */
    /* JavaSript uses arrays, so let's send in an array...*/
    public void addColumn(int index, String[] column) {
        ArrayList<String> newColumn = new ArrayList<String>();
        int i = 0;
        if (column.length == matrix.getRowDimension()+1) {
            i = 1;
            colIds.add( index-1, column[0] );
            hasColHeader = true;
        }
//        if (hasRowHeader && !topLeftElement.isEmpty()) {
//            newColumn.add( topLeftElement );
//        }
            
        for (;i<column.length;i++)
            newColumn.add( column[i] );
        
        matrix.addColumn( index-1, newColumn );
        
//        if (matrix.getRowDimension() > rowIds.size())
//            rowIds.add( index, "" );
           
    }
    
    /**
     * Removes the column with the name <code>columnName</code>
     * @param columnName The name of the row to be removed
     * @return The removed column
     * @throws NoSuchElementException If the matrix don't have a column with 
     * that name 
     */
    /* JavaSript uses arrays, so let's return an array...*/
    public String[] removeColumn(String columnName) {
        return removeRow( getColumnIndex( columnName ) );
    }
    
    /**
     * Removes the column with the index <code>column</code>.
     * @param column The index of the column to be removed
     * @return The removed column
     * @throws IndexOutOfBoundsException If the index is lager than the number 
     * of columns, i.e. if <code>column < 1 || column > number of column</code>
     */
    /* JavaSript uses arrays, so let's return an array...*/
    public String[] removeColumn(int column) {
        ArrayList<String> oldColumn = matrix.removeColumn( column-1 );
        
        if (hasColHeader)
            oldColumn.add( 0, colIds.remove( column-1 ) );
        
        return oldColumn.toArray( new String[oldColumn.size()] );
        
    }
    
    /**
     * Sets the column <code>index</code> as (new) row header.
     * 
     * @param index The index of the row to become new header
     * @throws IllegalAccessException If the matrix already has a row header
     */
    @Override
    public void setColumnAsRowHeader(int index) throws IllegalAccessException {
        if (hasRowHeader)
            // TODO Or should I just put the old header on the line where the new header was?
            throw new IllegalAccessException( "The matrix already has a row " +
                    "header." );
        
        String[] rHeader = removeColumn( index );
        
        if (hasColHeader) 
            topLeftElement = rHeader[0];

        setRowNames( rHeader );
    }
    
    /*
     * Response column methods
     * This methods is implemented by the class MatrixImplementationResource, 
     * This column is used by QUSAR. */

    public boolean hasResponseColumn() {
        return hasResponseColumn;
    }

    public void setHasResponseColumn( boolean hasResponse ) {
       this.hasResponseColumn = hasResponse;
    }

    public void setResponseColumn( int index ) {
        setHasResponseColumn( true );
        this.responseColumnIndex = index;
    }

    public int getResponseColumn() {
        return responseColumnIndex;
    }

    public void setResponse( int row, String value ) {
        if (responseColumn==null){
            responseColumn=new ArrayList<String>();
        }
        responseColumn.add( row-1, value );
    }

    public String getResponse( int row ) throws IndexOutOfBoundsException {
        if (row < 1 || row > responseColumn.size() )
            throw new IndexOutOfBoundsException( "The response column only " +
            		"has " + responseColumn.size() + " rows and starts on 1");
        return responseColumn.get( row-1 );
    }

    public IMatrixImplementationResource getInstance( int row, int col,
                                                      int responseColumn ) 
                                             throws IndexOutOfBoundsException {
        
        IMatrixImplementationResource newInstance = this.getInstance(row, col);
        
        if (responseColumn > col)
            throw new IndexOutOfBoundsException( "The matrix only has " + col +
                                                 " columns" );
        newInstance.setResponseColumn( responseColumn );
                
        return newInstance;
    }
    
    public String toString() {
        StringBuffer matrixStr = new StringBuffer();
        final String TAB = "\t";//"\u0009";
        final String COMMA = "\u002C\u0020";
        final String NEW_ROW = "\n";
        int rows, columns;
        
        try {
            rows = getRowCount();
            columns = getColumnCount();
        } catch ( Exception e1 ) {
            logger.error( "Could not get the number of rows or columns " +
            		"right: " + e1.getMessage() );
            return "Error: " + e1.getMessage();
        }
        
        if (hasColHeader) {
            if (hasRowHeader) {
                String topHeader = getRowName( 1 );
                for (int i=0;i<topHeader.length()%4;i++)
                    matrixStr.append( TAB );
            }
            for(String cHeader:colIds)
                matrixStr.append( cHeader ).append( TAB );
            // Let's remove the last tab on the row.
            matrixStr.replace( matrixStr.length()-1, matrixStr.length(),
                               NEW_ROW );
        }
        try {
            for (int r = 1; r <= rows ; r++) {
                if (hasRowHeader)
                    matrixStr.append( getRowName( r ) ).append( TAB );
                if (matrix.isLowerTriangular)
                    for (int c=1; c <= r; c++)
                        matrixStr.append( get(r,c) ).append( COMMA );
                else
                    for (int c=1; c <= columns; c++)
                        matrixStr.append( get(r,c) ).append( COMMA );
                // Let's remove the last ", " from the the row, add a new row
                matrixStr.replace( matrixStr.length()-2, matrixStr.length(),
                                   NEW_ROW ); 
            }
        } catch ( Exception e ) {
            logger.error( "Something went wrong when going through the " +
            		"matrix: " + e.getMessage() );
            return "Error: " + e.getMessage();
        }
        
        return matrixStr.toString();
    }
    
    /**
     * This is the internal matrix. It only has the values of the matrix itself,
     * headers etc is don by it's wrapper class.
     *  
     * @author klasjonsson
     *
     */
    private class Matrix {
        
        private ArrayList<ArrayList<String>> matrix;
        private boolean isLowerTriangular = false, isUpperTriangular = false, 
                isSymmetric = false;
        
        /**
         * Creates an 0x0 matrix.
         */
//        public Matrix() {
//            matrix = new ArrayList<ArrayList<String>>();
//        }
        
        /**
         * Creates a <code>rows x cols</code> matrix.
         * 
         * @param rows The number of rows in the matrix
         * @param cols The number of columns in the matrix
         */
        public Matrix(int rows, int cols) {
            matrix = new ArrayList<ArrayList<String>>();
            ArrayList<String> row;
            for (int i = 0;i<rows;i++) {
                row = new ArrayList<String>();
                for (int j = 0; j<cols;j++) {
                    row.add( "" );
                }
                matrix.add( row );
            }
        }
        
        /**
         * Creates a <code>rows x cols</code> triangular matrix, which can
         * be symmetric.
         * 
         * @param rows The number of rows in the matrix
         * @param cols The number of columns in the matrix
         * @param lowerTriangular True if it should be that, false if it's a 
         * upper triangular matrix.
         * @param Whether the matrix is to be considered to be symmetric or not
         * @throws NotDefinedException 
         * 
         */
        public Matrix(int rows, int cols, boolean lowerTriangular, 
                      boolean symmetric)  {
            matrix = new ArrayList<ArrayList<String>>();
            
            if (lowerTriangular)
                this.isLowerTriangular = true;
            else
                this.isUpperTriangular = true;
            
            this.isSymmetric = symmetric;
            
            ArrayList<String> row;
            for (int i = 0;i<rows;i++) {
                row = new ArrayList<String>();
                if (lowerTriangular)
                    for (int j = 0; j<i+1;j++) { 
                        row.add( "" );
                    }
                else {
                    for (int j = 0; j<cols-i;j++) {
                        row.add( "" );
                    } 
                }
                matrix.add( row );
            }
        }
        
        /**
         * Gets the element on row <code>row</code> and in the column 
         * <code>column</code>.
         *  
         * @param row The row of the wanted element 
         * @param col The column of the wanted element
         * @return A <code>String</code> containing the wanted element
         * @throws IndexOutOfBoundsException If <code>row</code> and/or 
         * <code>column</code> is outside the matrix, i.e. if <code>row < 0 || 
         * row >= number of rows || col < 0 || col >= number of columns</code>
         */
        public String get(int row, int col) throws IndexOutOfBoundsException {

            int wantedSize;

            if (row<getRowDimension() ) {
                if (isLowerTriangular) {
                    wantedSize = matrix.get( row ).size();
                    if (col<wantedSize)
                        return matrix.get( row ).get( col );
                    else {
                        if (isSymmetric) {
                            if (col<getRowDimension())
                                return matrix.get( col ).get( row );
                            else
                                throw new IndexOutOfBoundsException( "A " +
                                        "symmetric lower triangular matrix " +
                                        "should not have a value at [" + row + 
                                        ", " + col + "].");  
                        } else 
                            throw new IndexOutOfBoundsException( "A " +
                                    "unsymmetric lower triangular matrix do " +
                                    "not have a value at [" + row + ", " + col +
                                    "].");  
                    }
                } else if (isUpperTriangular) {
                    wantedSize = getColumn( col ).size();
                    if (row<wantedSize) {
                        return matrix.get( row ).get( col );
                    } else {
                        if (isSymmetric) {
                            if(row<getColumnDimension())
                                return matrix.get( col ).get( row );
                            else
                                throw new IndexOutOfBoundsException( "A " +
                                        "symmetric upper triangular matrix " +
                                        "should not have a value at [" + row + 
                                        ", " + col + "].");  
                        } else 
                            throw new IndexOutOfBoundsException( "A " +
                                    "unsymmetric upper triangular matrix do " +
                                    "not have a value at [" + row + ", " + col +
                                    "]."); 
                    }

                } else if (row<getRowDimension() && col<getColumnDimension())
                    return matrix.get( row ).get( col );
            }
            throw new IndexOutOfBoundsException( "Outside matrix (matrix " +
                    "dimension: [" + getRowDimension() + "," + 
                    getColumnDimension() + "]" );
        }
        
        
        /**
         * Replace the value of the element on row <code>row</code> and in the 
         * column <code>column</code>.
         * @param row The row of the element 
         * @param col The column of the element 
         * @param value The new value of the element
         * @return The previous value of the element
         * @throws IndexOutOfBoundsException If <code>row</code> and/or 
         * <code>column</code> is outside the matrix, i.e. if <code>row < 0 || 
         * row >= number of rows || col < 0 || col >= number of columns</code>
         */
        public String set(int row, int col, String value) 
                throws IndexOutOfBoundsException {
            int wantedSize;
            
            if (row<getRowDimension() ) {
                if (isLowerTriangular) {
                    wantedSize = matrix.get( row ).size();
                    if (col<wantedSize)
                        return matrix.get( row ).set( col, value );
                    else {
                        if (isSymmetric) {
                            if (col<getRowDimension())
                                return matrix.get( col ).set( row, value );
                            else
                                throw new IndexOutOfBoundsException( "A " +
                                		"symmetric lower triangular matrix " +
                                		"should not have a value at [" + row + 
                                		", " + col + "].");  
                        } else 
                            throw new IndexOutOfBoundsException( "A " +
                            		"unsymmetric lower triangular matrix do " +
                                    "not have a value at [" + row + ", " + col +
                                    "].");  
                    }
                } else if (isUpperTriangular) {
                    wantedSize = getColumn( col ).size();
                    if (row<wantedSize) {
                        return matrix.get( row ).set( col, value );
                    } else {
                        if (isSymmetric) {
                            if(row<getColumnDimension())
                                return matrix.get( col ).set( row, value );
                            else
                                throw new IndexOutOfBoundsException( "A " +
                                        "symmetric upper triangular matrix " +
                                        "should not have a value at [" + row + 
                                        ", " + col + "].");  
                        } else 
                            throw new IndexOutOfBoundsException( "A " +
                                    "unsymmetric upper triangular matrix do " +
                                    "not have a value at [" + row + ", " + col +
                                    "]."); 
                    }
                    
                } else if (col<getColumnDimension())
                    return matrix.get( row ).set( col, value );   
            }    
            
            throw new IndexOutOfBoundsException( "Outside matrix (matrix " +
            		"dimension: [" + getRowDimension() + "," + 
                    getColumnDimension() + "]" );
        }
        
        /**
         * Get the number of columns of the matrix.
         * 
         * @return The number of columns
         */
        public int getColumnDimension() {
            if (!isLowerTriangular)
                return matrix.get( 0 ).size();
            else
                return matrix.get( getRowDimension()-1 ).size();
        }
        
        /**
         * Get the number of rows of the matrix.
         * 
         * @return The number of rows
         */
        public int getRowDimension() {
            return matrix.size();
        }
        
        /**
         * Make the matrix one (empty) row lager.
         */
        public void addRow() {
            ArrayList<String> row = new ArrayList<String>();
            for (int j = 0; j<getColumnDimension();j++) {
                row.add( "" );
            }
            matrix.add( row );
        }
 
        /**
         * Adds a new row at the index <code>rowNumber</code>.
         * 
         * @param rowNumber The index where the new row is placed
         * @param row The contents of the new row
         * @throws IndexOutOfBoundsException If rowNumber is lager than the 
         * number of rows in the matrix
         */
        public void addRow(int rowNumber, ArrayList<String> row) 
                throws IndexOutOfBoundsException {
            if (row.size() != getColumnDimension())
                throw new IndexOutOfBoundsException( "The inserted row must " +
                		"have the same number of elemnt as the other rows: " + 
                        row.size() +"!=" + getColumnDimension() );
            
            if (rowNumber < getRowDimension() )
                matrix.add( rowNumber, row );
            else
                matrix.add( row ); 
        }
        
        /**
         * Make the matrix one (empty) column lager.
         */
        public void addColumn() {
            for (int j = 0; j<getRowDimension();j++) {
                matrix.get( j ).add( "" );
            }
        }
        
        /**
         * Adds a new column at the index <code>columnNumber</code>.
         * 
         * @param rowNumber The index where the new column is placed
         * @param col The contents of the new column
         * @throws IndexOutOfBoundsException If columnNumber is lager than the 
         * number of columns in the matrix
         */
        public void addColumn(int columnNumber, ArrayList<String> col) 
                throws IndexOutOfBoundsException {

            if (col.size() != getRowDimension() )
                throw new IndexOutOfBoundsException( "The inserted column " +
                		"must have the same number of elemnt as the other " +
                		"columns: " + col.size() +"!=" + getRowDimension() );
            
            if (columnNumber < getColumnDimension() ) 
                for (int i = 0;i < getRowDimension();i++)
                    matrix.get( i ).add( columnNumber, col.get( i ) );
            else                
                for (int i = 0;i < getRowDimension();i++)
                    matrix.get( i ).add( col.get( i ) );
        }
            
        /**
         * Get the contents of row with the index <code>row</code>
         * @param row The index of the wanted row
         * @return The contents of the row
         * @throws IndexOutOfBoundsException If rowNumber is lager than the 
         * number of rows in the matrix
         */
        public ArrayList<String> getRow(int row) 
                throws IndexOutOfBoundsException {
            if (row >= getRowDimension())
                throw new IndexOutOfBoundsException( "The matrix only has " + 
                    getRowDimension() + " rows." );
            
            return matrix.get( row );
        }
        
        /**
         * Get the contents of column with the index <code>col</code>
         * @param col The index of the wanted column
         * @return The contents of the column
         * @throws IndexOutOfBoundsException If columnNumber is lager than the 
         * number of columns in the matrix
         */
        public ArrayList<String> getColumn(int col) 
                throws IndexOutOfBoundsException{
            if (col >= getColumnDimension())
                throw new IndexOutOfBoundsException( "The matrix only has " + 
                        getColumnDimension() + " columns." );
            
            ArrayList<String> column = new ArrayList<String>(getRowDimension());
            for (int i = 0;i<getRowDimension();i++)
                column.add( matrix.get( i ).get( col ) );
                
            return column;
        }
        
        /**
         * Removes row with the index <code>row</code>
         * @param row The index of the row to be removed
         * @return The contents of the removed row
         * @throws IndexOutOfBoundsException If rowNumber is lager than the 
         * number of rows in the matrix
         */
        public ArrayList<String> removeRow(int row) 
                throws IndexOutOfBoundsException {
            if (row >= getRowDimension())
                throw new IndexOutOfBoundsException( "The matrix only has " + 
                    getRowDimension() + " rows." );
            
            return matrix.remove( row );
        }
        
        /**
         * Removes column with the index <code>col</code>
         * @param col The index of the column to be removed
         * @return The contents of the removed column
         * @throws IndexOutOfBoundsException If columnNumber is lager than the 
         * number of columns in the matrix
         */
        public ArrayList<String> removeColumn(int col) 
                throws IndexOutOfBoundsException {
            if (col >= getColumnDimension())
                throw new IndexOutOfBoundsException( "The matrix only has " + 
                        getColumnDimension() + " columns." );
            
            ArrayList<String> column = new ArrayList<String>(getRowDimension());
            for (int i = 0;i<getRowDimension();i++)
                column.add( matrix.get( i ).remove( col ) );
                
            return column;
        }
        
        /**
         * This for to telling the matrix it's symmetric. I.e. that if it is
         * set as triangular then it can assume that the values in the other  
         * part is the same as in the existing.
         *  
         * @param symmetric 
         */
        public void assumeMatrixSymmetric(boolean symmetric) {
           this.isSymmetric = symmetric;
        }
        
        /**
         * This method tells if the matrix is has been set as symmetric.
         * 
         * @return If the matrix is symmetric or not
         */
        public boolean isMatrixSymmetric() {
            return this.isSymmetric;
        }
        
        /**
         * Returns info if the matrix is created as a lower triangular matrix.
         * 
         * @return If the matrix is lower triangular
         */
        public boolean isMatrixLowerTriangular() {
            return this.isLowerTriangular;
        }
        
        /**
         * Returns info if the matrix is created as a upper triangular matrix.
         * TODO Support this as well as lower triangular
         * @return If the matrix is upper triangular
         */
//        public boolean isMatrixUpperTriangular() {
//            return this.isUpperTriangular;
//        }
    }

}
