package net.bioclipse.model;

import org.eclipse.jface.action.Action;


/**
 * Interface for creating actions pertaining to charts
 * @author Eskil Andersen
 *
 */
public interface ChartActionFactory 
{
	public Action createSaveAsSvgAction();
		
}
