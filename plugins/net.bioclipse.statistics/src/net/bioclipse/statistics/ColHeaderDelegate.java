/*******************************************************************************
 * Copyright (c) 2013  Klas Jšnsson <klas.joensson@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.statistics;

import net.bioclipse.statistics.editors.MatrixEditor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * Handles the column-header toggle-button.
 * 
 * @author Klas Jšnsson (klas.joensson@gmail.com)
 *
 */
public class ColHeaderDelegate implements IEditorActionDelegate {

    private IEditorPart targetPart;
    
    public void run( IAction action ) {
        if (targetPart != null && action != null) 
            if (targetPart instanceof MatrixEditor) 
                action.setChecked( ((MatrixEditor) targetPart).runColumnHeaderAction() );
        
    }

    public void selectionChanged( IAction action, ISelection selection ) {
        if (targetPart instanceof MatrixEditor) 
            action.setChecked( ((MatrixEditor) targetPart).hasColumnHeader() );
    }

    public void setActiveEditor( IAction action, IEditorPart targetEditor ) {
        targetPart = targetEditor;
        if (targetPart instanceof MatrixEditor) {
            action.setChecked( ((MatrixEditor) targetPart).hasColumnHeader() );
            ((MatrixEditor) targetPart).addActionDelegate( this );
        }
    }

}
