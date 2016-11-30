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

package nz.org.venice.prefs;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import nz.org.venice.main.*;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.prefs.settings.PreferencesModuleSettings;
import nz.org.venice.prefs.settings.Settings;
import nz.org.venice.util.Locale;

/**
 * The preferences module for venice. This class provides the user
 * interface to change any of the preferences settings. Preferences are
 * organised as a set of pages, each page is responsible for one group
 * of settings.
 * Example:
 * <pre>
 *      // Open a new preferences window displaying the quote source page
 *      PreferencesModule prefs = new PreferencesModule(desktop);
 * 
 *	// Create a frame around the module and add to the desktop
 *	ModuleFrame frame = new ModuleFrame(chart, 0, 0, 400, 300);
 *	desktop.add(frame);
 * </pre>
 *
 * @see PreferencesPage
 */

public class PreferencesModule extends JPanel implements Module, ActionListener {
    
    /**
     * Preferences page for retrieving stock quotes.
     */
    
    /** Refers to the stored equaton preferences page */
    public final static int EQUATION_PAGE = 0;

    /** Refers to the quote source preferences page */
    public final static int QUOTE_SOURCE_PAGE = 1;

    /** Refers to the tuning preferences page */
    public final static int TUNING_PAGE = 2;

    /** Refers to the proxy preferences page */
    public final static int PROXY_PAGE = 3;

    /** Refers to the proxy macros page */
    public final static int MACROS_PAGE = 4;

    /** Refers to the language preferences page */
    public final static int LANGUAGE_PAGE = 5;

    /** Refers to the user interface page */
    public final static int USER_INTERFACE = 6;

    private Vector pages;
    private DefaultListModel pageListModel;
    private JDesktopPane desktop;
    private PropertyChangeSupport propertySupport;
    private PreferencesPage activePage;
    
    private JList pageList;
    private JButton okButton;
    private JButton cancelButton;
    private JSplitPane split;

    private Settings settings;
    
    /**
     * Create a new Preference Module loaded with the last viewed page.
     *
     * @param desktop the parent desktop
     */
    public PreferencesModule(JDesktopPane desktop) {
	this(desktop, PreferencesManager.getLastPreferencesPage());
    }

    /**
     * Create a new Preference Module loaded with the given page.
     *
     * @param	desktop	the parent desktop
     * @param page the page to view
     */
    public PreferencesModule(JDesktopPane desktop, int page) {
	
	this.desktop = desktop;
	propertySupport = new PropertyChangeSupport(this);       
	pageListModel = new DefaultListModel();
	pages = new Vector();

	JPanel buttonPanel = new JPanel();
	okButton = new JButton(Locale.getString("OK"));
	okButton.addActionListener(this);
	cancelButton = new JButton(Locale.getString("CANCEL"));
	cancelButton.addActionListener(this);
        
	addPage(new EquationPage(desktop));
	addPage(new LanguagePage(desktop));
	addPage(new MacrosPage(desktop));
        addPage(new ProxyPage(desktop));	
	addPage(new QuoteSourcePage(desktop));	
	addPage(new IndexPreferencesPage(desktop));	
	addPage(new AlertDestinationPage(desktop));
	addPage(new TuningPage(desktop));
	addPage(new UserInterfacePage(desktop));	
	addPage(new ChartPreferencesPage(desktop));

	pageList = new JList(pageListModel);

	pageList.setSelectedIndex(page);
	activePage = (PreferencesPage)pages.elementAt(page);

	pageList.addListSelectionListener(new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent e) {
		    int index = pageList.getSelectedIndex();
		    if(index != -1 && pages.elementAt(index) != activePage) {
			activePage = (PreferencesPage)pages.elementAt(index);
			split.setRightComponent(activePage.getComponent());
		    }
		}
	    });

	setLayout(new BorderLayout());
	add(split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pageList, 
				   activePage.getComponent()),
	    BorderLayout.CENTER);

	buttonPanel.add(okButton);
	buttonPanel.add(cancelButton);
  
	add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addPage(PreferencesPage page) {
	// Add a border with the page's title around each page
	page.getComponent().setBorder(new TitledBorder(page.getTitle()));
        pageListModel.addElement(page.getTitle());
        pages.addElement(page);
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
	    // Save preferences from all pages
	    for(Iterator iterator = pages.iterator(); iterator.hasNext();) {
		PreferencesPage page = (PreferencesPage)iterator.next();
		page.save();
	    }

	    // Flush preference changes to backing store
	    PreferencesManager.flush();
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
	return Locale.getString("PREFERENCES_TITLE");
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
     * Tell module to save any current state data / preferences data because
     * the window is being closed.
     */
    public void save() {
	PreferencesManager.putLastPreferencesPage(pageList.getSelectedIndex());
	settings = new PreferencesModuleSettings();
    }

    public Settings getSettings() {
	return settings;
    }
}
