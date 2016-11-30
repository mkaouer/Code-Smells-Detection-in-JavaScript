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

package org.mov.parser;

import javax.swing.tree.*;

import org.mov.util.*;
import org.mov.quote.*;

/** 
 * Representation of a composite executable parse tree. Any expression
 * in the <i>Gondola</i> language is parsed into a composite structure
 * built upon this class. This class therefore represents an executable
 * expression. 
 * <p>
 * Any single object of this type could represent a <b>terminal expression</b>
 * that is a number such as "<code>5</code>",
 * a <b>unary expression</b> such as "<code>not(X)</code>",
 * a <b>binary expression</b> such as "<code>X and Y</code>" or a 
 * <b>ternary expression</b>. The arguments labelled above as <code>X</code>
 * and <code>Y</code> would be represented by separate <code>Expression</code>
 * classes.
 * Those classes would however be contained by this class.
 */
public abstract class Expression extends DefaultMutableTreeNode implements Cloneable {

    /** A boolean type that can be either {@link #TRUE} or {@link #FALSE}. */
    public static final int BOOLEAN_TYPE = 0;

    /** A float type that can contain any number. */
    public static final int FLOAT_TYPE = 1;

    /** An integer type that can contain any integer number. */
    public static final int INTEGER_TYPE = 2;

    /** Represents a stock float quote <b>type</b>: open, close, low, high */
    public static final int FLOAT_QUOTE_TYPE = 3;

    /** Represents a stock integer quote <b>type</b>: volume */
    public static final int INTEGER_QUOTE_TYPE = 4;

    /** Threshold level where a number is registered as <code>TRUE</code> */
    public final static float TRUE_LEVEL = 0.1F;

    /** Value of <code>TRUE</code> */
    public final static float TRUE = 1.0F;

    /** Value of <code>FALSE</code> */
    public final static float FALSE = 0.0F;
    
    /**
     * Create a new expression.
     */
    public Expression() {
	// nothing to do
    }

    /**
     * Evaluates the given expression and returns the result.
     *
     * @param   variables       variable storage area for expression
     * @param	quoteBundle	the quote bundle containing quote data to use
     * @param	symbol	the current symbol
     * @param	day	current date in cache fast access format
     * @return	the result of the expression
     * @throws	EvaluationException if the expression performs an illegal
     *          operation such as divide by zero.
     */
    abstract public float evaluate(Variables variables, QuoteBundle quoteBundle, 
                                   Symbol symbol, int day)
	throws EvaluationException;

    /**
     * Convert the given expression to a string.
     * 
     * @return	the string representation of the expression
     */
    abstract public String toString();

    /**
     * Perform type checking on the expression.
     *
     * @return	the return type of the expression
     * @throws	TypeMismatchException if the expression has incorrect types
     */
    abstract public int checkType() throws TypeMismatchException;

    /**
     * Get the type of the expression.
     *
     * @return one of {@link #BOOLEAN_TYPE}, {@link #FLOAT_TYPE},
     *         {@link #INTEGER_TYPE}, {@link #FLOAT_QUOTE_TYPE} or
     *         {@link #INTEGER_QUOTE_TYPE}.
     */
    abstract public int getType();

    /**
     * Return the number of arguments that this expressio needs.
     *
     * @return	the required number of arguments
     */
    abstract public int getNeededChildren();

    /**
     * Sub-classes must have a clone method.
     *
     * @return clone of this object
     */
    abstract public Object clone();

    /**
     * Return the given argument.
     *
     * @param	index	the argument index
     * @return	the argument
     */
    public Expression get(int index) {
        assert index < getNeededChildren();
        
        Expression expression = (Expression)getChildAt(index);
        assert expression != null;

	return expression;
    }

    /**
     * Set the argument.
     *
     * @param expression new argument expression
     * @param index index of the argument expression
     */
    public void set(Expression expression, int index) {
        assert index < getNeededChildren();

        Expression current = get(index);
        if(current != expression) {
            remove(index);
            insert(expression, index);            
        }
    }

    /**
     * Perform simplifications and optimisations on the expression tree.
     * For example, if the expression tree was <code>a and true</code> then the
     * expression tree would be simplified to <code>a</code>.
     */
    public Expression simplify() {
        assert getChildCount() == getNeededChildren();
        
        // Simplify child arguments
        if(getNeededChildren() > 0) {
            for(int i = 0; i < getNeededChildren(); i++) 
                set(get(i).simplify(), i);
        }

        return this;
    }

    /**
     * Return the index of the given argument in the expression. We override
     * this method because we use "==" to denote equality, not "equals" 
     * as the former would return the first argument with the same expression
     * not necessarily the actual expression instance desired.
     *
     * @param expression the child expression to locate
     * @return index of the child expression or <code>-1</code> if it could
     *              not be found
     */
    public int getIndex(Expression expression) {
        for(int index = 0; index < getNeededChildren(); index++) {
            if(get(index) == expression)
                return index;
        }
        
        // Not found
        return -1;
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
        
        // Check all children are the same - only check the children
        // we currently have - we might not have them all yet!
        for(int i = 0; i < getNeededChildren(); i++) {
            if(!get(i).equals(expression.get(i)))
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
        // If you implement equals you should implement hashCode().
        // Since I don't need it I haven't bothered to implement a very
        // good hash.
        return getClass().hashCode();
    }

    /** 
     * Count the number of nodes in the tree.
     *
     * @return number of nodes or 1 if this is a terminal node
     */
    public int size() {
        int count = 1;

        for(int i = 0; i < getChildCount(); i++) {
            Expression childExpression = get(i);

            count += childExpression.size();
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
               type == FLOAT_QUOTE_TYPE || type == INTEGER_QUOTE_TYPE);

        if(getType() == type)
            count = 1;

        for(int i = 0; i < getChildCount(); i++) {
            Expression childExpression = get(i);

            count += childExpression.size(type);
        }

        return count;
    }
}


