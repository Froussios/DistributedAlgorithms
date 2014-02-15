package org.da.ass1;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.da.ass1.messages.GenericMessage;

/**
 * Defines classes that can receive GenericMessages
 *
 */
public interface GenericMessageListener {

	public void receive(GenericMessage gm, long fromProcess) throws MalformedURLException, RemoteException, NotBoundException;
	
	public long getProcessId();
	
}
