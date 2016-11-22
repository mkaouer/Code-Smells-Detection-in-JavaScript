package com.crawljax.condition;

import com.crawljax.browser.EmbeddedBrowser;

import net.jcip.annotations.ThreadSafe;

import org.w3c.dom.NodeList;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Condition that counts how many times a condition is specified and returns true iff the specified
 * condition is satisfied less than the specified number.
 *
 * @author dannyroest@gmail.com (Danny Roest)
 * @version $Id: CountCondition.java 396 2010-07-27 09:16:28Z slenselink@google.com $
 */
@ThreadSafe
public class CountCondition implements Condition {

	private final Condition condition;
	private final AtomicInteger count = new AtomicInteger(0);
	private final AtomicInteger maxCount = new AtomicInteger(0);

	/**
	 * @param maxCount
	 *            number of times the condition can be satisfied.
	 * @param condition
	 *            the condition.
	 */
	public CountCondition(int maxCount, Condition condition) {
		this.maxCount.set(maxCount);
		this.condition = condition;
	}

	
	public boolean check(EmbeddedBrowser browser) {
		if (condition.check(browser)) {
			count.getAndIncrement();
		}
		return count.get() <= maxCount.get();
	}

	
	public NodeList getAffectedNodes() {
		return condition.getAffectedNodes();
	}

}
