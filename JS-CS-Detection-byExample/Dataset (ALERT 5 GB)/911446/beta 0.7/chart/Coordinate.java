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
 * Coordinates for charts.
 *
 * @author Mark Hummel
 * 
 */

package org.mov.chart;

public class Coordinate 
{

    protected static final int BREAK = -1; //End of coordinates/Out of bounds marker 

    private Comparable dataX;
    private Double dataY;    
    private Integer yCoord; 
    private int level;

    /**
     * Construct a new coordinate defined in the chart space  
     * 
     * @param x      The X value of the coordinate (a data value)
     * @param y      The Y value of the coordinate (a data value)
     * @param yCoord The screen value of the yCoordinate
     */
    public Coordinate(Comparable x, Double y, Integer yCoord) {
	dataX = x;
	dataY = y;
	this.yCoord = yCoord;
	level = 0;
    }

    /**
     * Construct a new coordinate defined in the chart space  
     * 
     * @param x      The X value of the coordinate (a data value)
     * @param y      The Y value of the coordinate (a data value)
     * @param yCoord The screen value of the yCoordinate
     * @param level  The chart level of the yCoordinate
     */
    public Coordinate(Comparable x, Double y, Integer yCoord, int level) {
	dataX = x;
	dataY = y;
	this.yCoord = yCoord;
	this.level = level;
    }
    
    /** 
     * Constrcut a Coordinate, as a null coordinate initially
     *
     */
    public Coordinate() {
	dataX = null;
	dataY = null;
	yCoord = new Integer(BREAK);
	level = 0;
    }
	
    /** 
     * 
     * Return the X value of this coordinate
     * 
     * @return the X Data value
    */
    public Comparable getXData() {
	return dataX;
    }

    /** 
     * 
     * Return the Y value of this coordinate
     * 
     * @return the Y Data value
    */
    public Double getYData() {
	return dataY;
    }
    
    /** 
     * 
     * Return the absolute y coordinate of this coordinate
     * 
     * @return the Y coordinate
    */
    public Integer getYCoord() {
	return yCoord;
    }

    /** 
     * 
     * Return the graph level the y coordinate
     * 
     * @return the level 
    */
    public int getLevel() {
	return level;
    }
    
    

    /**
     * 
     * Direct comparison between this coordinate and a specified one
     *
     * @param The line to compare this one to
     * @return True if the coordinate has the same x,y values,
     *         false otherwise
       
    */
    public boolean compareTo(Coordinate c) {
	if (dataX.compareTo(c.getXData()) == 0 &&
	    dataY.compareTo(c.getYData()) == 0) {
	    
	    return true;
	}
	return false;
    }
    
    //Debugging
    public String toString() {
	String rv = "(" + dataX + "," + dataY + "," + yCoord + ")";
	
	return rv;
    }

}