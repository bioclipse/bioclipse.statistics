package net.bioclipse.r.ui.handlers;

import net.bioclipse.r.ui.util.RunUtil;
import net.bioclipse.r.ui.views.RConsoleView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;

/**
 * Handler to execute a Text Selection in R
 *
 * @author ola
 *
 */
public class RunRAllHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

	   	RConsoleView rView = (RConsoleView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("net.bioclipse.r.ui.views.RConsoleView");
		String contents = RunUtil.getContent(event);
	   	rView.execEditorInpit(contents);
		//We are done
		return null;
	}

}
