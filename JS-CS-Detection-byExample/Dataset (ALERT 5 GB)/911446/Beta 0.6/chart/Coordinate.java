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
 * Coordinates for charts.
 *
 * @author Mark Hummel
 * 
 */

package org.mov.chart;

public class Coordinate
{

    protected static final int BREAK = -1; //End of coordinates/Out of bounds marker 

    private Integer x;
    private Integer y;

    public Coordinate(Integer x, Integer y) {
	this.x = x;
	this.y = y;
    }


    public Integer getX() {
	return x;
    }

    public Integer getY() {
	return y;
    }

}