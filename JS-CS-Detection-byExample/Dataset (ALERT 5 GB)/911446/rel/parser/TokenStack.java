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

import java.util.*;

/**
 * A stack of tokens which is used during parsing.
 */
public class TokenStack extends Vector {

    /**
     * Create a new token stack.
     */
    public TokenStack() {
	// nothing to do
    }

    // Get the token on the top of the stack
    public Token get() {
	if(size() > 0)
	    return (Token)firstElement();
	else
	    return null;
    }

    /**
     * Remove and return the token on the top of the stack.
     *
     * @return	the token on the top of the stack
     */
    public Token pop() {
	if(size() > 0)
	    return (Token)remove(0);
	else
	    return null;
    }

    /**
     * Remove the token on the top of the stack and compare it with the
     * given type.
     *
     * @param	tokenType	the expected token type on the stack.
     * @return	<code>TRUE</code> if the token is of the same type.
     */
    public boolean pop(int tokenType) {
	Token token = pop();
	
	if(token != null && tokenType == token.getType())
	    return true;
	else
	    return false;
    }

    /**
     * Compare the token on the top of the stack with the given type.
     * The token will not be removed from the stack.
     *
     * @param	tokenType	token type to compare with
     * @return	<code>1</code> if the token is of the same type; 
     *		<code>0</code> otherwise
     */
    public boolean match(int tokenType) {
	Token token = get();

	if(token != null && token.getType() == tokenType)
	    return true;
	else
	    return false;
    }

    public String toString() {
	String rv = "";
	String[] words = Token.wordsOfGondola();
	
	Iterator stackIterator = iterator();
	while (stackIterator.hasNext()) {
	    Token t = (Token)stackIterator.next();

	    switch (t.getType()) {
	    case Token.NUMBER_TOKEN:
		rv += "num: " + t.getValue();
		break;
	    case Token.VARIABLE_TOKEN:
		rv += "var: " + t.getVariableName();
		break;
	    case Token.STRING_TOKEN:
		rv += "str: " + t.getStringValue();
		break;
	    default:
		rv += words[t.getType()];
	    }	    
	}
	return rv;
    }

}
