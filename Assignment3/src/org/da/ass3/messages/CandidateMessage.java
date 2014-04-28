package org.da.ass3.messages;

public class CandidateMessage extends GenericMessage implements Comparable<GenericMessage>{

	private static final long serialVersionUID = 1L;

	private final int level;
	
	public CandidateMessage(int level){
		this.level = level;
	}
	
	public int getLevel(){
		return level;
	}
	
	/**
	 * Order on lexicographically
	 */
	@Override
    public int compareTo(GenericMessage other) {
		if (!(other instanceof CandidateMessage))
			return super.compareTo(other);
    	int comp = Long.compare(getLevel(), 
    			((CandidateMessage)other).getLevel());
		if(comp == 0){
			return Long.compare(getID().getBroadcaster(),
					other.getID().getBroadcaster());
		} else {
			return comp;
		}
	}
}
