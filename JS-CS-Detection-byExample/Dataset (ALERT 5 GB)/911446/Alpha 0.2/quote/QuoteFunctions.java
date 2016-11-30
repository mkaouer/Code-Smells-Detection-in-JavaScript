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

package org.mov.quote;

/**
 * Interim class containing statistical functions to be performed on
 * stock quotes. This class will eventually be
 * broken up so that each stat gets put into its own class, e.g.
 * average.java, RSI.java etc. They will then be put into the stats package.
 * The gondola language will be made so that it can turn any stat
 * class into a function.
 */
public class QuoteFunctions {

    /**
     * Find the standard deviation of the given values. This
     * algorthim will calculate the standard deviation on the values in
     * the given array in the range [start, end]. Start inclusive, end exclusive.
     *
     * @param values array of values to analyse 
     * @param start  analyse values from start
     * @param end    to end
     * @return the standard deviation
     */
    static public float sd(float[] values, int start, int end) {
	double average = avg(values, start, end);
	int period = end - start;

	double deviationSum = 0;
	for(int i = start; i < end; i++) {
	    deviationSum += (values[i] - average)*(values[i] - average);
	}

	return (float)Math.sqrt(deviationSum / period);
    }

    /**
     * Find the average of the given values. This
     * algorthim will calculate the average on the values in
     * the given array in the range [start, end]. Start inclusive, end exclusive.
     *
     * @param values array of values to analyse 
     * @param start  analyse values from start
     * @param end    to end
     * @return the average
     */
    static public float avg(float[] values, int start, int end) {
	float avg = 0;
	int period = end - start;

	// Sum quotes
	for(int i = start; i < end; i++) {
	    avg += values[i];
	}

	// Average
	avg /= period;

	return avg;
    }

    /**
     * RSI algorithm 
     * @param	quoteBundle	the quote cache to read the quotes from.
     * @param	symbol	the symbol to use.
     * @param	quote	the quote type we are interested in, e.g. DAY_OPEN.
     * @param	days	Number of days to run RSI calculation over
     * @param	lastDay	fast access date offset in cache.
     * @return  the RSI value
     */
    static public float rsi(QuoteBundle quoteBundle, Symbol symbol, 
			    int quote, int days, int lastDay) {
	System.err.println("Entering RSI for symbol "+symbol);

	//	Vector v = new Vector();

	// Determine the average up and down values for the days, divide by <period>
	float upvalues   = 0;
	float downvalues = 0;
	
	float last = 0;
	float current;
	for(int i = lastDay - days + 1; i <= lastDay; i++) {
	    //	for(int i = 0; i < v.size(); i++) {

	    try {
		current = quoteBundle.getQuote(symbol, quote, i);
		System.err.println("offset "+i+", value "+current);
		if (i < 0) {
		    if (current > last)
			upvalues += current;
		    else if (current < last)
			downvalues += current;
		}
		last = current;
	    }
	    catch(MissingQuoteException e) {
		// ignore
	    }
	}

	float up_average = upvalues / days;
	float down_average = downvalues / days;
	System.out.println(" up: "+up_average+
			   " down: "+down_average);
	// RS = (up average / down average) + 1
	float strength = (up_average / down_average) + 1;
	System.err.println("s1: "+strength);
	// N = 100 / RS
	strength = 100 / strength;
	
	// RSI = 100 - N
	strength = 100 - strength;
	System.err.println("Value: "+strength);
	return strength;
    }

}

