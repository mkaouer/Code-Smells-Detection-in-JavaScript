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
 * An exception which is raised when there is a problem parsing a
 * date.
 *
 * @author Andrew Leppard
 */
public class TradingDateFormatException extends Throwable {

    private String date;

    /** 
     * Create a new trading date format exception.
     *
     * @param date the date being parsed.
     */
    public TradingDateFormatException(String date) {
        super(Locale.getString("ERROR_PARSING_DATE", date));
        this.date = date;
    }

    /**
     * Return the date string being parsed.
     *
     * @return the date being parsed.
     */
    public String getDate() {
        return date;
    }
}
