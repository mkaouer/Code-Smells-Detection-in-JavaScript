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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mov.util.Locale;
import org.mov.parser.expression.AbstractExpression;

/**
 * A representation of any token in the <i>Gondola</i> language.
 *
 * @author Andrew Leppard
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
    
    /** Represents "<code>open</code>" symbol */
    public static final int DAY_OPEN_TOKEN = 19;
    
    /** Represents "<code>close</code>" symbol */
    public static final int DAY_CLOSE_TOKEN = 20;
    
    /** Represents "<code>low</code>" symbol */
    public static final int DAY_LOW_TOKEN = 21;
    
    /** Represents "<code>high</code>" symbol */
    public static final int DAY_HIGH_TOKEN = 22;
    
    /** Represents "<code>volume</code>" symbol */
    public static final int DAY_VOLUME_TOKEN = 23;
    
    /** Represents "<code>if</code>" symbol */
    public static final int IF_TOKEN = 24;
    
    /** Represents "<code>{</code>" symbol */
    public static final int LEFT_BRACE_TOKEN = 25;
    
    /** Represents "<code>}</code>" symbol */
    public static final int RIGHT_BRACE_TOKEN = 26;
    
    /** Represents "<code>.</code>" symbol */
    public static final int FULLSTOP_TOKEN = 27;
    
    /** Represents "<code>else</code>" symbol */
    public static final int ELSE_TOKEN = 28;
    
    /** Represents "<code>percent</code>" symbol */
    public static final int PERCENT_TOKEN = 29;
    
    /** Represents "<code>!=</code>" symbol */
    public static final int NOT_EQUAL_TOKEN = 30;
    
    /** Represents "<code>rsi</code>" symbol */
    public static final int RSI_TOKEN = 31;
    
    /** Represents "<code>true</code>" symbol */
    public static final int TRUE_TOKEN = 32;
    
    /** Represents "<code>false</code>" symbol */
    public static final int FALSE_TOKEN = 33;
    
    /** Represents "<code>dayofweek()</code>" symbol */
    public static final int DAY_OF_WEEK_TOKEN = 34;
    
    /** Represents "<code>dayofyear()</code>" symbol */
    public static final int DAY_OF_YEAR_TOKEN = 35;
    
    /** Represents "<code>day()</code>" symbol */
    public static final int DAY_TOKEN = 36;
    
    /** Represents "<code>month()</code>" symbol */
    public static final int MONTH_TOKEN = 37;
    
    /** Represents "<code>year()</code>" symbol */
    public static final int YEAR_TOKEN = 38;
    
    /** Represents "<code>sum</code>" symbol */
    public static final int SUM_TOKEN = 39;
    
    /** Represents "<code>sqrt</code>" symbol */
    public static final int SQRT_TOKEN = 40;
    
    /** Represents "<code>abs</code>" symbol */
    public static final int ABS_TOKEN = 41;
    
    /** Represents "<code>const</code>" symbol */
    public static final int CONSTANT_TOKEN = 42;
    
    /** Represents "<code>boolean</code>" symbol */
    public static final int BOOLEAN_TOKEN = 43;
    
    /** Represents "<code>int</code>" symbol */
    public static final int INTEGER_TOKEN = 44;
    
    /** Represents "<code>float</code>" symbol */
    public static final int FLOAT_TOKEN = 45;
    
    /** Represents "<code>=</code>" symbol */
    public static final int SET_TOKEN = 46;
    
    /** Represents "<code>;</code>" symbol */
    public static final int SEMICOLON_TOKEN = 47;
    
    /** Represents "<code>flor</code>" symbol */
    public static final int FOR_TOKEN = 48;
    
    /** Represents "<code>while</code>" symbol */
    public static final int WHILE_TOKEN = 49;
    
    /** Represents "<code>corr()</code>" symbol */
    public static final int CORR_TOKEN = 50;
    
    /** Represents "<code>ema()</code>" symbol */
    public static final int EMA_TOKEN = 51;

    /** Represents "<code>bol_lower()</code>" symbol */
    public static final int BBL_TOKEN = 52;
    
    /** Represents "<code>bol_upper()</code>" symbol */
    public static final int BBU_TOKEN = 53;
    
    /** Represents "<code>macd()</code>" symbol */
    public static final int MACD_TOKEN = 54;
    
    /** Represents "<code>momentum()</code>" symbol */
    public static final int MOMENTUM_TOKEN = 55;
    
    /** Represents "<code>obv()</code>" symbol */
    public static final int OBV_TOKEN = 56;
    
    /** Represents "<code>sd()</code>" symbol */
    public static final int SD_TOKEN = 57;
    
    /** Represents "<code>sin()</code>" symbol */
    public static final int SIN_TOKEN = 58;
    
    /** Represents "<code>cos()</code>" symbol */
    public static final int COS_TOKEN = 59;
    
    /** Represents "<code>log()</code>" symbol */
    public static final int LOG_TOKEN = 60;
    
    /** Represents "<code>exp()</code>" symbol */
    public static final int EXP_TOKEN = 61;
    
    /** Represents "<code>trend()</code>" symbol */
    public static final int TREND_TOKEN = 62;

    // Number of fixed length tokens, i.e. the ones above ^^
    private static final int FIXED_LENGTH_TOKENS = 63;
    
    /** Represents a number symbol */
    public static final int NUMBER_TOKEN = 100;
    
    /** Represents a variable */
    public static final int VARIABLE_TOKEN = 101;
    
    /** Represents a string */
    public static final int STRING_TOKEN = 102;
    
    // Token type (e.g. PERCENT_TOKEN, FULLSTOP_TOKEN etc)
    private int type;
    
    // For NUMBER_TOKEN - the actual number
    private double value;
    
    // For VARIABLE_TOKEN - the variable name
    private String variableName;
    
    // For NUMBER_TOKEN - the value's type
    private int valueType;
    
    // For STRING_TOKEN - the value's string
    private String stringValue;
    
    /**
     * Return a string containing all the words of Gondola language.
     *
     * @return	the string of words
     */
    
    public static String[] wordsOfGondola() {
        
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
        tokenStrings[LAG_TOKEN]                = "lag";
        tokenStrings[MIN_TOKEN]                = "min";
        tokenStrings[MAX_TOKEN]                = "max";
        tokenStrings[AVG_TOKEN]                = "avg";
        tokenStrings[DAY_OPEN_TOKEN]           = "open";
        tokenStrings[DAY_CLOSE_TOKEN]          = "close";
        tokenStrings[DAY_LOW_TOKEN]            = "low";
        tokenStrings[DAY_HIGH_TOKEN]           = "high";
        tokenStrings[DAY_VOLUME_TOKEN]         = "volume";
        tokenStrings[IF_TOKEN]                 = "if";
        tokenStrings[LEFT_BRACE_TOKEN]         = "{";
        tokenStrings[RIGHT_BRACE_TOKEN]        = "}";
        tokenStrings[FULLSTOP_TOKEN]           = ".";
        tokenStrings[ELSE_TOKEN]               = "else";
        tokenStrings[PERCENT_TOKEN]            = "percent";
        tokenStrings[NOT_EQUAL_TOKEN]          = "!=";
        tokenStrings[RSI_TOKEN]		       = "rsi";
        tokenStrings[TRUE_TOKEN]	       = "true";
        tokenStrings[FALSE_TOKEN]	       = "false";
        tokenStrings[DAY_OF_WEEK_TOKEN]	       = "dayofweek";
        tokenStrings[DAY_OF_YEAR_TOKEN]	       = "dayofyear";
        tokenStrings[DAY_TOKEN]	               = "day";
        tokenStrings[MONTH_TOKEN]	       = "month";
        tokenStrings[YEAR_TOKEN]	       = "year";
        tokenStrings[SUM_TOKEN]  	       = "sum";
        tokenStrings[SQRT_TOKEN]  	       = "sqrt";
        tokenStrings[ABS_TOKEN]  	       = "abs";
        tokenStrings[CONSTANT_TOKEN]  	       = "const";
        tokenStrings[BOOLEAN_TOKEN]  	       = "boolean";
        tokenStrings[INTEGER_TOKEN]  	       = "int";
        tokenStrings[FLOAT_TOKEN]  	       = "float";
        tokenStrings[SET_TOKEN]                = "=";
        tokenStrings[SEMICOLON_TOKEN]          = ";";
        tokenStrings[FOR_TOKEN]                = "for";
        tokenStrings[WHILE_TOKEN]              = "while";
        tokenStrings[CORR_TOKEN]               = "corr";
        tokenStrings[EMA_TOKEN]                = "ema";
        tokenStrings[BBL_TOKEN]                = "bol_lower";
        tokenStrings[BBU_TOKEN]                = "bol_upper";
        tokenStrings[MACD_TOKEN]               = "macd";
        tokenStrings[MOMENTUM_TOKEN]           = "momentum";
        tokenStrings[OBV_TOKEN]                = "obv";
        tokenStrings[SD_TOKEN]                 = "sd";
        tokenStrings[SIN_TOKEN]                = "sin";
        tokenStrings[COS_TOKEN]                = "cos";
        tokenStrings[LOG_TOKEN]                = "log";
        tokenStrings[EXP_TOKEN]                = "exp";
	tokenStrings[TREND_TOKEN]              = "trend";
        
        return tokenStrings;
    }
    
    /**
     * Perform lexical analysis on the given string. Extract the first
     * symbol found from the given string, set the passed token object
     * to represent this symbol then return the given string sans the
     * extracted symbol.
     *
     * @param   variables variables that will be assumed to be defined
     *                    for the equation
     * @param	token	the token to use to represent the first symbol found
     * @param	string	the string to extract the first symbol from
     * @return	the string minus the first symbol
     */
    
    public static String stringToToken(Variables variables, Token token, String string)
    throws ParserException {
        
        String[] tokenStrings = wordsOfGondola();

        boolean matched = false;
        
        // Is it a float or an integer number?
        if(Character.isDigit(string.charAt(0))) {
            double value = 0.0D;
            String numberString = new String();
            
            // Any values are considered to be integers, unless we find a decimal
            // point.
            int valueType = Expression.INTEGER_TYPE;
            
            do {
                if(string.charAt(0) == '.')
                    valueType = Expression.FLOAT_TYPE;
                
                numberString = numberString.concat(string.substring(0, 1));
                string = string.substring(1);
            } while(string.length() > 0 &&
            (Character.isDigit(string.charAt(0)) ||
            string.charAt(0) == '.'));
            
            // Now convert number string to double value
            try {
                value = AbstractExpression.parseDouble(numberString);
            }
            catch(NumberFormatException e) {
                throw new ParserException(Locale.getString("MALFORMED_NUMBER_ERROR"));
            }
            
            token.setType(Token.NUMBER_TOKEN);
            token.setValue(value);
            token.setValueType(valueType);
            matched = true;
        }
        
        // Is it a string?
        else if(string.charAt(0) == '\"') {
            int closingQuote = string.indexOf('\"', 1);
            
            if(closingQuote >= 0) {
                String quote = string.substring(1, closingQuote);
                string = string.substring(closingQuote + 1);
                
                token.setType(Token.STRING_TOKEN);
                token.setStringValue(quote);
                matched = true;
            }
            
            // Missing trailing quote
            else
                throw new ParserException(Locale.getString("MISSING_CLOSING_QUOTE"));
        }
        
        // Is it a keyword or variable?
        else if(Character.isLetter(string.charAt(0))) {
            
            // Extract all letters
            Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*");
            Matcher matcher = pattern.matcher(string);
            matcher.find();
            String identifier = matcher.group();
            
            for(int i = 0; !matched && i < tokenStrings.length; i++)
                if(identifier.equals(tokenStrings[i])) {
                    token.setType(i);
                    
                    // move string along
                    string = string.substring(tokenStrings[i].length());
                    matched = true;
                }
            
            // Maybe it's a variable?
            if(!matched) {
                
                // Greedily extract possible variable name
                pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*");
                matcher = pattern.matcher(string);
                
                if(matcher.find()) {
                    String variableName = matcher.group();
                    string = string.substring(variableName.length());
                    matched = true;
                    token.setType(Token.VARIABLE_TOKEN);
                    token.setVariableName(variableName);
                }
            }
            
            if(!matched)
                throw new ParserException(Locale.getString("UNKNOWN_IDENTIFIER_ERROR", identifier));
        }
        
        // Must be some sort of punctuation
        else {
            char symbol = string.charAt(0);
            
            for(int i = 0; !matched && i < tokenStrings.length; i++)
                if(string.startsWith(tokenStrings[i])) {
                    token.setType(i);
                    
                    // move string along
                    string = string.substring(tokenStrings[i].length());
                    matched = true;
                }
            
            if(!matched)
                throw new ParserException(Locale.getString("UNKNOWN_SYMBOL_ERROR", symbol));
        }
        
        assert matched;
        
        return string;
        
    }
    
    /**
     *
     * Create a new empty token.
     *
     */
    
    public Token() {
        type = 0;
        value = 0;
        
        variableName = null;
        valueType = 0;
    }
    
    /**
     *
     * Get the type of this token.
     *
     *
     *
     * @return	the type
     *
     */
    public int getType() {
        return type;
    }
    
    /**
     *
     * Set the type of this token.
     *
     *
     *
     * @param	type	the new type
     *
     */
    public void setType(int type) {
        this.type = type;
    }
    
    /**
     *
     * For number tokens, get the value.
     *
     *
     *
     * @return	the value.
     *
     */
    public double getValue() {
        assert getType() == NUMBER_TOKEN;
        return value;
    }
    
    /**
     *
     * For string tokens, get the value.
     *
     *
     *
     * @return	the value.
     *
     */
    public String getStringValue() {
        assert getType() == STRING_TOKEN;
        return stringValue;
    }
    
    /**
     *
     * For variable token, get the name.
     *
     *
     *
     * @return the name.
     *
     */
    public String getVariableName() {
        assert getType() == VARIABLE_TOKEN;
        return variableName;
    }
    
    /**
     *
     * For {@link #NUMBER_TOKEN} get the type.
     *
     *
     *
     * @return the type.
     *
     */
    public int getValueType() {
        assert getType() == NUMBER_TOKEN;
        return valueType;
    }
    
    /**
     *
     * For {@link #NUMBER_TOKEN}, negate the value.
     *
     */
    public void negate() {
        assert getType() == NUMBER_TOKEN;
        value = -value;
    }
    
    // For variable tokens, set the variable name
    private void setVariableName(String variableName) {
        assert getType() == VARIABLE_TOKEN;
        this.variableName = variableName;
    }
    
    // For variable or number tokens, set the value type
    private void setValueType(int valueType) {
        this.valueType = valueType;
    }
    
    // For number tokens, set the value.
    private void setValue(double value) {
        assert getType() == NUMBER_TOKEN;
        this.value = value;
    }
    
    // For string tokens, set the value.
    private void setStringValue(String stringValue) {
        assert getType() == STRING_TOKEN;
        this.stringValue = stringValue;
    }
}

