import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.rmi.RemoteException;
import java.util.Map;

import org.da.ass3.CandidateProcess;
import org.da.ass3.ConfigReader;
import org.da.ass3.Connector;
import org.da.ass3.OrdinaryProcess;
import org.da.ass3.RemoteHost;


public class SingleProcess
	extends Thread{
	
	/**
	 * args[0] = our id
	 * args[1] = y if candidate process, n if ordinary process
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws RemoteException
	 */
	public static void main(String[] args) throws FileNotFoundException, RemoteException{
		Long ourid = Long.parseLong(args[0]);
		Map<Long, RemoteHost> hosts = new ConfigReader().read();
		RemoteHost me = hosts.get(ourid);
		
		java.rmi.registry.Registry reg = java.rmi.registry.LocateRegistry.createRegistry(me.getRegport());

		try
		{
			Connector connector = new Connector(me);
			connector.setIndex(hosts);
	
			OrdinaryProcess op = new OrdinaryProcess(connector, ourid);
			CandidateProcess cp = "y".equals(args[1]) ? new CandidateProcess(connector, ourid, hosts.keySet()) : null;
			op.start();
			
			FileWriter fw = new FileWriter(ourid + ".txt", false);
			fw.write(" ");
			fw.close();
			
			File barrier = new File("BARRIER.txt");
			while (!barrier.exists()){
				Thread.sleep(200);
			}
			
			if ("y".equals(args[1])){
				cp.start();
			}
			
			op.join();
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
	
	
	public final String[] args;
	
	
	public SingleProcess(String... args) {
		this.args = args;
	}
	
	
	public void run() {
		try {
			SingleProcess.main(args);
		} catch (FileNotFoundException | RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
