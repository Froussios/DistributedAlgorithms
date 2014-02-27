package org.da.ass1;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.da.ass1.messages.Acknowledgement;
import org.da.ass1.messages.GenericMessage;
import org.da.ass1.messages.GenericMessage.MessageID;
import org.da.ass1.messages.Message;

public class TotalOrder implements GenericMessageListener{

	private final Connector connector;
	private final long myId;
	
	private long scalarClock;
	
	private Set<Long> allIds;
	private PriorityQueue<Message> queue;
	
	private HashMap<GenericMessage.MessageID, List<Long>> acknowledged; // A map of acknowledgements of message ids coupled to a list of process ids that still need to acknowledge this message
	
	private TotalOrderListener listener;
	
	public TotalOrder(Connector connector, long myId, Set<Long> allIds, TotalOrderListener listener){
		this.connector = connector;
		this.myId = myId;
		this.queue = new PriorityQueue<Message>();
		this.allIds = allIds;
		this.acknowledged = new HashMap<GenericMessage.MessageID, List<Long>>();
		this.listener = listener;
		
		connector.subscribe(this);
	}
	
	/**
	 * Set the application level object that will be called for delivered messages
	 * @param inListener
	 */
	private void setListener(TotalOrderListener inListener) {
		this.listener = inListener;
	}
	
	/**
	 * Broadcasts the message
	 * @param message The message to be broadcasted
	 * @throws MalformedURLException 
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public void broadcast(Message message) throws MalformedURLException, RemoteException, NotBoundException{
		// Update scalar Clock
		scalarClock++;
		
		// Add new clock value to message
		message.setTimestamp(scalarClock);
		
		// Set the id of the message
		GenericMessage.MessageID messageId = new GenericMessage.MessageID(myId, scalarClock);
		message.setID(messageId);
		
		// Send to each process
		for ( Long id : allIds )
			connector.send(id, message);
	}
	
	/**
	 * Handle a Message that was received by the {@code Connector} this instance is listening to.
	 * @param m The message
	 * @param fromProcess The process the message was received from
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public synchronized void receiveMessage(Message m, long fromProcess) throws MalformedURLException, RemoteException, NotBoundException{
		// Push to end of the queue (automatically done by PriorityQueue.add() )
		queue.add(m);
		
		if (!acknowledged.containsKey(m.getID())){
			ArrayList<Long> racks = new ArrayList<Long>();
			for (Long l : allIds){
				racks.add(l);
			}
			acknowledged.put(m.getID(), racks);
		}
		
		// Send acks to everyone else
		GenericMessage.MessageID messageId = m.getID(); // TODO Is this timestamp the one EVERYONE has for this message?
		for (Long id : this.allIds){
			GenericMessage ack = new Acknowledgement(messageId);
			
			// Update scalar Clock
			scalarClock++;
			ack.setTimestamp(scalarClock);
			
			connector.send(id, ack);
		}
	}
	
	/**
	 * Handle an Acknowledgement that was received by the {@code Connector} this instance is listening to.
	 * @param a The acknowledgement message
	 * @param fromProcess The process the message was received from
	 */
	public synchronized void receiveAcknowledgement(Acknowledgement a, long fromProcess){
		GenericMessage.MessageID msgid = a.getAckOf(); // The timestamp of the message we need to acknowledge
		
		// When receiving an acknowledgement for the first time, for a message that hasn't been received yet,
		// initialise the list of processes that have acknowledged the message  
		if (!acknowledged.containsKey(msgid)){
			ArrayList<Long> racks = new ArrayList<Long>();
			for (Long l : allIds){
				racks.add(l);
			}
			acknowledged.put(msgid, racks);
		}
		
		// Mark the message as having been acknowledged by this process
		List<Long> acks = acknowledged.get(msgid);
		acks.remove(fromProcess);
		
		// If everyone acknowledged this message, start popping fully acknowledged messages from the queue 
		if (acks.isEmpty()){
			while (!queue.isEmpty()){
				MessageID msg = queue.peek().getID();
				if (acknowledged.get(msg).isEmpty()) {
					// Deliver message
					Message m = queue.remove();
					connector.log("DELIVERY of message [" + msg.getBroadcaster() + ":" + msg.getBroadcasterTime() + "]");
					listener.deliverMessage(m);
				}
				else
					break;	//Head is not ack'ed, stop
			}
		}
	}
	
	
	@Override
	/**
	 * Handle a GenericMessage that was received at the level of Connector
	 */
	public void receive(GenericMessage gm, long fromProcess) throws MalformedURLException, RemoteException, NotBoundException {
		// Update scalar clock
		if ( this.scalarClock < gm.getTimestamp() )
			this.scalarClock = gm.getTimestamp();
		this.scalarClock++;
		
		// Add new clock value to message
		gm.setTimestamp(scalarClock);
		
		if (gm instanceof Message){
			receiveMessage((Message) gm, fromProcess);
		} else if (gm instanceof Acknowledgement){
			receiveAcknowledgement((Acknowledgement) gm, fromProcess);
		}
	}

	@Override
	public long getProcessId() {
		return myId;
	}

	
}
