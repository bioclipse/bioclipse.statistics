//package net.bioclipse.action.contribution;
//
//import org.eclipse.jface.action.IAction;
//import org.eclipse.jface.viewers.ISelection;
//import org.eclipse.ui.IEditorActionDelegate;
//import org.eclipse.ui.IEditorPart;
//import org.eclipse.ui.IWorkbench;
//import org.eclipse.ui.PartInitException;
//import org.eclipse.ui.PlatformUI;
//
//public class MatrixChartingActionDelegate implements IEditorActionDelegate 
//{
//	private final static String VIEW_ID ="net.bioclipse.plugins.views.ChartView";
//	public MatrixChartingActionDelegate() {
//		// TODO Auto-generated constructor stub
//	}
//
//	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
//		// TODO Auto-generated method stub
//
//	}
//
//	public void run(IAction action) {
//		// TODO Auto-generated method stub
//		IWorkbench work = PlatformUI.getWorkbench();
//		try {
//			work.getActiveWorkbenchWindow().getActivePage().showView(VIEW_ID);
//		} catch (PartInitException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public void selectionChanged(IAction action, ISelection selection) {
//		// TODO Auto-generated method stub
//
//	}
//
//}
