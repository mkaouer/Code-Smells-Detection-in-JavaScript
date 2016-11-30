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
import java.util.Iterator;

import nz.org.venice.parser.Token;
import nz.org.venice.parser.Expression;
import nz.org.venice.parser.expression.FunctionExpression;


/** 
 * Container for parse and lexical analysis information. Used for debugging
 * purposes such as returning the line number on which an error occurred.
 * @author Mark Hummel
 */

public class ParseMetadata {

    private final HashMap parseTree;
    private final HashMap tokenLineMap;
    private final HashMap bodyCache;
    

    /**
     * Construct an object containing the information used to construct tghe
     * parse tree.
     * 
     * @param parseTree The map containing links between the tokens and 
     * constructed expressions
     * @param tokenLineMap The map containing links between the tokens and
     * the line number on which they appeared.
     */
    public ParseMetadata(HashMap parseTree, HashMap tokenLineMap) {
	this.parseTree = parseTree;
	this.tokenLineMap = tokenLineMap;
	bodyCache = new HashMap();
    }

    /** 
     * @return The expression containing the function body for a given function.
     * @param functionName The name of the function which was called.
     * 
     */
    
    public Expression getFunctionBody(String functionName) {

	if (bodyCache.get(functionName) != null) {
	    return (Expression)bodyCache.get(functionName);
	}

    	Iterator iterator = parseTree.keySet().iterator();
	while (iterator.hasNext()) {
	    Expression e = (Expression)iterator.next();
	    
	    if (e instanceof FunctionExpression) {
		String funcName = ((FunctionExpression)e).getName();
		if (funcName.equals(functionName)) {
		    bodyCache.put(functionName, e.getChild(1));
		    return e.getChild(1);
		}
	    }
	}
	return null;
    }
    
    /**
     * @return The list of parameters for a given function
     * @param functionName The name of the function.
     */
    public Expression getParameterNames(String functionName) {
	Iterator iterator = parseTree.keySet().iterator();
	while (iterator.hasNext()) {
	    Expression e = (Expression)iterator.next();
	    Token t = (Token)parseTree.get(e);

	    if (e instanceof FunctionExpression) {
		String funcName = ((FunctionExpression)e).getName();
		if (funcName.equals(functionName)) {
		    return e.getChild(0);
		}
	    }
	}
	return null;
    }

    /**
     * @return the line number as a string where a given expression appears
     */
    public String getLineForExpression(Expression expression) {
	Token token = (Token)parseTree.get(expression);
	assert token != null;

	Integer tmp = (Integer)tokenLineMap.get(token);	
	if (tmp == null) {
	    return "undef";
	}
	
	String lineNumber = tmp.toString();
	return lineNumber;
    }
}