package org.da.ass3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.da.ass3.RemoteHost;

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
			if (sc.hasNext("#")) {
				sc.nextLine();
				continue;
			}
			long id = sc.nextLong();
			String ip = sc.next();
			int regport = sc.nextInt();

			map.put(id, new RemoteHost(id, ip, regport));
		}
		
		sc.close();
		
		return map;
	}
}
