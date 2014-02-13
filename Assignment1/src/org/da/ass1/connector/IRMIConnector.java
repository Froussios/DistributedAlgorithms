package org.da.ass1.connector;

import java.rmi.*;

import org.da.ass1.messages.*;

public interface IRMIConnector
	extends Remote
{
	public void receive(GenericMessage message) throws java.rmi.RemoteException;
}
