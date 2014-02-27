package org.da.ass2.messages;

/**
 * An ACK for a message
 *
 */
public class Acknowledgement extends GenericMessage{

	private static final long serialVersionUID = 1L;
	private MessageID acknowledging;
	
	/**
	 * The scalar clock id of the Message to 
	 * acknowledge
	 * 
	 * @param timestamp The timestamp we are acknowledging
	 */
	public Acknowledgement(MessageID inAcknowledging){
		this.acknowledging = inAcknowledging;
	}
	
	/**
	 * Find out what message we are acknowledging
	 * 
	 * @return The MessageID we are acknowledging
	 */
	public MessageID getAckOf(){
		return acknowledging;
	}
	
	/**
	 * A pretty string representation for the log
	 */
	public String toString(){
		return "ACK(" + acknowledging.getBroadcaster() +":"+ acknowledging.getBroadcasterTime() + ")";
	}
}
