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
import java.util.Locale;
import java.util.Scanner;
import java.util.Vector;

import net.bioclipse.core.domain.BioObject;
//import net.bioclipse.util.BioclipseConsole;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.views.properties.IPropertySource;



/**
 * Concept of a mathematical matrix.
 * 
 * @author egonw, ospjuth
 */
public class MatrixResource extends BioObject implements IMatrixResource {
	
	//Bioclipse 1 logger
	//private static final Logger logger = Activator.getLogManager().getLogger(MatrixResource.class.toString());
	
	private static final Logger logger = Logger.getLogger(MatrixResource.class);

	private static final int TAB_SCANNER = 1;
	private static final int COMMA_SCANNER = 2;
	private static final int WHITESPACE_SCANNER = 3;

    private static final String HARDCODED_RESPONSE_COLUMN_NAME = "RESPONSE";
	
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
//		super(name);
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
	
//	public MatrixResource(BioResourceType type, Object obj) {
//		super(type,obj);
//	}
//
//	public MatrixResource(BioResourceType type, String resString, String name) {
//		super(name);
//		if (getPersistedResource()==null){
//			persistedResource=PersistedResource.newResource(resString); 
//		}
//		setDefaultResourceType(type);
//	}
	
	/**
	 * Make a copy of this object and return it if it can be parsed. 
	 * Used to create new objects with a higher level taht replaces the old on parse 
	 */
//	public static IBioResource newResource(BioResourceType type, Object resourceObject, String name) {
//
//		if (resourceObject instanceof IPersistedResource) {
//			IPersistedResource persRes = (IPersistedResource) resourceObject;
//
//			boolean parentIsParsed=persRes.getBioResourceParent().isParsed();
//
//			//This is the copy
//			MatrixResource fResource= new MatrixResource(type, persRes);
//			fResource.setParsed(parentIsParsed);
//			fResource.setName(name);
//			if (fResource.parseResource()==true){
//				return fResource;
//			}
//			else{
//				logger.error("PersistedResource:" + fResource.getName() + " could not be parsed into a MatrixResource");
//				return null;
//			}
//			
//		}	
//
//		if (resourceObject instanceof File) {
//			IPersistedResource persRes = (IPersistedResource) resourceObject;
//			
//			//This is the copy
//			MatrixResource matResource = new MatrixResource(type, persRes);
//			matResource.setName(name);
//			if (matResource.parseResource() == true) {
//				return matResource;
//			} else {
//				logger.debug("PersistedResource:" + matResource.getName() + " could not be parsed into a MatrixResource");
//				return null;
//			}
//			
//		}
//		
//		logger.debug("ResourceObject not a File. Discarded.");
//		return null;
//	}

	public boolean isParsed()
	{
		return parsed;
	}
	
	/**
	 * Parse the resourceString into children or parsedResource object
	 * @return
	 */
	public boolean parseResource() {
//		if (!getPersistedResource().isLoaded()) return false;	//Return false if not loaded
		if (isParsed()) return true;	//Return true if already parsed

		logger.debug("Parsing the resource...");
		
		findMatrixImplementation();
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
				
				//Old parser
//				read(new String(getPersistedResource().getInMemoryResource()));

				// some demo code
//				matrixImpl.newMatrix(5, 4);
//				try {
//					matrixImpl.set(4, 3, 5.0);
//				} catch (Exception e) {
//					logger.error("Could not set matrix content! " + e.getMessage(), e);
//				}
				
//				if (propSource ==null){
//					propSource=new MatrixResourcePropertySource(this);
//				}
//				propSource.addAdvancedProperties();
				
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
//			BioclipseConsole.writeToConsole("Matrix is empty!");
			logger.debug("Matrix is empty!");
			this.setSize(matrixRows, matrixCols);
		}

		//Selected scanner, null from start
		int matrixScannerType=COMMA_SCANNER;
		
		//Test using comma and whitespace as delimiters
		Scanner testScanner=matrixScanner((matrixLines[0]), matrixScannerType);
		

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
			testScanner=matrixScanner((matrixLines[0]), matrixScannerType);
			
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
				testScanner=matrixScanner((matrixLines[0]), matrixScannerType);			}
			
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
		
		//Check if first row is a header, if this row contains anything but numbers it's considered a header
		Scanner matrixScanner = matrixScanner(matrixLines[0],matrixScannerType);
		
		if( !matrixScanner.hasNextDouble() )
		{
			//Remove one row from matrixRows because one of the rows is the header
			matrixRows--;
			int index = 0;
			while( matrixScanner.hasNext()) {
				index++;
				String colname = matrixScanner.next();
				matrixImpl.setColumnName(index, colname);
				
				//Handle response column for a hardcoded name
				//TODO: refactor this out
				if (colname.equalsIgnoreCase( HARDCODED_RESPONSE_COLUMN_NAME ) ){
				    matrixImpl.setResponseColumn( index );
				}
				
			}
		}
		
//		double[][] matrix = new double[matrixRows][matrixCols];
		this.setSize(matrixRows, matrixCols);
		//Read in remaining rows
		if( matrixImpl.hasColHeader() )
		{
			for(int i = 1; i < matrixLines.length; i++ )
			{
				insertRow( i, matrixLines[i], matrixScannerType);
			}
		}
		//No column headers, read into matrix from first line
		else
		{
			for(int i = 0; i < matrixLines.length; i++ )
			{
				insertRow( i+1, matrixLines[i], matrixScannerType);
			}
		}
		
		matrixScanner.close();
		logger.debug("Parsed matrix");		
	}

	//Utility method for inserting a row into the matrix (not the column header)
	private void insertRow( int row, String rowString, int matrixScannerType )
	{
		Scanner matrixScanner = matrixScanner(rowString, matrixScannerType);
		
		if( !matrixScanner.hasNextDouble() ) {
			matrixImpl.setRowName(row, matrixScanner.next());
		}
		int col = 1;
		while( matrixScanner.hasNext() )
		{
		    //If we have responses and this is the response column, add it
		    if (matrixImpl.hasResponseColumn() && matrixImpl.getResponseColumn()==col){
            String value = matrixScanner.next();
		        matrixImpl.setResponse( row, value );
		    }
		    else{
		        double value=Double.NaN;
		        try {
		            value = matrixScanner.nextDouble();
		        }catch (Exception e){
		            logger.error( "Error parsing double. Row=" + row +" Col=" + col + " Expected double but was: " + matrixScanner.next() + " Error: " + e.getMessage());
		        }
		        try {
		            if (col > matrixImpl.getColumnCount()) {
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

	public void unLoad(){
//		super.unLoad();
		propSource.removeAdvancedProperties();
	}

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
//			BioclipseConsole.writeToConsole("No matrix implementations found!");
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
//	    	  BioclipseConsole.writeToConsole("File is empty: creating an null matrix.");
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
		if (matrixImpl == null) findMatrixImplementation();
		if (matrixImpl.hasResponseColumn())
		    matrixImpl = matrixImpl.getInstance(row, col, matrixImpl.getResponseColumn());
		else
        matrixImpl = matrixImpl.getInstance(row, col);
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
            for (int col=1; col<=getColumnCount(); col++) {
            	String colName = getColumnName(col);
            	if (colName == null) {
            		colName = "";
            	} else {
//            		colName.replaceAll("\"", "\\\"");
            	}
            	buffer.append(colName);
            	if (col<getColumnCount()) buffer.append(',');
            }
            buffer.append('\n');
            for (int row=0; row<getRowCount(); row++) {
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
	
	
}
