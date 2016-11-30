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

import nz.org.venice.parser.TypeMismatchException;
import nz.org.venice.parser.Variables;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.Symbol;
import nz.org.venice.util.TradingDate;

/**
 * A function that returns the current day of week. The first day
 * of the week depends on the locale. In the U.S it starts on Sunday,
 * in France it starts on Monday. The first day of the week will be 1.
 */
public class DayOfWeekExpression extends TerminalExpression {

    public DayOfWeekExpression() {
        // nothing to do
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) {
        TradingDate date = quoteBundle.offsetToDate(day);
        return date.getDayOfWeek();
    }

    public String toString() {
        return "dayofweek()";
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
        return new DayOfWeekExpression();
    }

}
