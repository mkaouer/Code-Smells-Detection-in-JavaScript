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

import java.awt.*;
import java.util.*;

import org.mov.chart.graph.*;
import org.mov.chart.source.*;
import org.mov.util.*;

public class GraphTools {

    public static void renderLine(Graphics g, Graphable source, 
				  int xoffset, int yoffset,
				  float horizontalScale, float verticalScale,
				  float bottomLineValue, Vector xRange) {
	
	int xCoordinate, yCoordinate;
	int lastXCoordinate = -1 , lastYCoordinate = -1;
	Float y;
	Comparable x;
	Iterator iterator = xRange.iterator();
	int i = 0;

	while(iterator.hasNext()) {

	    x = (Comparable)iterator.next();

	    // Skip until our start X
	    if(x.compareTo(source.getStartX()) < 0) {
		i++;
		continue;
	    }

	    // If our graph is finished exit this loop
	    if(x.compareTo(source.getEndX()) > 0) 
		break;

	    // Otherwise draw point
	    y = source.getY(x);

	    // The graph is allowed to skip points
	    if(y != null) {
		xCoordinate = (int)(xoffset + horizontalScale * i);
		yCoordinate = yoffset - scaleAndFitPoint(y.floatValue(), 
							 bottomLineValue, 
							 verticalScale);
		
		if(lastXCoordinate != -1)
		    g.drawLine(xCoordinate, yCoordinate, 
			       lastXCoordinate, lastYCoordinate);
		else
		    g.drawLine(xCoordinate, yCoordinate, 
			       xCoordinate, yCoordinate);

		lastXCoordinate = xCoordinate;
		lastYCoordinate = yCoordinate ;
	    }

	    i++;
	}
    }

    public static void renderBar(Graphics g, Graphable source, 
				 int xoffset, int yoffset,
				 float horizontalScale, float verticalScale,
				 float bottomLineValue, Vector xRange) {
	int x2, y1, y2;
	int x1 = -1;
	Float y;
	float floatValue;
	Comparable x;
	Iterator iterator = xRange.iterator();
	int i = 0;

	y2 = yoffset - scaleAndFitPoint(0, bottomLineValue, verticalScale);

	while(iterator.hasNext()) {

	    x = (Comparable)iterator.next();

	    // Skip until our start X
	    if(x.compareTo(source.getStartX()) < 0) {
		i++;
		continue;
	    }

	    // If our graph is finished exit this loop
	    if(x.compareTo(source.getEndX()) > 0) 
		break;

	    // Otherwise draw point
	    y = source.getY(x);

	    // The graph is allowed to skip points
	    if(y == null) 
		floatValue = 0;
	    else
		floatValue = y.floatValue();

	    x2 = (int)(xoffset + horizontalScale * i);
	    y1 = yoffset - scaleAndFitPoint(floatValue, 
	    				    bottomLineValue, verticalScale);
	  
	    if(x1 != -1) 
		g.fillRect(Math.min(x1, x2), Math.min(y1, y2), 
			   Math.abs(x2-x1) + 1, Math.abs(y2-y1));
	    
	    x1 = x2 + 1;

	    i++;
	}
    }

    // Given the float y value of a point, the verticale offset and the
    // vertical scale, return the y coordinate where the point should be.
    public static int scaleAndFitPoint(float point, 
				       float offset, float scale) {
	return (int)((point - offset) * scale);
    }

    /** 
     * Returns a hash map of annotations with buy/sell recommendations
     * based on when graph 1 cuts graph 2:
     *
     * If the graph1 crosses from below to above graph2 its a signal to buy.
     * If the graph1 crosses from above to below graph2 its a signal to sell.
     *
     * This is true of Moving Average & MACD
     *
     * Assumption both graph1 & graph2 have the same x range
     *
     * @param	graph1	first graphable
     * @param	graph2	second graphable
     * @return	map of x values vs annotations
     */
    public static HashMap createAnnotations(Graphable graph1,
					    Graphable graph2) {
	HashMap annotations = new HashMap();

	// Iterate over x for the first graph (could just as easily have
	// been over the second).
	Set xRange = graph1.getXRange();
	Iterator iterator = xRange.iterator();
	Float graph1Y, graph2Y;
	Float lastGraph1Y = null;
	Float lastGraph2Y = null;

	while(iterator.hasNext()) {

	    Comparable x = (Comparable)iterator.next();

	    graph1Y = graph1.getY(x);
	    graph2Y = graph2.getY(x);

	    if(graph1Y != null && graph2Y != null &&
	       lastGraph1Y != null && lastGraph2Y != null) {

		// Buy
		if(graph1Y.compareTo(graph2Y) > 0 &&
		   lastGraph1Y.compareTo(lastGraph2Y) <= 0)
		    annotations.put((Object)x, "Buy");
		
		// Sell
		else if(graph1Y.compareTo(graph2Y) < 0 &&
		   lastGraph1Y.compareTo(lastGraph2Y) >= 0)
		    annotations.put((Object)x, "Sell");
	    }

	    if(graph1Y != null && graph2Y != null) {
		lastGraph1Y = graph1Y;
		lastGraph2Y = graph2Y;
	    }
	}

	return annotations;

    }
}

