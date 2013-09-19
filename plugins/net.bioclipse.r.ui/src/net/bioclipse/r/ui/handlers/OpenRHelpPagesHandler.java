package net.bioclipse.r.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;

/**
 * Handler for opening the internal R help pages 
 * in the default system browser
 *
 * @authors valyo
 *
 */
public class OpenRHelpPagesHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/net.bioclipse.r.ui/html/rtroubleshooting.html");
		return null;
	}

}