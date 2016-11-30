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
 * A representation of a value. 
 */
public class NumberExpression extends TerminalExpression {

    // The number's value and typey
    private float value;
    private int type;

    private final static float EPSILON = 0.001F;

    public NumberExpression(boolean value) {
        this.value = value? TRUE: FALSE;
        this.type = BOOLEAN_TYPE;
    }

    public NumberExpression(float value) {
        this.value = value;
        this.type = FLOAT_TYPE;
    }

    public NumberExpression(int value) {
        this.value = (float)value;
        this.type = INTEGER_TYPE;
    }

    public NumberExpression(float value, int type) {
        assert(type == Expression.BOOLEAN_TYPE || type == Expression.FLOAT_TYPE ||
               type == Expression.INTEGER_TYPE);

	this.value = value;
        this.type = type;
    }

    public float evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) {
	return value;
    }

    public String toString() {
        if(getType() == BOOLEAN_TYPE) {
            if(value >= TRUE_LEVEL)
                return "true";
            else
                return "false";
        }
        else if (getType() == FLOAT_TYPE) {
            return Float.toString(value);
        }
        else {
            assert getType() == INTEGER_TYPE;

            int valueInt = (int)value;
            return Integer.toString(valueInt);
        }
    }

    public int checkType() throws TypeMismatchException {
	return getType();
    }

    public boolean equals(float value) {
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
     * Get the value of the number.
     *
     * @return value
     */
    public float getValue() {
        return value;
    }

    /**
     * Set the value of the number.
     *
     * @param value the new value
     */
    public void setValue(float value) {
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
