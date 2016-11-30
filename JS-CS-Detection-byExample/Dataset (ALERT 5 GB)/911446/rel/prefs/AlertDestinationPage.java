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

import javax.swing.JRadioButton;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.ButtonGroup;
import javax.swing.BoxLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;

import nz.org.venice.util.Locale;

/**
  Preferences Page for where the user enables/disables alerts and where they 
  should be stored.  
 */
public class AlertDestinationPage extends JPanel implements PreferencesPage, ActionListener {

    private JDesktopPane desktop;
    private JRadioButton disableButton;
    private JRadioButton fileButton;
    private JRadioButton databaseButton;

    private String destination;

    /**
     * Create a new user interface preferences page.
     *
     * @param	desktop	the parent desktop.
     */
    public AlertDestinationPage(JDesktopPane desktop) {
	this.desktop = desktop;	
	destination = PreferencesManager.getAlertDestination();

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(createPanel());

    }

    private JPanel createPanel() {
	JPanel mainPanel = new JPanel();
	mainPanel.setLayout(new BorderLayout());
	JPanel borderPanel = new JPanel();

	ButtonGroup buttonGroup = new ButtonGroup();
	disableButton = new JRadioButton(Locale.getString("ALERT_DISABLE_ALL"));
	disableButton.setToolTipText(Locale.getString("ALERTSOURCE_DISABLE_BUTTON_TOOLTIP"));
	fileButton = new JRadioButton(Locale.getString("FILE"));
	fileButton.setToolTipText(Locale.getString("ALERTSOURCE_FILE_BUTTON_TOOLTIP"));
	databaseButton = new JRadioButton(Locale.getString("DATABASE"));
	databaseButton.setToolTipText(Locale.getString("ALERTSOURCE_DATABASE_BUTTON_TOOLTIP"));

	disableButton.addActionListener(this);
	fileButton.addActionListener(this);
	databaseButton.addActionListener(this);

	resetButtons();

	buttonGroup.add(disableButton);
	buttonGroup.add(fileButton);
	buttonGroup.add(databaseButton);

	borderPanel.add(disableButton);
	borderPanel.add(fileButton);
	borderPanel.add(databaseButton);

        borderPanel.setLayout(new BoxLayout(borderPanel, BoxLayout.PAGE_AXIS));
	
	mainPanel.add(borderPanel, BorderLayout.NORTH);

	return mainPanel;
    }

    private void resetButtons() {
      if (destination.equals(Locale.getString("FILE")))
	fileButton.setSelected(true);
      else if (destination.equals(Locale.getString("DATABASE")))
	databaseButton.setSelected(true);
      else
	{
	    destination = Locale.getString("ALERT_DISABLE_ALL");
	    disableButton.setSelected(true);
	}
    }

    /**
     * Return the window title.
     *
     * @return	the window title.
     */
    public String getTitle() {
	return Locale.getString("ALERT_TITLE");
    }
    
    /**
     * Update the preferences file.
     */
    public void save() {
	PreferencesManager.putAlertDestination(destination);
    }

    /**
     * Return displayed component for this page.
     *
     * @return the component to display.
     */
    public JComponent getComponent() {
	return this;
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == disableButton) {
	    destination = Locale.getString("ALERT_DISABLE_ALL");
	} else if (e.getSource() == fileButton) {
	    destination = Locale.getString("FILE");
	} else if (e.getSource() == databaseButton) {
	    //Check that the database is selected as a quote source.
	    //Warn if not

	    boolean confirmed = true;
	    if (PreferencesManager.getQuoteSource() != PreferencesManager.DATABASE) {
		int confirmValue = 
		JOptionPane.
		    showInternalConfirmDialog(desktop, 
					      Locale.
					      getString("ALERT_QUOTE_SOURCE_MESSG"),
					      Locale.getString("USE_DATABASE"),
					      JOptionPane.OK_CANCEL_OPTION);
		
		if (confirmValue != JOptionPane.OK_OPTION) {
		    confirmed = false;
		    resetButtons();
		}		
	    }
	    if (confirmed) {
		destination = Locale.getString("DATABASE");
	    } 
	} else {
	    assert false;
	}
    }
}
