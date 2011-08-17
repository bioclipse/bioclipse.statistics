package net.bioclipse.r.ui.editors;

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

}
