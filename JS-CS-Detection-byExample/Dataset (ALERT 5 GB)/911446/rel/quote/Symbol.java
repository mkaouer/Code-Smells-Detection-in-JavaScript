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

package nz.org.venice.quote;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import nz.org.venice.util.Locale;

/**
 * An immutable representation of a stock symbol, e.g. <code>CBA</code> or <code>WBC</code>.
 * Previously the stock symbol was stored as a String but this created the
 * question of whether it should be stored in lower or upper case.
 * Unfortunately, due to development drift, even though the stock symbols were
 * always displayed in upper case, they were stored in lower case. Often
 * a "missing" conversion meant the symbol was displayed in lower case or
 * worse it was stored in upper case which could cause the stock symbol to
 * become "invisible".
 *
 * <p>By creating a single class we reduce the amount of <code>toUpperCase()</code> and
 * <code>toLowerCase()</code> calls required, and have a single place to store symbol
 * parsing code.
 *
 * <p>To reduce memory, symbols are stored canonically. That is there is a single
 * object for each symbol. For example the string symbol "CBA" would be represented by
 * a single class, no matter where it was used. So instead of instantiating a new
 * symbol class, you find the canonical object using the {@link #find} method.
 *
 * @author Andrew Leppard
 */
public class Symbol implements Cloneable, Comparable {

    private String symbol;

    /** The minimum valid length for a symbol */
    public final static int MINIMUM_SYMBOL_LENGTH = 1;

    /** The maximum valid length for a symbol. */
    public final static int MAXIMUM_SYMBOL_LENGTH = 12;

    // Hashmap of linking strings to their canonical symbol instance
    private static HashMap registry = new HashMap();

    /**
     * Create a new symbol from the given string.
     *
     * @param string a string containing a single symbol
     * @exception SymbolFormatException if the string doesn't contain a valid quote
     */
    private Symbol(String string)
        throws SymbolFormatException {

        if(string.length() > MAXIMUM_SYMBOL_LENGTH)
            throw new SymbolFormatException(Locale.getString("SYMBOL_TOO_LONG",
							     string));
        else if(string.length() < MINIMUM_SYMBOL_LENGTH)
            throw new SymbolFormatException(Locale.getString("SYMBOL_TOO_SHORT",
							     string));

        // A symbol can only contain numbers, letters and certain other characters.
        for(int i = 0; i < string.length(); i++) {
            char letter = string.charAt(i);

            if(!Character.isLetterOrDigit(letter) &&
               letter != '.' && letter != '^' && letter != '-' && letter != '&' && letter != ':')
                throw new SymbolFormatException(Locale.getString("INVALID_SYMBOL",
								 string));
        }

        symbol = string;
    }

    /**
     * Return the canonical symbol instance of the given symbol string.
     *
     * @param string a string containing a single symbol
     * @exception SymbolFormatException if the string doesn't contain a valid quote
     */
    public static Symbol find(String string)
        throws SymbolFormatException {

        String upperCaseString = string.toUpperCase();
        Symbol symbol = (Symbol)registry.get(upperCaseString);

        if(symbol == null) {
            // To prevent two simultaneous threads adding the same symbol twice,
            // we run the add code in a synchronized block. Since
            // sychronisation is slow, it is only invoked if we encounter a
            // new symbol.
            synchronized(registry) {

                // We need to look this up again incase it was just added
                symbol = (Symbol)registry.get(upperCaseString);

                if(symbol == null) {
                    symbol = new Symbol(upperCaseString);
                    registry.put(upperCaseString, symbol);
                }
            }
        }

        return symbol;
    }

    /**
     * Return the symbol string.
     *
     * @return an upper case symbol string
     */
    public String get() {
        return symbol;
    }

    /**
     * Return the length of the symbol. This is guaranteed to be within bounds.
     *
     * @return the symbol length.
     */
    public int length() {
        return symbol.length();
    }

    /**
     * Return the character at the given offset. See the java.lang.String class
     * for more details.
     *
     * @param offset the character offset
     * @return the character at the given offset
     */
    public char charAt(int offset) {
        return symbol.charAt(offset);
    }

    /**
     * Convert a string containing a list of symbols separated by spaces
     * or commas into a sorted set of symbols with duplicates removed.
     * This function Checks that all symbols appear in the quote source.
     *
     * e.g "CBA WBC TLS" -> [CBA, TLS, WBC].
     *
     * @param	string	a comma or space separated list of symbols
     * @param   checkExists set this flag to <code>TRUE</code> to make sure
     *                      that the symbols are in the current quote source
     * @return	a sorted set of symbols
     * @exception SymbolFormatException if the string doesn't contain a
     *            list of valid quotes
     */
    public static SortedSet toSortedSet(String string, boolean checkExists)
        throws SymbolFormatException {

        // Split the string around spaces or commas
        Pattern pattern = Pattern.compile("[, ]+");
        String[] symbols = pattern.split(string);
	TreeSet sortedSet = new TreeSet();

        for(int i = 0; i < symbols.length; i++) {
            if(symbols[i].length() > 0) {
                Symbol symbol = find(symbols[i]);

                if(checkExists && !QuoteSourceManager.getSource().symbolExists(symbol))
                    throw new SymbolFormatException(Locale.getString("NO_QUOTES_SYMBOL",
                                                                     symbols[i]));
                sortedSet.add(symbol);
            }
        }

	return sortedSet;
    }

    /**
     * Convert a string containing a single symbol into a quote symbol.
     * This differs from {@link Symbol#find} as it performs better
     * error checking and checks that the symbol exists.
     *
     * @param string a string containing a single symbol
     * @exception SymbolFormatException if the string doesn't contain a valid quote
     */
    public static Symbol toSymbol(String string)
        throws SymbolFormatException {

        SortedSet symbols = toSortedSet(string, true);
        Object[] symbolsArray = symbols.toArray();

        if(symbolsArray.length > 1)
            throw new SymbolFormatException(Locale.getString("EXPECTING_SINGLE_SYMBOL"));
        else if(symbolsArray.length == 0)
            throw new SymbolFormatException(Locale.getString("MISSING_SYMBOL"));
        else
            return (Symbol)symbolsArray[0];
    }

    /**
     * Create a clone of this symbol.
     *
     * @return clone of this symbol
     */
    public Object clone() {
        // Since Symbols are canonical the clone is this class.
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
        // We can compare directly since the objects are canonical
	return this == object;
    }

    /**
     * Calculate the hash code for this symbol.
     *
     * @return the hash code
     */
    public int hashCode() {
        return symbol.hashCode();
    }

    /**
     * Convert the symbol to a string. This is identical to the
     * {@link Symbol#get} method.
     *
     * @return the symbol string
     */
    public String toString() {
        return symbol;
    }
}

