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
 * Manages a report or log. The class keeps the report in memory and
 * keeps track of the number of warnings and errors that have been
 * logged.
 *
 * @author Andrew Leppard
 */
public class Report {

    /** The line number where the report is truncated. */
    public static int TRUNCATE_LINE = 1000;

    // Internal report fields
    private StringBuffer buffer;
    private int warnings;
    private int errors;
    private int lines;

    /**
     * Create a new empty report.
     */
    public Report() {
        buffer = new StringBuffer();
        warnings = 0;
        errors = 0;
        lines = 0;
    }
    
    /**
     * Add a message to the report.
     *
     * @param text text of message
     */
    public void addMessage(String text) {
        if(lines == TRUNCATE_LINE) {
            buffer.append("\n");
            buffer.append(Locale.getString("REPORT_TRUNCATED", TRUNCATE_LINE));
        }

        else if(lines < TRUNCATE_LINE) {
            buffer.append(text);
            buffer.append("\n");
        }
        
        lines++;
    }

    /**
     * Add a warning message to the report.
     *
     * @param text text of message
     */
    public void addWarning(String text) {
        addMessage(text);
        warnings++;
    }

    /**
     * Add an error message to the report.
     *
     * @param text text of message
     */
    public void addError(String text) {
        addMessage(text);
        errors++;
    }

    /**
     * Get the report text.
     *
     * @return text the text of the report
     */
    public String getText() {
        return buffer.toString();
    }

    /**
     * Get the number of warnings logged.
     *
     * @return warning count
     */
    public int getWarningCount() {
        return warnings;
    }

    /**
     * Get the number of errors logged.
     *
     * @return error count
     */
    public int getErrorCount() {
        return errors;
    }
}