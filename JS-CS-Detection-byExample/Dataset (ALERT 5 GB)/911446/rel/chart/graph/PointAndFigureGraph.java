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

package nz.org.venice.chart.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import nz.org.venice.chart.Graphable;
import nz.org.venice.chart.PFGraphable;
import nz.org.venice.chart.PFData;
import nz.org.venice.chart.GraphTools;
import nz.org.venice.chart.source.GraphSource;
import nz.org.venice.quote.QuoteFunctions;
import nz.org.venice.util.Locale;
import nz.org.venice.ui.QuoteFormat;
import nz.org.venice.util.TradingDate;

/**
 * Point and Figure graph. This graph draws a series of characters (X/O)
 * mapping the general movement. A change in column shows a reversal
 * such that price difference met the price scale.
 *
 * @author Mark Hummel
 * @see PointAndFigureGraphUI
 */
public class PointAndFigureGraph extends AbstractGraph {

    // Point and Figure ready to graph
    private PFGraphable pointAndFigure;

    /**
     * Create a new point and figure graph.
     *
     * @param	source	the source to create the point and figure from
     */
    public PointAndFigureGraph(GraphSource source) {
	super(source);
        setSettings(new HashMap());
    }

    /**
     * Create a new point and figure graph.
     *
     * @param	source	the source to create the point and figure from
     * @param	settings  the settings for the graph
     */
    public PointAndFigureGraph(GraphSource source, HashMap settings) {	
	super(source);
        super.setSettings(settings);

	double priceReversalScale = 0.0;
	double boxPriceScale = 0.0;

	// Retrieve values from hashmap
        String priceReversalString = (String)settings.get(PointAndFigureGraphUI.PRICE_REVERSAL_SCALE);
        String boxString = (String)settings.get(PointAndFigureGraphUI.BOX_PRICE_SCALE);
	
	if (priceReversalString != null &&
	    boxString != null) {
	    try {
		priceReversalScale = Double.parseDouble(priceReversalString);
		boxPriceScale = Double.parseDouble(boxString);

		// Create point and figure graphable

		pointAndFigure = 
		    createPointAndFigureGraph(source.getGraphable(), 
					      priceReversalScale, 
					      boxPriceScale);
	    } catch(NumberFormatException e) {
		//Value should be already be checked
		assert false;
	    }
	}


    }
    
    public void render(Graphics g, Color colour, int xoffset, int yoffset,
		       double horizontalScale, double verticalScale,
		       double topLineValue, double bottomLineValue, 
		       List xRange, 
		       boolean vertOrientation) {

	// We ignore the graph colours and use our own custom colours
	g.setColor(Color.green.darker());
	GraphTools.renderMarker(g, pointAndFigure, xoffset, yoffset,
				horizontalScale,
				verticalScale, 
				topLineValue, bottomLineValue, xRange, 
				vertOrientation);
	
    }


    /**
     * Get the tool tip text for the given X value and y coordinate.
     *
     * @param	x	the X value
     * @param	y	the y coordinate
     * @param	yoffset	y offset from top of graph
     * @param	verticalScale	vertical scale factor
     * @param	bottomLineValue	the Y value of the lowest line in the graph
     * @return	the tool tip text for the default <code>GraphSource</code>
     */

    /*
      We currently return the price value of the top of an upmove
      and the bottom of a downmove rather than returning all points
      and dates in between.      
      
    */
    public String getToolTipText(Comparable x, int y, int yoffset,
				 double verticalScale,
				 double bottomLineValue) {

	Vector list = null;
	Double value = null;
	String text = "";
	int index;
	boolean upmove = true;
	
	if (pointAndFigure == null || x == null) {
	    return null;
	}

	String tmp = pointAndFigure.getString(x);
	if (tmp == null) {
	    return null;
	}
	
	if (tmp.compareTo("X") == 0) {
	    upmove = true;
	} else {
	    upmove = false;
	}
	
	list = pointAndFigure.getYList(x);
	if (list == null) {
	    return null;
	}

	index = (upmove) ? 
	    getLargestElement(list) : 
	    getSmallestElement(list);
	    
	if (index < list.size()) {
	    value = (Double)(list.elementAt(index));
	    TradingDate date = pointAndFigure.getDate(x);
	    
	    if (value == null) {
		return null;
	    }
	    
	    text = "<html>" + 
		getSource().getName() + 
		"," +
		date.toLongString() +
		"<p>";
	    
	    text += QuoteFormat.quoteToString(value.doubleValue()) + "</p></html>";
	    
	}

	return text;
    }
    

    // Highest Y value is in the moving average graph
    public double getHighestY(List x) {
	return pointAndFigure.getHighestY(x);
    }

    // Lowest Y value is in the moving average graph
    public double getLowestY(List x) {
	return pointAndFigure.getLowestY(x);
    }

    /**
     * Return the name of this graph.
     *
     * @return	<code>Point and Figure</code>
     */
    public String getName() {
	return Locale.getString("POINT_AND_FIGURE");
    }

    /**
     * Creates a new poing and figure graphable based on the given data source.
     *
     * <p>Rules of Point and Figure:
     *
     * <ul>
     * <li><code>X</code> = Upmoves, <code>O</code> = DownMoves</li>
     * <li>Stay in the same column until price changes direction, and then move one column
     *    to the right.</li>
     * <li>Plot only prices which meet the price scale. A plot is marked regardless
     *    of the direction. If a direction change occurs, the affect is to move the
     *    column one to the right.</li>
     * </ul>
     *
     * @param	source	the graph source to average
     * @return	the graphable containing averaged data from the source
     */
    public static PFGraphable createPointAndFigureGraph(Graphable source,
                                                        double priceReversalScale,
							double boxPriceScale) {
	PFGraphable pointAndFigure = new PFGraphable();

	// Date set and value array will be in sync
	double[] values = source.toArray();
	Vector yList = new Vector();
	Vector pfData = new Vector();
		
	Set xRange = source.getXRange();
	Set column = source.getXRange(); // Associate Column with date
	int columnNumber = 0, columnSpan = 1; 
	int dataLen = xRange.size();
	Iterator iterator = xRange.iterator();
	Iterator iterator2 = column.iterator();
	Iterator tempIterator;
	double diff, prev = 0.0;
	boolean plot; // Indicate whether this point gets plotted
	boolean upmove = true, changeDirection = false;	
	String marker;
	Comparable date, startDate;	
	
	int i = 0;	
	double average;	
		
	upmove = getFirstDirection(source, boxPriceScale);
		
	startDate = (Comparable)iterator2.next();	
	
	while(iterator.hasNext()) {
	    date = (Comparable)iterator.next();

	    diff = (i == 0)  ? 0 : (values[i] - prev);
	    plot = (Math.abs(diff) >= boxPriceScale) ? true : false;

	    // Now check the direction
	    if (plot) {
		if (Math.abs(diff) >= priceReversalScale) {
		    if (upmove == true && diff < 0) {
			changeDirection = true;
		    } else if (upmove == false && diff > 0) {
			changeDirection = true;
		    } else {
			changeDirection = false;		
		    }
		} else {
		    changeDirection = false;
		}
		
		// Stay in the same column until theres a change in direction
		if (changeDirection) {

		    // Associate a new set of points.
		    yList = new Vector();

		    upmove = (upmove) ? false: true;
		    columnNumber++;
		
		}
		
		marker = (upmove) ? "X" : "O";
		
		/*Rules for generating PF state that when 
		  price moves by greater than twice the box value
		  the inteverning boxes are marked.
		*/

		/* 
		   When prev = 0.0, no plot has yet been made.
		   We avoid putting in intervening marks in this
		   case to avoid a large column of 'X's arising out of
		   0. 
		*/

		diff = Math.abs(diff) - boxPriceScale;
		int counter = 1;	
		if (prev != 0.0) {
		    while (diff  >= 0.0) {		    
			double tmp = (upmove) ? values[i] - (boxPriceScale * counter) : values[i] + (boxPriceScale * counter);

			double tmp2 = 100 * boxPriceScale;
			double tmp3 = tmp * 100;
			
			/* Round values to fit in box value
			   operands multiplied by  by 100 and then
			   result divided by 100 to avoid round off
			   error.
			 */		 
			double rem = ( (tmp3) % tmp2) / 100.0;

			if (upmove) {
			    rem = boxPriceScale - rem;
			} else {
			    rem = -rem;
			}

			tmp += rem;
			Double yTemp = new Double(tmp);


			yList.add(yTemp);
			diff = Math.abs(diff) - boxPriceScale;
			counter++;		
		    }
		}

		/*Needs more data than just price and column for tooltips,
		  because in vanilla P&F, no passage of time is indicated. */
		PFData data = new PFData(date, yList, marker);		
		
		if (pfData.size() <= columnNumber) {
		    pfData.add(data); 
		} else {
		    pfData.setElementAt(data, columnNumber);
		}
	    }
	    //The price meeting the box value is defined by the last 
	    //marker placed, which is not necessarily the previous value.
	    if (plot) {
		prev = values[i];	
	    }
	    
	    i++;
	}

	/* 
	   Column span is reduced to create a gap, which 
	   makes graph look less cluttered.

	   The point of spanning data across date "columns"
	   is so that in the Graphable each date associates
	   with a price point.
	*/

	if (columnNumber == 0) {
	    columnNumber = 1;
	}
	columnSpan = (int)(dataLen / columnNumber);		
	pointAndFigure.setColumnSpan(columnSpan - 2);

	pointAndFigure.setBoxPrice(boxPriceScale);
	tempIterator = column.iterator();
	date = startDate;
		
	for (i = 0; i <= columnNumber; i++) {
	    PFData data = (PFData)pfData.elementAt(i);
	    
	    pointAndFigure.putData(date,
				   data.getDate(),
				   data.getList(),
				   data.getMarker());
 
	    date = dateIncrement(tempIterator, columnSpan);
	}

	return pointAndFigure;
    }

    public boolean isPrimary() {
        return true;
    }

    /**
     * Calculates default price scale based on graph source.
     *
     * @return default price scale
     */
    private double calculateDefaultPriceReversalScale() {
        double defaultPriceReversalScale;


        // Heuristic for a starting value for the default price scale
        Graphable graphable = getSource().getGraphable();
        double[] values = graphable.toArray();

	// Default = 3 box reversal
	defaultPriceReversalScale = calculateDefaultBoxPriceScale() * 3;
        defaultPriceReversalScale = QuoteFunctions.roundDouble(defaultPriceReversalScale, 2);
        return defaultPriceReversalScale;
    }

    private double calculateDefaultBoxPriceScale() {
        double defaultBoxPriceScale;
	PFGraphable temp;
	Graphable graphable = getSource().getGraphable();
	Set xRange = graphable.getXRange();
	int numColumns = 0;
	int minColumns = 10;
	double columnDataRatio = 0.04; //fudge factor 
	int dataLen;
	int columnSpan;
	
        // Heuristic for a starting value for the default price scale
	// Use 1 percent of the current price.
        double[] values = graphable.toArray();

	defaultBoxPriceScale = values[values.length-1] / 100;
        
	if (defaultBoxPriceScale <= 0.0) {
	    defaultBoxPriceScale = 0.01;

       }

	dataLen = xRange.size();
	minColumns = (int)(columnDataRatio * dataLen);

	//Make sure the number of columns generated by the settings
	//is not so small as to hide all the detail in the chart    

	while (numColumns < minColumns) {
	    temp = createPointAndFigureGraph(getSource().getGraphable(),
					     defaultBoxPriceScale * 3.0,
					     defaultBoxPriceScale);

	    columnSpan = temp.getColumnSpan(); 
	    if (columnSpan == 0) {
		columnSpan = 1;
	    }
	    numColumns = (int)(dataLen / columnSpan);

	    if (numColumns < minColumns) {
		defaultBoxPriceScale /= 2.0;
	    }
	}
	
	
	defaultBoxPriceScale = QuoteFunctions.roundDouble(defaultBoxPriceScale, 2);

	if (defaultBoxPriceScale <= 0.0) {
	    defaultBoxPriceScale = 0.01;
	}

        return defaultBoxPriceScale;
    }



    public void setSettings(HashMap settings) {
        super.setSettings(settings);

        // Calculate default price scale from data
        double defaultPriceReversalScale = calculateDefaultPriceReversalScale();        double defaultBoxPriceScale = calculateDefaultBoxPriceScale();

        // Retrieve values from hashmap
        double priceReversalScale = PointAndFigureGraphUI.getPriceReversalScale(settings, defaultPriceReversalScale);
        double boxPriceScale = PointAndFigureGraphUI.getBoxPriceScale(settings, defaultBoxPriceScale);	

	// Create point and figure graphable
	pointAndFigure = createPointAndFigureGraph(getSource().getGraphable(), priceReversalScale, boxPriceScale);
    }

    /**
     * Return the graph's user interface.
     *
     * @param settings the initial settings
     * @return user interface
     */
    public GraphUI getUI(HashMap settings) {
        return new PointAndFigureGraphUI(settings, 
					 calculateDefaultPriceReversalScale(),
					 calculateDefaultBoxPriceScale());
    }

    // Return true if the stock is moving upwards, false otherwise.
    private static boolean getFirstDirection(Graphable source, double boxValue) {
	Set xRange = source.getXRange();
	Iterator iterator = xRange.iterator();
	double diff, prev = 0.0;
	int i = 0;
	double[] values = source.toArray();
	
	while(iterator.hasNext()) {
	    Comparable x = (Comparable)iterator.next();
	    
	    diff = (i <= 1)  ? 0 : (values[i] - prev);
	    
	    if (Math.abs(diff) >= boxValue) {
		prev = values[i];
	    }
	    
	    i++;
	    
	    if (Math.abs(diff) >= boxValue) {
		if (diff > 0) {
		    return true;
		}
		if (diff < 0) {
		    return false;
		}
	    }
	}
	
	return true;
    }


    // Wheel reinvention.
    // Surely these functions exist in the Java API?  
    private int getSmallestElement(Vector v) {
	int i = 0;
	int rv = 0;
	double min = Double.MAX_VALUE, e = 0.0;
	
	for (i = 0; i < v.size(); i++) {
	    e = ((Double)(v.elementAt(i))).doubleValue();
	    if (e < min) {
		min = e;
		rv = i;
	    }
	}	       
	return rv;
    }

    private int getLargestElement(Vector v) {	
	int i = 0;
	int rv = 0;
	double max = Double.MIN_VALUE, e = 0.0;

	for (i = 0; i < v.size(); i++) {
	    e = ((Double)(v.elementAt(i))).doubleValue();
	    if (e > max) {
		max = e;
		rv = i;
	    }
	}
	return rv;
    }

    private static Comparable dateIncrement(Iterator iterator, int op) {
	int i = 0;
	Comparable rv = null;

	for (i = 0;  i < op && iterator.hasNext(); i++) {
	    rv = (Comparable)iterator.next();
	}
	return rv;
    }

    public boolean dataAvailable(Vector xRange) {	
	return pointAndFigure.dataAvailable(xRange);
    }

}



