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

package org.mov.ui;

import java.text.*;

import org.mov.util.*;

public class PriceFormat implements Comparable {

    private float price;

    public PriceFormat(float price) {
        this.price = price;
    }

    /**
     * Convert from a price (in dollars) to string. This will add the
     * appropriate "$" and "c" symbols as needed.
     *
     * @param	price	the price
     * @return	the price string
     */
    public static String priceToString(float price) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        
        return format.format((double)price);
    }

    public String toString() {
        return priceToString(price);
    }

    public float getPrice() {
        return price;
    }

    public int compareTo(Object object) {
        PriceFormat format = (PriceFormat)object;

        if(getPrice() < format.getPrice())
            return -1;
        if(getPrice() > format.getPrice())
            return 1;
        else
            return 0;
    }   
}
