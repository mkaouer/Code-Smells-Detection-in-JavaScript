/**
 * 
 */
package com.crawljax.condition;

import com.crawljax.browser.EmbeddedBrowser;

import net.jcip.annotations.Immutable;

/**
 * Logic operations for conditions.
 *
 * @author dannyroest@gmail.com (Danny Roest)
 * @version $Id: Logic.java 396 2010-07-27 09:16:28Z slenselink@google.com $
 */
@Immutable
public final class Logic {

	private Logic() {
		// Utility
	}

	/**
	 * @param condition
	 *            the condition.
	 * @return the condition negated.
	 */
	public static Condition not(final Condition condition) {
		return new AbstractCondition() {

			
			public boolean check(EmbeddedBrowser browser) {
				return !condition.check(browser);
			}

		};
	}

	/**
	 * @param conditions
	 *            the conditions.
	 * @return AND of conditions
	 */
	public static Condition and(final Condition... conditions) {
		return new AbstractCondition() {

			
			public boolean check(EmbeddedBrowser browser) {
				for (Condition condition : conditions) {
					if (!condition.check(browser)) {
						return false;
					}
				}
				return true;
			}
		};
	}

	/**
	 * @param conditions
	 *            the conditions.
	 * @return OR conditions
	 */
	public static Condition or(final Condition... conditions) {
		return new AbstractCondition() {

			
			public boolean check(EmbeddedBrowser browser) {
				for (Condition condition : conditions) {
					if (condition.check(browser)) {
						return true;
					}
				}
				return false;
			}
		};
	}

	/**
	 * @param conditions
	 *            the conditions.
	 * @return NAND conditions
	 */
	public static Condition nand(final Condition... conditions) {
		return new AbstractCondition() {

			
			public boolean check(EmbeddedBrowser browser) {
				return not(and(conditions)).check(browser);
			}

		};
	}

}
