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

import java.text.NumberFormat;

import org.mov.parser.Expression;
import org.mov.parser.Variables;
import org.mov.quote.QuoteBundle;
import org.mov.quote.Symbol;

/**
 * A representation of a value.
 */

public class NumberExpression extends TerminalExpression {

    // The number's value and typey
    private double value;
    private int type;

    private final static double EPSILON = 0.001F;

    private static NumberFormat format = AbstractExpression.getNumberFormat();

    public NumberExpression(boolean value) {
        this.value = value? TRUE: FALSE;
        this.type = BOOLEAN_TYPE;
    }

    public NumberExpression(double value) {
        this.value = value;
        this.type = FLOAT_TYPE;
    }

    public NumberExpression(int value) {
        this.value = (double)value;
        this.type = INTEGER_TYPE;
    }

    public NumberExpression(double value, int type) {
        assert(type == Expression.BOOLEAN_TYPE || type == Expression.FLOAT_TYPE ||
               type == Expression.INTEGER_TYPE);

	this.value = value;
        this.type = type;
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) {
	return value;
    }

    public static String toString(int type, double value) {
        switch(type) {
        case BOOLEAN_TYPE:
            if(value >= TRUE_LEVEL)
                return "true";
            else
                return "false";

        case FLOAT_TYPE:
            return format.format(value);

        default:
            assert type == INTEGER_TYPE;
            return Integer.toString((int)value);
        }
    }

    public String toString() {
        return toString(getType(), value);
    }

    public boolean equals(double value) {
        return (Math.abs(this.value - value) < EPSILON);
    }

    public boolean equals(Object object) {
        if(object instanceof NumberExpression) {
            NumberExpression expression = (NumberExpression)object;

            if(expression.getValue() == getValue() &&
               expression.getType() == getType())
                return true;
        }

        return false;
    }

    /**
     * Returns whether the given expression is a NumberExpression
     * set to the given constant. This is a convenience method often
     * called when printing functions to determine whether the print
     * should print only an abbreviated form, e.g.
     *
     * rsi(45, 0) is printed as rsi().
     *
     * @param expression the expression to query.
     * @param value the value to check
     * @return <code>true<code> if the expression is a <code>NumberExpression</code>
     *         with the given value.
     */
    public static boolean isConstant(Expression expression, int value) {
        if(expression instanceof NumberExpression) {
            NumberExpression numberExpression = (NumberExpression)expression;

            if((int)numberExpression.getValue() == value)
                return true;
        }

        return false;
    }

    /**
     * Get the value of the number.
     *
     * @return value
     */
    public double getValue() {
        return value;
    }

    /**
     * Set the value of the number.
     *
     * @param value the new value
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Get the type of the expression.
     *
     * @return one of {@link #BOOLEAN_TYPE}, {@link #FLOAT_TYPE} or {@link #INTEGER_TYPE}.
     */
    public int getType() {
        return type;
    }

    public Object clone() {
        return new NumberExpression(value, type);
    }
}

