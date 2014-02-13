import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

import org.da.ass1.ConfigReader;
import org.da.ass1.Connector;
import org.da.ass1.GenericMessageListener;
import org.da.ass1.RemoteHost;
import org.da.ass1.messages.GenericMessage;
import org.da.ass1.messages.Message;


public class main implements GenericMessageListener{

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 * @throws AlreadyBoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, MalformedURLException, RemoteException, NotBoundException, AlreadyBoundException {
		java.rmi.registry.LocateRegistry.createRegistry(1099);
		
		Long ourid = Long.parseLong(args[0]);
		Map<Long, RemoteHost> hosts = new ConfigReader().read();
		RemoteHost me = hosts.get(ourid);
		
		Connector c = new Connector(me);
		c.setIndex(hosts);
		c.subscribe(new main());
		
		System.out.println("INITIALIZING REMOTE INVOCATION AND SLEEPING FOR 5 SECONDS");
		c.send(1, new Message());
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("SHUTTING DOWN");
	}

	@Override
	public void receive(GenericMessage gm, long fromProcess) {
		System.out.println("RECEIVED GENERIC MESSAGE " + gm.toString());
	}

	@Override
	public long getProcessId() {
		return 1;
	}

}
