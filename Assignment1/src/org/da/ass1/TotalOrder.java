package org.da.ass1;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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
	
	private HashMap<Long, List<Acknowledgement>> acknowledged;
	
	public TotalOrder(Connector connector, long myId, Set<Long> allIds){
		this.connector = connector;
		this.myId = myId;
		this.queue = new PriorityQueue<Message>();
		this.allIds = allIds;
		this.acknowledged = new HashMap<Long, List<Acknowledgement>>();
	}
	
	public void send(Message m, long toProcess) throws MalformedURLException, RemoteException, NotBoundException{
		//connector.send(toProcess, m);
	}
	
	public void receiveMessage(Message m, long fromProcess){
		
	}
	
	public void receiveAcknowledgement(Acknowledgement a, long fromProcess){
		
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
