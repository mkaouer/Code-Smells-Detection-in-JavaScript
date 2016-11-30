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
 * This class represents quote module data which can restore Quote modules upon restart. 
 * 
 * @author Mark Hummel
 * @see PreferencesManager
 * @see SettingsWriter
 * @see SettingsReader 
 * @see Settings
*/

import javax.swing.JDesktopPane;
import java.util.*;
import nz.org.venice.main.Module;
import nz.org.venice.table.QuoteModule;
import nz.org.venice.quote.EODQuoteBundle;
import nz.org.venice.quote.EODQuoteRange;
import nz.org.venice.quote.QuoteSourceManager;
import nz.org.venice.table.WatchScreen;
import nz.org.venice.util.TradingDate;

import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.prefs.PreferencesException;
import nz.org.venice.prefs.settings.SettingsWriter;

public class QuoteModuleSettings extends AbstractSettings {
    
    private EODQuoteBundle quoteBundle;
    private boolean singleDate;

    /**
     *
     * QuoteModuleSettings default constructor
     */
    
    public QuoteModuleSettings() {
	super(Settings.TABLE, Settings.QUOTEMODULE);	
    }

    /**
     * 
     * Set the quoteBundle for these Settings
     * 
     * @param quoteBundle  The quoteBundle of a QuoteModule
     */
    
    public void setQuoteBundle(EODQuoteBundle quoteBundle) {
	this.quoteBundle = quoteBundle;
    }

    /**
     * 
     * Return the quoteBundle of these Settings
     * 
     * @return A quoteBundle of a QuoteModule
     */
    public EODQuoteBundle getQuoteBundle() {
	return quoteBundle;
    }

    /**
     * 
     * Return the singleDate flag
     * 
     * @return The singleDate flag
     */
   
    public boolean getSingleDate() {
	return singleDate;
    }
    

    /**
     * 
     * Set the singleDate flag
     * 
     * @param singleDate  Wether the quoteModule consists of a single date
     */
    public void setSingleDate(boolean singleDate) {
	this.singleDate = singleDate;
    }    
    
    /**
     * 
     * Return a QuoteModule based on these settings
     * 
     * @return A QuoteModule
     */
    public Module getModule(JDesktopPane desktop) {	
	return new QuoteModule(quoteBundle, singleDate);		
    }

}