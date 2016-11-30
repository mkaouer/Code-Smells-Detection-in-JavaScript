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

package org.mov.ui;


import java.net.URL;
import java.util.prefs.Preferences;
import javax.swing.*;

import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.CompoundSkin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;
import com.l2fprod.util.*;

import org.mov.main.Main;
import org.mov.prefs.PreferencesManager;
/**
 * This class is responsible for the loading of UI skins 
 */
public class SkinManager {
    /** Can be called from anywhere to update the current skin to the most recent settings */
    public static void loadSkin() {
	try {
	    Preferences p = PreferencesManager.getUserNode("/display/skin");
	    /* Possible skin types:
	       none - No skin
	       pack - l2fprod.com Theme Pack skin
               x11  - KDE or GTK theme
	    */
	    boolean skin_loaded = false;
	    String skin_type = p.get("skin_type", "none");
	    Skin skin = null;
	    
	    if (skin_type.equals("system")) {
		String landf = null;
		String system_skin = p.get("system_skin", "default");
		if (system_skin.equals("default"))
		    landf = UIManager.getSystemLookAndFeelClassName();
		else if (system_skin.equals("metal"))
		    landf = UIManager.getCrossPlatformLookAndFeelClassName();
		else if (system_skin.equals("mac"))
		    landf = "javax.swing.plaf.mac.MacLookAndFeel";
		else if (system_skin.equals("solaris"))
		    landf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		else if (system_skin.equals("win32"))
		    landf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		try {
		    UIManager.setLookAndFeel(landf);
		    SwingUtilities.updateComponentTreeUI(Main.getApplicationFrame());
		} catch (Exception ex) {
		    DesktopManager.showErrorMessage("Unable to load the skin "+landf);
		}
	    } else if (skin_type.equals("pack")) {
		URL pack_URL = null;
		try {	
		    pack_URL = new URL(p.get("pack_file", ""));
		    skin = SkinLookAndFeel.loadThemePack(pack_URL);
		    if (skin == null)
			System.err.println("Unable to load skin file "+pack_URL);
		    else
			skin_loaded = true;
		} catch (Exception e) {
		    System.err.println("Unable to load skin file "+pack_URL);
		}

	    } else if (skin_type.equals("x11")) {
		URL x11_URL_1 = null;
		URL x11_URL_2 = null;

		try {
		    x11_URL_1 = new URL(p.get("x11_file_1", ""));
		} catch (Exception e) {
		    System.err.println("Unable to load primary skin file "+x11_URL_1);
		}

		try {
		    x11_URL_2 = new URL(p.get("x11_file_2", ""));
		} catch (Exception e) {
		    System.err.println("Unable to load secondary skin file "+x11_URL_1);
		}		    

		if (x11_URL_2 == null || x11_URL_2.toString().length() == 0) {
		    skin = SkinLookAndFeel.loadSkin(x11_URL_1);
		    skin_loaded = true;
		} else {
		    // two skins are provided on the command line
		    // create a CompoundSkin.
		    // If a feature is not supported by the first Skin,
		    // the second will be used
		    skin = new CompoundSkin(SkinLookAndFeel.loadSkin(x11_URL_1),
					    SkinLookAndFeel.loadSkin(x11_URL_2));
		    skin_loaded = true;
		}
	    }
	    if (skin_loaded == true) {
		SkinLookAndFeel.setSkin(skin);
		UIManager.setLookAndFeel("com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
		SwingUtilities.updateComponentTreeUI(Main.getApplicationFrame());
	    } 
	    
	} catch (Exception e) {
	    System.err.println("SkinManager Exception: "+e);
	}
    }
}

