package net.bioclipse.chart.events;


/**
 * Interface for classes providing events when cells changes that effects charts
 * @author Eskil Andersen
 *
 */
public interface CellChangeProvider {
	public void notifyListeners(CellChangedEvent event);
	public void addListener( CellChangeListener listener);
	public void removeListener( CellChangeListener listener);
}
