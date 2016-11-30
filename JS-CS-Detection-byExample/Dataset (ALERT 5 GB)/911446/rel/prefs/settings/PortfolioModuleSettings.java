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
 * This class represents PortfolioModule data which can restore Portfolio modules upon restart. 
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
import nz.org.venice.portfolio.PortfolioModule;
import nz.org.venice.portfolio.Portfolio;
import nz.org.venice.quote.QuoteBundle;
import nz.org.venice.quote.EODQuoteRange;
import nz.org.venice.quote.EODQuoteBundle;
import nz.org.venice.quote.QuoteSourceManager;
import nz.org.venice.util.TradingDate;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.prefs.PreferencesException;

public class PortfolioModuleSettings extends AbstractSettings {

    Portfolio portfolio;
    EODQuoteBundle quoteBundle;
    
    /**
     *
     * PortfolioModuleSettings default constructor
     */

    public PortfolioModuleSettings() {
	super(Settings.PORTFOLIO, Settings.PORTFOLIOMODULE);
    }

    /**
     * Construct a PortfolioModuleSettings with title as key
     * 
     * @param title  The Title of the PortfolioModule
     */
    public PortfolioModuleSettings(String title) {
	super(Settings.PORTFOLIO, Settings.PORTFOLIOMODULE);
	super.setTitle(title);
    }

    /**
     *
     * Set the quoteBundle for the PortfolioModule Settings
     * 
     * @param quoteBundle  The quoteBundle of the PortfolioModule
     */
    public void setQuoteBundle(EODQuoteBundle quoteBundle) {
	this.quoteBundle = quoteBundle;
    }

    /**
     * 
     * Get the quoteBundle from the PortfolioModuleSettings
     * 
     * @return the quoteBundle of a PortfolioModule
     */
    public EODQuoteBundle getQuoteBundle() {
	return quoteBundle;
    }

    /**
     * 
     * Set the Portfolio for the PortfolioModuleSettings
     * 
     * @param portfolio  The portfolio of the PortfolioModule 
     */
    public void setPortfolio(Portfolio portfolio) {
	this.portfolio = portfolio;
    }

    /**
     * 
     * Get the Portfolio of a PortfolioModule
     * 
     * @return  portfolio  A portfolio   
     */

    public Portfolio getPortfolio() {
	return portfolio;
    }

    /**
     * 
     * Return a PortfolioModule based on the PortfolioModuleSettings
     * 
     * @param desktop The Venice  desktop
     * @return A PortfolioModule
     * 
     */

    public Module getModule(JDesktopPane desktop) {

	try {
	    portfolio = PreferencesManager.getPortfolio(getTitle());
	    
	    TradingDate lastDate = QuoteSourceManager.getSource().getLastDate();
	    
	    if (lastDate != null) {	
		
		EODQuoteRange quoteRange = 
		    new EODQuoteRange(portfolio.getStocksHeld(),
				       lastDate.previous(1),
				       lastDate);
		
		quoteBundle = new EODQuoteBundle(quoteRange);
		
		setQuoteBundle(quoteBundle);
		setPortfolio(portfolio);

		return new PortfolioModule(desktop, this);
	    }	    	    
	} catch (PreferencesException pfe) {
	}	
	return null;
    }

}