package org.da.ass1.messages;

/**
 * An ACK for a message
 *
 */
public class Acknowledgement extends GenericMessage{

	/**
	 * The scalar clock id of the Message to 
	 * acknowledge
	 * 
	 * @param timestamp The timestamp we are acknowledging
	 */
	public Acknowledgement(long timestamp){
		setTimestamp(timestamp);
	}
}