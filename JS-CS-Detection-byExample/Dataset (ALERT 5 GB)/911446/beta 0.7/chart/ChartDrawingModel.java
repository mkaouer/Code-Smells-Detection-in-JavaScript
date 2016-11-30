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

package org.mov.chart;

import java.util.Vector;
import java.util.HashMap;

public class ChartDrawingModel
{

    Vector points;
    Vector lines;
    HashMap text;
    
    public ChartDrawingModel() {
	points = new Vector();
	lines = new Vector();
	text = new HashMap();
    }
    
    
    /**
     *
     * Creates a new straight line drawn on the chart.
     * 
     * @param  start The first point on the line.
    */
    public void createNewLine(Coordinate start) {
	DrawnLine line = new DrawnLine();
	line.setStart(start);
	//So there's some defined end
	line.setEnd(start);
	lines.add(line);
    }
    
    /**
     *
     * Return the last line drawn, or in the process of being
     * drawn.
     *
     * @return A DrawnLine object (See DrawnLine.java)
    */
    public DrawnLine getLastLine() {
	return (DrawnLine)lines.elementAt(lines.size() - 1);
    }
	
    /**
     * Return all the lines drawn on the chart.
     *
     * @return A Vector of DrawnLine objects 
    */
    public Vector getDrawnLines() {
	return lines;
    }
    

    /**
     * Return a specific line drawn on the chart
     *
     * @param  index  The index to the list of lines
     * @return the index'th DrawnLine object drawn on the chart. 
     */
    public DrawnLine getDrawnLine(int index) {
	return (index < lines.size()) 
	    ? (DrawnLine)lines.elementAt(index)
	    : null;

    }

    /**
     *
     * Remove the drawn line corresponding at a specific set of 
     * start and end points
     *
     * @param start  The start point of the line
     * @param end    The end point of the line
    */
    public void removeLine(Coordinate start, Coordinate end) {
	int i;

	i = findLine(start, end);
	if (i != -1) {
	    lines.remove(i);
	}
    }

    /**
     *
     * Return the index of a line at a set of points if it exists 
     * on the chart.
     *
     * @param start  The start point of a line
     * @param end    The end point of a line
     * @return The index in the list of lines drawn on the chart
     *         if it exists, -1 otherwise.
    */
    private int findLine(Coordinate start, Coordinate end) {
	int i;
		
	for (i = 0; i < lines.size(); i++) {
	    DrawnLine line = (DrawnLine)lines.elementAt(i);
	    if (line.compareTo(start, end)) {
		return i;
	    }
	}
	return -1;
    }

    /**
      
     * Return the index of the point if it exists 
     * on the chart.
     *
     * @param point  The point to search for
     * @return The index in the list of points drawn on the chart
     
    */
    private int findPoint(Coordinate point) {
	int i;

	for (i = 0; i < points.size(); i++) {
	    Coordinate temp = (Coordinate)points.elementAt(i);
	    if (temp.compareTo(point)) {
		return i;
	    }
	}
	return -1;
    }

    /**
     *
     * Place a point on the chart at a point
     * 
     * @param point The coordinate to place on the chart
    */
    public void setPoint(Coordinate point) {
	points.add(point);
    }

    /**
     *
     * Return the list of all points drawn on the chart
     * 
     * @return A vector of all points drawn on the chart
    */
    public Vector getDrawnPoints() {
	return points;
    }

    
    /**
     *
     * Remove the point drawn on the chart at this coordinate
     * @param point The coordinate of the point to remove
    */
    public void removePoint(Coordinate point) {
	int i;

	i = findPoint(point);
	if (i != -1) {
	    points.remove(i);
	}
    }

    /**
     * Return all the text written on the chart
     *
     * @return A HashMap of all the text, keyed by coordinates.
    */
    public HashMap getText() {
	return text;
    }

    /**
     * Put text on the chart at a specific point
     * 
     * @param point The coordinate on the chart of where the text is to placed
     * @param str   The string to write on the chart.
    */
    public void setText(Coordinate point, String str) {
	
	text.put(point, str);
    }

    /**
     * Remove the text at a specific point
     *
     * @param point The coordinate on the chart of where to delete the text
    */
    public void removeText(Coordinate point) {
	text.remove(point);
    }

}