package org.da.ass1.connector;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import org.da.ass1.Connector;
import org.da.ass1.messages.*;


/**
 * The implementation of the remote interface for  receiving messages
 * @author Chris
 *
 */
public class RMIReceiver
	extends UnicastRemoteObject
	implements IRMIConnector
{
	private static final long serialVersionUID = 1L;
	
	private Connector connector;
	
	
	/**
	 * 
	 * @param connector The instance that will handle the reception of a message
	 * @param urlname The url this instace will be responding to
	 * @throws RemoteException
	 * @throws MalformedURLException url parameter is malformed
	 * @throws AlreadyBoundException url location is already used
	 */
	protected RMIReceiver(String urlname, Connector connector) throws RemoteException, MalformedURLException, AlreadyBoundException
	{
		super();
		
		this.connector = connector;
		
		java.rmi.Naming.bind(urlname, this);
	}

	
	/**
	 * The callee receives a message
	 * @param caller The id of the process sending the message
	 * @param message The message to be sent
	 * @throws RemoteException
	 */
	public void receive(long caller, GenericMessage message) throws RemoteException 
	{
		connector.receive(caller, message);
	}

}