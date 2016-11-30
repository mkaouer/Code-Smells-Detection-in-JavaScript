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

package org.mov.chart.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.mov.chart.Graphable;
import org.mov.chart.GraphableQuoteFunctionSource;
import org.mov.chart.GraphTools;
import org.mov.chart.source.GraphSource;
import org.mov.util.Locale;
import org.mov.util.TradingDate;
import org.mov.parser.EvaluationException;
import org.mov.quote.QuoteFunctions;

/**
 * 3 Bar Nett Countback lines. This graph draws a single across
 * a price value which indicates a buy signal if used with highs
 * in a down trend and a sell signal with lows in a new uptrend.
 *
 * @author Mark Hummel
 * @see CountbackLineGraphUI
 */
public class CountbackLineGraph extends AbstractGraph {

    // Moving average values ready to graph
    private Graphable countback;
    
    private Graphable dayLow;
    private Graphable dayHigh;

    public static final int BREAKOUT = 0;
    public static final int STOPLOSS = 1;

    /**
     * Create a new countback line.
     *
     * @param	source	the source to create a moving average from
     */
    public CountbackLineGraph(GraphSource low, 
			      GraphSource high,
			      GraphSource close) {
	super(close);	
	dayLow = low.getGraphable();
	dayHigh = high.getGraphable();
        setSettings(new HashMap());


    }

    public void render(Graphics g, Color colour, int xoffset, int yoffset,
		       double horizontalScale, double verticalScale,
		       double bottomLineValue, List xRange) {

	// We ignore the graph colours and use our own custom colours
	g.setColor(Color.green.darker());

	

	GraphTools.renderLine(g, countback, xoffset, yoffset,
			      horizontalScale,
			      verticalScale, bottomLineValue, xRange);
    }

    public String getToolTipText(Comparable x, int y, int yoffset,
				 double verticalScale,
				 double bottomLineValue)
    {
	return null; // we never give tool tip information
    }

    // Highest Y value is in the moving average graph
    public double getHighestY(List x) {
	return countback.getHighestY(x);
    }

    // Lowest Y value is in the moving average graph
    public double getLowestY(List x) {
	return countback.getLowestY(x);
    }

    /**
     * Return the name of this graph.
     *
     * @return	<code>Exponentially Weighted Moving Average</code>
     */
    public String getName() {
	return Locale.getString("COUNTBACK_LINE");
    }

    /**
     * Creates a new Countback line based on the given data source.
     *
     * @param	dayHigh	the graph source of the daily highs
     * @param   dayLow  the graph source of the daily lows
     * @param	type	the desired countback line
     * @return	the graphable containing averaged data from the source
     */
    public Graphable createCountbackLine(Graphable dayHigh,
					 Graphable dayLow,
					 int type) {
	
	Graphable countback = new Graphable();
	double dataLow[] = dayLow.toArray();
	double dataHigh[] = dayHigh.toArray();
	double countbackValue = 0.0;
	int localMin = -1, localMax = -1;
	int index = 0;
	TradingDate date;
	Vector dates = new Vector();
	Iterator iterator = dayHigh.iterator();
	int i;
	
	while (iterator.hasNext()) {
	    date = (TradingDate)iterator.next();
	    dates.add(date);
	}
	
	switch (type) {
	case BREAKOUT:
	    localMin = getPivotPoint(dataLow);
	    countbackValue = getBreakoutCBLValue(dataHigh, localMin, 
						 dataLow[localMin]);
	    index = localMin;
	    break;
	    
	case STOPLOSS:
	    localMax = getHighestHigh(dataHigh);
	    countbackValue = getStopLossCBLValue(dataLow, localMax,
						 dataLow[localMax]);
	    
	    index = localMax;
	    break;
	}
	for (i = index; i < dataHigh.length; i++) {
		date = (TradingDate)dates.elementAt(i);
		countback.putY(date, new Double(countbackValue));
	}
	
        return countback;
    }

    public boolean isPrimary() {
        return true;
    }

    public void setSettings(HashMap settings) {
        super.setSettings(settings);
	
        // Retrieve settings from hashmap
        int type = CountbackLineGraphUI.getType(settings);
        
	// Create moving average graphable
	countback = createCountbackLine(dayHigh,
					dayLow,
					type);
                                            
    }

    /**
     * Return the graph's user interface.
     *
     * @param settings the initial settings
     * @return user interface
     */
    public GraphUI getUI(HashMap settings) {
        return new CountbackLineGraphUI(settings);
    }
    
    /**
     *
     * Return the count back value of a possible breakout. 
     * 
     * @param data  An array of price data
     * @param startIndex  The starting point for the calculation
     * @param high  The initial high value
     * @return  The count back value 
    */
    private double getBreakoutCBLValue(double[] data, int startIndex, 
					 double high) {
	int count = 0;
	int i;
	double thisHigh = high;
	
	for (i = startIndex; i > 0; i--) {
	    if (data[i] > thisHigh) {
		thisHigh = data[i];
		count++;
	    }
	    if (count >= 3) {
		return thisHigh;
	    }
	}
	return thisHigh;
    }
    
    /**
     *
     * Return the stop loss value as determined by the count back line
     * 
     * @param data  An array of price data
     * @param startIndex  Where in the array to start the calculation
     * @param low         The starting low point 
     * @return  the stop loss value
    */
    private double getStopLossCBLValue(double[] data, int startIndex,
					      double low) {

	int count = 0;
	int i;
	double thisLow = low;
	double startVal = data[startIndex];

	for (i = startIndex; i > 0; i--) {
	    if (data[i] < thisLow) {
		thisLow = data[i];
		count++;
	    }
	    //if we encounter a low which is in fact higher than our 
	    //start point, then the trend must have ended.
	    //It is not valid to use a low from a previous
	    //trend to determine stop loss. Therefore, use the current low. 
	    if (data[i] > startVal && count >= 2) {
		return thisLow;
	    }
	    if (count >= 3) {
		return thisLow;
	    }
	}
	return thisLow;
    }
    

    /**
     *
     * Return the index to the first local minimum from the end of the
     * data that may also be a pivot point low, indicative of a breakout.
     *
     * @param  data  An array of price data
     * @return An index to the array
     *
    */
    private int getPivotPoint(double[] data) {
	int i;
	double diff = 0.0;
	int position = -1;
	int localMin = -1;
	double high = Double.MIN_VALUE; 
	double prevHigh = Double.MIN_VALUE;
	double prevPrevHigh = Double.MIN_VALUE;
	int count = 0;
	
	for (i = data.length-1; i >= 0; i--) {
	    if (position == -1) {
		diff = data[i-1] - data[i];
		if (diff > 0.0) {	    
		    localMin = i;
		    position = i;
			high = data[i];
		}
		continue;
	    }
	    //A close above the third high in an established trend
	    //signals the end of the trend
	    if (data[i] < prevPrevHigh && count >= 3) {
		break;
	    }
	    //If we find a datapoint lower than our start point
	    //and no downtrend has been established, reset the search
	    if (data[i] < data[localMin]) {
		localMin = i;
		count = 0;
		high = prevHigh = prevPrevHigh = Double.MIN_VALUE;
	    }
	    if (data[i] > high) {
		prevPrevHigh = prevHigh;
		prevHigh = high;
		high = data[i];
		position = i;
		count++;
	    }
	}
	return localMin;
    }


    /**
       Search backwards from end of data to find a local maximum
       *
       * @param  data An array of price data
       *
       * @return The index of the first local maximum from the end of the data
       *
       */
    private int getHighestHigh(double[] data) {
	int i;
	double diff;
	int position = -1;
	int localMax = -1;
	int count = 0;
	double low, prevLow, prevPrevLow;

	low = prevLow = prevPrevLow = Double.MAX_VALUE;

	for (i = data.length-1; i > 0; i--) {
	    if (position == -1) {
		diff = data[i-1] - data[i];
		if (diff < 0.0) {
		    localMax = i;
		    position = i;
		    low = data[i];
		}
		continue;
	    }
	    //A close above the third low in an established trend
	    //signals the end of the trend
	    if (data[i] > prevPrevLow && count >= 3) {
		break;
	    }
	    //If we find a datapoint higher than our start point
	    //and no uptrend has been established, reset the search
	    if (data[i] > data[localMax]) {
		localMax = i;
		count = 0;
		low = prevLow = prevPrevLow = Double.MAX_VALUE;
	    }
	    if (data[i] < low) {
		prevPrevLow = prevLow;
		prevLow = low;
		low = data[i];
		position = i;
		count++;
	    }
	}
	return localMax;
    }
    
}


