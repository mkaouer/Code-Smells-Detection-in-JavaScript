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
 * This class represents analyser results that were saved when Venice was closed.
 * 
 * @author Mark Hummel
 * @see PreferencesManager
 * @see SettingsWriter
 * @see SettingReader 
*/

import javax.swing.JDesktopPane;
import java.util.*;
import nz.org.venice.main.Module;
import nz.org.venice.analyser.PaperTradeResultModule;
import nz.org.venice.analyser.PaperTradeResult;
import nz.org.venice.analyser.GPResultModule;
import nz.org.venice.analyser.GAResultModule;
import nz.org.venice.analyser.ANNResultModule;


public class AnalyserResultSettings extends AbstractSettings {
        
    private Vector results;

    public AnalyserResultSettings(int type) {
	super(Settings.ANALYSER, type);	
    }
        
    /**
     *
     * Set the analyser results.
     * 
     * 
     */

    /*
      This method copies the list because 
      the results don't get serialized just with
      this.results = results.
     */

    public void setResults(List results) {
	this.results = new Vector();
	Iterator iterator = results.iterator();
	while (iterator.hasNext()) {
	    this.results.add(iterator.next());
	}	
    }

    /**
     *
     * Return the analyser results.
     * 
     * @return The analyser results saved.  
     */

    public List getResults() {
	return results;
    }

    /**
     *
     * Return an analyser results module based on the analyser result settings. 
     */

    public Module getModule(JDesktopPane desktop) {		
	switch (getType()) {
	case Settings.PAPERTRADERESULTS:	    
	    return new PaperTradeResultModule(this);
	
	case Settings.GPRESULTS:	    
	    return new GPResultModule(this);
	
	case Settings.GARESULTS:	    
	    return new GAResultModule(this);
	    
	case Settings.ANNRESULTS:	    
	    return new ANNResultModule(this);
	}	
	return null;
    }
}