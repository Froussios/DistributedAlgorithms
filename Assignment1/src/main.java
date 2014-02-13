import java.io.FileNotFoundException;
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
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Long ourid = Long.parseLong(args[0]);
		Map<Long, RemoteHost> hosts = new ConfigReader().read();
		RemoteHost me = hosts.get(ourid);
		
		Connector c = new Connector();
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
		// TODO Auto-generated method stub
		return 0;
	}

}
