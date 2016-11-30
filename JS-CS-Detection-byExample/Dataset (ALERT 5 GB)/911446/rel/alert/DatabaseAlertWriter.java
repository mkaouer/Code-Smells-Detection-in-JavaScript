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
import java.util.Iterator;

import nz.org.venice.quote.DatabaseManager;
import nz.org.venice.ui.DesktopManager;
import nz.org.venice.quote.Symbol;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.Locale;

/**
 * Store new alerts in the database. Update, enable and delete alerts already
 * stored.
 * 
 * @author Mark Hummel
 * @see AlertReader
 * @see AlertWriter
 */

public class DatabaseAlertWriter implements AlertWriter {

    private DatabaseManager manager;

    public DatabaseAlertWriter(DatabaseManager manager) {
	this.manager = manager;
    }



    public void set(OHLCVAlert alert) {	
	if (manager.getConnection()) {	    
	    String queryName = (alert.getEndDate() != null) 
		? "insertOHLCVAlert"
		: "insertOHLCVAlert_noEndDate";
	    	    
	    runQuery(queryName, alert, true);	    
	}
    }

    private String replaceParameters(String query, Alert alert) {
	String rv = query;
	
	rv = manager.replaceParameter(rv, "host", 
				      manager.getHost());
	
	rv = manager.replaceParameter(rv, "username", 
				      manager.getUserName());

	rv = manager.replaceParameter(rv, "symbol", 
				      alert.getSymbol().toString());

	rv = manager.replaceParameter(rv, "dateSet",
				      manager.toSQLDateString(alert.
							      getDateSet()));

	if (alert.getBoundType() == Alert.GONDOLA_TRIGGER) {
	      rv = manager.replaceParameter(rv, "target",
					    alert.getTargetExpression());
	} else {
	    rv = manager.replaceParameter(rv, "target",
					  alert.getTargetValue().toString());
	}
	
	rv = manager.replaceParameter(rv, "boundType",
				      Alert.boundTypeToString(alert.
							      getBoundType())
				      );

	rv = manager.replaceParameter(rv, "fieldType",
				      alert.getField());

	rv = manager.replaceParameter(rv, "start_date",
				      manager.toSQLDateString(alert.
							      getStartDate()));

	if (alert.getEndDate() != null) {
	    rv = manager.replaceParameter(rv, "end_date",
					  manager.toSQLDateString(alert.
								  getEndDate()));
	}	

	return rv;
    }

    public void set(GondolaAlert alert) {
	if (manager.getConnection()) {	    
	    String queryName = (alert.getEndDate() != null) 
		? "insertGondolaAlert"
		: "insertGondolaAlert_noEndDate";

	    runQuery(queryName, alert, true);	    
	}
    } 


    public void update(Alert alert, OHLCVAlert newAlert) {

	remove(alert);
	set(newAlert);
    }
    public void update(Alert alert, GondolaAlert newAlert) {
	remove(alert);
	set(newAlert);
    }

    public void enable(Alert alert) {
	if (manager.getConnection()) {
	    runQuery("enableAlert", alert, false);
	}
    }

    public void disable(Alert alert) {
	if (manager.getConnection()) {
	    runQuery("disableAlert", alert, false);
	}
    }
    
    public void remove(Alert alert) {
	if (manager.getConnection()) {
	    //If we're writing to the database, preferences should return
	    //a DatabaseAlertReader. 
	    DatabaseAlertReader reader = 
		(DatabaseAlertReader)AlertManager.getReader();

	    String uuid = reader.getUUID(alert);

	    if (uuid == null) {		
		return;
	    }

	    ArrayList newQueryList = new ArrayList();
	    List queries = manager.getQueries("deleteAlert");
	    Iterator iterator = queries.iterator();
	    while (iterator.hasNext()) {
		String query = (String)iterator.next();
		query = manager.replaceParameter(query, "id", uuid);
		query = replaceParameters(query, alert);
		newQueryList.add(query);
	    }
	    try {
		manager.executeUpdateTransaction(newQueryList);
	    } catch (SQLException e) {

	    }
	}     
    }
      
        
    public void remove(Symbol symbol) {	
	if (manager.getConnection()) {
	    try {
		String queryString = 
		    "DELETE FROM " + DatabaseManager.ALERT_TABLE_NAME +
		    "where symbol = '" + symbol + "' AND "  + 
		    "host = '" + manager.getHost() + "' AND " +
		    "username = '" + manager.getUserName() + "'";
		
		Statement statement = manager.createStatement();
		int result = statement.executeUpdate(queryString);	
	    } catch (SQLException e) {
		DesktopManager.
		    showErrorMessage(Locale.
				     getString("ERROR_TALKING_TO_DATABASE",
					       e.getMessage()));
	    }
	}
    }
            

    private void runQuery(String queryName, Alert alert, boolean newAlert) {
	String uuid = null;
	       
	if (!newAlert) {
	    DatabaseAlertReader reader = 
		(DatabaseAlertReader)AlertManager.getReader();
	    uuid = reader.getUUID(alert);
	    
	    if (uuid == null) {
		//Didn't find alert
		return;
	    }
	} else {
	    uuid = manager.getUUID();
	}
	
	ArrayList newQueryList = new ArrayList();
	List queries = manager.getQueries(queryName);		
	assert queries != null;

	Iterator iterator = queries.iterator();
	while (iterator.hasNext()) {
	    String query = (String)iterator.next();
	    query = manager.replaceParameter(query, "id", uuid);
	    query = replaceParameters(query, alert);
	    newQueryList.add(query);
	}
	try {
	    manager.executeUpdateTransaction(newQueryList);
	} catch (SQLException e) {

	}	    
    }    
}
