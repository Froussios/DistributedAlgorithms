package org.da.ass3;

import org.da.ass3.RemoteHost;

/**
 * A container that hold the info that specify a process on the network
 * 
 * @author Chris
 *
 */
public class RemoteHost implements Comparable<RemoteHost> {

	private final long id;
	private final String ip;
	private final int regport;

	/**
	 * New instance
	 * @param id The process id
	 * @param ip The ip of the host
	 * @param regport The port the host is listening to
	 */
	public RemoteHost(long id, String ip, int regport){
		this.id = id;
		this.ip = ip;
		this.regport = regport;
	}

	/**
	 * Get the process id
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * Get the ip address of the host
	 * @return
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Get the port the host is listening to
	 * @return
	 */
	public int getRegport() {
		return regport;
	}
	
	/**
	 * Format into a RMI url
	 * @param objectName The object name to get
	 * @return The location in RMI url form
	 */
	public String getURL(String objectName) {
		return "rmi://" + getIp() + ":" + getRegport() + "/" + objectName;
	}
	

	/**
	 * Compare by id
	 */
	@Override
	public int compareTo(RemoteHost arg0) {
		return Long.compare(this.getId(), arg0.getId());
	}
	
}

