package org.da.ass1.connector;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import org.da.ass1.Connector;
import org.da.ass1.messages.*;


/**
 * The implementation of the remote interface for  receiving messages
 * @author Chris
 *
 */
public class RMIReceiver
	extends UnicastRemoteObject
	implements IRMIConnector
{
	private static final long serialVersionUID = 1L;
	
	private Connector connector;
	
	
	/**
	 * 
	 * @param connector The instance that will handle the reception of a message
	 * @param urlname The url this instace will be responding to
	 * @throws RemoteException
	 * @throws MalformedURLException url parameter is malformed
	 * @throws AlreadyBoundException url location is already used
	 */
	public RMIReceiver(String urlname, Connector connector) throws RemoteException, MalformedURLException, AlreadyBoundException
	{
		super();
		
		this.connector = connector;
		
		System.out.println("Binding to " + urlname);
		java.rmi.Naming.bind(urlname, this);
	}

	
	/**
	 * The callee receives a message
	 * @param caller The id of the process sending the message
	 * @param message The message to be sent
	 * @throws RemoteException
	 * @throws NotBoundException 
	 * @throws MalformedURLException 
	 */
	public void receive(final long caller, final GenericMessage message) throws RemoteException, MalformedURLException, NotBoundException 
	{
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					connector.receive(caller, message);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
			}
			
		});
		t.start();
	}

}
