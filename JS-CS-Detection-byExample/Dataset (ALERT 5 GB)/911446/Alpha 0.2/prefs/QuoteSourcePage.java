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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mov.importer.ImporterModule;
import org.mov.ui.GridBagHelper;
import org.mov.prefs.PreferencesManager;
import org.mov.quote.QuoteFilter;
import org.mov.quote.QuoteFilterList;
import org.mov.quote.QuoteSourceManager;

/** 
 * Provides a preferences page to let the user modify the quote source.
 * The quote source can be from a database, files or from the internet.
 */
public class QuoteSourcePage extends JPanel implements PreferencesPage
{
    private JDesktopPane desktop;

    // Widgets from database pane
    private JRadioButton useDatabase;
    private JTextField databaseHost;
    private JTextField databasePort;
    private JTextField databaseUsername;
    private JPasswordField databasePassword;
    private JTextField databaseName;

    // Widgets from file pane
    private JRadioButton useFiles;
    private DefaultListModel fileListModel;
    private JButton addFiles;
    private JButton deleteFiles;
    private JComboBox formatComboBox;

    // Widgets from internet pane
    private JRadioButton useInternet;
    private JComboBox internetHost;
    private JTextField internetUsername;
    private JPasswordField internetPassword;

    // Widgets from the samples pane
    private JRadioButton useSamples;

    /**
     * Create a new Quote Source Preferences page.
     *
     * @param	desktop	the parent desktop.
     */
    public QuoteSourcePage(JDesktopPane desktop) {

	this.desktop = desktop;

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	Preferences p = PreferencesManager.getUserNode("/quote_source");
	String quoteSource = p.get("source", "samples");

        setBorder(new TitledBorder(getTitle()));

	// Tab Pane
	JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP);
	// Put all "use this option" radio buttons into group
	ButtonGroup buttonGroup = new ButtonGroup();

        // Add a pane for each quote source the user can select
        pane.addTab("Database", createDatabasePanel(quoteSource, buttonGroup));
        pane.addTab("Files", createFilesPanel(quoteSource, buttonGroup));
        //        pane.addTab("Internet", createInternetPanel(quoteSource, buttonGroup));
        pane.addTab("Samples", createSamplesPanel(quoteSource, buttonGroup));

	// Raise the select source's pane
        if(quoteSource.equals("database"))
	    pane.setSelectedIndex(0);
	else if(quoteSource.equals("files")) 
	    pane.setSelectedIndex(1);
        //	else if(quoteSource.equals("internet"))
	//    pane.setSelectedIndex(2);
	else
	    pane.setSelectedIndex(2);

	add(pane);
    }

    private JPanel createDatabasePanel(String quoteSource, ButtonGroup buttonGroup) {
        Preferences p = PreferencesManager.getUserNode("/quote_source/database");

        useDatabase = new JRadioButton("Use Database", true);
	buttonGroup.add(useDatabase);

        if(quoteSource.equals("database"))
            useDatabase.setSelected(true);
        else
            useDatabase.setSelected(false);
        
        TitledBorder titled = new TitledBorder("Database Preferences");
        JPanel databasePreferences = new JPanel();
        databasePreferences.setBorder(titled);
        databasePreferences.setLayout(new BorderLayout()); 
        JPanel borderPanel = new JPanel();
        
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        borderPanel.setLayout(gridbag);
        
        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
        
        // Database 
        JLabel label = new JLabel("Database");
        gridbag.setConstraints(label, c);
        borderPanel.add(label);
        
        JComboBox databaseType = new JComboBox();
        databaseType.addItem("MySQL");
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(databaseType, c);
        borderPanel.add(databaseType);
        
        // Host
        databaseHost = GridBagHelper.addTextRow(borderPanel, "Host", p.get("host", "db"),
                                                gridbag, c, 15);
        
        // Port
        databasePort = GridBagHelper.addTextRow(borderPanel, "Port", 
                                                p.get("port",  "3306"), 
                                                gridbag, c, 15);
        
        // Username
        databaseUsername = GridBagHelper.addTextRow(borderPanel, "Username", 
                                                    p.get("username", ""), gridbag, c, 15);
        
        // Password
        databasePassword = GridBagHelper.addPasswordRow(borderPanel, "Password", 
                                                        p.get("password", ""), 
                                                        gridbag, c, 15);
        
        // Database Name
        databaseName = GridBagHelper.addTextRow(borderPanel, "Database Name", 
                                                p.get("dbname", "shares"), gridbag, c, 15);
        
        databasePreferences.add(borderPanel, BorderLayout.NORTH);
        
        JPanel database = new JPanel();
        database.setLayout(new BorderLayout());
        database.add(useDatabase, BorderLayout.NORTH);
        database.add(databasePreferences, BorderLayout.CENTER);
        
        return database;
    }

    private JPanel createFilesPanel(String quoteSource, ButtonGroup buttonGroup) {
        Preferences p = PreferencesManager.getUserNode("/quote_source/files");

        useFiles = new JRadioButton("Use Files", true);
	buttonGroup.add(useFiles);

        if(quoteSource.equals("files"))
            useFiles.setSelected(true);
        else
            useFiles.setSelected(false);
        
        TitledBorder titled = new TitledBorder("Files");
        JPanel filePreferences = new JPanel();
        filePreferences.setBorder(titled);
        filePreferences.setLayout(new BorderLayout());
        
        String selectedFilter = p.get("format", "MetaStock");
        Box box = Box.createHorizontalBox();
        formatComboBox = new JComboBox();
        List formats = QuoteFilterList.getInstance().getList();

        for(Iterator iterator = formats.iterator(); iterator.hasNext();) {
            QuoteFilter filter = (QuoteFilter)iterator.next();
            formatComboBox.addItem(filter.getName());
            if(filter.getName().equals(selectedFilter))
                formatComboBox.setSelectedItem((Object)filter.getName());
        }
        
        box.add(new JLabel("Format"));
        box.add(Box.createHorizontalStrut(5));
        box.add(formatComboBox);
        
        filePreferences.add(box, BorderLayout.NORTH);
        
        final JList fileList = new JList();
        fileListModel = new DefaultListModel();
        fileList.setModel(fileListModel);
        fileList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    // If the user has selected an element in the file list
                    // then enable the delete button
                    deleteFiles.setEnabled(true);
                }
            });
        filePreferences.add(new JScrollPane(fileList), 
                            BorderLayout.CENTER);
        
        // Add files from prefs
        List fileListStrings = ImporterModule.getFileList();
        for(Iterator iterator = fileListStrings.iterator(); iterator.hasNext();) {
            String fileName = (String)iterator.next();
            fileListModel.addElement(fileName);
        }
        
        // Add, Delete buttons
        JPanel buttonPanel = new JPanel();
        addFiles = new JButton("Add");
        addFiles.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Get files user wants to import
                    JFileChooser chooser;
                    String lastDirectory = PreferencesManager.loadLastImportDirectory();

                    if(lastDirectory != null)
                        chooser = new JFileChooser(lastDirectory);
                    else
                        chooser = new JFileChooser();

                    chooser.setMultiSelectionEnabled(true);
                    int action = chooser.showOpenDialog(desktop);
                    
                    if(action == JFileChooser.APPROVE_OPTION) {
                        // Remember directory
                        lastDirectory = chooser.getCurrentDirectory().getAbsolutePath();
                        PreferencesManager.saveLastImportDirectory(lastDirectory);

                        // Add files to file list
                        File files[] = chooser.getSelectedFiles();
                        String fileName;
                        
                        for(int i = 0; i < files.length; i++) {
                            fileName = files[i].getPath();
                            
                            if(!fileListModel.contains((Object)fileName))
                                fileListModel.addElement((Object)fileName);
                        }
                    }
                }
            });
        deleteFiles = new JButton("Delete");
        deleteFiles.setEnabled(false);
        deleteFiles.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Get selected files from list
                    Object[] selected = fileList.getSelectedValues();
                    
                    // Remove all elements from list
                    for(int i = 0; i < selected.length; i++) {
                        fileListModel.removeElement(selected[i]);
                    }
                    
                    // Disable delete button after delete since nothing will be
                    // highlighted
                    deleteFiles.setEnabled(false);
                }
                });
        
        buttonPanel.add(addFiles);
        buttonPanel.add(deleteFiles);
        
        JPanel files = new JPanel();
        files.setLayout(new BorderLayout());
        files.add(useFiles, BorderLayout.NORTH);
        files.add(filePreferences, BorderLayout.CENTER);
        files.add(buttonPanel, BorderLayout.SOUTH);
        
        return files;
    }

    private JPanel createInternetPanel(String quoteSource, ButtonGroup buttonGroup) {
	// Internet Pane - temporary disabled. I can no longer connect to Sanford
        // using this code. I'll need to find a public site on the web where I can
        // download quotes from.
        Preferences p = PreferencesManager.getUserNode("/quote_source/internet");
        
        useInternet = new JRadioButton("Use Internet", true);
	buttonGroup.add(useInternet);

        if(quoteSource.equals("internet"))
            useInternet.setSelected(true);
        else
            useInternet.setSelected(false);
        
        TitledBorder titled = new TitledBorder("Internet");
        JPanel internetPreferences = new JPanel();
        internetPreferences.setBorder(titled);
        internetPreferences.setLayout(new BorderLayout());
        JPanel borderPanel = new JPanel();
        
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        borderPanel.setLayout(gridbag);
        
        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
        
        // Host
        JLabel label = new JLabel("Host");
        gridbag.setConstraints(label, c);
        borderPanel.add(label);
        
        internetHost = new JComboBox();
        internetHost.addItem("Sanford");
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(internetHost, c);
        borderPanel.add(internetHost);
        
        // Username
        internetUsername = GridBagHelper.addTextRow(borderPanel, "Username", 
                                                    p.get("username", ""), gridbag, c, 15);
        
        // Password
        internetPassword = 
            GridBagHelper.addPasswordRow(borderPanel, "Password",
                                         p.get("password", ""), gridbag, c, 15);
        
        internetPreferences.add(borderPanel, BorderLayout.NORTH);
        
        JPanel internet = new JPanel();
        internet.setLayout(new BorderLayout());
        internet.add(useInternet, BorderLayout.NORTH);
        internet.add(internetPreferences, BorderLayout.CENTER);

        return internet;
    }

    private JPanel createSamplesPanel(String quoteSource, ButtonGroup buttonGroup) {
        useSamples = new JRadioButton("Use Samples", true);
        buttonGroup.add(useSamples);

        if(quoteSource.equals("samples"))
            useSamples.setSelected(true);
        else
            useSamples.setSelected(false);
        
        JPanel samples = new JPanel();
        samples.setLayout(new BorderLayout());
        samples.add(useSamples, BorderLayout.NORTH);
        
        return samples;
    }

    public JComponent getComponent() {
	return this;
    }

    public String getTitle() {
	return "Quote Source";
    }

    public void save() {
	// Type
	Preferences p = 
	    PreferencesManager.getUserNode("/quote_source");
	if(useDatabase.isSelected())
	    p.put("source", "database");
	else if(useFiles.isSelected())
	    p.put("source", "files");
	//else if(useInternet.isSelected())
	//    p.put("source", "internet");
	else 
	    p.put("source", "samples");

	// Save database preferences
	{
	    p = PreferencesManager.getUserNode("/quote_source/database");
	    p.put("host", databaseHost.getText());
	    p.put("port", databasePort.getText());
	    p.put("username", databaseUsername.getText());
	    p.put("password", new String(databasePassword.getPassword()));
	    p.put("dbname", databaseName.getText());
	}

	// Save file preferences
	{
	    p = PreferencesManager.getUserNode("/quote_source/files");

	    p.put("format", (String)formatComboBox.getSelectedItem());

	    // Extract list of files from list
	    List fileList = new ArrayList();
	    for(int i = 0; i < fileListModel.getSize(); i++)
		fileList.add((String)fileListModel.elementAt(i));

	    ImporterModule.putFileList(fileList);
	}

	// Save internet preferences
	{
	    //p = PreferencesManager.getUserNode("/quote_source/internet");
	    //p.put("username", internetUsername.getText());
	    //p.put("password", new String(internetPassword.getPassword()));
	}

	// This makes the next query use our new settings
	QuoteSourceManager.flush();
    }
}
