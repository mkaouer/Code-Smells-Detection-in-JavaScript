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

package org.mov.ui;

import java.lang.String;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;

import org.mov.quote.QuoteCache;
import org.mov.quote.QuoteSourceManager;
import org.mov.quote.WeekendDateException;
import org.mov.util.TradingDate;

/**
 * Dialog for querying the user for a date.
 *
 * @see TradingDate
 */
public class TradingDateDialog {

    private TradingDateDialog() {
	// Cannot instantiate this class
    }

    /**
     * Opens a new dialog asking the user to enter a date. If the user
     * enters an illegal date, a date that falls on a weekend or any
     * date that we don't have data for, then the date will be rejected
     * and the user will be asked to re-enter the date.
     * The date will be returned or <code>null</code> if the user
     * cancelled the dialog.
     *
     * @param parent the parent desktop
     * @param title the title of the dialog
     * @param prompt the prompt string
     * @return the date
     */
    public static TradingDate getDate(JDesktopPane parent,
                                      String title,
                                      String prompt) {
        TradingDate date = null;
        TradingDate lastDate = QuoteSourceManager.getSource().getLastDate();

        // If there is an error loading the last date then return immediately
        if(lastDate == null)
            return null;
        String dateText = lastDate.toString("dd/mm/yyyy");
        boolean invalidResponse;

        do {
            invalidResponse = false; // assume user does OK
            
            // Prompt for date
            TextDialog dialog = new TextDialog(parent, prompt, title,
                                               dateText);
            
            dateText = dialog.showDialog();

            if(dateText != null) {
                date = parseDate(parent, dateText);

                if(date == null)
                    invalidResponse = true;
            }
            
	    // Keep going while user hasnt entered a valid date and
	    // is selecting "ok"
        } while(invalidResponse);

        return date;
    }

    // Given a string, parse it into a date and if there was an error raise a
    // dialog to the user.
    private static TradingDate parseDate(JDesktopPane parent, String dateText) {
        TradingDate date = null;
        int dateOffset;

        // Try and convert the date text into a date object
        date = new TradingDate(dateText, TradingDate.BRITISH);
        
        // If the date didn't parse it sets all the fields to 0
        if(date.getYear() == 0) {
            JOptionPane.showInternalMessageDialog(parent, 
                                                  "Can't parse date '" + dateText + "'",
                                                  "Invalid Trading Date",
                                                  JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        // The date parsed. Make sure it isn't on a weekend
        try {
            dateOffset = QuoteCache.getInstance().dateToOffset(date);
        }
        catch(WeekendDateException e) {
            JOptionPane.showInternalMessageDialog(parent, 
                                                  "Date falls on a weekend '" + dateText + "'",
                                                  "Invalid Trading Date",
                                                  JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // Finally make sure we have data for this date
        if(!QuoteSourceManager.getSource().containsDate(date)) {
            JOptionPane.showInternalMessageDialog(parent,
                                                  "No data available for date '" + dateText + "'",
                                                  "Invalid Trading Date",
                                                  JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return date;
    }
}
