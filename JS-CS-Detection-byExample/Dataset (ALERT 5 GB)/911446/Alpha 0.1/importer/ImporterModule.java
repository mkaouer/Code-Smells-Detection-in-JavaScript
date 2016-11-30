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

package org.mov.importer;

import org.mov.main.ModuleFrame;
import org.mov.main.Module;
import org.mov.util.*;
import org.mov.prefs.PreferencesManager;
import org.mov.quote.*;
import org.mov.ui.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * The importer module for venice which allows importing of quotes from
 * files or the internet. It provides an interface to allow the user
 * to perform a variety of quote imports. Currently quotes can be
 * imported from the internet, a range of quote file formats and can
 * be imported to a database or another quote file.
 */
public class ImporterModule extends JPanel
    implements Module, ActionListener {

    private JDesktopPane desktop;
    private PropertyChangeSupport propertySupport;

    // Import From
    private JRadioButton fromDatabase;
    private JRadioButton fromFiles;
    private JComboBox formatComboBox;
    private JRadioButton fromInternet;
    private JComboBox yearComboBox;

    // Import To
    private JCheckBox toFiles;
    private JCheckBox toDatabase;
    private JTextField toFileName;

    // Buttons
    private JButton importButton;
    private JButton cancelButton;

    // Importing into database
    private DatabaseQuoteSource databaseSource = null;

    /**
     * Create a new Importer Module.
     *
     * @param	desktop	the parent desktop
     */
    public ImporterModule(JDesktopPane desktop) {

	this.desktop = desktop;
	propertySupport = new PropertyChangeSupport(this);

	setLayout(new BorderLayout());

	Preferences p = PreferencesManager.getUserNode("/import_quotes");
	String importFromSource = p.get("from", "internet");

	Box importOptions = Box.createVerticalBox();

	// Import From
	{
	    TitledBorder titled = new TitledBorder("Import From");
	    JPanel importFromPanel = new JPanel();
	    importFromPanel.setBorder(titled);

	    GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    importFromPanel.setLayout(gridbag);

	    c.weightx = 1.0;
	    c.ipadx = 5;
	    c.anchor = GridBagConstraints.WEST;

	    // From Database
	    fromDatabase = new JRadioButton("Database");
	    fromDatabase.addActionListener(this);
	    if(importFromSource.equals("database"))
		fromDatabase.setSelected(true);
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    gridbag.setConstraints(fromDatabase, c);
	    importFromPanel.add(fromDatabase);

	    // From Files
	    fromFiles = new JRadioButton("Files");
	    fromFiles.addActionListener(this);
	    if(importFromSource.equals("files"))
		fromFiles.setSelected(true);
	    c.gridwidth = 1;
	    gridbag.setConstraints(fromFiles, c);
	    importFromPanel.add(fromFiles);

	    formatComboBox = new JComboBox();
	    formatComboBox.addActionListener(this);
	    Vector formats = QuoteFilterList.getInstance().getList();
	    Iterator iterator = formats.iterator();
	    QuoteFilter filter;
	    String selectedFilter = p.get("fileFilter", "MetaStock");

	    while(iterator.hasNext()) {
		filter = (QuoteFilter)iterator.next();
		formatComboBox.addItem(filter.getName());
                if(filter.getName().equals(selectedFilter))
                    formatComboBox.setSelectedItem((Object)filter.getName());
	    }

	    c.gridwidth = GridBagConstraints.REMAINDER;
	    gridbag.setConstraints(formatComboBox, c);
	    importFromPanel.add(formatComboBox);

	    // From Internet

            /*
	    fromInternet = new JRadioButton("Internet");
	    fromInternet.addActionListener(this);
	    if(importFromSource.equals("internet"))
		fromInternet.setSelected(true);
	    c.gridwidth = 1;
	    gridbag.setConstraints(fromInternet, c);
	    importFromPanel.add(fromInternet);

	    yearComboBox = new JComboBox();
	    yearComboBox.addActionListener(this);
	    // Add years from 1901 to preset
	    TradingDate today = new TradingDate();
	    int thisYear = today.getYear();
	    for(int year = 1901; year <= thisYear; year++) {
		yearComboBox.addItem("Quotes From " + Integer.toString(year));
	    }
	    yearComboBox.addItem("Latest Quotes");
	    yearComboBox.setSelectedItem(p.get("internetYear",
					       "Latest Quotes"));

	    c.gridwidth = GridBagConstraints.REMAINDER;
	    gridbag.setConstraints(yearComboBox, c);
	    importFromPanel.add(yearComboBox);
            */


	    // Put all "import from" radio buttons into group
	    ButtonGroup group = new ButtonGroup();
	    group.add(fromDatabase);
	    group.add(fromFiles);
            //	    group.add(fromInternet);

	    importOptions.add(importFromPanel);
	}

	// Import to
	{
	    TitledBorder titled = new TitledBorder("Import To");
	    JPanel importToPanel = new JPanel();
	    importToPanel.setBorder(titled);

	    GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    importToPanel.setLayout(gridbag);

	    c.weightx = 1.0;
	    c.ipadx = 5;
	    c.anchor = GridBagConstraints.WEST;

	    // To Database
	    toDatabase = new JCheckBox("Database");
	    toDatabase.addActionListener(this);
	    if(p.getBoolean("toDatabase", false))
		toDatabase.setSelected(true);
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    gridbag.setConstraints(toDatabase, c);
	    importToPanel.add(toDatabase);

	    // To Files
	    toFiles = new JCheckBox("Files");
	    toFiles.addActionListener(this);
	    if(p.getBoolean("toFiles", false))
		toFiles.setSelected(true);
	    c.gridwidth = 1;
	    gridbag.setConstraints(toFiles, c);
	    importToPanel.add(toFiles);

	    toFileName = new JTextField(p.get("toFileName", ""), 15);
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    gridbag.setConstraints(toFileName, c);
	    importToPanel.add(toFileName);

	    importOptions.add(importToPanel);	
	}

	add(importOptions, BorderLayout.CENTER);

	// Import, Cancel buttons
	JPanel buttonPanel = new JPanel();
	importButton = new JButton("Import");
	importButton.addActionListener(this);
	cancelButton = new JButton("Cancel");
	cancelButton.addActionListener(this);
	buttonPanel.add(importButton);
	buttonPanel.add(cancelButton);

	add(buttonPanel, BorderLayout.SOUTH);

	// Make sure the appropriate buttons are enabled and the others
	// are disabled
	checkDisabledStatus();
    }

    // Enable/disable the appropriate widgets depending on which widgets
    // are checked.
    private void checkDisabledStatus() {

	// Cant import from database to database
	toDatabase.setEnabled(!fromDatabase.isSelected());

	// File format is only applicable if importing from files and not
	// to files (where the format is fixed)
	formatComboBox.setEnabled(fromFiles.isSelected() &&
				  !toFiles.isSelected());

	// Year is only specified if importing from the internet
        //	yearComboBox.setEnabled(fromInternet.isSelected());

	// Destination file name is only specified if importing to files
	// and not importing from files
	toFileName.setEnabled(toFiles.isSelected() &&
			      !fromFiles.isSelected());

	// Import button is only enabled if the user has selected at least
	// one destination
	importButton.setEnabled(toFiles.isSelected() ||
				(toDatabase.isSelected() &&
				 toDatabase.isEnabled()));
    }

    /**
     *  This is called when one of the buttons is pressed
     */
    public void actionPerformed(ActionEvent e) {

	// Make sure the appropriate widgets are disabled if they are
	// not in use
	if(e.getSource() == fromFiles ||
           //	   e.getSource() == fromInternet ||
	   e.getSource() == fromDatabase ||	
	   e.getSource() == toDatabase ||
	   e.getSource() == toFiles) {	
	    checkDisabledStatus();
	}
	else if(e.getSource() == cancelButton) {
	    // Tell frame we want to close
	    propertySupport.
		firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
	}
	else if(e.getSource() == importButton) {

	    saveConfiguration();

	    // Close frame before doing import - we just want the progress bar
	    // visible
	    propertySupport.
		firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);

	    // Performing the quote import in a separate thread will
	    // prevent the application appearing to "lock up"
	    Thread importQuotes = new Thread() {		
		    public void run() {
			importQuotes();
		    }
		};	
	    importQuotes.start();
	}
    }

    // Import quotes
    private void importQuotes() {
        QuoteSource source = null;
	Vector dates = new Vector();

	// If we are importing from files we'll need to open a dialog
	if(fromFiles.isSelected()) {
	    // Get files user wants to import
	    JFileChooser chooser = new JFileChooser();
	    chooser.setMultiSelectionEnabled(true);
	    int action = chooser.showOpenDialog(desktop);

	    if(action == JFileChooser.APPROVE_OPTION) {
		File files[] = chooser.getSelectedFiles();
		Vector fileNames = Converter.toFileNameVector(files);

		// Cancel if no files were selected (one day = one file)
		if(fileNames.size() != 0) {

                    // Format is one in combo box - unless its disable
                    // which means we honour the format choosen in the
                    // user's preferences.
                    String format;
                    if(formatComboBox.isEnabled())
                        format = (String)formatComboBox.getSelectedItem();
                    else {
                        Preferences p =
                            PreferencesManager.getUserNode("/quote_source/files");
                        format = p.get("format", "MetaStock");
                    }

                    source = new FileQuoteSource(format, fileNames);
                    performImport(source);
                }
	    }
	}

	// Importing from the net

        /*
	else if(fromInternet.isSelected()) {
	    // Create dates array from combo box
	    String start = (String)yearComboBox.getSelectedItem();
	    TradingDate startDate = null;

	    // Otherwise go from the last day in the current qoute source
	    if(start.equals("Latest Quotes")) {
		if(toDatabase.isSelected()) {
		    QuoteSource databaseSource =
			QuoteSourceManager.createDatabaseQuoteSource();
		    startDate = databaseSource.getLastDate();
		}

		if(toFiles.isSelected()) {
		
		    TradingDate fileStartDate;
		    QuoteSource fileQuoteSource =
			QuoteSourceManager.createFileQuoteSource();

		    fileStartDate = fileQuoteSource.getLastDate();

		    // Pick the earliest of the two dates
		    if(fileStartDate != null &&
		       fileStartDate.before(startDate))
		    	startDate = fileStartDate;
		}

		// Increment start date to go past last date in data source
		startDate = startDate.next(1);
	    }

	    else {
		// Year is in last 4 characters
		String yearString =
		    start.substring(start.length() - 4,
				    start.length());

		startDate = new TradingDate(Integer.parseInt(yearString),
					    1, 1);
	    }

	    // End date is yesterday
	    TradingDate endDate = new TradingDate();
	    endDate = endDate.previous(1);

	    // Get vector of all trading dates inbetween
	    dates = Converter.dateRangeToTradingDateVector(startDate,
							   endDate);

	    assert false;
	    source = null;
	    //	    source = QuoteSourceManager.createInternetQuoteSource();
	}
        */

	// Or database
	else {
	    source = QuoteSourceManager.createDatabaseQuoteSource();

	    // Export all dates in database
            performImport(source);
	}
    }

    // Perform actual import given source and/or file list
    private void performImport(QuoteSource source) {

	Thread thread = Thread.currentThread();

        // Get a list of all dates between the first and last
        Vector dateRange =
            Converter.dateRangeToTradingDateVector(source.getFirstDate(),
                                                   source.getLastDate());

        // Now set up progress dialog to display date by date progress
        ProgressDialog progress = ProgressDialogManager.getProgressDialog();
        progress.setIndeterminate(false);
        progress.setMaximum(dateRange.size());
        progress.setProgress(0);
        progress.setMaster(true);
        progress.show("Importing");

	boolean isToFiles = toFiles.isSelected() && toFiles.isEnabled();
	boolean isToDatabase = toDatabase.isSelected() && toDatabase.isEnabled();

        // Set the application quote's source to be the quotes we are about
        // to import
        QuoteSourceManager.setSource(source);

	// Import a day at a time
        for(Iterator iterator = dateRange.iterator(); iterator.hasNext();) {
	    TradingDate date = (TradingDate)iterator.next();

            // Check to see if the source has any quotes for that date...
            if(source.containsDate(date)) {

                progress.setNote("Importing " + date.toString("d?/m?/yyyy"));

                // Load the next day's quotes to import - but only if we are
                // importing to/from the database, otherwise there's no point since
                // we would only copy the file name. 
                QuoteBundle quoteBundle = null;

                if(isToDatabase || fromDatabase.isSelected())
                    quoteBundle = 
                        new QuoteBundle(new QuoteRange(QuoteRange.ALL_SYMBOLS, date));

                // file -> file
                if(fromFiles.isSelected() && isToFiles) {
                    FileQuoteSource fileQuoteSource =
                        (FileQuoteSource)source;
                    importFileToFile(fileQuoteSource.
                                     getFileForDate(date));
                }			

                // anything but file -> file
                if(!fromFiles.isSelected() && isToFiles) {
                    importToFile(quoteBundle, date);
                }

                // anything -> database
                if(isToDatabase)
                    importToDatabase(quoteBundle, date);

                // Stop if the user hit cancel
                if(thread.isInterrupted())
                    break;
            }

            progress.increment();
	}

        // Now remove our temporary quote source and let the next access
        // to the quote source access the user's preferred quote source.
	QuoteSourceManager.flush();	

	ProgressDialogManager.closeProgressDialog(progress);	
    }

    // Import file name containing a day's quotes into the file list
    private void importFileToFile(String fileName) {
	// Get list of files
	Vector fileList = getFileList();

	// Add file if its not already there
	Iterator iterator = fileList.iterator();
	String traverseFileName;

	while(iterator.hasNext()) {
	    traverseFileName = (String)iterator.next();
	    if(traverseFileName.equals(fileName))
		return; // exit its already there
	}

	// If we got here its not so add it
	fileList.add((Object)fileName);

	// Save
	putFileList(fileList);
    }

    // Create a file containing the given day's quotes and import the file name
    // into file list.
    private void importToFile(QuoteBundle quoteBundle, TradingDate date) {

	// Create file name to store file quotes in
	String template = toFileName.getText(); // get file name format
	String fileName = date.toString(template); // insert date

	// Get filter we are using
	Preferences p = PreferencesManager.getUserNode("/quote_source/files");
	String format = p.get("format", "MetaStock");
	QuoteFilter filter = QuoteFilterList.getInstance().getFilter(format);

	// Create file and write quotes
	try {
	    FileWriter fileOut = new FileWriter(fileName);
	    PrintWriter out = new PrintWriter(new BufferedWriter(fileOut));

	    // Iterate through stocks printing them to file
            for(Iterator iterator = quoteBundle.iterator(); iterator.hasNext();) {
                Quote quote = (Quote)iterator.next();
		out.println(filter.toString(quote));
            }

	    out.close();

            // Finally add the file we just created to the list of files
            importFileToFile(fileName);
	}
	catch(java.io.IOException e) {
	    org.mov.ui.DesktopManager.
		showErrorMessage("Error writing to file: " +
				 fileName);
	}
    }

    // Import a day's quotes into the database
    private void importToDatabase(QuoteBundle quoteBundle, TradingDate date) {

	Preferences p = PreferencesManager.getUserNode("/quote_source/database");
	String databaseName = p.get("dbname", "shares");

	if(databaseSource == null)
	    databaseSource = QuoteSourceManager.createDatabaseQuoteSource();

	databaseSource.importQuotes(databaseName, quoteBundle, date);
    }

    // Save the configuration on screen to the preferences file
    private void saveConfiguration() {
	Preferences p = PreferencesManager.getUserNode("/import_quotes");

	// Import From
	if(fromDatabase.isSelected())
	    p.put("from", "database");
	else if(fromFiles.isSelected())
	    p.put("from", "files");
	else
	    p.put("from", "internet");
        //	p.put("internetYear", (String)yearComboBox.getSelectedItem());
	p.put("fileFilter", (String)formatComboBox.getSelectedItem());

	// Import To
	p.putBoolean("toDatabase", toDatabase.isSelected());
	p.putBoolean("toFiles", toFiles.isSelected());
	p.put("toFileName", toFileName.getText());
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
	return "Import Quotes";
    }

    /**
     * Return whether the module should be enclosed in a scroll pane.
     *
     * @return	enclose module in scroll bar
     */
    public boolean encloseInScrollPane() {
	return true;
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

    /**
     * Retreive the user's selection of files into the preferences structure.
     *
     * @return	a vector of strings containing quote file names
     */
    public static Vector getFileList() {
	Preferences p = PreferencesManager.getUserNode("/quote_source/files");

	// Files are stored in the nodes list1, list2, list3 etc - the
	// maximum size of each list is Preferences.MAX_VALUE_LENGTH so
	// we need to 'paste' together all the parts
	boolean complete = false;
	String fileList = "";
	String partialFileList;
	int bundleNumber = 1;

	while(!complete) {
	    partialFileList = p.get("list" +
				    Integer.toString(bundleNumber), "");

	    if(partialFileList.length() > 1) {
		fileList = fileList.concat(partialFileList);
	    }
	    else
		complete = true; // done!

	    bundleNumber++;
	}

	// Now split the comma separated entries
	String[] fileNames = fileList.split(", ");

	Vector fileVector = new Vector();
	for(int i = 0; i < fileNames.length; i++) {
	    if(fileNames[i].length() > 0)
		fileVector.add(fileNames[i]);
	}

	return fileVector;
    }

    /**
     * Save the user's selection of files into the preferences structure.
     *
     * @param	fileVector	a vector of strings containing quote file
     *				names
     */
    public static void putFileList(Vector fileVector) {
	// First convert the vector into a comma separated string of
	// all the file names
	String fileList = "";
	String fileName;
	Iterator iterator = fileVector.iterator();

	while(iterator.hasNext()) {
	    fileName = (String)iterator.next();

	    if(fileList.length() > 1)
		fileList = fileList.concat(", ");

	    fileList = fileList.concat(fileName);
	}

	// Now split up the string into bundles that can fit into the
	// preferences structure and write them to the preferences
	// structure
	Preferences p = PreferencesManager.getUserNode("/quote_source/files");
	
	int beginBundle = 0;
	int endBundle;
	int bundleNumber = 1;
	final int bundleSize = Preferences.MAX_VALUE_LENGTH - 1;
	String bundle;

	while(beginBundle < fileList.length()) {
	    endBundle = beginBundle + bundleSize;
	    if(endBundle > fileList.length())
		endBundle = fileList.length();

	    p.put("list" + Integer.toString(bundleNumber),
		  fileList.substring(beginBundle, endBundle));

	    beginBundle += bundleSize;
	    bundleNumber++;
	}

	// Delete any old bundles that might still contain quotes
	while(!p.get("list" + Integer.toString(bundleNumber), "").equals("")) {
	    p.put("list" + Integer.toString(bundleNumber++), "");	
	}
    }
}


