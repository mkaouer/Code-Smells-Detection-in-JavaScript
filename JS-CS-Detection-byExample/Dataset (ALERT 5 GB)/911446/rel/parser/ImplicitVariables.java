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

/**
 * A collection of the implicit variables which is always available in an expression evaluation.
 * @author  Mark Hummel
 */
public class ImplicitVariables {
    
    //Singleon instance of this class
    private static ImplicitVariables instance = null;
    private Variables referenceVars;

    private ImplicitVariables() {
	referenceVars = new Variables();	
	//A bit wasteful but preferable to copying the same chunk of code
	//multiple times throughout the analyser code.
	//Could probably just have a list of implicit names and check that
	init(referenceVars, true);		
    }
    
    /**
     * Return the static ImplcitVariables instance for this application 
     */
    public static synchronized ImplicitVariables getInstance() {
	if (instance == null) {
	    instance = new ImplicitVariables();
	}
	return instance;
    }
   
    private void init(Variables variables, boolean ordered) {
	
	if (ordered && !variables.contains("order")) {
	    variables.add("order", Expression.INTEGER_TYPE, Variable.CONSTANT);
	}
	
	if (!variables.contains("held")) {
	    variables.add("held", Expression.INTEGER_TYPE, Variable.CONSTANT);
	}
	
	if (!variables.contains("daysfromstart")) {
	    variables.add("daysfromstart", Expression.INTEGER_TYPE, Variable.CONSTANT);
	}
	if (!variables.contains("transactions")) {
	    variables.add("transactions", Expression.INTEGER_TYPE, Variable.CONSTANT);
	}
	if (!variables.contains("capital")) {
	    variables.add("capital", Expression.FLOAT_TYPE, Variable.CONSTANT);
	}
	if (!variables.contains("stockcapital")) {
	    variables.add("stockcapital", Expression.FLOAT_TYPE, Variable.CONSTANT);
	}

	if (!variables.contains("daysfromlasttransaction")) {
	    variables.add("daysfromlasttransaction", Expression.INTEGER_TYPE, Variable.CONSTANT);
	}
    }
    
    /**
     * Add the implicit variables into the set of variables provided.
     *
     * @param variables   The set of variables which will have the implcit 
     *                     variables added
     * @param ordered    Whether or not to add the order implicit variable
     */
    public void setup(Variables variables, boolean ordered) {		
	init(variables, ordered);	
    }

    /**
     * Used for checking the user defined variables don't clash with 
     * the implicit variables.
     * 
     * @return true if the variable being added is an implicit one 
     */
    public boolean contains(String name) {
	return referenceVars.contains(name);
    }
    
}