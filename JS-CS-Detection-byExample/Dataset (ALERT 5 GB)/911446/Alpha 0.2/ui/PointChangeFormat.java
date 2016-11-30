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

package org.mov.ui;

import java.lang.Comparable;
import java.text.NumberFormat;

public class PointChangeFormat implements Comparable {
    float change;

    public PointChangeFormat(float change) {
	this.change = change;
    }

    public PointChangeFormat(float initialValue, float finalValue) {
        change = (finalValue - initialValue) * 100;
    }

    public float getPointChange() {
	return change;
    }

    public String toString() {
        NumberFormat format = NumberFormat.getInstance();
        format.setMinimumIntegerDigits(1);
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(1);

        return format.format(getPointChange());
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

	PointChangeFormat change = (PointChangeFormat)object;

	if(getPointChange() < change.getPointChange())
	    return -1;
	if(getPointChange() > change.getPointChange())
	    return 1;

	return 0;
    }	
}
