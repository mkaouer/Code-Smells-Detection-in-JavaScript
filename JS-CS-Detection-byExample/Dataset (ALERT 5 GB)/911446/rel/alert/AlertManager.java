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

import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.quote.EODQuoteBundle;
import nz.org.venice.quote.EODQuoteRange;
import nz.org.venice.quote.Quote;
import nz.org.venice.quote.Symbol;
import nz.org.venice.quote.QuoteSourceManager;
import nz.org.venice.quote.QuoteSource;
import nz.org.venice.quote.MissingQuoteException;
import nz.org.venice.quote.WeekendDateException;
import nz.org.venice.parser.ParserException;
import nz.org.venice.parser.EvaluationException;
import nz.org.venice.parser.Expression;
import nz.org.venice.parser.Parser;
import nz.org.venice.util.TradingDate;
import nz.org.venice.util.Locale;
import nz.org.venice.ui.ProgressDialog;
import nz.org.venice.ui.ProgressDialogManager;


/**
 * Returns the singleton reference to the AlertDestination that the user
 * has selected in their preferences. This class will also be
 * updated when the user preferences has changed so the return Alert Destination
 * will always be update to date.
 *
 * Example:
 * <pre>
 *	List alerts = QuoteSourceManager.getDestination().getAlertsForSymbol("CBA");
 * </pre>
 * 
 * @author Mark
 * @see Alert
 * @see AlertWriter 
 */
public class AlertManager {
    
    // Singleton instances of AlertManager class - 
    // an instance each for reading and writing alerts
    private static AlertWriter destInstance = null;
    private static AlertReader sourceInstance = null;

    private AlertManager() {
        // declared here so constructor is not public
    }
    
    public static void setSourceInstance(AlertReader instance) {
	sourceInstance = instance;
    }

    //Must read and write alerts from the same place.   
    public static synchronized AlertReader getReader() {
	if (sourceInstance == null) {
	    String destination = PreferencesManager.getAlertDestination();
	    
	    if (destination.equals(Locale.getString("ALERT_DISABLED_ALL"))) {
		
	    }	    
	    if (destination.equals(Locale.getString("FILE"))) {
		sourceInstance = AlertFactory.createFileAlertReader();
	    }
	    if (destination.equals(Locale.getString("DATABASE"))) {
		sourceInstance = AlertFactory.createDatabaseAlertReader();
	    }
	}
	return sourceInstance;
    }

    public static synchronized AlertWriter getWriter() {
	if (destInstance == null) {
	    String destination = PreferencesManager.getAlertDestination();

	    if (destination.equals(Locale.getString("ALERT_DISABLE_ALL"))) {
		
	    }
			    
	    if (destination.equals(Locale.getString("FILE"))) {
		destInstance = AlertFactory.createFileAlertWriter();
	    }
	    if (destination.equals(Locale.getString("DATABASE"))) {
		destInstance = AlertFactory.createDatabaseAlertWriter();
	    }
	}
	return destInstance;
    }

    public static boolean alertsTriggered(List triggeredAlerts, List triggerValues) {
	AlertReader reader = getReader();
	List alerts;
	QuoteSource quoteSource = QuoteSourceManager.getSource();
	EODQuoteBundle quoteBundle = null;
	boolean alertsTriggered = false;
	TradingDate today = new TradingDate();

	boolean interrupted = false;
	ProgressDialog progress = ProgressDialogManager.getProgressDialog();
	Thread thread = Thread.currentThread();
	
	progress.show(Locale.getString("ALERT_CHECK"));
	progress.setIndeterminate(true);
	progress.setMaster(true);
	
	try {
	    alerts = reader.getAlerts();
	    ArrayList symbolList = new ArrayList();
	    Iterator alertIterator = alerts.iterator();

	    //Magic: For some reason, on startup, just using the size
	    //as maximum means the triggered alerts won't show.
	    //Increasing the size by at least 2 works for some reason. 	    
	    progress.setMaximum(alerts.size() + 2);
	    progress.setIndeterminate(false);
	    progress.setProgress(0);
	    
	    //First get the list of symbols and build the quote bundle
	    //containing all the symbols
	    while (alertIterator.hasNext()) {
		if (thread.isInterrupted()) {
		    interrupted = true;
		    ProgressDialogManager.closeProgressDialog(progress);
		    return false;
		}
		Alert alert = (Alert)alertIterator.next();
		symbolList.add(alert.getSymbol());
	    }
	    

	    EODQuoteRange quoteRange = new EODQuoteRange(symbolList);
	    if (quoteSource.loadQuoteRange(quoteRange)) {
		quoteBundle = new EODQuoteBundle(quoteRange);
	    } else {
		assert false;
	    }
	    
	    //Now check the alerts to see if any trigger
	    alertIterator = alerts.iterator();
	    while (alertIterator.hasNext()) {
		if (thread.isInterrupted()) {
		    interrupted = true;
		    ProgressDialogManager.closeProgressDialog(progress);
		    return false;
		}
		Alert alert = (Alert)alertIterator.next();
		
		progress.increment();
		
		//If today is before the alert start date, skip
		if (today.compareTo(alert.getStartDate()) < 0) {
		    continue;
		}

		//if an end date has been set and today is after that date,
		//skip
		if (alert.getEndDate() != null && 
		    today.compareTo(alert.getEndDate()) > 0) {
		    continue;
		}
		
		Symbol symbol = alert.getSymbol();
		
		int quoteType = (!alert.getField().equals(Alert.EXP_FIELD))
		    ? Alert.fieldToQuote(alert.getField())
				 : Quote.DAY_CLOSE;
				
		TradingDate lastDate = quoteBundle.getLastDate();
		TradingDate firstDate = quoteBundle.getFirstDate();
		
		TradingDate latestDate = 
		    latestDateWithQuote(alert.getSymbol(),
					quoteType,
					quoteBundle,
					lastDate,
					firstDate);
		
		if (latestDate.
		    compareTo(alert.getStartDate()) < 0) {
		    continue;
		} else {
		    if (alert.getEndDate() != null &&
			latestDate.
			compareTo(alert.getEndDate()) > 0) {
			continue;
		    }
		}

		if (triggers(alert, quoteBundle, latestDate)) {
		    triggeredAlerts.add(alert);
		    double quote = getQuote(alert.getSymbol(), quoteType,
					    quoteBundle, latestDate);
		    triggerValues.add(new Double(quote));
		    alertsTriggered = true;
		}
	    }
	} catch (AlertException e) {
	    
	} finally {
	    if (!interrupted) {
		ProgressDialogManager.closeProgressDialog(progress);
	    }
	    return alertsTriggered;
	}
    }
    
    private static TradingDate latestDateWithQuote(Symbol symbol,
						   int quoteType,
						   EODQuoteBundle quoteBundle,
						   TradingDate startDate,
						   TradingDate endDate) {
	TradingDate latest = (TradingDate)startDate.clone();

	while (latest.compareTo(endDate) > 0) {
	    if (latest.isWeekend()) {
		latest = latest.previous(1);
		continue;
	    }
	    try {
		double quote = getQuote(symbol, quoteType, quoteBundle, latest);

		return latest;
	    } catch (MissingQuoteException e) {
		
	    } catch (WeekendDateException e) {
		//Shouldn't happen
		assert false;
	    } finally {
		latest = latest.previous(1);
	    }
	}
	return null;
    }

    //Check that this method doesn't already exist in quotebundle
    private static double getQuote(Symbol symbol, int quoteType,
			    EODQuoteBundle quoteBundle, 
			    TradingDate date) 
	throws MissingQuoteException, WeekendDateException {
				       
	int offset = quoteBundle.dateToOffset(date);

	double rv = quoteBundle.getQuote(symbol,
					 quoteType,
					 offset);

	return rv;
    }

    private static boolean triggers(Alert alert, EODQuoteBundle quoteBundle,
				    TradingDate latestDate) {
	
	boolean rv = false;

	try {
	    //FIXME need to have better handling of OHLCV vs GONDOLA
	    //Maybe seperate method
	    Double tmp = alert.getTargetValue();
	    double targetValue = (tmp != null) ? tmp.doubleValue() : 0.0;

	    int offset = quoteBundle.dateToOffset(latestDate);

	    int quoteType = (!alert.getField().equals(Alert.EXP_FIELD))
		? Alert.fieldToQuote(alert.getField()) 
		: Quote.DAY_CLOSE;
	    
	    double quote = quoteBundle.getQuote(alert.getSymbol(), 
						quoteType, offset);

	    

	    switch (alert.getBoundType()) {
	    case Alert.UPPER_BOUND:
		 rv = (quote >= targetValue) ? true : false;
		 break;
	    case Alert.LOWER_BOUND:
		rv = (quote <= targetValue) ? true : false;
		break;
	    case Alert.EXACT_BOUND:
		rv = (quote == targetValue) ? true: false;
		break;
	    case Alert.GONDOLA_TRIGGER:
		try {
		    Expression expression = 
			Parser.parse(null, alert.getTargetExpression());
		    
		    if (expression.evaluate(null, quoteBundle, 
					    alert.getSymbol(),
					    0) >= Expression.TRUE) {
			rv = true;
		    } else {
			rv = false;
		    }
		} catch (EvaluationException e) {
		    
		} catch (ParserException e) {

		} finally {
		    return rv;
		}
	    default:
		assert false;
		return false;
	    }
	} catch (MissingQuoteException e) {
	    //Shouldn't happen because we supply the latest date which 
	    //has a value.
	    assert false;
	    
	} finally {
	    return rv;
	}
    }
}

