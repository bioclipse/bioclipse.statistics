package net.bioclipse.r.ui.handlers;

import net.bioclipse.r.business.Activator;
import net.bioclipse.r.business.IRBusinessManager;
import net.bioclipse.r.ui.editors.REditor;
import net.bioclipse.r.ui.views.RConsoleView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * Handler to source an R Script taken from active editor contents.
 *
 * @author ola
 *
 */
public class RunRScriptHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                   .getActivePage().getActiveEditor();
		if (!(editor instanceof REditor)) return null;
		REditor reditor = (REditor) editor;

		//Check the editor state and get the file path
		String filepath = reditor.getFilePath();
		System.out.println("File path is: " + filepath);

		//Get the file path with correct file separator
		IRBusinessManager r = Activator.getDefault().getJavaRBusinessManager();
		filepath = r.fixFilepath(filepath);

		//Pass the path to the R console method
	   	RConsoleView rView = (RConsoleView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("net.bioclipse.r.ui.views.RConsoleView");
	   	rView.execEditorInpit("source(\"" + filepath + "\")");

		//We are done
		return null;
	}

}
