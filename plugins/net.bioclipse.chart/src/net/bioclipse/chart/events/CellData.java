/**
 * 
 */
package net.bioclipse.chart.events;

/**
 * @author EskilA
 *
 */
public class CellData 
{
	//Name of the column containing the selected cell
	private String colName;
	//Row of the selected cell
	private int row;
	//Value of the selected cell
	private double value;
	
	public CellData()
	{
		
	}
	
	public CellData( String colName, int row, double value )
	{
		this.colName = colName;
		this.row = row;
		this.value = value;
	}
	
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	
}
