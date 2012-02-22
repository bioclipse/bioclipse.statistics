package net.bioclipse.r.ui.handlers;

import net.bioclipse.r.ui.views.RConsoleView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler to execute a Text Selection in R
 *
 * @author ola
 *
 */
public class RunRSnippetHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof ITextSelection)) return null;

		ITextSelection textsel = (ITextSelection) selection;
		System.out.println("You selected text: \n" + textsel.getText());

	   	RConsoleView rView = (RConsoleView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("net.bioclipse.r.ui.views.RConsoleView");
	   	rView.execEditorInpit(textsel.getText());
		//We are done
		return null;
	}

}
