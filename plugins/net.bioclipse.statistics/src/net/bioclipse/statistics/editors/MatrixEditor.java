package net.bioclipse.statistics.editors;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.bioclipse.chart.ChartUtils;
import net.bioclipse.chart.events.CellData;
import net.bioclipse.chart.events.CellSelection;
import net.bioclipse.dialogs.ChartDialog;
import net.bioclipse.dialogs.HistogramDialog;
//import net.bioclipse.model.BioResource;
//import net.bioclipse.model.BioResourceChangeListener;
import net.bioclipse.model.ChartConstants;
import net.bioclipse.model.ChartSelection;
import net.bioclipse.model.ColumnData;
import net.bioclipse.model.PlotPointData;
import net.bioclipse.statistics.Activator;
import net.bioclipse.statistics.model.MatrixResource;
//import net.bioclipse.statistics.model.MatrixResource;
//import net.bioclipse.util.BioclipseConsole;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
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
 * @author jonalv
 *
 */
public class MatrixEditor extends EditorPart implements /*BioResourceChangeListener,*/ ISelectionListener, ISelectionProvider  {

	private static final Logger logger = 
		Logger.getLogger( MatrixEditor.class.toString() );

	private IEditorInput editorInput;
	private boolean isDirty;
	private Grid grid;
	private List<ISelectionChangedListener> selectionListeners;
	private ISelection currentSelection;

	private final Clipboard cb = new Clipboard(
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay() );

	private MatrixResource matrix;

	public MatrixEditor() {
		super();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		//then call save() of the BioResource 
//		BioResourceEditorInput brinp = (BioResourceEditorInput) editorInput;
//		brinp.getBioResource().save();
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
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.setSite(site);
		super.setInput(input);
		logger.debug("initializing matrix editor...");
		this.editorInput = input;		
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

		selectionListeners = new Vector<ISelectionChangedListener>();
		
		matrix = new MatrixResource(editorInput.getName(),(IFileEditorInput) this.editorInput);
		matrix.parseResource();		

		grid = new Grid( parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.MULTI );
		grid.setHeaderVisible(true);
		grid.setRowHeaderVisible(true);
		grid.setCellSelectionEnabled(true);

		grid.setItemCount( matrix.getRowCount() );
		grid.setItemHeight(20);

		for (int i = 0; i < matrix.getColumnCount(); i++) {

			GridColumn column = new GridColumn(grid, SWT.NONE);
			column.setText("");
			String columnName = matrix.getColumnName(i+1);
			column.setText( columnName == null ? "" + (char)('A' + i) : columnName );
			column.setWidth(100);
		}
		grid.addListener( SWT.SetData, new Listener() {
			public void handleEvent(Event event) {
				GridItem item = (GridItem)event.item;

				int index = grid.indexOf(item);
				String rowName = matrix.getRowName(index+1);
				item.setHeaderText( rowName == null ? index + "" : rowName );
				for (int i = 0; i < matrix.getColumnCount(); i++) {
					item.setText(i, matrix.get(index+1, i+1) + "");
				}
			}
		} );

		EditEventHandler handler = new EditEventHandler();
		grid.addListener(SWT.MouseDoubleClick, handler );
		grid.addKeyListener(handler);

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

		//Register context menu with the workbench for extensions
		//this.getSite().registerContextMenu(manager, this);
		
		//Register MatrixGridEditor with the page as a receiver of SelectionChangedEvents
		getSite().getPage().addSelectionListener(this);
		
		//Register MatrixGridEditor as a SelectionProvider
//		getSite().setSelectionProvider(this);
		
		//Listens for selection of cells and passes it on to
		//ISelectionListeners
		grid.addSelectionListener( new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent se) {
				// TODO Auto-generated method stub
				
			}
			//When a cell is selected an event is sent to ISelectionListeners
			//notifying them of selected cells
			public void widgetSelected(SelectionEvent se) {
				Point selections[] = grid.getCellSelection();
				CellSelection cs = new CellSelection();
				for( Point p : selections )
				{
					GridColumn gc = grid.getColumn(p.x);
					String colName = gc.getText();
					GridItem gi = grid.getItem(p.y);
					String value = gi.getText(p.x);
					CellData cd = new CellData(colName,p.y,Double.parseDouble(value));
					cs.addCell(cd);
				}
				ChartUtils.markPoints(cs);
			}
			
		});
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
	 * Handles cell editing events like doubleclicks and pressing enter on a column
	 * @author Eskil Andersen
	 *
	 */
	private class EditEventHandler implements Listener, KeyListener
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

			textField.addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent arg0) {
				}

				public void keyReleased(KeyEvent event) 
				{
					if( event.character == SWT.CR )
					{
						item.setText(p.x, textField.getText());
						textField.setVisible(false);
						setDirty(true);
						
						double value = Double.parseDouble(textField.getText());
						
						matrix.set(p.y+1, p.x+1, value );
						textField.dispose();
					}
				}

			});
			
			textField.addFocusListener(new FocusListener()
			{

				public void focusGained(FocusEvent arg0) {
					//Nothing here
				}

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

		//Not used
		public void keyPressed(KeyEvent arg0) {

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
	 * Call when plotting scatter plots, line plots or time series. Sets up the data
	 * and calls plot functions from net.bioclipse.chart
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
					cd.add(Double.parseDouble(gi.getText(cellSelection[i].x)),cellSelection[i].y);
				}
			}
			
		}	
		
		//Setup a dialog where the user can adjust settings and plot if more than 2 columns are selected
		if( columnsVector.size() != 2)
		{
			ChartDialog chartDialog = new ChartDialog(Display.getCurrent().getActiveShell(),
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
			    ChartUtils.scatterPlot( cdx.getValues(), cdy.getValues(), cdx.getLabel(), cdy.getLabel(), cdx.getLabel() + " against " + cdy.getLabel(), cdx.getIndices(), this);
			    break;
			case ChartConstants.LINE_PLOT:
				ChartUtils.linePlot(cdx.getValues(), cdy.getValues(), cdx.getLabel(), cdy.getLabel(), cdx.getLabel() + " against " + cdy.getLabel());
				break;
			case ChartConstants.TIME_SERIES:
				ChartUtils.timeSeries(cdx.getValues(), cdy.getValues(), cdx.getLabel(), cdy.getLabel(), cdx.getLabel() + " against " + cdy.getLabel());
				break;
			default: 
				throw new IllegalArgumentException("Illegal value for diagramType"); 
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
		hd.open();
	}

	@Override
	public void setFocus() {
	}

//	public void resourceChanged(BioResource resource) {
//		// nothing to do
//	}

	public void selectionChanged(IWorkbenchPart part, final ISelection selection) 
	{
		if( selection instanceof ChartSelection && !selection.isEmpty())
		{


			Display display = Display.getCurrent();
			//may be null if outside the UI thread
			if (display == null)
				display = Display.getDefault();

			display.asyncExec(new Runnable()
			{

				public void run() 
				{
					if( grid.isDisposed() )
						return;
					
					int xColumn = -1;
					int yColumn = -1;


					IStructuredSelection ss = (IStructuredSelection) selection;

					PlotPointData element = (PlotPointData) ss.getFirstElement();

					//Match the column names i element where x and y values reside with their column numbers in grid
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
						selectedPoints[i++] = new Point(xColumn, gcd.getRownumber());
						selectedPoints[i++] = new Point(yColumn, gcd.getRownumber());
					}
					grid.setCellSelection(selectedPoints);
				}
				
			});
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
	}
}

