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
 * This interface should be used by any EODQuoteFilter that reads from 
 * files when importing EOD quotes. It extends EODQuoteFilter by adding
 * a function to get the importer needed by the filter.
 *
 * @author Guillermo Bonvehi (gbonvehi)
 * @see EODQuoteFilter
 */

package nz.org.venice.quote;

import nz.org.venice.util.Report;

public interface IFileEODQuoteFilter extends EODQuoteFilter {
	/**
     * Return the an instance of the importer.
     *
     * @return	the instance of the importer
     */
    public IFileEODQuoteImport getImporter(Report report);
}