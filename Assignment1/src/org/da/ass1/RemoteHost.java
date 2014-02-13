package org.da.ass1;

public class RemoteHost {

	private final long id;
	private final String ip;
	private final int regport;

	public RemoteHost(long id, String ip, int regport){
		this.id = id;
		this.ip = ip;
		this.regport = regport;
	}

	public long getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public int getRegport() {
		return regport;
	}
	
	public String getURL(String objectName) {
		return "rmi://" + getIp() + ":" + getRegport() + "/" + objectName;
	}
	
}
