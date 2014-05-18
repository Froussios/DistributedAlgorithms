package org.da.ass3.util;


/**
 * A generator for unique numbers.
 * 
 * @author Chris
 *
 */
public class UniqueGenerator
{
	public static final int defaultStart = 1;
	
	private int next = defaultStart;
	
	
	/**
	 * 
	 */
	public UniqueGenerator() {
	}
	
	
	/**
	 * 
	 * @param first The smallest number that will be returned.
	 */
	public UniqueGenerator(int first) {
		this.next = first;
	}
	
	
	/**
	 * Get a unique number.
	 * @return
	 */
	public synchronized int next() {
		return this.next++;
	}

}
