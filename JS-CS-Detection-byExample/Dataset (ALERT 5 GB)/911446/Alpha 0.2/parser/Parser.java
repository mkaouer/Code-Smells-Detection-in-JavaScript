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
 * manipulations on stock market quotes. 
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
 * NUMBER = ["-"]{0-9}+ ["." {0-9}+] | "true" | "false"
 * VARIABLE = {a-zA-Z}{a-zA-Z0-9}*
 * QUOTE      "open" | "close" | "low" | "high" | "volume"
 * FUNCTION = "lag" "(" QUOTE "," EXPR ")" | 
 *            "min" "(" QUOTE "," EXPR "," EXPR ")" | 
 *            "max" "(" QUOTE "," EXPR "," EXPR ")" |
 *            "avg" "(" QUOTE "," EXPR "," EXPR ")" |
 *            "sum" "(" QUOTE "," EXPR "," EXPR ")" |
 *            "rsi" "(" EXPR "," EXPR ")" |
 *            "not" "(" EXPR ")" |
 *            "percent" "(" EXPR "," EXPR ")" |
 *            "if"  "(" EXPR ")" "{" EXPR "}" "else" "{" EXPR "}" |
 *            "dayofweek" "(" ")" |
 *            "dayofyear" "(" ")" |
 *            "day" "(" ")" |
 *            "month" "(" ")" |
 *            "year" "(" ")" |
 *            "sqrt" "(" EXPR ")" |
 *            "abs" "(" EXPR ")"
 * </pre>
 */
public class Parser {

    private Parser() {
        // class should not be instantiated
    }

    public static Expression parse(Variables variables, String string)
        throws ExpressionException {

	// Perform lexical analysis on string - i.e. reduce it to stack of
	// tokens
	TokenStack tokens = lexicalAnalysis(variables, string);

	// Translate stack of tokens to expression
	Expression expression = parseExpression(tokens);

	// Check for type mismatch
	expression.checkType();

	return expression;
    }

    public static Expression parse(String string) throws ExpressionException
    {
        return parse(new Variables(), string);
    }

    private static TokenStack lexicalAnalysis(Variables variables, String string) 
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
		string = Token.stringToToken(variables, token, string);
		tokens.add(token);
	    }
	}

	return tokens;
    }

    private static Expression parseExpression(TokenStack tokens) 
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

    private static Expression parseBooleanExpression(TokenStack tokens) 
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
	
    private static Expression parseAddExpression(TokenStack tokens) 
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
    
    private static Expression parseMultiplyExpression(TokenStack tokens) 
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

    private static Expression parseFactor(TokenStack tokens) 
	throws ParserException {

	Expression expression;

	// NUMBER
	if(tokens.match(Token.NUMBER_TOKEN) || 
           tokens.match(Token.TRUE_TOKEN) || 
           tokens.match(Token.FALSE_TOKEN) || 
           tokens.match(Token.SUBTRACT_TOKEN))
	    expression = parseNumber(tokens);
	
	// FUNCTION
	else if(tokens.match(Token.LAG_TOKEN) ||
		tokens.match(Token.MIN_TOKEN) ||
		tokens.match(Token.MAX_TOKEN) ||
		tokens.match(Token.AVG_TOKEN) ||
		tokens.match(Token.SUM_TOKEN) ||
		tokens.match(Token.RSI_TOKEN) ||
		tokens.match(Token.NOT_TOKEN) ||
		tokens.match(Token.IF_TOKEN) ||
		tokens.match(Token.PERCENT_TOKEN) ||
		tokens.match(Token.DAY_OF_WEEK_TOKEN) ||
		tokens.match(Token.DAY_OF_YEAR_TOKEN) ||
		tokens.match(Token.DAY_TOKEN) ||
		tokens.match(Token.MONTH_TOKEN) ||
		tokens.match(Token.YEAR_TOKEN) ||
                tokens.match(Token.SQRT_TOKEN) ||
                tokens.match(Token.ABS_TOKEN))
	    expression = parseFunction(tokens);

	// EXPRESSION
	else if(tokens.match(Token.LEFT_PARENTHESIS_TOKEN)) {
	    tokens.pop();
	    expression = parseExpression(tokens);
	    parseRightParenthesis(tokens);
	}

	// VARIABLE
        else if(tokens.match(Token.VARIABLE_TOKEN))
	    expression = parseVariable(tokens);

	else
	    throw new ParserException("unexpected symbol");

	return expression;
    }

    private static Expression parseVariable(TokenStack tokens) 
	throws ParserException {

	Token variable = tokens.pop();
	Expression expression;

        if(variable.getType() == Token.VARIABLE_TOKEN) 
            return ExpressionFactory.newExpression(variable);
        else
            throw new ParserException("unexpected symbol");
    }

    private static Expression parseQuote(TokenStack tokens) 
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

    private static Expression parseNumber(TokenStack tokens) 
	throws ParserException {

	Token number = tokens.pop();
	boolean negate = false;

        if(number.getType() == Token.TRUE_TOKEN ||
           number.getType() == Token.FALSE_TOKEN)
            return ExpressionFactory.newExpression(number);

        else {
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
    }

    private static Expression parseFunction(TokenStack tokens) 
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
	case(Token.RSI_TOKEN):	  
	    arg1 = parseQuote(tokens);
	    parseComma(tokens);
	    arg2 = parseExpression(tokens);
	    break;

	case(Token.MIN_TOKEN):
	case(Token.MAX_TOKEN):
	case(Token.AVG_TOKEN):
	case(Token.SUM_TOKEN):
	    arg1 = parseQuote(tokens);
	    parseComma(tokens);
	    arg2 = parseExpression(tokens);
	    parseComma(tokens);
	    arg3 = parseExpression(tokens);	    
	    break;

	case(Token.PERCENT_TOKEN): 
	    arg1 = parseExpression(tokens);
            parseComma(tokens);
	    arg2 = parseExpression(tokens);
            break;

	case(Token.NOT_TOKEN):
        case(Token.SQRT_TOKEN):
        case(Token.ABS_TOKEN):
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
	    
        case(Token.DAY_OF_WEEK_TOKEN):
        case(Token.DAY_OF_YEAR_TOKEN):
        case(Token.DAY_TOKEN):
        case(Token.MONTH_TOKEN):
        case(Token.YEAR_TOKEN):
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

    private static void parseComma(TokenStack tokens) throws ParserException {
	if(!tokens.pop(Token.COMMA_TOKEN))
	    throw new ParserException("expected comma");
    }

    private static void parseLeftParenthesis(TokenStack tokens) 
	throws ParserException {
	if(!tokens.pop(Token.LEFT_PARENTHESIS_TOKEN))
	    throw new ParserException("expected left parenthesis");
    }

    private static void parseRightParenthesis(TokenStack tokens) 
	throws ParserException {
	if(!tokens.pop(Token.RIGHT_PARENTHESIS_TOKEN))
	    throw new ParserException("missing right parenthesis");
    }

    private static void parseLeftBrace(TokenStack tokens) throws ParserException {
	if(!tokens.pop(Token.LEFT_BRACE_TOKEN))
	    throw new ParserException("expected left brace");
    }

    private static void parseRightBrace(TokenStack tokens) 
	throws ParserException {

	if(!tokens.pop(Token.RIGHT_BRACE_TOKEN))
	    throw new ParserException("missing right brace");
    }

    private static void parseElse(TokenStack tokens) throws ParserException {
	if(!tokens.pop(Token.ELSE_TOKEN))
	    throw new ParserException("expected else token");
    }
}
