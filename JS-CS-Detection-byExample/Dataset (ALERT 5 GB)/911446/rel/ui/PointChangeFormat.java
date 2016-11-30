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

import java.lang.Comparable;
import java.text.NumberFormat;

/**
 * Representation of a quote's point change for display in a table. This class is
 * used by the {@link AbstractTable} class to identify the value type so that it can
 * render the value correctly. If the day close of a stock today is 30.0 and the day
 * close of the stock yesterday was 29.0, then today's point change is +1.
 *
 * @author Andrew Leppard
 */
public class PointChangeFormat implements Comparable {

    // The point change.
    private double change;

    // Use NumberFormat to format the value.
    private static NumberFormat format;

    /**
     * Create a new point change format object.
     *
     * @param change the point change.
     */
    public PointChangeFormat(double change) {
	this.change = change;
    }

    /**
     * Create a new point change format object.
     *
     * @param initialValue the initial quote value
     * @param finalValue the final quote value
     */
    public PointChangeFormat(double initialValue, double finalValue) {
        change = (finalValue - initialValue) * 100;
    }

    /**
     * Create a new point change format object.
     *
     * @param initialValue the initial quote value
     * @param finalValue the final quote value
     * @param isIndex if the source of the change is an index.
     */
    public PointChangeFormat(double initialValue, double finalValue, boolean isIndex) {
	double diff = finalValue - initialValue;
	change = (isIndex) ? diff : diff * 100;	
    }

    /**
     * Return the point change.
     *
     * @return the point change.
     */
    public double getPointChange() {
	return change;
    }

    /**
     * Create a string representation of the point change.
     *
     * @return string representation of the point change.
     */
    public String toString() {
        return getNumberFormat().format(getPointChange());
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

    /**
     * Get number format object for this class.
     *
     * @return the number format.
     */
    private static NumberFormat getNumberFormat() {
        // Synchronisation cannot cause issues here. So this code
        // isn't synchronised.
        if(format == null) {
            format = NumberFormat.getInstance();
            format.setMinimumIntegerDigits(1);
            format.setMinimumFractionDigits(0);
            format.setMaximumFractionDigits(1);
        }

        return format;
    }
}
