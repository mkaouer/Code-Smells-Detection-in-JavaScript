/**
 * Created Aug 7, 2008
 */
package com.crawljax.util;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * @author mesbah
 * @version $Id: HtmlNamespace.java 65 2010-01-13 14:16:52Z frankgroeneveld $
 */
public class HtmlNamespace implements NamespaceContext {
	/**
	 * @param prefix
	 *            The prefix of the URI.
	 * @return The namespace URI.
	 */
	public String getNamespaceURI(String prefix) {
		if (prefix == null) {
			throw new NullPointerException("Null prefix");
		} else if ("html".equals(prefix)) {
			return "http://www.w3.org/1999/xhtml";
		} else if ("xml".equals(prefix)) {
			return XMLConstants.XML_NS_URI;
		}

		return XMLConstants.DEFAULT_NS_PREFIX;
	}

	// This method isn't necessary for XPath processing.

	/**
	 * @param uri
	 *            TODO: DOCUMENT ME!
	 * @return TODO: DOCUMENT ME!
	 */
	public String getPrefix(String uri) {
		throw new UnsupportedOperationException();
	}

	// This method isn't necessary for XPath processing either.
	/**
	 * @param uri
	 *            TODO: DOCUMENT ME!
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator<?> getPrefixes(String uri) {
		throw new UnsupportedOperationException();
	}
}
