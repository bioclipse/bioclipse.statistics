package net.bioclipse.model;

/**
 * Model of a point in a plot, contains keys to find the cells it was made of
 * The cells being the contributors of the points x and y coordinates
 * @author EskilA
 */
public class PlotPointData
{
	private int rowNumber;
	private String xColumn, yColumn;
	
	public PlotPointData()
	{
		
	}
	
	public PlotPointData( int rowNumber, String xColumn, String yColumn )
	{
		this.rowNumber = rowNumber;
		this.xColumn = xColumn;
		this.yColumn = yColumn;
	}
	
	public int getRownumber() {
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
					&& comparison.getRownumber() == this.rowNumber )
			{
				return true;
			}
		}	
		return false;
	}

	@Override
	public int hashCode() {
		return (xColumn.hashCode() == 0 ? 1: xColumn.hashCode()) * (yColumn.hashCode() == 0 ? 1 : yColumn.hashCode()) *
			(rowNumber == 0 ? 1 : rowNumber);
	}
}

