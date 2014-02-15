package org.da.ass1.connector;

import java.net.MalformedURLException;
import java.rmi.*;

import org.da.ass1.messages.*;

public interface IRMIConnector
	extends Remote
{
	public void receive(long sender, GenericMessage message) throws java.rmi.RemoteException, MalformedURLException, NotBoundException;
}
