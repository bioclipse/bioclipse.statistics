/**
 * 
 */
package net.bioclipse.model;

/**
 * @author EskilA
 *
 */
public class Cell 
{
	private double value;
	private int rowIndex;
	
	public Cell( double value, int row )
	{
		this.value = value;
		this.rowIndex = row;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	
	
}
