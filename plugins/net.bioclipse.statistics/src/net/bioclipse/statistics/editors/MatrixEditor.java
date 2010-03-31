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
 *
 *****************************************************************************/

package net.bioclipse.statistics.editors;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.bioclipse.chart.ChartUtils;
import net.bioclipse.chart.events.CellData;
import net.bioclipse.chart.events.CellSelection;
import net.bioclipse.dialogs.ChartDialog;
import net.bioclipse.dialogs.HistogramDialog;
import net.bioclipse.model.ChartConstants;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;


/**
 * A spreadsheet like editor for editing matrices.
 * 
 * @author jonalv, Eskil Andersen, Ola Spjuth
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

	private IFile resource;

	private IProject project;

	public MatrixEditor() {
		super();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		//then call save() of the BioResource 
		boolean success = matrix.save();
		if( success ){
			this.setDirty(false);
		}
		else
		{
			logger.error("Could not save matrix to file");
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
//		BioclipseConsole.writeToConsole("SaveAs is not yet supported.");
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) 
	            throws PartInitException {
	    
		super.setSite(site);
		super.setInput(input);
		logger.debug("initializing matrix editor...");
		if (input instanceof IFileEditorInput) {
			IFileEditorInput feditorinput = (IFileEditorInput) input;
			this.editorInput = feditorinput;
			this.resource=feditorinput.getFile();
			this.project=resource.getProject();
		}else{
			showMessage("MatrixEditor can currently only be opened on a File");
			dispose();
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

		grid = new Grid( parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | 
		                 SWT.VIRTUAL | SWT.MULTI);
		grid.setHeaderVisible(true);
		grid.setRowHeaderVisible(true);
		grid.setCellSelectionEnabled(true);

		
		//Set up the matrix as a model for the grid
		matrix = new MatrixResource(editorInput.getName(),
		                            (IFileEditorInput) this.editorInput);
		matrix.parseResource();		

		setupGridForMatrix(matrix);
		

		hookContextMenu(parent);

		//Register context menu with the workbench for extensions
		//this.getSite().registerContextMenu(manager, this);
		
		//Register MatrixGridEditor with the page as a receiver of 
		//SelectionChangedEvents
		getSite().getPage().addSelectionListener(this);
		
		//Register MatrixGridEditor as a SelectionProvider
		getSite().setSelectionProvider(this);
		
		
		//Listens for selection of cells and passes it on to
		//ISelectionListeners
		grid.addSelectionListener( new SelectionAdapter()
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
					try{
		          CellData cd = new CellData(colName,p.y,Double.parseDouble(value));
		          cs.addCell(cd);
					}catch (NumberFormatException e){
					    logger.debug( "Swallowed a numberformat exception since cells " +
					    		"in MatrixEditor are required to be double." );
					}
				}
				//Sets the selection to the selected cells
				MatrixEditor.this.setSelection(cs);
				

			}
			
		});
	}

	private void hookContextMenu(Composite parent) {
		//Context menu
		MenuManager manager = new MenuManager("matrix editor tools");

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
				plot( ChartConstants.LINE_PLOT );
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

				boolean atLeastTwoColumns = selectedColumns.size() >= 2;

				scatterPlotAction.setEnabled( atLeastTwoColumns );
				linePlotAction.setEnabled(    atLeastTwoColumns );
				timeSeriesAction.setEnabled(  atLeastTwoColumns );
			}

		});

		manager.add(chartMenu);

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
		Menu menu = manager.createContextMenu(parent); 
		grid.setMenu(menu);
	}
	
	private void setupGridForMatrix(final IMatrixResource newMatrix) {
		
		grid.setItemCount( newMatrix.getRowCount() );
		grid.setItemHeight(20);

		for (int i = 0; i < newMatrix.getColumnCount(); i++) {

			GridColumn column = new GridColumn(grid, SWT.NONE);
			column.setText("");
			String columnName = newMatrix.getColumnName(i+1);
			column.setText( columnName == null ? "" + (char)('A' + i) : columnName );
			column.setWidth(100);
		}
		grid.addListener( SWT.SetData, new Listener() {
			public void handleEvent(Event event) {
				GridItem item = (GridItem)event.item;

				int index = grid.indexOf(item);
				String rowName = newMatrix.getRowName(index+1);
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
							double value = Double.parseDouble(textField.getText());
							matrix.set(p.y+1, p.x+1, value );
							
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
		if( columnsVector.size() != 2)
		{
			ChartDialog chartDialog = new ChartDialog(Display.getCurrent()
			                                          .getActiveShell(),
					SWT.NULL, plotType, columnsVector, true);
			chartDialog.open();
		}
		//If only 2 columns are selected no dialog is shown
		else
		{
			ColumnData cdx,cdy;
			cdx = ((ColumnData)columnsVector.get(0));
			cdy = ((ColumnData)columnsVector.get(1));
			switch( plotType )
			{
			case ChartConstants.SCATTER_PLOT:
			    ChartUtils.scatterPlot( cdx.getValues(), cdy.getValues(),
			                            cdx.getLabel(), cdy.getLabel(), 
			                            cdx.getLabel() + " against " + cdy.getLabel(), 
			                            cdx.getIndices(), this);
			    break;
			case ChartConstants.LINE_PLOT:
				ChartUtils.linePlot(cdx.getValues(), cdy.getValues(), cdx.getLabel(), 
				                    cdy.getLabel(), cdx.getLabel() + " against " + 
				                    cdy.getLabel(), cdx.getIndices(), this);
				break;
			case ChartConstants.TIME_SERIES:
				ChartUtils.timeSeries(cdx.getValues(), cdy.getValues(), cdx.getLabel(), 
				                      cdy.getLabel(), cdx.getLabel() + " against " + 
				                      cdy.getLabel(), cdx.getIndices(), this);
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
		hd.open(this);
	}

	@Override
	public void setFocus() {
	}

	public void selectionChanged(IWorkbenchPart part, final ISelection selection) 
	{
		if( selection instanceof ChartSelection && !selection.isEmpty())
		{
			ChartSelection cs = (ChartSelection) selection;

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

						//Match the column names i element where x and y values reside with 
						//their column numbers in grid
						for( int i = 0; i < grid.getColumnCount(); i++)
						{
							GridColumn gc = grid.getColumn(i);
							if( gc.getText().equals(element.getXColumn()))
							{
								xColumn = i;
							}
							else if( gc.getText().equals(element.getYColumn()))
							{
								yColumn = i;
							}
							if( xColumn != -1 && yColumn != -1)
								break;
						}			
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
					}

				});
			}
		}
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
//					boolean answer=MessageDialog.openConfirm(getSite().getShell(), 
//				    "Resource changed", "Matrix has been changed on file. Would you 
//				    like to reload contents from file?");
//					if (answer){
						reloadFromFile();
//					}else{
//						//Mark as dirty to indicate that save is required to keep contents
//						setDirty(true);
//					}
				}
			});
		}

	}
}

