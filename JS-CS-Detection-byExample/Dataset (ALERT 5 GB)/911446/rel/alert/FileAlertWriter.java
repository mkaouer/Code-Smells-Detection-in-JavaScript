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


import nz.org.venice.quote.Symbol;
import nz.org.venice.util.TradingDate;

import nz.org.venice.prefs.PreferencesManager;
import java.util.prefs.Preferences;

/**
 * Store new alerts in the filesystem. Update, delete and enable alerts already
 * stored. 
 * 
 * @author Mark Hummel
 * @see AlertWriter
 */

public class FileAlertWriter implements AlertWriter  {

    public FileAlertWriter() {
    }

    public void set(OHLCVAlert alert) {

	TradingDate endDate = alert.getEndDate();
	String endDateString = "NoEndDate";

	if (endDate == null) {
	    endDateString = "NoEndDate";
	} else {
	    endDateString = endDate.toString();
	}
	
	String keyPath = 
	    alert.getSymbol() + "," +  
	    alert.getStartDate() + "," + 
	    endDateString + "," + 
	    alert.getTargetValue() + "," +
	    alert.getDateSet() + "," + 
	    Alert.boundTypeToString(alert.getBoundType()) + "," + 	    
	    alert.getField();
	
	Preferences prefs = PreferencesManager.getUserNode("/table/alerts");	
	prefs.put(keyPath, "enabled");

	PreferencesManager.flush();
    }

    public void remove(OHLCVAlert alert) {
	String endDateString = (alert.getEndDate() == null) ? "NoEndDate" : 
	    alert.getEndDate().toString();

	String keyPath = 
	    alert.getSymbol() + "," + 
	    alert.getStartDate() + "," + 
	    endDateString + "," + 
	    alert.getTargetValue() + "," +
	    alert.getDateSet() + "," +   
	    Alert.boundTypeToString(alert.getBoundType()) + "," + 
	    alert.getField();
	
	Preferences prefs = PreferencesManager.getUserNode("/table/alerts");	
	prefs.remove(keyPath);
	
	PreferencesManager.flush();
    }

    public void remove(GondolaAlert alert) {
	String endDateString = "NoEndDate";
	if (alert.getEndDate() != null) {
	    endDateString = alert.getEndDate().toString();
	}

	String keyPath = 
	    alert.getSymbol() + "," + 
	    alert.getStartDate() + "," + 
	    endDateString + "," + 
	    escapeString(alert.getTargetExpression()) + "," + 
	    alert.getDateSet();
	
	Preferences prefs = 
	    PreferencesManager.getUserNode("/table/alerts");	
	prefs.remove(keyPath);
	
	PreferencesManager.flush();
    }

    public void set(GondolaAlert alert) {
	String endDateString = "NoEndDate";
	if (alert.getEndDate() != null) {
	    endDateString = alert.getEndDate().toString();
	}

	String keyPath = 
	    alert.getSymbol() + "," + 
	    alert.getStartDate() + "," + 
	    endDateString + "," + 
	    escapeString(alert.getTargetExpression()) + "," + 
	    alert.getDateSet();
	
	Preferences prefs = PreferencesManager.getUserNode("/table/alerts");	
	prefs.put(keyPath, "enabled");

	PreferencesManager.flush();
    }



    public void update(Alert alert, OHLCVAlert newAlert) {		
	remove(alert);
	set(newAlert);

	PreferencesManager.flush();
    }

    public void update(Alert alert, GondolaAlert newAlert) {		
	remove(alert);
	set(newAlert);

	PreferencesManager.flush();
    }

   
    public void enable(GondolaAlert alert) {
	String keyPath = 
	    alert.getSymbol() + "," + 
	    Alert.boundTypeToString(alert.getBoundType()) + "," + 
	    escapeString(alert.getTargetExpression()) + 
	    alert.getDateSet();
	
	Preferences prefs = PreferencesManager.getUserNode("/table/alerts");	
	prefs.remove(keyPath);
	prefs.put(keyPath, "enabled");

	PreferencesManager.flush();
    }

    

    public void disable(GondolaAlert alert) {
	String keyPath = 
	    alert.getSymbol() + "," + 
	    Alert.boundTypeToString(alert.getBoundType()) + "," + 
	    escapeString(alert.getTargetExpression()) + 
	    alert.getDateSet();
	
	Preferences prefs = PreferencesManager.getUserNode("/table/alerts");	
	prefs.remove(keyPath);
	prefs.put(keyPath, "disabled");

	PreferencesManager.flush();
    }
    
    public void enable(OHLCVAlert alert) {
	
	String endDateString = (alert.getEndDate() == null) 
	    ? "NoEndDate" 
	    : alert.getEndDate().toString();	    

	String keyPath = 
	    alert.getSymbol() + "," + 
	    alert.getStartDate() + "," + 
	    endDateString + "," + 
	    alert.getTargetValue() + "," + 
	    alert.getDateSet() + "," + 
	    Alert.boundTypeToString(alert.getBoundType()) + "," + 
	    alert.getField();
	
	Preferences prefs = PreferencesManager.getUserNode("/table/alerts");	
	prefs.remove(keyPath);
	prefs.put(keyPath, "enabled");

	PreferencesManager.flush();
    }

    public void disable(OHLCVAlert alert) {
	String endDateString = (alert.getEndDate() == null) 
	    ? "NoEndDate" 
	    : alert.getEndDate().toString();	    

	String keyPath = 
	    alert.getSymbol() + "," + 
	    alert.getStartDate() + "," + 
	    endDateString + "," + 
	    alert.getTargetValue() + "," + 
	    alert.getDateSet() + "," + 
	    Alert.boundTypeToString(alert.getBoundType()) + "," + 
	    alert.getField();

	Preferences prefs = PreferencesManager.getUserNode("/table/alerts");	
	prefs.remove(keyPath);
	prefs.put(keyPath, "disabled");

	PreferencesManager.flush();
    }

    public void set(Alert alert) {	
	if (alert.getType() == Alert.GONDOLA) {
	    GondolaAlert alertToAdd = (GondolaAlert)alert;
	    disable(alertToAdd);
	} else {
	    OHLCVAlert alertToAdd = (OHLCVAlert)alert;
	    disable(alertToAdd);
	}
    }

    public void remove(Alert alert) {
	if (alert.getType() == Alert.GONDOLA) {
	    GondolaAlert alertToRemove = (GondolaAlert)alert;	    
	    remove(alertToRemove);
	} else {
	    OHLCVAlert alertToRemove = (OHLCVAlert)alert;	    
	    remove(alertToRemove);
	}
    }
    
    public void enable(Alert alert) {
	if (alert.getType() == Alert.GONDOLA) {
	    GondolaAlert alertToEnable = (GondolaAlert)alert;
	    enable(alertToEnable);
	} else {
	    OHLCVAlert alertToEnable = (OHLCVAlert)alert;
	    enable(alertToEnable);
	}
    }
    public void disable(Alert alert) {
	if (alert.getType() == Alert.GONDOLA) {
	    GondolaAlert alertToDisable = (GondolaAlert)alert;
	    disable(alertToDisable);
	} else {
	    OHLCVAlert alertToDisable = (OHLCVAlert)alert;
	    disable(alertToDisable);
	}
    }
    
    //File alerts are saved as CSV - commas in expressions need to be
    //escaped out.
    private String escapeString(String expression) {
	if (expression.indexOf(',') == -1) {
	    return expression;
	} else {
	    String rv = expression.replaceAll(",","#COMMAREPLACED");
	    return rv;
	}
    }
}
