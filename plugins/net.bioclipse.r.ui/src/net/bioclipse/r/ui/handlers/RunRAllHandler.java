package net.bioclipse.r.ui.handlers;

import net.bioclipse.r.business.Activator;
import net.bioclipse.r.business.IRBusinessManager;
import net.bioclipse.r.ui.editors.REditor;
import net.bioclipse.r.ui.views.RConsoleView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler to execute a Text Selection in R
 *
 * @author ola
 *
 */
public class RunRAllHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (!(editor instanceof REditor)) return null;
		REditor reditor = (REditor)editor;

		IDocument doc = reditor.getDocumentProvider().getDocument(reditor.getEditorInput());
		String contents = doc.get();

		System.out.println("Editor content: \n" + contents);

		IRBusinessManager r = Activator.getDefault().getJavaRBusinessManager();
	   	r.evalSnippet(contents);
	   	RConsoleView rView = (RConsoleView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("net.bioclipse.r.ui.views.RConsoleView");
	   	rView.execSnippet(contents);
		//We are done
		return null;
	}

}
