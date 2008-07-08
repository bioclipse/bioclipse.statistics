package net.bioclipse.model;

import java.awt.Point;

/**
 * Model of a point in a plot, contains keys to find the cells it was made of
 * The cells being the contributors of the points x and y coordinates
 * @author EskilA
 */
public class PlotPointData
{
	private int rowNumber;
	private String xColumn, yColumn;
	private Point p;
	
	public PlotPointData()
	{
		
	}
	
	public PlotPointData( int rowNumber, String xColumn, String yColumn )
	{
		this.rowNumber = rowNumber;
		this.xColumn = xColumn;
		this.yColumn = yColumn;
	}
	
	public void setDataPoint(int i, int j){
		p = new Point(i,j);
	}
	
	public Point getDataPoint(){
		return p;
	}
	
	public int getRowNumber() {
		return rowNumber;
	}
	public void setRownumber(int rownumber) {
		this.rowNumber = rownumber;
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
	
	
}

