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

package nz.org.venice.chart;

import nz.org.venice.chart.graph.*;

import java.awt.*;

public class VerticalAxis {

    //
    // Constants that define the look and feel of the axis
    //

    // Minimum number of horizontal lines across graph
    static final int MINIMUM_HORIZONTAL_LINES = 4;

    // Maximum distance between horizontal lines in graph
    static final int MAX_HORIZONTAL_LINE_SPACING = 125;

    // Distance between horizontal line and Y label text
    static final int Y_LABEL_VERTICAL_OFFSET = 5;

    // Distance between right hand side of the graph and end of Y label text
    static final int Y_LABEL_HORIZONTAL_OFFSET = 10;

    // Space assigned for Y label text
    static final int Y_LABEL_WIDTH = 60;

    private double lowest;
    private double highest;
    private double[] minor;
    private double[] major;

    // Graph dimensions
    private int height = 0;
    private int horizontalLines;
    private int horizontalLineSpacing;
    private double valueDelta;
    private double bottomLineValue;
    private double topLineValue;
    private int heightOfGraph;
    private double scale;

    public VerticalAxis(double lowest, double highest,
			double[] minor, double[] major) {
	this.lowest = lowest;
	this.highest = highest;
	this.minor = minor;
	this.major = major;
    }

    public void setHeight(int height) {
	// now calculate dimensions of graph (if its changed)
	if(this.height != height) {
	    this.height = height;

	    calculateHorizontalLinesAndSpacing();
	    calculateOptimumValueDelta();
	    calculateHeightOfGraph();
	    calculateScale();
	}
    }

    private void calculateHorizontalLinesAndSpacing() {
	horizontalLines = MINIMUM_HORIZONTAL_LINES;
	
	do {	
	    horizontalLineSpacing = height / (++horizontalLines - 1);
	} while(horizontalLineSpacing > MAX_HORIZONTAL_LINE_SPACING);
    }

    private void calculateOptimumValueDelta() {
	
	// Get the *exact* amount to fit the graph perfectly but we hardly
	// want to draw a line every 2.531c
	double exactWorth = Math.abs((highest - lowest) / (horizontalLines - 1));

	// Get the smallest valueDelta that is bigger than the exact
	// needed valueDelta AND such that the top line value would be
	// greater than the highest share value
	for(int i = 0; i < major.length; i++) {
	    for(int j = 0; j < minor.length; j++) {

		valueDelta = major[i] * minor[j];

		bottomLineValue = (double)Math.floor(lowest / valueDelta) *
		    valueDelta;
		topLineValue = bottomLineValue + (horizontalLines - 1) *
		    valueDelta;

		if(valueDelta > exactWorth && topLineValue > highest)
		    return;
	    }	
	}

	// shouldnt get here
        assert false;

	valueDelta = 10000000.0D;
    }

    private void calculateHeightOfGraph() {
	heightOfGraph = (horizontalLines-1)*horizontalLineSpacing;
    }

    private void calculateScale() {
	scale = heightOfGraph / (topLineValue - bottomLineValue);
    }

    public double getBottomLineValue() {
	return bottomLineValue;
    }

    public double getTopLineValue() {
	return topLineValue;
    }

    public int getHeightOfGraph() {
	return heightOfGraph;
    }

    public double getScale() {
	return scale;
    }

    public void drawGridAndLabels(Graphics g, Graph graph, String title,
				  int xoffset, int yoffset, int width) {

	String yLabel;
	int yLabelWidth;
	int y;
	int yLabelVerticalOffset = Y_LABEL_VERTICAL_OFFSET;
	Font originalFont = g.getFont();
	boolean everySecondLine = false;
	boolean firstLineOnly = false;

	// If the lines get squished too close together well put the
	// label text right next to its line because we are running out
	// of space
	if((yLabelVerticalOffset + g.getFontMetrics().getHeight())
	   > horizontalLineSpacing)
	    yLabelVerticalOffset = 1;
	
	// Panic mode - if its still too small switch to a smaller font
	// say 2 points down and have no vertical offset
	if(g.getFontMetrics().getHeight() > horizontalLineSpacing) {
	    g.setFont(g.getFont().deriveFont(g.getFont().getSize2D() - 2));
	    yLabelVerticalOffset = 0;
	}

	// If its still too small and weve absolutely no room left
	// only print every second line
	if(g.getFontMetrics().getHeight() > horizontalLineSpacing + 2)
	    everySecondLine = true;

	// Finally if theres no room at all well only print it on the first
	// line
	if(g.getFontMetrics().getHeight() > 2 * horizontalLineSpacing) {
	    everySecondLine = false;
	    firstLineOnly = true;
	}

	for(int i = 0; i < horizontalLines; i++) {
	
	    y = i*horizontalLineSpacing + yoffset;

	    yLabel =
		graph.getYLabel(topLineValue - valueDelta * i);

	    yLabelWidth = g.getFontMetrics().stringWidth(yLabel);
	    g.drawLine(xoffset, y, xoffset + width + Y_LABEL_WIDTH, y);

	    // Write Y label unless one of the following is truee

	    // Dont write text if the 'first line only' mode is on and this
	    // isnt the first line
	    if(firstLineOnly && i != 0)
		continue;

	    // Dont write text if we are in 'every second line mode' and this
	    // isnt the second line
	    if(everySecondLine && (i % 2) != 0)
		continue;

	    g.drawString(yLabel, xoffset + width + Y_LABEL_WIDTH -
			 yLabelWidth - Y_LABEL_HORIZONTAL_OFFSET,
			 y - yLabelVerticalOffset);

	    // draw title
	    if(i == 0)
		g.drawString(title,
			     xoffset + Y_LABEL_HORIZONTAL_OFFSET,
			     y - yLabelVerticalOffset);
	}
	
	// Reset font
	g.setFont(originalFont);
    }
}
