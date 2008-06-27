package net.bioclipse.plugins.views;

import java.awt.Frame;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.bioclipse.chart.ChartUtils;
import net.bioclipse.chart.ScatterPlotRenderer;
import net.bioclipse.model.ChartConstants;
import net.bioclipse.model.ChartDescriptor;
import net.bioclipse.model.ChartManager;
import net.bioclipse.model.ChartModelEvent;
import net.bioclipse.model.ChartModelListener;
import net.bioclipse.model.ChartSelection;
import net.bioclipse.model.ScatterPlotMouseHandler;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;



/**
 * ChartView displays different charts generated with ChartUtils
 * @see ChartUtils
 */

public class ChartView extends ViewPart implements ISelectionListener, ISelectionProvider, ChartModelListener {
	private Action saveImageActionSVG,saveImageActionPNG,saveImageActionJPG;
	private Composite parent;
//	private Label imageLabel;
	private List<ISelectionChangedListener> selectionListeners;
	private ChartSelection selection;
	private static final Logger logger = Logger.getLogger(ChartView.class);
	private Frame frame;
	private static final boolean IS_MACOS = System.getProperty("os.name").contains("Mac");
	private CTabFolder tabFolder;
	private ScatterPlotMouseHandler pmh;
	private ScatterPlotRenderer renderer;
	
	
	/**
	 * The constructor.
	 */
	public ChartView() {
		super();
		selectionListeners = new ArrayList<ISelectionChangedListener>();
		pmh = new ScatterPlotMouseHandler();
		renderer = new ScatterPlotRenderer(false,true);
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		
		this.parent = parent;
		tabFolder = new CTabFolder(parent, SWT.TOP);
		tabFolder.setSimple(false);
		
		getSite().setSelectionProvider(this);
		getSite().getPage().addSelectionListener(this);
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ChartView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(parent);
		parent.setMenu(menu);
		getSite().registerContextMenu(menuMgr, this);
	}
	
//	/**
//	 * Sets the ChartManager that holds the charts that this view displays
//	 * @param model the ChartManager to use
//	 * @see ChartManager
//	 */
//	public void setModel(ChartManager model){
//		
//	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		addActions(manager);
	}

	private void fillContextMenu(IMenuManager manager) {
		addActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		addActions(manager);
	}
	
	private void addActions( IContributionManager manager )
	{
		manager.add(saveImageActionSVG);
		manager.add(saveImageActionPNG);
		manager.add(saveImageActionJPG);
		manager.add(new Separator());
	}

	private void makeActions() {
		
		//Create action for saving charts in SVG format
		saveImageActionSVG = new Action() {
			public void run()
			{
				FileDialog dialog = 
					new FileDialog(ChartView.this.getViewSite().getWorkbenchWindow().getShell(),
						SWT.SAVE);
				dialog.setFileName("Image.svg");
				String path = dialog.open();
				System.out.println(path);
				ChartUtils.saveImageSVG(path);
			}
		};
		saveImageActionSVG.setText("Export as SVG Image");
		saveImageActionSVG.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		saveImageActionSVG.setToolTipText("Export the chart as a SVG image");
		
		//Create action for saving charts in png format
		saveImageActionPNG = new Action(){
			public void run()
			{
				FileDialog dialog = 
					new FileDialog(ChartView.this.getViewSite().getWorkbenchWindow().getShell(),
						SWT.SAVE);
				dialog.setFileName("Image.png");
				String path = dialog.open();
				System.out.println(path);
				try {
					ChartUtils.saveImagePNG(path);
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("Failed to save chart as png " + e);
				}
			}
		};
		saveImageActionPNG.setText("Export as PNG Image");
		saveImageActionPNG.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		saveImageActionPNG.setToolTipText("Export the chart as a PNG image");
		
		//Create action for saving charts in JPEG format
		saveImageActionJPG = new Action(){
			public void run()
			{
				FileDialog dialog = 
					new FileDialog(ChartView.this.getViewSite().getWorkbenchWindow().getShell(),
						SWT.SAVE);
				dialog.setFileName("Image.jpg");
				String path = dialog.open();
				System.out.println(path);
				try {
					ChartUtils.saveImageJPG(path);
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("Failed to save chart as jpg " + e);
				}
			}
		};
		saveImageActionJPG.setText("Export as JPG Image");
		saveImageActionJPG.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		saveImageActionJPG.setToolTipText("Export the chart as a JPG image");
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		parent.setFocus();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) 
	{
	}

//	public Composite getParent() {
//		return parent;
//	}

//	public Label getImageLabel() {
//		return imageLabel;
//	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if(!selectionListeners.contains(listener))
		{
			selectionListeners.add(listener);
		}
	}

	public ISelection getSelection() {
		return selection;
	}

	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		if(selectionListeners.contains(listener))
			selectionListeners.remove(listener);
	}

	public void setSelection(ISelection selection) {
		
		this.getSite().getWorkbenchWindow().getWorkbench().getDisplay().asyncExec(new Runnable() {

			public void run() {
				ChartView.this.getSite().getPage().activate(ChartView.this);
			}
			
		});
		
		this.selection = (ChartSelection)selection;
		java.util.Iterator<ISelectionChangedListener> iter = selectionListeners.iterator();
		while( iter.hasNext() )
		{
			final ISelectionChangedListener listener = iter.next();
			final SelectionChangedEvent e = new SelectionChangedEvent(this, this.selection);
			//Does SWT stuff so this has to be called on SWT's thread
			this.getSite().getShell().getDisplay().asyncExec(new Runnable() {

				public void run() {
					listener.selectionChanged(e);
				}
				
			});
			
		}
	}

	/**
	 * Displays a chart in ChartView and sets up its mouse listener
	 * @param chart
	 */
	public void display( JFreeChart chart )
	{
		final ChartDescriptor cd = ChartUtils.getChartDescriptor(chart);
		
		
		CTabItem chartTab = new CTabItem(tabFolder, SWT.CLOSE);
		chartTab.setText(chart.getTitle().getText());
	
		//Clear chartComposite of all old controls
		/*Control[] children = chartTab.getChildren();		
		for( int i = 0; i<children.length;i++)
		{
			children[i].dispose();
		}*/
	
		Composite composite = new Composite(tabFolder, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		chartTab.setControl(composite);
		
		frame = SWT_AWT.new_Frame(composite);
	
		final ChartPanel chartPanel = new ChartPanel(chart);
		
		SwingUtilities.invokeLater(new Runnable()
		{
	
			public void run() {
				frame.removeAll();
				frame.add(chartPanel);
				frame.setVisible(true);
	
				if( cd.getPlotType() == ChartConstants.SCATTER_PLOT)
				{
					//Listens for mouseclicks on points
					XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
					plot.setRenderer(renderer);
					
					if( ChartView.IS_MACOS )
					{
						frame.addMouseListener(pmh);
					}
					else
					{
						chartPanel.addMouseListener(pmh);
					}	
				}
			}
		});
		tabFolder.setSelection(chartTab);
		tabFolder.forceFocus();
		tabFolder.layout();
	}
	
	/**
	 * Handles state changes in the model
	 */
	public void handleChartModelEvent(ChartModelEvent e) {
		// TODO Auto-generated method stub
		
	}
}