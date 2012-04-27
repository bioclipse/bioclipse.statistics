package net.bioclipse.r.ui.handlers;

import net.bioclipse.r.ui.util.RunUtil;
import net.bioclipse.r.ui.views.RConsoleView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PlatformUI;

/**
 * Handler to source a Text Selection or current line in R
 *
 * @authors ola, valyo
 *
 */
public class SourceRSnippetHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
	   	try {
			String code = RunUtil.getSelectedCode(event);
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("net.bioclipse.r.ui.views.RConsoleView");
			RConsoleView rView = (RConsoleView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("net.bioclipse.r.ui.views.RConsoleView");
			rView.execEditorInput(code);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

}
