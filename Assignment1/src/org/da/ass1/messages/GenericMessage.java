package org.da.ass1.messages;

import java.io.Serializable;

/**
 * Generic Message for sending to another process
 * using RMI
 *
 */
public abstract class GenericMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private long timestamp;
	
	/**
	 * Set the timestamp belonging to this message
	 * 
	 * @param timestamp The new timestamp to use
	 */
	public void setTimestamp(long timestamp){
		this.timestamp = timestamp;
	}
	
	/**
	 * Retrieve the timestamp belonging to this message
	 * 
	 * @return The timestamp of this message
	 */
	public long getTimestamp(){
		return this.timestamp;
	}
	
}
