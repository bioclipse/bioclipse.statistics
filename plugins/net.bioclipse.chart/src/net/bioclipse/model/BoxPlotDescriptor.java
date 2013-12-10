package net.bioclipse.model;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import net.bioclipse.chart.ChartConstants.plotTypes;
import net.bioclipse.chart.ChartPoint;
import net.bioclipse.chart.IChartDescriptor;


public class BoxPlotDescriptor implements IChartDescriptor {

    private IEditorPart source;
    private int[] indices;
    private double[][] values;
    private Point[] originCells;
    private String sourceName;
    private IResource resource;
    private String chartTitle;
    private String[] itemLabels;
    
    
    public BoxPlotDescriptor(IEditorPart source, 
                               String[] itemLabels, double[][] values, 
                               Point[] originCells, String ChartTitle) {
        this.source = source;
        this.itemLabels = itemLabels;
        this.values = values;
        this.originCells = originCells;
        this.chartTitle = ChartTitle;
        if (source != null) {
            IFileEditorInput input = (IFileEditorInput) source.getEditorInput().getAdapter( IFileEditorInput.class );
            if (input != null)
                resource = input.getFile();
            sourceName = source.getTitle();
            
        } else
            sourceName = "Unknown";
        
        this.indices = new int[originCells.length];
        int index = 0;
        for (Point p:originCells)
            this.indices[index++] = p.y;
    }
    
    @Override
    public Object getAdapter( Class adapter ) {
        return null;
    }

    @Override
    public String getTitle() {
        return this.chartTitle;
    }

    @Override
    public String getXLabel() {
        return itemLabels[0];
    }

    @Override
    public double[] getXValues() {
        return values[0];
    }

    @Override
    public double getXValue( int index ) {

        return values[0][index];
    }

    @Override
    public String getYLabel() {
        if (itemLabels.length<2)
            return "";
        
        return itemLabels[1];
    }

    @Override
    public double[] getYValues() {
        if (values.length<2)
            return new double[0];
        
        return values[1];
    }

    @Override
    public double getYValue( int index ) {
        if (values.length<2)
            return Double.NaN;
        
        return values[1][index];
    }

    public List<Double> getRow(int index) {
        List<Double> row = new ArrayList<Double>();
        for(int i=0;i<values.length;i++)
            row.add( values[i][index] );
        return row;
    }
    
    public int getNumberOfRows() {
        return values[0].length;
    }
    
    public List<Double> getColumn(int index) {
        List<Double> col = new ArrayList<Double>();
        for(int i=0;i<values[index].length;i++)
            col.add( values[index][i] );
        return col;
    }
    
    public int getNumberOfColumns() {
        return values.length;
    }
    
    @Override
    public IEditorPart getSource() {
        return source;
    }

    @Override
    public plotTypes getPlotType() {
        return plotTypes.BOX_PLOT;
    }

    @Override
    public int[] getSourceIndices() {
        return indices;
    }

    @Override
    public Point[] getOrigenCells() {
        return originCells;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public void setSourceName( String name ) {
        this.sourceName = name;
    }

    @Override
    public IResource getResource() throws FileNotFoundException {
        return this.resource;
    }

    @Override
    public void setItemLabels( String[] labels )
                                                throws IllegalArgumentException {
        this.itemLabels = labels;
    }

    @Override
    public String getItemLabel( int index ) throws IllegalAccessError {
        if (hasItemLabels() && (index > 0 || index < itemLabels.length))
            return itemLabels[index];
        
        throw new IllegalAccessError( "Cant find the item label." );
    }

    @Override
    public void setItemLabel( int index, String label )
                                                       throws IllegalAccessError {
       
    }

    @Override
    public String[] getItemLabels() {
        return itemLabels;
    }

    @Override
    public boolean hasItemLabels() {
        return (itemLabels != null);
    }

    @Override
    public void removeItemLabels() {
        itemLabels = null;
    }

    @Override
    public List<ChartPoint> handleEvent( ISelection selection ) {
        return new ArrayList<ChartPoint>();
    }

    @Override
    public <T> T getAdapter( int index, Class<T> clazz ) {
        return null;
    }

    @Override
    public boolean hasToolTips() {
        return hasItemLabels();
    }

    @Override
    public String getToolTip( int index ) {
        return getItemLabel( index );
    }

}
