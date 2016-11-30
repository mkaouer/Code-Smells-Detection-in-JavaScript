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

import java.lang.String;

import org.mov.parser.EvaluationException;
import org.mov.parser.Variable;
import org.mov.parser.Variables;
import org.mov.quote.QuoteBundle;
import org.mov.quote.Symbol;
import org.mov.util.Locale;

/**
 * A representation of an expression to return the value of a variable.
 */
public class GetVariableExpression extends TerminalExpression {
    
    // The variable's name and type
    private String name;
    private int type;
    
    public GetVariableExpression(String name, int type) {
        assert name != null && name.length() > 0;

        this.name = name;
        this.type = type;
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) 
        throws EvaluationException {

        Variable variable = variables.get(name);

        if(variable != null) {
            assert variable.getType() == type;
            return variables.getValue(name);
        }
        else
            throw new EvaluationException(Locale.getString("VARIABLE_NOT_DEFINED_ERROR", name));
    }

    public String toString() {
	return name;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public boolean equals(Object object) {
        if(object instanceof GetVariableExpression) {
            GetVariableExpression expression = (GetVariableExpression)object;

            if(expression.getName().equals(getName()) &&
               expression.getType() == getType())
                return true;
        }

        return false;
    }

    public Object clone() {
        return new GetVariableExpression(name, type);
    }
}
