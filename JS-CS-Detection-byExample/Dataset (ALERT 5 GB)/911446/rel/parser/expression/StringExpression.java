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

import nz.org.venice.parser.Variables;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.Symbol;

/**
 * A representation of a value.
 */
public class StringExpression extends TerminalExpression {

    // The string's text
    private String text;

    /** Create a new string expression with the given string text.
     *
     * @param text the initial text of the string.
     */
    public StringExpression(String text) {
        this.text = text;
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day) {
        // How can I get this to return a string without having to make the expression
        // keep creating temporary objects?
        return 0.0D;
    }

    public String toString() {
        return "\"" + text + "\"";
    }

    public boolean equals(Object object) {
        if(object instanceof StringExpression) {
            StringExpression expression = (StringExpression)object;

            if(expression.getText() == getText())
                return true;
        }

        return false;
    }

    public int hashCode() {
	return getText().hashCode();
    }

    /**
     * Get the text of the string.
     *
     * @return string text
     */
    public String getText() {
        return text;
    }

    /**
     * Get the type of the expression.
     *
     * @return {@link #STRING_TYPE}
     */
    public int getType() {
        return STRING_TYPE;
    }

    public Object clone() {
        return new StringExpression(text);
    }
}
