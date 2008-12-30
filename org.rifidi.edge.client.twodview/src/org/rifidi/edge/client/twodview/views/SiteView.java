/* 
 *  SiteView.java
 *  Created:	Aug 7, 2008
 *  Project:	RiFidi Dashboard - An RFID infrastructure monitoring tool
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.twodview.views;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.twodview.layers.EffectLayer;
import org.rifidi.edge.client.twodview.layers.FloorPlanLayer;
import org.rifidi.edge.client.twodview.layers.ListeningScalableLayeredPane;
import org.rifidi.edge.client.twodview.layers.NoteLayer;
import org.rifidi.edge.client.twodview.layers.ObjectLayer;
import org.rifidi.edge.client.twodview.listeners.SiteViewDropTargetListener;
import org.rifidi.edge.client.twodview.listeners.SiteViewKeyListener;
import org.rifidi.edge.client.twodview.listeners.SiteViewMouseWheelListener;
import org.rifidi.edge.client.twodview.sfx.ReaderAlphaImageFigure;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class SiteView extends ViewPart implements ISelectionProvider {

	@SuppressWarnings("unused")
	private Log logger = LogFactory.getLog(SiteView.class);

	public final static String ID = "org.rifidi.edge.client.twodview.views.SiteView";
	private ListeningScalableLayeredPane lp;
	private FloorPlanLayer floorplanLayer;
	private ObjectLayer objectLayer;
	private EffectLayer effectLayer;
	private NoteLayer noteLayer;
	private ArrayList<ISelectionChangedListener> listeners;

	/**
	 * 
	 */
	public SiteView() {
		listeners = new ArrayList<ISelectionChangedListener>();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());

		Canvas canvas = new Canvas(composite, SWT.NONE);

		canvas.setLayout(new FillLayout());

		// LWS holds the draw2d components
		LightweightSystem lws = new LightweightSystem(canvas);

		lp = new ListeningScalableLayeredPane();

		canvas.addListener(SWT.MouseWheel, new SiteViewMouseWheelListener(lp));
		canvas.addKeyListener(new SiteViewKeyListener(lp));

		floorplanLayer = new FloorPlanLayer();
		floorplanLayer.init();
		objectLayer = new ObjectLayer();
		effectLayer = new EffectLayer();
		noteLayer = new NoteLayer();

		lp.add(floorplanLayer, 0);
		lp.add(objectLayer, 1);
		// lp.add(effectLayer, 2);
		// lp.add(noteLayer, 3);

		lws.setContents(lp);

		// Drop Target and DT-Listener for Drag and Drop

		DropTarget dt = new DropTarget(canvas, DND.DROP_COPY | DND.DROP_MOVE
				| DND.DROP_LINK | DND.DROP_DEFAULT);
		dt.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dt.addDropListener(new SiteViewDropTargetListener(this, canvas));

		MenuManager menuMgr = new MenuManager();
		menuMgr.add(new GroupMarker("twodview"));

		Menu menu = menuMgr.createContextMenu(canvas);
		canvas.setMenu(menu);
		getSite().setSelectionProvider(this);
		getSite().registerContextMenu(menuMgr, this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the floorplanLayer
	 */
	public FloorPlanLayer getFloorplanLayer() {
		return floorplanLayer;
	}

	/**
	 * @return the objectLayer
	 */
	public ObjectLayer getObjectLayer() {
		return objectLayer;
	}

	/**
	 * @return the effectLayer
	 */
	public EffectLayer getEffectLayer() {
		return effectLayer;
	}

	/**
	 * @return the noteLayer
	 */
	public NoteLayer getNoteLayer() {
		return noteLayer;
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		// logger.debug("addSelectionChangedListener");
		this.listeners.add(listener);

	}

	@Override
	public ISelection getSelection() {
		// logger.debug("getSelection() called");
		RemoteReader rr = null;

		if (lp != null) {
			// logger.debug("LP not null");
			try {
				IFigure ifig = lp.getSelectedImage();
				if (ifig == null)
					return new StructuredSelection();
				ReaderAlphaImageFigure raif = (ReaderAlphaImageFigure) ifig;

				rr = raif.getReader();
				StructuredSelection ss = new StructuredSelection(rr);
				// logger.debug("returning RemoteReader in StructuredSelection: "
				// + ss.toString());
				return ss;
			} catch (ClassCastException e) {
				return new StructuredSelection();
			} catch (Exception e) {
				// logger.debug("ERROR: "+e.toString());
				return new StructuredSelection();
			}

		} else
			return new StructuredSelection();

	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		// logger.debug("removeSelectionChangedListener");
		listeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		// logger.debug("setSelection");
		// from objectLayer get Image where Reader is...
	}

	public ListeningScalableLayeredPane getLayeredPane() {
		return lp;
	}

	public void fireSelectionChanged() {

		for (ISelectionChangedListener listener : listeners) {
			SelectionChangedEvent sce = new SelectionChangedEvent(this,
					getSelection());
			listener.selectionChanged(sce);
		}
	}

}
