package com.crawljax.core.configuration;

/**
 * This class accepts all frames.
 * 
 * @author Stefan Lenselink <slenselink@google.com>
 * @version $Id: AcceptAllFramesChecker.java 393 2010-07-22 14:07:41Z slenselink@google.com $
 */
public class AcceptAllFramesChecker implements IgnoreFrameChecker {
	
	public boolean isFrameIgnored(String frameId) {
		return false;
	}
}
