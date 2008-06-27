package net.bioclipse.model;

import java.util.Vector;

import org.eclipse.ui.IEditorPart;

/**
 * @author EskilA
 * Used by ChartDialog to represent columns in a spreadsheet
 */
public class ColumnData
{
	private String label;
	private Vector<Cell> data;
	private IEditorPart dataSource;

	public ColumnData()
	{
		data = new Vector<Cell>();
		label = "";
	}
	
	public ColumnData(String label)
	{
		data = new Vector<Cell>();
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public double[] getValues()
	{
//		Double[] values = (Double[])data.toArray();
//		double[] doubleValues = new double[values.length];
//		for( int i = 0; i < values.length; i++)
//		{
//			doubleValues[i] = values[i].doubleValue();
//		}
//		return doubleValues;
		double[] doubleValues = new double[data.size()];
		for( int i = 0; i < data.size(); i++)
		{
			doubleValues[i] = ((Cell)data.get(i)).getValue();
		}
		return doubleValues;
	}
	
	public int[] getIndices()
	{
		int[] indices = new int[data.size()];
		for( int i = 0; i < data.size(); i++)
		{
			indices[i] = ((Cell)data.get(i)).getRowIndex();
		}
		return indices;
	}
	
	/**
	 * 
	 * @param source the editor from where the data originates
	 */
	public void setDataSource(IEditorPart source){
		dataSource = source;
	}
	
	
	/**Add a double value to the column
	 * @param value
	 */
	public void add(double value, int row)
	{
		data.add(new Cell(value,row));
	}

	@Override
	public boolean equals(Object obj) {
		if( !(obj instanceof String) )
			return false;
		String compLabel = (String)obj;
		if( compLabel.equals(this.label))
			return true;
		return false;
	}

	/**
	 * 
	 * @return The editor from where the data originates
	 */
	public IEditorPart getDataSource() {
		return dataSource;
	}	
}
