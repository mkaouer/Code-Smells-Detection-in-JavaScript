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

import java.util.ArrayList;
import java.util.List;

/**
 * An exception which is raised when there is a problem parsing or
 * verifying a quote.
 *
 * @author Andrew Leppard
 */
public class QuoteFormatException extends Throwable {

    // List of quote format problems
    private List messages;

    /**
     * Create a new quote format exception.
     *
     * @param message the reason the quote was invalid
     */
    public QuoteFormatException(String message) {
        messages = new ArrayList();
        messages.add(message);
    }

    /**
     * Create a new quote format exception.
     *
     * @param messages a list of reasons the quote was invalid
     */
    public QuoteFormatException(List messages) {
        this.messages = messages;
    }

    /**
     * Return the reasons the quote was invalid.
     *
     * @return reasons
     */
    public List getMessages() {
        return messages;
    }

    /**
     * Return the first reason the quote was invalid.
     *
     * @return first reason
     */
    public String getMessage() {
        return (String)messages.get(0);
    }
}