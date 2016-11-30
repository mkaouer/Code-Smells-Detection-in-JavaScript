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

/**
 * Enclosing class for adjusting a OHCLV quote source. Supports dividend and 
 * split adjustments.
 * 
 * @see OHLCVQuoteGraphSource
 * @author Mark Hummel
 */

package nz.org.venice.chart.source;

public class Adjustment {

    //Type - Split or dividend. 
    //A split applies a multiplier to a price value
    //A dividend applies an add/subtract to a price value.
    
    public static final int ADJUST_SPLIT = 0; 
    public static final int ADJUST_DIVIDEND = 1;

    private final Comparable startPoint;
    private final double value;
    private final int type;
    private final boolean direction;

    /**
     * Create a new adjustment to be applied to quote data.
     * 
     * @param type The type of the adjustment (ie split or dividend)
     * @param value The value to apply
     * @param startPoint The point in the data from which to apply the adjustment
     * @param direction true if the adjustment is to be applied in a positive direction. 
     */
    
    public Adjustment(int type, double value, Comparable startPoint, boolean direction) {
	
	this.type = type;
	this.value = value;
	this.startPoint = startPoint;
	this.direction = direction;
    }
    
    /**     
     * @return the adjustment type.
     */

    public int getType() {
	return type;
    }

    /**     
     * @return the adjustment value.
     */

    public double getValue() {
	return value;
    }
    
    /**     
     * @return the starting point.
     */

    public Comparable getStartPoint() {
	return startPoint;
    }

    /**     
     * @return the direction.
     */
    
    public boolean getDirection() {
	    return direction;
    }
}