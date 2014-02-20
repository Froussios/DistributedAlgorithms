package org.da.ass1;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.da.ass1.messages.Acknowledgement;
import org.da.ass1.messages.GenericMessage;
import org.da.ass1.connector.*;

public class Connector {

	private static String objectName = "DA-MessageReceiver";

	private GenericMessageListener gmListener;
	
	private long id;
	
	private Map<Long, RemoteHost> index;
	
	private Semaphore sem = new Semaphore(1);
	
	public Connector(RemoteHost local) throws RemoteException, MalformedURLException, AlreadyBoundException {
		new RMIReceiver(local.getURL(objectName), this);
	}
	
	/**
	 * Set the index
	 * 
	 * @param index The index that map each process id to the corresponding url
	 */
	public void setIndex(Map<Long, RemoteHost> index){
		this.index = index;
	}
	
	/**
	 * Register a receiving object for message that are sent to us
	 * 
	 * @param listener The GenericMessage listener
	 */
	public void subscribe(GenericMessageListener listener){
		this.gmListener = listener;
		this.id = listener.getProcessId();
		clearLog();
	}
	
	/**
	 * Send a message to a process with a certain process id
	 * 
	 * @param toProcess The id of the process to send to
	 * @param message The message to send
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
	public void send(long toProcess, GenericMessage message) throws MalformedURLException, RemoteException, NotBoundException{
		// Send over RMI to id
		RemoteHost rh = this.index.get(toProcess);
		String remoteUrl = rh.getURL(objectName);
		IRMIConnector remoteReceiver = (IRMIConnector) java.rmi.Naming.lookup(remoteUrl);
		// Random delay
		try {
			if (id != 4)
				Thread.sleep((long) (20));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		remoteReceiver.receive(id, message);
	}
	
	/**
	 * Receive a message from a process with a certain id
	 * 
	 * @param fromProcess The process the message was received from
	 * @param message The message that is received
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
	public void receive(long fromProcess, GenericMessage message) throws MalformedURLException, RemoteException, NotBoundException{
		// Log reception
		log("[" + fromProcess + ":" + message.getTimestamp() + "] " + message.toString());
		// Delegate message to listener
		this.gmListener.receive(message, fromProcess);
	}
	
	/**
	 * Write to our receive message log
	 * 
	 * @param message The message to write to file
	 */
	public void log(String message){
		try {
			sem.acquire();
			FileWriter fw = new FileWriter(id + ".log", true);
			fw.write(message+"\n");
			fw.close();
			sem.release();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void clearLog(){
		try {
			sem.acquire();
			FileWriter fw = new FileWriter(id + ".log", false);
			fw.write("");
			fw.close();
			sem.release();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
