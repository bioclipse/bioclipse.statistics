package net.bioclipse.r.ui.handlers;

import net.bioclipse.r.ui.util.RunUtil;
import net.bioclipse.r.ui.views.RConsoleView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Handler to source an R Script taken from active editor contents.
 *
 * @authors ola, valyo
 *
 */
public class RunRScriptHandler extends AbstractHandler implements IHandler {

	public static String NEWLINE = System.getProperty("line.separator");
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("net.bioclipse.r.ui.views.RConsoleView");
		   	RConsoleView rView = (RConsoleView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("net.bioclipse.r.ui.views.RConsoleView");
		   	String filepath = RunUtil.getFilePath();
		   	rView.execEditorInput("source(\"" + filepath + "\")");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}
}
