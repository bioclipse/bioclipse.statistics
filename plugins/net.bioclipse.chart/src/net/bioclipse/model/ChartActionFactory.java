package net.bioclipse.model;


/**
 * Interface for creating actions pertaining to charts
 * @author Eskil Andersen
 *
 */
public interface ChartActionFactory 
{
	public ChartAction createExportSvgAction();
	public ChartAction createExtportJpegAction();
	public ChartAction createExportPngAction();
}
