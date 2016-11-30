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

import java.awt.Graphics;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class GraphTools {

    public static void renderHorizontalLine(Graphics g,
                                            double yValue,
                                            int xoffset,
                                            int yoffset,
                                            double horizontalScale,
                                            double verticalScale,
                                            double bottomLineValue,
                                            List xRange) {

        int startX = xoffset;
        int endX = (int)(xoffset + (xRange.size() - 1) * horizontalScale);
        int y = yoffset - scaleAndFitPoint(yValue, bottomLineValue, verticalScale);

        g.drawLine(startX, y, endX, y);
    }

    public static void renderLine(Graphics g, Graphable source,
				  int xoffset, int yoffset,
				  double horizontalScale, double verticalScale,
				  double bottomLineValue, List xRange) {
	
	int xCoordinate, yCoordinate;
	int lastXCoordinate = -1 , lastYCoordinate = -1;
	Double y;
	Comparable x;
	Iterator iterator = xRange.iterator();
	int i = 0;
    int halfbarWidth=(int)(0.309 * horizontalScale);//bryan    int halfBlankWidth=(int) (horizontalScale-halfbarWidth*2)/2;//bryan
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
		yCoordinate = yoffset - scaleAndFitPoint(y.doubleValue(),
							 bottomLineValue,
							 verticalScale);
		
		if(lastXCoordinate != -1)
//		    g.drawLine(xCoordinate, yCoordinate,//
//			       lastXCoordinate, lastYCoordinate);		    g.drawLine(xCoordinate+halfbarWidth+1 +halfBlankWidth, yCoordinate,				       lastXCoordinate, lastYCoordinate);
		else
//		    g.drawLine(xCoordinate, yCoordinate,//
//			       xCoordinate, yCoordinate);		    g.drawLine(xCoordinate+halfbarWidth+1 +halfBlankWidth, yCoordinate,		    		xCoordinate+halfbarWidth+1 +halfBlankWidth, yCoordinate);
//		lastXCoordinate = xCoordinate;		lastXCoordinate = xCoordinate+halfbarWidth+1 +halfBlankWidth;
		lastYCoordinate = yCoordinate ;
	    }

	    i++;
	}
    }

    public static void renderBar(Graphics g, Graphable source,
				 int xoffset, int yoffset,
				 double horizontalScale, double verticalScale,
				 double bottomLineValue, List xRange) {
	int x2, y1, y2;
//	int x1 = -1;    int halfbarWidth=(int)(0.309 * horizontalScale);//bryan    int halfBlankWidth=(int) (horizontalScale-halfbarWidth*2)/2;//bryan
	Double y;
	double doubleValue;
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
		doubleValue = 0;
	    else
		doubleValue = y.doubleValue();

	    x2 = (int)(xoffset + horizontalScale * i);
	    y1 = yoffset - scaleAndFitPoint(doubleValue,
	    				    bottomLineValue, verticalScale);
		g.fillRect( x2 +halfBlankWidth, Math.min(y1, y2),	    				halfbarWidth*2+1, Math.abs(y2-y1));
//	    if(x1 != -1)//
//		g.fillRect(Math.min(x1, x2), Math.min(y1, y2),//
//			   Math.abs(x2-x1) + 1, Math.abs(y2-y1));//
//	//
//	    x1 = x2 + 1;

	    i++;
	}
    }

    // Given the double y value of a point, the verticale offset and the
    // vertical scale, return the y coordinate where the point should be.
    public static int scaleAndFitPoint(double point,
				       double offset, double scale) {
	return (int)((point - offset) * scale);
    }

    // chars is character to draw - in synch with list xRange
    public static void renderChar(Graphics g, PFGraphable source,
				 int xoffset, int yoffset,
				 double horizontalScale, double verticalScale,
				  double bottomLineValue, List xRange) {

	int xCoordinate, yCoordinate;
	int lastXCoordinate = -1 , lastYCoordinate = -1;
	Vector yList;
	Double y;
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
	    yList = source.getYList(x);

	    for (int j = 0; yList != null && j < yList.size(); j++) {
		y = (Double)yList.elementAt(j);
		
		// The graph is allowed to skip points
		if(y != null) {
		
		    xCoordinate = (int)(xoffset + horizontalScale * i);
		    yCoordinate = yoffset - scaleAndFitPoint(y.doubleValue(),
							     bottomLineValue,
							     verticalScale);
		    g.drawString(source.getString(x),xCoordinate, yCoordinate);
		
		}				
	    }
	
	    i++;
	}
	
    }
}

