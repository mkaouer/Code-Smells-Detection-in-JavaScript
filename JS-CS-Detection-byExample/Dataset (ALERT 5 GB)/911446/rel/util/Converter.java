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
 * Contains a set of general conversion functions. Previously this was a
 * grab bag of functions that converted anything to anything else.
 * However thinking in more OO terms some of these functions better belonged
 * elsewhere and have been moved. This class is gradually dwindling away and
 * will be removed soon.
 */

/* Unfortunately due to the delay of moving to a new Java, a new converter
   method has been added.
*/

import java.text.DecimalFormat;

public class Converter {

    /**
     * Convert a number to a fixed length string of the given number of
     * digits. E.g. converting 3 to a fixed 4 digit string yields "0003".
     *
     * @param	number	the number to convert into a string
     * @param	digits	the fixed number of digits to output
     * @return	the string
     */
    public static String toFixedString(int number, int digits) {
	String string = Integer.toString(number);
	String zero = new String("0");
	
	// Keep adding zeros at the front until its as big as digits
	while(string.length() < digits) {
	    string = zero.concat(string);
	}
	return string;
    }

    
    /**
     * Return a formatted string according to format.
     * 
     * @param args An array of integers containing data
     * @param lengths An array of ints specifying how long each args should be.
     * @return a formatted date string
     */
    public static String dateFormat(Integer args[], int lengths[], String separator) {
	
	String mesg = "";

	assert args.length == lengths.length;
	DecimalFormat[] formats = new DecimalFormat[args.length];
	
	for (int i = 0; i < args.length; i++) {
	    formats[i] = new DecimalFormat(constructFormat(lengths[i]));
	    mesg += formats[i].format(args[i]);
	    if (i < args.length - 1) {
		mesg += separator;
	    }
	}
	return mesg;
    }

    private static String constructFormat(int length) {
	String rv = "#";
	for (int i = 0; i < length; i++) {
	    rv += "0";
	}	
	rv += ".###";
	return rv;
    }    
}



