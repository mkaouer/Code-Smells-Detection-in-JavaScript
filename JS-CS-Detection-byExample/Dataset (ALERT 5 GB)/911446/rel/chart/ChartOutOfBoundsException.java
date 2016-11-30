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

package nz.org.venice.chart;

/**
 * An exception which is raised when the chart is zoomed in where there 
 * is no graph data. 
 *
 *
 * @author Mark Hummel
 */
public class ChartOutOfBoundsException extends Throwable {


    /**
     * Create a ChartOutOfBounds exception.
     *
     * @param reason for the exception.
     */    
    public ChartOutOfBoundsException(int reason) {
    }

    /**
     *  Create a ChartOutOfBounds exception.
     *
     */
    public ChartOutOfBoundsException() {
    }

}
