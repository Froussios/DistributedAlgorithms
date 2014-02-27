package org.da.ass2;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class Component {

	private Connector connector;
	private RemoteHost me;
	private Set<Long> allIds;
	private Map<Integer, Collection<RemoteHost>> requestSets;
	
	public Component(Connector connector, RemoteHost me, Set<Long> allIds, Map<Integer, Collection<RemoteHost>> requestSets){
		this.connector = connector;
		this.me = me;
		this.allIds = allIds;
		this.requestSets = requestSets;
	}
	
}
