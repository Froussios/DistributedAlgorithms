package org.da.ass1.messages;

import java.io.Serializable;

/**
 * Generic Message for sending to another process
 * using RMI
 *
 */
public abstract class GenericMessage implements Serializable, Comparable<GenericMessage>{

	/**
	 * An id that uniquely identified each broadcasted message in the network.
	 * The id consists of the id of the process that broadcasted the message and local time at the broadcasting process
	 * 
	 * @author Chris
	 *
	 */
	public static class MessageID implements Serializable
	{
		private static final long serialVersionUID = 1L;
		
		private long process;
		private long timeSent;
		
		public long getBroadcaster() { return this.process; }
		public long getBroadcasterTime() { return this.timeSent; }
		
		public MessageID(long inBroadcaster, long inBroadcasterTime) {
			this.process = inBroadcaster;
			this.timeSent = inBroadcasterTime;
		}
		
		@Override
		public boolean equals(Object obj)	{
			if ( obj instanceof MessageID )
			{
				MessageID other = (MessageID) obj;
				return this.process == other.process
						&& this.timeSent == other.timeSent;
			}
			else
				return false;
		}
		@Override
		public int hashCode() {
			return ((int) process)<<16+timeSent;
		}

	}
	
	
	private static final long serialVersionUID = 1L;
	
	private long timestamp;
	
	private MessageID id = null;
	
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
	
	/**
	 * Set the unique id of this message
	 * @param inMessageID
	 */
	public void setID(MessageID inMessageID) {
		this.id = inMessageID;
	}
	
	/**
	 * Get an object that uniquely identifies this message
	 * Broadcasted messages are identified as the same
	 * @param inMessageID
	 */
	public MessageID getID() {
		return this.id;
	}
	
	//@Override
    public int compareTo(GenericMessage other) {
		return Long.compare(this.timestamp, other.timestamp);
	}
	
}
