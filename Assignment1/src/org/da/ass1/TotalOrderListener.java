package org.da.ass1;

import org.da.ass1.messages.*;

/**
 * Users of {@code Total Order} receive messages on an instance that implements this interface.
 * 
 * @author Chris
 *
 */
public interface TotalOrderListener {
	/**
	 * Handle a message that is delivered
	 * @param message The message that is being delivered
	 */
	public void deliverMessage(Message message);
}
