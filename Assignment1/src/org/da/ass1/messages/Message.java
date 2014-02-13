package org.da.ass1.messages;

/**
 * A message object for passing to another process
 *
 */
public class Message extends GenericMessage{

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
	
	public String toString() {
		return this.payload;
	}
	
}
