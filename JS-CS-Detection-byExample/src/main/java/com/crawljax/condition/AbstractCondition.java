/**
 * 
 */
package com.crawljax.condition;

import net.jcip.annotations.ThreadSafe;

import org.w3c.dom.NodeList;

/**
 * Abstract class for Condition. This class and derivatives are Thread safe. The setAfftectedNodes
 * and getAffectedNodes are garded by using the ThreadLocal system.
 * 
 * @author dannyroest@gmail.com (Danny Roest)
 * @version $Id: AbstractCondition.java 369 2010-05-17 12:01:50Z lenselinkstefan@gmail.com $
 */
@ThreadSafe
public abstract class AbstractCondition implements Condition {

	private final ThreadLocal<NodeList> affectedNodes = new ThreadLocal<NodeList>();

	/**
	 * @return the affectedNodes. can be null
	 */
	public NodeList getAffectedNodes() {
		return affectedNodes.get();
	}

	/**
	 * @param affectedNodes
	 *            the affectedNodes to set
	 */
	protected void setAffectedNodes(NodeList affectedNodes) {
		this.affectedNodes.set(affectedNodes);
	}

}
