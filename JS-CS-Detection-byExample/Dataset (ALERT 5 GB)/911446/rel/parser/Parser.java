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

import java.util.ArrayList;
import java.util.Vector;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;

import nz.org.venice.parser.expression.ClauseExpression;
import nz.org.venice.parser.expression.DefineVariableExpression;
import nz.org.venice.parser.expression.GetVariableExpression;
import nz.org.venice.parser.expression.DefineParameterExpression;
import nz.org.venice.parser.expression.FunctionExpression;
import nz.org.venice.parser.expression.EvalFunctionExpression;
import nz.org.venice.parser.expression.LagExpression;
import nz.org.venice.parser.expression.NumberExpression;
import nz.org.venice.parser.expression.SetVariableExpression;
import nz.org.venice.parser.expression.StringExpression;
import nz.org.venice.parser.expression.IncludeExpression;

import nz.org.venice.quote.QuoteFunctions;
import nz.org.venice.prefs.StoredExpression;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.util.Locale;

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
 * ADD_OPERATOR      = "-" | "+"
 * MULTIPLY_EXPR     = FACTOR [ MULTIPLY_OPERATOR FACTOR ]
 * MULTIPLY_OPERATOR = "*" | "/"
 * FACTOR            = STRING | VARIABLE | NUMBER | FUNCTION | FLOW_CONTROL | QUOTE | "(" SUB_EXPR ")"
 * STRING            = "{a-zA-Z0-9}*"
 * NUMBER            = ["-"]{0-9}+ ["." {0-9}+] | "true" | "false"
 * VARIABLE_NAME     = {a-zA-Z}{a-zA-Z0-9_}*
 * TYPE              = "boolean" | "float" | "int"
 * VARIABLE          = [["const"] [TYPE]] VARIABLE_NAME ["=" SUB_EXPR]
 * QUOTE             = "open" [ "(" SYMBOL ")" ] | "close" [ "(" SYMBOL ")" ] |
                        "low" [ "(" SYMBOL ")" ] | "high" [ "(" SYMBOL ")" | 
			"volume" [ "(" SYMBOL ")" ] 
 * SYMBOL            = {A-Za-z}*
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
 *                     "random" "(" SUB_EXPR ")"       |
 *                     "trend" (" QUOTE "," SUB_EXPR ["," SUB_EXPR] ")"
 * FLOW_CONTROL      = "if"  "(" SUB_EXPR ")" EXPR "else" EXPR |
 *                     "for" "(" SUB_EXPR ";" SUB_EXPR ";" SUB_EXPR ")" EXPR |
 *                     "while" "(" SUB_EXPR ")" EXPR
 * </pre>
 *
 * @author Andrew Leppard
 */
public class Parser {

    private static HashMap tokenLineMap;
    private static HashMap parseTree;
    private static HashMap parameterMap;
    
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

	return parse(variables, string, false);
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
        return parse(new Variables(), string, false);
    }

    //Private constructor - set internal to true only for rules including other
    //rules. Otherwise parse metadata from a previous "run" will be available
    //and that will break things like parameter count checking.

    private static Expression parse(Variables variables, String string, boolean internal) throws ExpressionException {
		
	if (string == null || string.equals("")) {
	    throw new ExpressionException(Locale.getString("MISSING_EQUATION_NAME"));
	}

	createMaps(internal);	

	// Perform lexical analysis on string - i.e. reduce it to stack of
	// tokens
	TokenStack tokens = lexicalAnalysis(variables, string);

	// Translate stack of tokens to expression
	Expression expression = parseRootExpression(variables, tokens);

	// There should be no more tokens
	if(tokens.size() > 0)
	    throw new ParserException(Locale.getString("EXTRANEOUS_TEXT_ERROR"));
	expression.setParseMetadata(parseTree, tokenLineMap);

	// Check for type mismatch
	expression.checkType();	
	
	return expression;

    }

    private static void createMaps(boolean internal) {
	if (internal) {
	    if (parseTree == null) {
		parseTree = new HashMap();	
	    }
	    if (tokenLineMap == null) {
		tokenLineMap = new HashMap();	
	    }
	    if (parameterMap == null) {
		parameterMap = new HashMap();
	    }
	} else {
	    parseTree = new HashMap();	
	    tokenLineMap = new HashMap();	
	    parameterMap = new HashMap();
	} 
    }
    
    private static TokenStack lexicalAnalysis(Variables variables, String string)
	throws ParserException {

	TokenStack tokens = new TokenStack();
	Token token;
	int lineCount = 0;

	while(string.length() > 0) {

	    // skip spaces
	    while(string.length() > 0 &&
		  Character.isWhitespace(string.charAt(0))) {
		if (string.charAt(0) == '\n') {
		    lineCount++;
		}
		string = string.substring(1);
	    }

	    if(string.length() > 0) {
		
		// Extract next token
		token = new Token();
		string = Token.stringToToken(variables, token, string);
		if (token.getType() != Token.COMMENT_TOKEN) {
		    tokens.add(token);
		}		
		//Associate line number with token
		tokenLineMap.put(token, new Integer(lineCount));		
	    }
	}

	return tokens;
    }

    private static Expression parseRootExpression(Variables variables, TokenStack tokens)
	throws ParserException {
	
	List subExpressions = new ArrayList();
	Token head = tokens.get();

	while (head.getType() == Token.INCLUDE_TOKEN) {
	    tokens.pop();	    

	    List includedExpressions = new ArrayList();

	    if (!tokens.match(Token.STRING_TOKEN)) {
		throw new ParserException(Locale.getString("EXPECTED_STRING_TYPE_ERROR"));
	    }
	    StringExpression stringExpression = (StringExpression)parseString(variables, tokens);
	    String includeName = stringExpression.getText();
	    
	    //Read equation text
	    StoredExpression includedStoredExpression = null;
	    List storedExpressions = PreferencesManager.getStoredExpressions();
	    Iterator iterator  = storedExpressions.iterator();
	    while (iterator.hasNext()) {
		StoredExpression storedExpression =
		    (StoredExpression)iterator.next();
		if (storedExpression.name.equals(includeName)) {
		    includedStoredExpression = storedExpression;
		    break;
		}
	    }
	    
	    if (includedStoredExpression == null) {
		throw new ParserException(Locale.getString("UNKNOWN_IDENTIFIER_ERROR", includeName));
	    } else {
		try {
		    Expression includedExpression = 
			Parser.parse(variables, 
				     includedStoredExpression.expression, 
				     true); 
		    
		    //Included Expression different to ClauseExpression
		    //in that variables defined and set stay in scope.
		    //Allows an included function to access an included variable
		    
		    for (int i = 0; i < includedExpression.getChildCount(); i++) {
			Expression c = includedExpression.getChild(i);
			includedExpressions.add(c);
		    }
		    
		    IncludeExpression incExpression = 
			new IncludeExpression(includedExpressions);

		    subExpressions.add(incExpression);


		} catch (ExpressionException e) {
		    throw new ParserException(e.getReason()); 
		} finally {
		    
		}
	    }
	    head = tokens.get();	    
	}

	

	while(tokens.size() > 0) {
	    subExpressions.add(parseSubExpression(variables, tokens));	
	}
	
	if(subExpressions.size() == 0)
	    throw new ParserException(Locale.getString("EMPTY_EQUATION_ERROR"));
        else if(subExpressions.size() == 1) {	    
	    parseTree.put(subExpressions.get(0), head);	    
	    return (Expression)subExpressions.get(0);
        } else {
	    Expression clauseExpression = new ClauseExpression(subExpressions);
	    parseTree.put(clauseExpression, head);
	    return clauseExpression;
	}
    }

    private static Expression parseExpression(Variables variables, TokenStack tokens)
	throws ParserException {

	Token head = tokens.get();

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
	    Expression clauseExpression = new ClauseExpression(subExpressions);
	    parseTree.put(clauseExpression, head);
	    return clauseExpression;
	}
	
	// Otherwise parse a single sub-expression
	else {
	    Expression subExpression = parseSubExpression(variables, tokens);
	    parseTree.put(subExpression, head);
	    return subExpression;
	}
    }

    private static Expression parseSubExpression(Variables variables, TokenStack tokens)
	throws ParserException {
	
	Token head = tokens.get();
	Expression left = parseBooleanExpression(variables, tokens);

	if(tokens.match(Token.AND_TOKEN) ||
	   tokens.match(Token.OR_TOKEN)) {

	    Token operation = tokens.pop();
	    Expression right = parseBooleanExpression(variables, tokens);

	    Expression subExpression =
		ExpressionFactory.newExpression(operation, left, right);
	    parseTree.put(subExpression, head);
	    return subExpression;
	}
	parseTree.put(left, head);
	return left;
    }

    private static Expression parseBooleanExpression(Variables variables, TokenStack tokens)
	throws ParserException {

	Token head = tokens.get();
	Expression left = parseAddExpression(variables, tokens);

	if(tokens.match(Token.EQUAL_TOKEN) ||
	   tokens.match(Token.NOT_EQUAL_TOKEN) ||
	   tokens.match(Token.LESS_THAN_EQUAL_TOKEN) ||
	   tokens.match(Token.LESS_THAN_TOKEN) ||
	   tokens.match(Token.GREATER_THAN_TOKEN) ||
	   tokens.match(Token.GREATER_THAN_EQUAL_TOKEN)) {
	
	    Token operation = tokens.pop();
	    Expression right = parseAddExpression(variables, tokens);
	    parseTree.put(right, operation);

	    Expression boolExpression = 
		ExpressionFactory.newExpression(operation, left, right);
	    parseTree.put(boolExpression, head);
	    
	    return boolExpression;
	}
	parseTree.put(left, head);
	return left;
    }
	
    private static Expression parseAddExpression(Variables variables, TokenStack tokens)
	throws ParserException {

	Token head = tokens.get();
	Expression left = parseMultiplyExpression(variables, tokens);

	if(tokens.match(Token.ADD_TOKEN) ||
	   tokens.match(Token.SUBTRACT_TOKEN)) {
	    Token operation = tokens.pop();	
	    Expression right = parseMultiplyExpression(variables, tokens);
	    parseTree.put(right, operation);

	    Expression addExpression = 
		ExpressionFactory.newExpression(operation, left, right);
	    parseTree.put(addExpression, head);
	    return addExpression;
	    
	}
	parseTree.put(left, head);
	return left;
    }

    private static Expression parseMultiplyExpression(Variables variables, TokenStack tokens)
	throws ParserException {
	
	Token head = tokens.get();
	Expression left = parseFactor(variables, tokens);

	if(tokens.match(Token.MULTIPLY_TOKEN) ||
	   tokens.match(Token.DIVIDE_TOKEN)) {
	
	    Token operation = tokens.pop();
	    Expression right = parseFactor(variables, tokens);
	    parseTree.put(right, operation);

	    Expression multExpression = 
		ExpressionFactory.newExpression(operation, left, right);
	    parseTree.put(multExpression, head);
	    
	    return multExpression;
	}
	parseTree.put(left, head);
	return left;
    }	

    private static Expression parseFactor(Variables variables, TokenStack tokens)
	throws ParserException {

	Expression expression;
	Token head = tokens.get();

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
                tokens.match(Token.EXP_TOKEN) ||
	        tokens.match(Token.TREND_TOKEN) ||
		tokens.match(Token.RANDOM_TOKEN) ||
	        tokens.match(Token.ALERT_TOKEN) ||
		tokens.match(Token.HALT_TOKEN) ||
	        tokens.match(Token.FLOOR_TOKEN) ||
		tokens.match(Token.CEIL_TOKEN) ||
	        tokens.match(Token.LOGGING_TOKEN) ||
		tokens.match(Token.DATACHECK_TOKEN))
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
	else {
	    throw new ParserException(Locale.getString("UNEXPECTED_SYMBOL_ERROR"));

	}
	parseTree.put(expression, head);
	return expression;
    }

    private static Expression parseVariable(Variables variables, TokenStack tokens)
	throws ParserException {

	Token token = tokens.pop();
	assert token.getType() == Token.VARIABLE_TOKEN;
	
	Variable variable = variables.get(token.getVariableName());
       
	// Make sure the variable is defined
	if(variable == null) {
	    throw new ParserException(Locale.getString("UNKNOWN_IDENTIFIER_ERROR",
                                                       token.getVariableName()));
	}
	else if(tokens.match(Token.SET_TOKEN)) {
 	    tokens.pop();	

	    // Make sure we aren't trying to set a constant
	    if(variable.isConstant())
		throw new ParserException(Locale.getString("VARIABLE_IS_CONSTANT_ERROR",
                                                           token.getVariableName()));

	    //Make sure we aren't trying to assign a variable to a function
	    if (variable.isFunction()) 
		throw new ParserException(Locale.getString("VARIABLE_IS_FUNCTION"));

	    Expression value = parseSubExpression(variables, tokens);
	    Expression setVarExpression = 
		new SetVariableExpression(token.getVariableName(), 
					  variable.getType(),
					  value);

	    parseTree.put(setVarExpression, token);
	    return setVarExpression;
	}
	else {	    
	    if (variable.isFunction()) {		
		Vector parameterList = new Vector();

		parseLeftParenthesis(variables, tokens);
		
		Expression parameterExpression = null;
		
                while (!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
		    parameterExpression = parseSubExpression(variables, tokens);
		    parameterList.add(parameterExpression);

		    if (!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
			parseComma(variables, tokens);
		    }
                }
		
		/* If no parameters were supplied, add a terminal
		   expression which will be ignored. (ClauseExpression
		   requires at least one child)
		*/
		if (parameterExpression == null) {
		    parameterExpression = new NumberExpression(0);
		    parameterList.add(parameterExpression);
		}

		parseRightParenthesis(variables, tokens);
		
		Expression parameterListExpression = 
		    new ClauseExpression(parameterList);

		Expression evalFunctionExpression = 
		    new EvalFunctionExpression(variable.getName(),
					       variable.getType(),
					       parameterListExpression);
				
		parseTree.put(evalFunctionExpression, token);		
		return evalFunctionExpression;
	    } else {
		Expression getVarExpression = 
		    new GetVariableExpression(token.getVariableName(), 
					      variable.getType());
		parseTree.put(getVarExpression, token);
		return getVarExpression;
	    }
	}
    }

    private static Expression parseDefineVariable(Variables variables, TokenStack tokens)
	throws ParserException {

	String name ;
	boolean isConstant = false;
	Expression value = null;
	int type;
	Token token, head;

	// Parse "const"
	if(tokens.match(Token.CONSTANT_TOKEN)) {
	    isConstant = true;
	    tokens.pop();
	}
	
	// Parse the variable type: "boolean" | "float" | "int"
	token = tokens.pop();
	head = token;
	
	if(token.getType() == Token.BOOLEAN_TOKEN)
	    type = Expression.BOOLEAN_TYPE;
	else if(token.getType() == Token.FLOAT_TOKEN)
	    type = Expression.FLOAT_TYPE;
	else if(token.getType() == Token.INTEGER_TOKEN)
	    type = Expression.INTEGER_TYPE;
	else 
	    throw new ParserException(Locale.getString("EXPECTED_VARIABLE_TYPE_ERROR"));
			
	if (tokens.match(Token.FUNCTION_TOKEN)) {	    	   
	    return parseUserFunction(variables, tokens, type);
	}		
	
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
	// or if it is, it's defined as a function parameter
	
	if(variables.contains(name) && parameterMap.get(name) == null) {
	    throw new ParserException(Locale.getString("VARIABLE_DEFINED_ERROR", name));
	    
	}

	// Add variable	
	if (parameterMap.get(name) == null) {
	    variables.add(name, type, isConstant);
	}
	Expression defVarExpression = 
	    new DefineVariableExpression(name, type, isConstant, value);

	parseTree.put(defVarExpression, head);

	return defVarExpression;
	
    }

    private static Expression parseQuote(Variables variables, TokenStack tokens)
	throws ParserException {
	
	Token quote = tokens.pop();
	Expression expression;
	Expression symbol = null;

	//Has the user supplied an explicit symbol?
	if(tokens.match(Token.LEFT_PARENTHESIS_TOKEN)) {
	    parseLeftParenthesis(variables, tokens);	    
	    symbol = parseString(variables, tokens);	    
	    parseRightParenthesis(variables, tokens);
	}

	switch(quote.getType()) {
	case(Token.DAY_OPEN_TOKEN):
	case(Token.DAY_CLOSE_TOKEN):
	case(Token.DAY_LOW_TOKEN):
	case(Token.DAY_HIGH_TOKEN):
	case(Token.DAY_VOLUME_TOKEN):
	    expression = ExpressionFactory.newExpression(quote, symbol); 
	    break;	    
	default:
	    throw new ParserException(Locale.getString("EXPECTED_QUOTE_TYPE_ERROR"));
	}

	parseTree.put(expression, quote);
	return expression;
    }

    private static Expression parseString(Variables variables, TokenStack tokens)
	throws ParserException {

	Token string = tokens.pop();

        if(string.getType() == Token.STRING_TOKEN) {
	    Expression strExpression = ExpressionFactory.newExpression(string);
	    parseTree.put(strExpression, string);
	    return strExpression;
	} else {
            throw new ParserException(Locale.getString("EXPECTED_STRING_TYPE_ERROR"));
	}
    }

    private static Expression parseNumber(Variables variables, TokenStack tokens)
	throws ParserException {

	Token number = tokens.pop();
	boolean negate = false;

        if(number.getType() == Token.TRUE_TOKEN ||
           number.getType() == Token.FALSE_TOKEN) {
            Expression numExpression = ExpressionFactory.newExpression(number);
	    parseTree.put(numExpression, number);
	    return numExpression;
	}

        else {
            // Is there a "-" infront? Handle negative numbers
            if(number.getType() == Token.SUBTRACT_TOKEN) {
                number = tokens.pop();
		negate = true;
            }

            if(number.getType() == Token.NUMBER_TOKEN) {
                if(negate)
                    number.negate();
		Expression numExpression = 
		    ExpressionFactory.newExpression(number);	
		parseTree.put(numExpression, number);
		return numExpression;
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

	case(Token.TREND_TOKEN):
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
	case(Token.FLOOR_TOKEN):
	case(Token.CEIL_TOKEN):
	case (Token.RANDOM_TOKEN):
	    //Parse optional seed argument
	    if (!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
		arg1 = parseSubExpression(variables, tokens);
	    } 
	    break;
	case (Token.ALERT_TOKEN):
	    arg1 = parseSubExpression(variables, tokens);
	    //Parse String arguments 
	    if (!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
		parseComma(variables, tokens);
		arg2 = parseSubExpression(variables, tokens);
	    }
	    if (!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
		parseComma(variables, tokens);
		arg3 = parseSubExpression(variables, tokens);
	    }
	    if (!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
		parseComma(variables, tokens);
		arg4 = parseSubExpression(variables, tokens);
	    }
	    break;
	case (Token.HALT_TOKEN):
	    arg1 = parseSubExpression(variables, tokens);
	    //Parse String arguments 
	    if (!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
		parseComma(variables, tokens);
		arg2 = parseSubExpression(variables, tokens);
	    }
	    if (!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
		parseComma(variables, tokens);
		arg3 = parseSubExpression(variables, tokens);
	    }
	    if (!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
		parseComma(variables, tokens);
		arg4 = parseSubExpression(variables, tokens);
	    }
	    break;	
	case (Token.LOGGING_TOKEN):
	    arg1 = parseSubExpression(variables, tokens);
	    //Parse String arguments 
	    if (!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
		parseComma(variables, tokens);
		arg2 = parseSubExpression(variables, tokens);
	    }
	    if (!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
		parseComma(variables, tokens);
		arg3 = parseSubExpression(variables, tokens);
	    }
	    if (!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
		parseComma(variables, tokens);
		arg4 = parseSubExpression(variables, tokens);
	    }
	    break;
	case (Token.DATACHECK_TOKEN):
	    arg1 = parseQuote(variables, tokens);
	    parseComma(variables, tokens);
	    arg2 = parseSubExpression(variables, tokens);
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

	parseTree.put(expression, function);
	return expression;
    }

    private static Expression parseDayQuoteFunction(Variables variables, TokenStack tokens)
        throws ParserException {

	Token head = tokens.get();
	Expression lagExpression = 
	    new LagExpression(parseQuote(variables, tokens),
			      new NumberExpression(0));

	parseTree.put(lagExpression, head);
	return lagExpression;
    }

    private static Expression parseFlowControl(Variables variables, TokenStack tokens)
	throws ParserException {

	Token token = tokens.pop();
	Expression flowExpression;

	// All control flow functions have a left parenthesis after the function.
	parseLeftParenthesis(variables, tokens);

	if(token.getType() == Token.IF_TOKEN) {
	    Expression condition = parseSubExpression(variables, tokens);
	    parseRightParenthesis(variables, tokens);
	    Expression ifTrue = parseExpression(variables, tokens);
	    parseElse(variables, tokens);
	    Expression ifFalse = parseExpression(variables, tokens);
	    flowExpression = ExpressionFactory.newExpression(token, condition, ifTrue, ifFalse);
	}
	else if(token.getType() == Token.WHILE_TOKEN) {
	    Expression condition = parseSubExpression(variables, tokens);	
	    parseRightParenthesis(variables, tokens);
	    Expression command = parseExpression(variables, tokens);
	    flowExpression = ExpressionFactory.newExpression(token, condition, command);
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
	    flowExpression = ExpressionFactory.newExpression(token, initial, condition, loop, command);
	}
	parseTree.put(flowExpression, token);
	return flowExpression;
    }
    
    private static Expression parseUserFunction(Variables variables, TokenStack tokens, int type) throws ParserException {
	
	Token token = tokens.pop();
	assert token.getType() == Token.FUNCTION_TOKEN;
	
	Token functionName = tokens.pop();
	assert functionName.getType() == Token.VARIABLE_TOKEN;

	String functionNameStr = functionName.getVariableName();
	
	if (variables.contains(functionNameStr) &&
	    parameterMap.get(functionNameStr) == null) {
	    throw new ParserException(Locale.getString("VARIABLE_DEFINED_ERROR", functionName.getVariableName()));


	}

	variables.add(functionNameStr, 
		      type, 
		      false,
		      true, 
		      0.0);


	
	//parameterExpression will be replaced if parameters are defined
	//otherwise a terminal expression which will then be ignored is used. 
	Expression parameterExpression = new NumberExpression(0);

	parseLeftParenthesis(variables, tokens);
	if (!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
	    //parseParameters eats the right parenthesis
	    parameterExpression = parseParameters(variables, 
						  tokens, 
						  functionName.
						  getVariableName());
	} else {	    
	    parseRightParenthesis(variables, tokens);
	}
	
	parseLeftBrace(variables, tokens);
	Vector functionBodyList = new Vector();
	
	boolean inClause = true;
	while(inClause) {
	    Expression se = parseSubExpression(variables, tokens);	    
	    functionBodyList.add(se);

	    // If there are no more symbols then we are mising the matching "}"
	    if(tokens.size() == 0) 
		throw new ParserException(Locale.getString("MISSING_RIGHT_BRACE_ERROR"));
		
	    // Keep parsing sub-expressions until we find the matching "}"
	    if(tokens.match(Token.RIGHT_BRACE_TOKEN)) {
		tokens.pop();
		inClause = false;	    
	    }
	}

	Expression functionBody = new ClauseExpression(functionBodyList);		
	Expression userFunction = new FunctionExpression(functionName.getVariableName(), type, parameterExpression, functionBody);
	
	parseTree.put(userFunction, token);
	return userFunction;
    }

    private static Expression parseParameters(Variables variables, TokenStack tokens, String functionName) throws ParserException {

	Token token = tokens.pop();
	int type;
	String name;
	
	Vector parameterExpressions = new Vector();

	while (token.getType() != Token.RIGHT_PARENTHESIS_TOKEN) {
	    
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


	    Expression parameterExpression = 
		new DefineParameterExpression(name, type);
	    
	    //Check that if the parameter is repeated, it's not doing so 
	    //as part of the same signature.
	    if (parameterMap.get(name) != null &&
		parameterMap.get(name).equals(functionName)) {
		throw new ParserException(Locale.getString("VARIABLE_DEFINED_ERROR", name));
	    }

	    //Only add the variable if it has not already been added.
	    //It needs to be set once - if it's a variable, it's value 
	    //should persist. If it's a parameter, it's value will get
	    //overridden and have the passed value.
	    if (variables.get(name) == null) {
		variables.add(name, type, false);
		parameterMap.put(name, functionName);
	    }



	    parameterExpressions.add(parameterExpression);
	    if (tokens.match(Token.COMMA_TOKEN)) {
		parseComma(variables, tokens);
	    }

	    if (!tokens.match(Token.BOOLEAN_TOKEN) &&
		!tokens.match(Token.FLOAT_TOKEN) &&
		!tokens.match(Token.INTEGER_TOKEN) &&
		!tokens.match(Token.RIGHT_PARENTHESIS_TOKEN)) {
		throw new ParserException(Locale.getString("UNEXPECTED_SYMBOL_ERROR"));
	    }
	    token = tokens.pop();
	}	
	
	Expression parameterList = new ClauseExpression(parameterExpressions);
	parseTree.put(parameterList, token);
	return parameterList;
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
