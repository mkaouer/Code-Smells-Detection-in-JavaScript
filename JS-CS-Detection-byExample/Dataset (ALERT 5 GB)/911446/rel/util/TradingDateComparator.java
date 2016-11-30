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

package nz.org.venice.util;

import java.util.*;

/**
 * A comparator for comparing <code>TradingDate</code> objects.
 */
public class TradingDateComparator implements Comparator {

    /**
     * Sort forwards 
     */
    public static int FORWARDS = 0;

    /**
     * Sort backwards
     */
    public static int BACKWARDS = 1;

    // The direction of the sort
    private int direction;

    /**
     * Create a new <code>TradingDateComparator</code> which sorts in the
     * given direction.
     */
    public TradingDateComparator(int direction) {
	this.direction = direction;
    }

    /**
     * Compare the specified objects.
     *
     * @param	o1	the first object
     * @param	o2	the second object
     * @return	<code>0</code>if the objects are equal; <code>1</code> if
     * <code>o1</code> is greater than <code>o2</code> or <code>-1</code>
     * otherwise. If the search order is backwards this will be reversed.
     */
    public int compare(Object o1, Object o2) {
	TradingDate d1 = (TradingDate)o1;
	TradingDate d2 = (TradingDate)o2;

	if(direction == BACKWARDS)
	    return d2.compareTo(d1);
	else
	    return d1.compareTo(d2);
    }

    /** 
     * Test the specified objects for equality.
     *
     * @param	o1	the first object
     * @param	o2	the second object
     * @return	<code>1</code> if the objects have the same date; 
     * <code>0</code> otherwise.
     */
    public boolean equals(Object o1, Object o2) {
	TradingDate d1 = (TradingDate)o1;
	TradingDate d2 = (TradingDate)o2;
	
	return(d2.equals(d1));
    }
}




