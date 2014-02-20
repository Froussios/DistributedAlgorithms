import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public class test {

	private static String testing = "";
	
	public static void testSingleBroadcast() throws FileNotFoundException, MalformedURLException, RemoteException, NotBoundException, AlreadyBoundException{
		testing = "testSingleBroadcast";
		invokeJar("1", "silent");
		invokeJar("2", "silent");
		invokeJar("3", "silent");
		invokeJar("4", "Hello World!", "Hello World2!");
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void invokeJar(final String... args){
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					String command = "java -jar ass1.jar";
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
				    System.out.println(new String(c));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		t.start();
	}
	
	public static void main(String[] args){
		try {
			testSingleBroadcast();
		} catch (Exception e) {
			System.err.println(testing + " failed with exception:");
			e.printStackTrace();
		}
	}
}
