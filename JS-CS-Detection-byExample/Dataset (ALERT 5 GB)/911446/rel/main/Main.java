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

package nz.org.venice.main;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.io.*;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.UIManager;

import nz.org.venice.macro.MacroManager;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.prefs.settings.Settings;
import nz.org.venice.prefs.settings.ModuleFrameSettings;
import nz.org.venice.prefs.settings.ModuleFrameSettingsReader;
import nz.org.venice.prefs.settings.ModuleSettingsParserException;
import nz.org.venice.quote.IDQuoteSync;
import nz.org.venice.quote.QuoteSourceManager;
import nz.org.venice.quote.Symbol;
import nz.org.venice.quote.SymbolFormatException;
import nz.org.venice.ui.GPLViewDialog;
import nz.org.venice.ui.DesktopManager;
import nz.org.venice.ui.MainMenu;
import nz.org.venice.ui.ProgressDialog;
import nz.org.venice.ui.ProgressDialogManager;
import nz.org.venice.util.ExchangeRateCache;
import nz.org.venice.util.Locale;
import nz.org.venice.util.VeniceLog;
import nz.org.venice.alert.AlertManager;
    
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
    public static String SHORT_VERSION = "0.751b";

    /** Longer version string, e.g. "0.1 alpha" */
    public static String LONG_VERSION = "0.751 beta";

    /** Release date, e.g. 13/Jan/2003 */
    public static String RELEASE_DATE = "20/" + Locale.getString("JUL") + "/2012";

    /** Copyright date range, e.g. "2003-5" */
    public static String COPYRIGHT_DATE_RANGE = "2003-12";

    /**
     * Get the main frame for the current application
     * @return The frame
     */
    public static JFrame getApplicationFrame() {
	return Main.venice;
    }

    // Set the codepage to get correct console output
    private void setConsoleCodePage() {
	String osName = System.getProperty("os.name");
	String codePage = "";
	if(osName.startsWith("Windows")) codePage = "CP850";  
	else if(osName.startsWith("Mac")) codePage = "UTF-8";
	if(codePage != "") {
	    try {
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out),
					      false, codePage));
		System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), 
					      true, codePage));
	    } catch (Exception e) {
		e.printStackTrace();
	    } 
	}
    }

    // Go!
    private Main() {
        // Set the preferred language if any is defined as preferred,
        // otherwise setLocale gets the current language from the system.
        Locale.setLocale();
	// Set the console code page depending on your operating system.
	setConsoleCodePage();
	// Display a brief copyright message
        String title = (Locale.getString("VENICE_LONG") + ", " + LONG_VERSION + " / " +
			RELEASE_DATE);
        System.out.println(title);
        for(int i = 0; i < title.length(); i++)
            System.out.print("-");
        System.out.println("");
        System.out.println(Locale.getString("COPYRIGHT", COPYRIGHT_DATE_RANGE) + ", " +
			   "Andrew Leppard (andrew venice org nz)");
        System.out.println(Locale.getString("SEE_LICENSE"));

	displayPreferences = PreferencesManager.getDisplaySettings();
	setSize(displayPreferences.width, displayPreferences.height);
	setLocation(displayPreferences.x, displayPreferences.y);

	setTitle(Locale.getString("VENICE_SHORT") + " " + SHORT_VERSION);

	desktop = new JDesktopPane();
	desktopManager = new nz.org.venice.ui.DesktopManager(desktop);
	desktop.setDesktopManager(desktopManager);
        ExchangeRateCache.getInstance().setDesktopPane(desktop);

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

		// Temporarily disable functionality if the user has not accepted the license.
        if(PreferencesManager.getHasGPLAcceptance())
            MainMenu.getInstance().disableMenus();

        setVisible(true);

        // First make sure user has agreed to GPL. If they do not agree to
        // the license, then quit the application immediately.
        if (PreferencesManager.getHasGPLAcceptance()) {
            if(!GPLViewDialog.showGPLAcceptanceDialog()) {
                dispose();
                System.exit(0);
            }

            // Record user's acceptance and re-enable functionality.
            else {
                PreferencesManager.putHasGPLAcceptance();
                MainMenu.getInstance().enableMenus();
            }
        }
	
	//Restore saved windows + state
	//Need to make the frame visible before adding new frames
	setVisible(true);
	restoreSavedFrames();

	CommandManager.getInstance().triggeredAlerts();
	

    }

    // Save settings and exit!
    private void saveSettingsAndExit() {
	// Save window dimensions in prefs file
	displayPreferences.x = getX();
	displayPreferences.y = getY();
	displayPreferences.width = getWidth();
	displayPreferences.height = getHeight();
	PreferencesManager.putDisplaySettings(displayPreferences);

	// Call save() on each module so they can save their
	// preferences data
	desktopManager.save();

        // Shutdown the database if necessary
        QuoteSourceManager.shutdown();

	//Close the log if necessary
	VeniceLog.getInstance().close();

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
	
        // Now run Jython start up macros
        try {
            MacroManager.executeStartupMacros();
        } catch (java.lang.NoClassDefFoundError err) {
            System.out.println(Locale.getString("NO_JYTHON_ERROR"));
        }

        // Start up intra-day quote sync
        PreferencesManager.IDQuoteSyncPreferences idQuoteSyncPreferences =
            PreferencesManager.getIDQuoteSyncPreferences();
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


    /**
     * Restore saved internal frames and their modules, reconstructing their
     position and geometry.

    **/

    private void restoreSavedFrames() {

	Vector savedFrameFiles, dataList;
	Iterator iterator;
	int savedFrames;
	ProgressDialog progress = ProgressDialogManager.getProgressDialog();
	int progressValue = 0;

	if (!PreferencesManager.getRestoreSavedWindowsSetting()) {
	    ProgressDialogManager.closeProgressDialog(progress);
	    return;
	}
	
	savedFrameFiles = PreferencesManager.getSavedFrames();
	iterator = savedFrameFiles.iterator();
	savedFrames  = savedFrameFiles.size();

	if (savedFrames <= 0) {
	    ProgressDialogManager.closeProgressDialog(progress);
	    return;
	}

	Thread thread = Thread.currentThread();

	progress.show(Locale.getString("RESTORE_SAVED_WINDOWS_PROGRESS"));
	progress.setIndeterminate(false);
	progress.setMaximum(savedFrames);
	progress.setMaster(true);


	/* Make sure the initial desktop has displayed first */
	while (iterator.hasNext()) {
	    if (thread.isInterrupted()) {
		break;
	    }
	    progress.increment();
	    try {

		File savedFrameFile = (File)iterator.next();
		FileInputStream inputStream = new FileInputStream(savedFrameFile);

		try {
		    ModuleFrameSettings newFrameSettings = ModuleFrameSettingsReader.read(inputStream);
		    Settings moduleSettings = newFrameSettings.getModuleSettings();
		    //Recreate the module from settings.
		    Module newModule = moduleSettings.getModule(desktop);

		    //Place it initially at 0,0
		    ModuleFrame newFrame = desktopManager.newFrame(newModule);

		    newFrame.setSizeAndLocation(newFrame, desktop, false, true);
		    newFrame.setBounds(newFrameSettings.getBounds());
		    newFrame.setPreferredSize(newFrameSettings.getBounds().getSize());

		    if (newFrame.getModule().encloseInScrollPane()) {
			newFrameSettings.updateScrollPane(newFrame.getScrollPane());
		    }
		    
		} catch (ModuleSettingsParserException wpe) {
		    continue;
		}
	    } catch (FileNotFoundException fnf) {
		continue;
	    } catch (IOException ioe) {
		continue;
	    }
	}
	ProgressDialogManager.closeProgressDialog(progress);
	if (!thread.isInterrupted()) {
	    PreferencesManager.removeSavedFrames();
	}	
    }
}



