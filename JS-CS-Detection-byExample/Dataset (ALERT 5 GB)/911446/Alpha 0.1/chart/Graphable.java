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

import java.util.*;

import org.mov.util.*;

/**
 * Represents a graphable set of X points and their associated Y points.
 * <p>
 * Example, loading up the class with an exponential graph:
 * <pre>
 * Graphable graphable = new Graphable();
 * graphable.putY(new Float(1), new Float(1));
 * graphable.putY(new Float(2), new Float(4));
 * graphable.putY(new Float(3), new Float(9));
 * graphable.putY(new Float(4), new Float(16));
 * </pre>
 * Find the square of 3:
 * <pre>
 * Float squareOfThree = graphable.getY(new Float(3));
 * </pre>
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
    public Float getY(Comparable x) {
	return (Float)map.get(x);
    }

    /**
     * Associate the given X value with the given Y value. This
     * function is used to "load" up the graphable with data.
     * 
     * @param	x	the x value
     * @param	y	the associated y value
     */
    public void putY(Comparable x, Float y) {
        startX = endX = null;

	map.put(x, (Object)y);
    }

    /**
     * Given an X range, inspect all the associated Y values and return the 
     * highest.
     *
     * @param	xRange	a <code>Vector</code> of <code>Comparable</code> 
     *			objects
     * @return	the highest Y value
     */
    public float getHighestY(Vector xRange) {
	Iterator iterator = xRange.iterator();
	Float y = null;
	Float highestY = new Float(Float.NEGATIVE_INFINITY);

	while(iterator.hasNext()) {
	    y = getY((Comparable)iterator.next());

	    if(y != null && y.compareTo(highestY) > 0)
            	highestY = y;
        }
	
	return highestY.floatValue();
    }

    /**
     * Given an X range, inspect all the associated Y values and return the 
     * lowest.
     *
     * @param	xRange	a <code>Vector</code> of <code>Comparable</code> 
     *			objects
     * @return	the lowest Y value
     */
    public float getLowestY(Vector xRange) {
	Iterator iterator = xRange.iterator();
	Float y = null;
	Float lowestY = new Float(Float.MAX_VALUE);
	
	while(iterator.hasNext()) {

	    y = getY((Comparable)iterator.next());
	    
	    if(y != null && y.compareTo(lowestY) < 0)
		lowestY = y;
	}

	return lowestY.floatValue();
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
    public float[] toArray() {
	Collection valueCollection = map.values();
	Iterator iterator = valueCollection.iterator();

	float[] values = new float[map.size()];
	Float value;
	int i = 0;

	while(iterator.hasNext()) {
	    value = (Float)iterator.next();	   
	    values[i++] = value.floatValue();
	}
	
	return values;
    }
}
