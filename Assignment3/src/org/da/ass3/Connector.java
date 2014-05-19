package org.da.ass3;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Semaphore;

import org.da.ass3.connector.IRMIConnector;
import org.da.ass3.connector.RMIReceiver;
import org.da.ass3.messages.GenericMessage;

/**
 * An object that sends and receives messages within a group of processes.
 * 
 * @author Chris
 *
 */
public class Connector {

	private static final Random random = new Random();
	
	private static String objectName = "DA-MessageReceiver";

	private final ArrayList<GenericMessageListener> gmListeners = new ArrayList<GenericMessageListener>();
	
	private long id;
	
	private Map<Long, RemoteHost> index;
	
	private final Semaphore sem = new Semaphore(1);
	
	/**
	 * Instantiate a new Connector that receives GenericMessage intances from RMI
	 * @param local The RMI address info of the local process
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws AlreadyBoundException
	 */
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
	public synchronized void subscribe(GenericMessageListener listener){
		gmListeners.add(listener);
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
		log("Sent     " + message.toString() + "\t to   " + toProcess);
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
	public synchronized void receive(long fromProcess, GenericMessage message) throws MalformedURLException, RemoteException, NotBoundException{
		// Simulate delay
		this.delay();
		
		// Log reception
		log("Received " + message.toString() + "\t from " + fromProcess);
		// Delegate message to listener
		for (GenericMessageListener listener : gmListeners)
			listener.receive(message, fromProcess);
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
			//System.out.println("" + id + ": " + message);
			fw.close();
			sem.release();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Clear all the messages in the log file for this process
	 */
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
	
	/**
	 * Wait for a random amount of time
	 */
	private void delay() {
		double delay = 40 + random.nextGaussian()*20;
		if (delay < 10) delay = 10;
		
		try {
			Thread.sleep((long) delay);
		}
		catch (InterruptedException e) {
		}
	}
}
