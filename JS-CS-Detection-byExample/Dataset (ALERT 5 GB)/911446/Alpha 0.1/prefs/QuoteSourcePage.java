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
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.mov.importer.*;
import org.mov.prefs.PreferencesManager;
import org.mov.quote.*;

/** 
 * Provides a preferences page to let the user modify the quote source.
 * The quote source can be from a database, files or from the internet.
 */
public class QuoteSourcePage extends JPanel 
    implements ActionListener, ListSelectionListener, PreferencesPage
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
    private JList fileList;
    private DefaultListModel fileListModel;
    private JButton addFiles;
    private JButton deleteFiles;
    private JComboBox formatComboBox;

    // Widgets from internet pane
    private JRadioButton useInternet;
    private JComboBox internetHost;
    private JTextField internetUsername;
    private JPasswordField internetPassword;

    /**
     * Create a new Quote Source Preferences page.
     *
     * @param	desktop	the parent desktop.
     */
    public QuoteSourcePage(JDesktopPane desktop) {

	this.desktop = desktop;

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	Preferences p = PreferencesManager.getUserNode("/quote_source");
	String quoteSource = p.get("source", "database");

	// Tab Pane
	JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP);

	// Database Pane
	{
	    p = PreferencesManager.getUserNode("/quote_source/database");

	    useDatabase = new JRadioButton("Use Database", true);
	    useDatabase.addActionListener(this);
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
	    databaseHost = addTextRow(borderPanel, "Host", p.get("host", "db"),
				      gridbag, c);
	    
	    // Port
	    databasePort = addTextRow(borderPanel, "Port", 
				      p.get("port",  "3306"), 
				      gridbag, c);
	    
	    // Username
	    databaseUsername = addTextRow(borderPanel, "Username", 
					  p.get("username", ""), gridbag, c);
	    
	    // Password
	    databasePassword = addPasswordRow(borderPanel, "Password", 
					      p.get("password", ""), 
					      gridbag, c);
	    
	    // Database Name
	    databaseName = addTextRow(borderPanel, "Database Name", 
				      p.get("dbname", "shares"), gridbag, c);

	    databasePreferences.add(borderPanel, BorderLayout.NORTH);
	    	    
	    JPanel database = new JPanel();
	    database.setLayout(new BorderLayout());
	    database.add(useDatabase, BorderLayout.NORTH);
	    database.add(databasePreferences, BorderLayout.CENTER);

	    pane.addTab("Database", database);
      	    TitledBorder mainTitled = new TitledBorder("Skins");
	    this.setBorder(new TitledBorder("Quote Source"));
	}

	// File Pane
	{
	    p = PreferencesManager.getUserNode("/quote_source/files");

	    useFiles = new JRadioButton("Use Files", true);
	    useFiles.addActionListener(this);
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
	    Vector formats = QuoteFilterList.getInstance().getList();
	    Iterator iterator = formats.iterator();
	    QuoteFilter filter;

	    while(iterator.hasNext()) {
		filter = (QuoteFilter)iterator.next();
		formatComboBox.addItem(filter.getName());
		if(filter.getName().equals(selectedFilter))
		    formatComboBox.setSelectedItem((Object)filter.getName());
	    }

	    box.add(new JLabel("Format"));
	    box.add(Box.createHorizontalStrut(5));
	    box.add(formatComboBox);

	    filePreferences.add(box, BorderLayout.NORTH);

	    fileList = new JList();
	    fileListModel = new DefaultListModel();
	    fileList.setModel(fileListModel);
	    fileList.addListSelectionListener(this);	   
	    filePreferences.add(new JScrollPane(fileList), 
				BorderLayout.CENTER);

	    // Add files from prefs
	    Vector fileList = ImporterModule.getFileList();
	    iterator = fileList.iterator();
	    String fileName;

	    while(iterator.hasNext()) {
		fileName = (String)iterator.next();
		fileListModel.addElement(fileName);
	    }

	    // Add, Delete buttons
	    JPanel buttonPanel = new JPanel();
	    addFiles = new JButton("Add");
	    addFiles.addActionListener(this);
	    deleteFiles = new JButton("Delete");
	    deleteFiles.setEnabled(false);
	    deleteFiles.addActionListener(this);
	    
	    buttonPanel.add(addFiles);
	    buttonPanel.add(deleteFiles);

	    JPanel files = new JPanel();
	    files.setLayout(new BorderLayout());
	    files.add(useFiles, BorderLayout.NORTH);
	    files.add(filePreferences, BorderLayout.CENTER);
	    files.add(buttonPanel, BorderLayout.SOUTH);

	    pane.addTab("Files", files);
	}

	// Internet Pane - temporary disabled. I can no longer connect to Sanford
        // using this code. I'll need to find a public site on the web where I can
        // download quotes from.
	if(false) {
	    p = PreferencesManager.getUserNode("/quote_source/internet");

	    useInternet = new JRadioButton("Use Internet", true);
	    useInternet.addActionListener(this);
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
	    internetUsername = addTextRow(borderPanel, "Username", 
					  p.get("username", ""), gridbag, c);

	    // Password
	    internetPassword = 
		addPasswordRow(borderPanel, "Password",
			       p.get("password", ""), gridbag, c);

	    internetPreferences.add(borderPanel, BorderLayout.NORTH);

	    JPanel internet = new JPanel();
	    internet.setLayout(new BorderLayout());
	    internet.add(useInternet, BorderLayout.NORTH);
	    internet.add(internetPreferences, BorderLayout.CENTER);

	    pane.addTab("Internet", internet);
	}

	// Put all "use this option" radio buttons into group
	ButtonGroup group = new ButtonGroup();
	group.add(useDatabase);
	group.add(useFiles);
	group.add(useInternet);

	// Raise the select source's pane
	if(quoteSource.equals("files")) 
	    pane.setSelectedIndex(1);
	else if(quoteSource.equals("internet"))
	    pane.setSelectedIndex(2);
	else
	    pane.setSelectedIndex(0);

	add(pane);
    }

    // Helper method which adds a new text field in a new row to the given 
    // grid bag layout.
    private JTextField addTextRow(JPanel panel, String field, String value,
				  GridBagLayout gridbag,
				  GridBagConstraints c) {
	JLabel label = new JLabel(field);
	c.gridwidth = 1;
	gridbag.setConstraints(label, c);
	panel.add(label);

	JTextField text = new JTextField(value, 15);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(text, c);
	panel.add(text);

	return text;
    }

    // Helper method which adds a new password text field in a new row to
    // the given grid bag layout.
    private JPasswordField addPasswordRow(JPanel panel, String field, 
					  String value,
					  GridBagLayout gridbag,
					  GridBagConstraints c) {
	JLabel label = new JLabel(field);
	c.gridwidth = 1;
	gridbag.setConstraints(label, c);
	panel.add(label);
	    
	JPasswordField password = new JPasswordField(value, 15);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(password, c);
	panel.add(password);

	return password;
    }

    /**
     * This is called when an element is selected from the list.
     */
    public void valueChanged(ListSelectionEvent e) {
	// If the user has selected an element in the file list
	// then enable the delete button
	deleteFiles.setEnabled(true);
    }

    /**
     *  This is called when one of the buttons is pressed
     */
    public void actionPerformed(ActionEvent e) {

	if(e.getSource() == addFiles) {
	    // Get files user wants to import
	    JFileChooser chooser = new JFileChooser();
	    chooser.setMultiSelectionEnabled(true);
	    int action = chooser.showOpenDialog(desktop);

	    if(action == JFileChooser.APPROVE_OPTION) {
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
	else if(e.getSource() == deleteFiles) {
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
     * Returns the window title.
     *
     * @return	the window title.
     */
    public String getTitle() {
	return "Quote Source";
    }

    /**
     * Update the preferences file.
     */
    public void save() {
	// Type
	Preferences p = 
	    PreferencesManager.getUserNode("/quote_source");
	if(useFiles.isSelected())
	    p.put("source", "files");
	//else if(useInternet.isSelected())
	//    p.put("source", "internet");
	else 
	    p.put("source", "database");

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
	    Vector fileList = new Vector();
	    for(int i = 0; i < fileListModel.getSize(); i++) {
		fileList.add((String)fileListModel.elementAt(i));
	    }

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
