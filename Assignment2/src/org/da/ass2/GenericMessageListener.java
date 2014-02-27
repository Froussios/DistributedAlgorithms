package org.da.ass2;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.da.ass1.messages.GenericMessage;

/**
 * Defines classes that can receive GenericMessages
 *
 */
public interface GenericMessageListener {

	/**
	 * Handle a message that was received by the {@code Connector}
	 * @param gm The message that was received
	 * @param fromProcess The process the message was received from
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public void receive(GenericMessage gm, long fromProcess) throws MalformedURLException, RemoteException, NotBoundException;
	
	/**
	 * Get the process id of the local process
	 * @return
	 */
	public long getProcessId();
	
}
