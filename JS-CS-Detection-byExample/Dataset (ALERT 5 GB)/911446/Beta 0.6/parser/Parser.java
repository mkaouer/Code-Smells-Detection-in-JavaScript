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

import java.util.ArrayList;
import java.util.List;

import org.mov.parser.expression.ClauseExpression;
import org.mov.parser.expression.DefineVariableExpression;
import org.mov.parser.expression.GetVariableExpression;
import org.mov.parser.expression.LagExpression;
import org.mov.parser.expression.NumberExpression;
import org.mov.parser.expression.SetVariableExpression;
import org.mov.quote.QuoteFunctions;
import org.mov.util.Locale;

/**
 * Parse a string into an executable expression. This class acts as
 * a gatekeeper to the <i>Gondala</i> language which is used to perform
 * manipulations on stock market quotes.
 * <p>
 * <h2>Langauge EBNF</h2>
 * <pre>

 * ROOT_EXPR         = [SUB_EXPR]+
 * EXPR              = SUB_EXPR | "{" [SUB_EXPR]+ "}"
 * SUB_EXPR          = BOOLEAN_EXPR [ LOGIC BOOLEAN_EXPR ]
 * LOGIC             = "and" | "or"
 * BOOLEAN_EXPR      = ADD_EXPR [ RELATION ADD_EXPR ]
 * RELATION          = "==" | "<=" | ">=" | "<" | ">" | "!="
 * ADD_EXPR          = MULTIPLY_EXPR [ ADD_OPERATOR MULTIPLY_EXPR ]
 * ADD_OPERATION     = "-" | "+"
 * MULTIPLY_EXPR     = FACTOR [ MULTIPLY_OPERATOR FACTOR ]
 * MULTIPLY_OPERATOR = "*" | "/"
 * FACTOR            = STRING | VARIABLE | NUMBER | FUNCTION | FLOW_CONTROL | QUOTE | "(" SUB_EXPR ")"
 * STRING            = "{a-zA-Z0-9}*"
 * NUMBER            = ["-"]{0-9}+ ["." {0-9}+] | "true" | "false"
 * VARIABLE_NAME     = {a-zA-Z}{a-zA-Z0-9_}*
 * TYPE              = "boolean" | "float" | "int"
 * VARIABLE          = [["const"] [TYPE]] VARIABLE_NAME ["=" SUB_EXPR]
 * QUOTE             = "open" | "close" | "low" | "high" | "volume"
 * FUNCTION          = "lag" "(" QUOTE ["," SUB_EXPR] ")" |
 *                     "min" "(" QUOTE "," SUB_EXPR ["," SUB_EXPR] ")" |
 *                     "max" "(" QUOTE "," SUB_EXPR ["," SUB_EXPR] ")" |
 *                     "avg" "(" QUOTE "," SUB_EXPR ["," SUB_EXPR] ")" |
 *                     "sd" "(" QUOTE "," SUB_EXPR ["," SUB_EXPR] ")" |
 *                     "sum" "(" QUOTE "," SUB_EXPR ["," SUB_EXPR] ")" |
 *                     "rsi" "(" [SUB_EXPR ["," SUB_EXPR]] ")" |
 *                     "not" "(" SUB_EXPR ")" |
 *                     "percent" "(" SUB_EXPR "," SUB_EXPR ")" |
 *                     "dayofweek" "(" ")" |
 *                     "dayofyear" "(" ")" |
 *                     "day" "(" ")"       |
 *                     "month" "(" ")"     |
 *                     "year" "(" ")"      |
 *                     "sqrt" "(" SUB_EXPR ")" |
 *                     "abs" "(" SUB_EXPR ")" |
 *                     "corr" "(" STRING "," QUOTE "," SUB_EXPR ["," SUB_EXPR] ")"
 *                     "ema" "(" QUOTE "," SUB_EXPR ["," SUB_EXPR] ["," SUB_EXPR] ")"
 *                     "bol_lower" "(" QUOTE "," SUB_EXPR ["," SUB_EXPR] ")" |
 *                     "bol_upper" "(" QUOTE "," SUB_EXPR ["," SUB_EXPR] ")" |
 *                     "macd" "(" QUOTE ["," SUB_EXPR] ")" |
 *                     "momentum" "(" QUOTE "," SUB_EXPR ["," SUB_EXPR] ")" |
 *                     "obv" "(" SUB_EXPR ["," SUB_EXPR] ["," SUB_EXPR] ")" |
 *                     "sd" "(" QUOTE "," SUB_EXPR ["," SUB_EXPR] ")" |
 *                     "sin" "(" SUB_EXPR ")" |
 *                     "cos" "(" SUB_EXPR ")" |
 *                     "log" "(" SUB_EXPR ")" |
 *                     "exp" "(" SUB_EXPR ")" |
 *                     "dayfromstart" "(" ")" |
 *                     "capital" "(" ")" |
 *                     "stockcapital" "(" ")" |
 * FLOW_CONTROL      = "if"  "(" SUB_EXPR ")" EXPR "else" EXPR |
 *                     "for" "(" SUB_EXPR ";" SUB_EXPR ";" SUB_EXPR ")" EXPR |
 *                     "while" "(" SUB_EXPR ")" EXPR
 * </pre>
 *
 * @author Andrew Leppard
 */
public class Parser {

    private Parser() {
        // class should not be instantiated
    }

    /**
     * Parse the given string into an executable expression. The first
     * argument allows you to parse any predefined variables which are
     * available to the expression. Any variables defined by the
     * expression will be added to this.
     *
     * @param variables any predefined variables.
     * @param string the string to parse.
     * @return the parsed expression.
     * @exception ExpressionException if there was an error parsing the expression.
     */
    public static Expression parse(Variables variables, String string)
        throws ExpressionException {

	// Perform lexical analysis on string - i.e. reduce it to stack of
	// tokens
	TokenStack tokens = lexicalAnalysis(variables, string);

	// Translate stack of tokens to expression
	Expression expression = parseRootExpression(variables, tokens);

	// There should be no more tokens
	if(tokens.size() > 0)
	    throw new ParserException(Locale.getString("EXTRANEOUS_TEXT_ERROR"));

	// Check for type mismatch
	expression.checkType();

	return expression;
    }

    /**
     * Parse the given string into an executable expression.
     *
     * @param string the string to parse.
     * @return the parsed expression.
     * @exception ExpressionException if there was an error parsing the expression.
     */
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

    private static Expression parseRootExpression(Variables variables, TokenStack tokens)
	throws ParserException {
	
	List subExpressions = new ArrayList();

	while(tokens.size() > 0)
	    subExpressions.add(parseSubExpression(variables, tokens));	

	if(subExpressions.size() == 0)
	    throw new ParserException(Locale.getString("EMPTY_EQUATION_ERROR"));
        else if(subExpressions.size() == 1)
	    return (Expression)subExpressions.get(0);
        else
	    return new ClauseExpression(subExpressions);
    }

    private static Expression parseExpression(Variables variables, TokenStack tokens)
	throws ParserException {

	// If the next symbol is "{" parse a list of sub-expressions
	if(tokens.match(Token.LEFT_BRACE_TOKEN)) {
	    List subExpressions = new ArrayList();
	    boolean inClause = true;

	    tokens.pop();

	    while(inClause) {
		subExpressions.add(parseSubExpression(variables, tokens));

		// If there are no more symbols then we are mising the matching "}"
		if(tokens.size() == 0)
		    throw new ParserException(Locale.getString("MISSING_RIGHT_BRACE_ERROR"));

		// Keep parsing sub-expressions until we find the matching "}"
		if(tokens.match(Token.RIGHT_BRACE_TOKEN)) {
		    tokens.pop();
		    inClause = false;
		}
	    }

	    // Finally group all these sub-expressions together into a "clause" which
	    // will execute the expressions sequentially.
	    return new ClauseExpression(subExpressions);
	}
	
	// Otherwise parse a single sub-expression
	else
	    return parseSubExpression(variables, tokens);
    }

    private static Expression parseSubExpression(Variables variables, TokenStack tokens)
	throws ParserException {
	
	Expression left = parseBooleanExpression(variables, tokens);

	if(tokens.match(Token.AND_TOKEN) ||
	   tokens.match(Token.OR_TOKEN)) {

	    Token operation = tokens.pop();
	    Expression right = parseBooleanExpression(variables, tokens);

	    return(ExpressionFactory.newExpression(operation, left, right));
	}
	return left;
    }

    private static Expression parseBooleanExpression(Variables variables, TokenStack tokens)
	throws ParserException {

	Expression left = parseAddExpression(variables, tokens);

	if(tokens.match(Token.EQUAL_TOKEN) ||
	   tokens.match(Token.NOT_EQUAL_TOKEN) ||
	   tokens.match(Token.LESS_THAN_EQUAL_TOKEN) ||
	   tokens.match(Token.LESS_THAN_TOKEN) ||
	   tokens.match(Token.GREATER_THAN_TOKEN) ||
	   tokens.match(Token.GREATER_THAN_EQUAL_TOKEN)) {
	
	    Token operation = tokens.pop();
	    Expression right = parseAddExpression(variables, tokens);

	    return(ExpressionFactory.newExpression(operation, left, right));
	}
	return left;
    }
	
    private static Expression parseAddExpression(Variables variables, TokenStack tokens)
	throws ParserException {

	Expression left = parseMultiplyExpression(variables, tokens);

	if(tokens.match(Token.ADD_TOKEN) ||
	   tokens.match(Token.SUBTRACT_TOKEN)) {

	    Token operation = tokens.pop();	
	    Expression right = parseMultiplyExpression(variables, tokens);

	    return(ExpressionFactory.newExpression(operation, left, right));
	}
	return left;
    }

    private static Expression parseMultiplyExpression(Variables variables, TokenStack tokens)
	throws ParserException {
	
	Expression left = parseFactor(variables, tokens);

	if(tokens.match(Token.MULTIPLY_TOKEN) ||
	   tokens.match(Token.DIVIDE_TOKEN)) {
	
	    Token operation = tokens.pop();
	    Expression right = parseFactor(variables, tokens);

	    return(ExpressionFactory.newExpression(operation, left, right));
	}
	
	return left;
    }	

    private static Expression parseFactor(Variables variables, TokenStack tokens)
	throws ParserException {

	Expression expression;

	// NUMBER
	if(tokens.match(Token.NUMBER_TOKEN) ||
           tokens.match(Token.TRUE_TOKEN) ||
           tokens.match(Token.FALSE_TOKEN) ||
           tokens.match(Token.SUBTRACT_TOKEN))
	    expression = parseNumber(variables, tokens);

        // STRING
        else if(tokens.match(Token.STRING_TOKEN))
            expression = parseString(variables, tokens);
	
	// FUNCTION
	else if(tokens.match(Token.LAG_TOKEN) ||
		tokens.match(Token.MIN_TOKEN) ||
		tokens.match(Token.MAX_TOKEN) ||
		tokens.match(Token.AVG_TOKEN) ||
		tokens.match(Token.SD_TOKEN) ||
		tokens.match(Token.SUM_TOKEN) ||
		tokens.match(Token.RSI_TOKEN) ||
		tokens.match(Token.NOT_TOKEN) ||
		tokens.match(Token.PERCENT_TOKEN) ||
		tokens.match(Token.DAY_OF_WEEK_TOKEN) ||
		tokens.match(Token.DAY_OF_YEAR_TOKEN) ||
		tokens.match(Token.DAY_TOKEN) ||
		tokens.match(Token.MONTH_TOKEN) ||
		tokens.match(Token.YEAR_TOKEN) ||
                tokens.match(Token.SQRT_TOKEN) ||
                tokens.match(Token.ABS_TOKEN) ||
                tokens.match(Token.CORR_TOKEN) ||
                tokens.match(Token.EMA_TOKEN) ||
                tokens.match(Token.BBL_TOKEN) ||
                tokens.match(Token.BBU_TOKEN) ||
                tokens.match(Token.MACD_TOKEN) ||
                tokens.match(Token.MOMENTUM_TOKEN) ||
                tokens.match(Token.OBV_TOKEN) ||
                tokens.match(Token.SD_TOKEN) ||
                tokens.match(Token.SIN_TOKEN) ||
                tokens.match(Token.COS_TOKEN) ||
                tokens.match(Token.LOG_TOKEN) ||
                tokens.match(Token.EXP_TOKEN))
	    expression = parseFunction(variables, tokens);

        // ABBREVIATION QUOTE FUNCTIONS
        else if (tokens.match(Token.DAY_OPEN_TOKEN) ||
                 tokens.match(Token.DAY_CLOSE_TOKEN) ||
                 tokens.match(Token.DAY_HIGH_TOKEN) ||
                 tokens.match(Token.DAY_LOW_TOKEN) ||
                 tokens.match(Token.DAY_VOLUME_TOKEN))
            expression = parseDayQuoteFunction(variables, tokens);

	// FLOW_CONTROL
	else if(tokens.match(Token.IF_TOKEN) ||
		tokens.match(Token.FOR_TOKEN) ||
		tokens.match(Token.WHILE_TOKEN))
	    expression = parseFlowControl(variables, tokens);

	// EXPRESSION
	else if(tokens.match(Token.LEFT_PARENTHESIS_TOKEN)) {
	    tokens.pop();
	    expression = parseSubExpression(variables, tokens);
	    parseRightParenthesis(variables, tokens);
	}

	// GET/SET VARIABLE
        else if(tokens.match(Token.VARIABLE_TOKEN))
	    expression = parseVariable(variables, tokens);
	
	// DECLARE VARIABLE
	else if(tokens.match(Token.CONSTANT_TOKEN) ||
		tokens.match(Token.BOOLEAN_TOKEN) ||
		tokens.match(Token.INTEGER_TOKEN) ||
		tokens.match(Token.FLOAT_TOKEN))
	    expression = parseDefineVariable(variables, tokens);

	else
	    throw new ParserException(Locale.getString("UNEXPECTED_SYMBOL_ERROR"));

	return expression;
    }

    private static Expression parseVariable(Variables variables, TokenStack tokens)
	throws ParserException {

	Token token = tokens.pop();
	assert token.getType() == Token.VARIABLE_TOKEN;
	
	Variable variable = variables.get(token.getVariableName());

	// Make sure the variable is defined
	if(variable == null)
	    throw new ParserException(Locale.getString("UNKNOWN_IDENTIFIER_ERROR",
                                                       token.getVariableName()));

	else if(tokens.match(Token.SET_TOKEN)) {
	    tokens.pop();	

	    // Make sure we aren't trying to set a constant
	    if(variable.isConstant())
		throw new ParserException(Locale.getString("VARIABLE_IS_CONSTANT_ERROR",
                                                           token.getVariableName()));

	    Expression value = parseSubExpression(variables, tokens);
	    return new SetVariableExpression(token.getVariableName(), variable.getType(),
					     value);
	}
	else
	    return new GetVariableExpression(token.getVariableName(), variable.getType());
    }

    private static Expression parseDefineVariable(Variables variables, TokenStack tokens)
	throws ParserException {

	String name ;
	boolean isConstant = false;
	Expression value = null;
	int type;
	Token token;

	// Parse "const"
	if(tokens.match(Token.CONSTANT_TOKEN)) {
	    isConstant = true;
	    tokens.pop();
	}
	
	// Parse the variable type: "boolean" | "float" | "int"
	token = tokens.pop();
	
	if(token.getType() == Token.BOOLEAN_TOKEN)
	    type = Expression.BOOLEAN_TYPE;
	else if(token.getType() == Token.FLOAT_TOKEN)
	    type = Expression.FLOAT_TYPE;
	else if(token.getType() == Token.INTEGER_TOKEN)
	    type = Expression.INTEGER_TYPE;
	else
	    throw new ParserException(Locale.getString("EXPECTED_VARIABLE_TYPE_ERROR"));
	
	// Parse the name
	token = tokens.pop();
	if(token.getType() == Token.VARIABLE_TOKEN)
	    name = token.getVariableName();
	else
	    throw new ParserException(Locale.getString("ILLEGAL_VARIABLE_NAME_ERROR"));
	
	// Parse the initial value (if any)
	if(tokens.match(Token.SET_TOKEN)) {
	    tokens.pop();
	    value = parseSubExpression(variables, tokens);
	}
	else
	    value = new NumberExpression(0.0D, type);

	// Check the variable isn't already defined
	if(variables.contains(name))
	    throw new ParserException(Locale.getString("VARIABLE_DEFINED_ERROR", name));

	// Add variable
	variables.add(name, type, isConstant);
	return new DefineVariableExpression(name, type, isConstant, value);
    }

    private static Expression parseQuote(Variables variables, TokenStack tokens)
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
	    throw new ParserException(Locale.getString("EXPECTED_QUOTE_TYPE_ERROR"));
	}

	return expression;
    }

    private static Expression parseString(Variables variables, TokenStack tokens)
	throws ParserException {

	Token string = tokens.pop();

        if(string.getType() == Token.STRING_TOKEN)
            return ExpressionFactory.newExpression(string);
        else
            throw new ParserException(Locale.getString("EXPECTED_STRING_TYPE_ERROR"));
    }

    private static Expression parseNumber(Variables variables, TokenStack tokens)
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
                throw new ParserException(Locale.getString("EXPECTED_NUMBER_ERROR"));
        }
    }

    private static Expression parseFunction(Variables variables, TokenStack tokens)
	throws ParserException {

	Expression expression;
	Expression arg1 = null;
	Expression arg2 = null;
	Expression arg3 = null;
	Expression arg4 = null;
	
	Token function = tokens.pop();

	// all functions must have a left parenthesis after the function
	// name
	parseLeftParenthesis(variables, tokens);

	switch(function.getType()) {
            
	case(Token.LAG_TOKEN):
	case(Token.MACD_TOKEN):
	    arg1 = parseQuote(variables, tokens);

            // Parse optional offset argument
            if(!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
		parseComma(variables, tokens);
		arg2 = parseSubExpression(variables, tokens);
	    }
	    else
		arg2 = new NumberExpression(0);

	    break;

	case(Token.MIN_TOKEN):
	case(Token.MAX_TOKEN):
	case(Token.AVG_TOKEN):
	case(Token.SUM_TOKEN):
	case(Token.BBL_TOKEN):
	case(Token.BBU_TOKEN):
	case(Token.MOMENTUM_TOKEN):
	case(Token.SD_TOKEN):
	    arg1 = parseQuote(variables, tokens);
	    parseComma(variables, tokens);
	    arg2 = parseSubExpression(variables, tokens);

            // Parse optional offset argument
            if(!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
		parseComma(variables, tokens);
		arg3 = parseSubExpression(variables, tokens);	
	    }
	    else
		arg3 = new NumberExpression(0);

	    break;

	case(Token.RSI_TOKEN):	
            // Default period and no offset
            arg1 = new NumberExpression(QuoteFunctions.DEFAULT_RSI_PERIOD);
            arg2 = new NumberExpression(0);

            // Parse optional period argument
            if(!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {

                arg1 = parseSubExpression(variables, tokens);

                // Parse optional offset argument
                if(!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
                    parseComma(variables, tokens);
                    arg2 = parseSubExpression(variables, tokens);
                }
            }

            break;

        case(Token.CORR_TOKEN):
            arg1 = parseString(variables, tokens);
            parseComma(variables, tokens);
            arg2 = parseQuote(variables, tokens);
            parseComma(variables, tokens);
            arg3 = parseSubExpression(variables, tokens);

            // Parse optional offset argument
            if(!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
                parseComma(variables, tokens);
                arg4 = parseSubExpression(variables, tokens);
            }
            else
                arg4 = new NumberExpression(0);

            break;

	case(Token.EMA_TOKEN):
	    arg1 = parseQuote(variables, tokens);
	    parseComma(variables, tokens);
	    arg2 = parseSubExpression(variables, tokens);

            // Parse optional offset/smoothing constant argument
            if(!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
		parseComma(variables, tokens);
		arg3 = parseSubExpression(variables, tokens);	
                // Parse optional smoothing constant argument
                if(!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
                    parseComma(variables, tokens);
                    arg4 = parseSubExpression(variables, tokens);	
                }
                else {
                    if (arg3.getType()==NumberExpression.INTEGER_TYPE) {
                        // ema(quote,period,lag)
                        arg4 = new NumberExpression(0.1D);
                    } else {
                        // ema(quote,period,smoothing constant)
                        arg4 = arg3;
                        arg3 = new NumberExpression(0);
                    }
                }
	    }
	    else {
		// ema(quote,period)
                arg3 = new NumberExpression(0);
		arg4 = new NumberExpression(0.1D);
            }

	    break;

	case(Token.OBV_TOKEN):
            arg2 = new NumberExpression(0);
            arg3 = new NumberExpression(50000);
	    arg1 = parseSubExpression(variables, tokens);
            // Parse optional offset argument
            if(!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {

                parseComma(variables, tokens);
                arg2 = parseSubExpression(variables, tokens);

                // Parse optional initialValue argument
                if(!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
                    parseComma(variables, tokens);
                    arg3 = parseSubExpression(variables, tokens);
                }
            }

            break;

	case(Token.PERCENT_TOKEN):
	    arg1 = parseSubExpression(variables, tokens);
            parseComma(variables, tokens);
	    arg2 = parseSubExpression(variables, tokens);
            break;

	case(Token.NOT_TOKEN):
        case(Token.SQRT_TOKEN):
        case(Token.ABS_TOKEN):
	case(Token.SIN_TOKEN):
	case(Token.COS_TOKEN):
	case(Token.LOG_TOKEN):
	case(Token.EXP_TOKEN):
	    arg1 = parseSubExpression(variables, tokens);
	    break;
	
        case(Token.DAY_OF_WEEK_TOKEN):
        case(Token.DAY_OF_YEAR_TOKEN):
        case(Token.DAY_TOKEN):
        case(Token.MONTH_TOKEN):
        case(Token.YEAR_TOKEN):
            break;

	default:
	    // We shouldn't have entered this function unless it was one of the above
	    assert false;
	}

	// Create epxression
	expression = ExpressionFactory.newExpression(function, arg1, arg2,
						     arg3, arg4);
	
	// All functions must end with a right parenthesis
	parseRightParenthesis(variables, tokens);

	return expression;
    }

    private static Expression parseDayQuoteFunction(Variables variables, TokenStack tokens)
        throws ParserException {

        return new LagExpression(parseQuote(variables, tokens),
                                 new NumberExpression(0));
    }

    private static Expression parseFlowControl(Variables variables, TokenStack tokens)
	throws ParserException {

	Token token = tokens.pop();

	// All control flow functions have a left parenthesis after the function.
	parseLeftParenthesis(variables, tokens);

	if(token.getType() == Token.IF_TOKEN) {
	    Expression condition = parseSubExpression(variables, tokens);
	    parseRightParenthesis(variables, tokens);
	    Expression ifTrue = parseExpression(variables, tokens);
	    parseElse(variables, tokens);
	    Expression ifFalse = parseExpression(variables, tokens);
	    return ExpressionFactory.newExpression(token, condition, ifTrue, ifFalse);
	}
	else if(token.getType() == Token.WHILE_TOKEN) {
	    Expression condition = parseSubExpression(variables, tokens);	
	    parseRightParenthesis(variables, tokens);
	    Expression command = parseExpression(variables, tokens);
	    return ExpressionFactory.newExpression(token, condition, command);
	}
	else {
	    assert token.getType() == Token.FOR_TOKEN;
	    Expression initial = parseSubExpression(variables, tokens);
	    parseSemicolon(variables, tokens);
	    Expression condition = parseSubExpression(variables, tokens);
	    parseSemicolon(variables, tokens);
	    Expression loop = parseSubExpression(variables, tokens);
	    parseRightParenthesis(variables, tokens);
	    Expression command = parseExpression(variables, tokens);
	    return ExpressionFactory.newExpression(token, initial, condition, loop, command);
	}
    }

    private static void parseComma(Variables variables, TokenStack tokens) throws ParserException {
	if(!tokens.pop(Token.COMMA_TOKEN))
	    throw new ParserException(Locale.getString("EXPECTED_COMMA_ERROR"));
    }

    private static void parseLeftParenthesis(Variables variables, TokenStack tokens)
	throws ParserException {
	if(!tokens.pop(Token.LEFT_PARENTHESIS_TOKEN))
	    throw new ParserException(Locale.getString("EXPECTED_LEFT_PARENTHESIS_ERROR"));
    }

    private static void parseRightParenthesis(Variables variables, TokenStack tokens)
	throws ParserException {
	if(!tokens.pop(Token.RIGHT_PARENTHESIS_TOKEN))
	    throw new ParserException(Locale.getString("MISSING_RIGHT_PARENTHESIS_ERROR"));
    }

    private static void parseLeftBrace(Variables variables, TokenStack tokens)
	throws ParserException {
	if(!tokens.pop(Token.LEFT_BRACE_TOKEN))
	    throw new ParserException(Locale.getString("EXPECTED_LEFT_BRACE_ERROR"));
    }

    private static void parseRightBrace(Variables variables, TokenStack tokens)
	throws ParserException {

	if(!tokens.pop(Token.RIGHT_BRACE_TOKEN))
	    throw new ParserException(Locale.getString("MISSING_RIGHT_BRACE_ERROR"));
    }

    private static void parseElse(Variables variables, TokenStack tokens) throws ParserException {
	if(!tokens.pop(Token.ELSE_TOKEN))
	    throw new ParserException(Locale.getString("EXPECTED_ELSE_ERROR"));
    }

    private static void parseSemicolon(Variables variables, TokenStack tokens) throws ParserException {
	if(!tokens.pop(Token.SEMICOLON_TOKEN))
	    throw new ParserException(Locale.getString("EXPECTED_SEMICOLON_ERROR"));
    }
}
