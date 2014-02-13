package org.da.ass1;

import org.da.ass1.messages.GenericMessage;

public class Connector {

	private long scalarClock;
	
	private GenericMessageListener gmListener;
	
	private long id;
	
	// TODO Keep track of some mapping of project ids to RMI endpoints
	
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
	 */
	public void send(long toProcess, GenericMessage message){
		// TODO Update scalar Clock
		// TODO Add new clock value to message
		// TODO Send over RMI to id
	}
	
	/**
	 * Receive a message from a process with a certain id
	 * 
	 * @param fromProcess The process the message was received from
	 * @param message The message that is received
	 */
	public void receive(long fromProcess, GenericMessage message){
		// TODO Update scalar clock
		// TODO Add new clock value to message
		// TODO Delegate message to listener
	}
}
