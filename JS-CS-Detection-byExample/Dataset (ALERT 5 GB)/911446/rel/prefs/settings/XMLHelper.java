/* Merchant of Venice - technical analysis software for the stock market.
   Copyright (C) 2002 Andrew Leppard (aleppard@picknowl.com.au)

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package nz.org.venice.prefs.settings;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import nz.org.venice.quote.Symbol;

/**
   This class is for Java version 1.4 which doesn't have support for XPATH 
 */
public class XMLHelper {
    private XMLHelper() {

    }

    /**
     * 
     * Return a string value of the node defined by xpath.
     * 
     * @param   e      An element of the document
     * @param   xpath  A string representing a simplified xpath expression
     *                 It handles neither axes nor predicates.
     *                 If the last element in the path is not a leaf node,
     *                 it returns the first element in the list.
     *
     *        
     */

    public static String getValue(Element e, String xpath) {
	String rv;
		
	String tokens[] = xpath.split("/",2);

	String head = tokens[0];
	String tail = tokens.length > 1 ? tokens[1] : null;

	if (head != null) {
	    NodeList list = e.getElementsByTagName(head);
	    if (list.getLength() <= 0 ||
		list.getLength() > 1)  {
		return null;
	    }  else {
		if (tail == null) {
		    Node tmp = list.item(0).getFirstChild();		    
		    return tmp.getNodeValue();
		} else {
		    //To be strictly compatible (to XPATH 1.0 at least) 
		    //it should return a concatenation of all the list items
		    //but thats not really a useful behaviour for us here.
		    return getValue((Element)list.item(0), tail);
		}		
	    }
	} else {
	    return null;
	}
    }   


    /**
     *
     * Return a string value of the attribute of the element, if it exists.
     * Otherwise, an empty string is returned.
     * 
     * @param   e        A document element
     * @param   attrib   The attribute name of the element
     */
    public static String getAttribute(Element e, String attrib) {
	NamedNodeMap map = e.getAttributes();
	Node node = map.getNamedItem(attrib);	
	
	return (node != null) ? node.getNodeValue() : "";
    }


    /**
     *
     * Return a Vector of Nodes in a list.
     * 
     * @param listName      The tag name of the head of the list.
     * @param typeString    The attribute defining the type of the list.
     * @param entryName     The tag name of the entries of the list. 
     * 
     * Note that it is not a precondition of this method that e be the
     * head of the list.
     */

    public static Vector readList(Element e, String listName, 
				  String typeString, String entryName) {


	Vector outputList = new Vector();

	//This call means a direct read - e is the head of the list 
	//so we don't have to search the child elements for the head.	
	if (e.getTagName().equals(listName) &&
	    getAttribute(e, "type").equals(typeString)) {
	    outputList = processList(e, listName, entryName);
	} else {
	    //The head of the list must be later in the tree.
	    Vector list = getChildrenByTagName(e, listName);
	    
	    for (int i = 0; i < list.size(); i++) {	           	
		String test = getAttribute((Element)list.get(i), "type");

		if (getAttribute((Element)list.get(i), "type").equals(typeString)) {
		    
		    Vector tmp = processList((Element)list.get(i), listName, 
					     entryName);
		    if (tmp != null) {		
			outputList.addAll(tmp);
		    }
		}
	    }
	}
	
	return outputList;
    }
    
    /**
     *
     * Return a Vector of Nodes in a list.
     * 
     * @param listName      The tag name of the head of the list.
     * @param entryName     The tag name of the entries of the list. 
     * 
     * Note that it is not a precondition of this method that e be the
     * head of the list.
     */

    public static Vector readList(Element e, String listName, String entryName) {
	
	Vector outputList = new Vector();

	NodeList list = e.getElementsByTagName(listName);
	if (!singleton(list))  {
	    return outputList;
	}

	Element root = (Element)list.item(0);	
	NodeList entries = root.getElementsByTagName(entryName);
	
	for (int i = 0; i < entries.getLength(); i++) {
	    Node entry = entries.item(i);
	    outputList.add(entry);
	}
	
	return outputList;
    }
    
    //This used to be more than just a single line test. 
    public static boolean singleton(NodeList list) {
	return (list.getLength() == 1) ? true : false;
    }

    /**
     *
     * Return a List of Nodes which are direct descendents of the node n.
     * This method exists because the standard Java method getElementsByTagName
     * does a n/descendant::tagName rather than than n/tagName. 
     */

    public static Vector getChildrenByTagName(Node n, String tagName) {
	NodeList list = n.getChildNodes();
	Vector rv = new Vector();
	
	for (int i = 0; i < list.getLength(); i++) {
	    if (list.item(i).getNodeName().equals(tagName)) {
		rv.add(list.item(i));
	    } 
	}
	return rv;
    }

    /**
     * 
     * Return a list of nodes.
     * 
     * @param root       The root of the tree
     * @param listName   The tag name of the head of the list
     * @param entryName  The tag name of the list entries
     */

    private static Vector processList(Element root, String listName, String entryName) {
	Vector rv = new Vector();

	Vector entries = getChildrenByTagName(root,entryName);
	
	Iterator iterator = entries.iterator();
	
	while (iterator.hasNext()) {		
	    Node entry = (Node)iterator.next();
	    rv.add(entry);
	    
	}   
	return rv;
    }


    /**
     *
     * Convert a list whose elements are Node elements to String elements
     *
     * @param list A Vector
     * @return A Vector  
     */
    public static Vector nodeToStringList(Vector list) {
	Vector outputList = new Vector();
	Iterator iterator = list.iterator();

	while (iterator.hasNext()) {
	    Node entry = (Node)iterator.next();
	    Node textNode = entry.getFirstChild();
	    outputList.add(textNode.getNodeValue());
	}
	return outputList;
    }


    /**
     *
     * Convert a list whose elements consist of strings representing symbols
     * to elements representing Venice Symbols
     * 
     * @param list   A list of string elements
     * @return  A list of Symbol elements
     */

    public static Vector stringToSymbolsList(List list) {
	Vector outputList = new Vector();
	Iterator iterator = list.iterator();

	while (iterator.hasNext()) {
	    String name = (String)iterator.next();
	    try {
		Symbol s = Symbol.find(name);
		outputList.add(s);
	    } catch (nz.org.venice.quote.SymbolFormatException sfe) {
		
	    }
	}
	return outputList;
    }

}