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
		
		this.connector = connector;
		connector.subscribe(this);
		this.myid = id;
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
			if (!messageQueue.isEmpty()){
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
						connector.send(owner, new CandidateMessage(message.getLevel(), message.getId()));
					} catch (MalformedURLException | RemoteException
							| NotBoundException e) {
						e.printStackTrace();
					}
				} else {
					owner = potential_owner;
					try {
						connector.send(owner, new CandidateMessage(message.getLevel(), message.getId()));
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
		if (gm instanceof CandidateMessage)
			messageQueue.add(new MsgTuple((CandidateMessage) gm, fromProcess));
	}

	@Override
	public long getProcessId() {
		return myid;
	}
	
	private class MsgTuple implements Comparable<MsgTuple>{
		
		private int level;
		private long id;
		private long link;
		
		public MsgTuple(int level, long fromprocess){
			this.level = level;
			this.id = fromprocess;
			this.link = fromprocess;
		}
		
		public MsgTuple(CandidateMessage cm, long fromprocess){
			this.level = cm.getLevel();
			this.id = cm.getId();
			this.link = fromprocess;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public long getLink() {
			return link;
		}

		public void setLink(long link) {
			this.link = link;
		}

		@Override
		public int compareTo(MsgTuple o) {
			int comp = Integer.compare(level, o.getLevel());
			if (comp == 0){
				return Long.compare(id, o.getId());
			} else {
				return comp;
			}
		}

	}
}
