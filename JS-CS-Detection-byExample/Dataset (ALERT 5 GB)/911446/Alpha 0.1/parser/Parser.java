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

import java.util.*;

/**
 * Parse a string into an executable expression. This class acts as
 * a gatekeeper to the <i>Gondala</i> language which is used to perform
 * manipulations on stock market quotes. This language was specially
 * created to deal with quote objects and thus has some special features:
 * <p>
 * <ul><li>Inbuilt stock market types such as <i>volume</i> & <i>price</i>
 *         etc</li>
 *     <li>Built in functions to manipulate stock market quotes such as
 *	   <i>lag, min, max, avg</i> & <i>rsi</i>
 *     <li>Internal variables holding current quote and latest date being
 *         referenced</li>
 * </ul>
 * <p>
 * <h2>Language summary</h2>
 * <p>
 *
 * <p>
 * <h2>Functions</h2>
 * <p>
 *
 * <ul>
 * <li>
 * Average: <i>avg(quote, days, offset)</i>
 * <p>
 * Calculate the average stock quote over a period. For example to find
 * the average day open of the current stock over 5 days with the last day
 * being yesterday:
 * <p>
 * <code>avg(day_open, 5, -1)</code>
 * </li>
 *
 * <li>
 * Lag: <i>lag(quote, offset)</i>
 * <p>
 * Find the quote price for a stock on the given day. For example to find
 * the trading volume of the current stock today:
 * <p>
 * <code>lag(day_volume, 0)</code>
 * </li>
 *
 * <li>
 * Minimum Quote: <i>min(quote, days, offset)</i>
 * <p>
 * Calculate the minimum stock quote over a period. For example to find
 * the minimum day close of the current stock over 100 days with the last day
 * being yesterday:
 * <p>
 * <code>min(day_close, 100, -1)</code>
 * </li>
 *
 * <li>
 * Maximum Quote: <i>max(quote, days, offset)</i>
 * <p>
 * Calculate the maximum stock quote over a period. For example to find
 * the maximum day high of the current stock over 100 days with the last day 
 * being 7 trading ago:
 * <p>
 * <code>max(day_high, 100, -7)</code>
 * </li>
 *
 * <li>
 * Percent: <i>percent(value, percent)</i>
 * <p>
 * Calculate <i>percent</i> percent of the value and return. For example to
 * find 10% of the volume traded on a single day:
 * <p>
 * <code>percent(lag(day_volume, 0), 10)</code>
 * </li>
 *
 * </ul>
 * <p>
 * <h2>Operators</h2>
 * <p>
 *
 * <ul>
 *
 * <li>
 * Boolean operators: <i>x and y, not(x), x or y</i>
 * <p>
 * The standard boolean operators. 
 * </li>
 *
 * <li>
 * Mathematical operators: <i>x + y, x - y, x * y, x / y</i>
 * <p>
 * The standard mathematical operators.
 * </li>
 * 
 * <li>
 * Relational operators: <i>x == y, x < y, x <= y, x > y, x >= y, x != y</i>
 * <p>
 * The standard relational operators.
 * </li>
 *
 * </ul>
 *
 * <p>
 * <h2>Control Statements</h2>
 * <p>
 * <ul>
 * <li>
 * If statement: <i>if(condition) { ... } else { ... } </i>
 * <p>
 * Standard <i>if</i> statement as you would find in any language.
 * </li>
 *
 * </ul>
 * <h2>Examples</h2>
 * <p>
 * <ul>
 * <li>Returns true if the current stock has traded more than
 * $100 Million today (sort of):
 * <p><code>lag(day_volume, 0) * lag(day_close, 0) > 100000000</code></li>
 *
 * <li>Returns true if the current stock has gained more than 10%:
 * <p><code>
 * (100 * (lag(day_close, 0) - lag(day_open, 0))) / lag(day_open, 0) > 10
 * </code></li>
 *
 * <li>Returns true if the current stock has reached a 30 day high:
 * <p><code>lag(day_high, 0) > max(day_high, 29, -1)</code></li>
 *
 * </ul>
 * <p>
 * <h2>Langauge EBNF</h2>
 * <pre>
 * EXPR = BOOLEAN_EXPR [ LOGIC BOOLEAN_EXPR ]
 * LOGIC = "and" | "or"
 * BOOLEAN_EXPR = ADD_EXPR [ RELATION ADD_EXPR ]
 * RELATION = "==" | "<=" | ">=" | "<" | ">" | "!="
 * ADD_EXPR = MULTIPLY_EXPR [ ADD_OPERATOR MULTIPLY_EXPR ]
 * ADD_OPERATION = "-" | "+"
 * MULTIPLY_EXPR = FACTOR [ MULTIPLY_OPERATOR FACTOR ]
 * MULTIPLY_OPERATOR = "*" | "/"
 * FACTOR = VARIABLE | NUMBER | FUNCTION | "(" EXPR ")"
 * NUMBER = ["-"]{0-9}+ ["." {0-9}+]
 * VARIABLE = "held" | "age" 
 * QUOTE      "day_open" | "day_close" | "day_low" | "day_high" | 
 *            "day_volume"
 * FUNCTION = "lag" "(" QUOTE "," EXPR ")" | 
 *            "min" "(" QUOTE "," EXPR "," EXPR ")" | 
 *            "max" "(" QUOTE "," EXPR "," EXPR ")" |
 *            "avg" "(" QUOTE "," EXPR "," EXPR ")" |
 *            "rsi" "(" EXPR "," EXPR ")" |
 *            "not" "(" EXPR ")" |
 *            "percent" "(" EXPR "," EXPR ")" |
 *            "if"  "(" EXPR ")" "{" EXPR "}" "else" "{" EXPR "}"
 * </pre>
 * <h2>Symbols to be implemented</h2>
 * <p><i>age, held, today, yesterday</i>
 * 
 */
public class Parser {

    public Expression parse(String string) throws ExpressionException
    {
	// Perform lexical analysis on string - i.e. reduce it to stack of
	// tokens
	TokenStack tokens = lexicalAnalysis(string);

	// Translate stack of tokens to expression
	Expression expression = parseExpression(tokens);

	// Check for type mismatch
	expression.checkType();

	return expression;
    }

    private TokenStack lexicalAnalysis(String string) 
	throws ParserException {

	TokenStack tokens = new TokenStack();
	Token token;

	while(string.length() > 0) {

	    // skip spaces
	    while(string.length() > 0 &&
		  Character.isWhitespace(string.charAt(0)))		  
		string = string.substring(1);

	    if(string.length() > 0) {

		// Extract next token
		token = new Token();
		string = Token.stringToToken(token, string);
		tokens.add(token);
	    }
	}

	return tokens;
    }

    private Expression parseExpression(TokenStack tokens) 
	throws ParserException {

	Expression left = parseBooleanExpression(tokens);

	if(tokens.match(Token.AND_TOKEN) ||
	   tokens.match(Token.OR_TOKEN)) {

	    Token operation = tokens.pop();
	    Expression right = parseBooleanExpression(tokens);

	    return(ExpressionFactory.newExpression(operation, left, right));
	}
	return left;
    }

    private Expression parseBooleanExpression(TokenStack tokens) 
	throws ParserException {

	Expression left = parseAddExpression(tokens);

	if(tokens.match(Token.EQUAL_TOKEN) ||
	   tokens.match(Token.NOT_EQUAL_TOKEN) ||
	   tokens.match(Token.LESS_THAN_EQUAL_TOKEN) ||
	   tokens.match(Token.LESS_THAN_TOKEN) ||
	   tokens.match(Token.GREATER_THAN_TOKEN) ||
	   tokens.match(Token.GREATER_THAN_EQUAL_TOKEN)) {
	    
	    Token operation = tokens.pop();
	    Expression right = parseAddExpression(tokens);

	    return(ExpressionFactory.newExpression(operation, left, right));
	}
	return left;
    }
	
    private Expression parseAddExpression(TokenStack tokens) 
	throws ParserException {

	Expression left = parseMultiplyExpression(tokens);

	if(tokens.match(Token.ADD_TOKEN) ||
	   tokens.match(Token.SUBTRACT_TOKEN)) {

	    Token operation = tokens.pop();	    
	    Expression right = parseMultiplyExpression(tokens);

	    return(ExpressionFactory.newExpression(operation, left, right));
	}
	return left;
    }
    
    private Expression parseMultiplyExpression(TokenStack tokens) 
	throws ParserException {
	
	Expression left = parseFactor(tokens);

	if(tokens.match(Token.MULTIPLY_TOKEN) ||
	   tokens.match(Token.DIVIDE_TOKEN)) {
	    
	    Token operation = tokens.pop();
	    Expression right = parseFactor(tokens);

	    return(ExpressionFactory.newExpression(operation, left, right));
	}
	
	return left;
    }	

    private Expression parseFactor(TokenStack tokens) 
	throws ParserException {

	Expression expression;

	// VARIABLE
	if(tokens.match(Token.HELD_TOKEN) ||
	   tokens.match(Token.AGE_TOKEN)) {
	    expression = parseVariable(tokens);
	}

	// NUMBER
	else if(tokens.match(Token.NUMBER_TOKEN) || 
		tokens.match(Token.SUBTRACT_TOKEN)) {
	    expression = parseNumber(tokens);
	}
	
	// FUNCTION
	else if(tokens.match(Token.LAG_TOKEN) ||
		tokens.match(Token.MIN_TOKEN) ||
		tokens.match(Token.MAX_TOKEN) ||
		tokens.match(Token.AVG_TOKEN) ||
		tokens.match(Token.RSI_TOKEN) ||
		tokens.match(Token.NOT_TOKEN) ||
		tokens.match(Token.IF_TOKEN) ||
		tokens.match(Token.PERCENT_TOKEN)) {
	    expression = parseFunction(tokens);
	}

	// EXPRESSION
	else if(tokens.match(Token.LEFT_PARENTHESIS_TOKEN)) {
	    tokens.pop();
	    expression = parseExpression(tokens);
	    parseRightParenthesis(tokens);
	}
	else
	    throw new ParserException("unexpected symbol");

	return expression;
    }

    private Expression parseVariable(TokenStack tokens) 
	throws ParserException {

	Token variable = tokens.pop();
	Expression expression;
	
	switch(variable.getType()) {
	case(Token.HELD_TOKEN):
	case(Token.AGE_TOKEN):
	    expression = ExpressionFactory.newExpression(variable);
	    break;
	default:
	    throw new ParserException("expected variable");
	}

	return expression;
    }

    private Expression parseQuote(TokenStack tokens) 
	throws ParserException {
	
	Token quote = tokens.pop();
	Expression expression;
	
	switch(quote.getType()) {
	case(Token.DAY_OPEN_TOKEN):
	case(Token.DAY_CLOSE_TOKEN):
	case(Token.DAY_LOW_TOKEN):
	case(Token.DAY_HIGH_TOKEN):
	case(Token.DAY_VOLUME_TOKEN):
	    expression = ExpressionFactory.newExpression(quote);
	    break;
	default:
	    throw new ParserException("expected quote type");
	}

	return expression;
    }

    private Expression parseNumber(TokenStack tokens) 
	throws ParserException {

	Token number = tokens.pop();
	boolean negate = false;

	// Is there a "-" infront? Handle negative numbers 
	if(number.getType() == Token.SUBTRACT_TOKEN) {
	    number = tokens.pop();
	    negate = true;
	}

	if(number.getType() == Token.NUMBER_TOKEN) {
	    if(negate)
		number.negate();

	    return ExpressionFactory.newExpression(number);	    
	}
	else
	    throw new ParserException("expected number");
    }

    private Expression parseFunction(TokenStack tokens) 
	throws ParserException {

	Expression expression;
	Expression arg1 = null;
	Expression arg2 = null;
	Expression arg3 = null;
	    
	Token function = tokens.pop();

	// all functions must have a left parenthesis after the function
	// name
	parseLeftParenthesis(tokens);

	switch(function.getType()) {
	case(Token.LAG_TOKEN):

	    arg1 = parseQuote(tokens);
	    parseComma(tokens);
	    arg2 = parseExpression(tokens);
	    break;

	case(Token.MIN_TOKEN):

	    arg1 = parseQuote(tokens);
	    parseComma(tokens);
	    arg2 = parseExpression(tokens);
	    parseComma(tokens);
	    arg3 = parseExpression(tokens);	    
	    break;
	    
	case(Token.MAX_TOKEN):
	    
	    arg1 = parseQuote(tokens);
	    parseComma(tokens);
	    arg2 = parseExpression(tokens);
	    parseComma(tokens);
	    arg3 = parseExpression(tokens);	    
	    break;
	
	case(Token.AVG_TOKEN):
	    
	    arg1 = parseQuote(tokens);
	    parseComma(tokens);
	    arg2 = parseExpression(tokens);
	    parseComma(tokens);
	    arg3 = parseExpression(tokens);	    
	    break;

	case(Token.RSI_TOKEN):
	    
	    arg1 = parseExpression(tokens);
	    parseComma(tokens);
	    arg2 = parseExpression(tokens);	    
	    break;

	case(Token.NOT_TOKEN):

	    arg1 = parseExpression(tokens);
	    break;
					       
	case(Token.IF_TOKEN):
	    
	    arg1 = parseExpression(tokens);
	    parseRightParenthesis(tokens);
	    parseLeftBrace(tokens);
	    arg2 = parseExpression(tokens);
	    parseRightBrace(tokens);
	    parseElse(tokens);
	    parseLeftBrace(tokens);
	    arg3 = parseExpression(tokens);
	    parseRightBrace(tokens);				
	    break;
	    
	case(Token.PERCENT_TOKEN):

	    arg1 = parseExpression(tokens);
	    parseComma(tokens);
	    arg2 = parseExpression(tokens);
	    break;

	default:
	    throw new ParserException("expected function");
	}

	// create epxression
	expression = ExpressionFactory.newExpression(function, arg1, arg2, 
						     arg3);
	
	// all functions must end with a right parenthesis (ignore IF
	// because weve already removed its right parenthesis
	if(function.getType() != Token.IF_TOKEN)
	    parseRightParenthesis(tokens);

	return expression;
    }

    private void parseComma(TokenStack tokens) throws ParserException {
	if(!tokens.pop(Token.COMMA_TOKEN))
	    throw new ParserException("expected comma");
    }

    private void parseLeftParenthesis(TokenStack tokens) 
	throws ParserException {
	if(!tokens.pop(Token.LEFT_PARENTHESIS_TOKEN))
	    throw new ParserException("expected left parenthesis");
    }

    private void parseRightParenthesis(TokenStack tokens) 
	throws ParserException {
	if(!tokens.pop(Token.RIGHT_PARENTHESIS_TOKEN))
	    throw new ParserException("missing right parenthesis");
    }

    private void parseLeftBrace(TokenStack tokens) throws ParserException {
	if(!tokens.pop(Token.LEFT_BRACE_TOKEN))
	    throw new ParserException("expected left brace");
    }

    private void parseRightBrace(TokenStack tokens) 
	throws ParserException {

	if(!tokens.pop(Token.RIGHT_BRACE_TOKEN))
	    throw new ParserException("missing right brace");
    }

    private void parseElse(TokenStack tokens) throws ParserException {
	if(!tokens.pop(Token.ELSE_TOKEN))
	    throw new ParserException("expected else token");
    }
}
