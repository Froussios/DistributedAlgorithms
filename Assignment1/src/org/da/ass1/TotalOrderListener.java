package org.da.ass1;

import org.da.ass1.messages.*;

public interface TotalOrderListener {
	public void deliverMessage(Message message);
}
