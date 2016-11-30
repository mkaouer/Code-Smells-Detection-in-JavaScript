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

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

/**
 * A collection of variables.
 */
public class Variables {

	// Mapping between names and variables
	private HashMap variables;

	/**
	 * Create a new empty collection of variables.
	 */
	public Variables() {
		variables = new HashMap();
	}


	/**
	 * Creates a deep clone of variables.
	 * 
	 * @return a clone.
	 * 
	 * @throws CloneNotSupportedException
	 *             this should never happen.
	 */
	public Object clone() throws CloneNotSupportedException {
		Variables results = null;
		results = new Variables();

		results.variables.putAll(variables);
		return (results);
	}

    /**
     * Creates a deep clone of variables and also clones the variables as well.
     * 
     * @return a clone, with variables cloned as well.
     * 
     * @throws CloneNotSupportedException
     *             this should never happen.
     */

    public Object copyVariables() throws CloneNotSupportedException {
	Variables results = null;
	results = new Variables();
	
	try {
	    Iterator iterator = variables.keySet().iterator();
	    while (iterator.hasNext()) {
		String key = (String)iterator.next();
		Variable variable = (Variable)variables.get(key);
		Variable newVariable = (Variable)variable.clone();
		results.variables.put(key, newVariable);
	    } 
	} catch (CloneNotSupportedException e) {
	    
	}
	
	return (results);
    }

	/**
	 * Return whether the collection contains the given variable.
	 * 
	 * @param name
	 *            the name of the variable o query.
	 */
	public boolean contains(String name) {
		return variables.containsKey(name);
	}

	/**
	 * Add a new variable. The variable will be initialised to zero if numeric
	 * or <code>FALSE</code> if boolean.
	 * 
	 * @param name
	 *            the name of the variable.
	 * @param type
	 *            the type of the variable.
	 * @param isConstant
	 *            set to <code>TRUE</code> if the variable is a constant.
	 */
	public void add(String name, int type, boolean isConstant) {
	    add(name, type, isConstant, 0.0D);
	}

	/**
	 * Add a new variable.
	 * 
	 * @param name
	 *            the name of the variable.
	 * @param type
	 *            the type of the variable.
	 * @param isConstant
	 *            set to <code>TRUE</code> if the variable is a constant.
	 * @param value
	 *            the initial value.
	 */
	public void add(String name, int type, boolean isConstant, double value) {
		if (!variables.containsKey(name)) {
			Variable variable = new Variable(name, type, isConstant, value);
			variables.put(name, variable);
		} else {
		    //This can happen if a user enters one of the implicit
		    //parameters which is always added.
		    assert false;
		}
	}

	/**
	 * Add a new variable.
	 * 
	 * @param name
	 *            the name of the variable.
	 * @param type
	 *            the type of the variable.
	 * @param isConstant
	 *            set to <code>TRUE</code> if the variable is a constant.
	 * @param value
	 *            the initial value.
	 */
	public void add(String name, int type, boolean isConstant, int value) {
		if (!variables.containsKey(name)) {
			Variable variable = new Variable(name, type, isConstant, value);
			variables.put(name, variable);
		} else
			assert false;
	}


	/**
	 * Add a new variable.
	 * 
	 * @param name
	 *            the name of the variable.
	 * @param type
	 *            the type of the variable.
	 * @param isConstant
	 *            set to <code>TRUE</code> if the variable is a constant.
	 * @param isFunction
	 *            set to <code>TRUE</code> if the variable is a function.
	 * @param value
	 *            the initial value.
	 */
    public void add(String name, int type, boolean isConstant, boolean isFunction, double value) {
	if (!variables.containsKey(name)) {
	    Variable variable = new Variable(name, type, isConstant, isFunction, value);
	    variables.put(name, variable);
	} else {
	    //This can happen if a user enters one of the implicit
	    //parameters which is always added.
	    
	    assert false;
	}	
    }

   
	/**
	 * Set the value of the given variable.
	 * 
	 * @param name
	 *            the name of the variable.
	 * @param value
	 *            the new value of the variable.
	 */
	public void setValue(String name, double value) {
		Variable variable = get(name);

		if (variable != null)
			variable.setValue(value);
		else
		    assert false;
	}

	/**
	 * Set the value of the given variable.
	 * 
	 * @param name
	 *            the name of the variable.
	 * @param value
	 *            the new value of the variable.
	 */
	public void setValue(String name, int value) {
		Variable variable = get(name);

		if (variable != null)
			variable.setValue(value);
		else
			assert false;
	}

	/**
	 * Get the value of the variable.
	 * 
	 * @param name
	 *            the name of the variable.
	 * @return the variable's value.
	 */
	public double getValue(String name) {
		Variable variable = get(name);

		if (variable != null)
			return variable.getValue();
		else {
			assert false;
			return 0.0D;
		}
	}

	/**
	 * Get the type of the variable.
	 * 
	 * @param name
	 *            the name of the variable.
	 * @return the variable's type.
	 */
	public int getType(String name) {
		Variable variable = get(name);

		if (variable != null)
			return variable.getType();
		else {
			assert false;
			return Expression.FLOAT_TYPE;
		}
	}

	/**
	 * Get the variable of the given name.
	 * 
	 * @param name
	 *            the name of the variable.
	 * @return the variable.
	 */
	public Variable get(String name) {
		return (Variable) variables.get(name);
	}

    public int getSize() {
	return variables.size();
    }

    public void dump() {
	Iterator iterator = variables.keySet().iterator();
	while (iterator.hasNext()) {
	    String v = (String)iterator.next();
	    System.out.println("v = " + v);
	}
    }

    /**
     * Compare the two sets of variables and return any differences.
     * A difference is any two variables with the same name but different
     * values or any variables which exist in the first set, but not the second
     * and vice versa.
     * 
     * @param vars1 The first set of variables
     * @param vars2 The second set of variables
     * @return a list of the differences found.     
     */
    public static List getDifferences(Variables vars1, Variables vars2)  {
	Iterator iterator1 = vars1.variables.keySet().iterator();
    	Iterator iterator2 = vars2.variables.keySet().iterator();
	List diffs = new Vector();

	while (iterator1.hasNext()) {
	    String vname1 = (String)iterator1.next();
	    Variable v1 =  vars1.get(vname1);
	    
	    if (vars2.contains(vname1)) {
		if (vars2.getValue(v1.getName()) != v1.getValue()) {
		    diffs.add(v1);
		}
	    } else {
		diffs.add(v1);
	    }			       
	}

	while (iterator2.hasNext()) {
	    String vname2 = (String)iterator2.next();
	    Variable v2 =  vars2.get(vname2);
	    
	    if (!vars1.contains(vname2)) {
		diffs.add(v2);
	    }			       
	}

	return diffs;
    }

}