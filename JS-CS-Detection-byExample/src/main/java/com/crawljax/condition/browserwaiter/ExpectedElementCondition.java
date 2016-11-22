package com.crawljax.condition.browserwaiter;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.core.state.Identification;

import net.jcip.annotations.ThreadSafe;

/**
 * Checks whether an elements exists.
 * 
 * @author dannyroest@gmail.com (Danny Roest)
 * @version $Id: ExpectedElementCondition.java 446 2010-09-16 09:17:24Z slenselink@google.com $
 */
@ThreadSafe
public class ExpectedElementCondition implements ExpectedCondition {

	private final Identification identification;

	/**
	 * Constructor.
	 * 
	 * @param identification
	 *            the identification to use.
	 */
	public ExpectedElementCondition(Identification identification) {
		this.identification = identification;
	}

	
	public boolean isSatisfied(EmbeddedBrowser browser) {
		return browser.elementExists(identification);
	}

	
	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.identification;
	}

}
