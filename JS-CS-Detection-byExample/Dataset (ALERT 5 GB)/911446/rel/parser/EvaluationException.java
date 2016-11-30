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

package nz.org.venice.parser;

import nz.org.venice.util.Locale;

/**
 * An exception which is thrown when there is a problem executing an
 * expression. Since this exception can be thrown a lot by the GP,
 * and a stack trace is not used, common compile time exceptions
 * have been made static to avoid generating unused stack traces.
 * See Java Performance Tuning for more information.
 *
 * @author Andrew Leppard
 */
public class EvaluationException extends ExpressionException {

    /** An exception which is thrown when per performa a divide by zero operation. */
    public static EvaluationException DIVIDE_BY_ZERO_EXCEPTION =
        new EvaluationException(Locale.getString("DIVIDE_BY_ZERO_ERROR"));

    // The next two errors should never appear to the user so they aren't
    // localised.

    /** An exception which is thrown when the GP tries to access a date that is
        too far into the future. */
    public static EvaluationException FUTURE_DATE_EXCEPTION =
        new EvaluationException("Future date");

    /** An exception which is thrown when the GP tries to access a date that is
        too distant in the past. */
    public static EvaluationException PAST_DATE_EXCEPTION =
        new EvaluationException("Date too far into past");

    /** An exception which is thrown on an invalid avg() range. */
    public static EvaluationException AVG_RANGE_EXCEPTION =
        new EvaluationException(Locale.getString("AVG_RANGE_ERROR"));

    /** An exception which is thrown on an invalid bol_lower() range. */
    public static EvaluationException BBL_RANGE_EXCEPTION =
        new EvaluationException(Locale.getString("BBL_RANGE_ERROR"));

    /** An exception which is thrown on an invalid bol_upper() range. */
    public static EvaluationException BBU_RANGE_EXCEPTION =
        new EvaluationException(Locale.getString("BBU_RANGE_ERROR"));

    /** An exception which is thrown on an invalid corr() range. */
    public static EvaluationException CORR_RANGE_EXCEPTION =
        new EvaluationException(Locale.getString("CORR_RANGE_ERROR"));

    /** An exception which is thrown on an invalid ema() range. */
    public static EvaluationException EMA_RANGE_EXCEPTION =
        new EvaluationException(Locale.getString("EMA_RANGE_ERROR"));
    
    /** An exception which is thrown on an invalid max() range. */
    public static EvaluationException MAX_RANGE_EXCEPTION =
        new EvaluationException(Locale.getString("MAX_RANGE_ERROR"));

    /** An exception which is thrown on an invalid min() range. */
    public static EvaluationException MIN_RANGE_EXCEPTION =
        new EvaluationException(Locale.getString("MIN_RANGE_ERROR"));

    /** An exception which is thrown on an invalid momentum() range. */
    public static EvaluationException MOMENTUM_RANGE_EXCEPTION =
        new EvaluationException(Locale.getString("MOMENTUM_RANGE_ERROR"));

    /** An exception which is thrown on an invalid obv() range. */
    public static EvaluationException OBV_RANGE_EXCEPTION =
        new EvaluationException(Locale.getString("OBV_RANGE_ERROR"));

    /** An exception which is thrown on an invalid rsi() range. */
    public static EvaluationException RSI_RANGE_EXCEPTION =
        new EvaluationException(Locale.getString("RSI_RANGE_ERROR"));

    /** An exception which is thrown on an invalid sd() range. */
    public static EvaluationException SD_RANGE_EXCEPTION =
        new EvaluationException(Locale.getString("SD_RANGE_ERROR"));

    /** An exception which is thrown on an invalid sum() range. */
    public static EvaluationException SUM_RANGE_EXCEPTION =
        new EvaluationException(Locale.getString("SUM_RANGE_ERROR"));

    /** An exception which is thrown on an invalid avg() offset. */
    public static EvaluationException AVG_OFFSET_EXCEPTION =
        new EvaluationException(Locale.getString("AVG_OFFSET_ERROR"));

    /** An exception which is thrown on an invalid bol_upper() offset. */
    public static EvaluationException BBL_OFFSET_EXCEPTION =
        new EvaluationException(Locale.getString("BBL_OFFSET_ERROR"));

    /** An exception which is thrown on an invalid bbu() offset. */
    public static EvaluationException BBU_OFFSET_EXCEPTION =
        new EvaluationException(Locale.getString("BBU_OFFSET_ERROR"));

    /** An exception which is thrown on an invalid corr() offset. */
    public static EvaluationException CORR_OFFSET_EXCEPTION =
        new EvaluationException(Locale.getString("CORR_OFFSET_ERROR"));

    /** An exception which is thrown on an invalid ema() offset. */
    public static EvaluationException EMA_OFFSET_EXCEPTION =
        new EvaluationException(Locale.getString("EMA_OFFSET_ERROR"));

    /** An exception which is thrown on an invalid lag() offset. */
    public static EvaluationException LAG_OFFSET_EXCEPTION =
        new EvaluationException(Locale.getString("LAG_OFFSET_ERROR"));

    /** An exception which is thrown on an invalid macd() offset. */
    public static EvaluationException MACD_OFFSET_EXCEPTION =
        new EvaluationException(Locale.getString("MACD_OFFSET_ERROR"));

    /** An exception which is thrown on an invalid max() offset. */
    public static EvaluationException MAX_OFFSET_EXCEPTION =
        new EvaluationException(Locale.getString("MAX_OFFSET_ERROR"));

    /** An exception which is thrown on an invalid min() offset. */
    public static EvaluationException MIN_OFFSET_EXCEPTION =
        new EvaluationException(Locale.getString("MIN_OFFSET_ERROR"));

    /** An exception which is thrown on an invalid momentum() offset. */
    public static EvaluationException MOMENTUM_OFFSET_EXCEPTION =
        new EvaluationException(Locale.getString("MOMENTUM_OFFSET_ERROR"));

    /** An exception which is thrown on an invalid obv() offset. */
    public static EvaluationException OBV_OFFSET_EXCEPTION =
        new EvaluationException(Locale.getString("OBV_OFFSET_ERROR"));

    /** An exception which is thrown on an invalid rsi() offset. */
    public static EvaluationException RSI_OFFSET_EXCEPTION =
        new EvaluationException(Locale.getString("RSI_OFFSET_ERROR"));

    /** An exception which is thrown on an invalid sd() offset. */
    public static EvaluationException SD_OFFSET_EXCEPTION =
        new EvaluationException(Locale.getString("SD_OFFSET_ERROR"));

    /** An exception which is thrown on an invalid sum() offset. */
    public static EvaluationException SUM_OFFSET_EXCEPTION =
        new EvaluationException(Locale.getString("SUM_OFFSET_ERROR"));

    /** An exception which is thrown on an invalid ema() smoothing constant. */
    public static EvaluationException EMA_SMOOTHING_EXCEPTION =
        new EvaluationException(Locale.getString("EMA_SMOOTHING_ERROR"));

    /** An exception which is thrown when trying to calculate the square
        root of a negative number. */
    public static EvaluationException SQUARE_ROOT_NEGATIVE_EXCEPTION =
        new EvaluationException(Locale.getString("SQUARE_ROOT_NEGATIVE_ERROR"));

    /** An exception which is thrown when trying to calculate the logarithm
        of a negative number. */
    public static EvaluationException LOGARITHM_NEGATIVE_EXCEPTION =
        new EvaluationException(Locale.getString("LOGARITHM_NEGATIVE_EXCEPTION"));

    /** An exception which is thrown when the result of a calculation is undefined. e.g. Calculating the average of the empty set */
    public static EvaluationException UNDEFINED_RESULT_EXCEPTION =
        new EvaluationException(Locale.getString("UNDEFINED_RESULT_EXCEPTION"));

    /** An exception which is thrown when the result of a calculation for a day/symbol exceeds the time limit. */
    public static EvaluationException EVAL_TIME_TOO_LONG_EXCEPTION =
        new EvaluationException(Locale.getString("EVAL_TIME_TOO_LONG"));

    /** An exception which is thrown when the function call depth exceeds the limit. e.g. possibly due to infinite recursion */
    public static EvaluationException STACK_DEPTH_EXCEEDED_EXCEPTION =
        new EvaluationException(Locale.getString("STACK_OVERFLOW_EXCEPTION"));

    /** An exception which is thrown when a HaltExpression is evaluated. */
    public static EvaluationException EVALUATION_HALTED_EXCEPTION =
        new EvaluationException(Locale.getString("EVALUATION_HALTED_EXCEPTION"));

    private String message = null;

    /**
     * Create a new evaluation exception with the given error reason.
     * Make any exception without a run-time error message static so the
     * GP doesn't waste time building stack traces.
     *
     * @param	reason	the reason the execution failed
     */
    public EvaluationException(String reason) {
	super(reason);
    }
       
    public void setMessage(Expression e, String label, double value) {
	message = super.getReason();
	if (e.getParseMetadata() != null) {
	    String lineNumber = e.getParseMetadata().getLineForExpression(e);
	    
	    message = super.getReason() + label + " : " + value + " at line: " + lineNumber;        
	}
    }

    public String getReason() {
	return (message != null) ? message : super.getReason();
    }
}
