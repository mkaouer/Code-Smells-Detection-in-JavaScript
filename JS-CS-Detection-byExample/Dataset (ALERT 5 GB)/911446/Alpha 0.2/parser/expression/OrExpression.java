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

package org.mov.parser.expression;

import org.mov.util.*;
import org.mov.parser.*;
import org.mov.quote.*;

/**
 * An expression which performs boolean <code>or</code> on two sub-expressions.
 */
public class OrExpression extends LogicExpression {

    public OrExpression(Expression left, Expression right) {
	super(left, right);
    }

    public float evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
	throws EvaluationException {

	if(getLeft().evaluate(variables, quoteBundle, symbol, day) >= TRUE_LEVEL ||
	   getRight().evaluate(variables, quoteBundle, symbol, day) >= TRUE_LEVEL)
	    return TRUE;
	else
	    return FALSE;
    }

    public Expression simplify() {
        // First simplify all the child arguments
        super.simplify();

        NumberExpression left = (getLeft() instanceof NumberExpression? 
                                 (NumberExpression)getLeft() : null);
        NumberExpression right = (getRight() instanceof NumberExpression? 
                                  (NumberExpression)getRight() : null);

        // If either child argument is the constant TRUE we can simplify to the
        // constant TRUE
        if((left != null && left.getValue() >= TRUE_LEVEL) ||
           (right != null && right.getValue() >= TRUE_LEVEL))
            return new NumberExpression(true);

        // If either child argument is the constant FALSE we can simplify to the 
        // other child arguement
        else if(left != null && left.getValue() < TRUE_LEVEL)
            return getRight();
        else if(right != null && right.getValue() < TRUE_LEVEL)
            return getLeft();

        // If both child arguments are the same we can simplify to the left
        // argument
        else if(getLeft().equals(getRight()))
            return getLeft();

        else
            return this;
    }

    public boolean equals(Object object) {

        // Are they both or expressions?
        if(object instanceof OrExpression) {
            OrExpression expression = (OrExpression)object;

            // (x or y) == (x or y)
            if(getLeft().equals(expression.getLeft()) &&
               getRight().equals(expression.getRight()))
                return true;

            // (x or y) == (y or x)
            if(getLeft().equals(expression.getRight()) &&
               getRight().equals(expression.getLeft()))
                return true;
        }
    
        return false;
    }

    public String toString() {
	return super.toString("or");
    }

    public Object clone() {
        return new OrExpression((Expression)getLeft().clone(), 
                                (Expression)getRight().clone());
    }
}

