package org.da.ass3.messages;

public class CandidateMessage
		extends GenericMessage
		implements Comparable<GenericMessage>
{

	private static final long serialVersionUID = 1L;

	private final int level;
	private final long id;


	public CandidateMessage(int level, long id)
	{
		this.level = level;
		this.id = id;
	}


	public int getLevel()
	{
		return level;
	}


	public long getId()
	{
		return id;
	}


	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + ": Level " + level + ", Id " + id;
	}

}
