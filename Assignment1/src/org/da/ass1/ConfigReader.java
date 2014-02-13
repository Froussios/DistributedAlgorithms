package org.da.ass1;

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

	public Map<Long, RemoteHost> read() throws FileNotFoundException{
		HashMap<Long, RemoteHost> map = new HashMap<Long, RemoteHost>();
		File f = new File("config.txt");
		Scanner sc = new Scanner(f);
		
		while (sc.hasNext()){
			long id = sc.nextLong();
			String ip = sc.next();
			int regport = sc.nextInt();
			
			map.put(id, new RemoteHost(id, ip, regport));
		}
		
		return map;
	}
}
