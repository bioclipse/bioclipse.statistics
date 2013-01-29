/**
 * Util methods for Bioclipse workbench
 *
 * @author valyo
 *
 */

package net.bioclipse.r.ui.util;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchSite;

public class WorkbenchUIUtil {

	public static ISelection getCurrentSelection(final Object context) {
		if (context instanceof IEvaluationContext) {
			final IEvaluationContext evaluationContext = (IEvaluationContext) context;
			Object object = evaluationContext.getVariable(ISources.ACTIVE_SITE_NAME);
			if (object instanceof IWorkbenchSite) {
				final IWorkbenchSite site = (IWorkbenchSite) object;
				final ISelectionProvider selectionProvider = site.getSelectionProvider();
				if (selectionProvider != null) {
					return selectionProvider.getSelection();
				}
				return null;
			}
			else {
				object = evaluationContext.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
				if (object instanceof ISelection) {
					return (ISelection) object;
				}
			}
		}
		return null;
	}

}
