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

package nz.org.venice.ui;

/**
 * A version of the Comparable interface which allow objects to be sorted
 * differently in forward and backwards order.
 * This means that the ordering is not simply the reverse ordering
 * when the reverse flag is set. This is useful for orderings where
 * certain elements should always remain at the top or the bottom of the sort,
 * e.g. summation values. E.g. A, B, C, Total and C, B, A, Total.
 *
 * This comparable is supported in the {@link SortedTable}.
 *
 * @author Andrew Leppard
 */
public interface TwoWayComparable {

    /**
     * Compare this object with the specified object.
     *
     * @param object object to compare with
     * @param reverse order of sort, if set reverse the order.
     * @return	the value <code>0</code> if the objects are equal;
     * <code>1</code> if this object is after the specified object or
     * <code>-1</code> if this object is before the specified object
     */
    public int compareTo(Object object, boolean reverse);
}
