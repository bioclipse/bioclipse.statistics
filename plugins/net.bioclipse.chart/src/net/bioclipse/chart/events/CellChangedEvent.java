package net.bioclipse.chart.events;

/**
 * Fire this event when a cell changes whose data is displayed in a chart so it can update its
 * graphical display of the data
 * @author Eskil Andersen
 *
 */
public class CellChangedEvent {

	private CellData cellData;

	public void setCellData(CellData cellData) {
		this.cellData = cellData;
	}

	public CellData getCellData() {
		return cellData;
	}
}
