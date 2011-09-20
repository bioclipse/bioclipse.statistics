package net.bioclipse.r.ui.editors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;

public class REditor extends TextEditor {

	private ColorManager colorManager;

	public REditor() {
		super();
		colorManager = new ColorManager();
//		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
//		setDocumentProvider(new XMLDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

	public String getFilePath() {
		if (isDirty()) {
			boolean result = MessageDialog.openQuestion(getSite().getShell(),
					"R Editor", "The script was modifed. " +
							"Save changes before sourcing the script?");
			if (result) {
				doSave(null);
			}
		}
		IEditorInput einput = getEditorInput();
		if (!(einput instanceof IFileEditorInput)) return null;
		IFileEditorInput finput = (IFileEditorInput) einput;
		String filepath = finput.getFile().getRawLocation().toOSString();
		return filepath;
	}
}
