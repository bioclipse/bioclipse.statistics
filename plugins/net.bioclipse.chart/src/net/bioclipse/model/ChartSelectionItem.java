/* ***************************************************************************
 * Copyright (c) 2012 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/
package net.bioclipse.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.bioclipse.chart.ChartConstants;
import net.bioclipse.chart.IChartDescriptor;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 * 
 * @author Klas Jšnsson (klas.joensson@gmail.com), aka konditorn
 *
 */
public class ChartSelectionItem implements IStructuredSelection, IPropertySource, IAdaptable {
    
    private HashMap<String, Object> valueMap;
    private ArrayList<IPropertyDescriptor> properties;
    private ChartEntity chartEntity;
    private IChartDescriptor chartDescriptor;
    private static final Logger logger = Logger.getLogger(ChartSelectionItem.class);
    
    public ChartSelectionItem(ChartEntity ce, IChartDescriptor cd) { 
        chartEntity = ce;
        properties = new ArrayList<IPropertyDescriptor>();
        valueMap = new HashMap<String, Object>();
        chartDescriptor = cd;
        addPropertiesWithValues();
        addMoreInfo();
    }
    
    /**
     * This method adds information from the selected point.
     */
    private void addPropertiesWithValues() {

        if (chartEntity instanceof XYItemEntity) {
            int item = ((XYItemEntity) chartEntity).getItem();           
            int series = ((XYItemEntity) chartEntity).getSeriesIndex();
            XYDataset dataset = ((XYItemEntity) chartEntity).getDataset();
            String xLabel = chartDescriptor.getXLabel();
            String yLabel = chartDescriptor.getYLabel();
            if (dataset instanceof HistogramDataset) {
                /* The click was in a histogram */ 
                double xStart = ((HistogramDataset) dataset).getStartXValue( series, item );
                PropertyDescriptor descriptor0 = new TextPropertyDescriptor(ChartConstants.MAX_VALUE, "Max value");
                properties.add( descriptor0 );
                valueMap.put( ChartConstants.MAX_VALUE, xStart );
           
                double xEnd = ((HistogramDataset) dataset).getEndXValue( series, item );
                PropertyDescriptor descriptor1 = new TextPropertyDescriptor(ChartConstants.MIN_VALUE, "Min value");
                properties.add( descriptor1 );
                valueMap.put( ChartConstants.MIN_VALUE, xStart);
              
                double items = ((HistogramDataset) dataset).getEndYValue( series, item );
                PropertyDescriptor descriptor2 = new TextPropertyDescriptor(ChartConstants.ITEMS, "No of Selected values");
                properties.add( descriptor2 );
                valueMap.put( ChartConstants.ITEMS, items );
                
                logger.debug("Histogram data with max: "+xStart+", min: " + xEnd + "and items: "+items);
            } else if (dataset instanceof DefaultXYDataset) {
                /* The click was in  either a scatter plot or a time series*/
                double x = ((DefaultXYDataset) dataset).getXValue( series, item );
                PropertyDescriptor descriptor0 = new TextPropertyDescriptor(ChartConstants.X_VALUE, xLabel);
                properties.add( descriptor0 );
                valueMap.put( ChartConstants.X_VALUE, x );
                
                double y = ((DefaultXYDataset) dataset).getYValue( series, item );
                PropertyDescriptor descriptor1 = new TextPropertyDescriptor(ChartConstants.Y_VALUE, yLabel);
                properties.add( descriptor1 );
                valueMap.put( ChartConstants.Y_VALUE, y );
                               
                logger.debug("Scatter Plot/Time series data with x-value: "+x+", y-value: "+y+" and is from row "+item);
            } else if (dataset instanceof PcmLineChartDataset) {
                /* The click was in  a line plot */
                double x = ((PcmLineChartDataset) dataset).getXValue( series, item );
                PropertyDescriptor descriptor0 = new TextPropertyDescriptor(ChartConstants.X_VALUE, xLabel);
                properties.add( descriptor0 );
                valueMap.put( ChartConstants.X_VALUE, x );
                
                double y = ((PcmLineChartDataset) dataset).getYValue( series, item );
                PropertyDescriptor descriptor1 = new TextPropertyDescriptor(ChartConstants.Y_VALUE, yLabel);
                properties.add( descriptor1 );
                valueMap.put( ChartConstants.Y_VALUE, y );
                
                logger.debug("Line plot data with x-value: "+x+" and y-value: "+y+" and is from row "+item);
            } else {
                logger.error("No support for this dataset: " + dataset);
            }                
        } else if (chartEntity instanceof CategoryItemEntity) {
            /* The click was in a bar-diagram. */
            Comparable columnKey = ((CategoryItemEntity) chartEntity).getColumnKey();
            Comparable rowKey = ((CategoryItemEntity) chartEntity).getRowKey();
            CategoryDataset dataset = ((CategoryItemEntity) chartEntity).getDataset();
            Number value = dataset.getValue( rowKey, columnKey );
            PropertyDescriptor descriptor0 = new TextPropertyDescriptor(ChartConstants.X_VALUE, "Column");
            properties.add( descriptor0 );
            valueMap.put( ChartConstants.X_VALUE, columnKey );
            
            PropertyDescriptor descriptor1 = new TextPropertyDescriptor(ChartConstants.Y_VALUE, "Row");
            properties.add( descriptor1 );
            valueMap.put( ChartConstants.Y_VALUE, rowKey );
            PropertyDescriptor descriptor2 = new TextPropertyDescriptor(ChartConstants.ITEMS, "Value");
            properties.add( descriptor2 );
            valueMap.put( ChartConstants.ITEMS, value );
        } else if (chartEntity instanceof PlotEntity) {
            /* The chartEntity is of this type when just clicking in the plot 
             * with out selecting anything. The things added in the method 
             * addMoreInfo() is enough. */
        } else { 
            logger.error("No support for this ChartEntity: "+chartEntity);
        }
    }
    
    /**
     * This method adds information from the source of the chart, 
     * i.e. the editor.
     * 
     * @param ce
     */
    private void addMoreInfo() {
        if (chartDescriptor == null)
            return;
        if (chartDescriptor.getPlotType() != ChartConstants.plotTypes.HISTOGRAM && chartDescriptor.getSource() != null) {
            int item;
            if (valueMap.isEmpty() || !valueMap.containsKey( ChartConstants.ITEMS )) {
                item = 0;
            } else {
                item = (Integer) valueMap.get( ChartConstants.ITEMS );
                item += chartDescriptor.getSourceIndices()[0];
            }
            valueMap.put( ChartConstants.ITEMS, item );
        }
        
        PropertyDescriptor descriptor0 = new TextPropertyDescriptor(ChartConstants.SOURCE, "Source");
        properties.add( descriptor0 );
        valueMap.put( ChartConstants.SOURCE, chartDescriptor.getSourceName());

    }
    
    public IChartDescriptor getChartDescriptor() {
        return chartDescriptor;
    }
    
    public Object getEditableValue() {
        return null;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[properties.size()];
        for (int i=0; i< properties.size();i++){
            propertyDescriptors[i]=(IPropertyDescriptor) properties.get(i);
        }
        return propertyDescriptors;
    }

    public Object getPropertyValue( Object id ) {
        if (valueMap.containsKey( id )) 
            return valueMap.get( id );
        
        return null;
    }

    public boolean isPropertySet( Object id ) {
        return valueMap.containsKey( id );
    }

    public void resetPropertyValue( Object id ) {
        valueMap.remove( id );
    }

    public void setPropertyValue( Object id, Object value ) {
        if (id instanceof String)
            valueMap.put( (String)id, value );
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public Object getAdapter( Class adapter ) {
        if(adapter.isAssignableFrom( IPropertySource.class )) {
            return this;
        }
        return null;
    }

    public Object getFirstElement() {
        List<Object> values = toList();
        if (values.isEmpty())
            return null;
        return values.get( 0 );
    }

    public Iterator<IPropertyDescriptor> iterator() { 
        return properties.iterator();
    }

    public int size() {
        return valueMap.keySet().size();
    }

    public Object[] toArray() {
        Set<String> keys = valueMap.keySet();
        int length = keys.size();
        Object[] values = new Object[length];
        int i = 0;
        for (String key: keys){
            values[i] = valueMap.get( key );
            i++;
        }
        
        return values;
    }

    public List<Object> toList() {
        Set<String> keys = valueMap.keySet();
        List<Object> values = new ArrayList<Object>();
        for (String key: keys){
            values.add( valueMap.get( key ) );
        }
        
        return values;
    }
    
    public HashMap<String, Object> getValueMap() {
        return valueMap;
    }
}
