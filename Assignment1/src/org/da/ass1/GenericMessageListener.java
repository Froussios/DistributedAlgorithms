package org.da.ass1;

import org.da.ass1.messages.GenericMessage;

/**
 * Defines classes that can receive GenericMessages
 *
 */
public interface GenericMessageListener {

	public void receive(GenericMessage gm, long fromProcess);
	
	public long getProcessId();
	
}
