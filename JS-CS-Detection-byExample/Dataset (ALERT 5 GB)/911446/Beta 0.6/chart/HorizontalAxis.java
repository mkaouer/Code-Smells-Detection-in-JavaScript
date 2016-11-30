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
import org.mov.util.*;

public class HorizontalAxis {
    
    // Axis is in years
    public final static int YEARS = 0;
    
    // Axis is in quarters
    public final static int QUARTERS = 1;
    
    // Axis is in months
    public final static int MONTHS = 2;

    // Months in a quarter
    private final static int MONTHS_PER_QUARTER = 3;
    
    // Axis can be either major or minor
    public final static int MAJOR = 0;
    public final static int MINOR = 1;

    // Factors determing height of grid lines
    static final int STATIC_GRID_HEIGHT = 5;
    static final int VARIABLE_GRID_HEIGHT_SCALE = 50;
    static final double MAJOR_MINOR_GRID_PROPORTION = 1.5F;

    private Vector dates;
    private Vector points;
    private int period;
    private int type;

    public HorizontalAxis(Vector dates, int period, int type) {

	Iterator iterator = dates.iterator();
        TradingDate thisDate = null;
	int lastValue = -1;
	int thisValue;
	int i = 0;

        assert dates.size() > 0;

	this.period = period;
	this.type = type;

	this.dates = new Vector();
	this.points = new Vector();


	while(iterator.hasNext()) {
	    thisDate = (TradingDate)iterator.next();
	    thisValue = value(thisDate);

	    // Add the point if:
	    // 1. Its the first point on the graph
	    // 2. Its the last point on the graph
	    // 3. Its a change in axis (e.g Jan->Feb)

	    if(lastValue == -1 || !iterator.hasNext() || 
	       thisValue != lastValue)
		add(thisDate, new Integer(i));

	    i++;

	    lastValue = thisValue;
	}
    }

    public void drawLabels(Graphics g, double scale, int x, int y) {
	Integer value;
	int lastValue = 0;
	TradingDate date, lastDate;
	int midX, startX;
	int availableWidth;
	Iterator dateIterator = dates.iterator();
	Iterator pointIterator = points.iterator();
	String string;

        // There should be at least one point in our graph 
        assert dates.size() > 0;
        assert points.size() > 0;

	lastValue = ((Integer)pointIterator.next()).intValue();
	lastDate = (TradingDate)dateIterator.next();

	while(pointIterator.hasNext()) {

	    value = (Integer)pointIterator.next();
	    date = (TradingDate)dateIterator.next();
	    string = stringValue(lastDate);

	    availableWidth = (int)((value.intValue() - lastValue) * scale);
	    
	    if(availableWidth >
	       1.5 * g.getFontMetrics().stringWidth(string)) {
		midX =
		    (int)(((value.intValue() + lastValue) / 2) * scale) + x;
		startX =
		    midX - g.getFontMetrics().stringWidth(string) / 2;

		g.drawString(string, startX, y);
	    }

	    lastValue = value.intValue();
	    lastDate = date;
	}
    }

    public static double calculateScale(int width, int dataPoints) {
	double horizontalScale = 1.0D;
	
	if(dataPoints < width) {
	    horizontalScale = (double)width / dataPoints;
	}
	return horizontalScale;
    }

    public void drawGrid(Graphics g, int y,
			 double horizontalScale,
			 int heightOfGraph) {
	
	Iterator iterator = points.iterator();
	Integer axisPoint;
	int lineSize;
	int i = 0;

	// We dont draw the whole grid lines just stumps, the major
	lineSize = (int)(STATIC_GRID_HEIGHT + 
			 heightOfGraph / VARIABLE_GRID_HEIGHT_SCALE);

	// Major axis is indicated by taller stumps
	if(getType() == MAJOR)
	    lineSize *= MAJOR_MINOR_GRID_PROPORTION;

	while(iterator.hasNext()) {
	    axisPoint = (Integer)iterator.next();

	    // dont draw the first or last axis points as they are the
	    // start and end of the graph indicators
	    if(i++ > 0 && iterator.hasNext())
		g.drawLine((int)(axisPoint.intValue() * horizontalScale), y,
			   (int)(axisPoint.intValue() * horizontalScale), 
			   y - lineSize);
	}
    }
    
    private void add(TradingDate date, Integer point) {
	dates.add(date);
	points.add(point);
    }
    
    private int getPeriod() {
	return period;
    }

    private int getType() {
	return type;
    }

    private String stringValue(TradingDate date) {
	switch(period) {
	case(YEARS):
	    return Integer.toString(value(date));
	case(QUARTERS):
	    return "Q" + 
		Integer.toString(value(date));
	case(MONTHS):
	    return TradingDate.monthToText(value(date));
	default:
	    return "???";
	}
    }
    
    private int value(TradingDate date) {
	
	switch(period) {
	case(YEARS):
	    return date.getYear();
	case(QUARTERS):
	    return ((date.getMonth()-1) / MONTHS_PER_QUARTER + 1);
	case(MONTHS):
	    return date.getMonth();
	default:
	    return 0;
	}
    }
    
    public int getWidth() {
	// Dont use the width between the first two points as that
	// may be shorter than the average
	if(points.size() > 2)
	    return ((Integer)points.elementAt(2)).intValue() - 
		((Integer)points.elementAt(1)).intValue();
	
	// unless weve only two points
	else if (points.size() > 1)
	    return ((Integer)points.elementAt(1)).intValue() - 
		((Integer)points.elementAt(0)).intValue();
	
	// else no points
	return 0;
    }
}
