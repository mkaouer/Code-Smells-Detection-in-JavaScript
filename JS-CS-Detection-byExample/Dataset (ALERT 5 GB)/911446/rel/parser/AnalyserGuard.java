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

import java.util.HashMap;

import nz.org.venice.quote.Symbol;
import nz.org.venice.parser.Token;
import nz.org.venice.parser.Expression;

import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;

/** 
 * This class keeps track of the running time of expressions which can forever 
 * The check is done per date
 * @author Mark Hummel
 */

public class AnalyserGuard {

    private HashMap loopExpressionMap; //Contains expression eval runtime
    private HashMap functionExpressionMap; //Keeps stack count

    public static int maxRunTime = 60; //In seconds, ie one minute
    public static int maxStackDepth = 50;

    private static AnalyserGuard instance = null;

    public static synchronized AnalyserGuard getInstance() {
	if (instance == null) {
	    instance = new AnalyserGuard();
	} 
	return instance;
    }

    private AnalyserGuard() {
	loopExpressionMap = new HashMap();
	functionExpressionMap = new HashMap();
    }

    /**
     * Return true if the given expression evaluation exceeds the limit.
     * @param expression The expression being evaluated
     * @param expressionID Identifier for the expression
     * @param symbol The symbol on which the rule is being evaluated
     * @param day Integer offset of the quote bundle. Included so the limit
     * exceeded doesn't trigger if some progress is being made.
     */

    public boolean evaluationTimeElapsed(Expression expression, 
					 UUID expressionID,
					 Symbol symbol, int day) {
	String key = getKey(expression, expressionID, symbol, day);
	Long startTimeLong =  (Long)loopExpressionMap.get(key);

	if (startTimeLong == null) {
	    return false;
	}

	long now = System.currentTimeMillis();
	long diff = Math.abs(now - startTimeLong.longValue());
	int elapsedSeconds = (int)(diff / 1000);

	if (elapsedSeconds > maxRunTime) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Return true if the function call depth exceeds the limit. .
     * @param expression The expression being evaluated
     * @param expressionID Identifier for the expression
     * @param symbol The symbol on which the rule is being evaluated
     * @param day Integer offset of the quote bundle. Included so the limit
     * exceeded doesn't trigger if some progress is being made.
     */

    public boolean stackDepthLimitExceeded(Expression expression, 
					   UUID expressionID,
					   Symbol symbol, int day) {

	if (true)
	    return false;

	String key = getKey(expression, expressionID, symbol, day);

	Integer stackDepthInt = (Integer)functionExpressionMap.get(key);
	if (stackDepthInt == null) {
	    return false;
	} else {
	    if (stackDepthInt.intValue() > maxStackDepth) {
		return true;
	    } else {
		return false;
	    }
	}	
    }
    
    /**
     * Record that a loop has begun evaluation. 
     * @param expression The expression being evaluated
     * @param expressionID Identifier for the expression
     * @param symbol The symbol on which the rule is being evaluated
     * @param day Integer offset of the quote bundle. Included so the limit
     * exceeded doesn't trigger if some progress is being made.
     */

    public void startLoop(Expression expression, UUID expressionID, 
			  Symbol symbol, int day) {
	long now = System.currentTimeMillis();

	String key = getKey(expression, expressionID, symbol, day);
	//Don't want to "restart" the loop timer.
	assert loopExpressionMap.get(key) == null;

	loopExpressionMap.put(key,new Long(now));
	
    }


    /**
     * Record that a loop has completed. 
     * @param expression The expression being evaluated
     * @param expressionID Identifier for the expression
     * @param symbol The symbol on which the rule is being evaluated
     * @param day Integer offset of the quote bundle. Included so the limit
     * exceeded doesn't trigger if some progress is being made.
     */
    public void finishLoop(Expression expression, UUID expressionID, 
			   Symbol symbol, int day) {
	String key = getKey(expression, expressionID, symbol, day);
	
	assert loopExpressionMap.get(key) != null;
	loopExpressionMap.remove(key);
    }

    /**
     * Record that a function is being called.
     * @param expression The expression being evaluated
     * @param expressionID Identifier for the expression
     * @param symbol The symbol on which the rule is being evaluated
     * @param day Integer offset of the quote bundle. Included so the limit
     * exceeded doesn't trigger if some progress is being made.
     */
    public void startFunction(Expression expression, 
			      UUID expressionID, 
			      Symbol symbol, int day) {
		
	String key = getKey(expression, expressionID, symbol, day);
	Integer stackDepthInt =  (Integer)functionExpressionMap.get(key);

	int stackDepth = (stackDepthInt == null) ? 0 : stackDepthInt.intValue() + 1;

	functionExpressionMap.put(key,
				  new Integer(stackDepth));
	       
    }

    /**
     * Record that a function call has completed. 
     * @param expression The expression being evaluated
     * @param expressionID Identifier for the expression
     * @param symbol The symbol on which the rule is being evaluated
     * @param day Integer offset of the quote bundle. Included so the limit
     * exceeded doesn't trigger if some progress is being made.
     */
    public void finishFunction(Expression expression, 
			       UUID expressionID, 
			       Symbol symbol, int day) {

	String key = getKey(expression, expressionID, symbol, day);
	Integer stackDepthInt =  (Integer)functionExpressionMap.get(key);

	
	//Sometimes cancelling a run causes this to be null.FIXME
	//assert functionExpressionMap.get(key) != null;
	if (functionExpressionMap.get(key) == null) {
	    return;
	}

	int depth = stackDepthInt.intValue()-1;
	if (depth < 0) {
	    functionExpressionMap.remove(key);
	} else {
	    stackDepthInt = new Integer(depth);
	    functionExpressionMap.put(key, stackDepthInt);
	}
	    


    }

    
    //For regression testing only - don't use unless for some reason
    //we want to expose this functionality to users.
    protected void setRuntimeLimit(int secs) {	
	maxRunTime = secs;
    }

    protected void setMaxStackDepth(int depth) {
	maxStackDepth = depth;
    }

    private String getKey(Expression expression, UUID expressionID, Symbol symbol, int day) {
	String rv = expression.hashCode() + " " + expressionID + " " +
	    symbol + " " + day;
	return rv;
    }
    
}