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

package nz.org.venice.ui;

import java.lang.String;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;

import nz.org.venice.util.Locale;

/**
 * Dialog for querying the user for numbers.
 *
 * @author Andrew Leppard
 */
public class NumberDialog {

    private NumberDialog() {
	// Cannot instantiate this class
    }

    /**
     * Opens a new dialog asking the user to enter a number. If the user
     * enters an illegal number, then the number will be rejected
     * and the user will be asked to re-enter the number.
     * The number will be returned or <code>null</code> if the user
     * cancelled the dialog.
     *
     * @param parent       the parent desktop
     * @param title        the title of the dialog
     * @param prompt       the prompt string
     * @param defaultValue the default value to use.
     * @return the value
     */
    public static Double getDouble(JDesktopPane parent,
                                   String       title,
                                   String       prompt,
                                   double       defaultValue) {
        Double value = null;
        String valueText = Double.toString(defaultValue);
        boolean invalidResponse;

        do {
            invalidResponse = false; // assume user does OK
            
            // Prompt for number
            TextDialog dialog = new TextDialog(parent, prompt, title,
                                               valueText);
            
            valueText = dialog.showDialog();

            if(valueText != null) {
                value = parseDouble(parent, valueText);

                if(value == null)
                    invalidResponse = true;
            }
            
	    // Keep going while user hasnt entered a valid number and
	    // is selecting "ok"
        } while(invalidResponse);

        return value;
    }

    /**
     * Given a string, parse it into a double and if there was an error raise a
     * dialog to the user.
     *
     * @param parent the parent desktop
     * @param text   the text to parse
     * @return the value or <code>null</code> on error.
     */
    private static Double parseDouble(JDesktopPane parent, String text) {
        Double value = null;

        // Try and convert the text into a double object
        try {
            value = new Double(text);
        }
        catch(NumberFormatException e) {
            JOptionPane.showInternalMessageDialog(parent, 
                                                  Locale.getString("ERROR_PARSING_NUMBER",
                                                                   text),
                                                  Locale.getString("INVALID_NUMBER_TITLE"),
                                                  JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        return value;
    }
}
