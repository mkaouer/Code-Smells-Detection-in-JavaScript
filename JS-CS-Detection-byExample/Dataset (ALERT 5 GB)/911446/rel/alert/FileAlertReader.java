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

package nz.org.venice.alert;


import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.lang.NumberFormatException;

import nz.org.venice.quote.Symbol;
import nz.org.venice.quote.SymbolFormatException;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.TradingDateFormatException;


import nz.org.venice.prefs.PreferencesManager;
import java.util.prefs.Preferences;

/**
 * Retrieve alerts stored in the local filesystem.
 * 
 * @author Mark Hummel
 * @see AlertReader
 */

public class FileAlertReader implements AlertReader {

    public FileAlertReader() {
	
    }
    
    /**
     * Get all alerts currently stored for the symbol.
     * @return A list of all the alerts for this symbol.
     */    
    public List getAlerts() throws AlertException { 
	Preferences prefs = PreferencesManager.getUserNode("/table/alerts");
	
	ArrayList alerts = new ArrayList();
	
	try {
	    String[] keys = prefs.keys();
	    for (int i = 0; i < keys.length; i++) {				
		String enabledString = prefs.get(keys[i],"disabled");
		Alert alert = strToAlert(keys[i], enabledString);
		
		if (alert == null) {
		} else {
		    alerts.add(alert);
		}
	    }
	} catch (java.util.prefs.BackingStoreException e) {
	    
	} finally {
	    return alerts;
	}
    }
    
    /**
     * Get all alerts currently stored for the symbol.
     * @param symbol The symbol to filter the alerts on.
     * @return A list of all the alerts for this symbol.
     */    
    public List getAlertsBySymbol(Symbol symbol) {

	Preferences prefs = PreferencesManager.getUserNode("/table/alerts");
	
	ArrayList alerts = new ArrayList();
	
	try {
	    String[] keys = prefs.keys();
	    for (int i = 0; i < keys.length; i++) {				
		String enabledString = prefs.get(keys[i],"disabled");
		
		int symbolIndex = keys[i].indexOf(",");
		String symbolString = keys[i].substring(1, symbolIndex-1);
		
		if (!symbolString.equals(symbol.toString())) {
		    continue;
		}

		Alert alert = strToAlert(keys[i], enabledString);
		if (alert == null) {
		} else {
		    alerts.add(alert);
		}
	    }
	} catch (java.util.prefs.BackingStoreException e) {

	} finally {	    
	    return alerts;
	}

    }

    /**
     * Get all alerts currently stored for the symbol.
     * @param symbols The symbol list to filter the alerts on.
     * @return A list of all the alerts for this symbol.
     */    
    public List getAlertsBySymbolList(List symbols) {

	Preferences prefs = PreferencesManager.getUserNode("/table/alerts");
	ArrayList alerts = new ArrayList();
	
	List symbolStrings = new ArrayList();
	Iterator iterator = symbols.iterator();
	while (iterator.hasNext()) {
	    Symbol symbol = (Symbol)iterator.next();
	    String symbolString = symbol.toString();
	    symbolStrings.add(symbolString);
	}
	
	try {
	    String[] keys = prefs.keys();
	    for (int i = 0; i < keys.length; i++) {				
		String enabledString = prefs.get(keys[i],"disabled");
		
		int symbolIndex = keys[i].indexOf(",");
		String symbolString = keys[i].substring(0, symbolIndex);

		if (!symbolStrings.contains(symbolString)) {
		    continue;
		}

		Alert alert = strToAlert(keys[i], enabledString);
		if (alert == null) {

		} else {
		    alerts.add(alert);
		}
	    }
	} catch (java.util.prefs.BackingStoreException e) {

	} finally {
	    return alerts;
	}

    }

    
    private Alert strToAlert(String line, String enabledString) 
	throws AlertException {
	StringTokenizer tokenizer = new StringTokenizer(line, ",");
	
	int alertType = -1;
	Alert newAlert = null;

	try {
	    boolean enabled = (enabledString.equals("enabled")) ? true : false;

	    String symbolString = tokenizer.nextToken();
	    String startDateString = tokenizer.nextToken();
	    String endDateString = tokenizer.nextToken();
	    String target = unescapeString(tokenizer.nextToken());
	    String dateSetString = tokenizer.nextToken();

	    TradingDate dateSet = new TradingDate(dateSetString, 
						  TradingDate.BRITISH);
	   
	    

	    String boundTypeString = null;
	    String fieldTypeString = null;



	    //OHLCVAlert
	    if (tokenizer.hasMoreTokens()) {
		alertType = Alert.OHLCV;
		boundTypeString = tokenizer.nextToken();
		fieldTypeString = tokenizer.nextToken();
	    } else {
		alertType = Alert.GONDOLA;
	    }

	    Symbol symbol = Symbol.find(symbolString);
	    TradingDate startDate = new TradingDate(startDateString, 
						    TradingDate.BRITISH);

	    TradingDate endDate = (!endDateString.equals("NoEndDate")) 
		? new TradingDate(endDateString,
				  TradingDate.BRITISH) 
		: null;

	    if (boundTypeString != null) {
		int boundType = Alert.stringToBoundType(boundTypeString);

		Double targetValue = new Double(target);

		newAlert = new OHLCVAlert(symbol, startDate, endDate,
					  targetValue.doubleValue(),
					  boundType,fieldTypeString,
					  enabled);
	    } else {
		newAlert = new GondolaAlert(symbol, startDate, endDate,
					    target, true);
	    }


	    newAlert.setDateSet(dateSet);
	    
	} catch (NoSuchElementException e) {	    
	    throw new AlertException(AlertException.MISSING_FIELDS);
	} catch (SymbolFormatException e) {
	    throw new AlertException(AlertException.INVALID_SYMBOL);
	} catch (TradingDateFormatException e) {
	    throw new AlertException(AlertException.INVALID_DATE_FORMAT);
	} catch (NumberFormatException e) {
	    throw new AlertException(AlertException.INVALID_NUMBER_VALUE);
	} finally {
	    return newAlert;
	}
    }

    //File alerts are saved as CSV - commas in expressions need to be
    //added back in.
    private String unescapeString(String expression) {
	if (expression.indexOf("#COMMAREPLACED") == -1) {
	    return expression;
	} else {
	    String rv = expression.replaceAll("#COMMAREPLACED", ",");
	    return rv;
	}
    }
}