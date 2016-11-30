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

package nz.org.venice.analyser;

public final class GPModuleConstants {
    
    // percent format
    public final static String format = "0.00#";
    // max percent
    public final static double PERCENT_DOUBLE = 100.0;
    public final static int PERCENT_INT = 10000;
    
    // use "@" as separator char in the preferences
    public final static String separatorString = "@";
    // use " " as null string
    public final static String nullString = " ";
    
    // Number of rules (buy rule and sell rule)
    public final static int BUY_RULE = 0;
    public final static int SELL_RULE = 1;
    public final static int NUMBER_RULES = 2;
    
}
