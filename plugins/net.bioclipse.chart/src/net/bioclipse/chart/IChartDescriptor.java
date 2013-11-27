package net.bioclipse.chart;

import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;


public interface IChartDescriptor extends IAdaptable {

    /**
     * Returns the title of the chart.
     * 
     * @return The title
     */
    public String getTitle();
    
    /**
     * Returns the label of the x-axis.
     * 
     * @return The x-label
     */
    public String getXLabel();
    
    /**
     * Returns x-values of the chart.
     * 
     * @return The x-values
     */
    public double[] getXValues();
    
    /**
     * Returns a specific x-value. 
     * 
     * @param index The index of the wanted  x-value
     * @return The x-value with index <code>index</code>
     */
    public double getXValue(int index); 
    
    /**
     * Returns the label of the y-axis.
     * 
     * @return The y-label
     */
    public String getYLabel();
    
    /**
     * Returns y-values of the chart.
     * 
     * @return The y-values
     */
    public double[] getYValues();
    
    /**
     * Returns a specific y-value. 
     *  
     * @param index The index of the wanted  y-value
     * @return The y-value with index <code>index</code>
     */
    public double getYValue(int index);
    
    /**
     * Returns the <code>IEditorPart</code> that is used to displays the 
     * resource that contains the values that made up this chart. Might be 
     * <code>NULL</code> if the plot was made from e.g. the JS-console.
     * 
     * @return This charts editor or <code>NULL</code> 
     */
    /* TODO Should i really be a IEditorPart here? Is there some super class 
     * that is better to use?*/
    public IEditorPart getSource(); 
    
    /**
     * Returns an <code>int</code> that indicate what type of plot it is.
     * 
     * @return The plot type
     */
    public ChartConstants.plotTypes getPlotType();
    
    /**
     * Returns the row numbers of this charts values.
     * 
     * @return The values row numbers
     */
    public int[] getSourceIndices();
    
    /**
     * Returns the row and column number of the cells that contains the values 
     * sued for creating this chart stored in a <code>Point</code> for each 
     * value (both x- and y-values)
     * 
     * @return The row and column number as points
     */
    public Point[] getOrigenCells();
    
    /**
     * Returns the name of the source (e.g. the file name) for the values that 
     * was used to create this chart. This is shown in the properties view as 
     * the property "Source".
     *  
     * @return The source name
     */
    public String getSourceName();
    
    /**
     * Makes it possibly to set a name on the source if no <code>
     * IResource</code> is associated with this chart (e.g. if the chart was 
     * created from the JS-console).
     * 
     * @param name The name of the source
     */
    public void setSourceName(String name);
    
   /**
    * Returns the <code>IResource</code> that is associated with this chart, if
    * there's no such resource an exception is thrown.
    * 
    * @return The <code>IResource</code> associated with this chart
    * @throws FileNotFoundException if no <code>IResource</code> is associated 
    * with this chart 
    */
    public IResource getResource() throws FileNotFoundException;
    
    /**
     * Sets the item labesl for thsi chart. I.e. what should be shown in the 
     * tool-tips and point labels of the chart. If not set the y-value will be 
     * used.
     * It has to have the same size as the the number of x-and y-values, if not
     * an exception is thrown. 
     * 
     * @param labels The labels in a <code>String</code> array 
     * @throws IllegalArgumentException If the array is to long or to short
     */
    public void setItemLabels(String[] labels) throws IllegalArgumentException;
    
    /**
     * Returns a specific item label.
     *   
     * @param index The index of the wanted item label
     * @return The item label with index <code>index</code>
     * @throws IllegalAccessError If no item  label with that the <code>index
     * </code> exist
     */
    public String getItemLabel(int index) throws IllegalAccessError;
    
    /**
     * Changes a specific item label.
     *   
     * @param index The index of the item label to be changed
     * @throws IllegalAccessError If no item  label with that the <code>index
     * </code> exist
     */
    public void setItemLabel(int index, String label) throws IllegalAccessError;
    
    /**
     * Returns the item labels, if not set it will return an empty array.
     * 
     * @return The item labels or an empty array
     */
    public String[] getItemLabels();
    
    /**
     * A method to check if the are any labels set for theis chart.
     * 
     * @return <code>true</code> if the labels has been set
     */
    public boolean hasItemLabels();
    
    /**
     * Removes the items labels, if they are not wanted (e.g. a matrix with 
     * row-headers will per default get it's row-headers as point labels, 
     * if that's not wanted this method can be used to remove them without
     * Manipulating the matrix.
     */
    public void removeItemLabels();
    
    public List<ChartPoint> handleEvent( ISelection selection ); 
    
}
