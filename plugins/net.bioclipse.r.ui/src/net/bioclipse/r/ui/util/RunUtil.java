/**
 * Util methods for running code from the R editor
 *
 * @author valyo
 *
 */

package net.bioclipse.r.ui.util;

import java.util.ArrayList;

import net.bioclipse.r.ui.editors.REditor;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

public class RunUtil {
	
    public static final String fileseparator = java.io.File.separator;
    public static String cmdparser    = "(;?\r?\n|;)";
    
	public static String getSelectedCode(final ExecutionEvent event) throws CoreException {
		try {
			final ISelection selection = WorkbenchUIUtil.getCurrentSelection(event.getApplicationContext());
			final IWorkbenchPart workbenchPart = HandlerUtil.getActivePart(event);
			if (selection instanceof ITextSelection) {
				final ITextSelection textsel = (ITextSelection) selection;
				if (textsel.getLength() > 0) {
					System.out.println("You selected text: \n" + textsel.getText());
					final String code = textsel.getText();
					if (code != null) {
						return code;
					}
				}
				else {
					IDocument document = null;
					if (workbenchPart instanceof ITextEditor) {
						final ITextEditor editor = (ITextEditor) workbenchPart;
						if (!(editor instanceof REditor))
							return null;
						REditor reditor = (REditor) editor;
						document = reditor.getDocumentProvider().getDocument(reditor.getEditorInput());
					}
					int line = document.getLineOfOffset(textsel.getOffset());
					final IRegion lineInformation = document.getLineInformation(line);
					String code = document.get(lineInformation.getOffset(),lineInformation.getLength());
					return code;
				}
			}
		}catch (BadLocationException e) {
			// TODO: handle exception
			throw new CoreException(new Status(IStatus.ERROR, "net.bioclipse.r.ui", "An error occured when collecting the code to be run", e));
		}
		return null;
	}
	
	public static String getContent(ExecutionEvent event) {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (!(editor instanceof REditor)) return null;
		REditor reditor = (REditor)editor;

		IDocument doc = reditor.getDocumentProvider().getDocument(reditor.getEditorInput());
		String contents = doc.get();

		System.out.println("Editor content: \n" + contents);
		return contents;
	}
	
	public static String getFilePath() {
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (!(editor instanceof REditor)) return null;
		REditor reditor = (REditor) editor;

		//Check the editor state and get the file path
		String filepath = reditor.getFilePath();
		System.out.println("File path is: " + filepath);

		//Get the file path with correct file separator
		filepath = RunUtil.fixFilepath(filepath);
		return filepath;
	}
	
    public static String fixFilepath(String filepath) {
    	filepath = filepath.replace(fileseparator, "/");
		return filepath;
	}

    public static String[] parseCommand(String command) {
    	String[] cmd = command.split(cmdparser);
    	ArrayList<String> list = new ArrayList<String>();
    	for (String s : cmd)
    		if (!s.startsWith("#") && s.length() != 0)
    			list.add(s);
    	cmd = list.toArray(new String[list.size()]);
		return cmd;
    }
}
