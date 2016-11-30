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

package org.mov.parser;

/**
 * A representation of any token in the <i>Gondola</i> language.
 */
public class Token {

    /** Represents "<code>(</code>" symbol */
    public static final int LEFT_PARENTHESIS_TOKEN = 0;   

    /** Represents "<code>)</code>" symbol */
    public static final int RIGHT_PARENTHESIS_TOKEN = 1;  

    /** Represents "<code><=</code>" symbol */
    public static final int LESS_THAN_EQUAL_TOKEN = 2;   

    /** Represents "<code>>=</code>" symbol */
    public static final int GREATER_THAN_EQUAL_TOKEN = 3; 

    /** Represents "<code><</code>" symbol */
    public static final int LESS_THAN_TOKEN = 4;          

    /** Represents "<code>></code>" symbol */
    public static final int GREATER_THAN_TOKEN = 5;       

    /** Represents "<code>==</code>" symbol */
    public static final int EQUAL_TOKEN = 6;              

    /** Represents "<code>+</code>" symbol */
    public static final int ADD_TOKEN = 7;

    /** Represents "<code>-</code>" symbol */
    public static final int SUBTRACT_TOKEN = 8; 

    /** Represents "<code>*</code>" symbol */
    public static final int MULTIPLY_TOKEN = 9;   

    /** Represents "<code>/</code>" symbol */
    public static final int DIVIDE_TOKEN = 10;    

    /** Represents "<code>or</code>" symbol */
    public static final int OR_TOKEN = 11;        

    /** Represents "<code>and</code>" symbol */
    public static final int AND_TOKEN = 12;       

    /** Represents "<code>not</code>" symbol */
    public static final int NOT_TOKEN = 13;         

    /** Represents "<code>,</code>" symbol */
    public static final int COMMA_TOKEN = 14;      

    /** Represents "<code>lag</code>" symbol */
    public static final int LAG_TOKEN = 15;        

    /** Represents "<code>min</code>" symbol */
    public static final int MIN_TOKEN = 16;        

    /** Represents "<code>max</code>" symbol */
    public static final int MAX_TOKEN = 17;        

    /** Represents "<code>avg</code>" symbol */
    public static final int AVG_TOKEN = 18;        

    /** Represents "<code>held</code>" symbol */
    public static final int HELD_TOKEN = 19;        

    /** Represents "<code>day_open</code>" symbol */
    public static final int DAY_OPEN_TOKEN = 20;    

    /** Represents "<code>day_close</code>" symbol */
    public static final int DAY_CLOSE_TOKEN = 21;   

    /** Represents "<code>day_low</code>" symbol */
    public static final int DAY_LOW_TOKEN = 22;     

    /** Represents "<code>day_high</code>" symbol */
    public static final int DAY_HIGH_TOKEN = 23;    

    /** Represents "<code>day_volume</code>" symbol */
    public static final int DAY_VOLUME_TOKEN = 24;  

    /** Represents "<code>if</code>" symbol */
    public static final int IF_TOKEN = 25;         

    /** Represents "<code>{</code>" symbol */
    public static final int LEFT_BRACE_TOKEN = 26; 

    /** Represents "<code>}</code>" symbol */
    public static final int RIGHT_BRACE_TOKEN = 27;

    /** Represents "<code>.</code>" symbol */
    public static final int FULLSTOP_TOKEN = 28;   

    /** Represents "<code>else</code>" symbol */
    public static final int ELSE_TOKEN = 29;       

    /** Represents "<code>age</code>" symbol */
    public static final int AGE_TOKEN = 30;        

    /** Represents "<code>percent</code>" symbol */
    public static final int PERCENT_TOKEN = 31;    

    /** Represents "<code>!=</code>" symbol */
    public static final int NOT_EQUAL_TOKEN = 32;   

    /** Represents "<code>rsi</code>" symbol */
    public static final int RSI_TOKEN = 33;	    

    // Number of fixed length tokens, i.e. the ones above ^^
    private static final int FIXED_LENGTH_TOKENS = 34;

    /** Represents a number symbol */
    public static final int NUMBER_TOKEN = 100;       
    
    // Token type (e.g. PERCENT_TOKEN, FULLSTOP_TOKEN etc)
    private int type;

    // For NUMBER_TOKEN - the actual number
    private float value; 
    
    /**
     * Perform lexical analysis on the given string. Extract the first
     * symbol found from the given string, set the passed token object
     * to represent this symbol then return the given string sans the
     * extracted symbol.
     *
     * @param	token	the token to use to represent the first symbol found
     * @param	string	the string to extract the first symbol from
     * @return	the string minus the first symbol
     */
    public static String stringToToken(Token token, String string) 
	throws ParserException {

	String[] tokenStrings = new String[FIXED_LENGTH_TOKENS];

	// Map of token ID's to strings - match order
	tokenStrings[LEFT_PARENTHESIS_TOKEN]   = "(";
	tokenStrings[RIGHT_PARENTHESIS_TOKEN]  = ")";
	tokenStrings[LESS_THAN_EQUAL_TOKEN]    = "<=";
	tokenStrings[GREATER_THAN_EQUAL_TOKEN] = ">=";
	tokenStrings[LESS_THAN_TOKEN]          = "<";
	tokenStrings[GREATER_THAN_TOKEN]       = ">";
	tokenStrings[EQUAL_TOKEN]              = "==";
	tokenStrings[ADD_TOKEN]                = "+";
	tokenStrings[SUBTRACT_TOKEN]           = "-";
	tokenStrings[MULTIPLY_TOKEN]           = "*";
	tokenStrings[DIVIDE_TOKEN]             = "/";
	tokenStrings[OR_TOKEN]                 = "or";
	tokenStrings[AND_TOKEN]                = "and";
	tokenStrings[NOT_TOKEN]                = "not";
	tokenStrings[COMMA_TOKEN]              = ",";
	tokenStrings[HELD_TOKEN]               = "held";
	tokenStrings[LAG_TOKEN]                = "lag";
	tokenStrings[MIN_TOKEN]                = "min";
	tokenStrings[MAX_TOKEN]                = "max";
	tokenStrings[AVG_TOKEN]                = "avg";
	tokenStrings[DAY_OPEN_TOKEN]           = "day_open";
	tokenStrings[DAY_CLOSE_TOKEN]          = "day_close";
	tokenStrings[DAY_LOW_TOKEN]            = "day_low";
	tokenStrings[DAY_HIGH_TOKEN]           = "day_high";
	tokenStrings[DAY_VOLUME_TOKEN]         = "day_volume";
	tokenStrings[IF_TOKEN]                 = "if";
	tokenStrings[LEFT_BRACE_TOKEN]         = "{";
	tokenStrings[RIGHT_BRACE_TOKEN]        = "}";
	tokenStrings[FULLSTOP_TOKEN]           = ".";
	tokenStrings[ELSE_TOKEN]               = "else";
	tokenStrings[AGE_TOKEN]                = "age";
	tokenStrings[PERCENT_TOKEN]            = "percent";
	tokenStrings[NOT_EQUAL_TOKEN]          = "!=";
	tokenStrings[RSI_TOKEN]		       = "rsi";

	boolean matched = false;

	// First check to see if its a number 
	if(Character.isDigit(string.charAt(0))) {
	    float value = 0.0F;
	    String numberString = new String();

	    do {
		numberString = numberString.concat(string.substring(0, 1));
		string = string.substring(1);
	    } while(string.length() > 0 && 
		    (Character.isDigit(string.charAt(0)) ||
		    string.charAt(0) == '.'));

	    // Now convert number string to float value
	    try {
		value = Float.parseFloat(numberString);
	    }
	    catch(NumberFormatException e) {
		throw new ParserException("malformed number");
	    }

	    token.setType(Token.NUMBER_TOKEN);
	    token.setValue(value);

	    matched = true;
	}
	else 
	    for(int i = 0; !matched && i < tokenStrings.length; i++)
		if(tokenStrings[i] != null && 
		   string.startsWith(tokenStrings[i])) {
		    
		    token.setType(i);
		    
		    // move string along
		    string = string.substring(tokenStrings[i].length());
		    matched = true;
		}
	
	if(!matched)
	    throw new ParserException("unknown symbol");

	return string;
    }

    /**
     * Create a new empty token.
     */
    public Token() {
	type = 0;
	value = 0;
    }

    /**
     * Get the type of this token.
     *
     * @return	the type
     */
    public int getType() {
	return type;
    }
    
    /**
     * Set the type of this token.
     *
     * @param	type	the new type
     */
    public void setType(int type) {
	this.type = type;
    }

    /**
     * For number tokens, get the value.
     *
     * @return	the value.
     */
    public float getValue() {
	return value;
    }

    /**
     * For number tokens, negate the value.
     */
    public void negate() {
	value = -value;
    }

    /**
     * For number tokens, set the value.
     *
     * @param	value	the value.
     */
    public void setValue(float value) {
	this.value = value;
    }
}
