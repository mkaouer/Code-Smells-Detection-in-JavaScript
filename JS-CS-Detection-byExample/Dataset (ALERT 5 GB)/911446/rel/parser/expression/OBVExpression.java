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

package nz.org.venice.parser.expression;

import nz.org.venice.parser.EvaluationException;
import nz.org.venice.parser.Expression;
import nz.org.venice.parser.TypeMismatchException;
import nz.org.venice.parser.Variables;
import nz.org.venice.quote.Quote;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.QuoteBundleFunctionSource;
import nz.org.venice.quote.QuoteFunctions;
import nz.org.venice.quote.Symbol;
import nz.org.venice.util.VeniceLog;

/**
 * An expression which finds the OBV (On Balance Volume) over a given trading period.
 *
 * @author Alberto Nacher
 */
public class OBVExpression extends TernaryExpression {

    /**
     * Create a new On Balance Volume (OBV) expression for the given <code>quote</code> kind,
     * for the given number of <code>days</code>, starting with <code>lag</code> days away.
     *
     * @param	initialValue	the initialValue the OBV start with
     * @param	days	the number of days to count over
     * @param	lag	the offset from the current day
     */
    public OBVExpression(Expression days, Expression lag, Expression initialValue) {
        super(days, lag, initialValue);
    }

    public double evaluate(Variables variables, QuoteBundle quoteBundle, Symbol symbol, int day)
	throws EvaluationException {

        // Extract arguments
	int period = (int)getChild(0).evaluate(variables, quoteBundle, symbol, day);
        if(period <= 0) {	    
            EvaluationException e = EvaluationException.OBV_RANGE_EXCEPTION;
	    e.setMessage(this, "", period);
	    throw e;

	}
        int offset = (int)getChild(1).evaluate(variables, quoteBundle, symbol, day);
        if (offset > 0) {
            EvaluationException e = EvaluationException.OBV_OFFSET_EXCEPTION;
	    e.setMessage(this, "", offset);
	    throw e;
	}
        int initialValue = (int)getChild(2).evaluate(variables, quoteBundle, symbol, day);

        // Calculate and return the OBV.
        QuoteBundleFunctionSource sourceOpen =
            new QuoteBundleFunctionSource(quoteBundle, symbol, Quote.DAY_OPEN, day, offset, period);
        QuoteBundleFunctionSource sourceClose =
            new QuoteBundleFunctionSource(quoteBundle, symbol, Quote.DAY_CLOSE, day, offset, period);
        QuoteBundleFunctionSource sourceVolume =
            new QuoteBundleFunctionSource(quoteBundle, symbol, Quote.DAY_VOLUME, day, offset, period);

        return QuoteFunctions.obv(sourceOpen, sourceClose, sourceVolume, period, initialValue);
    }

    public String toString() {	
	/*
	assert getChild(0) != null;
	assert getChild(1) != null;
	assert getChild(2) != null;
	*/
		
	String rv = "obv(";

	for (int i = 0; i < getChildCount(); i++) {
	    rv += (getChild(i) == null) ? "(null)" : getChild(i).toString();
	    if (i < getChildCount()-1) {
		rv += ",";
	    }
	}
	rv += ")";
	return rv;

	/*
	return new String("obv(" + 
			  getChild(0).toString() + ", " +
			  getChild(1).toString() + ", " +
			  getChild(2).toString() + ")");
	*/
    }
    
    /*
    public boolean testSimplify() {
	for (int i = 0; i < getChildCount(); i++) {
	    if (getChild(i) == null) {
		return false;
	    }
	}
	return true;
    }
    */

    public int checkType() throws TypeMismatchException {
	assert getChild(0) != null;
	assert getChild(1) != null;
	assert getChild(2) != null;

	// All inputs must be integer values.
	if(getChild(0).checkType() == INTEGER_TYPE &&
	   getChild(1).checkType() == INTEGER_TYPE &&
	   getChild(2).checkType() == INTEGER_TYPE)
	    return getType();
	else {
	    String types = 
		getChild(0).getType() + " , " + 
		getChild(1).getType() + " , " + 
		getChild(2).getType();

	    String expectedTypes =
		INTEGER_TYPE + " , " + 
		INTEGER_TYPE + " , " + 
		INTEGER_TYPE;
	    
	    

	    throw new TypeMismatchException(this, types, expectedTypes);
	}
    }

    public int getType() {
        return INTEGER_TYPE;
    }

    public Object clone() {
        return new OBVExpression((Expression)getChild(0).clone(), 
                                 (Expression)getChild(1).clone(),
                                 (Expression)getChild(2).clone());
    }
}
