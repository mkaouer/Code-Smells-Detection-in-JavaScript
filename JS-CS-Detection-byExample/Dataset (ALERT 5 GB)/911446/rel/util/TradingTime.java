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

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

/**
 * A replacement time for java.util.Calendar.
 *
 * The main principles of this time class are speed (as fast as possible)
 * and size (as small as possible). It produces a much smaller and faster
 * time class than using the Calendar hierarchy.
 *
 * @author Andrew Leppard
 */
public class TradingTime implements Cloneable, Comparable {

    // Hour from 0 to 23.
    private int hour;

    // Minute from 0 to 59
    private int minute;

    // Second from 0 to 62 (leap seconds)
    private int second;

    // Format to display minutes and seconds
    private static NumberFormat format = null;

    /** Number of hours in one day. */
    public final static int HOURS_IN_DAY = 24;

    /** Number of minutes in one hour. */
    public final static int MINUTES_IN_HOUR = 60;

    /** Number of seconds in one minute. */
    public final static int SECONDS_IN_MINUTE = 60;

    /** Number of milliseconds in one second. */
    public final static int MILLISECONDS_IN_SECOND = 1000;

    /**
     * Create a new time from the given hour, minute and second.
     *
     * @param hour   the hour from 0 to 23.
     * @param minute the minute from 0 to 59.
     * @param second the second from 0 to 62 (leap seconds).
     */
    public TradingTime(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    /**
     * Create a new time set to now.
     */
    public TradingTime() {
	GregorianCalendar calendar = new GregorianCalendar();
	calendar.setTime(new Date());
	hour = calendar.get(Calendar.HOUR_OF_DAY);
	minute = calendar.get(Calendar.MINUTE);
	second = calendar.get(Calendar.SECOND);
    }

    /*
     * Create a new time from the given string. We can parse the following
     * time strings:
     * <p>
     * <table>
     * <tr><td><pre>HH:MM</pre></td><td>e.g. "16:02"</td></tr>
     * <tr><td><pre>HH:MM:SS</pre></td><td>e.g. "16:02:00"</td></tr>
     * <tr><td><pre>H:MMAP</pre></td><td>e.g. "4:02pm"</td></tr>
     * <tr><td><pre>HH:MMAP</pre></td><td>e.g. "04:02pm"</td></tr>
     * </table>
     *
     * @param time the time string to parse
     * @exception TradingTimeException if the time couldn't be parsed
     */
    public TradingTime(String time)
        throws TradingTimeFormatException {

        hour = 0;
        minute = 0;
        second = 0;

        time = time.toUpperCase();

        try {
            int colonIndex = time.indexOf(':');
            int i = 0;

            // HH:MM or HH:MM:SS
            if(colonIndex >= 0 && (time.length() == 5 || time.length() == 8)) {

                // HOUR
                hour = Integer.parseInt(time.substring(i, colonIndex));
                i = colonIndex + 1;

                // MINUTE
                colonIndex = time.indexOf(':', i);

                if(colonIndex == -1)
                    minute = Integer.parseInt(time.substring(i));
                else {
                    minute = Integer.parseInt(time.substring(i, colonIndex));

                    // SECOND
                    i = colonIndex + 1;

                    second = Integer.parseInt(time.substring(i));
                }
            }

            // H:MMAP or HH:MMAP (AP = "AM" or "PM")
            else if(colonIndex >= 0 && (time.length() == 6 || time.length() == 7)) {

                // HOUR
                hour = Integer.parseInt(time.substring(i, colonIndex));
                i = colonIndex + 1;

                // MINUTE
                int dayIndex = time.indexOf('A');
                if(dayIndex == -1)
                    dayIndex = time.indexOf('P');
                if(dayIndex == -1)
                    throw new TradingTimeFormatException(time);

                minute = Integer.parseInt(time.substring(i, dayIndex));

                // AM or PM
                if(time.length() > 5) {
                    if(hour == 12)
                        hour = 0;

                    i = dayIndex;
                    if(time.substring(i).equals("AM"));
                    else if(time.substring(i).equals("PM"))
                        hour += 12;
                    else
                        throw new TradingTimeFormatException(time);
                }
            }

            // We don't recognise the format
            else
                throw new TradingTimeFormatException(time);
        }

        // If we can't parse, throw an exception
        catch(NumberFormatException e) {
            throw new TradingTimeFormatException(time);
        }
        catch(StringIndexOutOfBoundsException e) {
            throw new TradingTimeFormatException(time);
        }

        // Simple range checking
        if(hour > 23 || minute > 59 || second > 62)
            throw new TradingTimeFormatException(time);
    }

    /**
     * Tests if this time is before the specified time.
     *
     * @param	time the specified time to compare
     * @return	<code>true</code> if the given time is before this one
     */
    public boolean before(Object time) {
	return (compareTo(time) < 0);
    }

    /**
     * Tests if this time is after the specified time.
     *
     * @param	time the specified time to compare
     * @return	<code>true</code> if the specified time is before this one;
     *		<code>false</code> otherwise.
     */
    public boolean after(Object time) {
	return (compareTo(time) > 0);
    }

    /**
     * Compares this time with the specified object.
     *
     * @param	time the specified time to compare
     * @return	<code>true</code> if the specified time is equal;
     * <code>false</code> otherwise.
     */
    public boolean equals(Object time) {
	return (compareTo(time) == 0);
    }

    /*
     * Create a clone of this time.
     *
     * @return	a clone of this time.
     */
    public Object clone() {
	return (Object)(new TradingTime(getHour(), getMinute(), getSecond()));
    }

    /**
     * Compare the current time to the specified object.
     *
     * @see #compareTo(TradingTime)
     */
    public int compareTo(Object time) {
	return compareTo((TradingTime)time);
    }

    /**
     * Compare the current time to the specified time.
     *
     * @param	time	the time to compare
     * @return the value <code>0</code> if the times are equal;
     * <code>1</code> if this time is after the specified time or
     * <code>-1</code> if this time is before the specified time.
     */
    public int compareTo(TradingTime time) {
	if(getHour() < time.getHour())
	    return -1;
	if(getHour() > time.getHour())
	    return 1;

	if(getMinute() < time.getMinute())
	    return -1;
	if(getMinute() > time.getMinute())
	    return 1;

	if(getSecond() < time.getSecond())
	    return -1;
	if(getSecond() > time.getSecond())
	    return 1;

	return 0;
    }

    /**
     * Return the hour.
     *
     * @return the hour staring from 0.
     */
    public int getHour() {
        return hour;
    }

    /**
     * Return the minute.
     *
     * @return the minute staring from 0.
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Return the second.
     *
     * @return the second staring from 0.
     */
    public int getSecond() {
        return second;
    }

    /**
     * @return the difference between this and another TradingTime in seconds
     * 
     */     
    public int diff(TradingTime time) {
	return (time.getHour() - getHour()) * MINUTES_IN_HOUR * SECONDS_IN_MINUTE + 
	    (time.getMinute() - getMinute()) * SECONDS_IN_MINUTE +
	    (time.getSecond() - getSecond());
    }

    /**
     * Returns a string version of the time.
     *
     * @return string version
     */
    public String toString() {
        return (getNumberFormat().format(getHour()) + ":" +
                getNumberFormat().format(getMinute()) + ":" +
                getNumberFormat().format(getSecond()));
    }

    /**
     * Return the format suitable for displaying the minute and second
     * fields.
     *
     * @return the number format.
     */
    private static NumberFormat getNumberFormat() {
        // Synchronisation cannot cause issues here. So this code
        // isn't synchronised.
        if(format == null) {
            format = NumberFormat.getInstance();
            format.setMinimumIntegerDigits(2);
            format.setMinimumFractionDigits(0);
            format.setMaximumFractionDigits(0);
        }

        return format;
    }
}
