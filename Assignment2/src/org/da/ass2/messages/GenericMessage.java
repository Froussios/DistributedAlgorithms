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
		
		/**
		 * Retrieve the broadcaster that sent this message
		 * 
		 * @return The id of the broadcaster
		 */
		public long getBroadcaster() { 
			return this.process; 
		}
		
		/**
		 * Retrieve the broadcasters scalar clock value
		 * 
		 * @return The scalar timestamp for the message
		 */
		public long getBroadcasterTime() { 
			return this.timeSent; 
		}
		
		/**
		 * Construct a MessageID for a certain broadcaster and
		 * scalar timestamp
		 * 
		 * @param inBroadcaster The broadcaster's id
		 * @param inBroadcasterTime The broadcaster's scalar timestamp
		 */
		public MessageID(long inBroadcaster, long inBroadcasterTime) {
			this.process = inBroadcaster;
			this.timeSent = inBroadcasterTime;
		}
		
		@Override
		/**
		 * An MessageID equals another object if it has the 
		 * same broadcaster id and broadcaster timestamp
		 */
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
		
		@Override
		public String toString() {
			return "[" + getBroadcaster() + ":" + getBroadcasterTime() + "]";
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
	
	/**
	 * Order on timestamp value
	 */
    public int compareTo(GenericMessage other) {
		return Long.compare(this.timestamp, other.timestamp);
	}
	
}
