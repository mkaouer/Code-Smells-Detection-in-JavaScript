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

/**
 * Create an executable expression from the given token and arguments.
 */
public class ExpressionFactory {
   
    // Cannot create an instance of this class
    private ExpressionFactory() {
	// not possible
    }

    /**
     * Create an executable terminal expression from the given token.
     *
     * @param	operation	the terminal expression, e.g. a number
     * @return	an executable expression
     */	
    public static Expression newExpression(Token operation) {
	return ExpressionFactory.newExpression(operation, null, null, null);
    }

    /**
     * Create an executable unary expression from the given token and argument.
     *
     * @param	operation	the unary expression, e.g. 
     *	"<code>not(X)</code>"
     * @param	arg1		the first argument
     * @return	an executable expression
     */	
    public static Expression newExpression(Token operation, Expression arg1) {
	return ExpressionFactory.newExpression(operation, arg1, null, null);
    }

    /**
     * Create an executable binary expression from the given token and 
     * arguments.
     *
     * @param	operation	the binary expression, e.g. 
     *	"<code>X and Y</code>"
     * @param	arg1		the first argument
     * @param	arg2		the second argument
     * @return	an executable expression
     */	
    public static Expression newExpression(Token operation, Expression arg1,
					   Expression arg2) {
	return ExpressionFactory.newExpression(operation, arg1, arg2, null);
    }

    /**
     * Create an executable ternary expression from the given token and 
     * arguments.
     *
     * @param	operation	the ternary expression, e.g. 
     *	"<code>if(X) { Y } else { Z }</code>"
     * @param	arg1		the first argument
     * @param	arg2		the second argument
     * @param	arg3		the third argument
     * @return	an executable expression
     */	
    public static Expression newExpression(Token operation, Expression arg1,
					   Expression arg2, Expression arg3) {

	Expression expression = null;

	switch(operation.getType()) {
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
	case(Token.HELD_TOKEN):
	    break;
	case(Token.DAY_OPEN_TOKEN):
	    expression = new DayOpenExpression();
	    break;
	case(Token.DAY_CLOSE_TOKEN):
	    expression = new DayCloseExpression();
	    break;
	case(Token.DAY_LOW_TOKEN):
	    expression = new DayLowExpression();
	    break;
	case(Token.DAY_HIGH_TOKEN):
	    expression = new DayHighExpression();
	    break;
	case(Token.DAY_VOLUME_TOKEN):
	    expression = new DayVolumeExpression();
	    break;
	case(Token.NUMBER_TOKEN):
	    expression = new NumberExpression(operation.getValue());
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
	case(Token.AGE_TOKEN):
	    break;
	case(Token.PERCENT_TOKEN):
	    expression = new PercentExpression(arg1, arg2);
	    break;
	case(Token.NOT_EQUAL_TOKEN):
	    expression = new NotEqualExpression(arg1, arg2);
	    break;
	}

	if(expression == null)
	    System.out.println("not implemented yet");


	return expression;
    }

}
