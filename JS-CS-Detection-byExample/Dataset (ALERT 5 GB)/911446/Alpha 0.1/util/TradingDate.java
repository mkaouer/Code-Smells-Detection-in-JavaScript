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

package org.mov.util;

import java.util.*;
import java.util.regex.*;

/**
 * A replacement date for java.util.Date, java.util.Calendar & 
 * java.sql.Date.
 *
 * The main principles of this date class are speed (as fast as possible)
 * and size (as small as possible). It produces a much smaller and faster
 * date class than using the Calendar hierarchy. It also beats java.util.Date
 * by not using deprecated methods.
 */
public class TradingDate implements Cloneable, Comparable {

    /** Date format will be in US format, e.g. <code>mm/dd/yy</code>, <code>mm/dd/yyyy</code> 
        etc. */
    public final static int US = 0;		

    /** Date format will be in britsh format, e.g. <code>dd/mm/yy</code>, <code>dd/mm/yyyy</code> 
        etc. */
    public final static int BRITISH = 1;	

    private int year;
    private int month;
    private int day;

    /**
     * Create a new date from the given year, month and day.
     *
     * @param	year	a four digit year, e.g. 1996
     * @param	month	the month starting from 1
     * @param	day	the day starting from 1
     */
    public TradingDate(int year, int month, int day) {
	this.year = year;
	this.month = month;
	this.day = day;
    }

    /**
     * Create a new date from the given <code>java.util.Calendar</code> object.
     *
     * @param	calendar	calendar date to convert
     */
    public TradingDate(Calendar date) {
	year = date.get(Calendar.YEAR);
	month = date.get(Calendar.MONTH) + 1;
	day = date.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Create a new date from the given <code>java.util.Date</code> object.
     *
     * @param	date	the date to convert
     */
    public TradingDate(Date date) {
	GregorianCalendar gc = new GregorianCalendar();
	gc.setTime(date);

	this.year = gc.get(Calendar.YEAR);
	this.month = gc.get(Calendar.MONTH) + 1;
	this.day = gc.get(Calendar.DATE);
    }

    /**
     * Create a new date from the given string. We can parse the following
     * date string:
     * <p>
     * <table>
     * <tr><td><pre>YYMMDD</pre></td><td>e.g. "010203"</td></tr>
     * <tr><td><pre>YYYYMMDD</pre></td><td>e.g. "20010203"</td></tr>
     * <tr><td><pre>DD/MM/YY</pre></td><td>e.g. "3/2/01"</td></tr>
     * <tr><td><pre>DD/MM/YYYY</pre></td><td>e.g. "3/2/2001"</td></tr>
     * <tr><td><pre>MM/DD/YY</pre></td><td>e.g. "2/3/01"</td></tr>
     * <tr><td><pre>MM/DD/YYYY</pre></td><td>e.g. "2/3/2001"</td></tr>
     * <tr><td><pre>DD-MM-YY</pre></td><td>e.g. "3-2-01"</td></tr>
     * <tr><td><pre>DD-MM-YYYY</pre></td><td>e.g. "3-2-2001"</td></tr>
     * <tr><td><pre>MM-DD-YY</pre></td><td>e.g. "2-3-01"</td></tr>
     * <tr><td><pre>MM-DD-YYYY</pre></td><td>e.g. "2-3-2001"</td></tr>
     * </table>
     *
     * @param	date	the date string to convert from
     * @param	type	either <code>BRITISH</code> or <code>US</code>
     */
    public TradingDate(String date, int type) {

	try {
	    // DD/MM/YY, DD/MM/YYYY, DD-MM-YY, DD-MM-YYYY		
	    if(date.indexOf('/') >= 0 ||
	       date.indexOf('-') >= 0) {
		int i = 0;

		// DAY
		day = 0;
		day += Integer.parseInt(date.substring(i, ++i));
		if(date.charAt(i) != '/' &&
		   date.charAt(i) != '-') {
		    day *= 10;
		    day += Integer.parseInt(date.substring(i, ++i));
		}

		// Skip /, -
		i++;
 
		// MONTH
		month = 0;
		month += Integer.parseInt(date.substring(i, ++i));
		if(date.charAt(i) != '/' &&
		   date.charAt(i) != '-') {
		    month *= 10;
		    month += Integer.parseInt(date.substring(i, ++i));
		}

		// Skip /, -
		i++;

		// YEAR
		year = 0;
		year += Integer.parseInt(date.substring(i, ++i));
		while(i < date.length()) {
		    year *= 10;
		    year += Integer.parseInt(date.substring(i, ++i));
		}

		if(year < 100) {
		    year = twoToFourDigitYear(year);
		}

		// Swap day and month around if expecting US dates
		if(type == US) {
		    int temp;
		    temp = day; day = month; month = temp;
		}	
	    }
    	    
	    // These formats are not localised...

	    // YYMMDD 
	    else if(date.length() == 6) {
		year = Integer.parseInt(date.substring(0, 2));
		month = Integer.parseInt(date.substring(2, 4));
		day = Integer.parseInt(date.substring(4, 6));
		
		year = twoToFourDigitYear(year);
	    }
	    
	    // YYYYMMDD
	    else if(date.length() == 8) {
		year = Integer.parseInt(date.substring(0, 4));
		month = Integer.parseInt(date.substring(4, 6));
		day = Integer.parseInt(date.substring(6, 8));
	    }
	}
	catch(NumberFormatException e) {
	    // If we can't parse, set everything to null
	    year = month = day = 0;
	}
    }

    /**
     * Create a new date set to today.
     */
    public TradingDate() {
	GregorianCalendar gc = new GregorianCalendar();
	gc.setTime(new Date());
	this.year = gc.get(Calendar.YEAR);
	this.month = gc.get(Calendar.MONTH) + 1;
	this.day = gc.get(Calendar.DATE);
    }

    /**
     * Return the year.
     *
     * @return four digit year
     */
    public int getYear() {
	return year;
    }

    /**
     * Return the month.
     *
     * @return the month starting with 1 for January
     */
    public int getMonth() {
	return month;
    }

    /**
     * Return the day.
     *
     * @return the day of the month starting from 1
     */
    public int getDay() {
	return day;
    }

    /**
     * Tests if this date is before the specified date.
     *
     * @param	the specified date to compare
     * @return	<code>true</code> if the given date is before this one
     */
    public boolean before(Object date) {
	if(compareTo(date) > 0)
	    return false;
	else 
	    return true;
    }

    /**
     * Tests if this date is after the specified date.
     *
     * @param	the specified date to compare
     * @return	<code>true</code> if the specified date is before this one; 
     *		<code>false</code> otherwise.
     */
    public boolean after(Object date) {
	if(compareTo(date) > 0)
	    return true;
	else 
	    return false;
    }

    /**
     * Compares this date with the specified object.
     *
     * @param	the specified date to compare
     * @return	<code>true</code> if the specified date is equal; 
     * <code>false</code> otherwise.
     */
    public boolean equals(Object date) {
	if(compareTo(date) == 0)
	    return true;
	else
	    return false;
    }

    /**
     * Create a clone of this date
     *
     * @return	a clone of this date
     */
    public Object clone() {
	return (Object)(new TradingDate(getYear(), getMonth(), 
					getDay()));
    }

    /**
     * Create a fast hash code of this date
     *
     * @return	hash code
     */
    public int hashCode() {
	// theres enough room in an int to store all the data
	return getDay() + getMonth() * 256 + getYear() * 65536;
    }

    /**
     * Create a new date which is the specified number of trading days
     * before this date.
     *
     * @param	days	the number of days to move
     * @return	date which is <code>days</code> before the current one
     */
    public TradingDate previous(int days) {

	Calendar date = this.toCalendar();

	for(int i = 0; i < days; i++) {

	    // Take 1 day or more to skip weekends as necessary
	    do {
		date.add(Calendar.DAY_OF_WEEK, -1);
	    } while(date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
		    date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
	}

	// Create new date
	return new TradingDate(date);
    }

    /**
     * Create a new date which is the specified number of trading days
     * after this date.
     *
     * @param	days	the number of days to move
     * @return	date which is <code>days</code> after the current one
     */
    public TradingDate next(int days) {

	Calendar date = this.toCalendar();

	for(int i = 0; i < days; i++) {

	    // Add 1 day or more to skip weekends as necessary
	    do {
		date.add(Calendar.DAY_OF_WEEK, 1);
	    } while(date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
		    date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
	}

	// Create new date
	return new TradingDate(date);
    }

    /**
     * Compare the current date to the specified object.
     *
     * @see #compareTo(TradingDate)
     */
    public int compareTo(Object date) {
	return compareTo((TradingDate)date);
    }

    /**
     * Compare the current date to the specified date.
     *
     * @param	date	the date to compare
     * @return the value <code>0</code> if the dates are equal;
     * <code>1</code> if this date is after the specified date or
     * <code>-1</code> if this date is before the specified date.
     */
    public int compareTo(TradingDate date) {
	if(getYear() < date.getYear())
	    return -1;
	if(getYear() > date.getYear())
	    return 1;

	if(getMonth() < date.getMonth())
	    return -1;
	if(getMonth() > date.getMonth())
	    return 1;

	if(getDay() < date.getDay())
	    return -1;
	if(getDay() > date.getDay())
	    return 1;

	return 0;
    }

    /**
     * Convert date to string in specified format. Will convert the date
     * to a string matching the given format.
     * The following substitutions will be made:
     * <p>
     * <table>
     * <tr><td><pre>d?</pre></td><td>Replaced with one or two digit day</td></tr>
     * <tr><td><pre>dd</pre></td><td>Replaced with two digit day</td></tr>
     * <tr><td><pre>m?</pre></td><td>Replaced with one or two digit month</td></tr>
     * <tr><td><pre>mm</pre></td><td>Replaced with two digit month</td></tr>
     * <tr><td><pre>MMM</pre></td><td>Replaced with 3 letter month name</td></tr>
     * <tr><td><pre>yy</pre></td><td>Replaced with two digit year</td></tr>
     * <tr><td><pre>yyyy</pre></td><td>Replaced with four digit year</td></tr>
     * </table>
     * <p>
     * E.g.:
     * <pre>text = date.toString("d?-m?-yyyy");</pre>
     *
     * @param	format	the format of the string
     * @return	the text string 
     */
    public String toString(String format) {
	format = replace(format, "d\\?", Integer.toString(getDay())); 
	format = replace(format, "dd", Converter.toFixedString(getDay(), 2)); 
	format = replace(format, "m\\?", Integer.toString(getMonth())); 
	format = replace(format, "mm", 
			 Converter.toFixedString(getMonth(), 2)); 

	format = replace(format, "MMM", monthToText(getMonth()));
	format = replace(format, "yyyy", 
			 Converter.toFixedString(getYear(), 4));	

	if(getYear() > 99) {
	    format = replace(format, "yy", 
			     Integer.toString(getYear()).substring(2));
	}
	else {
	    format = replace(format, "yy", 
			     Integer.toString(getYear()));
	}

	return format;
    }

    // In the given source string replace all occurences of patternText with
    // text.
    private String replace(String source, String patternText, String text) {
	Pattern pattern = Pattern.compile(patternText);
	Matcher matcher = pattern.matcher(source);
	return matcher.replaceAll(text);
    }

    /**
     * Outputs the date in a format SQL can understand - 2001-12-30.
     *
     * @return	SQL friendly date string
     */
    public String toString() {
	
    	return getYear() + "-" + getMonth() + "-" + getDay();
    }

    /**
     * Outputs the date in the format - 12/Dec.
     *
     * @return	short version of the date string
     */
    public String toShortString() {
	return getDay() + "/" + getMonth();
    }

    /**
     * Outputs date in the format - 30 Dec, 2001.
     *
     * @return	long version of the date string
     */
    public String toLongString() {
	return getDay() + " " + monthToText(getMonth()) + ", " +
	    getYear();
    }

    /**
     * Convert a month number to its 3 digit name.
     *
     * @param	month	the month number
     * @return	the 3 digit month string
     */
    public static String monthToText(int month) {
	String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
			   "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	
	month--;

	if(month < months.length && month >= 0)
	    return months[month];
	else
	    return "Dec";
    }

    /**
     * Convert this object to a java.util.Date.
     *
     * @return	<code>java.util.Date</code>
     */
    public Date toDate() {
	return this.toCalendar().getTime();
    }

    /**
     * Convert this object to a java.util.Calendar.
     *
     * @return	<code>java.util.Calendar</code>
     */
    public Calendar toCalendar() {
	// Convert from our month of 1-12 to theirs of 0-11
	return new GregorianCalendar(getYear(), getMonth() - 1, getDay());
    }

    /**
     * Converts a two digit year to four digit year. The year 0 to 30
     * are transformed to 2000 to 2030 respecitvely; the years 31 to 99 to 
     * 1931 and 1999 respectively.
     * 
     * @param	year	a two digit year
     * @return	a four digit year
     */
    public static int twoToFourDigitYear(int year) {
	// Convert year from 2 digit to 4 digit
	if(year > 30)
	    year += 1900;
	else
	    year += 2000;

	return year;
    }
}

