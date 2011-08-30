package net.bioclipse.r.ui.handlers;

import net.bioclipse.r.business.Activator;
import net.bioclipse.r.business.IRBusinessManager;
import net.bioclipse.r.ui.editors.REditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler to source an R Script taken from active editor contents.
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

		//Get the file path from editor
		IEditorInput einput = reditor.getEditorInput();
		if (!(einput instanceof IFileEditorInput)) return null;
		IFileEditorInput finput = (IFileEditorInput) einput;
		String filepath = finput.getFile().getRawLocation().toOSString();
		System.out.println("File path is: " + filepath);

		IRBusinessManager r = Activator.getDefault().getJavaRBusinessManager();
		r.source(filepath);

		//We are done
		return null;
	}

}
