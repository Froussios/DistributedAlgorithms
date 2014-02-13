package org.da.ass1.messages;

/**
 * Generic Message for sending to another process
 * using RMI
 *
 */
public abstract class GenericMessage {

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
