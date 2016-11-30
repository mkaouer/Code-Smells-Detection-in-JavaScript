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

package nz.org.venice.util;

public final class LocaleConstants {
    
    // To be modified if another localization is added. Just add the supported languages here.
    final public static java.util.Locale[] locales =  {
	new java.util.Locale("CA"), // Catalan
        java.util.Locale.ENGLISH,
        java.util.Locale.FRENCH,
	java.util.Locale.GERMAN,
        java.util.Locale.ITALIAN,
        java.util.Locale.SIMPLIFIED_CHINESE,
	new java.util.Locale("SV"), // Swedish
	new java.util.Locale("PL"), // Polish
    };

    final public static int localeCount = locales.length;
}
