package org.da.ass3;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
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
		
		ArrayList<Long> remaining = new ArrayList<Long>(allIds);
		while (!remaining.isEmpty()){
			int index = (int) Math.round(Math.random()*remaining.size());
			if (index == remaining.size())
				index--;
			untraversed.add(remaining.remove(index));
		}
		
		untraversed.remove((Long) myid);
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
		while (alive && ((!untraversed.isEmpty() && !killed)||!messageQueue.isEmpty())){
			long link = untraversed.peek();
			try {
				connector.send(link, new CandidateMessage(level, myid));
			} catch (MalformedURLException | RemoteException
					| NotBoundException e) {
				e.printStackTrace();
				break;
			}
			boolean R = true;
			while (R){
				R = false;
				System.out.println(myid + "] Candidate waiting for message");
				System.out.flush();
				int timeout = 50;
				do {
					// Wait
					try { Thread.sleep(20); } catch (InterruptedException e) {}
					timeout--;
				} while (alive && messageQueue.isEmpty());
				if (!alive)
					break;
				if (timeout == 0)
					break;
				MsgTuple message = messageQueue.poll();
				System.out.println(myid + "] Candidate received message " + message);
				if (message.getId() == myid && !killed){
					level++;
					untraversed.remove(message.getLink());
					System.out.println(printUntraversed());
					connector.log(printUntraversed());
					R = link != message.getLink();
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
						connector.log("X_X");
					}
				}
			}
		}
		System.out.println(myid + "] Exit");
		if (!killed)
			elected = true;
		
		try {
			FileWriter fw = new FileWriter(myid + ".txt");
			fw.write("elected = " + elected + ", level = " + level);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String printUntraversed() {
		String array = myid + "] Still waiting for confirmations from: ";
		array += "[";
		for (Long l : untraversed)
			array += l + ", ";
		array = array.substring(0, array.length()-2);
		array += "]";
		return array;
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
