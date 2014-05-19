import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.da.ass3.util.UniqueGenerator;

public class ProcessManager {

	/**
	 * Invoke a the Assignment1.jar with certain command line arguments
	 * 
	 * @param args
	 *            The command line arguments
	 */
	private static ProcessThread invokeJar(final String... args) {
		ProcessThread t = new ProcessThread(args);
		t.start();
		return t;
	}

	/**
	 * args[0] amount of processes to create args[1] amount of candidates to
	 * create
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File delme = new File("BARRIER.txt");
		delme.delete();
		delme = new File("config.txt");
		delme.delete();
		
		/*
		 * Generate config.txt
		 */
		UniqueGenerator portGenerator = new UniqueGenerator(1105);
		FileWriter fw = new FileWriter("config.txt", false);

		int amount = Integer.valueOf(args[0]);
		int candidates = Integer.valueOf(args[1]);

		for (int i = 0; i < amount; i++) {
			int port = portGenerator.next();
			fw.write(i + " localhost " + port);
		}
		fw.close();

		/*
		 * Launch
		 */
		ArrayList<ProcessThread> processes = new ArrayList<ProcessThread>();
		
		for (long i = 1; i <= amount; i++) {
			File fname = new File(i + ".txt");
			fname.delete();
			
			String candidate = "n";
			if (i <= candidates)
				candidate = "y";
			processes.add(invokeJar("" + i, candidate));
		}
		
		/*
		 * BARRIER
		 */
		FileWriter barrier = new FileWriter("BARRIER.txt", false);
		barrier.write(" ");
		barrier.close();
		
		/*
		 * Gather data 
		 */
		for (long i = 1; i <= amount; i++) {
			File fname = new File(i + ".txt");
			while (!fname.exists()){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
		
		/*
		 * Kill all 
		 */
		for (ProcessThread t : processes) {
			t.kill();
		}
		
		System.out.println("DONE");
	}

	private static class ProcessThread extends Thread{
		
		private Process process;
		private final String[] args;
		
		public ProcessThread(String... args){
			this.args = args;
		}
		
		public void kill(){
			process.destroy();
		}
		
		@Override
		public void run() {
			try {
				String command = "java -jar ass3.jar";
				for (String arg : args)
					command += " \"" + arg + "\"";
				process = Runtime.getRuntime().exec(command);
				process.waitFor();
				InputStream in = process.getInputStream();
				InputStream err = process.getErrorStream();

				byte b[] = new byte[in.available()];
				in.read(b, 0, b.length);
				System.out.println(new String(b));

				byte c[] = new byte[err.available()];
				err.read(c, 0, c.length);
				System.out.println(new String(c));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
