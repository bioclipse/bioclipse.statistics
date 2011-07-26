/*******************************************************************************
 * Copyright (c) 2011 Christian Ander.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 ******************************************************************************/
package net.bioclipse.r.business;

import net.bioclipse.r.business.Activator;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class RPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage{

    public void init(IWorkbench workbench) {
        //Initialize the preference store we wish to use
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
      }

    @Override
    protected void createFieldEditors() {
//        IntegerFieldEditor timeoutEditor=new IntegerFieldEditor(Activator.R_HOME, "Timeout (s)",getFieldEditorParent());
//        timeoutEditor.setValidRange( 1, 999999999 );
//        addField( timeoutEditor );
        DirectoryFieldEditor Rhome = new DirectoryFieldEditor(Activator.R_HOME, "R_HOME", getFieldEditorParent());
        addField( Rhome );
    }
}
