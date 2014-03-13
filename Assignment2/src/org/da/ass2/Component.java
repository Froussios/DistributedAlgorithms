package org.da.ass2;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

import org.da.ass2.messages.GenericMessage;
import org.da.ass2.messages.GenericMessage.MessageID;
import org.da.ass2.messages.Grant;
import org.da.ass2.messages.Inquire;
import org.da.ass2.messages.Postponed;
import org.da.ass2.messages.Release;
import org.da.ass2.messages.Relinquish;
import org.da.ass2.messages.Request;

/**
 * Implements the Meakawa mutual exclusion algorithm.
 * 
 * @author cfroussios
 * 
 */
public class Component implements GenericMessageListener {

	Runnable callback;
	private Connector connector;
	private RemoteHost me;
	private Set<Long> allIds;

	private long scalarClock;

	int no_grants = 0;
	boolean granted;
	boolean inquiring;
	Request current_grant;
	boolean postponed;
	PriorityQueue<Request> Q = new PriorityQueue<Request>();
	Set<RemoteHost> requestSet = new TreeSet<RemoteHost>();

	/**
	 * A new component
	 * 
	 * @param connector
	 *            The message exchange interface
	 * @param me
	 *            The localhost information
	 * @param allIds
	 *            All the processes in the network
	 * @param requestSets
	 *            This process'es request set
	 * @throws InterruptedException
	 */
	public Component(Connector connector, RemoteHost me, Set<Long> allIds,
			Map<Integer, Collection<RemoteHost>> requestSets)
			throws InterruptedException {
		this.connector = connector;
		this.me = me;
		this.allIds = allIds;

		for (Integer group : me.getGroups()) {
			Collection<RemoteHost> others = requestSets.get(group);
			for (RemoteHost other : others) {
				requestSet.add(other);
			}
		}

		this.connector.subscribe(this);
	}

	/**
	 * Request to enter the Critical Section
	 * 
	 * @throws NotBoundException
	 * @throws RemoteException
	 * @throws MalformedURLException
	 */
	public synchronized void requestCS(Runnable callback)
			throws MalformedURLException, RemoteException, NotBoundException {

		this.callback = callback;

		no_grants = 0;

		Request sendMe = applyTimestamp(new Request());
		for (RemoteHost other : requestSet) {
			connector.send(other.getId(), sendMe);
		}
	}

	/**
	 * Handle any message received
	 */
	@Override
	public void receive(GenericMessage gm, long fromProcess)
			throws MalformedURLException, RemoteException, NotBoundException {
		
		connector.log("Received " + gm.toString() + "\t from " + fromProcess);
		
		if (gm instanceof Grant) {
			receiveGrant(fromProcess);
		} else if (gm instanceof Inquire) {
			try {
				receiveInquire(fromProcess);
			} catch (InterruptedException e) {
			}
		} else if (gm instanceof Release) {
			receiveRelease(fromProcess, (Release) gm);
		} else if (gm instanceof Relinquish) {
			receiveRelinquish(fromProcess);
		} else if (gm instanceof Request) {
			receiveRequest(fromProcess, (Request) gm);
		} else if (gm instanceof Postponed) {
			receivePostponed(fromProcess);
		}
	}

	@Override
	public long getProcessId() {
		return me.getId();
	}

	/**
	 * Handle the reception of a grant message
	 * 
	 * @param fromProcess
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public synchronized void receiveGrant(long fromProcess)
			throws MalformedURLException, RemoteException, NotBoundException {
		no_grants++;
		if (no_grants == requestSet.size()) {
			postponed = false;

			callback.run();

			Release sendMe = applyTimestamp(new Release());
			for (RemoteHost other : requestSet) {
				connector.send(other.getId(), sendMe);
			}
		}
	}

	/**
	 * Handle the reception of a Inquire message
	 * 
	 * @param fromProcess
	 * @throws InterruptedException
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public void receiveInquire(long fromProcess) throws InterruptedException,
			MalformedURLException, RemoteException, NotBoundException {
		while (true) {
			synchronized (this) {
				if (postponed || no_grants == requestSet.size())
					break;
			}
			Thread.sleep(20);
		}

		synchronized (this) {
			if (postponed) {
				no_grants--;

				Relinquish sendMe = applyTimestamp(new Relinquish());
				connector.send(fromProcess, sendMe);
			}
		}
	}

	/**
	 * Handle the reception of a release message
	 * 
	 * @param fromProcess
	 * @param r
	 *            The message received
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public synchronized void receiveRelease(long fromProcess, Release r)
			throws MalformedURLException, RemoteException, NotBoundException {

		granted = false;
		inquiring = false;
		//System.out.println("Received release from " + fromProcess + ", # processes still waiting: " + Q.size());
		if (Q.size() > 0) {
			current_grant = Q.remove();
			long j = current_grant.getID().getBroadcaster();
			Grant sendMe = applyTimestamp(new Grant());
			connector.send(j, sendMe);
			granted = true;
		}
	}

	/**
	 * Handle the reception of a Relinquish message
	 * 
	 * @param fromProcess
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public synchronized void receiveRelinquish(long fromProcess)
			throws MalformedURLException, RemoteException, NotBoundException {

		inquiring = false;
		granted = false;
		Q.add(current_grant);
		current_grant = Q.remove();
		granted = true;
		long l = current_grant.getID().getBroadcaster();
		Grant sendMe = applyTimestamp(new Grant());
		connector.send(l, sendMe);
	}

	/**
	 * Handle the reception of a Request message
	 * 
	 * @param fromProcess
	 * @param r
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public synchronized void receiveRequest(long fromProcess, Request r)
			throws MalformedURLException, RemoteException, NotBoundException {

		if (!granted) {
			current_grant = r;
			Grant sendMe = applyTimestamp(new Grant());
			connector.send(fromProcess, sendMe);
			granted = true;
		} else {
			Q.add(r);
			//System.out.println("Received request from " + fromProcess + ", # processes waiting: " + Q.size());
			Request head = Q.peek();
			if (current_grant.compareTo(r) < 0 || head.compareTo(r) < 0) {
				Postponed sendMe = applyTimestamp(new Postponed());
				connector.send(fromProcess, sendMe);
			} else {
				if (!inquiring) {
					inquiring = true;
					long l = current_grant.getID().getBroadcaster();
					Inquire sendMe = applyTimestamp(new Inquire());
					connector.send(l, sendMe);
				}
			}
		}
	}

	/**
	 * Handle the reception of Postponed message.
	 * 
	 * @param fromProcess
	 */
	public synchronized void receivePostponed(long fromProcess) {

		postponed = true;
	}

	/**
	 * Prepare a message for sending
	 * 
	 * @param gm
	 *            The message to be prepared
	 * @return The prepared message
	 */
	private <T extends GenericMessage> T applyTimestamp(T gm) {
		scalarClock++;
		gm.setTimestamp(scalarClock);
		GenericMessage.MessageID messageId = new GenericMessage.MessageID(me.getId(), scalarClock);
		gm.setID(messageId);
		return gm;
	}

}
