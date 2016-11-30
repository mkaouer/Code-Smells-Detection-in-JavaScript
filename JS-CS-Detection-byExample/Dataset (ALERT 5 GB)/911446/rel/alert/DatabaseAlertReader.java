/* Merchant of Venice - technical analysis software for the stock market.
   Copyright (C) 2002 Andrew Leppard (aleppard@picknowl.com.au)

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
g   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package nz.org.venice.alert;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;

import nz.org.venice.quote.DatabaseManager;
import nz.org.venice.ui.DesktopManager;
import nz.org.venice.quote.Symbol;
import nz.org.venice.quote.SymbolFormatException;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.TradingDateFormatException;
import nz.org.venice.util.Locale;


/**
 * Retrieve alerts stored in the database.
 * 
 * @author Mark Hummel
 * @see AlertReader 
 */

public class DatabaseAlertReader implements AlertReader {

    private DatabaseManager manager;
    private HashMap alertMap; //When the alerts are read, cache the uuids
 
    public DatabaseAlertReader(DatabaseManager manager) {
	this.manager = manager;
	alertMap = new HashMap();

    }

    /**
     * Get all alerts currently stored.
     * @return A list of all the alerts currently stored.
     */    
    public List getAlerts() {
	HashMap endDateMap = new HashMap();
	alertMap.clear();
	List queries = manager.getQueries("getAllAlerts");	      

	if (manager.getConnection()) {
	    Iterator iterator = queries.iterator();
	    while (iterator.hasNext()) {
		String query = (String)iterator.next();

		try {
		    Statement statement = manager.createStatement();
		    ResultSet RS = statement.executeQuery(query);
		    
		    while (RS.next()) {
			String uuid = RS.getString(manager.ALERT_UUID_COLUMN);
			String username = RS.getString(manager.ALERT_USER_COLUMN);
			String host = RS.getString(manager.ALERT_HOST_COLUMN);
						
			if (!username.equals(manager.getUserName()) ||
			    !host.equals(manager.getHost())) {
			    continue;
			}
			
			String symbolString = 
			    RS.getString(manager.ALERT_SYMBOL_COLUMN);
			String startDateString = 
			    RS.getString(manager.ALERT_START_DATE_COLUMN);
			String endDateString = 
			    RS.getString(manager.ALERT_END_DATE_COLUMN);
			String targetString = 
			    RS.getString(manager.ALERT_TARGET_COLUMN);
						
			String boundTypeString =  
			    RS.getString(manager.ALERT_BOUND_TYPE_COLUMN);
			String targetTypeString = 
			    RS.getString(manager.ALERT_TARGET_TYPE_COLUMN);
			String enabledString = 
			    RS.getString(manager.ALERT_ENABLED_COLUMN);

			String dateSetString = 
			    RS.getString(manager.ALERT_DATESET_COLUMN);
			
			Symbol symbol = Symbol.find(symbolString);

			TradingDate startDate = 
			    new TradingDate(startDateString, 
					TradingDate.BRITISH);
			
			TradingDate endDate = null;

			if (!endDateString.equals("no enddate")) {
			    endDate = new TradingDate(endDateString,
						      TradingDate.BRITISH);
			    endDateMap.put(uuid, endDate);
			}
			
			boolean enabled = readBoolean(enabledString);
			
			TradingDate dateSet = 
			    new TradingDate(dateSetString,
					    TradingDate.BRITISH);

			
			//When target is "no target", that means an enddate row
			//Don't create an alert in this case.
			if (!targetString.equals("no target")) {
			    Alert alert = AlertFactory.newAlert(symbol,
								startDate,
								endDate,
								targetString,
								boundTypeString,
								targetTypeString,
								enabled,
								dateSet);

			    alertMap.put(uuid, alert);			      
			}		    
		    }	   
		} catch (TradingDateFormatException e) {
		    throw new AlertException(AlertException.
					     INVALID_DATE_FORMAT, 
					     e.getMessage());
		} catch (NumberFormatException e) {
		    throw new AlertException(AlertException.INVALID_NUMBER_VALUE,
					     e.getMessage());
		} catch (SymbolFormatException e) {
		    throw new AlertException(AlertException.INVALID_SYMBOL,
					     e.getMessage());
		} catch (SQLException e) {
		    DesktopManager.
			showErrorMessage(Locale.
					 getString("ERROR_TALKING_TO_DATABASE",
						   e.getMessage()));
		} finally {
		    insertEndDates(alertMap, endDateMap);
		    return new ArrayList(alertMap.values());
		}
	    }
	}

	insertEndDates(alertMap, endDateMap);
	return new ArrayList(alertMap.values());
    } 
    
    	//Update the Alerts with end dates where they have been set
    private void insertEndDates(HashMap alertMap, HashMap endDateMap) {	

	Set keySet = endDateMap.keySet();
	Iterator iterator = keySet.iterator();

	while (iterator.hasNext()) {
	    String uuid = (String)iterator.next();
	    TradingDate endDate = (TradingDate)endDateMap.get(uuid);
	    Alert alert = (Alert)alertMap.get(uuid);
	    alert.setEndDate(endDate);
	}
    }
    	
    /**
     * Get all alerts currently stored for the symbol.
     * @param symbol The symbol to filter the alerts on.
     * @return A list of all the alerts for this symbol.
     */
    public List getAlertsBySymbol(Symbol symbol) throws AlertException {
	ArrayList alerts = new ArrayList();
	
	List allAlerts = getAlerts();
	Iterator iterator = allAlerts.iterator();
	while (iterator.hasNext()) {
	    Alert alert = (Alert)iterator.next();
	    if (alert.getSymbol().equals(symbol)) {
		alerts.add(alert);
	    }
	}
	return alerts;
    }

    public List getAlertsBySymbolList(List symbols) throws AlertException {
	assert symbols != null;

	ArrayList alerts = new ArrayList();

	Iterator iterator = symbols.iterator();
	while (iterator.hasNext()) {
	    Symbol symbol = (Symbol)iterator.next();
	    
	    List alertsForSymbol = getAlertsBySymbol(symbol);
	    Iterator alertIterator = alertsForSymbol.iterator();
	    while (alertIterator.hasNext()) {
		alerts.add(alertIterator.next());
	    }
	}
	return alerts;
    }

    //Correct for the earlier versions of MySQL converting boolean to tinyint.
    //(Instead of true|false, you got 0,1)
    //Correct for this
    private boolean readBoolean(String boolString) {
	if (boolString.equals("0"))
	    return false;
	
	if (boolString.equals("1"))
	    return true;

	if (boolString.toUpperCase().equals("FALSE"))
	    return false;
	
	if (boolString.toUpperCase().equals("TRUE"))
	    return true;

	assert false;
	return false;
    }

    protected String getUUID(Alert alert) {
	Iterator iterator = alertMap.keySet().iterator();
	while (iterator.hasNext()) {
	    String uuid = (String)iterator.next();
	    Alert testAlert = (Alert)alertMap.get(uuid);

	    if (testAlert.isEqualTo(alert)) {
		return uuid;
	    }
	}
	return null;
    }
    
}
