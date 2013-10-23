/* *****************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.statistics.business.business;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.StringTokenizer;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.statistics.model.IMatrixResource;
import net.bioclipse.statistics.model.MatrixResource;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;

public class MatrixManager implements IBioclipseManager {

    private Logger logger = Logger.getLogger( this.getClass() );
    
    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "matrix";
    }

    public IMatrixResource create( IFile file ) throws ParseException {
        String name = file.getName();
        IFileEditorInput fei = new FileEditorInput( file );
        IMatrixResource matrix =
                new MatrixResource(name, fei);
        
        if (!matrix.parseResource())
            throw new ParseException( "Could not parse " + 
                    file.getFullPath().toOSString() + " into an matrix." );
        
        return matrix;
    }
    
    public IMatrixResource create(
            String valueSequence, int ncol) {
        StringTokenizer tokenizer = new StringTokenizer(valueSequence);
        int tokenCount = tokenizer.countTokens();
        double ceil = Math.ceil((double)tokenCount/(double)ncol);
        int nrow = (int)ceil;

        IMatrixResource matrix =
            new MatrixResource("", (IFileEditorInput)null);
        matrix.setSize(nrow, ncol);

        int rowCount = 0;
        int colCount = 0;
        while (tokenizer.hasMoreTokens()) {
            String value = tokenizer.nextToken();
            matrix.set(rowCount+1, colCount+1, value);
            colCount++;
            if (colCount == ncol) {
                colCount = 0;
                rowCount++;
            }
        }
        
        return matrix;
    }
    
    public IMatrixResource create(double[][] values) {
        IMatrixResource matrix =
            new MatrixResource("", (IFileEditorInput)null);
        int ncol = values.length;
        int nrow = values[0].length;
        matrix.setSize(ncol, nrow);
        
        for (int row=0; row<nrow; row++) {
            for (int col=0; col<ncol; col++) {
                matrix.set(row+1, col+1, values[row][col]);
            }
        }
        return matrix;
    }
    
    public IMatrixResource create(String[][] values) {
        IMatrixResource matrix =
            new MatrixResource("", (IFileEditorInput)null);
        int ncol = values.length;
        int nrow = values[0].length;
        matrix.setSize(ncol, nrow);
        
        for (int row=0; row<nrow; row++) {
            for (int col=0; col<ncol; col++) {
                matrix.set(row+1, col+1, values[row][col]);
            }
        }
        return matrix;
    }

    public void setColumnLabels(IMatrixResource matrix, String[] names) {
        int col = 1;
        for (String name : names) {
            matrix.setColumnName(col, name);
            col++;
        }
    }

    public void setRowLabels(IMatrixResource matrix, String[] names) {
        int row = 1;
        for (String name : names) {
            matrix.setRowName(row, name);
            row++;
        }
    }

    public IMatrixResource empty() {
        IMatrixResource matrix = new MatrixResource("", (IFileEditorInput)null);
        matrix.setSize(0,0);
        return matrix;
    }

    public IMatrixResource addRow(IMatrixResource matrix, List<String> values)
    throws BioclipseException {
    	int row = matrix.getRowCount();
    	if (row != 0 && values.size() != matrix.getColumnCount())
    		throw new BioclipseException(
    			"The existing matrix has a different number of columns than " +
    			"the number of values in the List."
    		);

    	row += 1;
    	for (int col=1; col<=values.size(); col++) {
    		matrix.set(row, col, values.get(col-1));
    	}
    	return matrix;
    }
    
    public void saveAsCSV(IMatrixResource matrix, IFile target,
    		              IProgressMonitor monitor)
    throws InvocationTargetException, BioclipseException, CoreException {
    	if (target.exists()) {
            throw new BioclipseException("File already exists!");
        }
    	if (monitor == null) monitor = new NullProgressMonitor();

    	monitor.beginTask("Writing file", 1);
    	try {
			target.create(
				new ByteArrayInputStream(
					matrix.asCSV().getBytes("US-ASCII")
				),
				false, monitor 
			);
			monitor.worked(1);
		} catch (UnsupportedEncodingException e) {
			logger.error( "Could not encorde the file: "+e.getMessage() );
		} finally {
			monitor.done();
		}
    }
}
