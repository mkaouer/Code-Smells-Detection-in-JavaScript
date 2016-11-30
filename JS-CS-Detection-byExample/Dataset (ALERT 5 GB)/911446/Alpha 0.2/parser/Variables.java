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

public class Variables {

    public HashMap variables;

    public Variables() {
        variables = new HashMap();
    }

    public boolean contains(String name) {
        return variables.containsKey(name);
    }

    public void add(String name, int type) {
        add(name, type, 0.0F);
    }

    public void add(String name, int type, float value) {
        if(!variables.containsKey(name)) {
            Variable variable = new Variable(name, type, value);
            variables.put(name, variable);
        }
        else
            assert false;
    }

    public void add(String name, int type, int value) {
        if(!variables.containsKey(name)) {
            Variable variable = new Variable(name, type, value);
            variables.put(name, variable);
        }
        else
            assert false;
    }

    public void setValue(String name, float value) {
        Variable variable = get(name);

        if(variable != null)
            variable.setValue(value);
        else
            assert false;
    }

    public void setValue(String name, int value) {
        Variable variable = get(name);

        if(variable != null)
            variable.setValue(value);
        else
            assert false;
    }

    public float getValue(String name) {
        Variable variable = get(name);

        if(variable != null)
            return variable.getValue();
        else {
            assert false;
            return 0.0F;
        }
    }

    public int getType(String name) {
        Variable variable = get(name);

        if(variable != null)
            return variable.getType();
        else {
            assert false;
            return Expression.FLOAT_TYPE;
        }
    }

    public Variable get(String name) {
        return (Variable)variables.get(name);
    }
}
