/**
 * 
 */
package com.crawljax.core;

import net.jcip.annotations.GuardedBy;

import java.util.Collection;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This class implements a BlockingQueue with Runnable as its Generic type and extends Stack with
 * also Runnable as generic type. This class is used in the ThreadPoolExecutor and its used to store
 * separate threads in a Queue like fashion (FILO).
 *
 * @author Stefan Lenselink <S.R.Lenselink@student.tudelft.nl>
 * @version $Id: CrawlQueue.java 389 2010-07-16 14:44:24Z slenselink@google.com $
 */
public class CrawlQueue extends Stack<Runnable> implements BlockingQueue<Runnable> {

	/**
	 * Auto generated serialVersionUID.
	 */
	private static final long serialVersionUID = 4656244727801517204L;

	
	public int drainTo(Collection<? super Runnable> c) {
		return drainTo(c, Integer.MAX_VALUE);
	}

	
	public synchronized int drainTo(Collection<? super Runnable> c, int maxRunnablelements) {
		int counter = 0;
		for (Runnable object : this) {
			counter++;
			if (counter < maxRunnablelements) {
				c.add(object);
			} else {
				break;
			}
		}
		for (Object object : c) {
			this.remove(object);
		}
		return counter;
	}

	
	public boolean offer(Runnable e) {
		return this.add(e);
	}

	
	public boolean offer(Runnable e, long timeout, TimeUnit unit) {
		return this.add(e);
	}

	
	public Runnable poll(long timeout, TimeUnit unit) {
		return remove();
	}

	
	public void put(Runnable e) {
		this.add(e);

	}

	
	public int remainingCapacity() {
		return Integer.MAX_VALUE;
	}

	
	public Runnable take() {
		return remove();
	}

	
	@GuardedBy("this")
	public synchronized Runnable element() {
		return this.get(this.size() - 1);
	}

	
	public Runnable poll() {
		return remove();
	}

	
	@GuardedBy("this")
	public synchronized Runnable remove() {
		if (this.size() <= 0) {
			return null;
		}
		return this.remove(this.size() - 1);
	}

}
