import java.io.*;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.da.ass2.*;
import org.da.ass2.messages.*;

/**
 * The main entrypoint for the jar files
 */
public class main implements TotalOrderListener{

	/**
	 * The id of the running process
	 */
	private long myId = -1;
	
	/**
	 * Running in (silent) automated test mode
	 */
	public boolean testing = false;
	
	/**
	 * Initialize ourselves with a certain id
	 * 
	 * @param id The id to initialize with
	 */
	public main(long id){
		myId = id;
	}
	
	public static Map<Long, Collection<RemoteHost>> fromHosts(Map<Long, RemoteHost> hosts){
		for (RemoteHost host : hosts.values()){
			host.getGroups();
		}
	}
	
	/**
	 * The main entrypoint
	 * 
	 * args[0] : our process id
	 * args[1] : (if supplied) testing, "silent" does nothing otherwise send the arg as a broadcast message
	 * args[i] : sends the i'th broadcast message (iff arg[1] does not specify silent)
	 * 
	 * @param args The command line arguments by which we are instructed to run a test (or not)
	 * @throws FileNotFoundException  If the config file cannot be found
	 * @throws NotBoundException If we cannot bind to our port
	 * @throws RemoteException If a remote exception occurs
	 * @throws MalformedURLException If a bad ip was specified in the config file
	 * @throws AlreadyBoundException If we are rebinding a port
	 */
	public static void main(String[] args) throws FileNotFoundException, MalformedURLException, RemoteException, NotBoundException, AlreadyBoundException {
		Long ourid = Long.parseLong(args[0]);
		Map<Long, RemoteHost> hosts = new ConfigReader().read();
		RemoteHost me = hosts.get(ourid);
		
		Map<Long, Collection<RemoteHost>> requestSets = 
		
		 java.rmi.registry.Registry reg = java.rmi.registry.LocateRegistry.createRegistry(me.getRegport());
		
		try
		{
			/*
			 * Set up all classes 
			 */
			main listener = new main(me.getId());
			listener.testing = args.length > 1;
			
			Connector c = new Connector(me);
			c.setIndex(hosts);
	
			TotalOrder torder = new TotalOrder(c, me.getId(), hosts.keySet(), listener);
			
			/*
			 * If not in testing mode launch the consule UI
			 * Otherwise execute the testing as instructed by the arguments
			 */
			if (!listener.testing){
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				while (true)
				{
					
					try {
						System.out.print(" > ");
						String line = br.readLine();
						
						if (line.toLowerCase().equals("exit"))
							break;
		
						Message message = new Message(line);
		
						torder.broadcast(message);
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else { 
				
				if (!"silent".equals(args[1])){
					try {
						Thread.sleep(500); // Wait for all processes to start running
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					/*
					 * Sequentially start broadcasting all our instructed messages
					 */
					for (int i = 1; i < args.length; i++){
						Message message = new Message(args[i]);
						torder.broadcast(message);
					}
				}
				
				/*
				 * Wait for everyone to finish 
				 */
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		finally
		{
			/*
			 * Release our port
			 */
			java.rmi.server.UnicastRemoteObject.unexportObject(reg,true);
			System.out.println("SHUTTING DOWN");
			System.exit(0);
		}
	}

	@Override
	/**
	 * Callback for when a message is delivered.
	 * If we are in interactive UI mode, show a pretty frame with a 
	 * colorized message.
	 */
	public void deliverMessage(final Message message) {
		if (!testing)
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				JFrame frame = new JFrame("Process " + myId + " | Message Delivery");
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
				frame.getContentPane().add(new JLabel("You ("+myId+") got the following message delivered from " + message.getID().getBroadcaster() + ":"));
				frame.getContentPane().add(new JLabel(" "));
				frame.getContentPane().add(new JLabel("<html><font color='red'>" + message.toString() + "</font></html>"));
				
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

}
