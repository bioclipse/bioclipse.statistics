/*******************************************************************************
 * Copyright (c) 2012  Klas J�nsson <klas.joensson@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.chart.ui.business;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

import net.bioclipse.chart.ui.Activator;

/**
 * Factory used for giving the manager to extension points.
 *
 * e.g. the extension point that is used for getting manager into the scripting
 * environment.
 *
 * @author jonalv
 */
public class ChartManagerFactory
       implements IExecutableExtension, IExecutableExtensionFactory {

    private Object manager;

    public void setInitializationData( IConfigurationElement config,
                                       String propertyName,
                                       Object data ) throws CoreException {

        manager = Activator.getDefault().getJavaScripChartManager();

        if ( manager == null ) {
            throw new IllegalStateException(
                          "Could not get the JavaScript flavoured " +
                          "ChartManager" );
        }
    }

    public Object create() throws CoreException {
        return manager;
    }
}
