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

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * A representation of a stock symbol, e.g. <code>CBA</code> or <code>WBC</code>.
 * Previously the stock symbol was stored as a String but this created the
 * question of whether it should be stored in lower or upper case.
 * Unfortunately, due to development drift, even though the stock symbols were
 * always displayed in upper case, they were stored in lower case. Often
 * a "missing" conversion meant the symbol was displayed in lower case or
 * worse it was stored in upper case which could cause the stock symbol to
 * become "invisible".
 * <p>By creating a single class we reduce the amount of <code>toUpperCase()</code> and
 * <code>toLowerCase()</code> calls required, and have a single place to store symbol 
 * parsing code. Also this implementation uses a single <code>int</code> to store the 
 * symbol, which is designed to reduce memory and also increase speed of <code>hashCode()</code>
 * calls. These calls are used extensively in the {@link QuoteCache}.
 */
public class Symbol implements Cloneable, Comparable {
    
    // Pack down symbol string, e.g. "ABCDE" into single int. 
    // Saves space!
    private int symbol;
    
    /** The minimum valid length for a symbol */
    public final static int MINIMUM_SYMBOL_LENGTH = 3;

    /** The maximum valid length for a symbol. This cannot be more than 6 */
    public final static int MAXIMUM_SYMBOL_LENGTH = 6;
    
    // A-Z contains 26 letters which fit in 5 bits
    private final static int BITS_PER_CHARACTER = 5;

    /**
     * Create a new symbol from the given string.
     *
     * @param string a string containing a single symbol
     * @exception SymbolFormatException if the string doesn't contain a valid quote
     */
    public Symbol(String string) 
        throws SymbolFormatException {
        set(string);
    }
   
    /**
     * Return the symbol string.
     *
     * @return an upper case symbol string
     */
    public String get() {
        String string = new String();
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

    // Set the symbol string.
    private void set(String string) 
        throws SymbolFormatException {
	
        string = string.toUpperCase();

        if(string.length() > MAXIMUM_SYMBOL_LENGTH)
            throw new SymbolFormatException("Symbol '" + string + "' is too long.");
        else if(string.length() < MINIMUM_SYMBOL_LENGTH)
            throw new SymbolFormatException("Symbol '" + string + "' is too short.");

        symbol = 0;
        
        for(int i = 0; i < string.length(); i++) {
            
            // 1..26 (A..Z)
            int characterNumber = ((int)string.charAt(i) + 1 - 
                                   (int)'A');
            
            if(characterNumber < 1 || characterNumber > BITS_PER_CHARACTER * 8) 
                throw new SymbolFormatException("Symbol '" + string + "' contains non-" +
                                                "alphabetical characters.");

            symbol += characterNumber << (BITS_PER_CHARACTER * i);
        }
    }

    /**
     * Return the length of the symbol. This is guaranteed to be within bounds.
     *
     * @return the symbol length.
     */
    public int length() {
        String string = get();
        int length = string.length();

        assert length >= MINIMUM_SYMBOL_LENGTH && length <= MAXIMUM_SYMBOL_LENGTH;

        return length;
    }

    /**
     * Return the character at the given offset. See the java.lang.String class
     * for more details.
     *
     * @param offset the character offset
     * @return the character at the given offset
     */
    public char charAt(int offset) {
        String string = get();

        return string.charAt(offset);
    }

    /**
     * Convert a string containing a list of symbols separated by spaces
     * or commas into a sorted set of symbols with duplicates removed.
     * This function Checks that all symbols appear in the quote source.
     *
     * e.g "CBA WBC TLS" -> [CBA, TLS, WBC].
     *
     * @param	string	a comma or space separated list of symbols
     * @return	a sorted set of symbols
     * @exception SymbolFormatException if the string doesn't contain a 
     *            list of valid quotes
     */
    public static SortedSet toSortedSet(String string)
        throws SymbolFormatException {

        // Split the string around spaces or commas
        Pattern pattern = Pattern.compile("[, ]+");
        String[] symbols = pattern.split(string);
	TreeSet sortedSet = new TreeSet();

        for(int i = 0; i < symbols.length; i++) {
            if(symbols[0].length() > 0) {
                Symbol symbol = new Symbol(symbols[i]);
                
                if(!QuoteSourceManager.getSource().symbolExists(symbol))
                    throw new SymbolFormatException("No quotes available for symbol '" +
                                                    symbol + "'.");
                sortedSet.add(symbol);
            }
        }	

	return sortedSet;
    }

    /**
     * Convert a string containing a single symbol into a quote symbol.
     * This differs from {@link Symbol#Symbol} as it performs better
     * error checking and checks that the symbol exists.
     * 
     * @param string a string containing a single symbol
     * @exception SymbolFormatException if the string doesn't contain a valid quote
     */
    public static Symbol toSymbol(String string)
        throws SymbolFormatException {
        
        SortedSet symbols = toSortedSet(string);
        Object[] symbolsArray = symbols.toArray();

        if(symbolsArray.length > 1)
            throw new SymbolFormatException("Expecting only a single symbol.");
        else if(symbolsArray.length == 0)
            throw new SymbolFormatException("Missing symbol.");
        else
            return (Symbol)symbolsArray[0];
    }

    /**
     * Create a clone of this symbol.
     *
     * @return clone of this symbol
     */
    public Object clone() {

        // Since the symbol class is immutable and because we may
        // potentially have thousands of them, a clone can actually
        // be the same class.
        return this;
    }
    
    /**
     * Compare this symbol to the given symbol.
     *
     * @param object symbol to compare
     * @return the value <code>0</code> if the symbols are equal;
     * <code>1</code> if this symbol is after the specified symbol or
     * <code>-1</code> if this symbol is before the specified symbol.
     */
    public int compareTo(Object object) {
	return toString().compareTo(object.toString());
    }
    
    /**
     * Compare this symbol to the given symbol.
     *
     * @param object the symbol to compare
     * @return <code>true</code> if they are equal
     */
    public boolean equals(Object object) {
	return object.hashCode() == hashCode();
    }
    
    /**
     * Calculate the hash code for this symbol.
     *
     * @return the hash code
     */
    public int hashCode() {
        return symbol;
    }
    
    /**
     * Convert the symbol to a string. This is identical to the
     * {@link Symbol#get} method.
     *
     * @return the symbol string
     */
    public String toString() {
        return get();
    }
}

