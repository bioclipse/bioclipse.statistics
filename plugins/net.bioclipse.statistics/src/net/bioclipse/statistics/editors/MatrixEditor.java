/* ***************************************************************************
 * Copyright (c) 2008-2009 The Bioclipse Project Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html

 * Contributors:
 *     Jonathan Alvarsson
 *     Eskil Andersen
 *     Ola Spjuth
 *     Klas Jšnsson
 *****************************************************************************/

package net.bioclipse.statistics.editors;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.bioclipse.chart.ChartUtils;
import net.bioclipse.chart.ScatterPlotRenderer;
import net.bioclipse.chart.events.CellData;
import net.bioclipse.chart.events.CellSelection;
import net.bioclipse.dialogs.ChartDialog;
import net.bioclipse.dialogs.HistogramDialog;
import net.bioclipse.model.ChartConstants;
import net.bioclipse.model.ChartDescriptor;
import net.bioclipse.model.ChartSelection;
import net.bioclipse.model.ColumnData;
import net.bioclipse.model.PlotPointData;
import net.bioclipse.statistics.model.IMatrixResource;
import net.bioclipse.statistics.model.MatrixResource;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;


/**
 * A spreadsheet like editor for editing matrices.
 * 
 * @author jonalv, Eskil Andersen, Ola Spjuth, Klas Jšnsson
 *
 */
public class MatrixEditor extends EditorPart implements ISelectionListener, 
                                                        ISelectionProvider, 
                                                        IResourceChangeListener{

	private static final Logger logger = 
		Logger.getLogger( MatrixEditor.class.toString() );

	private IFileEditorInput editorInput;
	private boolean isDirty;
	private Grid grid;
	private List<ISelectionChangedListener> selectionListeners;
	private ISelection currentSelection;

	private final Clipboard cb = 
	    new Clipboard(PlatformUI.getWorkbench()
	                  .getActiveWorkbenchWindow().getShell().getDisplay() );

	private MatrixResource matrix;
	private Composite myParent;
	private IFile resource;

	private IProject project;
	
	private List<IEditorActionDelegate> actionDelegates = 
	        new ArrayList<IEditorActionDelegate>();
	
	public MatrixEditor() {
		super();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		//then call save() of the BioResource 
	    if (matrix.getResource() == null) {
	        this.doSaveAs();
	    } else {
	        boolean success = matrix.save();
	        if( success ){
	            this.setDirty(false);
	        }
	        else
	        {
	            logger.error("Could not save matrix to file");
	        }
	    }
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		fireSetDirtyChanged();
	}

	private void fireSetDirtyChanged() {
		Runnable r = new Runnable() {
			public void run() {
				firePropertyChange(PROP_DIRTY);
			}
		};
		Display fDisplay = getSite().getShell().getDisplay();
		fDisplay.asyncExec(r);
	}

	@Override
	public void doSaveAs() {
	    SaveAsDialog saveAsDialog = new SaveAsDialog( this.getSite().getShell() );
        if ( matrix.getResource() instanceof IFile )
            saveAsDialog.setOriginalFile( (IFile) matrix.getResource() );
        int result = saveAsDialog.open();
        if ( result == SaveAsDialog.CANCEL ) {
            logger.debug( "SaveAs canceled." );
            return;
        }
        
        IPath path = saveAsDialog.getResult();
        boolean success = matrix.saveAs( path );
        if( success ) {
            this.resource = (IFile) matrix.getResource();
            this.project = resource.getProject();
            this.setPartName( matrix.getName() );
            this.setDirty(false);
        } else {
            logger.error( "Could not save matrix to file" );
            showMessage( "Ome thing went wrong while trying to save the " +
            		" matrix. Please see the log-file for more details." );
        }

	}

	@Override
	public void init(IEditorSite site, IEditorInput input) 
	            throws PartInitException {
	    
		super.setSite(site);
		super.setInput(input);
		super.setPartName(input.getName());
		logger.debug("initializing matrix editor...");
		if (input instanceof IFileEditorInput) {
			IFileEditorInput feditorinput = (IFileEditorInput) input;
			this.editorInput = feditorinput;
			this.resource=feditorinput.getFile();
			this.project=resource.getProject();
			setPartName(this.resource.getName());
		} else {
		    matrix = (MatrixResource) input.getAdapter( IMatrixResource.class );
		    if ( matrix != null ) {
		        this.editorInput = null;
	            this.resource = null;
	            String name = matrix.getName();
	            if ( name == null || name.isEmpty() )
	                name = "UNNAMED";
	            setPartName( name );
	            setDirty( true );
	            fireSetDirtyChanged();
		    } else {
		        showMessage("MatrixEditor could not open the matrix.");
                dispose();  
		    }
		       	   
		}
		
		selectionListeners = new Vector<ISelectionChangedListener>();

		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		
	}


    private void showMessage(String message) {
        MessageDialog.openInformation( getSite().getShell(),
                                       "Information",
                                       message );
    }

    private void showError(String message) {
        MessageDialog.openError( getSite().getShell(),
                                       "Error",
                                       message );
    }

	@Override
	public boolean isDirty() {
		return this.isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void createPartControl(Composite parent) {
		logger.debug("Creating editor part...");
		
		parent.setLayout(new FillLayout());
		
		if (myParent == null) {
		    myParent = parent;
		}
		
		grid = new Grid( myParent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | 
		                 SWT.VIRTUAL | SWT.MULTI);
		grid.setHeaderVisible(true);
		grid.setRowHeaderVisible(true);
		grid.setCellSelectionEnabled(true);

		//Set up the matrix as a model for the grid
		if (matrix == null)
		    if (editorInput != null)
		        matrix = new MatrixResource(editorInput.getName(),
		                            (IFileEditorInput) this.editorInput);
		    else {
		        showMessage("MatrixEditor could not open the file or find the matrix.");
                dispose();
		    }
		        
		matrix.parseResource();		

		setupGridForMatrix(matrix);
		hookContextMenu(myParent);
		
		//Register MatrixGridEditor with the page as a receiver of 
		//SelectionChangedEvents
		getSite().getPage().addSelectionListener(this);
		
		//Register MatrixGridEditor as a SelectionProvider
		getSite().setSelectionProvider(this);

		//Listens for selection of cells and passes it on to
		//ISelectionListeners
		grid.addSelectionListener( gridSelectionAdapter );
	}

	SelectionAdapter gridSelectionAdapter = new SelectionAdapter()
    {
        //When a cell is selected an event is sent to ISelectionListeners
        //notifying them of selected cells
        public void widgetSelected(SelectionEvent se) {
            Point selections[] = grid.getCellSelection();
            CellSelection cs = new CellSelection();
            cs.setSource(MatrixEditor.this);

            for( Point p : selections )
            {
                GridColumn gc = grid.getColumn(p.x);
                String colName = gc.getText();
                GridItem gi = grid.getItem(p.y);
                String value = gi.getText(p.x);
                CellData cd = new CellData(colName,p.y, value);
                cs.addCell(cd);
            }
            //Sets the selection to the selected cells
            MatrixEditor.this.setSelection(cs);
            

        }
        
    };
	
    final Action colHeader = new Action("&Column header", IAction.AS_CHECK_BOX) {
        @Override
        public void run() {
            super.run();
            if (hasColumnHeader()) {
                  try {
                        matrix.moveColumnHeaderToRow( 1 );
                        updateGrid();  
                    } catch ( IllegalAccessException e ) {
                        logger.error( "Could not move the row-header to the top " +
                                "row: " + e.getMessage() );
                    }
                
            } else {
                try {
                    matrix.setRowAsColumnHeader( 1 );
                    updateGrid();
                } catch ( IllegalAccessException e ) {
                    logger.error( "Could not set the first row as row-" +
                            "header: " + e.getMessage() );
                }
            }
            setChecked( matrix.hasColHeader() );
            for (IEditorActionDelegate ead:actionDelegates) {
                ead.selectionChanged( this, null );
            }
        }
    };
    
    /** 
     * Method to set the row-header from outside the class. E.g. using an 
     * toggle-button in the toolbar.
     * 
     * @return True if the matrix got an row header, else false
     */
    public boolean runRowHeaderAction() {
        rowHeader.run();
        return hasRowHeader();
    }
    
    /**
     * Method for checking if the matrix has a row-header.
     * 
     * @return True if the matrix has a row-header
     */
    public boolean hasRowHeader() {
        return matrix.hasRowHeader();
    }
    
    /** 
     * Method to set the column-header from outside the class. E.g. using an 
     * toggle-button in the toolbar.
     * 
     * @return True if the matrix got an column-header, else false
     */
    public boolean runColumnHeaderAction() {
        colHeader.run();
        return hasColumnHeader();
    }
    
    /**
     * Method for checking if the matrix has a column-header.
     * 
     * @return True if the matrix has a column-header
     */
    public boolean hasColumnHeader() {
        return matrix.hasColHeader();
    }
    
    final Action rowHeader = new Action("&Row header", IAction.AS_CHECK_BOX) {
        @Override
        public void run() {
            super.run();
            if (hasRowHeader()) {
                try {
                    matrix.moveRowHeaderToColumn( 1 );                 
                    updateGrid();
                } catch ( IllegalAccessException e ) {
                    logger.error( "Could not set the first row as row-" +
                            "header: " + e.getMessage() );
                }
            } else {
                try {
                    matrix.setColumnAsRowHeader( 1 );
                    updateGrid();
                } catch ( IllegalAccessException e ) {
                    logger.error( "Could not set the first row as row-" +
                            "header: " + e.getMessage() );
                } 
            }
            setChecked( matrix.hasRowHeader() );
            for (IEditorActionDelegate ead:actionDelegates) {
                ead.selectionChanged( this, null );
            }
        }
    };
    
    public void addActionDelegate(IEditorActionDelegate ead) {
        actionDelegates.add( ead );
    }
    
    public void removeActionDelegate(IEditorActionDelegate ead) {
        actionDelegates.remove( ead );
    }
    private void hookContextMenu(Composite parent) {
        //Context menu
        MenuManager manager = new MenuManager("matrix editor tools");
       
        
        
        final Action copyAction = new Action("&Copy"){

            @Override
            public void run() {
                super.run();
                copy( rectangularSelection() );
            }
        }; 

        manager.add( copyAction );
        manager.addMenuListener(new IMenuListener(){

            public void menuAboutToShow(IMenuManager manager) {

                copyAction.setEnabled( selectionIsRectangular() );

            }

        });

        //Plug-in placeholder
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

        final Action scatterPlotAction = new Action("&Scatter plot"){

			@Override
			public void run() 
			{
				plot( ChartConstants.SCATTER_PLOT );

			}			
		};

		final Action linePlotAction = new Action("&Line plot"){
			public void run(){
				plot( ChartConstants.LINE_PLOT );
			}
		};

		final Action timeSeriesAction = new Action("&Time series plot"){
			public void run(){
				plot( ChartConstants.TIME_SERIES );
			}
		};

		final Action histogramAction = new Action("&Histogram"){

			@Override
			public void run() {
				super.run();
				histogram();				
			}

		};
	
		MenuManager  chartMenu = new MenuManager("Chart");
		chartMenu.add(scatterPlotAction);
		chartMenu.add(linePlotAction);
		chartMenu.add(timeSeriesAction);
		chartMenu.add(histogramAction);
		
		chartMenu.addMenuListener(new IMenuListener(){

			public void menuAboutToShow(IMenuManager manager) {

				Vector<ColumnData> selectedColumns = getSelectedColumns();

				boolean atLeastOneColumn = selectedColumns.size() >= 1;
				
				scatterPlotAction.setEnabled( atLeastOneColumn );
				linePlotAction.setEnabled(    atLeastOneColumn );
				timeSeriesAction.setEnabled(  atLeastOneColumn );

			}

		});
		manager.add(chartMenu);

		MenuManager  headerMenu = new MenuManager("Headers");
		headerMenu.add( colHeader );
		headerMenu.add( rowHeader );
		manager.add( headerMenu );
		headerMenu.addMenuListener( new IMenuListener() {
            
            public void menuAboutToShow( IMenuManager manager ) {
                colHeader.setChecked( matrix.hasColHeader() );
                rowHeader.setChecked( matrix.hasRowHeader() );
            }
		} );
		
		
		Menu menu = manager.createContextMenu(parent); 
		grid.setMenu(menu);
	}
	
	private void updateGrid() {
	    grid.dispose();
	    createPartControl( myParent );
	    myParent.layout( true );
	}
	
	private void setupGridForMatrix(final IMatrixResource newMatrix) {
		
		grid.setItemCount( newMatrix.getRowCount() );
		grid.setItemHeight(20);

		for (int i = 0; i < newMatrix.getColumnCount(); i++) {

			GridColumn column = new GridColumn(grid, SWT.NONE);
			column.setText("");
			String columnName = null;
			if (newMatrix.hasColHeader())
			    columnName = newMatrix.getColumnName(i+1);
			column.setText( columnName == null ? "" + (char)('A' + i) : columnName );
			column.setWidth(100);
		}
		grid.addListener( SWT.SetData, new Listener() {
			public void handleEvent(Event event) {
				GridItem item = (GridItem)event.item;

				int index = grid.indexOf(item);
				if (newMatrix.hasColHeader() && index == newMatrix.getRowCount())
				    return;
				String rowName = null;
				if (newMatrix.hasRowHeader())
				    rowName = newMatrix.getRowName(index+1);
				item.setHeaderText( rowName == null ? index + "" : rowName );
				for (int i = 0; i < newMatrix.getColumnCount(); i++) {
					item.setText(i, newMatrix.get(index+1, i+1));
				}
			}
		} );

		EditEventHandler handler = new EditEventHandler();
		grid.addListener(SWT.MouseDoubleClick, handler );
		grid.addKeyListener(handler);
	}

	protected void reloadFromFile() {

		//Remove old listeners
		for (Listener li : grid.getListeners(SWT.SetData)){
			grid.removeListener(SWT.SetData, li);
		}
		for (Listener li : grid.getListeners(SWT.MouseDoubleClick)){
			grid.removeListener(SWT.MouseDoubleClick, li);
		}

		matrix.setParsed(false);
		matrix.parseResource();

		//Remove old columns
		for (GridColumn col : grid.getColumns()){
			col.dispose();
		}
		
		grid.removeAll();
		
		//Set up the new matrix
		setupGridForMatrix(matrix);
		
	}


	/* Produces a String[][] of cell contents. The precondition is that
	 * the selection really is rectangular.
	 */
	private String[][] rectangularSelection() {
		int colMax = Integer.MIN_VALUE,
		rowMax = Integer.MIN_VALUE,
		colMin = Integer.MAX_VALUE,
		rowMin = Integer.MAX_VALUE;

		for( Point cell : grid.getCellSelection() )
		{
			if (colMin > cell.x)
				colMin = cell.x;
			if (colMax < cell.x)
				colMax = cell.x;

			if (rowMin > cell.y)
				rowMin = cell.y;
			if (rowMax < cell.y)
				rowMax = cell.y;
		}

		int width  = colMax - colMin + 1,
		height = rowMax - rowMin + 1;

		String[][] contents = new String[height][width];
		for( Point cell : grid.getCellSelection() )
			contents[cell.y - rowMin][cell.x - colMin]
			                          = grid.getItem( cell.y ).getText( cell.x );

		return contents;
	}

	protected void copy(String[][] rectangularContents) {

		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < rectangularContents.length; row++) {
			for (int col = 0; col < rectangularContents[0].length; col++) {
				sb.append( rectangularContents[row][col] );
				sb.append( '\t' );
			}
			sb.append( '\n' );
		}

		TextTransfer textTransfer = TextTransfer.getInstance();
		Transfer[] types = new Transfer[] { textTransfer };
		cb.setContents( new Object[]{ sb.toString() }, types );
	}
	
	/**
	 * Handles cell editing events like doubleclick and pressing enter on a column
	 * @author Eskil Andersen
	 *
	 */
	private class EditEventHandler extends KeyAdapter implements Listener
	{
		public void handleEvent( Event event )
		{
			final GridEditor gridEditor = new GridEditor(grid);
			final GridItem item = grid.getFocusItem();
			if( item == null)
				return;
			final Point p = grid.getFocusCell();
			final Text textField = new Text( grid, SWT.PUSH);
			textField.setText(item.getText(p.x));
			
			textField.setEditable(true);
			textField.setFocus();
			textField.setEnabled(true);
			//Set caret position at end of text
			textField.setSelection(textField.getCharCount());

			textField.addKeyListener(new KeyAdapter() {

				public void keyReleased(KeyEvent event) 
				{
					if( event.character == SWT.CR )
					{
						//If input was empty string set cell to "0.0"
						if( textField.getText().equals("")){
							item.setText(p.x, "0.0");						
							setDirty(true);
							
							//Set the edited value in underlying model
							matrix.set(p.y+1, p.x+1, 0.0d );
							
							//Remove widgets used to edit cell
							disposeEditingWidgets(textField, gridEditor);
							return;
						}
						//Make sure the user didn't enter two dots
						else if( textField.getText().split("\\.").length > 2 ){
							Display.getCurrent().beep();
							return;
						}
						//Make sure that last character is not a dot
						else if( !(textField.getText().charAt(
						                            textField.getText().length()-1) == '.'))
						{
							//If string starts with a dot prepend a zero in the text displayed
							if( textField.getText().startsWith(".")){
								item.setText(p.x, "0" + textField.getText());
							}
							else{
								item.setText(p.x, textField.getText());	
							}
							setDirty(true);
							
							//Set the edited value in underlying model
							matrix.set(p.y+1, p.x+1, textField.getText() );
							
							//Remove widgets used to edit cell
							disposeEditingWidgets(textField, gridEditor);
							return;
						}
						//Emit beep if the input was otherwise incorrect
						else{
							Display.getCurrent().beep();
						}
					}					
				}

			});
			
			textField.addFocusListener(new FocusAdapter()
			{
				public void focusLost(FocusEvent arg0) {
					textField.setVisible(false);
				}
				
			});

			gridEditor.minimumWidth = (item.getBounds(p.x)).width;
			gridEditor.minimumHeight = (item.getBounds(p.x)).height;
			gridEditor.horizontalAlignment = SWT.CENTER;
			gridEditor.verticalAlignment = SWT.TOP;
			gridEditor.setEditor(textField, item, p.x);
			gridEditor.layout();
		}
		
		//Disposes the widgets used for cell editing
		private void disposeEditingWidgets(Text textField, GridEditor gridEditor){
			textField.setVisible(false);
			textField.dispose();
			gridEditor.dispose();
		}

		public void keyReleased(KeyEvent event) {
			if( event.character == SWT.CR )
			{
				this.handleEvent(null);
			}

		}
	}


	/**
	 * @return A Vector containing selected columns 
	 */
	private Vector<ColumnData> getSelectedColumns() 
	{
		Point[] cellSelection = grid.getCellSelection();
		Vector<ColumnData> columnsVector = new Vector<ColumnData>();
		for( int i=0;i<cellSelection.length;i++)
		{
			String columnName = grid.getColumn(cellSelection[i].x).getText();
			Iterator<ColumnData> j = columnsVector.iterator();
			boolean hasColumn = false;
			while( j.hasNext() )
			{
				ColumnData cd = (ColumnData) j.next();
				if( cd.getLabel().equals(columnName))
				{
					hasColumn = true;
				}

			}
			if( !hasColumn)
			{
				columnsVector.add(new ColumnData(columnName));
			}
		}
		return columnsVector;
	}

	private boolean selectionIsRectangular() 
	{
		int colMax = Integer.MIN_VALUE,
		rowMax = Integer.MIN_VALUE,
		colMin = Integer.MAX_VALUE,
		rowMin = Integer.MAX_VALUE;

		for( Point cell : grid.getCellSelection() )
		{
			if (colMin > cell.x)
				colMin = cell.x;
			if (colMax < cell.x)
				colMax = cell.x;

			if (rowMin > cell.y)
				rowMin = cell.y;
			if (rowMax < cell.y)
				rowMax = cell.y;
		}

		int width  = colMax - colMin + 1,
		height = rowMax - rowMin + 1;

		return grid.getCellSelectionCount() == width * height;
	}

	/**
	 * Call when plotting scatter plots, line plots or time series. Sets up the 
	 * data and calls plot functions from net.bioclipse.chart
	 * @param plotType ChartConstants.LINE_PLOT, SCATTER_PLOT or TIME_SERIES
	 */
	public void plot( int plotType )
	{
		Point[] cellSelection = grid.getCellSelection();

		int colMax = Integer.MIN_VALUE,
		rowMax = Integer.MIN_VALUE,
		colMin = Integer.MAX_VALUE,
		rowMin = Integer.MAX_VALUE;

		for( Point cell : cellSelection )
		{
			if (colMin > cell.x)
				colMin = cell.x;
			if (colMax < cell.x)
				colMax = cell.x;

			if (rowMin > cell.y)
				rowMin = cell.y;
			if (rowMax < cell.y)
				rowMax = cell.y;
		}


		//Calculate how many columns have been selected
		Vector<ColumnData> columnsVector = this.getSelectedColumns();
		
		
		GridItem gi;
		ColumnData cd;
		Iterator<ColumnData> j;
		//Order data
		for( int i=0;i<cellSelection.length;i++)
		{
			gi = grid.getItem(cellSelection[i].y);
			j = columnsVector.iterator();
			while( j.hasNext() )
			{
				cd = j.next();
				if( cd.getLabel().equals(grid.getColumn(cellSelection[i].x).getText()))
				{
				    try{
			          cd.add(Double.parseDouble(gi.getText(cellSelection[i].x)),
			                 cellSelection[i].y);
				    }catch (NumberFormatException e){
				        showError( "Selection contains cells that could not be parsed " +
				        		"into a value. " +
				        		"Please select only values before trying to plot again." );
				    }
				}
			}
			
		}	
		
		//Setup a dialog where the user can adjust settings and plot if more than 2 
		//columns are selected
		
		if( columnsVector.size() > 2)
		{
			ChartDialog chartDialog = new ChartDialog(Display.getCurrent()
			                                          .getActiveShell(),
					SWT.NULL, plotType, columnsVector, true, this, cellSelection);
			chartDialog.open();
		}
		//If only 1 or 2 columns are selected no dialog is shown
		else
		{
			ColumnData cdx,cdy;
			if (columnsVector.size() == 1) {
			    cdy = ((ColumnData)columnsVector.get(0));
			    cdx = new ColumnData("Row");
			    int start = cdy.getIndices()[0];
			    int end = cdy.getIndices().length+ start;
			    for (int i = start;i<end;i++)
			        cdx.add( i+1, i );
			} else {
			    cdx = ((ColumnData)columnsVector.get(0));
			    cdy = ((ColumnData)columnsVector.get(1));
			}
			ChartDescriptor descriptor = new ChartDescriptor(this, cdx.getIndices(),plotType ,cdx.getLabel(),cdy.getLabel(), cellSelection);
			switch( plotType )
			{
			case ChartConstants.SCATTER_PLOT:
			    ChartUtils.scatterPlot( cdx.getValues(), cdy.getValues(),
			                            cdx.getLabel(), cdy.getLabel(), 
			                            cdx.getLabel() + " against " + cdy.getLabel(), 
			                            descriptor);
			    break;
			case ChartConstants.LINE_PLOT:
				ChartUtils.linePlot(cdx.getValues(), cdy.getValues(), cdx.getLabel(), 
				                    cdy.getLabel(), cdx.getLabel() + " against " + 
				                    cdy.getLabel(), descriptor);
				break;
			case ChartConstants.TIME_SERIES:
				ChartUtils.timeSeries(cdx.getValues(), cdy.getValues(), cdx.getLabel(), 
				                      cdy.getLabel(), cdx.getLabel() + " against " + 
				                      cdy.getLabel(), descriptor);
				break;
			default: 
				throw new IllegalArgumentException("Illegal value for plotType"); 
			}
			ChartUtils.setDataColumns(cdx.getLabel(), cdy.getLabel());
		}
	}

	/**
	 * Calls a dialog for setting up a histogram and orders data to the right format
	 */
	public void histogram()
	{
		Point[] cellSelection = grid.getCellSelection();
		
		double[] values = new double[cellSelection.length];
		int i = 0;
		for ( Point point : cellSelection )
			values[i++] = Double.parseDouble(
					grid.getItem(point.y)
					.getText(point.x) );

		HistogramDialog hd = new HistogramDialog(Display.getCurrent().getActiveShell(),
				SWT.NULL, values);
		hd.open(this, cellSelection);
	}

	@Override
	public void setFocus() {
		if(!grid.isDisposed()) {grid.setFocus();}
	}

	public void selectionChanged(IWorkbenchPart part, final ISelection selection) 
	{
		if( selection instanceof ChartSelection && !selection.isEmpty())
		{
			final ChartSelection cs = (ChartSelection) selection;

			if( cs.getDescriptor().getSource() == this){

				Display display = Display.getCurrent();
				//may be null if outside the UI thread
				if (display == null)
					display = Display.getDefault();

				display.asyncExec(new Runnable()
				{

					@SuppressWarnings("unchecked")
					public void run() 
					{
						if( grid.isDisposed() )
							return;

						int xColumn = -1;
						int yColumn = -1;


						IStructuredSelection ss = (IStructuredSelection) selection;

						PlotPointData element = (PlotPointData) ss.getFirstElement();
						ChartDescriptor descriptor = cs.getDescriptor();
						//Match the column names i element where x and y values reside with 
						//their column numbers in grid
						for( int i = 0; i < grid.getColumnCount(); i++)
						{
							GridColumn gc = grid.getColumn(i);
							if( gc.getText().equals(element.getXColumn()) || gc.getText().equals(descriptor.getXLabel()) )
							{
								xColumn = i;
							}
							else if( gc.getText().equals(element.getYColumn()) || gc.getText().equals(descriptor.getYLabel()) )
							{
								yColumn = i;
							}
							if( xColumn != -1 && yColumn != -1)
								break;
						}
						
						if( xColumn != -1 && yColumn != -1) {
						    Iterator iter = ss.iterator();
						    Point[] selectedPoints = new Point[ss.size()*2];
						    int i = 0;
						    while( iter.hasNext())
						    {
						        PlotPointData gcd = (PlotPointData) iter.next();
						        selectedPoints[i++] = new Point(xColumn, gcd.getRowNumber());
						        selectedPoints[i++] = new Point(yColumn, gcd.getRowNumber());
						    }
						    grid.setCellSelection(selectedPoints);
						} else if( xColumn == -1 && yColumn != -1) {
						    /* In this case the plot is done from a single 
						     * column, to be exact it originate from the column
						     * number yColumn. */
						    Iterator iter = ss.iterator();
                            Point[] selectedPoints = new Point[ss.size()];
                            int i = 0;
                            PlotPointData gcd;
                            while( iter.hasNext() ) {
                                gcd = (PlotPointData) iter.next();
                                selectedPoints[i++] = new Point(yColumn, gcd.getRowNumber());
                            }
						    grid.setCellSelection(selectedPoints);
						} else {
						    /* If we end up here then the user probably clicked 
						     * in a histogram */
						    Point[] originCells = cs.getDescriptor().getOrigenCells();
						    Object maxv = element.getPropertyValue( ChartConstants.MAX_VALUE );
						    double maxValue = 0;
						    if (maxv != null && maxv instanceof Double)
						        maxValue = (Double) maxv; 
						    Object minv = element.getPropertyValue( ChartConstants.MIN_VALUE );
						    double minValue = 0;
                            if (minv != null && minv instanceof Double)
                                minValue = (Double) minv;
						    ArrayList<Point> selectedPoints = new ArrayList<Point>();
						    for (int i = 1; i <= matrix.getColumnCount(); i++)  {
	                            for (int j=1; j <= matrix.getRowCount(); j++) {
	                                String strValue = matrix.get( j, i );
	                                try {
	                                    double value = Double.parseDouble( strValue );	                                    
	                                    if (value >= minValue && value <= maxValue) {
	                                        Point test = new Point(i-1,j-1);
	                                        for (Point cell:originCells) {
	                                            if (cell.equals( test )) {
	                                                for (int k = 1;k <= matrix.getColumnCount(); k++)
	                                                    selectedPoints.add( new Point(k-1,j-1) );
	                                            }
	                                        }
	                                    }
	                                } catch (NumberFormatException e) {
	                                    /* If we end up here the cell didn't 
	                                     * contained a number, it's OK. But it 
	                                     * has nothing to do with our histogram
	                                     * so let's move on*/
	                                }
	                            }
	                            
	                        }
						    if (!selectedPoints.isEmpty()) {
						        grid.setCellSelection( selectedPoints.toArray( new Point[selectedPoints.size()] ) );
						        String selectedRows = "";
						        int row = -1;
						        for (Point cell:selectedPoints) {						            
						            if (row != cell.y) 
						                selectedRows += cell.y+", ";
						            row = cell.y;
						        }
						        selectedRows = selectedRows.substring( 0, selectedRows.length()-2 );
					            PropertyDescriptor descriptor0 = new TextPropertyDescriptor(ChartConstants.SELECTED_ROWS, "Rows");
					            PropertyDescriptor[] propDesc = {descriptor0};
					            element.addPropertyDescriptors( propDesc );
						        element.setPropertyValue( ChartConstants.SELECTED_ROWS, selectedRows );
						    }
						}
					}

				});
			}
		} else if (selection instanceof CellSelection) {
		    JFreeChart chart = ChartUtils.getActiveChart();
		    IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		    
		    if (chart != null) {
		        ChartDescriptor cd = ChartUtils.getChartDescriptor( chart );
		        try {
		            if (cd.getResource().equals( ((MatrixEditor) editor).resource )) {
		                XYPlot plot = (XYPlot) chart.getPlot();
		                XYItemRenderer plotRenderer = plot.getRenderer();
		                if (plotRenderer instanceof ScatterPlotRenderer) {
		                    ScatterPlotRenderer renderer = (ScatterPlotRenderer) plotRenderer;
		                    renderer.clearMarkedPoints();

		                    String xLabel = plot.getDomainAxis().getLabel();
		                    String yLabel = plot.getRangeAxis().getLabel();
		                    CellSelection cSel = (CellSelection) selection;
		                    List<Double> xValues = getCellValues(cSel, xLabel);
		                    List<Double> yValues = getCellValues(cSel, yLabel);
		                    if (!xValues.isEmpty() && !yValues.isEmpty())
		                        for (int i = 0;i<xValues.size();i++)
		                            selectPoints( xValues.get( i ), yValues.get( i ), chart, plot, renderer );
		                }
		            }
		        } catch ( FileNotFoundException e ) {
		            logger.error( "Could not find the source" );
		        }
		    }

		}
	}


	private void selectPoints( double xValue, double yValue,JFreeChart chart, XYPlot plot, ScatterPlotRenderer renderer ) {
	        Number xK, yK;
	        if (xValue != Double.NaN && xValue != Double.NaN ) {
	            for (int j=0; j<plot.getDataset().getItemCount(plot.getDataset().getSeriesCount()-1);j++) {
	                for (int i=0; i<plot.getDataset().getSeriesCount();i++) {
	                    xK = plot.getDataset().getX(i,j);
	                    yK = plot.getDataset().getY(i,j);
	                    if (xValue == xK.doubleValue() && yValue == yK.doubleValue()) {     
	                        renderer.addMarkedPoint(j, i);
	                    }

	                }
	            }

	        chart.plotChanged( new PlotChangeEvent(plot) );
	        chart.fireChartChanged();
	    }
    }

    private List<Double> getCellValues( CellSelection cSel, String label ) { 
        
	    List<Double> values = new ArrayList<Double>(cSel.size());
        Iterator<CellData> cellItr = cSel.iterator();
        CellData data;
        while (cellItr.hasNext()){
            data = cellItr.next();
            if (label.equals( "Row" ))
                values.add( (double) data.getRowIndex() + 1 );
            else if (label.equals( data.getColName() )) {              
                try {
                    values.add( Double.parseDouble(  data.getValue() ));
                } catch (NumberFormatException e) {
                    values.add( Double.NaN );
                } 
            } else {
                GridColumn[] columns = grid.getColumns();
                double value;
                int row = data.getRowIndex()+1;
                int col = -1;
                for (int i = 0;i<columns.length;i++) {                    
                    if (label.equals( columns[i].getText() )) {
                        col = i+1;
                    }
                }
                if (col != -1) {
                    String strValue = matrix.get( row, col );
                    try {
                        value = Double.parseDouble( strValue );
                    } catch (NumberFormatException e) {
                        logger.error( "The value in the cell [" + col + ", " + 
                                row + "], " + strValue + " is not a number." );
                        value = Double.NaN;
                    }
                } else {
                    if (!cSel.getSource().equals( this ))
                        return new ArrayList<Double>();
                    
                    logger.error( "Could not identify the column" );
                    value = Double.NaN;
                }
                
                values.add( value ); 
            }
        }

        return values;
    }

    //Selection Provider methods
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if( !selectionListeners.contains(listener))
			selectionListeners.add(listener);
	}

	public ISelection getSelection() {
		return currentSelection;
	}

	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		if( selectionListeners.contains(listener))
			selectionListeners.remove(listener);
	}

	public void setSelection(ISelection selection) {
		currentSelection = selection;
		Iterator<ISelectionChangedListener> iter = selectionListeners.iterator();
		while( iter.hasNext() )
		{
			//Send selection to registered listeners
			(iter.next()).selectionChanged(
					new SelectionChangedEvent(this, currentSelection));
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		getSite().getPage().removeSelectionListener(this);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	public void resourceChanged(IResourceChangeEvent event) {
		
	    // Its not properly saved get...
	    if (project == null)
	        return;
	    
		final IPath DOC_PATH = new Path(project.getName()+"/dataset.csv");

        //we are only interested in POST_CHANGE events
        if (event.getType() != IResourceChangeEvent.POST_CHANGE)
           return;
        IResourceDelta rootDelta = event.getDelta();
        //get the delta, if any, for the documentation directory
        IResourceDelta docDelta = rootDelta.findMember(DOC_PATH);
        if (docDelta == null)
           return;
        
		
		if (docDelta.getKind()==IResourceDelta.REMOVED){
		    
		    final IEditorPart toclose=this;
		    
	      Display.getDefault().asyncExec(new Runnable(){
            public void run() {
                logger.debug("Matrix file has been removed!");
                getSite().getPage().closeEditor( toclose, false );
            }
	      });

			return;
		}

		if (docDelta.getKind()==IResourceDelta.CHANGED){
			Display.getDefault().asyncExec(new Runnable(){

				public void run() {
				    reloadFromFile();
				}
			});
		}

	}
	
}