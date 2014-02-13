package org.da.ass1;

public class RemoteHost {

	private final long id;
	private final String ip;
	private final int regport;
	private final int objport;
	
	public RemoteHost(long id, String ip, int regport, int objport){
		this.id = id;
		this.ip = ip;
		this.regport = regport;
		this.objport = objport;
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

	public int getObjport() {
		return objport;
	}
	
	public boolean isMe() {
		return "ME".equals(ip);
	}
}
