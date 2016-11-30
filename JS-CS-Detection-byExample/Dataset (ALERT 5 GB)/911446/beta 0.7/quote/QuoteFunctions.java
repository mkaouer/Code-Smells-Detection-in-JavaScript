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

import org.mov.parser.EvaluationException;

/**
 * This class contains functions that manipulate stock quotes. By placing
 * them together in a single class, they can be used by both the 
 * Gondola language and charting functions.
 *
 * @author Andrew Leppard
 * @see QuoteFunctionSource
 */
public class QuoteFunctions {

    /** This is the default/recommended period for the RSI. */
    final public static int DEFAULT_RSI_PERIOD = 45;

    // This class cannot be instantiated
    private QuoteFunctions() {
        assert false;
    }

    /**
     * Find the standard deviation of the given values. This
     * algorthim will calculate the standard deviation of the first period
     * days. If a quote is missing on any of the days, then
     * that day will be skipped and the function will find the average of the shorter
     * period.
     *
     * @param source the source quottes
     * @param period the number of days to average
     * @return       the standard deviation
     * @see          org.mov.chart.graph.StandardDeviationGraph
     * @exception    EvaluationException if {@link QuoteBundleFunctionSource} is not
     *               allowed access to a quote. See {@link org.mov.analyser.gp.GPQuoteBundle}.
     */
    static public double sd(QuoteFunctionSource source, int period)
        throws EvaluationException {
        
        double average = avg(source, period);
        double deviationSum = 0.0D;
        int actualPeriod = 0;
        
        for(int i = 0; i < period; i++) {
            double value = source.getValue(i);

            if(!Double.isNaN(value)) {
                deviationSum += (value - average)*(value - average);
                actualPeriod++;
            }
        }

	if(actualPeriod > 2)
            deviationSum /= (actualPeriod - 1);

        return Math.sqrt(deviationSum);
    }

    // deprecated - remove me
    static public double sd(double[] values, int start, int end) {
	double average = avg(values, start, end);
	int period = end - start;

	double deviationSum = 0.0D;
	for(int i = start; i < end; i++) {
	    deviationSum += (values[i] - average)*(values[i] - average);
	}

        if(period > 2)
            deviationSum /= (period - 1);

	return Math.sqrt(deviationSum);
    }

    /**
     * Find the average of the given quotes. This function will calculate the average
     * of the first period days. If a quote is missing on any of the days, then
     * that day will be skipped and the function will find the average of the shorter
     * period.
     *
     * @param source source of quotes to average
     * @param period the number of days to average
     * @return       the average
     * @see          org.mov.chart.graph.MovingAverageGraph
     * @see          org.mov.parser.expression.AvgExpression
     * @exception    EvaluationException if {@link QuoteBundleFunctionSource} is not
     *               allowed access to a quote. See {@link org.mov.analyser.gp.GPQuoteBundle}.
     */
    static public double avg(QuoteFunctionSource source, int period)
        throws EvaluationException {

   	double avg = 0.0D;
        int actualPeriod = 0;

	// Sum quotes
        for(int i = 0; i < period; i++) {
            double value = source.getValue(i);

            if(!Double.isNaN(value)) {
                avg += value;
                actualPeriod++;
            }
	}

	// Average
        if(actualPeriod > 1)
            avg /= actualPeriod;

	return avg;
    }

    // deprecated - remove me
    static public double avg(double[] values, int start, int end) {
	double avg = 0.0D;
	int period = end - start;

	// Sum quotes
	for(int i = start; i < end; i++) {
	    avg += values[i];
	}

	// Average
        if(period > 1)
            avg /= period;

	return avg;
    }

    /**
     * Calculate the Pearson product-moment correlation between the two
     * variables. This will return a correlation co-efficient which is in the range of
     * -1 (negative correlation) through to (no correlation) through to 1 (perfect
     * correlation.
     *
     * The correlation co-efficient is calculated as follows:<pre>
     * r = sum(Zx * Zy)
     *     ------------
     *         N - 1
     *
     * Where Zx = X - E(X)
     *            --------
     *              Sx
     * </pre>Where E(X) is the mean of X and Sx is the standard deviation of X.
     *
     * Simillarly for Zy.
     *
     * @param x      values to test against
     * @param y      values to detect correlation against x
     * @param period number of days to analyse
     * @return       the correlation co-efficient
     * @see          org.mov.parser.expression.CorrExpression
     * @exception    EvaluationException if {@link QuoteBundleFunctionSource} is not
     *               allowed access to a quote. See {@link org.mov.analyser.gp.GPQuoteBundle}.
     */
    static public double corr(QuoteFunctionSource x, QuoteFunctionSource y, int period) 
        throws EvaluationException {

        double r = 0.0D;
        double ex = avg(x, period);
        double sx = sd(x, period);
        double ey = avg(y, period);
        double sy = sd(y, period);
        int actualPeriod = 0;

        if(sx != 0.0D && sy != 0.0D) {
            for(int i = 0; i < period; i++) {
                double xi = x.getValue(i);
                double yi = y.getValue(i);

                if(!Double.isNaN(xi) && !Double.isNaN(yi)) {
                    double zx = (xi - ex) / sx;
                    double zy = (yi - ey) / sy;

                    r += zx * zy;
                    actualPeriod++;
                }
            }

            if(actualPeriod > 1)
                r /= (actualPeriod - 1);
        }

        return r;
    }

    /**
     * Calculate the Relative Strength Indicator (RSI) value. Technical Analysis
     * by Martin J. Pring describes the RSI as:
     *
     * "It is a momentum indicator, or oscillator, that measures the relative internal
     *  strength of a security against <i>itself</i>....".
     *
     * The formula for the RSI is as follows:<pre>
     *
     *               100
     * RSI = 100 - ------
     *             1 + RS
     *
     *       average of x days' up closes
     * RS = ------------------------------
     *      average of x days' down closes
     *
     * </pre>To calculate an X day RSI you need X + 1 quote values. So make
     * the period argument one more day that the period of the RSI.
     *
     * @param source source of quotes to average
     * @param period one plus the period of the RSI
     * @return       RSI
     * @see          org.mov.chart.graph.RSIGraph
     * @see          org.mov.parser.expression.RSIExpression
     * @exception    EvaluationException if {@link QuoteBundleFunctionSource} is not
     *               allowed access to a quote. See {@link org.mov.analyser.gp.GPQuoteBundle}.
     */
    static public double rsi(QuoteFunctionSource source, int period)
        throws EvaluationException {

        double sumGain = 0.0D;
        double sumLoss = 0.0D;
        int numberGains = 0;
        int numberLosses = 0;
        double previous = Double.NaN;
        int i = 0;

        // Get the day before the RSI calculation
        while(Double.isNaN(previous) && i < period)
            previous = source.getValue(i++);

        // Calculate average day up and down closes
        while(i < period) {
            double value = source.getValue(i++);

            if(!Double.isNaN(value)) {
                if(value > previous) {
                    sumGain += (value - previous);
                    numberGains++;
                }
                
                else if(value < previous) {
                    sumLoss += (previous - value);
                    numberLosses++;
                }
                
                previous = value;
            }
        }

        // If the period is too small, return a neutral result
        if(numberLosses == 0 && numberGains == 0)
            return 50.0D;

        // If avg loss is 0, then RSI returns 100 by definition.
        else if(numberLosses == 0 || (sumLoss / numberLosses == 0.0D))
            return 100.0D;

        else {
            double avgGain;
            double avgLoss = sumLoss / numberLosses;

            if (numberGains == 0)
                avgGain = 0;
            else
                avgGain = sumGain / numberGains;

            double RS = avgGain / avgLoss;
            return 100.0D - 100.0D / (1.0D + RS);
        }
    }

    /**
     * Calculate the Exponential Moving Average (EMA) value. The Exponential Moving
     * Average is a weighted moving average where the most recent values are
     * weighted higher than the previous values.
     *
     * The formula for the EMA is as follows:</pre>
     *
     * EMA(current) = EMA(previous) + k * (day close - EMA(previous))
     *
     * </pre>Where EMA(current) is the current EMA value you are calculating,
     * EMA(previous) is the previous value and <code>k</code> is a smoothing
     * constant.
     *
     * @param source the source of quotes to average
     * @param period the number of days to analyse
     * @param smoothingConstant a smoothing constant
     * @return       the exponential moving average
     * @see          org.mov.chart.graph.ExpMovingAverageGraph
     * @exception    EvaluationException if {@link QuoteBundleFunctionSource} is not
     *               allowed access to a quote. See {@link org.mov.analyser.gp.GPQuoteBundle}.
     */
    static public double ema(QuoteFunctionSource source, int period, double smoothingConstant)
        throws EvaluationException {

	double EMA = 0.0D;
	double previousEMA = 0.0D;
	int actualPeriod = 0;

        for(int i = 0; i < period; i++) {
            double value = source.getValue(i);

            if(!Double.isNaN(value)) {
                if (actualPeriod >= 1)
                    EMA = previousEMA + smoothingConstant * (value - previousEMA);

                else
                    EMA = value;
                
                previousEMA = EMA;
                actualPeriod++;
	    }
	}	
	return EMA;
    }

    /**
     * Calculate the Moving Average Convergence Divergence (MACD) value.
     * The Moving Average Convergence Divergence is the remainder of
     * the 26 days EMA and the 12 days EMA.
     * The smoothing constant for the EMA functions is set to 0.1.
     *
     * The formula for the MACD is as follows:</pre>
     *
     * MACD = EMA(26) - EMA(12)
     *
     * </pre>Where EMA(26) is the 26 days EMA and EMA(12) is the 12 days EMA.
     *
     * @param sourceSlow the source of quotes used by EMA to average (slow average)
     * @param sourceFast the source of quotes used by EMA to average (fast average)
     * @return       the moving average convergence divergence
     * @see          org.mov.chart.graph.MACDGraph
     * @exception    EvaluationException if {@link QuoteBundleFunctionSource} is not
     *               allowed access to a quote. See {@link org.mov.analyser.gp.GPQuoteBundle}.
     */
    static public double macd(QuoteFunctionSource sourceSlow, QuoteFunctionSource sourceFast)
        throws EvaluationException {

	double MACD = 0.0D;
	double smoothingConstant = 0.1D;
	int periodSlow = 26;
	int periodFast = 12;

        MACD = ema(sourceSlow, periodSlow, smoothingConstant) - ema(sourceFast, periodFast,
                                                                    smoothingConstant);
        
	return MACD;
    }

    /**
     * Calculate the Momentum value.
     * The Moving Average Convergence Divergence is the remainder of
     * the today value and the period delayed value.
     *
     * The formula for the Momentum is as follows:</pre>
     *
     * Momentum = Quote(Today) - Quote(Today+1-period)
     *
     * </pre>Where Quote is got from the input parameter: source.
     *
     * @param source the source of quotes
     * @return       the momentum
     * @see          org.mov.chart.graph.MomentumGraph
     * @exception    EvaluationException if {@link QuoteBundleFunctionSource} is not
     *               allowed access to a quote. See {@link org.mov.analyser.gp.GPQuoteBundle}.
     */
    static public double momentum(QuoteFunctionSource source, int period)
        throws EvaluationException {

	double momentum = 0.0D;
        double value;
        double valueDay;
        
        valueDay = source.getValue(0);
        
        if(!Double.isNaN(valueDay)) {
            for(int i = period-1; i > 0; i--) {
                value = source.getValue(i);

                if(!Double.isNaN(value)) {
                    momentum = value - valueDay;
                    i = 0;
                }
            } 
        }
        
	return momentum;
    }

    /**
     * Calculate the On Balance Volume (OBV) value.
     * The On Balance Volume is counted adding or subtracting the day volume
     * from range until today, starting from an initial value.
     *
     * The formula for the OBV is as follows:</pre>
     *
     * if close(current)>open(current):
     * OBV(current) = OBV(previous) + Volume(current)
     * if close(current)<open(current):
     * OBV(current) = OBV(previous) - Volume(current)
     *
     * @param sourceOpen the source of open quotes
     * @param sourceClose the source of close quotes
     * @param sourceVolume the source of volumes
     * @param range the range which we calculate over
     * @param initialValue the starting value of OBV
     * @return       the on balance volume value
     * @see          org.mov.chart.graph.OBVGraph
     * @exception    EvaluationException if {@link QuoteBundleFunctionSource} is not
     *               allowed access to a quote. See {@link org.mov.analyser.gp.GPQuoteBundle}.
     */
    static public int obv(QuoteFunctionSource sourceOpen,
                          QuoteFunctionSource sourceClose,
                          QuoteFunctionSource sourceVolume,
                          int range, int initialValue)
        throws EvaluationException {

	int OBV = initialValue;
        
        for(int i = range-1; i >= 0; i--) {
            double open = sourceOpen.getValue(i);
            double close = sourceClose.getValue(i);
            double volume = sourceVolume.getValue(i);

            if((!Double.isNaN(open)) && (!Double.isNaN(close)) && (!Double.isNaN(volume))) {
                if(close>open)
                    OBV += new Double(volume).intValue();
                else if(close<open)
                    OBV -= new Double(volume).intValue();
            }
	}
        
	return OBV;
    }

    /**
     * Calculate the upper band of the bollinger graph. The upper band can
     * be calculated by:</pre>
     *
     * BollingerUpper = Average + 2 * SD
     *
     * </pre>Where SD is the standard deviation.
     *
     * @param source the source of quotes
     * @param period the number of days to analyse
     * @return       the upper bollinger band
     * @see          org.mov.chart.graph.BollingerBandsGraph
     * @exception    EvaluationException if {@link QuoteBundleFunctionSource} is not
     *               allowed access to a quote. See {@link org.mov.analyser.gp.GPQuoteBundle}.
     */
    static public double bollingerUpper(QuoteFunctionSource source, int period)
        throws EvaluationException {

        double sd = sd(source, period);
        double avg = avg(source, period);
        return (avg + 2.0D * sd);
    }

    /**
     * Calculate the lower band of the bollinger graph. The lower band can
     * be calculated by:</pre>
     *
     * BollingerLower = Average - 2 * SD
     *
     * </pre>Where SD is the standard deviation.
     *
     * @param source the source of quotes
     * @param period the number of days to analyse
     * @return       the lower bollinger band
     * @see          org.mov.chart.graph.BollingerBandsGraph
     * @exception    EvaluationException if {@link QuoteBundleFunctionSource} is not
     *               allowed access to a quote. See {@link org.mov.analyser.gp.GPQuoteBundle}.
     */
    static public double bollingerLower(QuoteFunctionSource source, int period)
        throws EvaluationException {

        double sd = sd(source, period);
        double avg = avg(source, period);
        return (avg - 2.0D * sd);
    }

    public static final double roundDouble(double d, int places) {
        return Math.round(d * Math.pow(10, (double) places)) / Math.pow(10,
            (double) places);
    }
    
    /**
     * Calculate the line of best fit of the data given by source. 
     * 
     * using the formula:
     * slope = period * Sum(xy) - Sum(x)Sum(y) / period * Sum(x^2) - (Sum(x))^2
     * intercept = ( Sum(y) - slope * Sum(x) ) / period
     * @param source the source of quotes
     * @param period the number of days to analyse
     * @return the value of the trend at the end of period     

    */

    static public double bestFit(QuoteFunctionSource source, int period) 
	throws EvaluationException
    {
	
	double value;
	int i;
	double slope = 0.0;
	double intercept = 0.0;
	double sumXY, sumX , sumY, sumXSq, sqSumX;

	sumXY = sumX = sumY = sumXSq = sqSumX = 0.0;
	
	for(i = 1; i <= period; i++) {
	    value = source.getValue(i-1);

	    if(!Double.isNaN(value)) {
		//slope
		sumXY += (i * value);
		sumX += i;
		sumY += value;		
				
		sumXSq += (i * i);
	    }
	}

	sqSumX = sumX * sumX;

	slope = ( (period * sumXY) - (sumX * sumY)) / ( (period * sumXSq) - sqSumX);		
	intercept = (sumY - slope * sumX) / period;

	double rv = (slope * (period+1) + intercept);
	return (slope * (period+1) + intercept);
	
    }
    
    /**
     * Return the equation of the line of best fit of the data given by source. 
     * Uses the same formula as bestFit, but returns slope and intercept
     * so that it can be used on charts.
     * 
     * 
     * @param source the source of quotes
     * @param period the number of days to analyse
     * @return the value of the trend at the end of period     

    */

    static public double[] bestFitFunction(QuoteFunctionSource source, int start, int period) 
	throws EvaluationException
    {
	
	double value;
	int i;
	double slope = 0.0;
	double intercept = 0.0;
	double sumXY, sumX , sumY, sumXSq, sqSumX;
	double rv[] = new double[2];
	int end;

	sumXY = sumX = sumY = sumXSq = sqSumX = 0.0;
	
	for(i = 1; i <= period; i++) {
	    value = source.getValue(i-1);

	    if(!Double.isNaN(value)) {
		//slope
		sumXY += (i * value);
		sumX += i;
		sumY += value;		
				
		sumXSq += (i * i);
	    }
	}

	sqSumX = sumX * sumX;
	
	slope = ( (period * sumXY) - (sumX * sumY)) / ( (period * sumXSq) - sqSumX);		
	intercept = (sumY - slope * sumX) / period;
	
	rv[0] = slope;
	rv[1] = intercept;
	
	return rv;
	
    }
}
