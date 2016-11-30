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

import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.quote.DatabaseManager;
import nz.org.venice.quote.DatabaseAccessManager;
import nz.org.venice.quote.Symbol;
import nz.org.venice.util.TradingDate;

/**
 * Contains static methods for creating alerts and File/Database Alert Readers 
 * and Writers based on the user preference. 
 * 
 * 
 * @author Mark Hummel
 * @see FileAlertWriter
 * @see DatabaseAlertWriter
 * @see nz.org.venice.prefs.PreferencesManager
 */
public class AlertFactory {

    /**
     * Create a reader which retrieves file alerts from the filesystem.     
     *
     * @return	the file alert reader 
     */   
    public static FileAlertReader createFileAlertReader() {
	return new FileAlertReader();
    }

    /**
     * Create a reader which retrieves database alerts 
     *
     * @return	the database alert reader 
     */   
    public static DatabaseAlertReader createDatabaseAlertReader() {
	PreferencesManager.DatabasePreferences prefs = 
	    PreferencesManager.getDatabaseSettings();
        
	String password = DatabaseAccessManager.getInstance().getPassword();

        DatabaseManager dbm = new DatabaseManager( 
						 prefs.software, 
						 prefs.driver,
						 prefs.host, 
						 prefs.port, 
						 prefs.database, 
						 prefs.username, 
						 password);
	return new DatabaseAlertReader(dbm);
    }

    /**
     * Create a writer which creates file alerts directly using the 
     * user preferences.
     *
     * @return	the file alert writer 
     */
    public static FileAlertWriter createFileAlertWriter() {
	return new FileAlertWriter();
    }

    
    /**
     * Create a writers which creates database alerts directly using the 
     * user preferences.
     *
     * @return	the database alert writer
     */
    public static DatabaseAlertWriter createDatabaseAlertWriter() {
        PreferencesManager.DatabasePreferences prefs = 
           PreferencesManager.getDatabaseSettings();
        
	String password = DatabaseAccessManager.getInstance().getPassword();
        DatabaseManager dbm = new DatabaseManager( 
						 prefs.software, 
						 prefs.driver,
						 prefs.host, 
						 prefs.port, 
						 prefs.database, 
						 prefs.username, 
						 password);

	return new DatabaseAlertWriter(dbm);
    }

    public static Alert newAlert(Symbol symbol, 
				 TradingDate startDate, 
				 TradingDate endDate,
				 String target,
				 String boundType,
				 String fieldType,
				 boolean enabled,
				 TradingDate dateSet) throws AlertException {
	if (boundType.equals("no bound") || 
	    fieldType.equals("no field")) {
	    
	    Alert alert = new GondolaAlert(symbol,
					   startDate,
					   endDate,
					   target,
					   enabled);

	    alert.setDateSet(dateSet);

	    return alert;
	} else {	

	    if (Alert.fieldToQuote(fieldType) == -1) {
		//Won't happen because the above method will assert false
		throw new AlertException(AlertException.INVALID_FIELD);
	    }

	    Double targetValue = new Double("0.0");
	    try {
		targetValue = new Double(target);
	    } catch (NumberFormatException e) {
		throw new AlertException(AlertException.INVALID_NUMBER_VALUE);
	    } finally {		
		Alert alert = new OHLCVAlert(symbol,
					     startDate,
					     endDate,
					     targetValue.doubleValue(),
					     Alert.
					     stringToBoundType(boundType),
					     fieldType,
					     enabled);

		alert.setDateSet(dateSet);
		return alert;
	    }					 
	}
	
    }

}
    
