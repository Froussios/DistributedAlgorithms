import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.da.ass3.CandidateProcess;
import org.da.ass3.Connector;
import org.da.ass3.OrdinaryProcess;
import org.da.ass3.RemoteHost;
import org.da.ass3.util.UniqueGenerator;


public class Main {

	public static void main(String[] args) throws RemoteException, MalformedURLException, AlreadyBoundException, InterruptedException{
		
		UniqueGenerator portGenerator = new UniqueGenerator(1105);
		
		Collection<Long> allIds = new ArrayList<>();
		Collection<Long> candidateIds = new ArrayList<>();
		
		int ordinaryCount = 20;
		int candidateCount = 20;
		for (long i=1 ; i<=ordinaryCount ; i++) {
			allIds.add(i);
			if (i <= candidateCount) 
				candidateIds.add(i);
		}
		
//		allIds.addAll(Arrays.asList(1L, 2L, 3L, 4L));
//		candidateIds.addAll(Arrays.asList(1L, 2L, 3L, 4L));
		
		ArrayList<Registry> registries = new ArrayList<>();
		HashMap<Long, RemoteHost> index = new HashMap<Long, RemoteHost>();	
		ArrayList<OrdinaryProcess> ordinaryProcesses = new ArrayList<>();
		ArrayList<CandidateProcess> candidateProcesses = new ArrayList<>();
		
		for (Long id : allIds) {
			int port = portGenerator.next();
			registries.add(LocateRegistry.createRegistry(port));
			index.put(id, new RemoteHost(id, "localhost", port));
			
			Connector connector = new Connector(new RemoteHost(id, "localhost", port));
			connector.setIndex(index);
			ordinaryProcesses.add(new OrdinaryProcess(connector, id));
			
			if (candidateIds.contains(id))
				candidateProcesses.add(new CandidateProcess(connector, id, allIds));
		}
		
		// Start the ordinary processes
		for (OrdinaryProcess op : ordinaryProcesses) {
			op.start();
		}
		
		Thread.sleep(500);
		
		// Start the candidates
		for (CandidateProcess cp : candidateProcesses) {
			cp.start();
		}
		
		// Wait for election
		for (CandidateProcess cp : candidateProcesses) {
			cp.join();
		}
//		boolean first = true;
//		for (CandidateProcess cp : candidateProcesses) {
//			if (first)
//				cp.join(5000);
//			else
//				cp.join(500);
//			first = false;
//		}	
		
		// Print ending state
		boolean correct = true;
		for (CandidateProcess cp : candidateProcesses) {
			correct = correct && cp.isDone();
			System.out.println("Candidate " + cp.getProcessId() + " was elected: " + cp.isElected());
		}
		System.out.println("Output is valid: " + correct);
		
		// Kill everything
		for ( CandidateProcess cp : candidateProcesses )
			cp.kill();
		for ( OrdinaryProcess op : ordinaryProcesses )
			op.kill();
		
		// Cleanup and exit
		for (Registry registry : registries) {
			java.rmi.server.UnicastRemoteObject.unexportObject(registry, true);
		}
		System.out.println("DONE");
		System.exit(0);
		
		
//		java.rmi.registry.Registry reg = java.rmi.registry.LocateRegistry.createRegistry(1105);
//		java.rmi.registry.Registry reg2 = java.rmi.registry.LocateRegistry.createRegistry(1106);
//		java.rmi.registry.Registry reg3 = java.rmi.registry.LocateRegistry.createRegistry(1107);
//		java.rmi.registry.Registry reg4 = java.rmi.registry.LocateRegistry.createRegistry(1108);
//		
//		HashMap<Long, RemoteHost> index = new HashMap<Long, RemoteHost>();
//		index.put(1L, new RemoteHost(1L, "localhost", 1105));
//		index.put(2L, new RemoteHost(2L, "localhost", 1106));
//		index.put(3L, new RemoteHost(3L, "localhost", 1107));
//		index.put(4L, new RemoteHost(4L, "localhost", 1108));
//		
//		try{
//			Connector c1 = new Connector(new RemoteHost(1, "localhost", 1105));
//			c1.setIndex(index);
//			OrdinaryProcess proc1 = new OrdinaryProcess(c1, 1);
//			CandidateProcess proc1_c = new CandidateProcess(c1, 1, Arrays.asList(new Long[] {1L, 2L, 3L, 4L}));
//			
//			Connector c2 = new Connector(new RemoteHost(2, "localhost", 1106));
//			c2.setIndex(index);
//			OrdinaryProcess proc2 = new OrdinaryProcess(c2, 2);
//			CandidateProcess proc2_c = new CandidateProcess(c2, 2, Arrays.asList(new Long[] {1L, 2L, 3L, 4L}));
//			
//			Connector c3 = new Connector(new RemoteHost(3, "localhost", 1107));
//			c3.setIndex(index);
//			OrdinaryProcess proc3 = new OrdinaryProcess(c3, 3);
//			CandidateProcess proc3_c = new CandidateProcess(c3, 3, Arrays.asList(new Long[] {1L, 2L, 3L, 4L}));
//			
//			Connector c4 = new Connector(new RemoteHost(4, "localhost", 1108));
//			c4.setIndex(index);
//			OrdinaryProcess proc4 = new OrdinaryProcess(c4, 4);
//			CandidateProcess proc4_c = new CandidateProcess(c4, 4, Arrays.asList(new Long[] {1L, 2L, 3L, 4L}));
//			
//			// Start the ordinary processes
//			proc1.start();
//			proc2.start();
//			proc3.start();
//			proc4.start();
//			
//			// Wait for them to set up
//			Thread.sleep(500);
//			
//			// Start the candidate
//			proc1_c.start();
//			proc2_c.start();
//			proc3_c.start();
//			proc4_c.start();
//			
//			// Wait for proc2_c to be elected
//			proc1_c.join(5000);
//			proc2_c.join(500);
//			proc3_c.join(500);
//			proc4_c.join(500);
//			
//			boolean correct = proc1_c.isDone() && proc2_c.isDone() && proc3_c.isDone() && proc4_c.isDone();
//			
//			System.out.println("Proc1_c was elected: " + proc1_c.isElected());
//			System.out.println("Proc2_c was elected: " + proc2_c.isElected());
//			System.out.println("Proc3_c was elected: " + proc3_c.isElected());
//			System.out.println("Proc4_c was elected: " + proc4_c.isElected());
//			
//			System.out.println("Output is valid: " + correct);
//			
//			// Kill ordinary processes
//			proc1.kill();
//			proc2.kill();
//			proc3.kill();
//			proc4.kill();
//			proc1_c.kill();
//			proc2_c.kill();
//			proc3_c.kill();
//			proc4_c.kill();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		// Release registries
//		java.rmi.server.UnicastRemoteObject.unexportObject(reg,true);
//		java.rmi.server.UnicastRemoteObject.unexportObject(reg2,true);
//		java.rmi.server.UnicastRemoteObject.unexportObject(reg3,true);
//		java.rmi.server.UnicastRemoteObject.unexportObject(reg4,true);
		
	}
	
}
