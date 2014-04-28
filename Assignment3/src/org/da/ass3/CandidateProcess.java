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

public class CandidateProcess extends Thread implements GenericMessageListener {

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
	 * The links our children reside on
	 */
	private ArrayList<Long> links = new ArrayList<Long>();
	
	/**
	 * Our process level
	 */
	private int level = -1;
	
	/**
	 * The acks we have received
	 */
	private ArrayList<Long> acks = new ArrayList<Long>();
	
	/**
	 * Are we elected leader
	 */
	private boolean elected = false;
	
	/**
	 * Construct a new candidate process without
	 * running it.
	 * 
	 * Don't forget to run start()!
	 */
	public CandidateProcess(Connector connector, long id){
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
	
	/**
	 * Was this candidate elected?
	 */
	public boolean isElected(){
		return elected;
	}
	
	@Override
	public void run(){
		int K = 0;
		
		while (alive){
			// Increase level
			level++;
			
			// Alternate
			if (level % 2 == 0){
				if (links.isEmpty())
					elected = true;
				else {
					// Choose a bigger set (or the remainder)
					K = Math.min((int) Math.pow(2, level/2.0d), links.size());
					
					// Inform all the connections of their new candidate
					for (int i = 0 ; i < K; i++){
						long proc = links.remove(0);
						try {
							connector.send(proc, new CandidateMessage(level));
						} catch (MalformedURLException | RemoteException
								| NotBoundException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				if (acks.size() < K)
					break;
			}
		}
	}

	@Override
	public void receive(GenericMessage gm, long fromProcess)
			throws MalformedURLException, RemoteException, NotBoundException {
		if (gm instanceof Acknowledgement)
			acks.add(((Acknowledgement) gm).getID().getBroadcaster());
	}

	@Override
	public long getProcessId() {
		return myid;
	}
}
