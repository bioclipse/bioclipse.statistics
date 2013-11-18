/* ***************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.bioclipse.model.PlotPointData;

//import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class ChartSelection implements IStructuredSelection
{
	private List<PlotPointData> points;
	private ChartDescriptor descriptor;
	
	public ChartDescriptor getDescriptor() {
		return descriptor;
	}
	
	public boolean addAll(ChartSelection arg0) {
	    if (points.addAll(arg0.toList())){
	        updateDescr();
	        return true;
	    }
	    return false;
	}

	public boolean addAll(Collection<? extends PlotPointData> arg0) {
	    if (points.addAll(arg0)){
            updateDescr();
            return true;
        }
        return false;
	}

	public boolean addAll(int arg0, Collection<? extends PlotPointData> arg1) {
	    if (points.addAll(arg0, arg1)){
            updateDescr();
            return true;
        }
        return false;
	}

	public void setDescriptor(ChartDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public ChartSelection()
	{
		points = new ArrayList<PlotPointData>();
	}
	
	public void clear() {
		points.clear();
	}

	/**
	 * @param ppd ChartPoint to add to selection
	 */
	public boolean addPoint( PlotPointData ppd )
	{
		if( !points.contains(ppd)){
			points.add(ppd);
			updateDescr();
			return true;
		}
		return false;
	}
	
	public boolean contains(Object arg0) {
		return points.contains(arg0);
	}

	/**
	 * @param ppd ChartPoint to remove from selection
	 */
	public boolean removePoint( PlotPointData ppd)
	{
		if( points.contains(ppd)){
			points.remove(ppd);
			updateDescr();
			return true;
		}
		return false;
	}
	
	public boolean isEmpty() {
		return points.isEmpty();
	}

	public Object getFirstElement() {
		if( points.size() == 0)
			return null;
		return points.get(0);
	}

	public Iterator<PlotPointData> iterator() {
		return points.iterator();
	}

	public int size() {
		return points.size();
	}

	public Object[] toArray() {
		return points.toArray();
	}

	public List<PlotPointData> toList() {
		return points;
	}
	
	/**
	 * Adds some extra info to the properties view.
	 */
	private void updateDescr() {
	    if (!points.isEmpty()) {
	        if (points.size() == 1) {
	            PlotPointData ppd = points.get( 0 );
	            IPropertyDescriptor[] descriptors = new IPropertyDescriptor[3];
	            descriptors[0] = 
	                    new TextPropertyDescriptor(ChartConstants.X_VALUE, 
	                                               ppd.getXColumn());
	            descriptors[1] = 
	                    new TextPropertyDescriptor(ChartConstants.Y_VALUE, 
	                                               ppd.getYColumn());
	            ppd.addPropertyDescriptors( descriptors );

	            descriptors[2] = 
                        new TextPropertyDescriptor(ChartConstants.SOURCE, 
                                                   ChartConstants.SOURCE);
                ppd.addPropertyDescriptors( descriptors );
                
	        } else {
	            double sum = 0;
	            Number max = null, min = null;
	            for(PlotPointData ppd:points) {
	                Object obj = ppd.getPropertyValue( ChartConstants.Y_VALUE );
	                if (obj instanceof Number) {
	                    double value = ((Number) obj).doubleValue();
	                    sum += value;
	                    if (max == null || value > max.doubleValue()) 
	                        max = value;
	                    if (min == null || value < min.doubleValue()) 
	                        min = value;
	                }
	            }
	            
	            IPropertyDescriptor[] descriptors =  new IPropertyDescriptor[5];
	            descriptors[0] = 
	                    new TextPropertyDescriptor(ChartConstants.ITEMS, 
	                                               "Number points selected");
	            descriptors[1] = 
	                    new TextPropertyDescriptor(ChartConstants.AVERAGE_VALUE, 
	                                               "Average");
	            descriptors[2] = 
	                    new TextPropertyDescriptor(ChartConstants.MAX_VALUE, 
	                                               "Max value");
	            descriptors[3] = 
	                    new TextPropertyDescriptor(ChartConstants.MIN_VALUE, 
	                                               "Min value");
	            descriptors[4] = 
	                    new TextPropertyDescriptor(ChartConstants.SOURCE, 
	                                               ChartConstants.SOURCE);
	            
	            for(PlotPointData ppd:points) {
	                ppd.addPropertyDescriptors( descriptors );
	                ppd.setPropertyValue( ChartConstants.ITEMS, points.size() );
	                ppd.setPropertyValue( ChartConstants.AVERAGE_VALUE, sum/points.size() );
	                ppd.setPropertyValue( ChartConstants.MAX_VALUE, max );
	                ppd.setPropertyValue( ChartConstants.MIN_VALUE, min );
	            }
	        }
	    }
	}

}
