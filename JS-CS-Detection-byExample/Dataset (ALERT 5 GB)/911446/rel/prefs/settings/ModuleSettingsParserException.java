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

package nz.org.venice.prefs.settings;

/**
 * An exception raised when there is an error parsing Settings.
 *
 * @author Mark Hummel
 * @see ModuleFrameSettingsReader
 * @see ModuleFrameSettingsWriter
 * @see nz.org.venice.portfolio.PortfolioParserException
 * @see nz.org.venice.table.WatchScreenParserException
 */
public class ModuleSettingsParserException extends Throwable {

    /**
     * Create a new ModuleSettings parser exception.
     *
     * @param message a message describing the error.
     */
    public ModuleSettingsParserException(String message) {
        super(message);
    }
}