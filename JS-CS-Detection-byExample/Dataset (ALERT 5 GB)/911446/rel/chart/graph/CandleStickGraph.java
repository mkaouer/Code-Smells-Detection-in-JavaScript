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

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import nz.org.venice.chart.*;
import nz.org.venice.chart.source.*;
import nz.org.venice.util.*;

/**
 * Candlestick graph. This graph draws a single vertical line between the
 * day low and the day high values and draws a candle between the day open and
 * the day close.
 *
 * @author Quentin Bossard
 */
public class CandleStickGraph extends AbstractGraph {

    private Graphable dayOpen;
    private Graphable dayLow;
    private Graphable dayHigh;
    private Graphable dayClose;

    /**
     * Create a new Candlestick graph.
     *
     * @param	dayOpen	source containing the day open values
     * @param	dayLow	source containing the day low values
     * @param	dayHigh	source containing the day high values
     * @param	dayClose source containing the day close values
     */
    public CandleStickGraph(GraphSource dayOpen, GraphSource dayLow, GraphSource dayHigh,
                            GraphSource dayClose) {
	super(dayClose);

	this.dayOpen = dayOpen.getGraphable();
	this.dayLow = dayLow.getGraphable();
	this.dayHigh = dayHigh.getGraphable();
	this.dayClose = dayClose.getGraphable();
    }

	// See Graph.java
    public void render(Graphics g, Color c, int xoffset, int yoffset,
        double horizontalScale, double verticalScale, 
		       double topLineValue, double bottomLineValue,
		       List xRange, boolean vertOrientation) {

        int xCoordinate, lowY, highY, closeY, openY;
        Double dayLowY, dayHighY, dayCloseY, dayOpenY;
        Iterator iterator = xRange.iterator();
        int i = 0;
        int halfbarWidth=(int)(0.309 * horizontalScale);//bryan
        int halfBlankWidth=(int) (horizontalScale-halfbarWidth*2)/2;//bryan
	int vertDirection = (vertOrientation) ? 1 : -1;

        while (iterator.hasNext()) {

            Comparable x = (Comparable) iterator.next();

            // Skip until our start date
            if (x.compareTo(dayClose.getStartX()) < 0) {
              i++;
              continue;
            }

            // If our graph is finished exit this loop
            if (x.compareTo(dayClose.getEndX()) > 0)
                break;

            // Otherwise draw bar
            dayOpenY = dayOpen.getY(x);
            dayLowY = dayLow.getY(x);
            dayHighY = dayHigh.getY(x);
            dayCloseY = dayClose.getY(x);

            // The graph is allowed to skip points
            if (dayLowY != null && dayHighY != null && dayCloseY != null
                && dayOpen != null) {

                xCoordinate = (int) (xoffset + horizontalScale * i);

		openY = GraphTools.calcYCoord(yoffset, dayOpenY.doubleValue(),
					      topLineValue, bottomLineValue, 
					      verticalScale,
					      vertDirection);

		lowY = GraphTools.calcYCoord(yoffset, dayLowY.doubleValue(),
					     topLineValue, bottomLineValue, 
					     verticalScale,
					     vertDirection);

		highY = GraphTools.calcYCoord(yoffset, dayHighY.doubleValue(),
					      topLineValue, bottomLineValue, 
					      verticalScale,
					      vertDirection);

		closeY = GraphTools.calcYCoord(yoffset, dayCloseY.doubleValue(),
					       topLineValue, bottomLineValue,
					       verticalScale,
					       vertDirection);

                // Draw bar
                if (closeY > openY) { // red candle : open higher than close
                    g.setColor(Color.RED);
                    g.drawRect(xCoordinate +halfBlankWidth, openY,	halfbarWidth*2+1, closeY-openY);
                    g.drawLine(xCoordinate+halfbarWidth+1 +halfBlankWidth, lowY, xCoordinate+halfbarWidth+1 +halfBlankWidth, closeY);
                    g.drawLine(xCoordinate+halfbarWidth+1 +halfBlankWidth, openY, xCoordinate+halfbarWidth+1 +halfBlankWidth, highY);
                } else if (closeY < openY) { // green candle : close higher than open
                    g.setColor(Color.GREEN);
                    g.drawRect(xCoordinate +halfBlankWidth, closeY, halfbarWidth*2+1, openY-closeY);
                    g.drawLine(xCoordinate+halfbarWidth+1 +halfBlankWidth, lowY, xCoordinate+halfbarWidth+1 +halfBlankWidth, openY);
                    g.drawLine(xCoordinate+halfbarWidth+1 +halfBlankWidth, closeY, xCoordinate+halfbarWidth+1 +halfBlankWidth, highY);
                } else { // no candle
                    g.setColor(Color.RED);
                    g.drawLine(xCoordinate+halfbarWidth+1 +halfBlankWidth, lowY, xCoordinate+halfbarWidth+1 +halfBlankWidth, highY);
                   g.drawLine(xCoordinate +halfBlankWidth , openY,
                    		xCoordinate+halfbarWidth*2+1 +halfBlankWidth, openY);

                }
            }
            i++;
        }
    }

    /**
     * Get the tool tip text for the given X value and y coordinate.
     *
     * @param	x	the X value
     * @param	y	the y coordinate
     * @param	yoffset	y offset from top of graph
     * @param	verticalScale	vertical scale factor
     * @param	bottomLineValue	the Y value of the lowest line in the graph
     * @return	tool tip text containing the day low, day high and day close
     *		quotes
     */
    public String getToolTipText(Comparable x, int y, int yoffset,
				 double verticalScale,
				 double bottomLineValue) {
	Double dayLowY = dayLow.getY(x);
	Double dayHighY = dayHigh.getY(x);
	
	if(dayLowY != null && dayHighY != null) {

	    int dayLowYCoordinate = yoffset -
		GraphTools.scaleAndFitPoint(dayLowY.doubleValue(),
					    bottomLineValue, verticalScale);

	    int dayHighYCoordinate = yoffset -
		GraphTools.scaleAndFitPoint(dayHighY.doubleValue(),
					    bottomLineValue, verticalScale);
	
	    // Its our graph if its within TOOL_TIP_BUFFER pixels of the
	    // line from day low to day high
//	    if(y >= (dayLowYCoordinate - Graph.TOOL_TIP_BUFFER) &&
//	       y <= (dayHighYCoordinate + Graph.TOOL_TIP_BUFFER))
	    if(y <= (dayLowYCoordinate + Graph.TOOL_TIP_BUFFER) &&
		 	       y >= (dayHighYCoordinate - Graph.TOOL_TIP_BUFFER))
	    return getSource().getToolTipText(x);
	}
	return null;
    }

    // Highest value will always be in the day high source
    public double getHighestY(List x) {
	return dayHigh.getHighestY(x);
    }

    // Lowest value will always be in the day low source
    public double getLowestY(List x) {
        return dayLow.getLowestY(x);
    }

    /**
     * Return the name of this graph.
     *
     * @return <code>Candle Stick</code>
     */
    public String getName() {
        return Locale.getString("CANDLE_STICK");
    }

    public boolean isPrimary() {
        return true;
    }
}
