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

import java.util.HashMap;

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
}