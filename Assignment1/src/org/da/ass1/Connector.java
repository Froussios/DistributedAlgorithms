package org.da.ass1;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

import org.da.ass1.messages.GenericMessage;

import org.da.ass1.connector.*;

public class Connector {

	private static String objectName = "DA-MessageReceiver";
	
	//private long scalarClock;
	
	private GenericMessageListener gmListener;
	
	private long id;
	
	private Map<Long, RemoteHost> index;
	
	public Connector(RemoteHost local) throws RemoteException, MalformedURLException, AlreadyBoundException {
		IRMIConnector rmiConnector = new RMIReceiver(local.getURL(objectName), this);
	}
	
	/**
	 * Set the index
	 * 
	 * @param index The index that map each process id to the corresponding url
	 */
	public void setIndex(Map<Long, RemoteHost> index){
		this.index = index;
	}
	
	/**
	 * Register a receiving object for message that are sent to us
	 * 
	 * @param listener The GenericMessage listener
	 */
	public void subscribe(GenericMessageListener listener){
		this.gmListener = listener;
		this.id = listener.getProcessId();
	}
	
	/**
	 * Send a message to a process with a certain process id
	 * 
	 * @param toProcess The id of the process to send to
	 * @param message The message to send
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
	public synchronized void send(long toProcess, GenericMessage message) throws MalformedURLException, RemoteException, NotBoundException{
//		// Update scalar Clock
//		scalarClock++;
//		
//		// Add new clock value to message
//		message.setTimestamp(scalarClock);
		
		// Send over RMI to id
		RemoteHost rh = this.index.get(toProcess);
		String remoteUrl = rh.getURL(objectName);
		IRMIConnector remoteReceiver = (IRMIConnector) java.rmi.Naming.lookup(remoteUrl);
		remoteReceiver.receive(id, message);
	}
	
	/**
	 * Receive a message from a process with a certain id
	 * 
	 * @param fromProcess The process the message was received from
	 * @param message The message that is received
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
	public void receive(long fromProcess, GenericMessage message) throws MalformedURLException, RemoteException, NotBoundException{
//		// Update scalar clock
//		if ( this.scalarClock < message.getTimestamp() )
//			this.scalarClock = message.getTimestamp();
//		this.scalarClock++;
//		
//		// Add new clock value to message
//		message.setTimestamp(scalarClock);
		
		// Delegate message to listener
		this.gmListener.receive(message, fromProcess);
	}
}
