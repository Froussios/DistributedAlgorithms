import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public class test {

	private static String testing = "";
	
	public static void testConcurrentMultipleBroadcast() throws FileNotFoundException, MalformedURLException, RemoteException, NotBoundException, AlreadyBoundException{
		testing = "testConcurrentMultipleBroadcast";
		invokeJar("1", "1Hello World!", "1Hello 2 World!", "1Hello 3 World!");
		invokeJar("2", "2Hello World!", "2Hello 2 World!", "2Hello 3 World!");
		invokeJar("3", "3Hello World!", "3Hello 2 World!", "3Hello 3 World!");
		invokeJar("4", "4Hello World!", "4Hello 2 World!", "4Hello 3 World!");
		
		try {
			Thread.sleep(11000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	public static void testConcurrentBroadcast() throws FileNotFoundException, MalformedURLException, RemoteException, NotBoundException, AlreadyBoundException{
		testing = "testConcurrentBroadcast";
		invokeJar("1", "Hello World!");
		invokeJar("2", "Hello 2 World!");
		invokeJar("3", "Hello 3 World!");
		invokeJar("4", "Hello 4 World!");
		
		try {
			Thread.sleep(11000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	public static void testMultipleBroadcast() throws FileNotFoundException, MalformedURLException, RemoteException, NotBoundException, AlreadyBoundException{
		testing = "testMultipleBroadcast";
		invokeJar("1", "silent");
		invokeJar("2", "silent");
		invokeJar("3", "silent");
		invokeJar("4", "Hello World!", "Hello 2 World!", "Hello 3 World!", "Hello 4 World!", "Hello 5 World!", "Hello 6 World!", "Hello 7 World!", "Hello 8 World!", "Hello 9 World!", "Hello 10 World!");
		
		try {
			Thread.sleep(11000);
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
			testConcurrentMultipleBroadcast();
		} catch (Exception e) {
			System.err.println(testing + " failed with exception:");
			e.printStackTrace();
		}
	}
}
