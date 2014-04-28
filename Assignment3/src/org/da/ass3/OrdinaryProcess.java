package org.da.ass3;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;

import org.da.ass3.messages.Acknowledgement;
import org.da.ass3.messages.CandidateMessage;
import org.da.ass3.messages.GenericMessage;
import org.da.ass3.messages.GenericMessage.MessageID;

public class OrdinaryProcess extends Thread implements GenericMessageListener {

	/**
	 * Is this thread supposed to stop?
	 */
	private boolean alive = true;
	
	/**
	 * The connector for this process
	 */
	private final Connector connector;
	
	/**
	 * Our id
	 */
	private final long myid;
	
	/**
	 * Construct a new ordinary process without
	 * running it.
	 * 
	 * Don't forget to run start()!
	 */
	public OrdinaryProcess(Connector connector, long id){
		super("OrdinaryProcess");
		
		this.connector = connector;
		this.myid = id;
	}
	
	/**
	 * Stop this thread
	 */
	public void kill(){
		alive = false;
	}
	
	@Override
	public void run(){
		while (alive){
			
		}
	}

	@Override
	public void receive(GenericMessage gm, long fromProcess)
			throws MalformedURLException, RemoteException, NotBoundException {
		
	}

	@Override
	public long getProcessId() {
		return myid;
	}
}
