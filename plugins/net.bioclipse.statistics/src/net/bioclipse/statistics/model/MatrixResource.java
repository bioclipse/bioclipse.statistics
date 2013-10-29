/* *****************************************************************************
 * Copyright (c) 2006, 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Egon Willighagen
 *     Ola Spjuth
 *******************************************************************************/
package net.bioclipse.statistics.model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Scanner;
import java.util.Vector;

import net.bioclipse.chart.ChartDescriptorFactory;
import net.bioclipse.chart.ChartUtils;
import net.bioclipse.chart.IChartDescriptor;
import net.bioclipse.chart.ui.business.IChartManager;
import net.bioclipse.chart.ui.business.IJavaChartManager;
import net.bioclipse.core.domain.BioObject;
import net.bioclipse.model.ChartConstants;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.properties.IPropertySource;



/**
 * Concept of a mathematical matrix.
 * 
 * @author egonw, ospjuth
 */
public class MatrixResource extends BioObject implements IMatrixResource {

	private static final Logger logger = Logger.getLogger(MatrixResource.class);

	private static final int TAB_SCANNER = 1;
	private static final int COMMA_SCANNER = 2;
	private static final int WHITESPACE_SCANNER = 3;

	private String name;
	private boolean parsed;
	private IFileEditorInput input;
	
	public static String ID = "net.bioclipse.statistics.MatrixResource";
	
	private MatrixResourcePropertySource propSource;
	
	private IMatrixImplementationResource matrixImpl;
	
	public MatrixResource() {
	    this.name = "";
	    this.input = null;
	}
	
	public MatrixResource(String name, IFileEditorInput input) {
		this.name = name;
		this.input = input;
	}
	
	public String getName()
	{
		return name;
	}
	
	/**
	 * 
	 * @param input the input is the file resource from which the matrix will be loaded
	 */
	public void setInput(IFileEditorInput input){
		this.input = input;
	}

	public boolean isParsed()
	{
		return parsed;
	}
	
	/**
	 * Parse the resourceString into children or parsedResource object
	 * @return
	 */
	public boolean parseResource() {

		if (isParsed()) return true;	//Return true if already parsed

		logger.debug("Parsing the resource...");
		
		matrixImpl = new StringMatrix();
		if (matrixImpl != null) {
			// OK, next step: reading the input into the matrix
			try {
				
				//TODO: change loading
//				String matrixString = new String(getPersistedResource().getInMemoryResource());
				
				IFile matrixFile = input.getFile();
				BufferedReader br = new BufferedReader(new InputStreamReader(matrixFile.getContents()));
				StringBuilder matrixBuilder = new StringBuilder();
				
				while(true){
					String line = br.readLine();
					if( line == null)					
						break;
					matrixBuilder.append(line);
					matrixBuilder.append("\n");
				}
				
				parseStringIntoMatrix(matrixBuilder.toString());

				setParsedResource(matrixImpl);
				setParsed(true);
				return true;
			} catch (Exception e1) {
			    throw new RuntimeException(
			        "Exception occured when parsing the resource", e1);
			}
		}
		
		setParsed(false);
		return false;
	}
	
	/**
	 * A method for creating a matrix from a string. Observe that the row and 
	 * column headers has to be set after the matrix is created.
	 * 
	 * @param matrixStr The string to be parsed
	 * @param columns The number of columns of the matrix
	 * @param regex The String used for separating the elements
	 * @return True if the string was successfully parsed
	 */
	public boolean parseString(String matrixStr, int columns, String regex) {
	    
	    if (matrixImpl == null) 
	        matrixImpl = new StringMatrix();
	    
	    String[] matrixArray = matrixStr.split( regex );
	    String newMatrixStr = "";
	    
	    for (int i=0;i<matrixArray.length;i++) {
	        for (int j=0;j<columns;j++) {
	            newMatrixStr += matrixArray[i]+'\u002C'; 
	        }
	        
	        newMatrixStr = newMatrixStr.
	                substring( 0, (newMatrixStr.length() - 1) ) +"\n";
	    }
	    
	    parseStringIntoMatrix( newMatrixStr );
	    setParsedResource(matrixImpl);
        setParsed(true);
        
	    return true;
	}
	
	/**
	 * A method for creating a matrix from a string, the matrix values has to be
	 * separated with a space-sign. Observe that the row and 
     * column headers has to be set after the matrix is created.
     * 
     * @param matrixStr The string to be parsed
     * @param columns The number of columns of the matrix
     * @return True if the string was successfully parsed
	 */
	public boolean parseString(String matrixStr, int columns) {
	    return parseString( matrixStr, columns, "\u0020" );
	}
	
	/**
     * A method for creating a matrix from a string, the matrix values has to be
     * separated with a comma (i.e. CSV, comma separated values). Observe that the 
     * row and column headers has to be set after the matrix is created.
     * 
     * @param matrixStr The string to be parsed
     * @param columns The number of columns of the matrix
     * @return True if the string was successfully parsed
     */
	public boolean parseCSVString(String matrixStr, int columns) {
        return parseString( matrixStr, columns, "\u002C" );
    }
	
    public void setParsed(boolean b) {
		parsed = b;
	}

	private void setParsedResource(IMatrixImplementationResource parsedResource) {
		this.matrixImpl = parsedResource;
	}


	/**
	 * Scanner for tab-delimited values.
	 * @param line the input string to separate
	 * @return
	 */
	private Scanner matrixScanner(String line, int scanner_type) {

		Scanner matrixScanner;
		if (scanner_type==TAB_SCANNER){
			//Separate on tabs
	        matrixScanner = new Scanner(line).useDelimiter("\\t");       
		}
		else if (scanner_type==COMMA_SCANNER){
			//Separate on tabs
	        matrixScanner = new Scanner(line).useDelimiter(",|,\\s+");       
		}
		else if (scanner_type==WHITESPACE_SCANNER){
	        matrixScanner = new Scanner(line).useDelimiter("\\s+");       
		}
		else{
			throw new IllegalArgumentException("No supported scanner type provided");
		}
		
        matrixScanner.useLocale(Locale.US); // Assures decimal marker is a point
        
        return matrixScanner;
    }

	private void parseStringIntoMatrix(
			String matrixString) 
	{
		
		String[] matrixLines = matrixString.split("\\n");
		int matrixRows = matrixLines.length;
		int matrixCols = 0;
		
		if (matrixRows == 0) {
			logger.debug("Matrix is empty!");
			this.setSize(matrixRows, matrixCols);
		}
		
        int[] info = decideColumnSeparator( matrixLines[matrixLines.length-1] );
        int matrixScannerType = info[0];
        matrixCols = info[1];
        this.setSize(matrixRows, matrixCols);
        
        info = decideColumnSeparator( matrixLines[0] );
        if (info[1] == matrixCols) {
            for(int i = 0; i < matrixLines.length; i++ )
            {
                insertRow( i+1, matrixLines[i], matrixScannerType);
            }
            try {
                if (matrixImpl.get( 1, 1 ).isEmpty() || matrixImpl.get( 1, 1 ).matches( "\\s+" )) { 
                    matrixImpl.setColumnAsRowHeader( 1 );
                    matrixImpl.setRowAsColumnHeader( 1 );
                }
            } catch ( Exception e ) {
                logger.error( "Could not set the row and/or column header(s)" );
            }  
            
        } else if(info[1] == matrixCols-1) {
            /* The matrix is lacking the top left element, this suggest it 
             * contains both row and column headers.*/
            insertRow( 1, fixColumnHeader( matrixLines[0], matrixScannerType ),
                       matrixScannerType);            
            for(int i = 1; i < matrixLines.length; i++ )
            {
                insertRow( i+1, matrixLines[i], matrixScannerType);
            }
            try {
                matrixImpl.setRowAsColumnHeader( 1 );
                matrixImpl.setColumnAsRowHeader( 1 );
            } catch ( IllegalAccessException e ) {
                logger.error( "Could not set the row and/or column header(s)" );
            }
        } else {
            logger.error( "Matrix is misshapen: The first and last row has " +
            		"different sizes, nor does the first row seems to be a " +
            		"header" );
        }
        
		logger.debug("Parsed matrix");		
	}

	private String fixColumnHeader(String headerRow, int scannerType) {
	    switch (scannerType) {
	        case COMMA_SCANNER:
	            if (headerRow.startsWith( "," ) )
	                headerRow = headerRow.substring( headerRow.indexOf( ',' )+1 );
	            
	            return " ,"+headerRow;
	        case TAB_SCANNER:
	                return "\t"+headerRow;
	        case WHITESPACE_SCANNER:
	            return "  "+headerRow;
	        default:
	            return headerRow;
	    }
	}
	
	/**
	 * This method tries to decide what separator that was used in the source.
	 * 
	 * @param testStr A <code>String</code> for testing, e.g. the top row in the
	 *         source.  
	 * @return An array of integers, where the first element is the separator
	 *             type and the second the number of columns.
	 */
	private int[] decideColumnSeparator(String testStr) {
	    int matrixCols = 0;
	    
        //Selected scanner, null from start
        int matrixScannerType=COMMA_SCANNER;
        
        //Test using comma and whitespace as delimiters
        Scanner testScanner=matrixScanner( testStr, matrixScannerType );
        
        //Determine number of columns
        while( testScanner.hasNext() )
        {
            testScanner.next();
            matrixCols++;
        }
        
        if (matrixCols<=1){
            matrixCols=0;
            //Too few, try tab-separated
            matrixScannerType=TAB_SCANNER;
            testScanner=matrixScanner( testStr, matrixScannerType );
            
            //Determine number of columns
            while( testScanner.hasNext() )
            {
                testScanner.next();
                matrixCols++;
            }

            if (matrixCols<=1){
                matrixCols=0;
                //Too few, try whitespace-separated
                matrixScannerType=WHITESPACE_SCANNER;
                testScanner=matrixScanner( testStr, matrixScannerType );         }
            
            //Determine number of columns
            while( testScanner.hasNext() )
            {
                testScanner.next();
                matrixCols++;
            }

            if (matrixCols<=1){
                //Too few, give up
                throw new IllegalArgumentException("Could not parse file");
            }

        }

        if (matrixScannerType==COMMA_SCANNER){
            logger.debug("Comma separated scanner used");
        }
        else if (matrixScannerType==TAB_SCANNER){
            logger.debug("Tab separated scanner used");
        }
        else if (matrixScannerType==WHITESPACE_SCANNER){
            logger.debug("WHitespace separated scanner used");
        }
        
        testScanner.close();
	    
        return new int[]{matrixScannerType, matrixCols};
	}
	
	//Utility method for inserting a row into the matrix (not the column header)
	private void insertRow( int row, String rowString, int matrixScannerType )
	{
	    Scanner matrixScanner = matrixScanner(rowString, matrixScannerType);

	    int col = 1;
	    String value;
	    while( matrixScanner.hasNext() )
	    {
	        //If we have responses and this is the response column, add it
	        if (matrixImpl.hasResponseColumn() && matrixImpl.getResponseColumn()==col){
	            value = matrixScanner.next();
	            matrixImpl.setResponse( row, value );
	        }
	        else{
	            value = matrixScanner.next();
	            if (value.isEmpty())
	                value = " ";
	            try {
	                if (col > matrixImpl.getColumnCount() ) {		                
	                    // disregard data that does not fit the matrix
	                    logger.error("Found more data than can fit the matrix: too few labels?");                       
	                } else {
	                    matrixImpl.set(row, col, value);
	                }
	            } catch (Exception e) 
	            {
	                logger.error("Failed to insert " + value + "at row " + row + ", col: " +col );
	                e.printStackTrace();
	            }
	        }
	        col++;
	    }
	    try {
	        if (row == 1 && col == matrixImpl.getColumnCount() - 1 ) {
	            /* If this is true, then its likely that the matrix has both 
	             * column and row headers. */
	            String[] temp = new String[col+1];
	            temp[0]= " ";
	            for (int i = 1; i <= col;i++)
	                temp[i] = matrixImpl.get( 1, i );

	            for (int i = 0; i < temp.length; i++)
	                matrixImpl.set( 1, i+1, temp[i] );
	        }
	    } catch ( Exception e ) {
	        logger.error( "Could not set up the matrix properly: "+e.getMessage() );
	    }
	}

	/**
	 * For properties
	 * May be overridden by subclasses
	 */
	public Object getAdapter(Class adapter) {
		if (adapter ==IPropertySource.class){
			if (propSource ==null){
				propSource=new MatrixResourcePropertySource(this);
			}
			return propSource;
		}
		if (adapter == IMatrixResource.class) {
		    if (matrixImpl == null) {
		        matrixImpl = new StringMatrix();
		    }
		    return this;
		}
		
		return null;
	}

	public void set(int row, int col, double value) {
		if (matrixImpl == null) return;
		try {
			matrixImpl.set(row, col, value);
		} catch (Exception e) {
			logger.error("Could not set cell value!", e);
		}
	}
	
	   public void set(int row, int col, String value) {
	        if (matrixImpl == null) return;
	        try {
	            matrixImpl.set(row, col, value);
	        } catch (Exception e) {
	            logger.error("Could not set cell value!", e);
	        }
	    }
	
	public String get(int row, int col) {
		if (matrixImpl == null) return "null";
		try {
		    if (matrixImpl.hasResponseColumn() 
		            && col==matrixImpl.getResponseColumn()){
		        return matrixImpl.getResponse( row );
		    }
			return "" + matrixImpl.get(row, col);
		} catch (Exception e) {
			logger.error("Could not determine cell content!", e);
		}
		return "error";
	}

	public int getColumnCount() {
		if (matrixImpl == null) return 0;
		try {
			return matrixImpl.getColumnCount();
		} catch (Exception e) {
			logger.error("Could not determine the col count!");
		}
		return -1;
	}
	
	public int getRowCount() {
		if (matrixImpl == null) return 0;
		try {
			return matrixImpl.getRowCount();
		} catch (Exception e) {
			logger.error("Could not determine the row count!");
		}
		return -1;
	}
    
	public String getParsedResourceAsString() {
		Object parsedRes = this.getParsedResource();
		if (parsedRes instanceof IMatrixImplementationResource) {
			try {
				return toString((IMatrixImplementationResource)parsedRes);
			} catch (Exception e) {
				logger.error("Could not serialize the matrix to a String! " + e.getMessage(), e);
			}
		} else {
			logger.error("GetParsedResourceAsString(): Unexpected Class type: " + parsedRes.getClass().getName());
		}
		return null;
	}
	
	private Object getParsedResource() {
		return matrixImpl;
	}

	public boolean updateParsedResourceFromString(String resString) {
		try {
			read(resString);
		} catch (Exception e) {
			logger.error("UpdateParsedResourceFromString: Could not parse resString: " +
				e.getMessage(), e);
			return false;
		}
		return true;
	}

	
  	//TODO Implement save for Bioclipse 2
	public boolean save() {
		Object parseRes = this.getParsedResource();
		if (parseRes == null) {
			logger.error("Save(): Cannot save a null parsed resource!");
		} else if (parseRes instanceof IMatrixImplementationResource) {
			try {
				String result = "";
				if (matrixImpl.hasColHeader()) {
					// COLUMNS LABELS FOUND, SAVE THEM
					for (int i=0;i<matrixImpl.getColumnCount(); i++) {
						result += matrixImpl.getColumnName(i+1);
						if ((i+1)<matrixImpl.getColumnCount()) {
							result += ",";
						}
					}
					result += "\n";
				}
				result += toString((IMatrixImplementationResource) parseRes);
				byte[] byteStream = result.getBytes();
				BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(byteStream));
				
				input.getFile().setContents(bis, false, true, null);
				//There is no getPersistedResouce for this class (Bioclipse 2)
//				this.getPersistedResource().setInMemoryResource(byteStream);
//				this.getPersistedResource().save();
//				this.setParsedResourceDirty(false);
				
				return true;
			} catch (Exception e) {
				logger.error("Could not serialize the matrix to a String! " + e.getMessage(), e);
			}
		} else {
			logger.error("Save(): Unexpected Class type: " + parseRes.getClass().getName());
		}
		return false;
	}
	
	public boolean saveAs(IPath path) {
	    
	    IFile target = ResourcesPlugin.getWorkspace().getRoot().getFile( path );
	    IProgressMonitor monitor = new NullProgressMonitor();
	    monitor.beginTask("Writing file", 1);
        try {
            target.create(
                new ByteArrayInputStream(
                    this.asCSV().getBytes("US-ASCII")
                ),
                false, monitor 
            );
            monitor.worked(1);
            name = path.lastSegment();
            input = new FileEditorInput( target );
            setResource( target );
            return true;
            
        } catch (UnsupportedEncodingException e) {
           logger.error( "Could not encode the matrix: "+e.getMessage() );
        } catch ( CoreException e ) {
            logger.error( "Could not save the matrix to file: "+e.getMessage());
        } finally {
            monitor.done();
        }
	    
	    return false;
	}

	public void unLoad(){
//		super.unLoad();
		propSource.removeAdvancedProperties();
	}

	/* This method is only used to get the JammaMatrix, let's leave that stuff
	 * if we want to move the StringMatrix there and/or start using the 
	 * JamaMatrix again */
	private void findMatrixImplementation() {
		// ok, find IMatrixImplementationResource's
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint("net.bioclipse.statistics.matrixImplementation");
		
		if (extensionPoint != null) {
			logger.debug("Extension points provided by net.bioclipse.statistics: " + extensionPoint.getUniqueIdentifier());

			IExtension[] extensions = extensionPoint.getExtensions();

			logger.debug("Found # matrix implementations: " + extensions.length);
			for (int i=0; i<extensions.length; i++) {
				logger.debug("Found extension: " + extensions[i].getClass().getName());
				
				IConfigurationElement[] configelements = extensions[i].getConfigurationElements();
				for (int j=0; j<configelements.length & matrixImpl == null; j++) {
					try {
						matrixImpl = (IMatrixImplementationResource)configelements[i].createExecutableExtension("class");
						logger.info("Took the first matrix implementation: " + matrixImpl.getClass().getName());
					} catch (Exception e) {
						logger.debug(
								"Failed to instantiate factory: "
								+ configelements[j].getAttribute("class")
								+ " in type: "
								+ extensionPoint.getUniqueIdentifier()
								+ " in plugin: "
								+ configelements[j]
								                 .getDeclaringExtension().getExtensionPointUniqueIdentifier()
								                 , e);
					}
				}
			}
//			setParsedResource(matrixImpl);
		} else {
			logger.error("No matrix implementations found!");
		}		
	}

	private String toString(IMatrixImplementationResource matrix) throws Exception {
		StringBuffer buffer = new StringBuffer();
		
		for (int row=0; row<matrix.getRowCount(); row++) {
			if (matrix.getRowName(row+1) != null) {
				buffer.append(matrix.getRowName(row+1)).append(",");
			}
			
			for (int col=0; col<matrix.getColumnCount(); col++) {
				buffer.append(matrix.get(row+1, col+1));
				if (col<matrix.getColumnCount()) {
					buffer.append(",");
				}
			}
			buffer.append("\n");
		}
		buffer.append("\n");
		return buffer.toString();
	}
	
	public String toString() {
	    String result = "Matrix:\n";
	    try {
            result = matrixImpl.toString();
        } catch (Exception exception) {
            result = result + exception.getMessage();
        }
        return result;
	}

	/**
	 * This method is from the Jama library.
	 * 
	 * @param input
	 */
	private void read(String input) throws Exception {
	      StreamTokenizer tokenizer = new StreamTokenizer(
	          new StringReader(input)
	      );

	      // Although StreamTokenizer will parse numbers, it doesn't recognize
	      // scientific notation (E or D); however, Double.valueOf does.
	      // The strategy here is to disable StreamTokenizer's number parsing.
	      // We'll only get whitespace delimited words, EOL's and EOF's.
	      // These words should all be numbers, for Double.valueOf to parse.

	      tokenizer.resetSyntax();
	      tokenizer.wordChars(0,255);
	      tokenizer.whitespaceChars(0, ' ');
	      tokenizer.eolIsSignificant(true);
	      Vector v = new Vector();

	      // Ignore initial empty lines
	      while (tokenizer.nextToken() == StreamTokenizer.TT_EOL);
	      if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
	    	  // OK, found an empty matrix, that's fine
	    	  logger.debug("File is empty: creating an null matrix.");
	    	  setSize(0,0);
	          return;
	      }
	      do {
	         v.addElement(Double.valueOf(tokenizer.sval)); // Read & store 1st row.
	      } while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);

	      int n = v.size();  // Now we've got the number of columns!
	      logger.info("Input @columns: " + n);
	      double row[] = new double[n];
	      for (int j=0; j<n; j++)  // extract the elements of the 1st row.
	         row[j]=((Double)v.elementAt(j)).doubleValue();
	      v.removeAllElements();
	      v.addElement(row);  // Start storing rows instead of columns.
	      while (tokenizer.nextToken() == StreamTokenizer.TT_WORD) {
	         // While non-empty lines
	         v.addElement(row = new double[n]);
	         int j = 0;
	         do {
	            if (j >= n) throw new java.io.IOException
	               ("Row " + v.size() + " is too long.");
	            row[j++] = Double.valueOf(tokenizer.sval).doubleValue();
	         } while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);
	         if (j < n) throw new java.io.IOException
	            ("Row " + v.size() + " is too short.");
	      }
	      int m = v.size();  // Now we've got the number of rows.
	      logger.info("Input @rows: " + m);

	      double[][] A = new double[m][];
	      v.copyInto(A);  // copy the rows out of the vector
	      
	      setSize(m,n);
	      for (int i=0; i<m; i++) { // loop over rows
	    	  for (int j=0; j<n; j++) { // loop over columns
	    		  matrixImpl.set(i+1, j+1, A[i][j]);
	    	  }
	      }
	      
	      logger.debug("Done reading...");
	}
	
	public void setSize(int row, int col) {
		if (matrixImpl == null) matrixImpl = new StringMatrix();
//		    findMatrixImplementation();
		if (matrixImpl.hasResponseColumn())
		    matrixImpl = matrixImpl.getInstance(row, col, matrixImpl.getResponseColumn());
		else
        matrixImpl = matrixImpl.getInstance(row, col);
		setParsedResource(matrixImpl);
		setParsed(true);
	}
	
	public void triangularMatrix(int size, boolean lowerTriangular, boolean symmetric) {
	    if (matrixImpl == null) matrixImpl = new StringMatrix();
	    matrixImpl = matrixImpl.getInstance( size, size, lowerTriangular, symmetric );
	    setParsedResource(matrixImpl);
	    setParsed(true);
	}
	
	public String[] getEditorIDs(){
		String[] editors = new String[2];
		editors[0] = "net.bioclipse.editors.MatrixGridEditor";
		editors[1] = "net.bioclipse.editors.TextEditor";
		return editors;
	}

	public boolean hasRowHeader()
	{
		return matrixImpl.hasRowHeader();
	}
	
	public boolean hasColHeader()
	{
		return matrixImpl.hasColHeader();
	}

	public String getColumnName(int index) {
		return matrixImpl.getColumnName(index);
	}

	public String getRowName(int index) {
		return matrixImpl.getRowName(index);
	}

	public void setColumnName(int index, String name) {
		matrixImpl.setColumnName(index, name);
	}

	public void setRowName(int index, String name) {
		matrixImpl.setRowName(index, name);
	}

	public String asCSV() {
        StringBuffer buffer = new StringBuffer();
        try {
            if (matrixImpl.hasColHeader()) {
                for (int col=1; col<=getColumnCount(); col++) {

                    String colName = getColumnName(col);
                    if (colName == null) {
                        colName = "";
                    } else {
                        // colName.replaceAll("\"", "\\\"");
                    }
                    buffer.append(colName);
                    if (col<getColumnCount()) buffer.append(',');
                }
                buffer.append('\n');
            }
           
            for (int row=0; row<getRowCount(); row++) {
                if (matrixImpl.hasRowHeader())
                    if (getRowName(row+1) != null) {
                        buffer.append(getRowName(row+1)).append(',');
                    }

                for (int col=0; col<getColumnCount(); col++) {
                    buffer.append(get(row+1, col+1));
                    if (col<(getColumnCount()-1)) {
                        buffer.append(',');
                    }
                }
                buffer.append('\n');
            }
            buffer.append('\n');
        } catch (Exception exception) {
            buffer.append(exception.getMessage());
        }
        return buffer.toString();
	}
	
	public void moveRowHeaderToColumn(int index) throws IllegalAccessException {
	    matrixImpl.moveRowHeaderToColumn( index );
	}

	public void setRowAsColumnHeader(int index) throws IllegalAccessException {
	    matrixImpl.setRowAsColumnHeader( index );
	}

	public void moveColumnHeaderToRow(int index) throws IllegalAccessException {
	    matrixImpl.moveColumnHeaderToRow( index );
	}

	public void setColumnAsRowHeader(int index) throws IllegalAccessException {
	    matrixImpl.setColumnAsRowHeader( index );
	}
	
	/* TODO Should this be here (i.e. called as: myMatrix.plot...) or in the 
	 * manager (i.e. called as matrix.scatterPlot(myMatrix, "myTitle", ...) )*/
	
	public IChartDescriptor plotAsScatterPlot(String title, int xColumn, int yColumn) {
	    return createChartDescriptor( title, ChartConstants.plotTypes.SCATTER_PLOT, xColumn, yColumn );
	}
	
	public IChartDescriptor plotAsLinePlot(String title, int xColumn, int yColumn) {
	    return createChartDescriptor( title, ChartConstants.plotTypes.LINE_PLOT, xColumn, yColumn );
    }

	public IChartDescriptor plotAsTimeSerie(String title, int xColumn, int yColumn) {
	    return createChartDescriptor( title, ChartConstants.plotTypes.TIME_SERIES, xColumn, yColumn );
	}
	
	public IChartDescriptor plotAsHistogram(String title, int column, int bins) {
        int[] columns = {column};
        return this.plotAsHistogram( title, columns, bins );
    }
	
	public IChartDescriptor plotAsHistogram(String title, int[] columns, int bins) {
	    IChartDescriptor descriptor = null;
	    IChartManager chart = ChartUtils.getManager( IJavaChartManager.class );
	    int rows = getRowCount();
	    int size = columns.length*rows;
	    int valueIndex = 0;
	    double[] values = new double[size];
        Point[] originCells = new Point[size];
        for (int col:columns) {
            for (int row=1;row<=rows;row++) {
                values[valueIndex] = Double.parseDouble(this.get( row, col ));
                originCells[valueIndex++] = new Point(col, row);
            }
        }
        if (title == null)
            title = getName();
        
        descriptor = ChartDescriptorFactory.histogramDescriptor( null, "", values, "", bins, originCells, title );
        if (hasRowHeader()) {
            descriptor.setItemLabels( getRowNames() );
         }
        chart.plot( descriptor );
        return descriptor;
	}
	
	private IChartDescriptor createChartDescriptor(String title, ChartConstants.plotTypes chartType, int xColumn, int yColumn) {
	    int rows = getRowCount();
	    IChartManager chart = ChartUtils.getManager( IJavaChartManager.class );
	    double[] xValues = new double[rows];
	    double[] yValues = new double[rows];
	    Point[] originCells = new Point[rows*2];
	    IChartDescriptor descriptor = null;
	    for (int i=0;i<rows;i++) {
	        xValues[i] = Double.parseDouble( this.get( i+1, xColumn ) );
	        yValues[i] = Double.parseDouble( this.get( i+1, yColumn ) );
	        originCells[i] = new Point(xColumn, i+1);
	        originCells[i+rows] = new Point(yColumn, i+1);
	    }
	    String xLabel = "", yLabel = "";
	    if (hasColHeader()) {
	        xLabel = getColumnName( xColumn );
	        yLabel = getColumnName( yColumn );
	    }

	    switch(chartType) {
	        case SCATTER_PLOT:
	            descriptor = ChartDescriptorFactory.scatterPlotDescriptor( null, xLabel, xValues, yLabel, yValues, originCells, title );
	            break;
	        case LINE_PLOT:
                descriptor = ChartDescriptorFactory.linePlotDescriptor( null, xLabel, xValues, yLabel, yValues, originCells, title );
                break;
	        case TIME_SERIES:
                descriptor = ChartDescriptorFactory.linePlotDescriptor( null, xLabel, xValues, yLabel, yValues, originCells, title );
                break;
	        default:
	            descriptor = ChartDescriptorFactory.scatterPlotDescriptor( null, xLabel, xValues, yLabel, yValues, originCells, title ); 
	    }
	    if (hasRowHeader()) {
	       descriptor.setItemLabels( getRowNames() );
	    }
	        
	    chart.plot( descriptor );
	    
	    return descriptor;
	}
	
	private String[] getRowNames(){
	    int rows = getRowCount();
	    String[] rowNames = new String[rows];
	    for (int i=0;i<rows;i++) {
	        rowNames[i] = getRowName( i+1 );
	    }
	    
	    return rowNames;
	}
}
