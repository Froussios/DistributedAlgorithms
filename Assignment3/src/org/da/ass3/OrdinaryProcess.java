package org.da.ass3;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

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
		
		this.myid = id;
		
		this.connector = connector;
		connector.subscribe(this);
	}
	
	/**
	 * Stop this thread
	 */
	public void kill(){
		alive = false;
	}
	
	private ConcurrentLinkedQueue<MsgTuple> messageQueue = new ConcurrentLinkedQueue<MsgTuple>();
	private int level = -1;
	private long owner_id = -1;
	private long potential_owner = -1;
	private long owner = -1;
	
	@Override
	public void run(){
		while (alive){
			// Wait for a message
			try { Thread.sleep(100); } catch (InterruptedException e) {}
			if (!messageQueue.isEmpty()){
				System.out.println(myid + "] Ordinary received message");
				MsgTuple message = messageQueue.poll();
				
				// Construct the current owner tuple to compare to
				MsgTuple current = new MsgTuple(level, owner_id);
				
				// Compare
				int compare = message.compareTo(current);
				if (compare < 0){
					// Ignore
				} else if ( compare > 0){
					potential_owner = message.getLink();
					level = message.getLevel();
					owner_id = message.getId();
					if (owner == -1)
						owner = potential_owner;
					try {
						System.out.println(myid + "] Ordinary sent message " + message.getLevel() + " " + message.getId() + " to " + owner);
						connector.send(owner, new CandidateMessage(message.getLevel(), message.getId()));
					} catch (MalformedURLException | RemoteException
							| NotBoundException e) {
						e.printStackTrace();
					}
				} else {
					owner = potential_owner;
					try {
						if (owner != -1){
							System.out.println(myid + "] Ordinary sent message " + message.getLevel() + " " + message.getId() + " to " + owner);
							connector.send(owner, new CandidateMessage(message.getLevel(), message.getId()));
						}
					} catch (MalformedURLException | RemoteException
							| NotBoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void receive(GenericMessage gm, long fromProcess)
			throws MalformedURLException, RemoteException, NotBoundException {
		if (gm instanceof CandidateMessage){
			CandidateMessage cm = (CandidateMessage) gm;
			if (cm.getId() == myid)
				return;
			messageQueue.add(new MsgTuple(cm, fromProcess));
		}
	}

	@Override
	public long getProcessId() {
		return myid;
	}
}
