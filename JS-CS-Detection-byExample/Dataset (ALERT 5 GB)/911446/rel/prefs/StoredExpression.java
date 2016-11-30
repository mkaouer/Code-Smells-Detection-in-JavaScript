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

package nz.org.venice.prefs;

/**
 * A representation of an expression that can be referenced by name. A stored expression
 * is saved in the Preferences data so that the user does not have to re-type the
 * expression.
 */
public class StoredExpression {
    /** Name of the stored expression. */
    public String name;

    /** The stored expression. */
    public String expression;

    /**
     * Create a new stored expression.
     *
     * @param name the name of the expression.
     * @param expression the expression to store.
     */
    public StoredExpression(String name, String expression) {
	this.name = name;
	this.expression = expression;
    }
}
