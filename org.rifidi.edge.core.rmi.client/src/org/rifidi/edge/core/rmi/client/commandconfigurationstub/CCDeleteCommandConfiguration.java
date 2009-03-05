package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.rmi.CommandConfigurationStub;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call deletes a CommandConfiguration with the specified ID. It returns
 * null.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CCDeleteCommandConfiguration extends
		ServerDescriptionBasedRemoteMethodCall<Object, RuntimeException> {

	/** The ID of the CommandConfiguration to delete */
	private String commandConfigurationID;

	/***
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription of the CommandConfigurationStub
	 * @param commandConfigurationID
	 *            The ID of the CommandConfiguration to delete
	 */
	public CCDeleteCommandConfiguration(ServerDescription serverDescription,
			String commandConfigurationID) {
		super(serverDescription);
		this.commandConfigurationID = commandConfigurationID;
	}

	@Override
	protected Object performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		CommandConfigurationStub stub = (CommandConfigurationStub) remoteObject;
		stub.deleteCommandConfiguration(this.commandConfigurationID);
		return null;
	}

}