package net.bioclipse.r.ui.handlers;

import net.bioclipse.r.business.Activator;
import net.bioclipse.r.business.IRBusinessManager;
import net.bioclipse.r.ui.editors.REditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler to execute an R Script taken from active editor contents.
 *
 * @author ola
 *
 */
public class RunRScriptHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (!(editor instanceof REditor)) return null;
		REditor reditor = (REditor)editor;

		IDocument doc = reditor.getDocumentProvider().getDocument(reditor.getEditorInput());
		String contents = doc.get();

		System.out.println("Editor contents: " + contents);

		IRBusinessManager r = Activator.getDefault().getJavaRBusinessManager();

	   	String[] scrlines = contents.split("\n");
    	for (String rcmd: scrlines)
    		r.eval(rcmd);
//		r.evalScript(contents);
//TODO: Implement

		//We are done
		return null;
	}

}
