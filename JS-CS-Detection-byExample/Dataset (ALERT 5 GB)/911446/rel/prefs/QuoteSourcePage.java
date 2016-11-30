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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JCheckBox;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import nz.org.venice.ui.DesktopManager;
import nz.org.venice.ui.GridBagHelper;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.quote.DatabaseQuoteSource;
import nz.org.venice.quote.DatabaseManager;
import nz.org.venice.quote.DatabaseAccessManager;
import nz.org.venice.quote.QuoteSourceManager;
import nz.org.venice.util.Locale;

/** 
 * Provides a preferences page to let the user modify the quote source.
 * The quote source can be from a database (internal or external) or
 * sample quotes.
 *
 * @author Andrew Leppard
 */
public class QuoteSourcePage extends JPanel implements PreferencesPage
{
    private JDesktopPane desktop;

    // Widgets from database pane
    private JRadioButton useDatabase;
    private JComboBox databaseSoftware;
    private JComboBox databaseDriver;
    private JTextField databaseHost;

    // Widgets from internal pane
    private JRadioButton useInternal;

    // This field needs to be initialised as it may be referenced
    // before the widget is created.
    private JTextField databasePort = null;
    private JTextField databaseUsername;
    private JPasswordField databasePassword;
    private JTextField databaseName;
    private JCheckBox databasePasswordPrompt; 

    // Widgets from internet pane
    private JRadioButton useInternet;
    private JComboBox internetHost;
    private JTextField internetUsername;
    private JPasswordField internetPassword;

    // Widgets from the samples pane
    private JRadioButton useSamples;

    // Preferences
    private PreferencesManager.DatabasePreferences databasePreferences = null;

    // Quote source enumeration
    private final static int SAMPLES  = 0;
    private final static int INTERNAL = 1;
    private final static int DATABASE = 2;

    // Database enumeration
    private final static int MYSQL      = 0;
    private final static int POSTGRESQL = 1;
    private final static int HSQL       = 2;
    private final static int OTHER      = 3;
    
    // Database default drivers
    private final static String[] mysql_drivers      = {"org.gjt.mm.mysql.Driver","com.mysql.jdbc.Driver"};
    private final static String[] postgresql_drivers = {"org.postgresql.Driver"};
    private final static String[] hsql_drivers       = {"org.hsqldb.jdbcDriver"};

    // Database default ports
    private final static int MYSQL_DEFAULT_PORT = 3306;
    private final static int POSTGRESQL_DEFAULT_PORT = 5432;
    private final static int HSQLDB_DEFAULT_PORT = 9001;

    /**
     * Create a new Quote Source Preferences page.
     *
     * @param	desktop	the parent desktop.
     */
    public QuoteSourcePage(JDesktopPane desktop) {

	this.desktop = desktop;

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	// Load quote source preferences
	int quoteSource = PreferencesManager.getQuoteSource();

	// Tab Pane
	JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP);
	// Put all "use this option" radio buttons into group
	ButtonGroup buttonGroup = new ButtonGroup();
	
        // Add a pane for each quote source the user can select.
        // These should be added in the same order as DATABASE, INTERNAL, etc.
        pane.addTab(Locale.getString("SAMPLES"), 
		    createSamplesPanel(quoteSource, buttonGroup));
        pane.addTab(Locale.getString("INTERNAL"),
                    createInternalPanel(quoteSource, buttonGroup));
        pane.addTab(Locale.getString("DATABASE"), 
		    createDatabasePanel(quoteSource, buttonGroup));

	// Raise the select source's pane
        if(quoteSource == PreferencesManager.INTERNAL)
	    pane.setSelectedIndex(INTERNAL);
        else if(quoteSource == PreferencesManager.DATABASE)
	    pane.setSelectedIndex(DATABASE);
	else
	    pane.setSelectedIndex(SAMPLES);

	add(pane);
    }

    private JPanel createInternalPanel(int quoteSource, ButtonGroup buttonGroup) {
        String internalFileName = PreferencesManager.getInternalFileName();

        useInternal = new JRadioButton(Locale.getString("USE_INTERNAL"), true);
	buttonGroup.add(useInternal);
	useInternal.setToolTipText(Locale.getString("QUOTESOURCE_INTERNAL_TOOLTIP"));

	useInternal.setSelected(quoteSource == PreferencesManager.INTERNAL);
        
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BorderLayout());
        outerPanel.add(useInternal, BorderLayout.NORTH);
        
        return outerPanel;
    }

    private JPanel createDatabasePanel(int quoteSource, ButtonGroup buttonGroup) {
	databasePreferences = PreferencesManager.getDatabaseSettings();
        useDatabase = new JRadioButton(Locale.getString("USE_DATABASE"), true);
	useDatabase.setToolTipText(Locale.getString("QUOTESOURCE_USE_DATABASE_TOOLTIP"));
	buttonGroup.add(useDatabase);

	useDatabase.setSelected(quoteSource == PreferencesManager.DATABASE);
        
        TitledBorder titled = new TitledBorder(Locale.getString("DATABASE_PREFERENCES"));
        JPanel preferencesPanel = new JPanel();
        preferencesPanel.setBorder(titled);
        preferencesPanel.setLayout(new BorderLayout()); 
        JPanel borderPanel = new JPanel();
        
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        borderPanel.setLayout(gridbag);
        
        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
        
        // Database 
        JLabel label = new JLabel(Locale.getString("DATABASE"));
        gridbag.setConstraints(label, c);
        borderPanel.add(label);
        
        databaseSoftware = new JComboBox();
        databaseSoftware.addItem(Locale.getString("MYSQL"));
        databaseSoftware.addItem(Locale.getString("POSTGRESQL"));
        databaseSoftware.addItem(Locale.getString("HSQLDB"));
        databaseSoftware.addItem(Locale.getString("OTHER"));
        
	databaseSoftware.setToolTipText(Locale.getString("QUOTESOURCE_DATABASE_TOOLTIP"));

	if(databasePreferences.software.equals(DatabaseManager.MYSQL_SOFTWARE))
	    databaseSoftware.setSelectedIndex(DatabaseManager.MYSQL);
	    else if(databasePreferences.software.equals(DatabaseManager.POSTGRESQL_SOFTWARE))
	       databaseSoftware.setSelectedIndex(DatabaseManager.POSTGRESQL);
		else if(databasePreferences.software.equals(DatabaseManager.HSQLDB_SOFTWARE))
	       databaseSoftware.setSelectedIndex(DatabaseManager.HSQLDB);
		else
		   databaseSoftware.setSelectedIndex(DatabaseManager.OTHER);
        // If the user changes the database, then update the port
        // field to reflect the default port of the database.
        databaseSoftware.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(databasePort != null && databaseDriver != null) {
                        populateDatabaseDrivers();
			selectuseDatabase();
                        databaseUsername.setEnabled(true);
                        databasePassword.setEnabled(true);
			databasePasswordPrompt.setEnabled(true);
                        if(databaseSoftware.getSelectedIndex() == DatabaseManager.MYSQL)
                            databasePort.setText(Integer.toString(MYSQL_DEFAULT_PORT));
                        else if(databaseSoftware.getSelectedIndex() == DatabaseManager.POSTGRESQL)
                            databasePort.setText(Integer.toString(POSTGRESQL_DEFAULT_PORT));
                        else if(databaseSoftware.getSelectedIndex() == DatabaseManager.HSQLDB) {
                            databasePort.setText(Integer.toString(HSQLDB_DEFAULT_PORT));
                            // And enable/disable the username and password fields if applicable
                            // Hypesonic SQL does not accept the username password fields
                            databaseUsername.setEnabled(false);
                            databasePassword.setEnabled(false);
			    databasePasswordPrompt.setEnabled(false);
			}
		    }
		}});

	// Use ActionListener and DocumentListener to make sure useDatabase
	// is always selected if database properties are changed
	ActionListener al = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      selectuseDatabase();
	    }
	  };

	DocumentListener dl = new DocumentListener() {
	    public void changedUpdate(DocumentEvent e) {
	    }
            
	    public void insertUpdate(DocumentEvent e) {
	      selectuseDatabase();
	    }
	    
	    public void removeUpdate(DocumentEvent e) {
	      selectuseDatabase();
	    }
	  };

        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(databaseSoftware, c);
        borderPanel.add(databaseSoftware);

        // Show known drivers for the selected database software
        label = new JLabel(Locale.getString("DRIVER"));
        c.gridwidth = 1;
        gridbag.setConstraints(label, c);
        borderPanel.add(label);
        
        databaseDriver = new JComboBox();
        databaseDriver.setEditable(true);
	databaseDriver.setToolTipText(Locale.getString("QUOTESOURCE_DB_DRIVER_TOOLTIP"));

        // Only display known drivers that are currently installed.
        populateDatabaseDrivers();

	databaseDriver.addActionListener(al);
	
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(databaseDriver, c);
        borderPanel.add(databaseDriver);

        // Host
        databaseHost = GridBagHelper.addTextRow(borderPanel, 
						Locale.getString("HOST"), 
						databasePreferences.host,
                                                gridbag, c, 15);
	databaseHost.addActionListener(al);
	databaseHost.getDocument().addDocumentListener(dl);
	databaseHost.setToolTipText(Locale.getString("QUOTESOURCE_DB_HOST_FIELD_TOOLTIP"));
        
        // Port
        databasePort = GridBagHelper.addTextRow(borderPanel, 
						Locale.getString("PORT"), 
                                                databasePreferences.port,
                                                gridbag, c, 15);

	databasePort.addActionListener(al);
	databasePort.getDocument().addDocumentListener(dl);
	databasePort.setToolTipText(Locale.getString("QUOTESOURCE_DB_PORT_FIELD_TOOLTIP"));
        
        // Username
        databaseUsername = GridBagHelper.addTextRow(borderPanel, 
						    Locale.getString("USERNAME"), 
                                                    databasePreferences.username, gridbag, c, 15);
	databaseUsername.addActionListener(al);
	databaseUsername.getDocument().addDocumentListener(dl);
	databaseUsername.setToolTipText(Locale.getString("QUOTESOURCE_DB_USER_FIELD_TOOLTIP"));


        
        // Password
	String passwdString = (databasePreferences.password.equals(""))
	    ? "" 
	    : DatabaseAccessManager.getInstance().unMask(databasePreferences.password);
	
        databasePassword = GridBagHelper.addPasswordRow(borderPanel, 
							Locale.getString("PASSWORD"), 
                                                        passwdString, 
                                                        gridbag, c, 15);
	databasePassword.addActionListener(al);
	databasePassword.getDocument().addDocumentListener(dl);
	databasePassword.setToolTipText(Locale.getString("QUOTESOURCE_DB_PASSWORD_FIELD_TOOLTIP"));

        
	if (databasePreferences.passwordPrompt) {
	    databasePassword.setEnabled(false);
	}
       
        // Database Name
        databaseName = GridBagHelper.addTextRow(borderPanel, 
						Locale.getString("DATABASE_NAME"), 
                                                databasePreferences.database, gridbag, c, 15);
	databaseName.addActionListener(al);
	databaseName.getDocument().addDocumentListener(dl);	


	databasePasswordPrompt = GridBagHelper.addCheckBoxRow(borderPanel, 
							      Locale.getString("PROMPT_FOR_PASSWORD"), 
							      databasePreferences.passwordPrompt, 
							      gridbag, c);
	
	databasePasswordPrompt.setToolTipText(Locale.getString("QUOTESOURCE_DB_PASSWORD_PROMPT_TOOLTIP"));

	databasePasswordPrompt.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    selectuseDatabase();
		    if (databasePasswordPrompt.isSelected()) {
			databasePassword.setEnabled(false);
		    } else {
			databasePassword.setEnabled(true);
		    }
		}
	    });
	
		
	
        // Hypesonic SQL does not accept the username password fields
        if(databaseSoftware.getSelectedIndex() == DatabaseManager.HSQLDB) {
            databaseUsername.setEnabled(false);
            databasePassword.setEnabled(false);
	    databasePasswordPrompt.setEnabled(false);
        }

   
        preferencesPanel.add(borderPanel, BorderLayout.NORTH);
        
        JPanel database = new JPanel();
        database.setLayout(new BorderLayout());
        database.add(useDatabase, BorderLayout.NORTH);
        database.add(preferencesPanel, BorderLayout.CENTER);
        
        return database;
    }

    /** 
     * Sets the databaseDriver dropdown to contain the list of valid drivers
     * for the currently selected software
     *
     */
    private void populateDatabaseDrivers() {
        String drivers[] = {};
        databaseDriver.removeAllItems();
        if(databaseSoftware.getSelectedIndex() == MYSQL)
            drivers = mysql_drivers;
		else if(databaseSoftware.getSelectedIndex() == POSTGRESQL)
		    drivers = postgresql_drivers;
		else if(databaseSoftware.getSelectedIndex() == HSQL)
		    drivers = hsql_drivers;

        for(int i = 0; i < drivers.length; i++) {
	        try {
	            Class.forName(drivers[i]);
		        databaseDriver.addItem(drivers[i]);
	        } catch (ClassNotFoundException e) {}
	    }
		    
		if (databasePreferences.driver != null)
		    try {
		        Class.forName(databasePreferences.driver);
		        databaseDriver.setSelectedItem(databasePreferences.driver);
		    } catch (ClassNotFoundException e) {}
		else
		    databaseDriver.setSelectedIndex(0);
		
		if (databaseDriver.getSelectedItem() == null) {
	        databaseDriver.addItem(Locale.getString("DATABASE_NO_DRIVERS"));
	        databaseDriver.setToolTipText(Locale.getString("DATABASE_NO_DRIVERS_DETAIL"));
		    databaseDriver.setSelectedIndex(0);
		} else {
		    databaseDriver.setToolTipText(Locale.getString("QUOTESOURCE_DB_DRIVER_TOOLTIP"));
		}
    }

    private JPanel createSamplesPanel(int quoteSource, ButtonGroup buttonGroup) {
        useSamples = new JRadioButton(Locale.getString("USE_SAMPLES"), true);
        buttonGroup.add(useSamples);
	useSamples.setToolTipText(Locale.getString("QUOTESOURCE_SAMPLES_TOOLTIP"));
	

	useSamples.setSelected(quoteSource == PreferencesManager.SAMPLES);
        
        JPanel samples = new JPanel();
        samples.setLayout(new BorderLayout());
        samples.add(useSamples, BorderLayout.NORTH);
        
        return samples;
    }

    private void selectuseDatabase() {
      if (!useDatabase.isSelected())
	useDatabase.setSelected(true);
    }

    public JComponent getComponent() {
	return this;
    }

    public String getTitle() {
	return Locale.getString("QUOTE_SOURCE_PAGE_TITLE");
    }

    public void save() {
	// Save quote source preferences
	int quoteSource;

	if (useDatabase.isSelected()) {
	  quoteSource = PreferencesManager.DATABASE;
	
	  // Save database preferences
	  String software = (String)databaseSoftware.getSelectedItem();
	  if (software.equals(Locale.getString("MYSQL")))
	    databasePreferences.software = "mysql";
	  else if (software.equals(Locale.getString("POSTGRESQL")))
            databasePreferences.software = "postgresql";
	  else
            databasePreferences.software = "hsql";

	  databasePreferences.driver = databaseDriver.getSelectedItem().toString();
	  databasePreferences.host = databaseHost.getText();
	  databasePreferences.port = databasePort.getText();
	  databasePreferences.username = databaseUsername.getText();
	  databasePreferences.password = 
	    (databasePassword.getPassword().length > 0) 
	    ? DatabaseAccessManager.getInstance().mask(new String(databasePassword.getPassword()))
	    : "";
	  databasePreferences.passwordPrompt = databasePasswordPrompt.isSelected();
	  databasePreferences.database = databaseName.getText();

	  PreferencesManager.putDatabaseSettings(databasePreferences);

	  // This makes the next query use our new settings
	}
	else if (useInternal.isSelected())
	    quoteSource = PreferencesManager.INTERNAL;
	else
	    quoteSource = PreferencesManager.SAMPLES;

	PreferencesManager.putQuoteSource(quoteSource);
	QuoteSourceManager.flush();
    }
}
