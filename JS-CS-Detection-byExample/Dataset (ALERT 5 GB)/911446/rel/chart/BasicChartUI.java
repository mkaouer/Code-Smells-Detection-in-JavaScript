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

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.*;

import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.chart.graph.*;

/**
 * Chart Implementation.
 */
public class BasicChartUI extends ComponentUI implements ImageObserver  {
    
    //
    // Constants that define the look and feel of the graph
    //

    // Buffer space between start of pimary graph and first line. Multiply
    // this number by the height of the font to get the space
    private static final double TOP_GRAPH_BUFFER_MULTIPLIER = 1.5;

    // Space assigned for X label
    private static final int X_LABEL_HEIGHT = 40;

    // Space assigned for Y label text
    private static final int Y_LABEL_WIDTH = 60;

    // Minimum height of graph level in pixels (not including X lables)
    private static final int MINIMUM_LEVEL_HEIGHT = 50;

    // Horizontal margin in annotated text pop-up boxes (in pixels)
    private static final int ANNOTATED_TEXT_MARGIN = 5;

    //
    // Other constants
    //

    // Trading month has a minimum of this many trading days (roughly :)
    private static final int MINIMUM_TRADING_DAYS_IN_MONTH = 20;

    // When buffering an image, we create a buffer area around (in pixels)
    private static final int BUFFER_BUFFER_SIZE = 200;

    // Ratio of primary level to secondary level size (secondary graph
    // levels such as volume graphs try to be 1/4 of the size of the primary
    // e.g. day close graphs)
    private static final int PRIMARY_HEIGHT_UNITS = 4;
    private static final int SECONDARY_HEIGHT_UNITS = 1;

    // These variables are the same for each graph we draw
    private double horizontalScale;
    private int firstHorizontalLine;

    // We buffer the graph image for speed so we dont have to recalculate
    // it each time the user moves the scroll bar
    private BufferedImage image = null;
    private int bufferWidth = 0;
    private int bufferHeight = 0;
    private Vector levelHeights;

    // Also precompute data that doesnt change on resizing
    private HorizontalAxis quartersHorizontalAxis = null;
    private HorizontalAxis monthsHorizontalAxis = null;
    private HorizontalAxis majorHorizontalAxis = null;
    private HorizontalAxis minorHorizontalAxis = null;
    private Vector verticalAxes = null;
    private HashMap colourMap = null;

    // Remember these variables last values as they are needed for
    // tool tip generation
    private int xoffset, yoffset;

    /**
     * Return the minimum width needed by this component.
     *
     * @param	chart	the chart component
     */
    public static int getMinimumWidth(Chart chart) {
	// The minimum width is merely the length of this plus some
	// space for the Y axis labels. The minimum height is fixed but
	// may or may not include the X axis labels height.
	if(chart.getXRange() != null)
	    return chart.getXRange().size() + Y_LABEL_WIDTH;
	else
	    return Y_LABEL_WIDTH;
    }

    /**
     * Return the minimum height needed by this component.
     *
     * @param	chart	the chart component
     */
    public static int getMinimumHeight(Chart chart) {
	// Levels of graphs
	int levels = chart.getLevels().size();
	
	return MINIMUM_LEVEL_HEIGHT * levels + X_LABEL_HEIGHT;
    }

    public static ComponentUI createUI(JComponent c) {
	return new BasicChartUI();
    }

    /**
     * Return the X value at the given x coordinate.
     *
     * @param	chart	the chart component
     * @param	xCoordinate	an x coordinate on the screen
     * @return	the X value at the x coordinate
     */
    public Comparable getXAtPoint(Chart chart, int xCoordinate) {

	Comparable x = null;

       	// First find the X at this point
	int xOffset = (int)(xCoordinate / horizontalScale);

	if(xOffset >= chart.getXRange().size())
	    xOffset = chart.getXRange().size()-1;
	if(xOffset < 0)
	    xOffset = 0;

	if(chart.getXRange() != null && xOffset < chart.getXRange().size())
	    x = (Comparable)chart.getXRange().elementAt(xOffset);
	
	return x;
    }

    /**
     * Return the Y value at the given x coordinate.
     *
     * @param	chart	the chart component
     * @param	yCoordinate	an y coordinate on the screen
     * @return	the Y value at the y coordinate
     */
    public synchronized Double getYAtPoint(Chart chart, int yCoordinate) {

	/* calculate the verticalScale for the level at this point */

	// Get graph level at this point
	int level = getLevelAtPoint(yCoordinate);
	// Get vertical axis of graph level
	
	VerticalAxis verticalAxis =
	    (VerticalAxis)verticalAxes.elementAt(level);
	int yoffset = getStartOfLevel(level) + firstHorizontalLine + 
	    ((Integer)levelHeights.elementAt(level)).intValue();
	double verticalScale = verticalAxis.getScale();
	double height = verticalAxis.getHeightOfGraph();
	double bottomLineValue = verticalAxis.getBottomLineValue();
		

	double dataValue = GraphTools.getPointFromScale(yCoordinate, 
							 bottomLineValue,
							 yoffset,
							 verticalScale);
	return new Double(dataValue);
    }

    /**
     * Return which graph level contains the given y coordinate.
     *
     * @param	yCoordinate	the y coordinate to query
     * @return	the level which contains the y coordinate or <code>0</code>
     *		if none do
     */
    protected int getLevelAtPoint(int yCoordinate) {
	
	Iterator iterator = levelHeights.iterator();
	int level = 0;
	int yoffset = 0;
	int levelHeight;

	while(iterator.hasNext()) {
	    yoffset += ((Integer)iterator.next()).intValue();
	    if(yCoordinate <= yoffset)
		return level;
	    level++;
	}
       	return 0;
    }

    /**
     * Return the start y offset for the given level.
     *
     * @param	level	the level
     * @return	the start y offset
     */
    private int getStartOfLevel(int level) {
	int yoffset = 0;

	if(level > 0)
	    for(int i = 0; i < level-1; i++) {
		yoffset += ((Integer)levelHeights.elementAt(i)).intValue();
	    }
	
	return yoffset;
    }

    /**
     * Return the tool tip text for the given x, y coordinates.
     *
     * @param	chart	the chart component
     * @param	xCoordinate	the x coordinate
     * @param	yCoordinate	the y coordinate
     */
    public String getToolTipText(Chart chart, int xCoordinate,
				 int yCoordinate) {

	// Abort if some of our variables are not set yet
	if(verticalAxes == null)
	    return null;

	Insets insets = chart.getInsets();
	int height = chart.getHeight() - insets.top - insets.bottom;

	// Get x at this point
	Comparable x = getXAtPoint(chart, xCoordinate);
	// Get graph level at this point
	int level = getLevelAtPoint(yCoordinate);
	// Get vertical axis of graph level
	VerticalAxis verticalAxis =
	    (VerticalAxis)verticalAxes.elementAt(level);
	int yoffset = getStartOfLevel(level) + firstHorizontalLine;

	String toolTipText = null;

	if(yCoordinate < (height - X_LABEL_HEIGHT) && verticalAxis != null &&
	   x != null) {

	    // Iterate through all graphs until one of the graph gives us
	    // a tooltip
	    Iterator iterator =
		((Vector)chart.getLevels().elementAt(level)).iterator();
	    Graph graph;

	    while(iterator.hasNext() && toolTipText == null) {
		graph = (Graph)iterator.next();
		
		toolTipText =
		    graph.getToolTipText(x, yCoordinate, yoffset +
					 verticalAxis.getHeightOfGraph(),
					 verticalAxis.getScale(),
					 verticalAxis.getBottomLineValue());
	    }
	}

	return toolTipText;	
    }

    /**
     * Paint the component.
     *
     * @param	g the graphics to paint to
     * @param	c the chart component
     */
    public void paint(Graphics g, JComponent c) {

	// Get size
	Insets insets = c.getInsets();

	int width = c.getWidth() - insets.left - insets.right;
	int height = c.getHeight() - insets.top - insets.bottom;

	// Do we need to allocate a new image buffer? We do if:
	// 1) There isnt one
	// 2) Its not big enough to fit the image
	// 3) Its bigger than the image by more than twice the buffer size
	if(image == null ||
	   image.getWidth() < width || image.getHeight() < height ||
	   image.getWidth() > (width + 2*BUFFER_BUFFER_SIZE) ||
	   image.getHeight() > (height + 2*BUFFER_BUFFER_SIZE)) {
	
	    // Allocate a little more space than we need
	    image =
		new BufferedImage(width + BUFFER_BUFFER_SIZE,
				  height + BUFFER_BUFFER_SIZE,
				  BufferedImage.TYPE_3BYTE_BGR);	
	}

	ChartDrawingModel cdm = ((Chart)c).getChartDrawingModel();
	
	
	// Draw it iff the size has changed
	if(width != bufferWidth || 
	   height != bufferHeight ||
	   cdm.dataExists()) {	    

	    bufferedPaint(image.getGraphics(), (Chart)c, width, height);
	
	    bufferWidth = width;
	    bufferHeight = height;
	    	    
	}	

	// Copy buffer to screen
	g.drawImage(image, 0, 0, this);
	
	// Finally highlight region
	highlightRegion(g, (Chart)c, height);

    }

    public BufferedImage getImage() {       	
	
	return image;
    }

    // Repaint the component and recalculate everything
    private synchronized void bufferedPaint(Graphics g, Chart chart,
			       int width, int height) {

	// Calculate horizontal axis
	calculateHorizontalAxes(g, chart, width, height);

	// Draw everything	
	drawBackground(g, chart, width, height);	
	drawLevels(g, chart, width, height);       
	drawHorizontalLabels(g, height);
	drawAnnotations(g, chart);
	drawLines(g, chart);
	drawPoints(g, chart, height);
	drawText(g, chart);
	drawCursor(g, chart);
    }

    // Create a new horizontal axis which is sized for the component
    private void calculateHorizontalAxes(Graphics g, Chart chart,
					 int width, int height) {
	// Minor horizontal axis can either be MONTHS or QUARTERS. Test
	// to see if twice the width of "Feb" can fit within the pixel
	// space given if we use MONTHS, if not - use QUARTERS.

        assert chart.getXRange().size() > 0;

	// Calculate horizontal scale
	horizontalScale =
	    HorizontalAxis.calculateScale(width - Y_LABEL_WIDTH,
					  chart.getXRange().size());

	if(horizontalScale * MINIMUM_TRADING_DAYS_IN_MONTH <
	   2 * g.getFontMetrics().stringWidth(new String("Feb"))) {
	
	    if(quartersHorizontalAxis == null)
		quartersHorizontalAxis =
		    new HorizontalAxis(chart.getXRange(),
				       HorizontalAxis.QUARTERS,
				       HorizontalAxis.MINOR);

	    minorHorizontalAxis = quartersHorizontalAxis;
	}
	else {
	    if(monthsHorizontalAxis == null)		
		monthsHorizontalAxis =
		    new HorizontalAxis(chart.getXRange(),
				       HorizontalAxis.MONTHS,
				       HorizontalAxis.MINOR);

	    minorHorizontalAxis = monthsHorizontalAxis;
	}

	// Major horizontal axis is always years
	if(majorHorizontalAxis == null)
	    majorHorizontalAxis =
		new HorizontalAxis(chart.getXRange(), HorizontalAxis.YEARS,
				   HorizontalAxis.MAJOR);
    }

    // Highlight the component's highlighted region
    private void highlightRegion(Graphics g, Chart chart, int height) {
	if(chart.getHighlightedStart() != null &&
	   chart.getHighlightedEnd() != null) {

	    // Convert X value to X coordinate
	    int start = getXCoordinate(chart, chart.getHighlightedStart());
	    int end = getXCoordinate(chart, chart.getHighlightedEnd());

	    g.setXORMode(Color.pink);

	    g.fillRect(start < end? start: end, 0,
		       1+Math.abs(end-start), height);

	    g.setPaintMode();
	}
    }


    /*
      Show free hand lines drawn on chart.
    */

    private void drawPoints(Graphics g, Chart chart, int height) {

	ChartDrawingModel elements = chart.getChartDrawingModel();
	Vector points = elements.getDrawnPoints();

	if (points.size() > 0) {
	    
	    int x1, y1;
	    int x2, y2;
	    int absY1, absY2;
	    Coordinate coord1, coord2;
	    boolean normalOrientation = chart.getOrientation();
	    
	    Color prev = g.getColor();
	    g.setColor(Color.MAGENTA);
	    
	    if (points.size() == 1) {
		
		coord1 = (Coordinate)points.elementAt(0);
		absY1 = coord1.getYCoord().intValue();
		
		if (absY1 != Coordinate.BREAK) {
		    x1 = getXCoordinate(chart, coord1);
		    y1 = getYCoordinate(chart, coord1);		
		    g.drawLine(x1,y1,x1+1,y1+1);
		}
	    } else {
		for (int i = 0; i < points.size()-1; i++) {
		    x1 = x2 = y1 = y2 = 0;

		    coord1 = (Coordinate)points.elementAt(i);
		    
		    absY1 = coord1.getYCoord().intValue();
		    
		    if (absY1 != Coordinate.BREAK) {		    
			x1 = getXCoordinate(chart, coord1);
			y1 = getYCoordinate(chart, coord1);
		    }

		    coord2 = (Coordinate)points.elementAt(i+1);
		    absY2 = coord2.getYCoord().intValue();		    
		    
		    if (absY2 != Coordinate.BREAK) {
			x2 = getXCoordinate(chart, coord2);
			y2 = getYCoordinate(chart, coord2);
		    }		    

		    if (absY1 == Coordinate.BREAK) {			
			g.drawLine(x2,y2,x2+1,y2+1);
		    } else if (absY2 == Coordinate.BREAK) {			
			g.drawLine(x1,y1,x1+1,y1+1);
		    } else {
			//otherwise connect the dots			
			if (!normalOrientation) {
			    y1 = height - y1;
			    y2 = height - y2;
			}
			g.drawLine(x1,y1,x2,y2);
		    }		       					
		}				

	    }
	    g.setColor(prev);
	    g.setPaintMode();
	}
	
    }
    
    //Paint any lines which have been drawn on the chart. 
    private void drawLines(Graphics g, Chart chart) {
	
	Color prev;
	ChartDrawingModel elements; 
	Vector lines;
	Iterator iterator;

	elements = chart.getChartDrawingModel();
	lines = elements.getDrawnLines();
	
	if (lines.size() > 0) {
	    iterator = lines.iterator();

	    while (iterator.hasNext())  {
		int startX, endX;
		int startY, endY;
		DrawnLine line;
		
		line = (DrawnLine)iterator.next();
		startX = getXCoordinate(chart, line.getStart());
		startY = getYCoordinate(chart, line.getStart());
		
		endX = getXCoordinate(chart, line.getEnd());
		endY = getYCoordinate(chart, line.getEnd());
		
		boolean normalOrientation = chart.getOrientation(); 
		if (!normalOrientation) {
		    int tmp;
		    tmp = startY;
		    startY = endY;
		    endY = tmp;
		}		
		
		prev = g.getColor();
		
		g.setColor(Color.MAGENTA);
		g.drawLine(startX,startY, endX, endY);
		g.setColor(prev);

	    }
	    g.setPaintMode();	    
	}
    }

    /* 
       Show text added to chart by drawing.
    */

    private void drawText(Graphics g, Chart chart) {
	Color prevColour = g.getColor();
	int x,y,absY;
	Font prevFont = g.getFont();
	int fontSize;
	String fontFamily = "Courier";
	ChartDrawingModel elements = chart.getChartDrawingModel();
	
	g.setColor(BasicChartUI.
		   getComplementaryColour(PreferencesManager.
					  getDefaultChartBackgroundColour()));
						       

	HashMap map = elements.getText();
	Iterator it = map.values().iterator();
	Iterator it2 = map.keySet().iterator();
	while (it.hasNext()) {
	    String val = (String)it.next();
	    if (it2.hasNext()) {
		Coordinate key = (Coordinate)it2.next();
		absY = key.getYCoord().intValue();
		x = getXCoordinate(chart, key);
 		y = getYCoordinate(chart, key);

		double verticalScale = getVerticalScale(chart, absY);
		
		fontSize = 12;	
		/* Is verticalScale monitor/resolution indedendant?
		   In any case, these fudge factors should be replaced/
		   calculated
		 */
		if (verticalScale >= 2000.0) {		    
		    fontSize += 4;
		}
		if (verticalScale >= 3000.0) {		    
		    fontSize += 4;
		}
		if (verticalScale >= 4000.0) {		    
		    fontSize += 4;
		}

		Font f = new Font(fontFamily, Font.PLAIN, fontSize);
		g.setFont(f);
		g.drawString(val, x, y);
	    }
	}       
	g.setColor(prevColour);
	g.setFont(prevFont);
	g.setPaintMode();
    }

    private void drawCursor(Graphics g, Chart chart) {
	int x;
	int y;
	
	if (chart.getTracker() == null) {
	    return;
	}

	if (!chart.getTracker().isActive()) {
	    return; 
	}
	Coordinate point = chart.getTracker().getCoordinate(); 

	x = getXCoordinate(chart, point);
	y = getYCoordinate(chart, point) - (MINIMUM_LEVEL_HEIGHT / 2);
	
	Color prev = g.getColor();
	
	g.setColor(BasicChartUI.
		   getComplementaryColour(PreferencesManager.
					  getDefaultChartBackgroundColour()));
	g.drawOval(x,y, 5, 5);
	g.setColor(prev);
    }

    // For the given point, return the X coordinate
    private int getXCoordinate(Chart chart, Coordinate point) {
	assert point != null;
	assert chart != null;
	return getXCoordinate(chart, point.getXData());
    }

    // For the given X value, return the X coordinate
    private int getXCoordinate(Chart chart, Comparable x) {

	int i = 0;
	
	if(chart.getXRange() != null) {
	    Iterator iterator = chart.getXRange().iterator();
	    Comparable thisX = null;

	    while(iterator.hasNext()) {
		thisX = (Comparable)iterator.next();

		if(x.compareTo(thisX) <= 0)
		    return (int)(i * horizontalScale);
		
		i++;
	    }
	}
	return (int)(i * horizontalScale);
    }

    // For the given point, return the Y coordinate
    private int getYCoordinate(Chart chart, Coordinate point) {
	return getYCoordinate(chart, 
			      point.getYData(), 
			      point.getYCoord().intValue(),
			      point.getLevel());
    }

    // For the given Y value, return the Y coordinate
    private int getYCoordinate(Chart chart, Double y, int yIndex, int level) {

	double dataValue;
		
	// Get vertical axis of graph level
	VerticalAxis verticalAxis =
	    (VerticalAxis)verticalAxes.elementAt(level);
	double verticalScale = verticalAxis.getScale();
	double height = verticalAxis.getHeightOfGraph();
	double bottomLineValue = verticalAxis.getBottomLineValue();
	int yoffset = getStartOfLevel(level) + firstHorizontalLine + ((Integer)levelHeights.elementAt(level)).intValue();
	
	//The absolute position for the data point 
	int scaledPoint =  GraphTools.scaleAndFitPoint( y.doubleValue(), 
						(double)bottomLineValue, 
						verticalScale);

	int absY = yoffset - scaledPoint;
	
	return absY;
	
    }

    // For the given Y value, return the Y coordinate
    private int getYCoordinate(Chart chart, 
			       Double y, 
			       int yIndex, 
			       int height2,
			       int lvl) {	

	double dataValue;
	
	// Get graph level at this point
	int level = getLevelAtPoint(lvl);
	// Get vertical axis of graph level
	VerticalAxis verticalAxis =
	    (VerticalAxis)verticalAxes.elementAt(level);
	double verticalScale = verticalAxis.getScale();
	double height = verticalAxis.getHeightOfGraph();
	double bottomLineValue = verticalAxis.getBottomLineValue();
	int yoffset = getStartOfLevel(level) + firstHorizontalLine + ((Integer)levelHeights.elementAt(level)).intValue();
	

	int yOffset = verticalAxis.getHeightOfGraph() +
	    (height2-verticalAxis.getHeightOfGraph())/2;
	

	yoffset = yOffset;

	//The absolute position for the data point 
	int scaledPoint =  GraphTools.scaleAndFitPoint( y.doubleValue(), 
						(double)bottomLineValue, 
						verticalScale);

	int absY = yoffset - scaledPoint;
	
	return absY;
	
    }

    private double getVerticalScale(Chart chart, int yIndex) {
	// Get graph level at this point
	int level = getLevelAtPoint(yIndex);
	// Get vertical axis of graph level
	VerticalAxis verticalAxis =
	    (VerticalAxis)verticalAxes.elementAt(level);
	double verticalScale = verticalAxis.getScale();

	return verticalScale;
    }

    //Return the difference between point (x,y) and the closest point
    //generated by the equation of the line given by start and end. 
    public double getDifference(Chart chart, Integer x, Integer y, 
				Coordinate start, Coordinate end) {

	double slope = 1.0;
	double intersect;
	double diff = 10.0;
	Comparable dataX;
	Double dataY;
	int absY;
	int x1, y1, x2, y2;
	double candX, candY;
	int level;

	dataX = start.getXData();
	dataY = start.getYData();
	absY = start.getYCoord().intValue();
	level = start.getLevel();
	
	x1 = getXCoordinate(chart, dataX);
	y1 = getYCoordinate(chart, dataY, absY, level);
	
	dataX = end.getXData();
	dataY = end.getYData();
	absY = end.getYCoord().intValue();
	level = end.getLevel();
	
	x2 = getXCoordinate(chart, dataX);
	y2 = getYCoordinate(chart, dataY, absY, level);
	
	if (x2 == x1) {
	    slope = 0;		
	} else {
	    slope = ( (double)(y1) - (double)(y2)) / ((double)(x1) - (double)(x2));
	}
	
	if (x2 != x1) {
	    intersect = (double)(y2) - slope * (double)(x2);
	} else {
	    intersect = (double)(x2);
	}
	
	candX = (double)(x.intValue());
	candY = (double)(y.intValue());
	
	if (x2 != x1) {
	    diff = candY - (slope * candX + intersect);
	} else {
	    diff = candY - intersect;
	}
	
	return diff;
    }

    //Return the difference between point (x,y) and the closest point
    //generated by the equation of the line given by start and end.     
    public double getDifference(Chart chart,  Coordinate c, Coordinate start, Coordinate end) {

	double slope = 1.0;
	double intersect;
	double diff = 10.0;
	Comparable dataX;
	Double dataY;
	int absY;
	int x1, y1, x2, y2;
	int candX, candY; 
	int level;

	dataX = start.getXData();
	dataY = start.getYData();
	absY = start.getYCoord().intValue();
	level = start.getLevel();	
	
	x1 = getXCoordinate(chart, dataX);
	y1 = getYCoordinate(chart, dataY, absY, level);
	
	dataX = end.getXData();
	dataY = end.getYData();
	absY = end.getYCoord().intValue();
	level = end.getLevel();
	
	x2 = getXCoordinate(chart, dataX);
	y2 = getYCoordinate(chart, dataY, absY, level);
	
	if (x2 == x1) {
	    slope = 0;		
	} else {
	    slope = ( (double)(y1) - (double)(y2)) / ((double)(x1) - (double)(x2));
	}
	
	if (x2 != x1) {
	    intersect = (double)(y2) - slope * (double)(x2);
	} else {
	    intersect = (double)(x2);
	}
	
	dataX = c.getXData();
	dataY = c.getYData();
	absY = c.getYCoord().intValue();
	level = c.getLevel();
	
	candX = getXCoordinate(chart, dataX);
	candY = getYCoordinate(chart, dataY, absY, level);

	if (x2 != x1) {
	    diff = candY - (slope * candX + intersect);
	} else {
	    diff = candY - intersect;
	}	
	return diff;
    }

    //Return which end of the line has moved 
    public Coordinate getMoved(Chart chart, Integer x, Integer y, 
			       Coordinate start, Coordinate end) {
	Comparable dataX;
	Double dataY;
	int absY;
	int x1, y1, x2, y2;
	double dist1, dist2;
	Coordinate rv = null;
	int level;

	dataX = start.getXData();
	dataY = start.getYData();
	absY = start.getYCoord().intValue();
	level = start.getLevel();

	x1 = getXCoordinate(chart, dataX);
	y1 = getYCoordinate(chart, dataY, absY, level);
				
	dataX = end.getXData();
	dataY = end.getYData();
	absY = end.getYCoord().intValue();
	level = end.getLevel();
		
	x2 = getXCoordinate(chart, dataX);
	y2 = getYCoordinate(chart, dataY, absY, level);

	/* Determine which end of the line the user has
	   chosen to move. */		
	dist1 = Math.sqrt(
			  ((x1 - x.intValue()) * 
			   (x1 - x.intValue())) + 
			  ((y1 - y.intValue()) * 
			   (y1 - y.intValue())));
	
	dist2 = Math.sqrt(
			  ((x2 - x.intValue()) * 
			   (x2 - x.intValue())) + 
			  ((y2 - y.intValue()) * 
			   (y2 - y.intValue())));
	
	if (dist1 < dist2) {
	    return end;
	} else {      	
	    return start;
	}	

    }

    /* Return true if coordinates c1 and c2 are "within" delta of each
       other. True distance would be pythagorean. 
    */
    public boolean intersect(Chart chart, Coordinate c1, Coordinate c2,
			     int delta) {

	Comparable dataX;
	Double dataY;
	int absY;
	int x1, y1, x2, y2;
	int level;

	dataX = c1.getXData();
	dataY = c1.getYData();
	absY = c1.getYCoord().intValue();
	level = c1.getLevel();

	if (absY == Coordinate.BREAK) {
	    return false;
	}
	
	x1 = getXCoordinate(chart, dataX);
	y1 = getYCoordinate(chart, dataY, absY, level);

	dataX = c2.getXData();
	dataY = c2.getYData();
	absY = c2.getYCoord().intValue();	
	level = c2.getLevel();

	if (absY == Coordinate.BREAK) {
	    return false;
	}

	x2 = getXCoordinate(chart, dataX);	
	y2 = getYCoordinate(chart, dataY, absY, level);

	if (Math.abs(x1 - x2) < delta &&
	    Math.abs(y1 - y2) < delta) {
	    return true;
	}
	return false;

    }

    /**
     * Reset the double buffer and force the chart to redraw.
     */

    //This should be synchronized. Otherwise, the chart attempts to 
    //draw before this method completes.

    //But if it is synchronized EODQuoteChartMenu.updateGraph() breaks. 
    //We need to fix the chart/ui threading issues. 

    //Seems to have been resolved - currently testing

    public synchronized void resetBuffer() {
	// Recalculate everything
	quartersHorizontalAxis = null;
	monthsHorizontalAxis = null;
	majorHorizontalAxis = null;
	verticalAxes = null;
	colourMap = null;
	image = null;
	bufferWidth = 0;
	bufferHeight = 0;
    }

    // Set the background colour
    private void drawBackground(Graphics g, Chart chart, int width,
				int height) {
	
	//background colours with non zero alpha value (transparency)
	//display with artifacts without "wiping" the canvas first
	g.setColor(Color.black);
	g.fillRect(0, 0, width, height);
	g.setColor(chart.getBackground());
	g.fillRect(0, 0, width, height);
	
    }

    // Draw the vertical grid and the vertical labels onto the chart
    private void drawVerticalGridAndLabels(Graphics g,
					   Graph firstGraph,
					   String title,
					   VerticalAxis verticalAxis,
					   int yoffset, int width) {
	g.setColor(Color.lightGray);
	verticalAxis.drawGridAndLabels(g, firstGraph, title, 0, yoffset,
				       width - Y_LABEL_WIDTH);

    }

    // Draw all the graph levels onto the chart
    private void drawLevels(Graphics g, Chart chart, int width, int height) {
	// Calculate space between top of graph and first horizontalLine
	firstHorizontalLine = getFirstHorizontalLine(g);

	// Calculate height of each level
	calculateLevelHeights(chart, height);

	// Draw each graph level
	Iterator iterator = chart.getLevels().iterator();

	int yoffset = firstHorizontalLine;
	int level = 0;

	while(iterator.hasNext()) {
	    Vector graphs = (Vector)iterator.next();

	    drawLevel(g, graphs, chart, yoffset, width,
		      ((Integer)levelHeights.elementAt(level)).intValue() -
		      firstHorizontalLine, level);
		
	    yoffset += ((Integer)levelHeights.elementAt(level++)).intValue();
	}
    }

    // Draw a single graph level onto the chart
    private void drawLevel(Graphics g, Vector graphs, Chart chart, int yoffset,
			   int width, int height, int level) {


	VerticalAxis verticalAxis = calculateVerticalAxis(chart, graphs,
							  height, level);
		
	drawVerticalGridAndLabels(g, (Graph)graphs.firstElement(),
				  getLevelTitle(graphs),
				  verticalAxis, yoffset, width);
	drawGraphs(g, graphs, chart, verticalAxis, yoffset, width, height);
	drawHorizontalGrid(g, chart, verticalAxis, yoffset);
    }

    // Calculates the title to put on the graph level
    private String getLevelTitle(Vector graphs) {

	Vector symbols = new Vector();
	String symbol;
	boolean found;

	Iterator symbolsIterator;
	Iterator iterator = graphs.iterator();

	while(iterator.hasNext()) {
	    symbol = ((Graph)iterator.next()).getSourceName();

	    // add it if its not already in our list of symbols
	    symbolsIterator = symbols.iterator();
	
	    found = false;

	    while(symbolsIterator.hasNext()) {

		// the same?
		if(symbol.compareTo((String)symbolsIterator.next()) == 0) {
		    found = true;
		    break;
		}
	    }

	    // Add to list
	    if(!found)
		symbols.add(symbol);

	}

        /*
	// Now convert list of symbols to string of comma separated
	// company names
	String title = new String("");
	String companyName = null;

	symbolsIterator = symbols.iterator();

	while(symbolsIterator.hasNext()) {
	    symbol = (String)symbolsIterator.next();

	    if(title.length() != 0)
		title = title.concat(", ");

	    companyName =
		QuoteSourceManager.getSource().getSymbolName(symbol);

	    if(companyName != null)
		title =
		    title.concat(companyName);
	}
        */

	return new String("");

    }

    // Create a new vertical axis which is sized for the component
    private VerticalAxis calculateVerticalAxis(Chart chart, Vector graphs,
					       int height, int level) {

	Graph firstGraph = (Graph)graphs.firstElement();

	// Do we need to recalculate vertical axis vector?
	if(verticalAxes == null)
	    verticalAxes = new Vector();

	// Ensure vector is large enough to hold 'level' axes
	while(verticalAxes.size() <= level)
	    verticalAxes.add(null);

	// Recreate vertical axis if its null otherwise buffer
	VerticalAxis verticalAxis =
	    (VerticalAxis)verticalAxes.elementAt(level);

	if(verticalAxis == null) {
	    
	    verticalAxis =
		new VerticalAxis(getLowestY(chart.getXRange(), graphs),
				 getHighestY(chart.getXRange(), graphs),
				 firstGraph.getAcceptableMinorDeltas(),
				 firstGraph.getAcceptableMajorDeltas());
	    verticalAxes.setElementAt(verticalAxis, level);
	}

	// Fix height of vertical axis
	verticalAxis.setHeight(height);

	return verticalAxis;
    }

    // Draw all of the given graphs at this level onto the graph
    private void drawGraphs(Graphics g, Vector graphs, Chart chart,
			    VerticalAxis verticalAxis,
			    int yoffset, int width, int height) {

	Graph graph;
	Iterator iterator = graphs.iterator();

	// Draw vector of overlapping graphs
	while(iterator.hasNext()) {

	    graph = (Graph)iterator.next();

	    graph.render(g, getGraphColour(graph, chart), 0, yoffset +
			 verticalAxis.getHeightOfGraph() +
			 (height-verticalAxis.getHeightOfGraph())/2,
			 horizontalScale,
			 verticalAxis.getScale(),
			 verticalAxis.getTopLineValue(),
			 verticalAxis.getBottomLineValue(),
			 chart.getXRange(),
			 chart.getOrientation());
	}
    }

    // Draw the horizontal grid onto the chart
    private void drawHorizontalGrid(Graphics g, Chart chart,
				   VerticalAxis verticalAxis, int yoffset) {

	g.setColor(Color.lightGray);

	minorHorizontalAxis.drawGrid(g, yoffset +
				     verticalAxis.getHeightOfGraph(),
				     horizontalScale,
				     verticalAxis.getHeightOfGraph());
	majorHorizontalAxis.drawGrid(g, yoffset +
				     verticalAxis.getHeightOfGraph(),
				     horizontalScale,
				     verticalAxis.getHeightOfGraph());
    }

    // Draw the horizontal axis labels onto the chart
    private void drawHorizontalLabels(Graphics g, int height) {

	g.setColor(Color.lightGray);

	minorHorizontalAxis.drawLabels(g, horizontalScale,
				       0, height - X_LABEL_HEIGHT +
				       g.getFontMetrics().getHeight());
	majorHorizontalAxis.drawLabels(g, horizontalScale,
				       0, height - X_LABEL_HEIGHT +
				       g.getFontMetrics().getHeight()*2);

    }

    // Draws onto graph annotations such as "16/9 Buy" etc. These
    // can be from any graph on the chart.
    private void drawAnnotations(Graphics g, Chart chart) {

	// Iterate through all graphs and draw all their annotations
	Iterator iterator = chart.getLevels().iterator();
	Iterator innerIterator;
	Graph graph;
	HashMap annotations;
	VerticalAxis verticalAxis;
	int level = 0;
	int yoffset;

	while(iterator.hasNext()) {
	    innerIterator = ((Vector)iterator.next()).iterator();
	    yoffset = getStartOfLevel(level) + firstHorizontalLine;
	    verticalAxis = (VerticalAxis)verticalAxes.elementAt(level++);

	    while(innerIterator.hasNext()) {
		graph = (Graph)innerIterator.next();
                //		annotations = graph.getAnnotations();
                annotations = null;

		// Draw this graph's annotations if it has any and
		// its turned on
		if(annotations != null && chart.isAnnotated(graph))
		    drawGraphAnnotations(g, chart, graph, verticalAxis,
					 yoffset, annotations);

	    }
	}
    }	

    // Draw all annotations in the given hashmap
    private void drawGraphAnnotations(Graphics g, Chart chart, Graph graph,
				      VerticalAxis verticalAxis,
				      int yoffset, HashMap annotations) {

	Set xRange = annotations.keySet();
	Iterator iterator = xRange.iterator();
	Comparable x;
	String text;

	while(iterator.hasNext()) {

	    x = (Comparable)iterator.next();

	    // Only display annotation if its within the range displayed by the
	    // chart
	    if(x.compareTo(chart.getStartX()) >= 0 &&
	       x.compareTo(chart.getEndX()) <= 0) {
	
		text = (String)annotations.get(x); // associated annotation

		// Insert X value into text field
		text = x.toString() + ": " + text;
		Double y = graph.getY(x);

		// Ignore annotations where the data source has no y value
		if(y != null) {

		    // Put y position near graph (assumes y graph is a line)
		    int yCoordinate = yoffset +
			verticalAxis.getHeightOfGraph() -
			GraphTools.
			scaleAndFitPoint(y.doubleValue(),
					 verticalAxis.getBottomLineValue(),
					 verticalAxis.getScale());

		    drawAnnotation(g, text, getXCoordinate(chart, x),
				   yCoordinate);
		}
	    }
	}
    }

    // Draws at the given point a single annotation with the given text.
    private void drawAnnotation(Graphics g, String text, int x, int y) {

	int width = g.getFontMetrics().stringWidth(text) +
	    2 * ANNOTATED_TEXT_MARGIN;
	int height = g.getFontMetrics().getHeight();

	g.setColor(Color.yellow);
	g.fillRect(x, y, width, height);

	g.setColor(Color.black);
	g.drawString(text, x + ANNOTATED_TEXT_MARGIN,
		     y + height - g.getFontMetrics().getDescent());
    }

    public boolean imageUpdate(Image image, int infofloags, int x, int y,
			       int width, int height) {
	return true;
    }

    // Find the lowest Y value for all the graphs in the given vector
    // over the given X range.
    private double getLowestY(Vector x, Vector graphs) {
	Iterator iterator = graphs.iterator();
	double y;
	double lowestY = Double.MAX_VALUE;

	while(iterator.hasNext()) {
		
	    y = ((Graph)iterator.next()).getLowestY(x);

	    if(y < lowestY)
		lowestY = y;
	}
	return lowestY;
    }

    // Find the highest Y value for all the graphs in the given vector
    // over the given X range.
    private double getHighestY(Vector x, Vector graphs) {
	Iterator iterator = graphs.iterator();
	double y;
	double highestY = Double.NEGATIVE_INFINITY;

	while(iterator.hasNext()) {
	    y = ((Graph)iterator.next()).getHighestY(x);

	    if(y > highestY)
		highestY = y;
	}

	return highestY;
    }

    // Calculate the height of each graph level.
    private void calculateLevelHeights(Chart chart, int height) {

	// At the moment the top level is the primary level, whilst
	// the remaining are secondary - later on the user will be able
	// to change this
	height -= X_LABEL_HEIGHT;

	int levels = chart.getLevels().size();
	int primaryLevelHeight;
	int secondaryLevelHeight;
	int units = PRIMARY_HEIGHT_UNITS + (levels-1) * SECONDARY_HEIGHT_UNITS;
	int unitHeight = height / units;

	// 1. If the height of the secondary level is too small, make it
	// the minimum size
	if(unitHeight * SECONDARY_HEIGHT_UNITS < MINIMUM_LEVEL_HEIGHT)
	    secondaryLevelHeight = MINIMUM_LEVEL_HEIGHT;
	else
	    secondaryLevelHeight = unitHeight * SECONDARY_HEIGHT_UNITS;

	// 2. Primary height is the space left over
	primaryLevelHeight = height - secondaryLevelHeight * (levels-1);

	// 3. Create vector of height for each level
	levelHeights = new Vector();

	levelHeights.add(new Integer(primaryLevelHeight));

	while(--levels > 0)
	    levelHeights.add(new Integer(secondaryLevelHeight));
    }

    // Return the y value of the first horizontal line from the top of
    // this component.
    private int getFirstHorizontalLine(Graphics g) {
	return(firstHorizontalLine = (int)(TOP_GRAPH_BUFFER_MULTIPLIER *
					   g.getFontMetrics().getHeight()));
    }

    // Create a mapping between stock symbol and the colour we should
    // graph it. E.g. if CBA is charted in two different levels
    // they will both be in the same colour.
    private void calculateColourMap(Chart chart) {

	if(colourMap == null) {

	    // Create map between colour and each graph symbol, e.g so
	    // CBA would be one colour and WBC another
	
	    // Colours of graphs in order of use
	    Color[] colours = {Color.blue.darker(),
			       Color.cyan.darker(),
			       Color.magenta.darker(), Color.orange.darker(),
			       Color.pink.darker(),
			       						
			       Color.blue, Color.magenta,
			       Color.orange, Color.pink,

			       Color.blue.darker().darker(),
			       Color.cyan.darker().darker(),
			       Color.magenta.darker().darker(),
			       Color.orange.darker().darker(),
			       Color.pink.darker().darker(),

			       Color.blue.brighter(),
			       Color.cyan.brighter(),
			       Color.magenta.brighter(),
			       Color.orange.brighter(),
			       Color.pink.brighter()};
	

	    //Filter the colour map, removing any where the contrast is 
	    //insufficient (ie darkblue on black)
	    Color backgroundColour = 
		PreferencesManager.getDefaultChartBackgroundColour();

	    colours = filterColours(colours, backgroundColour);
	    
	    
	   

	    colourMap = new HashMap();
	
	    // Iterate through all graphs and grab all sources
	    Iterator levelsIterator = chart.getLevels().iterator();
	    Iterator graphsIterator;
	    Graph graph;
	    String symbol;
	    int coloursUsed = 0;
	
	    while(levelsIterator.hasNext()) {
		
		graphsIterator = ((Vector)levelsIterator.next()).iterator();
		
		while(graphsIterator.hasNext()) {
		    // Get symbol
		    graph = (Graph)graphsIterator.next();
		    symbol = graph.getSourceName();
		
		    // Add mapping between symbol and colour if it doesnt
		    // exist yet
		    if(colourMap.get(symbol) == null) {
			colourMap.put(symbol, colours[coloursUsed++]);
			
			// Re-use colours if necessary
			if(coloursUsed >= colours.length)
			    coloursUsed = 0;
		    }
		}
	    }
	}
    }

    /**
     * Return the colour we will draw the given graph. Note that the graph
     * may choose to override this colour.
     *
     * @param	graph	the graph to query
     * @param	chart	the chart component
     * @return	the colour the graph might be
     */
    public Color getGraphColour(Graph graph, Chart chart) {

	if(colourMap == null)
	    calculateColourMap(chart);

	Color colour = (Color)colourMap.get(graph.getSourceName());
	
	if(colour != null)
	    return colour;

	// If the colour map is missing a symbol then default
	// to dark grey (shouldnt happen)
	return Color.darkGray;
    }

    /**
     * 
     * Given a colour, return it's colour wheel complement 
     * so that it generates a useful contrast. 
     * (e.g Given white return black, blue return orange etc)
     * 
     * @param colour A colour
     * @return it's complement 
     */

    /*
      This code treats RGB space as a vector in R3 (4 for alpha channel)
      To find the colour complement, this method:
      1. Finds the closest main colour defined in bright/dark array
      2. Calculates the distance between the given colour and the closest colour.
      3. Apply that delta to the main complement colour (ie if the closest 
      colour is white, the closest opposite is black) and return that colour.
            
      Further refinement: Calculate angles from the origin, and rotate the
      delta using those angles.
     */
    public static Color getComplementaryColour(Color colour) {
	Color violet = new Color(125, 0, 125, 0);
		
	Color[] brightColours = {Color.WHITE, Color.RED, Color.YELLOW, Color.ORANGE};
	Color[] darkColours = {Color.BLACK, Color.GREEN, violet, Color.BLUE};
	
	Color closestColour = null;
	Color oppositeColour = null;
	boolean closestIsBright = false;

	double[] distances = new double[2 * brightColours.length];

	int minColourIndex = -1;
	double minDistance = 30.0; //~sqrt(255+255+255)

	//Find the nearest major colour in bright/dark list closest to colour
	for (int i = 0; i < brightColours.length; i+=2) {
	    distances[i] = getDistance(colour, brightColours[i]);
	    distances[i+1] = getDistance(colour, darkColours[i]);
	    
	    if (distances[i] < minDistance) {
		minDistance = distances[i];
		minColourIndex = i;
		closestColour = brightColours[i];
		closestIsBright = true;
	    }

	    if (distances[i+1] < minDistance) {
		minDistance = distances[i+1];
		minColourIndex = i; //mincolourindex  applies to bright/dark
		closestColour = darkColours[i];
		closestIsBright = false;
	    }
	}

	assert minColourIndex != -1;

	oppositeColour = (closestIsBright) 
	    ? darkColours[minColourIndex] 
	    : brightColours[minColourIndex];

	int[] deltaC = getDelta(colour, closestColour);
	
	Color rv = applyDelta(oppositeColour, deltaC);
	return rv;	
    }

    private static double getDistance(Color colour1, Color colour2) {		
	int r1 = colour1.getRed();
	int g1 = colour1.getGreen();
	int b1 = colour1.getBlue();

	int r2 = colour2.getRed();
	int g2 = colour2.getGreen();
	int b2 = colour2.getBlue();

	int rdiff = Math.abs(r2 - r1);
	int gdiff = Math.abs(g2 - g1);
	int bdiff = Math.abs(b2 - b1);

	return Math.sqrt(rdiff + gdiff + bdiff);
    }

    private static int[] getDelta(Color colour1, Color colour2) {
	int r1 = colour1.getRed();
	int g1 = colour1.getGreen();
	int b1 = colour1.getBlue();

	int r2 = colour2.getRed();
	int g2 = colour2.getGreen();
	int b2 = colour2.getBlue();

	int rdiff = Math.abs(r2 - r1);
	int gdiff = Math.abs(g2 - g1);
	int bdiff = Math.abs(b2 - b1);
	
	int[] delta = {rdiff, gdiff, bdiff};

	return delta;

    }

    private static Color applyDelta(Color mainColour, int[] deltaColour)  {
	int redValue = mainColour.getRed() - deltaColour[0];
	int greenValue = mainColour.getGreen() - deltaColour[1];
	int blueValue = mainColour.getBlue() - deltaColour[2];
		
	if (redValue < 0) {
	    redValue *= -1;
	}

	if (greenValue < 0) {
	    greenValue *= -1;
	}
	
	if (blueValue < 0) {
	    blueValue *= -1;
	}

	if (redValue > 255) {
	    redValue %= 255;
	}

	if (greenValue > 255) {
	    greenValue %= 255;
	}

	if (blueValue > 255) {
	    blueValue %= 255;
	}

	return new Color(redValue, greenValue, blueValue, mainColour.getAlpha());	
    }

    public static boolean isLightColour(Color colour) {
	double distanceFromWhite = getDistance(colour, Color.WHITE);
	double distanceFromBlack = getDistance(colour, Color.BLACK);
	
	return (distanceFromWhite < distanceFromBlack);

    }

    private Color[] filterColours(Color[] map, Color backgroundColor) {

	Color[] filteredColours = new Color[map.length];
	int filteredColourCount = 0;

	for (int i = 0; i < map.length; i++) {
	    //20 ~ sqrt(125 + 125 + 125)
	    //Need to polish the colours and set this number more
	    //exactly.
	    if (getDistance(backgroundColor, map[i]) >= 18) {
		filteredColours[filteredColourCount++] = map[i];
	    }
	}
	Color[] rv = new Color[filteredColourCount];
	for (int i = 0; i < filteredColourCount; i++) {
	    rv[i] = filteredColours[i];
	}
	return rv;
    }
    
}
