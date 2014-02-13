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
		
		c.send(1, new Message());
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
