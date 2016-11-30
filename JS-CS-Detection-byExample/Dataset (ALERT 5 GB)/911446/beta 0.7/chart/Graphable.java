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

package org.mov.chart;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Represents a graphable set of X points and their associated Y points.
 * <p>
 * Example, loading up the class with an exponential graph:
 * <pre>
 * Graphable graphable = new Graphable();
 * graphable.putY(new Double(1), new Double(1));
 * graphable.putY(new Double(2), new Double(4));
 * graphable.putY(new Double(3), new Double(9));
 * graphable.putY(new Double(4), new Double(16));
 * </pre>
 * Find the square of 3:
 * <pre>
 * Double squareOfThree = graphable.getY(new Double(3));
 * </pre>
 *
 * @author Andrew Leppard
 */
public class Graphable {

    private Comparable startX = null;
    private Comparable endX = null;
    private LinkedHashMap map;

    /**
     * Create an empty graphable.
     */
    public Graphable() {
	map = new LinkedHashMap();
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
    public Double getY(Comparable x) {
	return (Double)map.get(x);
    }

    /**
     * Associate the given X value with the given Y value. This
     * function is used to "load" up the graphable with data.
     *
     * @param	x	the x value
     * @param	y	the associated y value
     */
    public void putY(Comparable x, Double y) {
        startX = endX = null;

	map.put(x, (Object)y);
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
	Double y = null;
	Double highestY = new Double(Double.NEGATIVE_INFINITY);

	while(iterator.hasNext()) {
	    y = getY((Comparable)iterator.next());

	    if(y != null && y.compareTo(highestY) > 0)
            	highestY = y;
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
	Double lowestY = new Double(Double.MAX_VALUE);
	
	while(iterator.hasNext()) {

	    y = getY((Comparable)iterator.next());

	    if(y != null && y.compareTo(lowestY) < 0)
		lowestY = y;
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

    /**
     * Return all the Y values as an array. The array will be ordered
     * by the X values.
     *
     * @return	array of Y values
     */
    public double[] toArray() {
	Collection valueCollection = map.values();
	Iterator iterator = valueCollection.iterator();

	double[] values = new double[map.size()];
	Double value;
	int i = 0;

	while(iterator.hasNext()) {
	    value = (Double)iterator.next();	
	    values[i++] = value.doubleValue();
	}
	
	return values;
    }

    /**
     * Return an interator which iterates over the X values.
     *
     * @return iterator
     */
    public Iterator iterator() {
        return map.keySet().iterator();
    }

    public LinkedHashMap getMap() {
	return map;
    }

}
