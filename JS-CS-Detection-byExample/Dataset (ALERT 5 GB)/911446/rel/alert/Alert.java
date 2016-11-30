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

/**
 * Representation of either a Quote (Open, High, Low, Close, Volume (OHCLV)) 
 * alert or a Gondola expression alert.
 *
 * @author Mark Hummel

 */

package nz.org.venice.alert;


import nz.org.venice.quote.Symbol;
import nz.org.venice.quote.Quote;
import nz.org.venice.util.TradingDate;

public abstract class Alert {

    public static final int OHLCV           = 0;
    public static final int GONDOLA         = 1;

    public static final int UPPER_BOUND     = 0;
    public static final int LOWER_BOUND     = 1;
    public static final int EXACT_BOUND     = 2;
    public static final int GONDOLA_TRIGGER = 3; //Gondola expression 
    
    public static final String OPEN_FIELD   = "open";
    public static final String HIGH_FIELD   = "high";
    public static final String LOW_FIELD    = "low";
    public static final String CLOSE_FIELD  = "close";
    public static final String VOLUME_FIELD = "volume";
    public static final String EXP_FIELD    = "exp"; //Gondola Expression
    
    private Symbol symbol;
    private TradingDate startDate;
    private TradingDate endDate;
    private boolean enabled;
    private int type;
    private TradingDate dateSet;
	
    public Alert() {
	
    }

    public Alert(Symbol symbol, TradingDate startDate, TradingDate endDate, boolean enabled) {
	this.symbol = symbol;
	this.startDate = startDate;
	this.endDate = endDate;
	this.enabled = enabled;
    }
     
    public void setDateSet(TradingDate dateSet) {
	this.dateSet = dateSet;
    }

    public TradingDate getDateSet() {
	return dateSet;
    }

    public void setSymbol(Symbol symbol) {
	this.symbol = symbol;
    }
   
    public Symbol getSymbol() {
	return symbol;
    }

    public TradingDate getStartDate() {
	return startDate;
    }

    public void setStartDate(TradingDate startDate) {
	this.startDate = startDate;
    }

    public TradingDate getEndDate() {
	return endDate;
    }

    public void setEndDate(TradingDate endDate) {
	this.endDate = endDate;
    }

    public boolean getEnabled() {
	return enabled;
    }

    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

    //Created so that I don't have to remember
    //string->enum conversions and be consistent
    public static int stringToBoundType(String s) {
	if (s.equals("UPPER")) {
	    return UPPER_BOUND;
	} else if (s.equals("LOWER")) {
	    return LOWER_BOUND;
	} else if (s.equals("EXACT")) {
	    return EXACT_BOUND;
	} else if (s.equals("GONDOLA_TRIGGER")) {
	    return GONDOLA_TRIGGER;
	}
	assert false;
	return 0;
    }
    
    //Created so that I don't have to remember
    //string->enum conversions and be consistent
    public static String boundTypeToString(int bound) {
	switch (bound) {
	case UPPER_BOUND: return "UPPER";
	case LOWER_BOUND: return "LOWER";
	case EXACT_BOUND: return "EXACT";
	case GONDOLA_TRIGGER: return "GONDOLA_TRIGGER";
	default: 
	    assert false;
	    return null;	    
	}
    }

    public static int fieldToQuote(String field) {
	if (field.equals(OPEN_FIELD)) {
	    return Quote.DAY_OPEN;
	} else if (field.equals(CLOSE_FIELD)) {
	    return Quote.DAY_CLOSE;
	} else if (field.equals(HIGH_FIELD)) {
	    return Quote.DAY_HIGH;
	} else if (field.equals(LOW_FIELD)) {
	    return Quote.DAY_LOW;
	} else if (field.equals(VOLUME_FIELD)) {
	    return Quote.DAY_VOLUME;
	} else {
	    assert false;
	    return -1;
	}
    }
    //FIXME - needs to be generic so that AM can display alerts without
    //caring about details
    //Fix by making abstract, get subclasses to implement
    //and remove the specific getValue,getExpression
    public Object getTarget() {
	if (type == OHLCV) {
	    return getTargetValue();
	} else {
	    return getTargetExpression();
	}
    }
    
    public String toString() {
	TradingDate endDate = getEndDate();
	String endDateString = (endDate != null) ? endDate.toString() : "";

	return getSymbol() + "," + getStartDate() + "," + 
	    endDateString + "," + getDateSet();
    }

    public abstract Double getTargetValue();

    public abstract String getTargetExpression();

    public abstract void setTargetValue(Double value);

    public abstract void setTargetExpression(String expression);
    
    public void setType(int type) {
	assert type == OHLCV || type == GONDOLA;
	this.type = type;
    }

    public abstract int getType();

    public abstract int getBoundType();
    
    public abstract void setBoundType(int boundType);

    public abstract String getField();

    public abstract void setField(String field);

    public abstract Object clone();

    //TODO Convert to overridden equals(Object alert)
    public boolean isEqualTo(Alert alert) {	    
	if (!alert.getSymbol().equals(this.getSymbol())) {
	    return false;
	}
	if (!alert.getStartDate().equals(this.getStartDate())) {
	    return false;
	}

	TradingDate endDate = alert.getEndDate();
	TradingDate thisEndDate = this.getEndDate();
	if (endDate == null && thisEndDate == null) {
	    
	} else if (endDate != null && thisEndDate != null) {
	    if (!endDate.equals(thisEndDate)) {
		return false;
	    }
	} else {
	    return false;
	}
	return true;
    }

}
