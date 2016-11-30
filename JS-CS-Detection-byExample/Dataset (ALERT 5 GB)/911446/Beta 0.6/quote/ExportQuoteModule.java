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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.mov.main.Module;
import org.mov.main.ModuleFrame;
import org.mov.prefs.PreferencesManager;
import org.mov.ui.DesktopManager;
import org.mov.ui.GridBagHelper;
import org.mov.ui.ProgressDialog;
import org.mov.ui.ProgressDialogManager;
import org.mov.util.Locale;
import org.mov.util.TradingDate;

/**
 * The export module allows exporting of quotes from Venice.
 * It provides an interface to allow the user to perform
 * an export of all the quotes in Venice's database. The actual
 * exporting is handled by other classes.
 *
 * @author Andrew Leppard
 * @see DatabaseQuoteSource
 * @see FileEODQuoteExport
 * @see ImportQuoteModule
 */
public class ExportQuoteModule extends JPanel implements Module {

    private JDesktopPane desktop;
    private PropertyChangeSupport propertySupport;

    // Fields
    private JComboBox formatComboBox;
    private JTextField fileNamesTextField;

    // Parsed Fields for file export
    private EODQuoteFilter filter;
    private String fileNames;
   
    /**
     * Create a new export quote module.
     *
     * @param desktop the parent desktop
     */ 
    public ExportQuoteModule(JDesktopPane desktop) {
        this.desktop = desktop;
        propertySupport = new PropertyChangeSupport(this);

        setLayout(new BorderLayout());

        buildGUI();
    }

    /**
     * Layout the user interface.
     */
    private void buildGUI() {
        Preferences p = PreferencesManager.getUserNode("/export_quotes");
        TitledBorder titledBorder = new TitledBorder(Locale.getString("EXPORT_TO_FILES"));
        JPanel titledPanel = new JPanel();
        titledPanel.setBorder(titledBorder);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        titledPanel.setLayout(gridbag);
        
        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;        

        // Export to files
        {
            JLabel label = new JLabel(Locale.getString("FORMAT"));
            c.gridwidth = 1;
            gridbag.setConstraints(label, c);
            titledPanel.add(label);

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

            String fileNames = p.get("toFileNames", 
                                     "/tmp/quotes/dd-mm-yy.txt");
            fileNamesTextField =
                GridBagHelper.addTextRow(titledPanel, Locale.getString("FILE_NAMES"), fileNames,
                                         gridbag, c, 15);
        }
        
        add(titledPanel, BorderLayout.CENTER);

        // Export, Cancel buttons
        JPanel buttonPanel = new JPanel();
        JButton exportButton = new JButton(Locale.getString("EXPORT"));
        exportButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Export quotes
                    exportQuotes();
                }
            });

        JButton cancelButton = new JButton(Locale.getString("CANCEL"));
        cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Tell frame we want to close
                    propertySupport.firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
                }
            });

        buttonPanel.add(exportButton);
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Import quotes from venice.
     */
    private void exportQuotes() {
        // Only save configuration if user exports with it
        saveConfiguration();

        // Performing the quote export in a separate thread will
        // prevent the application appearing to "lock up"
        Thread thread = new Thread() {		
                public void run() {
                    parseFields();
                    exportQuotesToFiles();
                }
            };	
        thread.start();        
    }

    /**
     * Save the configuration on screen to the preferences file
     */
    private void saveConfiguration() {
        Preferences p = PreferencesManager.getUserNode("/export_quotes");
        p.put("fileFilter", (String)formatComboBox.getSelectedItem());
        p.put("fileNames", fileNamesTextField.getText());
    }

    /**
     * Parse all the fields for export.
     */
    private void parseFields() {
        // Parse quote format
        String format = (String)formatComboBox.getSelectedItem();
        filter = EODQuoteFilterList.getInstance().getFilter(format);

        // And get file name format
        fileNames = fileNamesTextField.getText();
    }

    /**
     * Export all the quotes in the Database into files.
     */
    private void exportQuotesToFiles() {
        // Tell frame we want to close
        propertySupport.firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);

        // Now set up progress dialog to display the file by file progress
        ProgressDialog progress = ProgressDialogManager.getProgressDialog();
        progress.setIndeterminate(true);
        progress.setMaster(true);
        progress.show(Locale.getString("EXPORTING"));

        // Get first and last dates
        QuoteSource source = QuoteSourceManager.getSource();
        TradingDate firstDate = source.getFirstDate();
        TradingDate lastDate;
        List dateRange;

        // If the source is empty, then this thread will be interrupted.
        // and an error message printed. All we need to do is exit now.
        if(Thread.currentThread().isInterrupted()) {
            ProgressDialogManager.closeProgressDialog(progress);	
            return;
        }

        lastDate = source.getLastDate();
        dateRange = TradingDate.dateRangeToList(firstDate, lastDate);

        progress.setIndeterminate(false);
        progress.setMaximum(dateRange.size());
        progress.setProgress(0);

        // Export one date into one file at a time
        for(Iterator iterator = dateRange.iterator(); iterator.hasNext();) {
            TradingDate date = (TradingDate)iterator.next();
            String fileName = date.toString(fileNames);
            
            // Update progress dialog
            //progress.setNote(Locale.getString("EXPORTING_FILE", fileName));
                
            // Load quotes from source and place them in a list ready to write
            EODQuoteRange quoteRange = new EODQuoteRange(EODQuoteRange.ALL_SYMBOLS, date);
            EODQuoteBundle quoteBundle = new  EODQuoteBundle(quoteRange);
            List quotes = new ArrayList();

            for(Iterator innerIterator = quoteBundle.iterator(); innerIterator.hasNext();)
                quotes.add(innerIterator.next());

            // Stop if the user hit cancel
            if(Thread.currentThread().isInterrupted())   
                break;
            
            // Export into file
            try {
                FileEODQuoteExport.exportFile(filter, new File(fileName), quotes);
            }
            catch(IOException e) {
                DesktopManager.showErrorMessage(Locale.getString("ERROR_WRITING_TO_FILE",
                                                                 fileName));
                break;
            }

            progress.increment();
        }
        
        ProgressDialogManager.closeProgressDialog(progress);

        // Let the user know the export has completed
        JOptionPane.showInternalMessageDialog(desktop, 
                                              Locale.getString("EXPORT_COMPLETE"),
                                              Locale.getString("EXPORT_COMPLETE_TITLE"),
                                              JOptionPane.INFORMATION_MESSAGE);
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
        return Locale.getString("EXPORT_TITLE");
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
