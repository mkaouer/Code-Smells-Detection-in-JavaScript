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

package nz.org.venice.chart;

import java.util.*;

import nz.org.venice.util.TradingDate;

/**
 * Represents a graphable set of X points and their associated list of Y points.
 *
 * @author Mark Hummel
 */
public class PFGraphable extends Graphable {

    private Comparable startX = null;
    private Comparable endX = null;
    private LinkedHashMap map;
    private LinkedHashMap charMap;
    private LinkedHashMap dateMap; 
    private double boxPrice;
    private int columnSpan;

    /**
     * Create an empty PFgraphable.
     */
    public PFGraphable() {
	super();
	
	map = getMap();	
	charMap = new LinkedHashMap();
	dateMap = new LinkedHashMap();
	
	boxPrice = 0.01;
	columnSpan = 1;
    }

    /**
     * Get the last X value where we have an associated Y value.
     *
     * @return	the last x value which we contain data
     */
    public Comparable getEndX() {
        if(endX == null) {
            // Get last value
            Iterator iterator = map.keySet().iterator();
            while(iterator.hasNext())
                endX = (Comparable)iterator.next();
        }

	return endX;
    }

    /**
     * Get the first X value where we have an associated Y value.
     *
     * @return	the first x value which we contain data
     */
    public Comparable getStartX() {
        if(startX == null) {
            // Get first value
            Iterator iterator = map.keySet().iterator();
            assert iterator.hasNext();
            startX = (Comparable)iterator.next();
        }
	return startX;
    }

    /**
     * Get the Y value for the given X value.
     *
     * @param	x	the x value
     * @return	y	the associated y value
     */
    public Vector getYList(Comparable x) {
	return (Vector)map.get(x);
    }

    /**
     * Get the character value for the given X value.
     *
     * @param	x	the x value
     * @return	y	the associated character value
     */
    public String getString(Comparable x) {
	return (String)charMap.get(x);
    }

    /**
     * Get the date value for the given X value.
     *
     * @param	x	the x value
     * @return	y	the associated character value
     */
    public TradingDate getDate(Comparable x) {
	return (TradingDate)dateMap.get(x);
    }


    /**
     * Return the box price for this graph  
     * 
     * @return box price The box price
    */
    public double getBoxPrice() {
	return boxPrice;
    }

    /**
     * Return the size of the column span.
     *
     * @return columnSpan The column span
    */
    public int getColumnSpan() {
	return columnSpan;
    }
    
    /**
     * Associate the given X value with the given data. This
     * function is used to "load" up the graphable with data.
     *
     * @param	x	the x value
     * @param	date	a date representing the point at which data changed  
     * @param	list	a list of price values within a move. 
     * @param	marker	a string denoting a price value as an upmove or downmove.   
     
     */
    public void putData(Comparable x, Comparable date, Vector list, String marker) {
        startX = endX = null;

	putDate(x, date);
	putYList(x, list);
	putString(x, marker); 
    }

    /**
     * Set the box price at graph generation 
     *
     * @param  value   The box price which represents the smallest 
     *                 plottable price point
    */
    public void setBoxPrice(double value) {
	boxPrice = value;
    }

    /**
     * Set the size of the date column span. A PF graph does not necessarily
     * have price data for a particular date. 
     * 
     * @param value  The size of the column span
     *
    */
    public void setColumnSpan(int value) {
	columnSpan = value;
    }

    /**
     * Associate the given X value with the given Y value. This
     * function is used to "load" up the graphable with data.
     *
     * @param	x	the x value
     * @param	y	the y value
     */
    public void putDate(Comparable x, Comparable y) {
        startX = endX = null;

	 dateMap.put(x, (Object)y);	
	
    }

    /**
     * Associate the given X value with the given Y value. This
     * function is used to "load" up the graphable with data.
     *
     * @param	x	the x value
     * @param	y	the associated List of y values
     */
    public void putYList(Comparable x, Vector y) {
        startX = endX = null;

	 map.put(x, (Object)y);	
	
    }

    

    /**
     * Associate the given x value with the given character. This
     * function is used to "load" up the graphable with data.
     *
     * @param	x	the x value
     * @param	s	the associated character
     */
    public void putString(Comparable x, String s) {
	charMap.put(x, (Object)s);
    }


    /**
     * Given an X range, inspect all the associated Y values and return the
     * highest.
     *
     * @param	xRange	a <code>List</code> of <code>Comparable</code>
     *			objects
     * @return	the highest Y value
     */
    public double getHighestY(List xRange) {
	Iterator iterator = xRange.iterator();
	Iterator iterator2;
	Vector yList;
	Double y = null;
	Double highestY = new Double(Double.NEGATIVE_INFINITY);

	while(iterator.hasNext()) {
	    yList = getYList((Comparable)iterator.next());
	
	    if (yList != null) {
		iterator2 = yList.iterator();	
		while (iterator2.hasNext()) {		
		    y = (Double)iterator2.next();
		    if(y != null && y.compareTo(highestY) > 0)
			highestY = y;
		}
	    }
        }
		
	return highestY.doubleValue();
    }

    /**
     * Given an X range, inspect all the associated Y values and return the
     * lowest.
     *
     * @param	xRange	a <code>List</code> of <code>Comparable</code>
     *			objects
     * @return	the lowest Y value
     */
    public double getLowestY(List xRange) {
	Iterator iterator = xRange.iterator();
	Double y = null;
	Vector yList;
	Double lowestY = new Double(Double.MAX_VALUE);
	Comparable x;
	
	while(iterator.hasNext()) {

	    x = (Comparable)iterator.next();
	    yList = getYList(x);
	    if (yList != null) {
		Iterator iterator2 = yList.iterator();
	
		while (iterator2.hasNext()) {
		    y = (Double)iterator2.next();
		
		    if(y != null && y.compareTo(lowestY) < 0)
			lowestY = y;
		}
	    } 	
	}

	return lowestY.doubleValue();
    }

    /**
     * Get all the X values for where we have an associated Y value.
     *
     * @return	the set of all X values which have associated Y values
     */
    public Set getXRange() {
	return map.keySet();
    }

    public boolean dataAvailable(Vector x) {
	double val, lowest;

	val = getHighestY(x);
	lowest = getLowestY(x);

	if (getHighestY(x) == Double.NEGATIVE_INFINITY) {
	    return false;
	}
	return true;
    }


}
