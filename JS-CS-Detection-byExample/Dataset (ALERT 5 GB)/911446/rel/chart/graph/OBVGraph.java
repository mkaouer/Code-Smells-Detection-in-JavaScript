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
import java.util.Iterator;
import java.util.List;

import nz.org.venice.chart.Graphable;
import nz.org.venice.chart.GraphTools;
import nz.org.venice.chart.source.GraphSource;
import nz.org.venice.util.Locale;

/**
 * On Balance Volume (OBV) graph. This graph is used as a precursor for
 * stock trends. If it goes down its a possible indicator that the
 * stock will too and vice-versa. OBV is calculated by starting with
 * an arbitary constant. To calculate a day's OBV, get the previous
 * day's OBV. If the stock went up that day, add the volume. If the stock
 * went down, minus the volume.
 *
 * @author Andrew Leppard
 */
public class OBVGraph extends AbstractGraph {

    // Initial (arbitrary) value to start from
    private final static double INITIAL_VALUE = 50000D;

    // OBV values ready to graph
    private Graphable obv;

    /**
     * Create a new On Balance Volume (OBV) graph.
     *
     * @param	open	the day open price
     * @param	close	the day close price
     * @param	volume	the day volume
     */
    public OBVGraph(GraphSource open, GraphSource close,
		    GraphSource volume) {
	
	// Use same axis as volume
	super(volume);

	// create OBV
	obv = createOBV(open.getGraphable(), close.getGraphable(),
			volume.getGraphable());
    }

    public void render(Graphics g, Color colour, int xoffset, int yoffset,
		       double horizontalScale, double verticalScale,
		       double topLineValue, double bottomLineValue, 
		       List xRange, 
		       boolean vertOrientation) {

	g.setColor(colour);
	GraphTools.renderLine(g, obv, xoffset, yoffset,
			      horizontalScale,
			      verticalScale, 
			      topLineValue, bottomLineValue, 
			      xRange,
			      vertOrientation);
    }

    public String getToolTipText(Comparable x, int y, int yoffset,
				 double verticalScale,
				 double bottomLineValue)
    {
	return null; // we never give tool tip information
    }

    // Override base class
    public double getHighestY(List x) {
	return obv.getHighestY(x);
    }

    // Override base class
    public double getLowestY(List x) {
	return obv.getLowestY(x);
    }

    /**
     * Create OBV graphable.
     *
     * @param	open	the day open price
     * @param	close	the day close price
     * @param	volume	the day volume
     * @return	the OBV
     */
    public static Graphable createOBV(Graphable open, Graphable close, Graphable volume) {
        double runningOBV = INITIAL_VALUE;
	Graphable obv = new Graphable();

	// All graphables should be in-sync
	Iterator iterator = open.getXRange().iterator();

	while(iterator.hasNext()) {
	    Comparable x = (Comparable)iterator.next();

	    Double dayOpen = open.getY(x);
	    Double dayClose = close.getY(x);
	    Double dayVolume = volume.getY(x);

	    if(dayClose.compareTo(dayOpen) > 0)
		runningOBV += dayVolume.intValue();
	    else if(dayClose.compareTo(dayOpen) < 0)
		runningOBV -= dayVolume.intValue();

	    obv.putY(x, new Double(runningOBV));
	}

	return obv;
    }

    /**
     * Return the name of this graph.
     *
     * @return <code>OBV</code>
     */
    public String getName() {
        return Locale.getString("OBV");
    }

    public boolean isPrimary() {
        return false;
    }
}

