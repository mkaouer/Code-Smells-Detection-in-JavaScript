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
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import nz.org.venice.chart.*;
import nz.org.venice.chart.source.*;
import nz.org.venice.util.Locale;

/**
 * Multiple Moving Average graph. This graph draws 10 simple moving averages.
 * It typically shows two groups - traders and investors.
 * When the two lines meet or interset they indicate agreement between 
 * the two groups
 */
public class MultipleMovingAverageGraph extends AbstractGraph {

    private Vector fastMovingAverages;
    private Vector slowMovingAverages;

    private int periodsFast[] = {3, 5, 8, 10, 15};
    private int periodsSlow[] = {30,35, 40, 45, 50, 60};
    
    
    /**
     * Create a new multiple moving average graph.
     *
     * @param	source	the source to create the moving averages from
     */
    public MultipleMovingAverageGraph(GraphSource source) {

	super(source);
        setSettings(new HashMap());
	
    }

    /**
     * Create a new multiple moving average graph according to Simple Moving Average.
     *
     * @param	source	the source to create two moving averages from
     *
     */
    private void createMultipleMovingAverageGraph(Graphable source) {
	int i;

	fastMovingAverages = new Vector();
	slowMovingAverages = new Vector();

	for (i = 0; i < periodsFast.length; i++) {
	    Graphable avg = MovingAverageGraph.createMovingAverage(source, periodsFast[i]);
	    fastMovingAverages.add(avg); 
	}
	for (i = 0; i < periodsSlow.length; i++) {
	    Graphable avg = MovingAverageGraph.createMovingAverage(source, periodsSlow[i]);
	    slowMovingAverages.add(avg); 
	}
	
    }

    public void render(Graphics g, Color colour, int xoffset, int yoffset,
		       double horizontalScale, double verticalScale,
		       double topLineValue, double bottomLineValue, 
		       List dates, 
		       boolean vertOrientation) {

	// We ignore the graph colours and use our own custom colours

	g.setColor(Color.green.darker());
	Iterator iterator = fastMovingAverages.iterator();
	while (iterator.hasNext()) {
	    Graphable avg = (Graphable)iterator.next();
	    GraphTools.renderLine(g, avg, xoffset, yoffset,
				  horizontalScale,
				  verticalScale, 
				  topLineValue, bottomLineValue, dates, 
				  vertOrientation);
	    
	}
	g.setColor(Color.red.darker());
	iterator = slowMovingAverages.iterator();
	while (iterator.hasNext()) {
	    Graphable avg = (Graphable)iterator.next();
	    GraphTools.renderLine(g, avg, xoffset, yoffset,
				  horizontalScale,
				  verticalScale, 
				  topLineValue, bottomLineValue, 
				  dates, 
				  vertOrientation);
	}
	
    }

    public String getToolTipText(Comparable x, int y, int yoffset,
				 double verticalScale,
				 double bottomLineValue)
    {
	return null; // we never give tool tip information
    }

    // Highest Y value is the highest of all the moving averages
    public double getHighestY(List x) {
	
	Iterator iterator = fastMovingAverages.iterator();
	double max = Double.MIN_VALUE;
	double value = 0.0;

	while (iterator.hasNext()) {
	    Graphable avg = (Graphable)iterator.next();
	    value = avg.getHighestY(x);

	    if (value > max) {
		max = value;
	    }
	}
	iterator = slowMovingAverages.iterator();
	while (iterator.hasNext()) {
	    Graphable avg = (Graphable)iterator.next();
	    value = avg.getHighestY(x);
	    if (value > max) {
		max = value;
	    }
	}
	return max;
    }

    // Lowest Y value is the lowest of both the moving averages
    public double getLowestY(List x) {
	
	Iterator iterator = fastMovingAverages.iterator();
	double min = Double.MAX_VALUE;
	double value = 0.0;

	while (iterator.hasNext()) {
	    Graphable avg = (Graphable)iterator.next();
	    value = avg.getLowestY(x);

	    if (value < min) {
		min = value;
	    }
	}
	iterator = slowMovingAverages.iterator();
	while (iterator.hasNext()) {
	    Graphable avg = (Graphable)iterator.next();
	    value = avg.getLowestY(x);
	    if (value < min) {
		min = value;
	    }
	}
	return min;
    }

    /**
     * Return the name of this graph.
     *
     * @return	<code>MultipleMovingAverage</code>
     */
    public String getName() {
	return Locale.getString("MULT_MOVING_AVERAGE");
    }

    public boolean isPrimary() {
        return true;
    }

    public void setSettings(HashMap settings) {
        super.setSettings(settings);
        
        createMultipleMovingAverageGraph(getSource().getGraphable());
    }

    /**
     * Return the graph's user interface.
     *
     * @param settings the initial settings
     * @return user interface
     */
    public GraphUI getUI(HashMap settings) {
        return null;
    }
}



