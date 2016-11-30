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

package org.mov.prefs;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import org.mov.main.*;
import org.mov.prefs.PreferencesManager;

/**
 * The preferences module for venice. This class provides the user
 * interface to change any of the preferences settings. Preferences are
 * organised as a set of pages, each page is responsible for one group
 * of settings.
 * Example:
 * <pre>
 *      // Open a new preferences window displaying the quote source page
 *      PreferencesModule prefs = new PreferencesModule(desktop,
 *				      PreferencesModule.QUOTE_SOURCE_PAGE);
 * 
 *	// Create a frame around the module and add to the desktop
 *	ModuleFrame frame = new ModuleFrame(chart, 0, 0, 400, 300);
 *	desktop.add(frame);
 * </pre>
 *
 * @see PreferencesPage
 */

public class PreferencesModule extends JPanel
    implements Module, ActionListener {
    
    /**
     * Preferences page for retrieving stock quotes.
     */
    
    private Vector pages;
    public static int QUOTE_SOURCE_PAGE = 0;
    
    private JDesktopPane desktop;
    private PropertyChangeSupport propertySupport;
    private PreferencesPage activePage;
    
    private JButton okButton;
    private JButton cancelButton;
    private JSplitPane split;
    
    /**
     * Create a new Preference Module.
     *
     * @param	desktop	the parent desktop
     * @param	initialPage	the initial page to display, so far we only
     *				support 
     */
    public PreferencesModule(JDesktopPane desktop) {
	
	this.desktop = desktop;
	propertySupport = new PropertyChangeSupport(this);       
	
	DefaultListModel pageListModel = new DefaultListModel();
	pages = new Vector();

        pageListModel.addElement((Object)new String("Functions"));
        pages.addElement(new EquationPage(desktop));
        
        pageListModel.addElement((Object)new String("Quote Source"));
	pages.addElement(new QuoteSourcePage(desktop));

	// Only add the skins page if skinlf.jar is available
	if(Main.isSkinsAvailable()) {
	    pageListModel.addElement((Object)new String("Skins"));
	    pages.addElement(new SkinPage(desktop));
	}

	final JList pageList = new JList(pageListModel);

	MouseListener mouseListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
		    int index = pageList.locationToIndex(e.getPoint());
		    pageList.setSelectedIndex(index);
		    activePage = (PreferencesPage) pages.elementAt(index);
		    split.setRightComponent(activePage.getComponent());
		}
	    };
	pageList.addMouseListener(mouseListener);


	pageList.setSelectedIndex(0);
	activePage = (PreferencesPage) pages.elementAt(0);

	//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	setLayout(new BorderLayout());
	add(split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
			   pageList,
			   activePage.getComponent()),
	    BorderLayout.CENTER);

	JPanel buttonPanel = new JPanel();
	okButton = new JButton("OK");
	okButton.addActionListener(this);
	cancelButton = new JButton("Cancel");
	cancelButton.addActionListener(this);
	buttonPanel.add(okButton);
	buttonPanel.add(cancelButton);
  
	add(buttonPanel, BorderLayout.SOUTH);
    }


    /**
     * Overrides the default method.  Determines the preferred size
     * of all page components, rather than just the one to be displayed first
     */
    public Dimension getPreferredSize() {
        Dimension d = new Dimension();
        for(int i = 0; i < pages.size(); i++) {
            JPanel activePage = (JPanel) pages.elementAt(i);
            if (activePage.getPreferredSize().width > d.width)
                d.width = activePage.getPreferredSize().width;
            
            if (activePage.getPreferredSize().height > d.height)
                d.height = activePage.getPreferredSize().height;
        }

        d.width += okButton.getPreferredSize().width;
        d.height += okButton.getPreferredSize().height;

        d.width += cancelButton.getPreferredSize().width;
        d.height += cancelButton.getPreferredSize().height;
        
        return d;
    }

    
    /**
     * Called when the user clicks on the save or cancel button.
     *
     * @param	e	The event.
     */
    public void actionPerformed(ActionEvent e) {

	if(e.getSource() == okButton) {
	    // Save preference data - currently theres only one and its
	    // the active page - so save it.
	    activePage.save();

	    // flush changes to backing store
	    try {
		PreferencesManager.userRoot().flush();
	    }
	    catch(Exception be) {
		// ignore it
	    }
	}

	// ok or cancel button closes window
	propertySupport.
	    firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
    }

    /**
     * Add a property change listener for module change events.
     *
     * @param	listener	listener
     */
    public void addModuleChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a property change listener for module change events.
     *
     * @param	listener	listener
     */
    public void removeModuleChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    /**
     * Return displayed component for this module.
     *
     * @return the component to display.
     */
    public JComponent getComponent() {
	return this;
    }

    /**
     * Return menu bar for quote source preferences module.
     *
     * @return	the menu bar.
     */
    public JMenuBar getJMenuBar() {
	return null;
    }

    /**
     * Return frame icon for quote source preferences module.
     *
     * @return	the frame icon.
     */
    public ImageIcon getFrameIcon() {
	return null;
    }

    /**
     * Returns the window title.
     *
     * @return	the window title.
     */
    public String getTitle() {
	return "Application Preferences";
    }

    /**
     * Return whether the module should be enclosed in a scroll pane.
     *
     * @return	enclose module in scroll bar
     */
    public boolean encloseInScrollPane() {
	return false;
    }

    /**
     * Called when window is closing. We handle the saving explicitly so
     * this is only called when the user clicks on the close button in the
     * top right hand of the window. Dont trigger a save event for this.
     *
     * @return	enclose module in scroll bar
     */
    public void save() {
	// Same as hitting cancel - do not save anything
    }
}
