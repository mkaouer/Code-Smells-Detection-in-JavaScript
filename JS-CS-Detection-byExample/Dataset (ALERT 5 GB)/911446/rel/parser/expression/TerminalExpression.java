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

/**
 * Abstract base class for all expressions requiring no arguments.
 */
abstract public class TerminalExpression extends AbstractExpression {

    /**
     * Create a new terminal expression.
     */
    public TerminalExpression() {
	// nothing to do
    }

    /**
     * Return the number of children required in a terminal expression.
     * This will always evaluate to <code>0</code>.
     *
     * @return	<code>0</code>
     */
    public int getChildCount() {
	return 0;
    }

    // A terminal expression cannot have a type mismatch
    public int checkType() throws TypeMismatchException {
        return getType();
    }
}
