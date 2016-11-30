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

import org.mov.parser.expression.*;
import org.mov.quote.Quote;

/**
 * Create an executable expression from the given token and arguments.
 *
 * @author Andrew Leppard
 */
public class ExpressionFactory {

    // Cannot create an instance of this class
    private ExpressionFactory() {
        // not possible
        assert false;
    }

    /**
     * Create an executable terminal expression from the given token.
     *
     * @param	token	the terminal expression, e.g. a number
     * @return	an executable expression
     */	
    public static Expression newExpression(Token token) {
	return ExpressionFactory.newExpression(token, null, null, null, null);
    }

    /**
     * Create an executable unary expression from the given token and argument.
     *
     * @param	token	the unary expression, e.g. 
     *	"<code>not(X)</code>"
     * @param	arg1		the first argument
     * @return	an executable expression
     */	
    public static Expression newExpression(Token token, Expression arg1) {
	return ExpressionFactory.newExpression(token, arg1, null, null, null);
    }

    /**
     * Create an executable binary expression from the given token and 
     * arguments.
     *
     * @param	token	the binary expression, e.g. 
     *	"<code>X and Y</code>"
     * @param	arg1		the first argument
     * @param	arg2		the second argument
     * @return	an executable expression
     */	
    public static Expression newExpression(Token token, Expression arg1,
					   Expression arg2) {
	return ExpressionFactory.newExpression(token, arg1, arg2, null, null);
    }

    /**
     * Create an executable ternary expression from the given token and 
     * arguments.
     *
     * @param	token	the ternary expression, e.g. 
     *	"<code>if(X) { Y } else { Z }</code>"
     * @param	arg1		the first argument
     * @param	arg2		the second argument
     * @param	arg3		the third argument
     * @return	an executable expression
     */	
    public static Expression newExpression(Token token, Expression arg1, Expression arg2, 
					   Expression arg3) {
	return ExpressionFactory.newExpression(token, arg1, arg2, arg3, null);
    }

    /**
     * Create an executable quaternary expression from the given token and 
     * arguments.
     *
     * @param	token	the ternary expression, e.g. 
     *	"<code>if(X) { Y } else { Z }</code>"
     * @param	arg1		the first argument
     * @param	arg2		the second argument
     * @param	arg3		the third argument
     * @param	arg4		the fourth argument
     * @return	an executable expression
     */	
    public static Expression newExpression(Token token, Expression arg1, Expression arg2, 
					   Expression arg3, Expression arg4) {
	Expression expression = null;

	switch(token.getType()) {
	case(Token.AND_TOKEN):
	    expression = new AndExpression(arg1, arg2);
	    break;
	case(Token.OR_TOKEN):
	    expression = new OrExpression(arg1, arg2);
	    break;
	case(Token.EQUAL_TOKEN):
	    expression = new EqualThanExpression(arg1, arg2);
	    break;
	case(Token.LESS_THAN_EQUAL_TOKEN):
	    expression = new LessThanEqualExpression(arg1, arg2);
	    break;
	case(Token.LESS_THAN_TOKEN):
	    expression = new LessThanExpression(arg1, arg2);
	    break;
	case(Token.GREATER_THAN_TOKEN):
	    expression = new GreaterThanExpression(arg1, arg2);
	    break;
	case(Token.GREATER_THAN_EQUAL_TOKEN):
	    expression = new GreaterThanEqualExpression(arg1, arg2);
	    break;
	case(Token.ADD_TOKEN):
	    expression = new AddExpression(arg1, arg2);
	    break;
	case(Token.SUBTRACT_TOKEN):
	    expression = new SubtractExpression(arg1, arg2);
	    break;
	case(Token.MULTIPLY_TOKEN):
	    expression = new MultiplyExpression(arg1, arg2);
	    break;
	case(Token.DIVIDE_TOKEN):
	    expression = new DivideExpression(arg1, arg2);
	    break;
	case(Token.DAY_OPEN_TOKEN):
	    expression = new QuoteExpression(Quote.DAY_OPEN);
	    break;
	case(Token.DAY_CLOSE_TOKEN):
	    expression = new QuoteExpression(Quote.DAY_CLOSE);
	    break;
	case(Token.DAY_LOW_TOKEN):
	    expression = new QuoteExpression(Quote.DAY_LOW);
	    break;
	case(Token.DAY_HIGH_TOKEN):
	    expression = new QuoteExpression(Quote.DAY_HIGH);
	    break;
	case(Token.DAY_VOLUME_TOKEN):
	    expression = new QuoteExpression(Quote.DAY_VOLUME);
	    break;
	case(Token.LAG_TOKEN):
	    expression = new LagExpression(arg1, arg2);
	    break;
	case(Token.MIN_TOKEN):
	    expression = new MinExpression(arg1, arg2, arg3);
	    break;
	case(Token.MAX_TOKEN):
	    expression = new MaxExpression(arg1, arg2, arg3);
	    break;
	case(Token.AVG_TOKEN):
	    expression = new AvgExpression(arg1, arg2, arg3);
	    break;
	case(Token.RSI_TOKEN):
	    expression = new RSIExpression(arg1, arg2);
	    break;
	case(Token.NOT_TOKEN):
	    expression = new NotExpression(arg1);
	    break;
	case(Token.IF_TOKEN):
	    expression = new IfExpression(arg1, arg2, arg3);
	    break;
	case(Token.PERCENT_TOKEN):
	    expression = new PercentExpression(arg1, arg2);
	    break;
	case(Token.NOT_EQUAL_TOKEN):
	    expression = new NotEqualExpression(arg1, arg2);
	    break;
        case(Token.TRUE_TOKEN):
            expression = new NumberExpression(Expression.TRUE, Expression.BOOLEAN_TYPE);
            break;
        case(Token.FALSE_TOKEN):
            expression = new NumberExpression(Expression.FALSE, Expression.BOOLEAN_TYPE);
            break;
	case(Token.NUMBER_TOKEN):
	    expression = new NumberExpression(token.getValue(), token.getValueType());
	    break;
        case(Token.STRING_TOKEN):
            expression = new StringExpression(token.getStringValue());
            break;
	case(Token.DAY_OF_WEEK_TOKEN):
	    expression = new DayOfWeekExpression();
	    break;
	case(Token.DAY_OF_YEAR_TOKEN):
	    expression = new DayOfYearExpression();
	    break;
	case(Token.DAY_TOKEN):
	    expression = new DayExpression();
	    break;
	case(Token.MONTH_TOKEN):
	    expression = new MonthExpression();
	    break;
	case(Token.YEAR_TOKEN):
	    expression = new YearExpression();
	    break;
	case(Token.SUM_TOKEN):
	    expression = new SumExpression(arg1, arg2, arg3);
	    break;
        case(Token.SQRT_TOKEN):
            expression = new SqrtExpression(arg1);
            break;
        case(Token.ABS_TOKEN):
            expression = new AbsExpression(arg1);
            break;
        case(Token.FOR_TOKEN):
            expression = new ForExpression(arg1, arg2, arg3, arg4);
            break;
        case(Token.WHILE_TOKEN):
            expression = new WhileExpression(arg1, arg2);
            break;
        case(Token.CORR_TOKEN):
            expression = new CorrExpression(arg1, arg2, arg3, arg4);
            break;
        case(Token.EMA_TOKEN):
            expression = new EMAExpression(arg1, arg2, arg3, arg4);
            break;
        case(Token.BBL_TOKEN):
            expression = new BBLExpression(arg1, arg2, arg3);
            break;
        case(Token.BBU_TOKEN):
            expression = new BBUExpression(arg1, arg2, arg3);
            break;
        case(Token.MACD_TOKEN):
            expression = new MACDExpression(arg1, arg2);
            break;
        case(Token.MOMENTUM_TOKEN):
            expression = new MomentumExpression(arg1, arg2, arg3);
            break;
        case(Token.OBV_TOKEN):
            expression = new OBVExpression(arg1, arg2, arg3);
            break;
        case(Token.SD_TOKEN):
            expression = new StandardDeviationExpression(arg1, arg2, arg3);
            break;
        case(Token.SIN_TOKEN):
            expression = new SineExpression(arg1);
            break;
        case(Token.COS_TOKEN):
            expression = new CosineExpression(arg1);
            break;
        case(Token.LOG_TOKEN):
            expression = new LogarithmExpression(arg1);
            break;
        case(Token.EXP_TOKEN):
            expression = new ExponentialExpression(arg1);
            break;
        default:
            // No such token
            assert false;
	}

	return expression;
    }
    
    // return null if an Expression can't be correctly generated from the input String
    public static Expression newExpression(String inputExpressionString) {
        Expression expression = null;
        
        // We need to specify the variables that are given to the expression
        // expressions so they can be parsed properly.
        Variables variables = new Variables();
        variables.add("held", Expression.INTEGER_TYPE, Variable.CONSTANT);
        variables.add("order", Expression.INTEGER_TYPE, Variable.CONSTANT);
        variables.add("daysfromstart", Expression.INTEGER_TYPE, Variable.CONSTANT);
        variables.add("transactions", Expression.INTEGER_TYPE, Variable.CONSTANT);
        variables.add("capital", Expression.FLOAT_TYPE, Variable.CONSTANT);
        variables.add("stockcapital", Expression.FLOAT_TYPE, Variable.CONSTANT);
        
        if(inputExpressionString.length() == 0) {
            expression = null;
        } else {
            try {
                expression = Parser.parse(variables, inputExpressionString);
            }
            catch(ExpressionException e) {
                expression = null;
            }
        }
        return expression;
    }
}

