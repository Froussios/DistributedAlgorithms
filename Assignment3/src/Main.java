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
	}
	
}
