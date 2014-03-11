import java.io.*;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
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
public class Main{
	
	/**
	 * From a group of hosts construct a group -> hosts mapping
	 * 
	 * @param hosts The mapping of hosts
	 * @return The group -> hosts mapping
	 */
	public static Map<Integer, Collection<RemoteHost>> fromHosts(Map<Long, RemoteHost> hosts){
		Map<Integer, Collection<RemoteHost>> out = new HashMap<Integer, Collection<RemoteHost>>();
		for (RemoteHost host : hosts.values()){
			for (Integer g : host.getGroups()){
				if (!out.containsKey(g)){
					out.put(g, new ArrayList<RemoteHost>());
				}
				out.get(g).add(host);
			}
		}
		return out;
	}
	
	/**
	 * We are the main process, deploy all the jars.
	 * @throws FileNotFoundException 
	 * @throws InterruptedException 
	 */
	public static void deployJars(int timeout) throws FileNotFoundException, InterruptedException{
		Map<Long, RemoteHost> hosts = new ConfigReader().read();
		LinkedList<Thread> threads = new LinkedList<Thread>();
		for (Long id : hosts.keySet()){
			Thread t = invokeJar("" + id);
			threads.add(t);
		}
		
		for (Thread t : threads)
			t.join(timeout);
	}
	
	/**
	 * Invoke a the Assignment1.jar with certain command line arguments
	 * 
	 * @param args The command line arguments
	 */
	private static Thread invokeJar(final String... args){
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					/*String command = "java -jar ass2.jar";
					for (String arg: args)
						command += " \"" + arg + "\"";
					Process p = Runtime.getRuntime().exec(command);
					p.waitFor();
					InputStream in = p.getInputStream();
					InputStream err = p.getErrorStream();

					byte b[]=new byte[in.available()];
					in.read(b,0,b.length);
					System.out.println(new String(b));

					byte c[]=new byte[err.available()];
					err.read(c,0,c.length);
					System.out.println(new String(c));*/
					try {
						Main.main(args);
					} catch (NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (AlreadyBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		});

		t.start();
		return t;
	}
	
	public static void main(String[] args) throws FileNotFoundException, MalformedURLException, RemoteException, NotBoundException, AlreadyBoundException, InterruptedException {
		//We are the main process that launches the components
		int threadTimeout = 8000;
		
		if (args.length == 0){
			deployJars(threadTimeout+500);
			System.out.println("SHUTTING DOWN");
			System.exit(0);
		}
		//Otherwise we are supposed to start requesting CSs
		Long ourid = Long.parseLong(args[0]);
		Map<Long, RemoteHost> hosts = new ConfigReader().read();
		RemoteHost me = hosts.get(ourid);
		
		Map<Integer, Collection<RemoteHost>> requestSets = fromHosts(hosts); 
		
		java.rmi.registry.Registry reg = java.rmi.registry.LocateRegistry.createRegistry(me.getRegport());
		
		try
		{
			Thread.sleep(2000);
			
			/*
			 * Set up all classes 
			 */
			Connector connector = new Connector(me);
			connector.setIndex(hosts);
			Component comp = new Component(connector, me, hosts.keySet(), requestSets);
			comp.useResources(1);
			
			Thread.sleep(threadTimeout);
		} catch (Exception e){
			e.printStackTrace();
		}
		finally
		{
			/*
			 * Release our port
			 */
			System.out.println(me.getId() + " stopped listening");
			java.rmi.server.UnicastRemoteObject.unexportObject(reg,true);
		}
	}

}
