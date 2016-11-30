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

/**
 * An exception which is raised when invalid alert data is read.
 * For example, if the value for target price cannot be parsed as a double.
 * Shouldn't happen, but external processes could read/write alerts.
 *
 * @author Mark Hummel
 */
public class AlertException extends Throwable {

    public static final int INVALID_DATE_FORMAT   = 0;
    public static final int INVALID_NUMBER_VALUE  = 1; 
    public static final int INVALID_SYMBOL        = 2;
    public static final int MISSING_FIELDS        = 3;
    public static final int INVALID_EXPRESSION    = 4;
    public static final int INVALID_FIELD         = 5;
    
    private final int reason;
    private final String message;
    
    /**
     * Create an Alert exception.
     *
     * @param reason for the exception.
     */    
    public AlertException(int reason) {
	this.reason = reason;
	this.message = "";
    }

     /**
      * Create a ChartOutOfBounds exception.
     *
     * @param reason for the exception.
     * @param message messages created by the original exception 
     */    
    public AlertException(int reason, String message) {
	this.reason = reason;
	this.message = message;
    }
    
    public String getMessage() {
	return message;
    }

}
