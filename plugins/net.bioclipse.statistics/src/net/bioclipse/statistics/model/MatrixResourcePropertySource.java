/*******************************************************************************
 * Copyright (c) 2006, 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Egon Willighagen - core API and implementation
 *******************************************************************************/
package net.bioclipse.statistics.model;
import net.bioclipse.core.domain.props.BioObjectPropertySource;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
/**
 * Provides the properties for an MatrixResource.
 * 
 * @author egonw
 */
public class MatrixResourcePropertySource extends BioObjectPropertySource implements IPropertySource{
        protected static final String COL_SIZE = "Column count";
        protected static final String ROW_SIZE = "Row count";
        private final Object AddedPropertiesTable[][] = {
                        { COL_SIZE, new TextPropertyDescriptor(COL_SIZE, "Column count")},		  
                        { ROW_SIZE, new TextPropertyDescriptor(ROW_SIZE, "Row count")},		  
        };	
        private MatrixResource item;
        public MatrixResourcePropertySource(MatrixResource item) {
                super(item);
                this.item = item;
        }
        public void addAdvancedProperties() {
                for (int i=0;i<AddedPropertiesTable.length;i++) {				
                        // Add each property supported.
                        PropertyDescriptor descriptor;
                        descriptor = (PropertyDescriptor)AddedPropertiesTable[i][1];
                        descriptor.setCategory("Matrix Properties");
                        getProperties().add((IPropertyDescriptor)descriptor);
                }
                // Build the hashmap of property->value pair
                getValueMap().put(COL_SIZE, item.getColumnCount());
                getValueMap().put(ROW_SIZE, item.getRowCount());
        }
        public void removeAdvancedProperties() {
                for (int i=0;i<AddedPropertiesTable.length;i++) {				
                        // Add each property supported.
                        PropertyDescriptor descriptor;
                        descriptor = (PropertyDescriptor)AddedPropertiesTable[i][1];
                        getProperties().remove((IPropertyDescriptor)descriptor);
                }
                // Build the hashmap of property->value pair
                getValueMap().remove(COL_SIZE);
                getValueMap().remove(ROW_SIZE);
        }
}
