/*
 * Created on 23-Sep-2004
 *
 * Merchant of Venice - technical analysis software for the stock market.
 * Copyright (C) 2002 Andrew Leppard (aleppard@picknowl.com.au)
 * This portion of code Copyright (C) 2004 Dan Makovec (venice@makovec.net)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package nz.org.venice.macro;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import nz.org.venice.main.Module;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.prefs.settings.Settings;
import nz.org.venice.util.Locale;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JButton;
/**
 * @author Dan Makovec venice@makovec.net
 *
 * This module gives the user to define and edit Python macro scripts
 * which control the behaviour of the overall application
 */
public class MacroModule extends JPanel implements Module, ActionListener {

	private JPanel list_panel = null;
	private JPanel button_panel = null;
	private JScrollPane macro_pane = null;
	private JList macro_list = null;
	private JButton new_button = null;
	private JButton edit_button = null;
	private JButton import_button = null;
	private JButton delete_button = null;

    private PropertyChangeSupport propertySupport;

    private String frameIcon = "nz/org/venice/images/TableIcon.gif";

    private Settings settings;

    /* (non-Javadoc)
     * @see nz.org.venice.main.Module#getTitle()
     */
    public String getTitle() {
        return Locale.getString("MACRO_MANAGER");
    }

    /* (non-Javadoc)
     * @see nz.org.venice.main.Module#addModuleChangeListener(java.beans.PropertyChangeListener)
     */
    public void addModuleChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);

    }

    /* (non-Javadoc)
     * @see nz.org.venice.main.Module#removeModuleChangeListener(java.beans.PropertyChangeListener)
     */
    public void removeModuleChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);

    }

    /* (non-Javadoc)
     * @see nz.org.venice.main.Module#getFrameIcon()
     */
    public ImageIcon getFrameIcon() {
    	return new ImageIcon(ClassLoader.getSystemClassLoader().getResource(frameIcon));
    }

    /* (non-Javadoc)
     * @see nz.org.venice.main.Module#getComponent()
     */
    public JComponent getComponent() {
        return this;
    }

    /* (non-Javadoc)
     * @see nz.org.venice.main.Module#getJMenuBar()
     */
    public JMenuBar getJMenuBar() {
        return null;
    }

    /* (non-Javadoc)
     * @see nz.org.venice.main.Module#encloseInScrollPane()
     */
    public boolean encloseInScrollPane() {
        return false;
    }

    /* (non-Javadoc)
     * @see nz.org.venice.main.Module#save()
     */
    public void save() {
        // TODO Auto-generated method stub

    }

    private JDesktopPane desktop;

	/**
	 * This is the default constructor
	 */
	public MacroModule(JDesktopPane desktop) {
		this.desktop = desktop;
		propertySupport = new PropertyChangeSupport(this);
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private  void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(300,200);
		this.add(getJPanel(), java.awt.BorderLayout.CENTER);
		this.add(getJPanel1(), java.awt.BorderLayout.SOUTH);
	}
	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (list_panel == null) {
			list_panel = new JPanel();
			list_panel.setLayout(new BorderLayout());
			list_panel.add(getMacro_pane(), java.awt.BorderLayout.CENTER);
		}
		return list_panel;
	}
	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (button_panel == null) {
			button_panel = new JPanel();
			button_panel.add(getJButton(), null);
			button_panel.add(getJButton2(), null);
			button_panel.add(getJButton1(), null);
			button_panel.add(getJButton3(), null);
		}
		return button_panel;
	}
	/**
	 * This method initializes macro_pane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getMacro_pane() {
		if (macro_pane == null) {
			macro_pane = new JScrollPane();
			macro_pane.setViewportView(getJList());
		}
		return macro_pane;
	}
	/**
	 * This method initializes jList
	 *
	 * @return javax.swing.JList
	 */
	private JList getJList() {
		if (macro_list == null) {
			macro_list = new JList();
		}
		return macro_list;
	}
	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (new_button == null) {
			new_button = new JButton();
			new_button.setText(Locale.getString("NEW"));
			new_button.setMnemonic(java.awt.event.KeyEvent.VK_N);
			new_button.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
				}
			});
		}
		return new_button;
	}
	/**
	 * This method initializes jButton1
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton1() {
		if (edit_button == null) {
			edit_button = new JButton();
			edit_button.setMnemonic(java.awt.event.KeyEvent.VK_E);
			edit_button.setText(Locale.getString("EDIT"));
		}
		return edit_button;
	}
	/**
	 * This method initializes jButton2
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton2() {
		if (import_button == null) {
			import_button = new JButton();
			import_button.setText(Locale.getString("IMPORT"));
			import_button.setMnemonic(java.awt.event.KeyEvent.VK_I);
		}
		return import_button;
	}
	/**
	 * This method initializes jButton3
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton3() {
	    if (delete_button == null) {
	        delete_button = new JButton();
	        delete_button.setMnemonic(java.awt.event.KeyEvent.VK_D);
	        delete_button.setText(Locale.getString("DELETE"));
	    }
	    return delete_button;
	}

	public void actionPerformed(ActionEvent e) {
	    if (e.getSource() == new_button) {
	    } else if (e.getSource() == edit_button) {
	    } else if (e.getSource() == import_button) {
	        JFileChooser chooser;
	        String lastDirectory = PreferencesManager.getDirectoryLocation("macros");

	        if(lastDirectory != null)
	            chooser = new JFileChooser(lastDirectory);
	        else
	            chooser = new JFileChooser();

	        chooser.setMultiSelectionEnabled(false);
	        int action = chooser.showOpenDialog(desktop);
	        if(action == JFileChooser.APPROVE_OPTION) {
	            // Remember directory
	            lastDirectory = chooser.getCurrentDirectory().getAbsolutePath();
	            PreferencesManager.putDirectoryLocation("importer",lastDirectory);

	            File file = chooser.getSelectedFile();
	            if (file != null) {
	            }
	        }
	    } else if (e.getSource() == delete_button) {
	    }
	}

    public Settings getSettings() {
	return settings;
    }
}
