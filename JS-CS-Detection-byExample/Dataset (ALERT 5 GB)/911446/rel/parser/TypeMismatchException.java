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

package nz.org.venice.parser;

import nz.org.venice.util.Locale;

/**
 * An exception which is thrown when there is a type mismatch error
 * when executing an expression. A type mismatch error is when
 * an incorrect type was supplied. For example if the expression required
 * a <code>BOOLEAN_TYPE</code> and a <code>VALUE_TYPE</code> was given
 * instead.
 * @see Expression
 */
public class TypeMismatchException extends ExpressionException {

    /**
     * Create a new type mismatch exception.
     */    
    public TypeMismatchException() {
	super(Locale.getString("TYPE_MISMATCH_ERROR"));
    }
    
    /**
     * Create a new type mismatch exception where the message text explains
     * what caused the type mismatch.
     * 
     * @param expression    The expression object which throws the exception
     * @param type          A string list where the items are the types of the
     *                      expression.
     * @param expectedTypes A string list where the items are the types the
     *                      expected by the expression. 
     */
    public TypeMismatchException(Expression expression, String type, String expectedTypes) {
	super(Locale.getString("TYPE_MISMATCH_ERROR") + " on expression object: " + expression.getClass().getName() + " expression = " + expression.toString() + " expected types = " + expectedTypes  + " got: " + type);
    }

    /**
     * Create a new type mismatch exception where the message text explains
     * what caused the type mismatch.
     * 
     * @param expression    The expression object which throws the exception
     * @param type          The type of the expression.
     *                      
     * @param expectedType  The type expected by the expression.
     *                      
     */
    public TypeMismatchException(Expression expression, int type, int expectedType) {
	super(Locale.getString("TYPE_MISMATCH_ERROR") + " on expression object: " + expression.getClass().getName() + " expression = " + expression.toString() + " Expected Type: " + expectedType  + " got: " + type);
    }

}
