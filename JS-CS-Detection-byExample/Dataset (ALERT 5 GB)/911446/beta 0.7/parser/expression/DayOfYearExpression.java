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

import org.mov.parser.TypeMismatchException;
import org.mov.parser.Variables;
import org.mov.quote.QuoteBundle;
import org.mov.quote.Symbol;
import org.mov.util.TradingDate;

/**
 * A function that returns the current day of year. The first day of
 * the year (Januarary 1st) will be 1.
 */
public class DayOfYearExpression extends TerminalExpression {

    public DayOfYearExpression() {
        // nothing to do
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) {
        TradingDate date = quoteBundle.offsetToDate(day);
        return date.getDayOfYear();
    }

    public String toString() {
        return "dayofyear()";
    }

    public int checkType() throws TypeMismatchException {
	return getType();
    }

    /**
     * Get the type of the expression.
     *
     * @return returns {@link #INTEGER_TYPE}.
     */
    public int getType() {
        return INTEGER_TYPE;
    }

    public Object clone() {
        return new DayOfYearExpression();
    }

}
