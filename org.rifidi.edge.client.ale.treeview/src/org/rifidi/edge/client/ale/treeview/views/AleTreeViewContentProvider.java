/* 
 *  AleTreeViewContentProvider.java
 *  Created:	Mar 12, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.treeview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.treeview.views;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.osgi.service.prefs.Preferences;
import org.rifidi.edge.client.ale.api.proxy.AleProxyFactory;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ALELRServicePortType;
import org.rifidi.edge.client.ale.treeview.Activator;
import org.rifidi.edge.client.ale.treeview.preferences.AleTreeViewPreferences;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class AleTreeViewContentProvider implements ITreeContentProvider {

	private Log logger;

	/**
	 * 
	 */
	public AleTreeViewContentProvider() {
		logger = LogFactory.getLog(AleTreeViewContentProvider.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof AleProxyFactory) {
			Object[] objects = new Object[2];
			ALEServicePortType proxy = ((AleProxyFactory) parentElement)
					.getAleServicePortType();
			ALELRServicePortType lrproxy = ((AleProxyFactory) parentElement)
					.getAleLrServicePortType();
			objects[0] = proxy;
			objects[1] = lrproxy;
			return objects;
		}
		if (parentElement instanceof ALEServicePortType) {

			try {
				ArrayList<String> strings = (ArrayList<String>) ((ALEServicePortType) parentElement)
						.getECSpecNames(new EmptyParms()).getString();
				String[] ecSpecs = new String[strings.size()];

				for (int i = 0; i < strings.size(); i++) {
					ecSpecs[i] = strings.get(i);
				}
				return ecSpecs;

			} catch (Exception e) {
				logger.error(e.getCause() + ":\n" + e.getMessage());
				return new Object[0];
			}
		}
		if (parentElement instanceof ALELRServicePortType) {
			try {
				ArrayList<String> strings = (ArrayList<String>) ((ALELRServicePortType) parentElement)
						.getLogicalReaderNames(
								new org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.EmptyParms())
						.getString();
				String[] lrSpecs = new String[strings.size()];
				for (int i = 0; i < strings.size(); i++) {
					lrSpecs[i] = strings.get(i);
				}
				return lrSpecs;
			} catch (Exception e) {
				logger.error(e.getCause() + ":\n" + e.getMessage());
				return new Object[0];
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	@Override
	public Object getParent(Object arg0) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof AleProxyFactory) {
			if (((AleProxyFactory) element).getAleEndpoint() == null)
				return false;
			else
				return true;
		}
		if (element instanceof ALEServicePortType) {

			ArrayList<String> strings;
			try {
				strings = (ArrayList<String>) ((ALEServicePortType) element)
						.getECSpecNames(new EmptyParms()).getString();
				if (strings.size() > 0)
					return true;

			} catch (Exception e) {
				logger.error(e.getCause() + ":\n" + e.getMessage());
				return false;
			}

		}
		if (element instanceof ALELRServicePortType) {

			ArrayList<String> strings;
			try {
				strings = (ArrayList<String>) ((ALELRServicePortType) element)
						.getLogicalReaderNames(new org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.EmptyParms()).getString();
				if (strings.size() > 0)
					return true;

			} catch (Exception e) {
				logger.error(e.getCause() + ":\n" + e.getMessage());
				return false;
			}

		}

		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof AleProxyFactory
				&& ((AleProxyFactory) inputElement).getServerName().isEmpty()) {
			Preferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
			Object[] obj = new Object[1];

			obj[0] = new AleProxyFactory(node.get(
					AleTreeViewPreferences.ALE_ENDPOINT,
					AleTreeViewPreferences.ALE_ENDPOINT_DEFAULT), node.get(
					AleTreeViewPreferences.ALELR_ENDPOINT,
					AleTreeViewPreferences.ALELR_ENDPOINT_DEFAULT));
			return obj;
		} else if (hasChildren(inputElement))
			return getChildren(inputElement);
		else {
			return new Object[0];
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

}