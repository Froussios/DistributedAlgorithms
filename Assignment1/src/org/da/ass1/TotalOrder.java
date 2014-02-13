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
import org.da.ass1.messages.Message;

public class TotalOrder implements GenericMessageListener{

	private final Connector connector;
	private final long myId;
	
	private Set<Long> allIds;
	private PriorityQueue<Message> queue;
	
	private HashMap<Long, List<Long>> acknowledged; // A map of acknowledgements of message ids coupled to a list of process ids that still need to acknowledge this message
	
	public TotalOrder(Connector connector, long myId, Set<Long> allIds){
		this.connector = connector;
		this.myId = myId;
		this.queue = new PriorityQueue<Message>();
		this.allIds = allIds;
		this.acknowledged = new HashMap<Long, List<Long>>();
	}
	
	public void send(Message m, long toProcess) throws MalformedURLException, RemoteException, NotBoundException{
		//connector.send(toProcess, m);
	}
	
	public void receiveMessage(Message m, long fromProcess){
		
	}
	
	public synchronized void receiveAcknowledgement(Acknowledgement a, long fromProcess){
		long msgid = a.getAckOf(); // The timestamp of the message we need to acknowledge
		if (!acknowledged.containsKey(msgid)){
			ArrayList<Long> racks = new ArrayList<Long>();
			for (Long l : allIds){
				racks.add(l);
			}
		}
		List<Long> acks = acknowledged.get(msgid);
		acks.remove(fromProcess);
		if (acks.isEmpty()){
			// Everyone acknowledged, start popping from the front of the queue
			while (!queue.isEmpty()){
				long msg = queue.peek().getTimestamp();
				if (acknowledged.get(msg).isEmpty())
					queue.remove();
				else
					break;	//Head is not ack'ed, stop
			}
		}
	}
	
	@Override
	public void receive(GenericMessage gm, long fromProcess) {
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
