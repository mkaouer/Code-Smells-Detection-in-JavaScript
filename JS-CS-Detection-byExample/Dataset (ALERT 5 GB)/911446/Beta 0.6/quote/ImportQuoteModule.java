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

package org.mov.quote;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.mov.main.Module;
import org.mov.main.ModuleFrame;
import org.mov.prefs.ProxyPage;
import org.mov.prefs.PreferencesManager;
import org.mov.ui.DesktopManager;
import org.mov.ui.GridBagHelper;
import org.mov.ui.ProgressDialog;
import org.mov.ui.ProgressDialogManager;
import org.mov.ui.TextViewDialog;
import org.mov.util.Locale;
import org.mov.util.Report;
import org.mov.util.TradingDate;
import org.mov.util.TradingDateFormatException;

/**
 * The import quote module allows importing of quotes into Venice.
 * It provides an interface to allow the user to perform a variety
 * of quote imports. The actual importing is handled by other
 * classes.
 *
 * @author Andrew Leppard
 * @see DatabaseQuoteSource
 * @see ExportQuoteModule
 * @see FileEODQuoteImport
 * @see YahooEODQuoteImport
 */
public class ImportQuoteModule extends JPanel implements Module {

    private JDesktopPane desktop;
    private PropertyChangeSupport propertySupport;

    // Import From
    private JRadioButton fromFiles;
    private JComboBox formatComboBox;
    private JRadioButton fromInternet;
    private JComboBox sourceComboBox;
    private JTextField symbolList;
    private JTextField startDateTextField;
    private JTextField endDateTextField;
    
    // Parsed fields for internet import
    TradingDate startDate;
    TradingDate endDate;
    List symbols;

    // Parsed fields for file import
    EODQuoteFilter filter;
    File files[];

    /**
     * Create a new import quote module.
     *
     * @param desktop the parent desktop
     */
    public ImportQuoteModule(JDesktopPane desktop) {
        this.desktop = desktop;
        propertySupport = new PropertyChangeSupport(this);

        setLayout(new BorderLayout());

        buildGUI();
    }

    /**
     * Layout the user interface.
     */
    private void buildGUI() {
        Preferences p = PreferencesManager.getUserNode("/import_quotes");
        String importFromSource = p.get("from", "internet");
        
        TitledBorder titledBorder = new TitledBorder(Locale.getString("IMPORT_FROM"));
        JPanel titledPanel = new JPanel();
        titledPanel.setBorder(titledBorder);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        titledPanel.setLayout(gridbag);
        
        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;        

        // Import from files
        {
            fromFiles = new JRadioButton(Locale.getString("FILES"));
            fromFiles.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        checkDisabledStatus();
                    }
                });
            
            if(importFromSource.equals("files"))
                fromFiles.setSelected(true);
            c.gridwidth = 1;
            gridbag.setConstraints(fromFiles, c);
            titledPanel.add(fromFiles);
            
            formatComboBox = new JComboBox();
            List formats = EODQuoteFilterList.getInstance().getList();
            String selectedFilter = p.get("fileFilter", "MetaStock");
            
            for(Iterator iterator = formats.iterator(); iterator.hasNext();) {
                EODQuoteFilter filter = (EODQuoteFilter)iterator.next();
                formatComboBox.addItem(filter.getName());
                if(filter.getName().equals(selectedFilter))
                    formatComboBox.setSelectedItem(filter.getName());
            }
            
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(formatComboBox, c);
            titledPanel.add(formatComboBox);        
        }

        // Import from the internet
        {
            fromInternet = new JRadioButton(Locale.getString("INTERNET"));
            fromInternet.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        checkDisabledStatus();
                    }
                });

            if(importFromSource.equals("internet"))
                fromInternet.setSelected(true);
            c.gridwidth = 1;
            gridbag.setConstraints(fromInternet, c);
            titledPanel.add(fromInternet);
            
            sourceComboBox = new JComboBox();
            sourceComboBox.addItem(Locale.getString("YAHOO"));

            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(sourceComboBox, c);
            titledPanel.add(sourceComboBox);
            
            c.gridx = 1;
            symbolList = GridBagHelper.addTextRow(titledPanel, Locale.getString("SYMBOLS"), 
                                                  p.get("internetSymbolList", ""),
                                                  gridbag, c, 11);

            c.gridx = 1;
            TradingDate today = new TradingDate();
            TradingDate previous = today.previous(30);
            
            startDateTextField = GridBagHelper.addTextRow(titledPanel, 
                                                          Locale.getString("START_DATE"),
                                                          p.get("internetStartDate", 
                                                                previous.toString("dd/mm/yyyy")),
                                                          gridbag, c, 11);
            c.gridx = 1;
            endDateTextField = GridBagHelper.addTextRow(titledPanel, 
                                                        Locale.getString("END_DATE"), 
                                                        p.get("internetEndDate",
                                                              today.toString("dd/mm/yyyy")),
                                                        gridbag, c, 11);
        }
        
        // Put all "import from" radio buttons into group
        ButtonGroup group = new ButtonGroup();
        group.add(fromFiles);
        group.add(fromInternet);

        add(titledPanel, BorderLayout.CENTER);

        // Import, Cancel buttons
        JPanel buttonPanel = new JPanel();
        JButton importButton = new JButton(Locale.getString("IMPORT"));
        importButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Import quotes
                    importQuotes();
                }
            });

        JButton cancelButton = new JButton(Locale.getString("CANCEL"));
        cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Tell frame we want to close
                    propertySupport.firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
                }
            });

        buttonPanel.add(importButton);
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Make sure the appropriate buttons are enabled and the others
        // are disabled
        checkDisabledStatus();
    }

    /**
     * Enable/disable the appropriate widgets depending on which widgets
     * are checked.
     */
    private void checkDisabledStatus() {
        // File format is only applicable if importing from files.
        formatComboBox.setEnabled(fromFiles.isSelected());

        // Exchange, symbol list and dates are only applicable if importing
        // from the internet
        sourceComboBox.setEnabled(fromInternet.isSelected());
        symbolList.setEnabled(fromInternet.isSelected());
        startDateTextField.setEnabled(fromInternet.isSelected());
        endDateTextField.setEnabled(fromInternet.isSelected());
    }

    /**
     * Import quotes into venice.
     */
    private void importQuotes() {
        // Only save configuration if user imports with it
        saveConfiguration();

        // Performing the quote import in a separate thread will
        // prevent the application appearing to "lock up"
        Thread thread = new Thread() {		
                public void run() {
                    if(fromFiles.isSelected())
                        importQuotesFromFiles();
                    else {
                        assert fromInternet.isSelected();
                        importQuotesFromInternet();
                    }
                }
            };	
        thread.start();        
    }

    /**
     * Save the configuration on screen to the preferences file
     */
    private void saveConfiguration() {
        Preferences p = PreferencesManager.getUserNode("/import_quotes");
        
        // Import From
        if(fromFiles.isSelected())
            p.put("from", "files");
        else
            p.put("from", "internet");
        
        p.put("internetSymbolList", symbolList.getText());
        p.put("internetStartDate", startDateTextField.getText());
        p.put("internetEndDate", endDateTextField.getText());
        p.put("fileFilter", (String)formatComboBox.getSelectedItem());
    }

    /**
     * Import quotes from files.
     */
    private void importQuotesFromFiles() {
        if(parseFileFields()) {
            Report report = new Report();
            int quotesImported = 0;

            // Get database to import to
            DatabaseQuoteSource database = getDatabaseSource();

            // Tell frame we want to close
            propertySupport.firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);

            // Now set up progress dialog to display the file by file progress
            ProgressDialog progress = ProgressDialogManager.getProgressDialog();
            progress.setIndeterminate(false);
            progress.setMaximum(files.length);
            progress.setProgress(0);
            progress.setMaster(true);
            progress.show(Locale.getString("IMPORTING"));

            // Import a file at a time
            for(int i = 0; i < files.length; i++) {
                File file = files[i];
                
                // Update progress dialog
                progress.setNote(Locale.getString("IMPORTING_FILE", file.getName()));

                // Import quotes from the given file
                quotesImported += importQuotesFromSingleFile(database, report, file);
                
                // Stop if the user hit cancel
                if(Thread.currentThread().isInterrupted())
                    break;
                
                progress.increment();
            }

            QuoteSourceManager.flush();
            ProgressDialogManager.closeProgressDialog(progress);
            displayReport(report, quotesImported);
        }
    }

    /**
     * Import quotes from single file.
     *
     * @param database database to store quotes
     * @param report   report to update
     * @param file     file to import
     * @return number of quotes imported
     */
    private int importQuotesFromSingleFile(DatabaseQuoteSource database, Report report, File file) {
        int quotesImported = 0;
        FileEODQuoteImport importer = new FileEODQuoteImport(report, filter);

        if(importer.open(file)) {
            while(importer.isNext()) {
                List quotes = importer.importNext();

                // Import into database
                if(quotes.size() > 0)
                    quotesImported += database.importQuotes(quotes);
            }

            importer.close();
        }

        if(quotesImported > 0)
            report.addMessage(file.getName() + ": " +
                              Locale.getString("IMPORTED_QUOTES", 
                                               quotesImported));
        return quotesImported;
    }

    /**
     * Parse all the fields for file import.
     *
     * @return <code>true</code> if the fields were successfully parsed, <code>false</code>
     *         otherwise
     */
    private boolean parseFileFields() {
        // Get files user wants to import
        JFileChooser chooser;
        String lastDirectory = PreferencesManager.loadDirectoryLocation("importer");
            
        if(lastDirectory != null)
            chooser = new JFileChooser(lastDirectory);
        else
            chooser = new JFileChooser();
        
        chooser.setMultiSelectionEnabled(true);
        int action = chooser.showOpenDialog(desktop);
            
        if(action == JFileChooser.APPROVE_OPTION) {
            // Remember directory
            lastDirectory = chooser.getCurrentDirectory().getAbsolutePath();
            PreferencesManager.saveDirectoryLocation("importer",lastDirectory);
            
            files = chooser.getSelectedFiles();
            
            // Cancel if no files were selected (one day = one file)
            if(files.length != 0) {
                // Get quote filter format
                String format = (String)formatComboBox.getSelectedItem();
                filter = EODQuoteFilterList.getInstance().getFilter(format);
                return true;
            }
        }

        return false;
    }

    /**
     * Import quotes from the internet.
     */
    private void importQuotesFromInternet() {
        if(parseInternetFields()) {
            Report report = new Report();
            int quotesImported = 0;

            // Get database to import to
            DatabaseQuoteSource database = getDatabaseSource();

            // Tell frame we want to close
            propertySupport.firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);

            // Set up proxy support
            ProxyPage.setupNetworking();

            // Now set up progress dialog to display the symbol by symbol progress
            ProgressDialog progress = ProgressDialogManager.getProgressDialog();
            progress.setIndeterminate(false);
            progress.setMaximum(symbols.size());
            progress.setProgress(0);
            progress.setMaster(true);
            progress.show(Locale.getString("IMPORTING"));

            // Import a symbol at a time
            try {
                for(Iterator iterator = symbols.iterator(); iterator.hasNext();) {

                    Symbol symbol = (Symbol)iterator.next();

                    // Update progress dialog
                    progress.setNote(Locale.getString("IMPORTING_SYMBOL", symbol.toString()));
                    
                    // Load quotes from internet
                    List quotes =
                       YahooEODQuoteImport.importSymbol(report, symbol, startDate, endDate);
                    
                    // Import into database
                    if(quotes.size() > 0) {

                        // remove the symbol argument
                        int symbolQuotesImported = database.importQuotes(quotes);
                        report.addMessage(Locale.getString("YAHOO") + ":" + symbol + ": " +
                                          Locale.getString("IMPORTED_QUOTES", 
                                                           symbolQuotesImported));
                        quotesImported += symbolQuotesImported;
                    }
                 
                    // Stop if the user hit cancel
                    if(Thread.currentThread().isInterrupted())
                        break;
                    
                    progress.increment();
                }
            }

            catch(ImportExportException e) {
                DesktopManager.showErrorMessage(e.getMessage());
            }

            QuoteSourceManager.flush();
            ProgressDialogManager.closeProgressDialog(progress);
            displayReport(report, quotesImported);
        }
    }

    /**
     * Parse all the fields for internet import.
     *
     * @return <code>true</code> if the fields were successfully parsed, <code>false</code>
     *         otherwise
     */
    private boolean parseInternetFields() {
        // Parse symbol list and validate
        try {
            // Don't check that the symbols exist before import. After all
            // they won't at the first import.
            symbols = new ArrayList(Symbol.toSortedSet(symbolList.getText(), false));
        }
        catch(SymbolFormatException e) {
            JOptionPane.showInternalMessageDialog(desktop, 
                                                  e.getMessage(),
                                                  Locale.getString("INVALID_SYMBOL_LIST"),
                                                  JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if(symbols.size() == 0) {
            JOptionPane.showInternalMessageDialog(desktop, 
                                                  Locale.getString("MISSING_SYMBOLS"),
                                                  Locale.getString("INVALID_SYMBOL_LIST"),
                                                  JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Parse dates and validate
        try {
            startDate = new TradingDate(startDateTextField.getText(), TradingDate.BRITISH);
            endDate = new TradingDate(endDateTextField.getText(), TradingDate.BRITISH);
        }
        catch(TradingDateFormatException e) {
            JOptionPane.showInternalMessageDialog(desktop, 
                                                  Locale.getString("ERROR_PARSING_DATE",
                                                                   e.getDate()),
                                                  Locale.getString("INVALID_DATE"),
                                                  JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Make sure the dates are in the correct order!
        if(startDate.after(endDate)) {
            JOptionPane.showInternalMessageDialog(desktop,
                                                  Locale.getString("DATE_RANGE_ERROR"),
                                                  Locale.getString("INVALID_DATE"),
                                                  JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Success
        return true;
    }

    /**
     * Display the import report to the user. Initially just show a simple dialog
     * which describes the number of quotes imported and if there were any
     * warnings or errors and allows the user to view the full report. Display
     * the full report to if the user wishes.
     *
     * @param report the report to display
     * @param quotesImported the number of quotes imported
     */
    private void displayReport(final Report report, final int quotesImported) {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String message = Locale.getString("IMPORTED_QUOTES", quotesImported);
                    
                    if((report.getErrorCount() + report.getWarningCount()) > 0)
                        message = message.concat("\n" +
                                                 Locale.getString("IMPORTED_WARNINGS", 
                                                                  report.getErrorCount(),
                                                                  report.getWarningCount()));
                    
                    // Give the user the option of viewing the import report
                    Object[] options = {Locale.getString("OK"), Locale.getString("VIEW_REPORT")};
                    int option =
                        JOptionPane.showInternalOptionDialog(desktop, 
                                                             message, 
                                                             Locale.getString("IMPORT_COMPLETE_TITLE"),
                                                             JOptionPane.DEFAULT_OPTION,
                                                             JOptionPane.INFORMATION_MESSAGE,
                                                             null,
                                                             options,
                                                             options[0]);
                    
                    if(option == 1) {
                        Thread thread = new Thread(new Runnable() {
                                public void run() {
                                    TextViewDialog.showTextDialog(report.getText(),
                                                                  Locale.getString("IMPORT_REPORT"));
                                }});
                        thread.start();
                    }
                    
                }
            });
    }

    /**
     * Return the database source to import to. We can only import to a database
     * source (either the internal or an external database). The only source
     * that is not a database is the samples source. If this is selected then
     * silently convert them over to the "Internal Database". This way Venice "just works".
     *
     * @return database quote source
     */
    private DatabaseQuoteSource getDatabaseSource() {
        // If the user is still using the "Samples" quotes, then convert them to
        // "Internal Database".
        int quoteSource = PreferencesManager.getQuoteSource();                    
        if(quoteSource == PreferencesManager.SAMPLES) {
            PreferencesManager.setQuoteSource(PreferencesManager.INTERNAL);
            QuoteSourceManager.flush();
        }

        // We know the quote source must be a database now
        return (DatabaseQuoteSource)QuoteSourceManager.getSource();
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
        return Locale.getString("IMPORT_TITLE");
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
     */
    public void save() {
        // Same as hitting cancel - do not save anything
    }
}
