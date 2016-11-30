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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.org.venice.util.Locale;
import nz.org.venice.util.Report;

/**
 * Import quotes from files into Venice or export them to files.
 *
 * @author Andrew Leppard
 * @see ImportQuoteModule
 * @see YahooEODQuoteImport
 */
public class FileEODQuoteImport implements IFileEODQuoteImport {

    // Maximum number of quotes imported by importNext()
    private static final int MAX_QUOTE_COUNT = 500;

    private Report report;
    private EODQuoteFilter filter;
    private File file;

    private FileInputStream fileStream;
    private InputStreamReader inputStream;
    private BufferedReader fileReader;
    private String fileName;
    private int lineNumber;
    private boolean isNext;

    /**
     * Create a new object to import quotes. Write any errors or warnings to the
     * given report file and use the given quote filter.
     *
     * @param report the report
     * @param filter the quote filter
     */
    public FileEODQuoteImport(Report report, EODQuoteFilter filter) {
        this.report = report;
        this.filter = filter;
        this.file = null;
        this.fileName = null;

        this.fileStream = null;
        this.inputStream = null;
        this.fileReader = null;
        this.lineNumber = 0;
        this.isNext = false;
    }

    /**
     * Open the given file to import.
     *
     * @param file the file to import
     * @return <code>TRUE</code> if the file was successfully opened;\
     *         <code>FALSE</code> otherwise.
     */

	public boolean open(File file) {
        boolean success = false;

        assert fileStream == null;
        
        try {
            fileStream = new FileInputStream(file);
            inputStream = new InputStreamReader(fileStream);
	    fileReader = new BufferedReader(inputStream);
            fileName = file.getName();

            lineNumber = 1;
            isNext = true;
            success = true;

	} catch (IOException e) {
            report.addError(fileName + ":" +
                            Locale.getString("ERROR") + ": " +
                            Locale.getString("ERROR_READING_FROM_FILE", fileName));
            fileStream = null;
            success = false;
        }

        return success;
    }

    /** 
     * Import the next bundle quotes from the file.
     *
     * @return list of quotes
     */

	public List importNext() {
        assert fileStream != null;

        List quotes = new ArrayList();

	try {
	    String line = fileReader.readLine();
            
	    while(line != null) {
                try {
                    EODQuote quote = filter.toEODQuote(line);
                    quotes.add(quote);
                    verify(quote);
                }
                catch(QuoteFormatException e) {
                    report.addError(fileName + ":" +
                                    Integer.toString(lineNumber) + ":" +
                                    Locale.getString("ERROR") + ": " +
                                    e.getMessage());
                }

                lineNumber++;

                if(quotes.size() >= MAX_QUOTE_COUNT)
                    break;
                
                line = fileReader.readLine();
	    }
            
            if(line == null)
                isNext = false;

	} catch (IOException e) {
            report.addError(fileName + ":" +
                            Locale.getString("ERROR") + ": " +
                            Locale.getString("ERROR_READING_FROM_FILE", fileName));
        }

        return quotes;
    }

    /**
     * Return whether there are any more quotes in the file.
     *
     * @return <code>TRUE</code> if there are more quotes to import;
     *         <code>FALSE</code> otherwise.
     */

	public boolean isNext() {
        assert fileStream != null;

        return isNext;
    }

    /**
     * Close the file being imported.
     */

	public void close() {
        assert fileStream != null;

        try {
	    fileReader.close();
	} catch (IOException e) {
            report.addError(fileName + ":" +
                            Locale.getString("ERROR") + ": " +
                            Locale.getString("ERROR_READING_FROM_FILE", fileName));
        }        
        
        fileStream = null;
    }

    /**
     * Verify the quote is valid. Log any problems to the report and try to clean
     * it up the best we can.
     *
     * @param quote the quote
     */
    private void verify(EODQuote quote) {
        try {
            quote.verify();
        }
        catch(QuoteFormatException e) {
            List messages = e.getMessages();

            for(Iterator iterator = messages.iterator(); iterator.hasNext();) {
                String message = (String)iterator.next();

                report.addWarning(fileName + ":" + 
                                  Integer.toString(lineNumber) + ":" +
                                  Locale.getString("WARNING") + ": " +
                                  message);
            }
        }
    }
}
