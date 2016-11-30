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
import java.lang.String;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mov.chart.Graphable;
import org.mov.chart.GraphTools;
import org.mov.util.Locale;
import org.mov.util.TradingDate;
import org.mov.quote.MissingQuoteException;
import org.mov.quote.QuoteSourceManager;
import org.mov.ui.ProgressDialog;
import org.mov.ui.ProgressDialogManager;

/**
 * Advance/Decline graph. This graphs the Advance/Decline market indicator. This
 * graph is used to indicate whether trends in the market are short or long lived.
 * If the market is going up and the advance/decline line is going down, there
 * is something wrong.
 */
public class AdvanceDeclineGraph implements Graph {

    private Graphable advanceDecline;

    // Graph starts at an arbitary value
    private static final int START_VALUE = 0;

    /**
     * Create a new Advance/Decline graph.
     */
    public AdvanceDeclineGraph() {
	advanceDecline = createAdvanceDecline();
    }

    public void render(Graphics g, Color colour, int xoffset, int yoffset,
		       double horizontalScale, double verticalScale,
		       double bottomLineValue, List xRange) {

	GraphTools.renderLine(g, advanceDecline, xoffset, yoffset,
			      horizontalScale,
			      verticalScale, bottomLineValue, xRange);
    }

    public String getToolTipText(Comparable x, int y, int yoffset,
				 double verticalScale,
				 double bottomLineValue)
    {
	// we will give out the number that advanced and the number that
	// declined

	return null; // we never give tool tip information
    }

    /**
     * Get the first X value that this graph will draw.
     *
     * @return	X value of the first x coordinate in the default
     *		<code>GraphSource</code>'s <code>Graphable</code>
     */
    public Comparable getStartX() {
	return advanceDecline.getStartX();
    }

    /**
     * Get the last X value that this graph will draw.
     *
     * @return	X value of the last x coordinate in the default
     *		<code>GraphSource</code>'s <code>Graphable</code>
     */
    public Comparable getEndX() {
	return advanceDecline.getEndX();
    }

    /**
     * Get all X values that this graph will draw.
     *
     * @return	X values in the default <code>GraphSource</code>'s
     *		<code>Graphable</code>
     */
    public Set getXRange() {
	return advanceDecline.getXRange();
    }

    /**
     * Convert the Y value to a label to be displayed in the vertical
     * axis.
     *
     * @param	value	y value
     * @return	the Y label text that the default <code>GraphSource</code>
     *		would display
     */
    public String getYLabel(double value) {
	return Integer.toString((int)value);
    }

    /**
     * Return the Y value for the given X value.
     *
     * @param	x value
     * @return	Y value of the default <code>GraphSource</code>
     */
    public Double getY(Comparable x) {
	return advanceDecline.getY(x);
    }

    /**
     * Return the highest Y value in the given X range.
     *
     * @param	x	range of X values
     * @return	the highest Y value of the default <code>GraphSource</code>
     */
    public double getHighestY(List x) {
	return advanceDecline.getHighestY(x);
    }

    /**
     * Return the loweset Y value in the given X range.
     *
     * @param	x	range of X values
     * @return	the lowest Y value of the default <code>GraphSource</code>
     */

    public double getLowestY(List x) {
	return advanceDecline.getLowestY(x);
    }

    /**
     * Return an array of acceptable major deltas for the vertical
     * axis.
     *
     * @return	an array of doubles representing the minor deltas
     *		of the default <code>GraphSource</code>
     */
    public double[] getAcceptableMajorDeltas() {
	double[] major = {1.0D, // 1 point
			 10.0D, // 10 points
			 100.0D, // 100 points
			 1000.0D, // 1000 points
			 10000.0D, // 10,000 points
			 100000.0D}; // 100,000 points
	return major;	
    }

    /**
     * Return an array of acceptable minor deltas for the vertical
     * axis.
     *
     * @return	an array of doubles representing the minor deltas
     *		of the default <code>GraphSource</code>
     * @see	Graph#getAcceptableMajorDeltas
     */
    public double[] getAcceptableMinorDeltas() {
	double[] minor = {1D, 1.1D, 1.25D, 1.3333D, 1.5D, 2D, 2.25D,
			 2.5D, 3D, 3.3333D, 4D, 5D, 6D, 6.5D, 7D, 7.5D,
			 8D, 9D};
	return minor;
    }

    /**
     * Return the annotations for this graph or <code>null</code> if it
     * does not have any. The annotations should be in a map of X values
     * to <code>String</code> values.
     *
     * @return	map of annotations
     */
    public HashMap getAnnotations() {
        return null;
    }

    /**
     * Return if this graph has any annotations.
     *
     * @return	<code>true</code> if this graph has annotations;
     *		<code>false</code> otherwise
     */
    public boolean hasAnnotations() {
        return false;
    }

    /**
     * Create Advance/Decline graphable.
     */
    public static Graphable createAdvanceDecline() {
	Graphable advanceDecline = new Graphable();

        // Get a list of all dates between the first and last
        TradingDate firstDate = QuoteSourceManager.getSource().getFirstDate();
        TradingDate lastDate = QuoteSourceManager.getSource().getLastDate();
        List dates = TradingDate.dateRangeToList(firstDate, lastDate);

	Thread thread = Thread.currentThread();
	ProgressDialog progress = ProgressDialogManager.getProgressDialog();
	progress.setIndeterminate(false);
	progress.setMaximum(dates.size());
	progress.setProgress(0);
        progress.setMaster(true);
	progress.show(Locale.getString("CALCULATING_ADVANCE_DECLINE"));

	int cumulativeAdvanceDecline = START_VALUE;

	// Iterate over every date
        for(Iterator iterator = dates.iterator(); iterator.hasNext();) {
	    TradingDate date = (TradingDate)iterator.next();

            try {
                int todayAdvanceDecline =
                    QuoteSourceManager.getSource().getAdvanceDecline(date);

		cumulativeAdvanceDecline += todayAdvanceDecline;

		advanceDecline.putY((Comparable)date,
				    new Double(cumulativeAdvanceDecline));
            }
            catch(MissingQuoteException e) {
                // Don't have this date in the quote source... no problem
            }

            // Stop if the user hit cancel
            if(thread.isInterrupted())
                break;

	    progress.increment();
	}

	ProgressDialogManager.closeProgressDialog(progress);	

	return advanceDecline;
    }

    public HashMap getSettings() {
        return new HashMap();
    }

    public void setSettings(HashMap settings) {
        // ignore
    }

    public GraphUI getUI(HashMap settings) {
        // No user interface
        return null;
    }

    /**
     * Return the name of this graph.
     *
     * @return	<code>Advance/Decline</code>
     */
    public String getName() {
	return Locale.getString("ADVANCE_DECLINE");
    }

    public String getSourceName() {
        return Locale.getString("ADVANCE_DECLINE");
    }

    public boolean isPrimary() {
        return true;
    }
}


