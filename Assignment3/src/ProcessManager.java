import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

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

		/*
		 * Set number of processes
		 */
		int amount = 30;
		int candidates = amount;
		if (args.length == 2) {
			amount = Integer.valueOf(args[0]);
			candidates = Integer.valueOf(args[1]);
		}

		for (int i = 1; i <= amount; i++) {
			int port = portGenerator.next();
			fw.write(i + " localhost " + port + "\n");
		}
		fw.close();

		/*
		 * Launch
		 */
		System.out.println("" + amount + " processes, " + candidates + " candidates.");
		
		ArrayList<ProcessThread> processes = new ArrayList<ProcessThread>();
		
		for (long i = 1; i <= amount; i++) {
			File fname = new File(i + ".txt");
			fname.delete();
			
			String candidate = "n";
			if (i <= candidates)
				candidate = "y";
			processes.add(invokeJar("" + i, candidate));
		}

		System.out.print("Dispatched work, waiting for setup (PRESS ANYKEY+ENTER TO ABORT) ");
		for (long i = 1; i <= amount; i++) {
			File fname = new File(i + ".txt");
			while (!fname.exists()){
				try {
					if (System.in.available() != 0){
						break;
					}
					Thread.sleep(10);	
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}	
			fname.delete();
			if (i % (amount/3) == 0)
				System.out.print(".");
		}
		System.out.println();
		System.out.println("Workload accepted: starting algorithm (est. time = " + ((candidates * Math.log(candidates) + candidates) * 0.1) + " seconds)");
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
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
		long startTime = System.currentTimeMillis();
		System.out.print("Waiting for processes to finish (PRESS ANYKEY+ENTER TO ABORT) ");
		String result = "";
		for (long i = 1; i <= candidates; i++) {
			File fname = new File(i + ".txt");
			while (!fname.exists()){
				try {
					if (System.in.available() != 0){
						break;
					}
					Thread.sleep(10);	
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}	
			Scanner sc = new Scanner(fname);
			String line = i + ": " + sc.nextLine();
			result += line + "\n";
			sc.close();
			if (i % (amount/3) == 0)
				System.out.print(".");
		}
		System.out.println();
		
		/*
		 * Kill all 
		 */
		for (ProcessThread t : processes) {
			t.kill();
		}
		
		/*
		 * Cleanup results
		 */
		for (long i = 1; i <= candidates; i++) {
			File fname = new File(i + ".txt");
			fname.delete();
		}
		
		System.out.println("DONE, time taken = " + ((System.currentTimeMillis()-startTime)/1000.0d) + " seconds");
		System.out.print(result);
	}

	private static class ProcessThread extends Thread{
		
		private Process process = null;
		private SingleProcess singleProcess = null;
		private final String[] args;
		
		public ProcessThread(String... args){
			this.args = args;
		}
		
		public void kill(){
			if (process != null)
				process.destroy();
			else if (singleProcess != null)
				singleProcess.interrupt();
		}
		

		@Override
		public void run() {
			runJar();
			//runThread();
		}
		
		public void runJar() {
			try {
				String command = "java -jar ass3.jar";
				for (String arg : args)
					command += " \"" + arg + "\"";
				process = Runtime.getRuntime().exec(command);
				ISConsumer stdout = new ISConsumer(process.getInputStream());
				ISConsumer stderr = new ISConsumer(process.getErrorStream());
				stdout.start();
				stderr.start();
				process.waitFor();
				stdout.kill();
				stderr.kill();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void runThread() {
			singleProcess = new SingleProcess(args);
			singleProcess.start();
			try {
				singleProcess.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static class ISConsumer extends Thread{
		private final InputStream is;
		private boolean alive = true;
		
		public ISConsumer (InputStream is){
			this.is = is;
		}
		
		public void kill(){
			alive = false;
		}
		
		public void run(){
			while (alive){
				try {
					is.read();
					if (is.available() == 0)
						Thread.sleep(10);
				} catch (IOException | InterruptedException e) {
				}
			}
		}
	}
}
