package org.da.ass3;

import org.da.ass3.messages.CandidateMessage;

public class MsgTuple implements Comparable<MsgTuple>{
	
	private int level;
	private long id;
	private long link;
	
	public MsgTuple(int level, long fromprocess){
		this.level = level;
		this.id = fromprocess;
		this.link = fromprocess;
	}
	
	public MsgTuple(CandidateMessage cm, long fromprocess){
		this.level = cm.getLevel();
		this.id = cm.getId();
		this.link = fromprocess;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLink() {
		return link;
	}

	public void setLink(long link) {
		this.link = link;
	}

	@Override
	public int compareTo(MsgTuple o) {
		int comp = Integer.compare(level, o.getLevel());
		if (comp == 0){
			return Long.compare(id, o.getId());
		} else {
			return comp;
		}
	}

}