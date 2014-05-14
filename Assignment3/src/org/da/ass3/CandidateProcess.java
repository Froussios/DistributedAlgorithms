package org.da.ass3;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.da.ass3.messages.CandidateMessage;
import org.da.ass3.messages.GenericMessage;

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
	 * Construct a new candidate process without
	 * running it.
	 * 
	 * Don't forget to run start()!
	 */
	public CandidateProcess(Connector connector, long id, Collection<Long> allIds){
		super("CandidateProcess");
		
		this.connector = connector;
		this.myid = id;
		connector.subscribe(this);
		
		this.untraversed.addAll(allIds);
		untraversed.remove(myid);
	}
	
	/**
	 * Stop this thread
	 */
	public void kill(){
		alive = false;
	}

	private ConcurrentLinkedQueue<MsgTuple> messageQueue = new ConcurrentLinkedQueue<MsgTuple>();
	private ConcurrentLinkedQueue<Long> untraversed = new ConcurrentLinkedQueue<Long>();
	private int level = 0;
	private boolean killed = false;
	private boolean elected = false;
	
	@Override
	public void run(){
		while (alive && !untraversed.isEmpty()){
			long link = untraversed.poll();
			try {
				connector.send(link, new CandidateMessage(level, myid));
			} catch (MalformedURLException | RemoteException
					| NotBoundException e) {
				break;
			}
			boolean R = true;
			while (R){
				R = false;
				System.out.println(myid + "] Candidate waiting for message");
				while (alive && messageQueue.isEmpty()){
					// Wait
					try { Thread.sleep(100); } catch (InterruptedException e) {}
				}
				if (!alive)
					break;
				MsgTuple message = messageQueue.poll();
				System.out.println(myid + "] Candidate received message " + message);
				if (message.getId() == myid && !killed){
					level++;
					untraversed.remove(message.getLink());
					System.out.print(myid + "] Still waiting for confirmations from: ");
					for (Long l : untraversed)
						System.out.print("\t" + l);
					System.out.println();
				} else {
					if (message.compareTo(new MsgTuple(level, myid)) < 0){
						// Goto R
						R = true;
					} else {
						try {
							System.out.println(myid + "] " + killed + " Candidate sent message " + message.getLevel() + " " + message.getId() + " to " + link);
							connector.send(message.getLink(), new CandidateMessage(message.getLevel(), message.getId()));
						} catch (MalformedURLException | RemoteException
								| NotBoundException e) {
							e.printStackTrace();
						}
						killed = true;
						// Goto R
						R = true;
					}
				}
			}
		}
		System.out.println(myid + "] Exit");
		if (!killed)
			elected = true; // TODO notify everyone
	}

	@Override
	public void receive(GenericMessage gm, long fromProcess)
			throws MalformedURLException, RemoteException, NotBoundException {
		if (gm instanceof CandidateMessage)
			messageQueue.add(new MsgTuple((CandidateMessage) gm, fromProcess));
	}
	
	public boolean isElected() {
		return elected;
	}
	
	public boolean isDone(){
		return elected || killed;
	}

	@Override
	public long getProcessId() {
		return myid;
	}
}
