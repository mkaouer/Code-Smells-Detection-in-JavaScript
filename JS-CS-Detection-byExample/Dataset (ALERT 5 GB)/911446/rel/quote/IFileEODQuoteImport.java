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
 *  Interface for importing quotes from files. 
 *  
 * @author Guillermo Bonvehi (gbonvehi)
 * @see FileEODQuoteImport

 */

package nz.org.venice.quote;

import java.io.File;
import java.util.List;

public interface IFileEODQuoteImport {

	/**
	 * Open the given file to import.
	 *
	 * @param file the file to import
	 * @return <code>TRUE</code> if the file was successfully opened;\
	 *         <code>FALSE</code> otherwise.
	 */
	public abstract boolean open(File file);

	/** 
	 * Import the next bundle quotes from the file.
	 *
	 * @return list of quotes
	 */
	public abstract List importNext();

	/**
	 * Return whether there are any more quotes in the file.
	 *
	 * @return <code>TRUE</code> if there are more quotes to import;
	 *         <code>FALSE</code> otherwise.
	 */
	public abstract boolean isNext();

	/**
	 * Close the file being imported.
	 */
	public abstract void close();

}