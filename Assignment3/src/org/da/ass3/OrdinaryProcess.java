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
	 * The link our owner resides on
	 */
	private Long link = null;
	
	/**
	 * Our process level
	 */
	private int level = -1;
	
	/**
	 * Our owner process id
	 */
	private long owner = -1;
	
	/**
	 * All currently received candidate messages
	 */
	private ArrayList<CandidateMessage> candidateMessages = (ArrayList<CandidateMessage>) Collections.synchronizedList(new ArrayList<CandidateMessage>());
	
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
		this.owner = id;
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
			// Send ACK over link
			try {
				if (link != null)
					connector.send(link, new Acknowledgement());
			} catch (MalformedURLException | RemoteException
					| NotBoundException e) {
				e.printStackTrace();
				break;
			}
			
			// Increase level
			level++;
			
			// Create R
			ArrayList<CandidateMessage> R = new ArrayList<CandidateMessage>();
			R.addAll(candidateMessages);
			
			// Sort R lexicographically (small -> large)
			Collections.sort(R);
			CandidateMessage max = R.get(R.size()-1);
			
			// Set up comparable
			CandidateMessage current = new CandidateMessage(level);
			current.setID(new MessageID(owner, 0));
			
			// Compare and set
			if (max.compareTo(current) > 0){
				level = max.getLevel();
				owner = max.getID().getBroadcaster();
				link = max.getID().getBroadcaster();
			} else {
				link = null;
			}
		}
	}

	@Override
	public void receive(GenericMessage gm, long fromProcess)
			throws MalformedURLException, RemoteException, NotBoundException {
		if (gm instanceof CandidateMessage)
			candidateMessages.add((CandidateMessage) gm);
	}

	@Override
	public long getProcessId() {
		return myid;
	}
}
