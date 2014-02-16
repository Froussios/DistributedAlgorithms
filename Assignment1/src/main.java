import java.io.*;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.da.ass1.*;
import org.da.ass1.messages.*;


public class main implements TotalOrderListener{

	private long myId = -1;
	
	public main(long id){
		myId = id;
	}
	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 * @throws AlreadyBoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, MalformedURLException, RemoteException, NotBoundException, AlreadyBoundException {
		Long ourid = Long.parseLong(args[0]);
		Map<Long, RemoteHost> hosts = new ConfigReader().read();
		RemoteHost me = hosts.get(ourid);
		java.rmi.registry.LocateRegistry.createRegistry(me.getRegport());
		
		main listener = new main(me.getId());
		
		Connector c = new Connector(me);
		c.setIndex(hosts);

		TotalOrder torder = new TotalOrder(c, me.getId(), hosts.keySet(), listener);
		
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
		
		System.out.println("SHUTTING DOWN");
		System.exit(0);
	}

	@Override
	public void deliverMessage(final Message message) {
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
