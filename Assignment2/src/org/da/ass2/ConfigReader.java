package org.da.ass2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Dirty, dirty way to read in ids coupled to ips and ports
 *
 */
public class ConfigReader {

	/**
	 * Reads a config.txt and transforms it into a map of process ids and RemoteHosts
	 * 
	 * @return A map of process ids for RemoteHosts
	 * @throws FileNotFoundException IF config.txt could not be found
	 */
	public Map<Long, RemoteHost> read() throws FileNotFoundException{
		HashMap<Long, RemoteHost> map = new HashMap<Long, RemoteHost>();
		File f = new File("config.txt");
		Scanner sc = new Scanner(f);
		
		while (sc.hasNext()){
			long id = sc.nextLong();
			String ip = sc.next();
			int regport = sc.nextInt();
			
			String rest = sc.nextLine();
			Scanner ssc = new Scanner(rest);
			ArrayList<Integer> groups = new ArrayList<Integer>();
			while (ssc.hasNext()){
				group.add(ssc.nextInt());
			}
			
			map.put(id, new RemoteHost(id, ip, regport, groups));
		}
		
		sc.close();
		
		return map;
	}
}
