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

package nz.org.venice.util;

/**
 * A selection of functions to perform find/replace on strings.
 *
 * @author Andrew Leppard
 */
public class Find {

    /**
     * Replace the first occurence of the pattern text with replacement text.
     * This function treats all arguments as simple strings.
     *
     * @param source The source string containing the text to replace.
     * @param pattern The pattern to find and replace.
     * @param replacement The text to use to replace the pattern.
     * @return The version of the source string after the find/replace.
     */
    public static String replace(String source, String pattern, String replacement) {
        // This function used to use Java's regex code, but this was unsatisfactory
        // for a simple find replace call because it did not treat the replacement
        // text as simple text, instead it looked for special symbols to do grouping,
        // referencing etc. This caused exceptions to be thrown when we wanted
        // to replace '%' with $.
        int location = source.indexOf(pattern);
        if(location != -1) {
            return(source.substring(0, location) +
                   replacement +
                   source.substring(location + pattern.length()));
        }

        return source;
    }

    /**
     * Replace all occurences of the pattern text with replacement text.
     * This function treats all arguments as simple strings.
     *
     * @param source The source string containing the text to replace.
     * @param pattern The pattern to find and replace.
     * @param replacement The text to use to replace the pattern.
     * @return The version of the source string after the find/replace.
     */
    public static String replaceAll(String source, String pattern, String replacement) {
        // This function used to use Java's regex code, but this was unsatisfactory
        // for a simple find replace call because it did not treat the replacement
        // text as simple text, instead it looked for special symbols to do grouping,
        // referencing etc. This caused exceptions to be thrown when we wanted
        // to replace '%' with $.
        int location = source.indexOf(pattern);

        while(location != -1) {
            source = (source.substring(0, location) +
                      replacement +
                      source.substring(location + pattern.length()));
            location = source.indexOf(pattern, location + replacement.length());
        }

        return source;
    }
}