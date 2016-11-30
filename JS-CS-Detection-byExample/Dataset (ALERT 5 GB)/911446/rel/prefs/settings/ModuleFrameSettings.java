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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;

import nz.org.venice.util.Locale;
import nz.org.venice.main.Main;
import nz.org.venice.main.Module;
import nz.org.venice.macro.StoredMacro;
import nz.org.venice.quote.Symbol;
import nz.org.venice.quote.SymbolFormatException;
import nz.org.venice.table.WatchScreen;
import nz.org.venice.table.WatchScreenParserException;
import nz.org.venice.table.WatchScreenReader;
import nz.org.venice.table.WatchScreenWriter;

import nz.org.venice.main.ModuleFrame;
import java.util.Collection;
import java.util.Vector;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * This class stores geometry data and a module identifier for saved frames. 
 * 
 * @author Mark Hummel
 * @see ModuleFrameSettingsReader
 * @see ModuleFrameSettingsWriter
 * @see nz.org.venice.prefs.PreferencesManager
   
 */

public class ModuleFrameSettings extends AbstractSettings {
    Rectangle bounds;
    String moduleKey;
    Settings moduleSettings;
    
    
    /**
     *
     * Create new ModuleFrameSettings. 
     * 
     */

    public ModuleFrameSettings() {
	super(Settings.FRAME, Settings.MODULE);
    }

    /**
     *
     * Create new ModuleFrameSettings. 
     * 
     * @param   key     The ModuleFrame Settings Identifier       
     */

    public ModuleFrameSettings(String key) {
	super(Settings.FRAME, Settings.MODULE, key);
    }

    /**
     * Set the ModuleFrame bounds.
     * 
     * @param bounds  The JFrame bounds
     */

    public void setBounds(Rectangle bounds) {
	this.bounds = bounds;
    }

    /**
     * 
     * Set the module of the frame identifier (Deprecated)
     * 
     * @param key   The module identifier
     */

    public void setModuleKey(String key) {
	moduleKey = key;
    }

    public String getModuleKey() {
	return moduleKey;
    }


    /**
     * Return the geometry of the JFrame.
     * 
     * @return  The JFrame bounds  
     */

    public Rectangle getBounds() {
	return bounds;
    }	

    /**
     *  Return the Settings of the module of the frame.
     *
     * @return  The module settings as a Settings object.
     *
     */
    public Settings getModuleSettings() {
	return moduleSettings;
    }

    /**
     *
     * Set the module settings of the module.
     * 
     * @param moduleSettings   The module settings.
     */
    
    public void setModuleSettings(Settings moduleSettings) {
	this.moduleSettings = moduleSettings;
    }
    
    /**
     *
     * Return a SettingsWriter to write the settings.
     *
     */

}