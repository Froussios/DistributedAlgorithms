package org.da.ass2;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.da.ass2.messages.GenericMessage;
import org.da.ass2.messages.GenericMessage.MessageID;
import org.da.ass2.messages.Grant;
import org.da.ass2.messages.Inquire;
import org.da.ass2.messages.Postponed;
import org.da.ass2.messages.Release;
import org.da.ass2.messages.Relinquish;
import org.da.ass2.messages.Request;

public class Component implements GenericMessageListener {

	private Connector connector;
	private RemoteHost me;
	private Set<Long> allIds;
	private Map<Integer, Collection<RemoteHost>> requestSets;
	
	private long scalarClock;
	
	private PriorityQueue<Request> requestQueue;
	
	private LinkedList<Long> confirmations = null;
	private Request granted;
	private boolean relinquished = false;
	
	private Semaphore waitForPostponed = new Semaphore(1);
	
	public Component(Connector connector, RemoteHost me, Set<Long> allIds, Map<Integer, Collection<RemoteHost>> requestSets) throws InterruptedException{
		this.connector = connector;
		this.me = me;
		this.allIds = allIds;
		this.requestSets = requestSets;
		
		this.requestQueue = new PriorityQueue<Request>();
		waitForPostponed.acquire();
	}
	
	/**
	 * Request to enter the Critical Section
	 * 
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
	public void requestCS() throws MalformedURLException, RemoteException, NotBoundException{
		
		Request sendMe = applyTimestamp(new Request());
		
		confirmations = new LinkedList<Long>();
		
		for (Integer group : me.getGroups()){
			Collection<RemoteHost> others = requestSets.get(group);
			for (RemoteHost other : others){
				confirmations.add(other.getId());
				connector.send(other.getId(), sendMe);
			}
		}
		
		while (true){
			synchronized(confirmations){
				if(confirmations.isEmpty()){
					break;
				}
			}
			try {Thread.sleep(100);} catch (InterruptedException e) { break; }
		}
	}
	
	/**
	 * Release our critical section
	 * 
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public void releaseCS() throws MalformedURLException, RemoteException, NotBoundException{
		
		Release sendMe = applyTimestamp(new Release());
		
		for (Integer group : me.getGroups()){
			Collection<RemoteHost> others = requestSets.get(group);
			for (RemoteHost other : others){
				connector.send(other.getId(), sendMe);
			}
		}
	}

	@Override
	public void receive(GenericMessage gm, long fromProcess)
			throws MalformedURLException, RemoteException, NotBoundException {
		if (gm instanceof Grant){
			receiveGrant(fromProcess);
		} else if (gm instanceof Inquire){
			try {
				receiveInquire(fromProcess);
			} catch (InterruptedException e) {
			}
		} else if (gm instanceof Release){
			receiveRelease(fromProcess, (Release) gm);
		} else if (gm instanceof Relinquish){
			receiveRelinquish(fromProcess);
		} else if (gm instanceof Request){
			receiveRequest(fromProcess, (Request) gm);
		} else if (gm instanceof Postponed){
			receivePostponed(fromProcess);
		}
	}

	@Override
	public long getProcessId() {
		return me.getId();
	}
	
	public void receiveGrant(long fromProcess){
		synchronized(confirmations){
			confirmations.remove(fromProcess);
			if (confirmations.isEmpty())
				waitForPostponed.release();
		}
	}
	
	public void receiveInquire(long fromProcess) throws InterruptedException, MalformedURLException, RemoteException, NotBoundException{
		waitForPostponed.acquire();
		synchronized(requestQueue){
			if (!requestQueue.isEmpty() || relinquished){
				relinquished = true;
				confirmations.add(fromProcess);
				connector.send(fromProcess, applyTimestamp(new Relinquish()));
			}
		}
	}
	
	public void receiveRelease(long fromProcess, Release r) throws MalformedURLException, RemoteException, NotBoundException{
		synchronized(requestQueue){
			granted = null;
			if (!requestQueue.isEmpty()){
				Request req = requestQueue.remove();
				granted = req;
				connector.send(req.getID().getBroadcaster(), applyTimestamp(new Grant())); 
			}
		}
	}
	
	public void receiveRelinquish(long fromProcess) throws MalformedURLException, RemoteException, NotBoundException{
		synchronized(requestQueue){
			Request old = granted;
			Request req = requestQueue.remove();
			granted = req;
			connector.send(req.getID().getBroadcaster(), applyTimestamp(new Grant()));
			requestQueue.add(old);
		}
	}
	
	public void receiveRequest(long fromProcess, Request r) throws MalformedURLException, RemoteException, NotBoundException{
		synchronized(requestQueue){
			if (granted == null){
				granted = r;
				connector.send(fromProcess, applyTimestamp(new Grant()));
			} else {
				requestQueue.add(r);
				if (requestQueue.peek().equals(r) && granted.compareTo(r)>0){
					connector.send(fromProcess, applyTimestamp(new Inquire()));
				} else {
					connector.send(fromProcess, applyTimestamp(new Postponed()));
				}
			}
		}
	}
	
	public void receivePostponed(long fromProcess){
		waitForPostponed.release();
	}
	
	private <T extends GenericMessage> T applyTimestamp(T gm){
		scalarClock++;
		gm.setTimestamp(scalarClock);
		return gm;
	}
	
}
