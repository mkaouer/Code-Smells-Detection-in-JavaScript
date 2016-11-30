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

package org.mov.quote;

import java.util.*;

public class Symbol implements Comparable {
    
    // Pack down symbol string, e.g. "ABCDE" into single int
    private int symbol;
    
    // Enough room to store "ABCDE". This value can be 1->6.
    public final static int MAXIMUM_SYMBOL_LENGTH = 5;
    
    // A-Z contains 26 letters which fit in 5 bits
    private final static int BITS_PER_CHARACTER = 5;

    public Symbol() {
	symbol = 0;
    }
    
    public Symbol(String symbolString) {
        setSymbol(symbolString);
    }
    
    public String getSymbol() {
        if(symbol == 0)
            return null;
	
        String symbolString = new String();
        int i = 0;
        boolean isMoreCharacters = true;
	char[] characters = new char[MAXIMUM_SYMBOL_LENGTH];
	
        while(isMoreCharacters) {
	    
            // 1..26 (A..Z)
            int characterNumber = symbol >> (BITS_PER_CHARACTER * i);
            characterNumber &= ((2 << BITS_PER_CHARACTER - 1) - 1);
	    
            if(characterNumber == 0)
                isMoreCharacters = false;
            else 
		characters[i++] = (char)(characterNumber - 1 + (int)'A');
        }
	
	return String.copyValueOf(characters, 0, i);
    }
    
    public void setSymbol(String symbolString) {
	
        symbolString = symbolString.toUpperCase();
	
        symbol = 0;
	
        assert symbolString.length() <= MAXIMUM_SYMBOL_LENGTH;

        for(int i = 0; i < symbolString.length(); i++) {

	    // 1..26 (A..Z)
	    int characterNumber = ((int)symbolString.charAt(i) + 1 - 
				   (int)'A');
	    
	    assert characterNumber <= BITS_PER_CHARACTER * 8;
	    symbol += characterNumber << (BITS_PER_CHARACTER * i);
	}
    }
    
    public int compareTo(Object object) {
	return toString().compareTo(object.toString());
    }
    
    public boolean equals(Object object) {
	return object.hashCode() == hashCode();
    }
    
    public int hashCode() {
        return symbol;
    }
    
    public String toString() {
        return getSymbol();
    }
}

