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
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import nz.org.venice.chart.Graphable;
import nz.org.venice.chart.GraphableQuoteFunctionSource;
import nz.org.venice.chart.GraphTools;
import nz.org.venice.chart.BasicChartUI;
import nz.org.venice.chart.source.GraphSource;
import nz.org.venice.parser.EvaluationException;
import nz.org.venice.quote.QuoteFunctions;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.util.Locale;
import nz.org.venice.util.TradingDate;

/**
 * Draw the fibonacci retracement levels on the chart
 *
 * @author Andrew Goh
 */
public class FiboGraph extends AbstractGraph {

	private double zeropctlevel;
	private double hundredpctlevel;
	
	private double fibopct[] = { 0.0D, 0.236D, 0.384D, 0.5D, 0.618D, 0.786D, 1.0D } ;

  /**
     * Create a Fibonacci chart.
     *
     * @param	source	the EOD source 
     */
    public FiboGraph(GraphSource source) {
	super(source);
        //setSettings(new HashMap());
    }

    /**
     * Create a Fibonacci chart.
     *
     * @param	source	the EOD source 
     * @param	settings the settings of the graph 
     */
    public FiboGraph(GraphSource source, HashMap settings) {
	super(source);
        setSettings(settings);
    }

    public void render(Graphics g, Color colour, int xoffset, int yoffset,
		       double horizontalScale, double verticalScale,
		       double topLineValue, double bottomLineValue, 
		       List xRange, boolean vertOrientation) {


	// We ignore the graph colours and use our own custom colours
	Color backgroundColour = PreferencesManager.getDefaultChartBackgroundColour();
	if (BasicChartUI.isLightColour(backgroundColour)) {
		g.setColor(Color.blue);
	} else {
	    g.setColor(BasicChartUI.getComplementaryColour(Color.blue));
	}

	double yValue = 0.0D;
	String label;
	DecimalFormat fmt = new DecimalFormat("0.0");
	String sfmt = (zeropctlevel < 100 ? "0.00": "0.0");
	DecimalFormat fmt1 = new DecimalFormat(sfmt);

	
	
	for( int i = 0 ; i< fibopct.length ; i++ ) {
		yValue = (hundredpctlevel - zeropctlevel) * fibopct[i] + zeropctlevel; 
	    GraphTools.renderHorizontalLine(g, yValue, xoffset, yoffset,    		
					    horizontalScale, verticalScale, 
					    bottomLineValue, topLineValue, 
					    xRange, vertOrientation
					    );

	    label = fmt.format(fibopct[i]*100) + "% " + fmt1.format(yValue);  
	    renderLabel(g, yValue, label, xoffset, yoffset,
	            horizontalScale, verticalScale, 
			topLineValue, bottomLineValue, xRange);

		}
    }

    public static void renderLabel(Graphics g,
				   double yValue,
				   String text,
				   int xoffset,
				   int yoffset,
				   double horizontalScale,
				   double verticalScale,				   
				   double topLineValue,
				   double bottomLineValue,
				   List xRange) {

    	//int startX = xoffset;
    	int endX = (int)(xoffset + (xRange.size() - 1) * horizontalScale);
    	int y = yoffset - GraphTools.scaleAndFitPoint(yValue, 
						      bottomLineValue, verticalScale);
    	
    	endX -= 30; // space for text
    	y -= 1; //move on top of line

    	//g.drawLine(startX, y, endX, y);
    	g.drawString(text,endX, y);
    }
    
    public String getToolTipText(Comparable x, int y, int yoffset,
				 double verticalScale,
				 double bottomLineValue)
    {
	return null; // we never give tool tip information
    }

    // Highest Y value is in the moving average graph
    public double getHighestY(List x) {
    	return (zeropctlevel>hundredpctlevel ? zeropctlevel : hundredpctlevel);
    }

    // Lowest Y value is in the moving average graph
    public double getLowestY(List x) {
    	return (zeropctlevel<hundredpctlevel ? zeropctlevel : hundredpctlevel);
    }


    public void setSettings(HashMap settings) {
        super.setSettings(settings);
        
        // Retrieve levels from settings hashmap
        zeropctlevel = Double.parseDouble((String) settings.get(FiboGraphUI.ZEROPCT));
        hundredpctlevel = Double.parseDouble((String) settings.get(FiboGraphUI.HUNDREDPCT));

    }

    /**
     * Return the graph's user interface.
     *
     * @param settings the initial settings
     * @return user interface
     */
    public GraphUI getUI(HashMap settings) {
        return new FiboGraphUI(settings);
    }

    /**
     * Return the name of this graph.
     *
     */
    public String getName() {
        return Locale.getString("FIBO_CHART");
    }

    public boolean isPrimary() {
        return true;
    }
}


