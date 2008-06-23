package net.bioclipse.model;

/**
 * Model of a point in a plot, contains keys to find the cells it was made of
 * The cells being the contributors of the points x and y coordinates
 * @author EskilA
 */
public class PlotPointData 
{
	private int rownumber;
	private String xColumn, yColumn;
	
	public PlotPointData()
	{
		
	}
	
	public PlotPointData( int rowNumber, String xColumn, String yColumn )
	{
		this.rownumber = rowNumber;
		this.xColumn = xColumn;
		this.yColumn = yColumn;
	}
	
	public int getRownumber() {
		return rownumber;
	}
	public void setRownumber(int rownumber) {
		this.rownumber = rownumber;
	}
	public String getXColumn() {
		return xColumn;
	}
	public void setXColumn(String column) {
		xColumn = column;
	}
	public String getYColumn() {
		return yColumn;
	}
	public void setYColumn(String column) {
		yColumn = column;
	}	
}

