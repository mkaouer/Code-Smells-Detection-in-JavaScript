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

package org.mov.main;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.mov.macro.MacroManager;
import org.mov.prefs.PreferencesManager;
import org.mov.quote.IDQuoteSync;
import org.mov.quote.QuoteSourceManager;
import org.mov.quote.Symbol;
import org.mov.quote.SymbolFormatException;
import org.mov.ui.GPLViewDialog;
import org.mov.ui.DesktopManager;
import org.mov.ui.MainMenu;
import org.mov.util.Locale;

/**
 * The top level class which contains the main() function. This class builds
 * the outer frame and creates the desktop.
 *
 * @author Andrew Leppard
 */
public class Main extends JFrame {

    private JDesktopPane desktop;
    private DesktopManager desktopManager;
    private PreferencesManager.DisplayPreferences displayPreferences;

    private static Main venice;

    /** Short version string, e.g. "0.1a" */
    public static String SHORT_VERSION = "0.6b";

    /** Longer version string, e.g. "0.1 alpha" */
    public static String LONG_VERSION = "0.6 beta";

    /** Release date, e.g. 13/Jan/2003 */
    public static String RELEASE_DATE = "23/" + Locale.getString("OCT") + "/2005";

    /** Copyright date range, e.g. "2003-5" */
    public static String COPYRIGHT_DATE_RANGE = "2003-5";

    /**
     * Get the main frame for the current application
     * @return The frame
     */
    public static JFrame getApplicationFrame() {
	return Main.venice;
    }

    // Go!
    private Main() {
        // Set the preferred language if any is defined as preferred,
        // otherwise setLocale gets the current language from the system.
        Locale.setLocale();
        // Display a brief copyright message
        String title = (Locale.getString("VENICE_LONG") + ", " + LONG_VERSION + " / " +
			RELEASE_DATE);
        System.out.println(title);
        for(int i = 0; i < title.length(); i++)
            System.out.print("-");
        System.out.println("");
        System.out.println(Locale.getString("COPYRIGHT", COPYRIGHT_DATE_RANGE) + ", " +
			   "Andrew Leppard (aleppard@picknowl.com.au)");
        System.out.println(Locale.getString("SEE_LICENSE"));

	displayPreferences = PreferencesManager.loadDisplaySettings();
	setSize(displayPreferences.width, displayPreferences.height);
	setLocation(displayPreferences.x, displayPreferences.y);

	setTitle(Locale.getString("VENICE_SHORT") + " " + SHORT_VERSION);

	desktop = new JDesktopPane();
	desktopManager = new org.mov.ui.DesktopManager(desktop);
	desktop.setDesktopManager(desktopManager);

        // I didn't mind the blue colour background on the desktop pane
        // under the default steel l&f, but the Windows XP uses a very
        // strong blue colour that looks horrible. So this light green
        // which is the Venice theme will be the default.
        desktop.setBackground(new Color(238, 241, 238));
	CommandManager.getInstance().setDesktopManager(desktopManager);

	// Instantiate main menu singleton
        MainMenu.getInstance(this, desktopManager);

	setContentPane(desktop);
	addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    // User closed window by hitting "X" button
		    saveSettingsAndExit();
		}
		public void windowClosed(WindowEvent e) {
		    // User closed window by selecting exit from the menu
		    saveSettingsAndExit();
		}
	    });
    }

    // Save settings and exit!
    private void saveSettingsAndExit() {
	// Save window dimensions in prefs file
	displayPreferences.x = getX();
	displayPreferences.y = getY();
	displayPreferences.width = getWidth();
	displayPreferences.height = getHeight();
	PreferencesManager.saveDisplaySettings(displayPreferences);

	// Call save() on each module so they can save their
	// preferences data
	desktopManager.save();

        // Shutdown the database if necessary
        QuoteSourceManager.shutdown();

	dispose();	
	System.exit(0);
    }

    /**
     * Start the application. Currently the application ignores all
     * command line arguments.
     */
    public static void main(String[] args) {
	// Set the look and feel to be the default for the current platform
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {
            // Shouldn't happen, but if it does just keep going
        }
        venice = new Main();

        // Temporarily disable functionality if the user has not accepted the license.
        if(PreferencesManager.requireGPLAcceptance())
            MainMenu.getInstance().disableMenus();

        venice.setVisible(true);

        // First make sure user has agreed to GPL. If they do not agree to
        // the license, then quit the application immediately.
        if (PreferencesManager.requireGPLAcceptance()) {
            if(!GPLViewDialog.showGPLAcceptanceDialog()) {
                venice.dispose();
                System.exit(0);
            }

            // Record user's acceptance and re-enable functionality.
            else {
                PreferencesManager.setGPLAcceptance();
                MainMenu.getInstance().enableMenus();
            }
        }

        // Now run Jython start up macros
        try {
            MacroManager.executeStartupMacros();
        } catch (java.lang.NoClassDefFoundError err) {
            System.out.println(Locale.getString("NO_JYTHON_ERROR"));
        }

        // Start up intra-day quote sync
        PreferencesManager.IDQuoteSyncPreferences idQuoteSyncPreferences =
            PreferencesManager.loadIDQuoteSyncPreferences();
        IDQuoteSync.getInstance().setPeriod(idQuoteSyncPreferences.period);

        try {
            List symbols = new ArrayList(Symbol.toSortedSet(idQuoteSyncPreferences.symbols,
                                                            false));
            
            IDQuoteSync.getInstance().addSymbols(symbols);
        } catch(SymbolFormatException e) {
            // Ignore error in preferences
        }

        IDQuoteSync.getInstance().setTimeRange(idQuoteSyncPreferences.openTime,
                                               idQuoteSyncPreferences.closeTime);
        IDQuoteSync.getInstance().setEnabled(idQuoteSyncPreferences.isEnabled);
    }
}



