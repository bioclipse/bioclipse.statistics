 /*******************************************************************************
 * Copyright (c) 2007 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org/legal/epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/

package net.bioclipse.r;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

public class TextRFactory implements IExecutableExtension, 
                                     IExecutableExtensionFactory {

	private Object textR;
	
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		
		try {
			textR = new TextR(null);
		} catch (NoRException e) {
			e.printStackTrace();
		}
	}

	public Object create() throws CoreException {
		return textR;
	}
}
