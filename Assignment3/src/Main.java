import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;

import org.da.ass3.CandidateProcess;
import org.da.ass3.Connector;
import org.da.ass3.OrdinaryProcess;
import org.da.ass3.RemoteHost;


public class Main {

	public static void main(String[] args) throws RemoteException, MalformedURLException, AlreadyBoundException, InterruptedException{
		
		java.rmi.registry.Registry reg = java.rmi.registry.LocateRegistry.createRegistry(1105);
		java.rmi.registry.Registry reg2 = java.rmi.registry.LocateRegistry.createRegistry(1106);
		
		HashMap<Long, RemoteHost> index = new HashMap<Long, RemoteHost>();
		index.put(1L, new RemoteHost(1L, "localhost", 1105));
		index.put(2L, new RemoteHost(2L, "localhost", 1106));
		
		try{
			Connector c1 = new Connector(new RemoteHost(1, "localhost", 1105));
			c1.setIndex(index);
			OrdinaryProcess proc1 = new OrdinaryProcess(c1, 1);
			
			Connector c2 = new Connector(new RemoteHost(2, "localhost", 1106));
			c2.setIndex(index);
			OrdinaryProcess proc2 = new OrdinaryProcess(c2, 2);
			CandidateProcess proc2_c = new CandidateProcess(c2, 2, Arrays.asList(new Long[] {1L, 2L}));
			
			// Start the ordinary processes
			proc1.start();
			proc2.start();
			
			// Wait for them to set up
			Thread.sleep(500);
			
			// Start the candidate
			proc2_c.start();
			
			// Wait for proc2_c to be elected
			proc2_c.join(5000);
			
			// Kill ordinary processes
			proc1.kill();
			proc2.kill();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Release registries
		java.rmi.server.UnicastRemoteObject.unexportObject(reg,true);
		java.rmi.server.UnicastRemoteObject.unexportObject(reg2,true);
		System.out.println("DONE");
		System.exit(0);
	}
	
}
