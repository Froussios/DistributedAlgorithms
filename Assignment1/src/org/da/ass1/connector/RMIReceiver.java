package org.da.ass1.connector;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import org.da.ass1.messages.*;

public class RMIReceiver
	extends UnicastRemoteObject
	implements IRMIConnector
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected RMIReceiver(/*insert listener here*/) throws RemoteException
	{
		super();
		// TODO Auto-generated constructor stub
		
		//java.rmi.Naming.bind("rmi://localhost:1201/object-name", this);
	}

	public void receive(GenericMessage message) throws RemoteException 
	{
		// TODO Auto-generated method stub
		
	}

}
