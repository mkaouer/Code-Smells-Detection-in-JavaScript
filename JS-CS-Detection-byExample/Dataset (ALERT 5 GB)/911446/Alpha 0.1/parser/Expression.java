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
public abstract class Expression extends DefaultMutableTreeNode {

    /** A boolean type that can contain either <code>1</code> or 
	<code>0</code> */
    public static final int BOOLEAN_TYPE = 0;

    /** A floating point value type */
    public static final int VALUE_TYPE = 1;

    /** Represents stock volume value */
    public static final int VOLUME_TYPE = 2;

    /** Represents a stock quote <b>value</b>: open, close, low, high */
    public static final int PRICE_TYPE = 3;

    /** Represents a stock quote <b>type</b>: open, close, low, high */
    public static final int QUOTE_TYPE = 4;

    /** Threshold level where a number is registered as <code>TRUE</code> */
    public final static float TRUE_LEVEL = 0.0001F;

    /** <code>Value of TRUE</code> */
    public final static float TRUE = 1.0F;

    /** <code>Value of FALSE</code> */
    public final static float FALSE = 0.0F;
    
    /**
     * Create a new expression.
     */
    public Expression() {
	// nothing to do
    }

    /**
     * Compares whether the two types are equivelent. This performs
     * type overloading in the <i>Gondola</i> language. The following
     * types can be converted into each other without causing a type
     * mismatch:
     * <p>
     * <table>
     * <tr>
     * <tr><td><code>VALUE_TYPE</code></td><td>Can be converted into 
     *		<code>VOLUME_TYPE</code> or <code>PRICE_TYPE</code>.
     * <tr><td><code>VOLUME_TYPE</code></td><td>Can be converted into 
     *		<code>VALUE_TYPE</code>.
     * <tr><td><code>PRICE_TYPE</code></td><td>Can be converted into 
     *		<code>VALUE_TYPE</code>.
     * </tr>
     * </table>
     *
     * @param	type1	a type
     * @param	type2	another type
     * @return	if the two types are equivelant
     */
    protected boolean equivelantTypes(int type1, int type2) {

	// Types are equivelant iff:
	// A They are the same
	// B The left type is VALUE_TYPE, the right either volume or price
	// C The right type is VALUE_TYPE, the left either volume or price

	if((type1 == type2) || // A
	   (type1 == VALUE_TYPE && (type2 == VOLUME_TYPE ||
				    type2 == PRICE_TYPE)) || // B
	   (type2 == VALUE_TYPE && (type1 == VOLUME_TYPE ||
				    type1 == PRICE_TYPE))) // C
	    return true;
	else
	    return false;
    }

    /**
     * Evaluates the given expression and returns the result.
     *
     * @param	quoteBundle	the quote bundle containing quote data to use
     * @param	symbol	the current symbol
     * @param	day	current date in cache fast access format
     * @return	the result of the expression
     * @throws	EvaluationException if the expression tries to access
     *		dates outside of the cache
     */
    abstract public float evaluate(QuoteBundle quoteBundle, String symbol, int day)
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
     * Return the number of children (arguments) that this expression
     * needs.
     *
     * @return	the required number of arguments
     */
    abstract public int getNeededChildren();
}


