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

/**
 * An alert whose trigger conditions are a quote value (Open, High, Low, Close, 
 * Volume) and a bound (Upper, Lower, Exact). 
 * 
 * @author Mark Hummel
 * @see Alert
 */

public class OHLCVAlert extends Alert {
    
    private double targetValue;
    private int boundType;
    private String field;
	   
    public OHLCVAlert() {
	super();
    }

    public OHLCVAlert(Symbol symbol, 
		      TradingDate startDate,
		      TradingDate endDate,
		      double targetValue,
		      int boundType,
		      String field, 
		      boolean enabled) {
	
	super(symbol, startDate, endDate, enabled);
	this.targetValue = targetValue;
	this.boundType = boundType;
	this.field = field;
	super.setType(Alert.OHLCV);
    }
    
    public Double getTargetValue() {
	return new Double(targetValue);
    }

    public void setTargetValue(Double target) {
	this.targetValue = target.doubleValue();
    }

    public void setTargetExpression(String expression) {
    }

    public String getTargetExpression() {
	return null;
    }

    public int getType() {
	return OHLCV;
    }

    public int getBoundType() {
	return boundType;
    }

    public void setBoundType(int boundType) {
	this.boundType = boundType;
    }

    public String getField() {
	return field;
    }

    public void setField(String field) {
	this.field = field;
    }

    public Object clone() {
	OHLCVAlert clone = new OHLCVAlert(getSymbol(), 
					  getStartDate(), 
					  getEndDate(),
					  targetValue,			 
					  boundType,
					  field,
					  getEnabled());
	clone.setDateSet(getDateSet());

	return clone;
    }

    public boolean isEqualTo(Alert alert) {
	if (!super.isEqualTo(alert)) {
	    return false;
	}

	if (alert.getTargetValue().doubleValue() != this.getTargetValue().doubleValue()) {
	    return false;
	}

	if (alert.getBoundType() != this.getBoundType()) {
	    return false;
	}

	if (!alert.getField().equals(this.getField())) {
	    return false;
	}
	return true;
    } 

    public String toString() {
	String rv = super.toString() + "," + getTargetValue() + "," + 
	    boundTypeToString(getBoundType()) + "," + 
	    field;
	return rv;
    }
}
