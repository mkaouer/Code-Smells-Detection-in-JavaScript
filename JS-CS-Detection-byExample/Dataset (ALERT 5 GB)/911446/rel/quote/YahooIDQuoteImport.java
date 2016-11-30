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
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.MalformedURLException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.net.URL;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.util.Find;
import nz.org.venice.util.Locale;

/**
 * Import intra-day quotes from Yahoo into Venice.
 *
 * @author Andrew Leppard
 */
public class YahooIDQuoteImport {

    // The following symbols will be replaced by the quote, date range we are after:
    private final static String SYMBOLS = "_SYM_";

    // Each Yahoo site uses the same URL formatting. So we define it once here.
    private final static String YAHOO_PATTERN = ("?s=" + SYMBOLS + "&f=sl1d1t1c1ohgv&e=.csv");

    private final static String YAHOO_URL_PATTERN =
        ("http://finance.yahoo.com/d/quotes.csv" + YAHOO_PATTERN);

    // This class is not instantiated.
    private YahooIDQuoteImport() {
        assert false;
    }

    /**
     * Retrieve intra-day quotes from Yahoo.
     *
     * @param symbols the symbols to import.
     * @param suffix optional suffix to append (e.g. ".AX"). This suffix tells
     *               Yahoo which exchange the symbol belongs to.
     * @exception ImportExportException if there was an error retrieving the quotes
     */
    public static List importSymbols(List symbols, String suffix)
         throws ImportExportException {

        List quotes = new ArrayList();
        String URLString = constructURL(symbols, suffix);
        IDQuoteFilter filter = new YahooIDQuoteFilter();

        PreferencesManager.ProxyPreferences proxyPreferences =
            PreferencesManager.getProxySettings();

        try {
	    URL url = new URL(URLString);

            InputStreamReader input = new InputStreamReader(url.openStream());
            BufferedReader bufferedInput = new BufferedReader(input);
            String line;

            do {
                line = bufferedInput.readLine();

                if(line != null) {
                    try {
                        IDQuote quote = filter.toIDQuote(line);
                        quote.verify();
                        quotes.add(quote);
                    }
                    catch(QuoteFormatException e) {
                        // Ignore
                    }
                }
            }
            while(line != null);

            bufferedInput.close();
        }

	catch(BindException e) {
            throw new ImportExportException(Locale.getString("UNABLE_TO_CONNECT_ERROR",
                                                             e.getMessage()));
	}

	catch(ConnectException e) {
	    throw new ImportExportException(Locale.getString("UNABLE_TO_CONNECT_ERROR",
                                                             e.getMessage()));
	}

	catch(UnknownHostException e) {
            throw new ImportExportException(Locale.getString("UNKNOWN_HOST_ERROR",
                                                             e.getMessage()));
	}

	catch(NoRouteToHostException e) {
            throw new ImportExportException(Locale.getString("DESTINATION_UNREACHABLE_ERROR",
                                                             e.getMessage()));
	}

	catch(MalformedURLException e) {
            throw new ImportExportException(Locale.getString("INVALID_PROXY_ERROR",
                                                             proxyPreferences.host,
                                                             proxyPreferences.port));
        }

        catch(FileNotFoundException e) {
            throw new ImportExportException(Locale.getString("ERROR_DOWNLOADING_QUOTES"));
        }

        catch(IOException e) {
            throw new ImportExportException(Locale.getString("ERROR_DOWNLOADING_QUOTES"));
        }

        return quotes;
    }

    /**
     * Construct the URL necessary to retrieve all the quotes for the given symbol between
     * the given dates from Yahoo.
     *
     * @param symbols the symbos to import.
     * @param suffix optional suffix to append (e.g. ".AX"). This suffix tells
     *               Yahoo which exchange the symbol belongs to.
     * @return URL string
     */
    private static String constructURL(List symbols, String suffix) {
        String URLString = YAHOO_URL_PATTERN;
        String symbolStringList = "";

	if (suffix == null) {
	    suffix = "";
	}

        // Construct a plus separated list of symbols, e.g. IBM+MSFT+...
        for(Iterator iterator = symbols.iterator(); iterator.hasNext();) {
            Symbol symbol = (Symbol)iterator.next();
            String symbolString = symbol.toString();

            // Append symbol with optional suffix. If the user has not provided a full-stop, provide
            // them with one.
            if(suffix.length() > 0) {
                if(!suffix.startsWith("."))
                    symbolString += ".";
                symbolString += suffix;
            }

            symbolStringList += symbolString;

            if(iterator.hasNext())
                symbolStringList += "+";
        }

        URLString = Find.replace(URLString, SYMBOLS, symbolStringList);
        return URLString;
    }
}
