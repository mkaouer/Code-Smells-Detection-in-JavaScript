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

package org.mov.chart;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.mov.chart.graph.GraphUI;
import org.mov.ui.DesktopManager;
import org.mov.util.Locale;

/**
 * Provides a dialog that enables the user to modify graph settings. Each
 * graph can have its own user interface that enables the user to modify
 * its settings. This dialog takes that interface and displays it in a
 * standard way to the user.
 *
 * @author Andrew Leppard
 * @see org.mov.chart.graph.GraphUI
 */
public class GraphSettingsDialog extends JInternalFrame {

    // No button has been pressed on the dialog
    private final static int DIALOG_IS_UP = 0;

    /** The user has pressed the ADD button. */
    public final static int ADD = 1;

    // public final static int EDIT = 2;
    // public final static int DELETE = 3;

    /** The user has pressed the CANCEL button. */
    public final static int CANCEL = 4;

    // The button that has been pressed
    private int buttonPressed = DIALOG_IS_UP;

    // The graph's user interface
    private GraphUI graphUI;

    /**
     * Create a new graph settings dialog.
     *
     * @param graphUI the graph settings user interface
     * @param name the name of the graph
     */
    public GraphSettingsDialog(GraphUI graphUI, String name) {
        super(name);

        this.graphUI = graphUI;

        buildUI(graphUI);
    }

    /**
     * Show the dialog.
     *
     * @return the button pressed, {@link #ADD} or {@link #CANCEL}.
     */
    public int showDialog() {
	// Open dialog in centre of window
	Dimension size = getPreferredSize();
	int x = (DesktopManager.getDesktop().getWidth() - size.width) / 2;
	int y = (DesktopManager.getDesktop().getHeight() - size.height) / 2;
	setBounds(x, y, size.width, size.height);

	DesktopManager.getDesktop().add(this);
        show();

	try {
	    while(buttonPressed == DIALOG_IS_UP) {
		Thread.sleep(10);
	    }
	}
	catch(Exception e) {
	    // ignore
	}

        return buttonPressed;
    }

    /**
     * Return the settings in the user interface. This method should only
     * be called if the user pressed the {@link #ADD} button.
     *
     * @return the settings
     */
    public HashMap getSettings() {
        assert buttonPressed == ADD;

        return graphUI.getSettings();
    }

    /**
     * Build the user interface based on the given graph user interface.
     *
     * @param graphUI graph user interface
     */
    private void buildUI(GraphUI graphUI) {
        JButton addButton = new JButton(Locale.getString("ADD"));
        addButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
                    if(checkSettings()) {
                        buttonPressed = ADD;
                        dispose();
                    }
                }});

        /*
        JButton editButton = new JButton(Locale.getString("EDIT"));
        editButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
                    buttonPressed = EDIT;
                    dispose();
                }});

        JButton deleteButton = new JButton(Locale.getString("DELETE"));
        deleteButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
                    buttonPressed = DELETE;
                    dispose();
                }});
        */

        JButton cancelButton = new JButton(Locale.getString("CANCEL"));
        cancelButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
                    buttonPressed = CANCEL;
                    dispose();
                }});

        JPanel panel = new JPanel();
        TitledBorder border = new TitledBorder(Locale.getString("SETTINGS"));
        BorderLayout layout = new BorderLayout();
        panel.setBorder(border);
        panel.setLayout(layout);
        panel.add(graphUI.getPanel());

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(addButton);
        //        buttonPanel.add(editButton);
        // buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Check the settings entered by the user. Display an error message if the
     * settings are invalid.
     *
     * @return <code>true</code> if they were valid.
     */
    private boolean checkSettings() {
        String error = graphUI.checkSettings();

        if(error != null)
            JOptionPane.showInternalMessageDialog(this,
                                                  error,
                                                  Locale.getString("ERROR_GRAPH_SETTINGS_TITLE"),
                                                  JOptionPane.ERROR_MESSAGE);
        return(error == null);
    }
}