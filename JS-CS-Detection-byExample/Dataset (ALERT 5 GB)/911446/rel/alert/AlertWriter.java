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
 * Provides an interface to set, modify and delete alerts. Allows AlertModule  
 * to do the updates regardless of where the alerts are stored.
 *
 * @author Mark Hummel
 * @see Alert
 * @see AlertReader
 * @see AlertModule
 */

public interface AlertWriter {

    
    /**
     * 
     * Set the alert. 
     * @param alert    The alert to add
     */    
    public void set(OHLCVAlert alert);


    /**
     * 
     * Set the alert. 
     * @param alert    The alert to add
     */    
    public void set(GondolaAlert alert);

    /**
     *
     * Update the alert and make it a OHLCVAlert.
     * 
     * @param alert      The alert to modify
     * @param newAlert   The alert settings to use for modification
     */

    public void update(Alert alert, OHLCVAlert newAlert);

    /**
     *
     * Update the alert and make it a GondolaAlert.
     * 
     * @param alert      The alert to modify
     * @param newAlert   The alert settings to use for modification
     */

    public void update(Alert alert, GondolaAlert newAlert);
               
    /**
     * Remove a specific alert.
     * 
     * @param alert      Tne alert to remove.
     */
    
    public void remove(Alert alert);


    /**
     *
     * Enable an alert.
     *
     * @param alert The alert to enable.
     */
    public void enable(Alert alert);

    /**
     *
     * Enable an alert.
     *
     * @param alert The alert to disable.
     */
    public void disable(Alert alert);
  
}
