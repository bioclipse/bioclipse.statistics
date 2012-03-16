package net.bioclipse.r.ui.views;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * Action for saving a R session
 *
 * @authors valyo
 *
 */
public class saveRSession implements IViewActionDelegate{

	@Override
	public void run(IAction action) {
		RConsoleView rView = (RConsoleView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("net.bioclipse.r.ui.views.RConsoleView");
		rView.executeCommand("save.image(\".RData\")");
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IViewPart view) {
		// TODO Auto-generated method stub
		
	}

}
