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

package nz.org.venice.ui;

import nz.org.venice.util.Money;

/**
 * An abstract representation of the concept of Change. This class stores the
 * change of a value (in percent). It is currently used as a place holder
 * for the class <code>SortedTable</code> to allow us to differentiate it
 * from the other <code>double</code> values used in that object. This way
 * we can format it differently.
 */
public class ChangeFormat implements Comparable {
    double change;

    /**
     * Create a new Change object from the given change.
     *
     * @param	change	the change in percent
     */
    public ChangeFormat(double change) {
	this.change = change;
    }

    /**
     * Create a new change object and calculate the change
     * from the initial and final monetary values.
     *
     * @param initialValue the initial amount
     * @param finalValue the final amount
     */
    public ChangeFormat(Money initialValue, Money finalValue) {
        this(initialValue.doubleValue(), finalValue.doubleValue());
    }

    /**
     * Create a new change object and calculate the change
     * from the initial and final monetary values.
     *
     * @param initialValue the initial amount
     * @param finalValue the final amount
     */
    public ChangeFormat(double initialValue, double finalValue) {
	change = 0.0D;

        if(initialValue != 0.0D)
            change = ((finalValue - initialValue) / initialValue) * 100;
    }

    /**
     * Get the change percent.
     *
     * @return	the change in percent
     */
    public double getChange() {
	return change;
    }

    /**
     * Compare two change objects.
     *
     * @param	object	change object to compare to
     * @return	the value <code>0</code> if the change objects are equal;
     * <code>1</code> if this change object is after the specified change
     * object or
     * <code>-1</code> if this change object is before the specified change
     * object
     */
    public int compareTo(Object object) {

	ChangeFormat change = (ChangeFormat)object;

	if(getChange() < change.getChange())
	    return -1;
	if(getChange() > change.getChange())
	    return 1;

	return 0;
    }	
}
