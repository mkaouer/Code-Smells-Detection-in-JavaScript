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

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mov.chart.*;
import org.mov.chart.source.*;
import org.mov.util.*;

/**
 * Simple line graph. This graph is used to draw any kind of line such
 * as day close, day open, day high, day low etc.
 */
public class LineGraph extends AbstractGraph {

    /**
     * Create a new simple line graph.
     *
     * @param	source	the source to render
     */
    public LineGraph(GraphSource source) {
	super(source);
    }

    public void render(Graphics g, Color colour, int xoffset, int yoffset,
		       float horizontalScale, float verticalScale,
		       float bottomLineValue, List xRange) {

	g.setColor(colour);
	GraphTools.renderLine(g, getSource().getGraphable(), xoffset, yoffset, 
			      horizontalScale,
			      verticalScale, bottomLineValue, xRange);
    }

    public String getToolTipText(Comparable x, int yCoordinate, int yoffset,
				 float verticalScale,
				 float bottomLineValue)
    {
	Float y = getY(x);
	
	if(y != null) {
	    int yOfGraph = yoffset - 
		GraphTools.scaleAndFitPoint(y.floatValue(),
					    bottomLineValue, verticalScale);
	    // Its our graph *only* if its within 5 pixels	    
	    if(Math.abs(yCoordinate - yOfGraph) < Graph.TOOL_TIP_BUFFER) 
		return getSource().getToolTipText(x);
	}
	return null;
    }
}
