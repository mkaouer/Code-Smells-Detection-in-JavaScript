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
 * This class represents AnalyserModule data which can be used to restore analyser modules upon restart. 
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
import nz.org.venice.analyser.PaperTradeModule;
import nz.org.venice.analyser.PaperTradeResult;
import nz.org.venice.analyser.GPModule;
import nz.org.venice.analyser.GAModule;
import nz.org.venice.analyser.ANNModule;

public class AnalyserModuleSettings extends AbstractSettings {
        
    /**
     * 
     * Create a new AnalyserModuleSettings object
     * 
     * @param type  The type of the specific AnalyserModule
     */

    public AnalyserModuleSettings(int type) {
	super(Settings.ANALYSER, type);	
    }

    /**
     * 
     * Return an AnalyserModule of the settings type based on these
     * settings.
     * 
     * @return  An Analyser Module 
     */
        
    public Module getModule(JDesktopPane desktop) {		
	switch (getType()) {
	case Settings.PAPERTRADEMODULE:	    	
	    return new PaperTradeModule(desktop);
	
	case Settings.GPMODULE:	    	
	    return new GPModule(desktop);
	    
	case Settings.GAMODULE:	    	
	    return new GAModule(desktop);
	
	case Settings.ANNMODULE:	    	
	    return new ANNModule(desktop);
	}	
	return null;
    }    
}