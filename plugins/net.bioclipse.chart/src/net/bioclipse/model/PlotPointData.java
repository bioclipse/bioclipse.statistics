/* ***************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Model of a point in a plot, contains keys to find the cells it was made of
 * The cells being the contributors of the points x and y coordinates
 * @author EskilA
 */
public class PlotPointData implements IPropertySource
{
	private int rowNumber;
	private String xColumn, yColumn;
	private Point p;
	private ArrayList<IPropertyDescriptor> descriptors;
	private HashMap<String, Object> valueMap;
	
	public PlotPointData()
	{
		descriptors = new ArrayList<IPropertyDescriptor>();
		valueMap = new HashMap<String, Object>();
	}
	
	public PlotPointData( int rowNumber, String xColumn, String yColumn )
	{
	    descriptors = new ArrayList<IPropertyDescriptor>();
        valueMap = new HashMap<String, Object>();
		this.rowNumber = rowNumber;
		this.xColumn = xColumn;
		this.yColumn = yColumn;
		
//        descriptors.add( new TextPropertyDescriptor(ChartConstants.X_COLUMN, "X column") );
//        valueMap.put( ChartConstants.X_COLUMN, xColumn );
//        descriptors.add( new TextPropertyDescriptor(ChartConstants.Y_COLUMN, "Y column") );
//        valueMap.put( ChartConstants.Y_COLUMN, yColumn );
	}
	
	public void setDataPoint(int i, int j){
		p = new Point(i,j);
        descriptors.add( new TextPropertyDescriptor(ChartConstants.POINT, "Point") );
        valueMap.put( "point", p );
	}
	
	public Point getDataPoint(){
		return p;
	}
	
	public int getRowNumber() {
		return rowNumber;
	}
	public void setRownumber(int rownumber) {
		this.rowNumber = rownumber;
		IPropertyDescriptor test = new TextPropertyDescriptor(ChartConstants.ROW_NUMBER, "Row number");
		if (!descriptors.contains( test ))
		    descriptors.add( test );
		valueMap.put( "rowNumber", rownumber );
	}
	public String getXColumn() {
		return xColumn;
	}
	public void setXColumn(String column) {
		xColumn = column;
		IPropertyDescriptor test = new TextPropertyDescriptor(ChartConstants.X_COLUMN, "X Column");
		if (!descriptors.contains( test ))
		    descriptors.add( test );
		valueMap.put( "xColumn", column );
	}
	public String getYColumn() {
		return yColumn;
	}
	public void setYColumn(String column) {
		yColumn = column;
	      IPropertyDescriptor test = new TextPropertyDescriptor(ChartConstants.Y_COLUMN, "Y Column");
	        if (!descriptors.contains( test ))
	            descriptors.add( test );
	        valueMap.put( "yColumn", column );
	}

	public boolean equals(Object arg0) {
		if( this == arg0 )
			return true;
		if( arg0 == null)
			return false;
		if( (arg0 instanceof PlotPointData))
		{
			PlotPointData comparison = (PlotPointData) arg0;
			if( comparison.getXColumn().equals(this.getXColumn()) && comparison.getYColumn().equals(this.getYColumn())
					&& comparison.getRowNumber() == this.rowNumber )
			{
				return true;
			}
		}	
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 73;
		hash = 31 * hash + rowNumber;
		hash = 31 * hash + (null == xColumn ? 0 : xColumn.hashCode());
		hash = 31 * hash + (null == yColumn ? 0 : yColumn.hashCode());
		return hash;
	}

	@Override
	public String toString() {
		return "Row number: " + rowNumber + " X Column: " + xColumn + " Y Column: " + yColumn + " " + super.toString();
	}
	
    public IPropertyDescriptor[] getPropertyDescriptors() {    
        IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[descriptors.size()];
        for (int i=0; i< descriptors.size();i++){
            propertyDescriptors[i]=(IPropertyDescriptor) descriptors.get(i);
        }
        return propertyDescriptors;
    }
	
    public Object getPropertyValue( Object id ) {       
        return valueMap.get( id );
    }

    public Object getEditableValue() {
        return null;
    }

    public boolean isPropertySet( Object id ) {
        return valueMap.containsKey( id );
    }

    public void resetPropertyValue( Object id ) {    }

    public void setPropertyValue( Object id, Object value ) {
        if (id instanceof String) {
            String idStr = (String) id;
            valueMap.put( idStr, value );
        }
    }
    
    public void addPropertyDescriptors(IPropertyDescriptor[] newDescriptors) {
        for (IPropertyDescriptor propDescr:newDescriptors)
            if (propDescr != null && !hasDescriptor( propDescr ))
                descriptors.add( propDescr );
    }
    
    private boolean hasDescriptor(IPropertyDescriptor newDescr) {
        if (descriptors.isEmpty() || newDescr == null)
            return false;
        
        boolean result = false;
        for (IPropertyDescriptor oldDescr:descriptors) {
                if(oldDescr.getId().equals( newDescr.getId() ))
                    result = true;
        }
        
        return result;
    }
    
    public void addValues(HashMap<String, Object> newValues) {
        valueMap.putAll( newValues );
    }
}

