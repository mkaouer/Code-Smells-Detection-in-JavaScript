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

import java.util.ArrayList;
import java.util.List;

import nz.org.venice.parser.Expression;
import nz.org.venice.parser.EvaluationException;
import nz.org.venice.parser.TypeMismatchException;
import nz.org.venice.parser.Variables;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.Symbol;

/**
 * A clause is an ordered list of sub-expressions. Each sub-expression is
 * executed serially. The value and type of the clause is the value and type of
 * the last sub-expression in the clause.

 * @author Mark Hummel
 */

public class IncludeExpression extends ClauseExpression {
    // Number of sub-expressions in the clause
    private int childCount;

    private boolean included = false;

    /**
     * Create a new clause expression from the given list of expressions.
     *
     * @param children
     *            list of expressions.
     */
    public IncludeExpression(List children) {
        super(children);

    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle,
                           Symbol symbol, int day) throws EvaluationException {

        // Execute all the sub-expressions in this clause and return the value
        // of
        // the last sub-expression.
        double value = 0.0D;

	//May only want to evaluate this once. 
	//(Per run that is. Expressions aren't reconstructed every iteration)
	included = true;

        for (int child = 0; child < getChildCount(); child++)
            value = getChild(child).evaluate(variables, quoteBundle, symbol, day);

        return value;
    }

}