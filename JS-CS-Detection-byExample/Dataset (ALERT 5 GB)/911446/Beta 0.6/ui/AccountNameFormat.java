/* Merchant of Venice - technical analysis software for the stock market.
   Copyright (C) 2004 Andrew Leppard (aleppard@picknowl.com.au)

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

package org.mov.ui;

import org.mov.util.Locale;

/**
 * This class describes how to display and sort the name of portfolio
 * accounts in the table. Account names are simply displayed as strings,
 * in the table, and are basically sorted as Strings. The only exception
 * is that the total entry always appears at the bottom.
 */
public class AccountNameFormat implements TwoWayComparable {
    String name;

    /** The account name format that represents the total row */
    public final static AccountNameFormat TOTAL =
        new AccountNameFormat(Locale.getString("TOTAL"));

    /**
     * Create a new account name format object.
     *
     * @param name the name of the account
     */
    public AccountNameFormat(String name) {
        this.name = name;
    }

    /**
     * Get the account name.
     *
     * @return	the name
     */
    public String getName() {
	return name;
    }

    /**
     * Compare two account names.
     *
     * @param	object	account name object to compare to
     * @return	the value <code>0</code> if the objects are equal;
     * <code>1</code> if this object is after the specified object or
     * <code>-1</code> if this object is before the specified object
     */
    public int compareTo(Object object, boolean reverse) {
        AccountNameFormat name = (AccountNameFormat)object;

        // The total row should always sort to the bottom
        if(this == TOTAL)
            return 1;
        else if(name == TOTAL)
            return -1;

        // Otherwise just compare the strings normally
        else if(!reverse)
            return getName().compareTo(name.getName());
        else
            return -(getName().compareTo(name.getName()));
    }

    /**
     * Return a string representation of the account name.
     *
     * @return account name
     */
    public String toString() {
        return getName();
    }
}
