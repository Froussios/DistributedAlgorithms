package org.da.ass1.messages;

/**
 * An ACK for a message
 *
 */
public class Acknowledgement extends GenericMessage{

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
	
	public MessageID getAckOf(){
		return acknowledging;
	}
	
	public String toString(){
		return "ACK(" + acknowledging.getBroadcaster() +":"+ acknowledging.getBroadcasterTime() + ")";
	}
}
