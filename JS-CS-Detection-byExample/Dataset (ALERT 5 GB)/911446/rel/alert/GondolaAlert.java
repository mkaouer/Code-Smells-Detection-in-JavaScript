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
 * An alert whose trigger condition is a Gondola Expression.
 * i.e. The alert triggers when the expression evaluates to True.
 *
 * @author Mark Hummel
 * @see Alert
 */

public class GondolaAlert extends Alert {

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
    
    private String targetExpression;
      
    public GondolaAlert() {
	super();
    }

    public GondolaAlert(Symbol symbol, 
			TradingDate startDate, 
			TradingDate endDate,
			String targetExpression,
			boolean enabled) {

	super(symbol, startDate, endDate, enabled);
	super.setType(Alert.GONDOLA);
	this.targetExpression = targetExpression;
    }

    public void setTargetValue(Double value) {
    }

    public Double getTargetValue() {
	return null;
    }

    public String getTargetExpression() {
	return targetExpression;
    }

    public void setTargetExpression(String targetExpression) {
	this.targetExpression = targetExpression;
    }

    public int getType() {
	return GONDOLA;
    }

    public int getBoundType() {
	return GONDOLA_TRIGGER;
    }
    
    //do nothing, not applicable for this type of alert
    //Just here so we can do manage alerts generically without
    //doing stuff like: if instanceof OHLCVAlert do this otherwise do that.
    public void setBoundType(int boundType) {	

    }

    public String getField() {
	return EXP_FIELD;
    }

    //do nothing, see comment above.
    public void setField(String field) {
	
    }

    public String toString() {
	return super.toString() + "," + getTargetExpression();
    }

    public Object clone() {
	GondolaAlert clone = new GondolaAlert(getSymbol(), 
					      getStartDate(), 
					      getEndDate(),
					      targetExpression,
					      getEnabled());

	clone.setDateSet(getDateSet());
	return clone;
    }

    public boolean isEqualTo(Alert alert) {
	if (!super.isEqualTo(alert)) {
	    return false;
	}

	if (!alert.getTargetExpression().equals(this.getTargetExpression())) {
	    return false;
	}
	return true;
    }
    
}
