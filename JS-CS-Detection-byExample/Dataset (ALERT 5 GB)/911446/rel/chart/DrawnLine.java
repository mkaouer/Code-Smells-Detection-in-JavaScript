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
 * Graphic elements (e.g lines, points, text) for drawing charts.
 *
 * @author Mark Hummel
 * 
 */

package nz.org.venice.chart;

import java.util.Vector;

public class DrawnLine 
{

    private Coordinate start; 
    private Coordinate end;

    public DrawnLine() {
	start = null;
	end = null;
    }


    /**
     * Construct a new DrawnLine with a pair of end points
     * 
     * @param start  The first end point
     * @param end    The second end point
     *
    */
    public DrawnLine(Coordinate start, Coordinate end) {
	this.start = start;
	this.end = end;
    }

    /**
     * Return the coordinate of the first point of the line
     * 
     * @return The Coordinate of the start point

    */
    public Coordinate getStart() {
	return start;
    }

    /**
     * Return the coordinate of the last point of the line
     * 
     * @return The Coordinate of the end point

    */
    public Coordinate getEnd() {
	return end;
    }

    /**
     * Set the start point of the line
     *
     * @param p  The Coordinate of the first point in the line
     */
    public void setStart(Coordinate p) {
	start = p;
    }

    /**
     * Set the end point of the line
     *
     * @param p  The Coordinate of the end point in the line
     */
    public void setEnd(Coordinate p) {
	end = p;
    }


    /**
     * 
     * Direct comparison between this line and a specified one
     *
     * @param l The line to compare against this one
     * @return True if the line has the same start and end point
     *         false otherwise
       
    */
    public boolean compareTo(DrawnLine l) {
	if (start == null ||
	    end == null) {
	    
	    return false;
	}
	
	if (start.compareTo(l.getStart()) &&
	    end.compareTo(l.getEnd())) {
	    return true;
	}
	return false;
    }
    
    /**
     * 
     * Direct comparison between this line and a specified one
     
     * @param start The start point of the line to compare 
     * @param end The end point of the line to compare 
     * @return True if the line has the same start and end point
     *         false otherwise
       
    */
    public boolean compareTo(Coordinate start, Coordinate end) {
	DrawnLine temp = new DrawnLine(start, end);
	return compareTo(temp);
    }
    
    //For debugging
    public String toString() {
	String rv = "Start: " + start + " " + "End: " + end;

	return rv;
    }

}