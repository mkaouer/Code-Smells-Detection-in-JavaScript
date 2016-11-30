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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

/**
 * Export quotes from Venice to a file.
 *
 * @author Andrew Leppard
 * @see ExportQuoteModule
 */
public class FileEODQuoteExport {

    // This class is not instantiated.
    private FileEODQuoteExport() {
        assert false;
    } 

    /**
     * Export a single day of quotes from Venice into a file
     *
     * @param filter format of quote file
     * @param file quote file to export
     * @param quotes list of quotes to export
     * @exception IOException if there was an error writing the file
     */
    public static void exportFile(EODQuoteFilter filter, File file, List quotes)
        throws IOException {

        // Don't bother creating empty files
        if(quotes.size() > 0) {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);
            
            // Iterate through stocks printing them to file
            for(Iterator iterator = quotes.iterator(); iterator.hasNext();) {
                EODQuote quote = (EODQuote)iterator.next();
                printWriter.println(filter.toString(quote));
            }
            
            printWriter.close();
        }
    }
}