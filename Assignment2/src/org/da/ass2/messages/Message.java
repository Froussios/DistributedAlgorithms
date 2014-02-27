package org.da.ass2.messages;

/**
 * A message object for passing to another process
 *
 */
public class Message extends GenericMessage{

	private static final long serialVersionUID = 1L;
	
	private String payload;
	
	/**
	 * Construct a Message with an empty String as payload
	 */
	public Message(){
		this("");
	}
	
	/**
	 * Construct a Message with a certain payload String
	 * 
	 * @param payload The actual message content
	 */
	public Message(String payload){
		this.payload = payload;
	}
	
	/**
	 * Return the payload as the string representation
	 */
	public String toString() {
		return this.payload;
	}
	
}
