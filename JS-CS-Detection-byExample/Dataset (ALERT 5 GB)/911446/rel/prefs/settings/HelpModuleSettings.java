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
 * This class represents HelpModule data which can restore Help modules upon restart. 
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
import nz.org.venice.help.HelpModule;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.prefs.PreferencesException;

public class HelpModuleSettings extends AbstractSettings {
    

    private int positionInStack; 
    private Stack visitedPages;

    /**
     * Default HelpModuleSettings constructor
     */

    public HelpModuleSettings() {
	super(Settings.TABLE, Settings.HELPMODULE);
    }

    /**
     * Create a HelpModuleSettings object using title as key
     * 
     * @param  title  The title of the HelpModule
     */

    public HelpModuleSettings(String title) {
	super(Settings.TABLE, Settings.HELPMODULE);
	super.setTitle(title);
    }

    /**
     * 
     * Return a stack of visitedPages
     * 
     * @return A stack representing help pages visited
     */

    public Stack getVisitedPages() {
	return visitedPages;
    }

    /**
     * Set the stack of help pages visited.
     * 
     * @param visitedPages  A stack
     */

    public void setVisitedPages(Stack visitedPages) {
	this.visitedPages = visitedPages;
    }

    /**
     * 
     * Return the position in  a stack of visited pages
     * 
     * @return  The position in the stack
     */

    public int getPositionInStack() {
	return positionInStack;
    }

    /**
     *
     * Set the position in a stack of visited pages
     * 
     * @param positionInStack  An index to the stack
     */

    public void setPositionInStack(int positionInStack) {
	this.positionInStack = positionInStack;
    }

    /**
     * 
     * Return a HelpModule based on this Settings object
     * 
     * @param  desktop   The Venice desktop
     * @return A HelpModule
     */

    public Module getModule(JDesktopPane desktop) {	
	return new HelpModule(desktop, this);
    }
}