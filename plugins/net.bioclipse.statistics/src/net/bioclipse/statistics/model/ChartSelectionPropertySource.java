/* ***************************************************************************
 * Copyright (c) 2012 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/
package net.bioclipse.statistics.model;

import java.util.ArrayList;
import java.util.HashMap;
import net.bioclipse.model.PcmLineChartDataset;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 * 
 * @author Klas Jšnsson (klas.joensson@gmail.com), aka konditorn
 *
 */
public class ChartSelectionPropertySource implements ISelection, IPropertySource, IAdaptable {

    protected static final String ITEMS = "Items selected";
    protected static final String SELECTED_ROWS = "Rows";

    // Specific for histogram and bar-diagrams
    protected static final String MAX_VALUE = "Max value";
    protected static final String MIN_VALUE = "Min value";

    // Specific for the plots
    protected static final String X_VALUE = "X-Value";
    protected static final String Y_VALUE = "Y-value";

    private HashMap<String, Object> valueMap;
    private ArrayList<IPropertyDescriptor> properties;
    private ChartEntity chartEntity;
    private static final Logger logger = Logger.getLogger(ChartSelectionPropertySource.class);

    public ChartSelectionPropertySource(ChartEntity ce) { 
        chartEntity = ce;
        properties = new ArrayList<IPropertyDescriptor>();
        valueMap = new HashMap<String, Object>();

        addPropertiesWithValues();
    }

    private void addPropertiesWithValues() {

        if (chartEntity instanceof XYItemEntity) {
            int item = ((XYItemEntity) chartEntity).getItem();           
            int series = ((XYItemEntity) chartEntity).getSeriesIndex();
            XYDataset dataset = ((XYItemEntity) chartEntity).getDataset();

            if (dataset instanceof HistogramDataset) {
                /* The click was in a histogram */ 
                double xStart = ((HistogramDataset) dataset).getStartXValue( series, item );
                PropertyDescriptor descriptor0 = new TextPropertyDescriptor(MAX_VALUE, "Max value");
                properties.add( descriptor0 );
                valueMap.put( MAX_VALUE, xStart );

                double xEnd = ((HistogramDataset) dataset).getEndXValue( series, item );
                PropertyDescriptor descriptor1 = new TextPropertyDescriptor(MIN_VALUE, " Min value");
                properties.add( descriptor1 );
                valueMap.put( MIN_VALUE, xEnd );

                double items = ((HistogramDataset) dataset).getEndYValue( series, item );
                PropertyDescriptor descriptor2 = new TextPropertyDescriptor(ITEMS, "No of Selected values");
                properties.add( descriptor2 );
                valueMap.put( ITEMS, items );

                logger.debug("Histogram data with max: "+xStart+", min: " + xEnd + "and items: "+items);
            } else if (dataset instanceof DefaultXYDataset) {
                /* The click was in  either a scatter plot or a time series*/
                double x = ((DefaultXYDataset) dataset).getXValue( series, item );
                PropertyDescriptor descriptor0 = new TextPropertyDescriptor(X_VALUE, "X-Value");
                properties.add( descriptor0 );
                valueMap.put( X_VALUE, x );

                double y = ((DefaultXYDataset) dataset).getYValue( series, item );
                PropertyDescriptor descriptor1 = new TextPropertyDescriptor(Y_VALUE, "y-value");
                properties.add( descriptor1 );
                valueMap.put( Y_VALUE, y );

                PropertyDescriptor descriptor2 = new TextPropertyDescriptor(ITEMS, "Selected row");
                properties.add( descriptor2 );
                valueMap.put( ITEMS, item );

                logger.debug("Scatter Plot/Time series data with x-value: "+x+", y-value: "+y+" and is from row "+item);
            } else if (dataset instanceof PcmLineChartDataset) {
                /* The click was in  a line plot */
                double x = ((PcmLineChartDataset) dataset).getXValue( series, item );
                PropertyDescriptor descriptor0 = new TextPropertyDescriptor(X_VALUE, "X-value");
                properties.add( descriptor0 );
                valueMap.put( X_VALUE, x );

                double y = ((PcmLineChartDataset) dataset).getYValue( series, item );
                PropertyDescriptor descriptor1 = new TextPropertyDescriptor(Y_VALUE, "y-value");
                properties.add( descriptor1 );
                valueMap.put( Y_VALUE, y );

                PropertyDescriptor descriptor2 = new TextPropertyDescriptor(ITEMS, "Selected row");
                properties.add( descriptor2 );
                valueMap.put( ITEMS, item );

                logger.debug("Line plot data with x-value: "+x+" and y-value: "+y+" and is from row "+item);
            } else {
                logger.error("No support for this dataset: " + dataset);
            }                
        } else if (chartEntity instanceof CategoryItemEntity) {
            /* The click was in a bar-diagram. Is it used in BC?*/
            Comparable columnKey = ((CategoryItemEntity) chartEntity).getColumnKey();
            Comparable rowKey = ((CategoryItemEntity) chartEntity).getRowKey();
            System.out.println("Column key: "+ columnKey);
            System.out.println("Row key: "+ rowKey);
            CategoryDataset dataset = ((CategoryItemEntity) chartEntity).getDataset();
            if (dataset instanceof DefaultCategoryDataset) {
                Number value = ((DefaultCategoryDataset) dataset).getValue( rowKey, columnKey );
                logger.debug("Bar-diagram data with the value: "+value);
            }else {
                logger.error("No support for this dataset: " + dataset);
            }
        }
        else {
            logger.error("No support for this ChartEntity: "+chartEntity);
        }
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

}
