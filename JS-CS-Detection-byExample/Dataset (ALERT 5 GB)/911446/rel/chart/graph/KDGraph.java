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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nz.org.venice.chart.GraphTools;
import nz.org.venice.chart.Graphable;
import nz.org.venice.chart.source.GraphSource;
import nz.org.venice.parser.EvaluationException;
import nz.org.venice.util.Locale;
import nz.org.venice.util.TradingDate;

public class KDGraph extends AbstractGraph {
    private Graphable indicatorKGraphable = null;
    private Graphable indicatorDGraphable = null;
    private GraphSource low = null;
    private GraphSource high = null;
    private GraphSource close = null;

    public KDGraph(GraphSource low, GraphSource high, GraphSource close) {
    	this(low,high,close,new HashMap());
    }

    public KDGraph(GraphSource low, GraphSource high, GraphSource close, HashMap settings) {
        super(null);
        this.low = low;
        this.high = high;
        this.close = close;
        this.setSettings(settings);
    }


    public void render(Graphics g, Color colour, int xoffset, int yoffset,
		       double horizontalScale, double verticalScale,
		       double topLineValue, double bottomLineValue, 
		       List xRange, 
		       boolean vertOrientation) {
    	
    	g.setColor(Color.red);
    	GraphTools.renderLine(g, indicatorDGraphable, xoffset, yoffset,
		      horizontalScale,
		      verticalScale, 
		      topLineValue, bottomLineValue, 
		      xRange, 
		      vertOrientation);

    	g.setColor(Color.green.darker());
    	GraphTools.renderLine(g, indicatorKGraphable, xoffset, yoffset,
			      horizontalScale,
			      verticalScale, 
			      topLineValue, bottomLineValue, 
			      xRange, 
			      vertOrientation);

    }

    public void createKDGraph(Graphable lowGraph, Graphable highGraph, Graphable closeGraph, int period, int ksmooth, int dsmooth) {
    	indicatorKGraphable = new Graphable();
    	indicatorDGraphable = new Graphable();

        TradingDate date = (TradingDate)closeGraph.getStartX();
        
        List closes = new ArrayList(); //<Double>
        List fastks = new ArrayList(); //<Double>
        List ks = new ArrayList(); //<Double>
        List ds = new ArrayList(); //<Double>
        
        List highs = new ArrayList(); //<Double>
        List lows = new ArrayList();//<Double>
        Double maxhigh = new Double(Double.MIN_VALUE);
        Double minlow = new Double(Double.MAX_VALUE);
        
        for(Iterator hiter = highGraph.iterator(), liter = lowGraph.iterator(), citer = closeGraph.iterator();
        		hiter.hasNext() && liter.hasNext() && citer.hasNext();
        		) {
            date = (TradingDate)citer.next();
            boolean added = false;
            
            Double currentClose = closeGraph.getY(date); 

            if (currentClose.isNaN()) continue;
            
            Double currentHigh = highGraph.getY(date);
            Double currentLow =lowGraph.getY(date); 

            closes.add(currentClose);
            highs.add(currentHigh);
            lows.add(currentLow);

         // Calculate KD only when we have enough data
            if (closes.size()>=period) {
            	if (maxhigh.equals(new Double(Double.MIN_VALUE))) {
            		minlow = new Double(Double.MAX_VALUE);
            		for(int i = 0;i<highs.size();i++) {
            			if (((Double) highs.get(i)).compareTo(maxhigh) > 0 ) maxhigh = (Double)highs.get(i);
            			if (((Double)lows.get(i)).compareTo(minlow) < 0 ) minlow = (Double)lows.get(i);
            		}
            	} else {
            		if (currentHigh.compareTo(maxhigh) > 0) maxhigh = currentHigh;
            		if (currentLow.compareTo(minlow) < 0) minlow = currentLow;
            	}
                fastks.add(new Double((
                		(currentClose.doubleValue()-minlow.doubleValue())
                		/(maxhigh.doubleValue()-minlow.doubleValue()))*100.0));
                // Calculate Slow K if K Smooth > 1 and corresponding D
                int fastksize = fastks.size();
                if (fastksize>ksmooth) {
                	Double k = new Double(0.0);
                	for(int i = 1;i<=ksmooth;i++)
            			k = new Double(k.doubleValue() + ((Double)fastks.get(fastksize-i)).doubleValue());
                	k = new Double(k.doubleValue()/ksmooth);
                	ks.add(k);
                	// We require at least 3 K to calculate D
                	int kssize = ks.size();
                	if (kssize>2) {
                		Double d = new Double(0.0);
                    	for(int i = 1;i<=dsmooth;i++)
                			d = new Double(d.doubleValue() + ((Double)ks.get(kssize-i)).doubleValue());
            			d = new Double(d.doubleValue()/dsmooth);
                    	ds.add(d);
                		indicatorKGraphable.putY(date, (Double)ks.get(kssize-1));
                		indicatorDGraphable.putY(date, d);
                		added = true;
                	} else
                		ds.add(new Double(0.0));
                }
                closes.remove(0);
                // max or min being removed is the last high value, recalculate all
                if ((maxhigh.equals(highs.get(0)) || minlow.equals(lows.get(0))))
            		maxhigh = new Double(Double.MIN_VALUE);
                highs.remove(0);
                lows.remove(0);
            }
            if (!added) {
        		indicatorKGraphable.putY(date, new Double(0.0));
        		indicatorDGraphable.putY(date, new Double(0.0));
            }
            
        }
    }

    public String getSourceName() {
    	return close.getName();
    }
    
    public int getSourceType() {
    	return close.getType();
    }

    public Comparable getStartX() {
    	return close.getGraphable().getStartX();
    }
    
    public Comparable getEndX() {
    	return close.getGraphable().getEndX();
    }
    
    public Set getXRange() {
    	return close.getGraphable().getXRange();
    }
    
    public double[] getAcceptableMajorDeltas() {

	    double[] major = {0.001D, // 0.1c
			     0.01D, // 1c
			     0.1D, // 10c
			     1.0D, // $1
			     10.0D, // $10
			     100.0D, // $100
			     1000.0D}; // $1000
	    return major;	
    }

    public double[] getAcceptableMinorDeltas() {
	    double[] minor = {1D, 1.1D, 1.25D, 1.3333D, 1.5D, 2D, 2.25D,
			     2.5D, 3D, 3.3333D, 4D, 5D, 6D, 6.5D, 7D, 7.5D,
			     8D, 9D};
	    return minor;
    }

    public double getHighestY(List x) {
    	return 100.0;
    }

    // Lowest Y value is the lowest of both the moving averages
    public double getLowestY(List x) {
    	return 0.0d;
    }
    
    // Override vertical axis
    public String getYLabel(double value) {
    	return new Integer((int)value).toString();
    }
    
    public void setSettings(HashMap settings) {
        super.setSettings(settings);
        int period = KDGraphUI.getPeriod(settings);
        int ksmooth = KDGraphUI.getKSmooth(settings);
        int dsmooth = KDGraphUI.getDSmooth(settings);
        createKDGraph(low.getGraphable(), high.getGraphable(), close.getGraphable(), period, ksmooth, dsmooth);
    }

    /**
     * Return the name of this graph.
     *
     * @return <code></code>
     */
    public String getName() {
        return Locale.getString("KD");
    }

    public boolean isPrimary() {
        return false;
    }
    
    public String getToolTipText(Comparable x, int y, int yoffset, double verticalScale, double bottomLineValue) {
		   return this.getToolTipText(x);
    }
    
    public String getToolTipText(Comparable x) {
		TradingDate date = (TradingDate)x;
	
		return
		    new String("<html>"+
		    		date.toLongString()+"<p>" +
    				"<font color=green>K "+((float)((int)(indicatorKGraphable.getY(x).floatValue()*100))/100)+"</font>" +
		    		"&nbsp;"+
					"<font color=red>D "+((float)((int)(indicatorDGraphable.getY(x).floatValue()*100))/100)+"</font>" +
					"</p></html>");
    }
    
    public GraphUI getUI(HashMap settings) {
        return new KDGraphUI(settings);
    }

}


