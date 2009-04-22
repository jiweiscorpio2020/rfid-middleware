/**
 * 
 */
package org.rifidi.edge.client.ale.ecspecview.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec;
import org.rifidi.edge.client.ale.ecspecview.views.ALEEditorView;

/**
 * @author kyle
 * 
 */
public class NewECSpec extends AbstractHandler implements IHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		String generatedName = Long.toString(System.currentTimeMillis());
		ALEEditorView view;
		try {
			view = (ALEEditorView) window.getActivePage().showView(
					ALEEditorView.ID, generatedName,
					IWorkbenchPage.VIEW_VISIBLE);
			view.initSpecView(generatedName, new ECSpec());
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}