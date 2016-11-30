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

package nz.org.venice.parser.expression;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Locale;

import nz.org.venice.parser.Expression;
import nz.org.venice.parser.ExpressionFactory;
import nz.org.venice.parser.ParseMetadata;
import nz.org.venice.util.VeniceLog;

import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;

/**
 * The abstract base class for all expressions in the <i>Gondola</i> language. This
 * class implements the {@link Expression} interface and provides functions for
 * managing an expression's child nodes. E.g. the expression <code>4+5</code> would
 * consist of three nodes. The plus being the root node, which would have two
 * child nodes of <code>4</code> and <code>5</code>.
 *
 * @see Expression
 */
public abstract class AbstractExpression implements Expression {

    // Constants that manage the USA/Australian/UK localization
    // the goal is forcing that localization also for others languages
    private final static String format = "0.000000#";
    private final static DecimalFormat decimalFormat = new DecimalFormat(format, new DecimalFormatSymbols(Locale.ENGLISH));

    // Pointer to parent node (if any)
    private Expression parent;

    // Array of children.
    private final Expression children[];
    
    private static java.util.logging.Handler handler;
    private static java.util.logging.Logger logger;

    private ParseMetadata parseMetadata;

    private final String id;

    /**
     * Create a new expression with no children.
     *
     */                
    //Terminal Expression    
    public AbstractExpression() {
	children = null;
	this.parent = getParent();
	id = setId();
    }

    /**
     * Create a new expression with the given number of children.
     *
     * @param children An array of the children of the expression.
     */                
    public AbstractExpression(Expression[] children) {	
	for (int i = 0; i < children.length; i++) {
	    assert children[i] != null;
	    children[i].setParent(this);
	}
	this.children = children;
	this.parent = null;
	id = setId();
    }


    /**
     * Create a new expression with the given number of children.
     *
     * @param children A List of the children of the expression.
     */
    public AbstractExpression(List children) {	
	this.children = new Expression[children.size()];
	for (int i = 0; i < children.size(); i++) {
	    Expression child = (Expression)children.get(i);
	    child.setParent(this);
	    this.children[i] = child;
	}
	
	this.parent = null;
	id = setId();
    }
    
    /**
     * Get the parent of this node.
     */
    public Expression getParent() {
        return parent;
    }

    /**
     * Set the parent of this node.
     *
     * @param parent the new parent.
     */
    public void setParent(Expression parent) {
        assert parent != this;
	assert parent != null;

        this.parent = parent;

    }
    
    /**
     * Return the child of this node at the given index.
     *
     * @return child at given index.
     */
    public Expression getChild(int index) {	
        assert index <= getChildCount();
	
        return children[index];
    }

    /**
     * Return whether this node is the root node. The root node has no
     * parent.
     * 
     * @return <code>TRUE</code> iff this node is the root node.
     */
    public boolean isRoot() {
        return getParent() == null;
    }

    public Expression setChild(Expression child, int index) {
	assert index <= getChildCount();
	assert child != this;

	return ExpressionFactory.setChild(this, child, index);
    }

    /**
     * Set this expression's child to the given child. The new child
     * will be removed from its parent.
     *
     * @param child the new child.
     * @param index the index of the new child.
     */
    public void setChildMutableVersion(Expression child, int index) {
        assert index <= getChildCount();
        assert child != this;

	Expression oldParent = null;

	VeniceLog.getInstance().log("setChild: Index = " + index + " child = " + child);

        // Remove reference to new child from its old parent
	
	//But the old parent can be this expression!
	//What does that mean?
        if (child != null && child.getParent() != null) {	    
            oldParent = child.getParent();
	    	    
	    if (oldParent != this) {
		//Calling the oldet child for oldparent causes null to end up in tree
		//Which is ok as long as it's replaceimmediately.
		//However, there's a bug somewhere which means this isn't 
		//happening.
		//oldParent.setChildMutableVersion(null, oldParent.getIndex(child));	    
		oldParent.setChild(null, oldParent.getIndex(child));	    
	    }
        }

        // Remove parent reference to this class from old child
        if (getChild(index) != null)
            getChild(index).setParent(null);

        // Set parent reference to this class in new child
        if (child != null)
            child.setParent(this);

        // Set reference to new child in this class
	
	//When oldParent = this, this method is run recursively, so
	//children[index] is null.
	children[index] = child;
	
	VeniceLog.getInstance().log("SetChild Exit");
    }
    

    /**
     * Return true if none of the children of this expression are null.
     * This means the expression can be simplified safely.
     *
     * @return true if all children are not null
     */
    public boolean validTree() {
	boolean rv = true;
	for (int i = 0; i < getChildCount(); i++) {
	    if (getChild(i) == null) {
		return false;
	    } else {
		rv = getChild(i).validTree();
		if (rv == false) {
		    return false;
		}
	    }
	}
	return rv;
    }

    /**
     * Perform simplifications and optimisations on the expression tree.
     * For example, if the expression tree was <code>a and true</code> then the
     * expression tree would be simplified to <code>a</code>.
     */
    
    //This version replaces the expression with new children
    //instead of setting child to null/replacing and avoiding the problem 
    //of nulls in the expression tree.  
    public Expression simplify() {

	Expression[] newChildren = new Expression[getChildCount()];
	
	for (int i = 0; i < getChildCount(); i++) {
	    Expression newChild = children[i].simplify();
	    if (newChild == null) {
		assert false;
	    }
	    newChildren[i] = newChild;
	}		

	Expression rv =  ExpressionFactory.newExpression(this, newChildren);
       
	return rv;
    }

    /**
     * Perform simplifications and optimisations on the expression tree.
     * For example, if the expression tree was <code>a and true</code> then the
     * expression tree would be simplified to <code>a</code>.
     */
    //Deprecated
    public Expression simplifyMutableVersion() {
	assert validTree() == true;

	VeniceLog.getInstance().log("Simplify: Type = " + getClass().getName() + " val = " + toString());	    

        // Simplify child arguments
        if(getChildCount() > 0) {
            for(int i = 0; i < getChildCount(); i++)  {		
		Expression childi = getChild(i);
		Expression simplified = childi.simplify();
		setChildMutableVersion(simplified, i);    
	    }	
        }
	
	VeniceLog.getInstance().log("Exit Simplify");	    
        return this;
    }

    /**
     * Return the index of the given argument in the expression. This
     * method uses reference equality rather than using the equals
     * method.
     *
     * @param child the child expression to locate
     * @return index of the child expression or <code>-1</code> if it could
     *              not be found
     */
    public int getIndex(Expression child) {
        for(int index = 0; index < getChildCount(); index++) {
            if(getChild(index) == child)
                return index;
        }
        
        // Not found
        return -1;
    }

    /**
     * Parses doubleText from a string to produce a double.
     *
     * @param doubleText the string to be parsed
     * @return the parsed value
     */
    public static double parseDouble(String doubleText) throws NumberFormatException {
        double retValue = 0;
        try {
            Number num = decimalFormat.parse(doubleText);
            if (num == null)
                throw new ParseException("AbstractExpression - parseDouble - null Error", 0);
            retValue = decimalFormat.parse(doubleText).doubleValue();
        } catch (ParseException e) {
            throw new NumberFormatException();
        }
        return retValue;
    }

    /**
     * Parses intText from a string to produce an integer.
     *
     * @param intText the string to be parsed
     * @return the parsed value
     */
    public static int parseInt(String intText) throws NumberFormatException {
        int retValue = 0;
        try {
            Number num = decimalFormat.parse(intText);
            if (num == null)
                throw new ParseException("AbstractExpression - parseInt - null Error", 0);
            retValue = decimalFormat.parse(intText).intValue();
        } catch (ParseException e) {
            throw new NumberFormatException();
        }
        return retValue;
    }

    /**
     * Parses doubleText from a string to produce a Double Object.
     *
     * @param doubleText the string to be parsed
     * @return the parsed value as Double Object
     */
    public static Double valueOfDouble(String doubleText) throws NumberFormatException {
        Double retValue = null;
        try {
            Number num = decimalFormat.parse(doubleText);
            if (num == null)
                throw new ParseException("AbstractExpression - valueOfDouble - null Error", 0);
            retValue = new Double(decimalFormat.parse(doubleText).doubleValue());
        } catch (ParseException e) {
            throw new NumberFormatException();
        }
        return retValue;
    }

    /**
     * Parses intText from a string to produce an Integer Object.
     *
     * @param intText the string to be parsed
     * @return the parsed value as Integer Object
     */
    public static Integer valueOfInt(String intText) throws NumberFormatException {
        Integer retValue = null;
        try {
            Number num = decimalFormat.parse(intText);
            if (num == null)
                throw new ParseException("AbstractExpression - valueOfInt - null Error", 0);
            retValue = new Integer(decimalFormat.parse(intText).intValue());
        } catch (ParseException e) {
            throw new NumberFormatException();
        }
        return retValue;
    }

    public static NumberFormat getNumberFormat() {

        // Synchronisation cannot cause issues here. So this code
        // isn't synchronised.
        
        NumberFormat format = null;

        format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setGroupingUsed(false);
        format.setMinimumIntegerDigits(1);
        format.setMinimumFractionDigits(1);
        format.setMaximumFractionDigits(6);
        
        return format;
        
    }



    /**
     * Returns whether this expression tree and the given expression tree
     * are equivalent.
     *
     * @param object the other expression
     */    
    public boolean equals(Object object) {

        // Top level nodes the sames?
        if(!(object instanceof Expression))
            return false;
        Expression expression = (Expression)object;
        
        if(getClass() != expression.getClass())
            return false;
        
	if (getChildCount() != expression.getChildCount()) 
	    return false;

        // Check all children are the same - only check the children
        // we currently have - we might not have them all yet!

	//Not sure if the "we might not have them all yet
	//-as of 7.3, children are immutable and not allowed to be null 
        for(int i = 0; i < getChildCount(); i++) {
            if(!getChild(i).equals(expression.getChild(i)))
                return false;
        }

        return true;
    }
    

    /**
     * If you override the {@link #equals} method then you should override
     * this method. It provides a very basic hash code function.
     *
     * @return a poor hash code of the tree
     */
    
    public int hashCode() {
	/*
	  Equals now implemented and expressions being stored in HashMaps.
        // If you implement equals you should implement hashCode().
        // Since I don't need it I haven't bothered to implement a very
        // good hash.
        return super.hashCode();
	*/
	
	//For Terminal Expressions, hash value will use class hashcode 
	//unless they implement hashcode (which implies they implement equals)

	//+i here adds an order so expression(child1, child2) has a different
	//hash to expression(child2, child1)
	int hc = getClass().hashCode();
	for (int i = 0; i < getChildCount(); i++) {
	    hc ^= (getChild(i).hashCode() + i);
	}
	    	
	return hc;
    }
    

    /** 
     * Count the number of nodes in the tree.
     *
     * @return number of nodes or 1 if this is a terminal node
     */
    public int size() {
        int count = 1;

        for(int i = 0; i < getChildCount(); i++) {
            Expression child = getChild(i);

            count += child.size();
        }

        return count;
    }

    /**
     * Count the number of nodes in the tree with the given type.
     *
     * @return number of nodes in the tree with the given type.
     */
    public int size(int type) {
        int count = 0;

        assert(type == BOOLEAN_TYPE || type == FLOAT_TYPE || type == INTEGER_TYPE ||
               type == FLOAT_QUOTE_TYPE || type == INTEGER_QUOTE_TYPE || type == STRING_TYPE);

        if(getType() == type)
            count = 1;

        for(int i = 0; i < getChildCount(); i++) {
            Expression child = getChild(i);

            count += child.size(type);
        }

        return count;
    }

    /**
     * Return an iterator over the node's children.
     *
     * @return iterator.
     */
    public Iterator iterator() {
        List list = new ArrayList();
        buildIterationList(list, this);
        return list.iterator();
    }

    private void buildIterationList(List list, Expression expression) {
        list.add(expression);
        
        for(int i = 0; i < expression.getChildCount(); i++)
            buildIterationList(list, expression.getChild(i));
    }

    public String toString() {
	String rv = "";
	for (int i = 0; i < getChildCount(); i++) {
	    if (getChild(i) != null) {
		rv += getChild(i).toString();
	    }
	}
	return rv;
    }

    abstract public Object clone();

    public String printParents() {
	String rv = "";
	Expression parent = getParent();
	while (parent != null) {
	    //nz.org.venice.parser.expression = 32 chars
	    rv += parent.getClass().getName().substring(32) + ".";
	    parent = parent.getParent();
	}
	return rv;
    }

    public void setParseMetadata(HashMap parseTree, HashMap tokenLineMap) {
	parseMetadata = new ParseMetadata(parseTree, tokenLineMap);
    }

    public ParseMetadata getParseMetadata() {	
	if (parent != null) {
	    return parent.getParseMetadata();
	} else {
	    return parseMetadata;
	}
    }
    
    private String setId() {
	return UUIDGenerator.getInstance().generateRandomBasedUUID().toString();
    }

    public String getId() {
	return id;
    }
}
