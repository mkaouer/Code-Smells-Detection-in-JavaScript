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
import javax.swing.JDesktopPane;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;

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

/**
 * This class can save the toplevel Module data which is common to all modules.
 * 
 * @author Mark Hummel
 * @see Settings
 * @see SettingsWriter
*/

public abstract class AbstractSettings implements Settings {

        
    private int group;
    private int type;
    private String name;

    private String key;
    private String title;

    private int hbarValue;
    private int vbarValue;

    private int hbarPolicy;
    private int vbarPolicy;

    public AbstractSettings(int group, int type) {
	this.group = group;
	this.type = type;

	name = this.getClass().getName();
    }

    public AbstractSettings(int group, int type, String key) {
	this.group = group;
	this.type = type;
	this.key = key;	

	name = this.getClass().getName();
	

    }

    /**
     * Set the title of the Module.
     * 
     * @param title The module title
     */

    public void setTitle(String title) {
	this.title = title;
    }

    /**
     *
     * Get the title of the module
     * 
     * @return The module title
     */

    public String getTitle() {
	return title;
    }

    public int getType() {
	return type;
    }

    public int getGroup() {
	return group;
    }

    public void setGroup(int group) {
	this.group = group;
    }

    public void setType(int type) {
	this.type = type;
    }

    public void setKey(String key) {
	this.key = key;
    }

    public String getKey() {
	return key;
    }
    
    public Module getModule(JDesktopPane desktop) {
	return null;
    }

    public String toString() {
	String rv = "Group: " + String.valueOf(group) + 
	    "Type: " + String.valueOf(type) +
	    "Title: " + title;

	return rv;
    }

    public void setHBarValue(int hbarValue) {
	this.hbarValue = hbarValue;
    }

    public int getHBarValue() {
	return hbarValue;
    }

    public void setVBarValue(int vbarValue) {
	this.vbarValue = vbarValue;
    }

    public int getVBarValue() {
	return vbarValue;
    }

    public int getHBarPolicy() {
	return hbarPolicy;
    }

    public void setHBarPolicy(int policy) {
	hbarPolicy = policy;
    }

    public void setVBarPolicy(int policy) {
	vbarPolicy = policy;
    }

    
    public int getVBarPolicy() {
	return vbarPolicy;
    }

    /**
     *
     * Set the scrollPane scrollbar values with this settings scroll bar
     * values 
     * 
     * @param scrollPane  The ModuleFrame scrollPane to update
     */

    public void updateScrollPane(JScrollPane scrollPane) {

	scrollPane.setHorizontalScrollBarPolicy(getHBarPolicy());
	scrollPane.getHorizontalScrollBar().setValue(getHBarValue());	       
	scrollPane.setVerticalScrollBarPolicy(getVBarPolicy());
	scrollPane.getVerticalScrollBar().setValue(getVBarValue());
	
    }
    
    /**
     *
     * Set the Settings scrollbar values from the ModuleFame scrollPane
     *
     * @param scrollPane  The ModuleFrame scrollPane to read scrollbar values from

     */

    public void setScrollBarValues(JScrollPane scrollPane) {

	JScrollBar hbar = scrollPane.getHorizontalScrollBar();
	JScrollBar vbar = scrollPane.getVerticalScrollBar();
	
	setHBarValue(hbar.getValue());
	setHBarPolicy(scrollPane.getHorizontalScrollBarPolicy());
	setVBarValue(vbar.getValue());
	setVBarPolicy(scrollPane.getVerticalScrollBarPolicy());
	
    }
}