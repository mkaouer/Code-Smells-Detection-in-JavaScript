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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.table.TableColumn;

import org.mov.macro.StoredMacro;
import org.mov.ui.AbstractTableModel;
import org.mov.ui.MacroEditor;
import org.mov.ui.MainMenu;
import org.mov.util.Locale;

/**
 * This class allows the user to set their preferences for macro loading and
 * execution
 *
 * @author Dan Makovec venice@makovec.net
 */
public class MacrosPage extends JPanel implements PreferencesPage {
    private JDesktopPane desktop;

    private JTextField directory_text = null;

    private JTable macros_table = null;

    private AbstractTableModel table_model = null;

    /**
     * Used for building the table combo box for determining how many entries to
     * add to the combo box
     */
    private int startup_items = 0;

    private static final int FILENAME_COLUMN = 0;

    private static final int NAME_COLUMN = 1;

    private static final int STARTUP_COLUMN = 2;

    private static final int INMENU_COLUMN = 3;

    final String[] names = { Locale.getString("FILENAME_COLUMN_HEADER"),
            Locale.getString("MACRO_NAME_COLUMN_HEADER"),
            Locale.getString("STARTUP_COLUMN_HEADER"),
            Locale.getString("INMENU_COLUMN_HEADER") };

    private List stored_macros;

    /** The element ID of the last selected macro */
    private int selected_row = -1;

    private int edited_row = -1;

    private StoredMacro edited_macro = null;
    private MacroEditor editor = null;
    private JButton add_button = null;
    private JButton edit_button = null;
    private JButton delete_button = null;
    private JButton up_button = null;
    private JButton down_button = null;

    /*
     * (non-Javadoc)
     * 
     * @see org.mov.prefs.PreferencesPage#getTitle()
     */
    public String getTitle() {
        return Locale.getString("MACROS_PAGE_TITLE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mov.prefs.PreferencesPage#save()
     */
    public void save() {
        PreferencesManager.putDirectoryLocation("macros", directory_text.getText());
        PreferencesManager.putStoredMacros(stored_macros);
        MainMenu.getInstance().buildMacroMenu();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mov.prefs.PreferencesPage#getComponent()
     */
    public JComponent getComponent() {
        return this;
    }

    /**
     * This is the default constructor
     */
    public MacrosPage(JDesktopPane desktop_pane) {
        super();
        this.desktop = desktop_pane;
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(300, 200);
        this.add(getDir_panel(), java.awt.BorderLayout.NORTH);
        this.add(getTable_panel(), java.awt.BorderLayout.CENTER);
        this.add(getButton_panel(), java.awt.BorderLayout.SOUTH);
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDir_panel() {
        JButton browse_button = null;

        JPanel dir_panel = new JPanel();
        dir_panel.setLayout(new BorderLayout());
        javax.swing.JLabel label = new JLabel();
        label.setText(Locale.getString("MACROS_DIRECTORY"));
        dir_panel.add(label, java.awt.BorderLayout.WEST);
        dir_panel.add(getDirectory_text(), java.awt.BorderLayout.CENTER);
        dir_panel.add(getBrowse_button(), java.awt.BorderLayout.EAST);
        return dir_panel;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDirectory_text() {
        if (directory_text == null) {
            directory_text = new JTextField();
            directory_text.setText(PreferencesManager.getDirectoryLocation("macros"));
        }
        return directory_text;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getBrowse_button() {
        JButton browse_button = new JButton();
        browse_button.setText(Locale.getString("BROWSE"));
        browse_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get directory
                JFileChooser chooser;
                String lastDirectory = PreferencesManager.getDirectoryLocation("macros");
                
                if (lastDirectory != null)
                    chooser = new JFileChooser(lastDirectory);
                else
                    chooser = new JFileChooser();

                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int action = chooser.showOpenDialog(desktop);

                if (action == JFileChooser.APPROVE_OPTION) {
                    // Set the new macro directory
                    File file = chooser.getSelectedFile();
                    String directory = file.getPath();
                    directory_text.setText(directory);
                    PreferencesManager.putDirectoryLocation("macros",
                            directory);
                    stored_macros = PreferencesManager.getStoredMacros();
                    table_model.fireTableDataChanged();

                }
            }
        });

        return browse_button;
    }

    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTable_panel() {
        JPanel table_panel = new JPanel();
        table_panel.setLayout(new BorderLayout());
        table_panel.add(getJScrollPane(), BorderLayout.CENTER);
        return table_panel;
    }

    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        JScrollPane jScrollPane = new JScrollPane(getMacros_table());
        return jScrollPane;
    }

    /**
     * This method initializes jTable
     * 
     * @return javax.swing.JTable
     */
    private JTable getMacros_table() {
        if (macros_table == null) {

            // Set up the data
            stored_macros = PreferencesManager.getStoredMacros();
            // Define the table model
            table_model = new AbstractTableModel() {
                public int getColumnCount() {
                    return names.length;
                }

                public int getRowCount() {
                    return stored_macros.size();
                }

                public boolean isCellEditable(int row, int col) {
                    if (col != FILENAME_COLUMN)
                        return true;
                    return false;
                }

                public String getColumnName(int column) {
                    return names[column];
                }

                public Class getColumnClass(int c) {
                    return getValueAt(0, c).getClass();
                }

                public void setValueAt(Object value, int row, int col) {
                    StoredMacro m = (StoredMacro) stored_macros.get(row);
                    selected_row = row;
                    switch (col) {
                    case NAME_COLUMN:
                        m.setName(value.toString());
                        break;

                    case FILENAME_COLUMN:
                        m.setFilename(value.toString());
                        break;

                    case STARTUP_COLUMN:
                        String s = value.toString();
                        int old_seq = m.getStart_sequence();
                        if (s.equals(Locale.getString("NO_START"))) {
                            for (int i = 0; i < stored_macros.size(); i++) {
                                if (i != row) {
                                    StoredMacro t = (StoredMacro) stored_macros
                                            .get(i);
                                    int other_old_seq = t.getStart_sequence();
                                    if (other_old_seq > 1
                                            && other_old_seq > old_seq)
                                        t.setStart_sequence(other_old_seq - 1);
                                }
                            }
                            if (m.isOn_startup())
                                startup_items--;
                            setComboEditor();
                            m.setOn_startup(false);
                            m.setStart_sequence(0);

                        } else {
                            Integer i_value = (Integer) value;
                            int new_seq = i_value.intValue();
                            if (!m.isOn_startup())
                                startup_items++;
                            for (int i = 0; i < stored_macros.size(); i++) {
                                if (i != row) {
                                    StoredMacro t = (StoredMacro) stored_macros
                                            .get(i);
                                    int other_old_seq = t.getStart_sequence();

                                    // Case 1: Moving from high to low
                                    if (t.isOn_startup()
                                            && other_old_seq >= new_seq
                                            && (other_old_seq < old_seq || !m
                                                    .isOn_startup())) {
                                        t.setStart_sequence(other_old_seq + 1);

                                        // Case 2: Moving from low to high
                                    } else if (t.isOn_startup()
                                            && other_old_seq > 1
                                            && other_old_seq > old_seq
                                            && other_old_seq <= new_seq) {
                                        t.setStart_sequence(other_old_seq - 1);
                                    }
                                }
                            }
                            setComboEditor();
                            m.setOn_startup(true);
                            m.setStart_sequence(new_seq);

                        }
                        fireTableDataChanged();
                        break;
                    case INMENU_COLUMN:
                        Boolean b = (Boolean) value;
                        m.setIn_menu(b.booleanValue());
                        break;
                    default:
                        System.err
                                .println("Macros table: invalid column selected");
                    }
                    ;
                    stored_macros.set(selected_row, m);
                }

                public Object getValueAt(int row, int col) {
                    StoredMacro m = (StoredMacro) stored_macros.get(row);
                    switch (col) {
                    case NAME_COLUMN:
                        return m.getName();

                    case FILENAME_COLUMN:
                        return m.getFilename();

                    case STARTUP_COLUMN:
                        if (m.isOn_startup()) {
                            return new Integer(m.getStart_sequence());
                        } else {
                            return Locale.getString("NO_START");
                        }

                    case INMENU_COLUMN:
                        return new Boolean(m.isIn_menu());

                    default:
                        System.err
                                .println("Macros table: invalid column selected");
                        return null;
                    }
                } // getValueAt()
            };

            macros_table = new JTable(table_model);

            // Customize the standard text editor
            JTextField t = new JTextField();
            t.setBackground(java.awt.Color.pink);
            javax.swing.text.Caret caret = t.getCaret();
            caret.setVisible(true);
            t.setCaret(caret);
            DefaultCellEditor ce = new DefaultCellEditor(t);
            macros_table.setDefaultEditor(String.class, ce);

            // Set the checkbox column width
            TableColumn mc = macros_table.getColumn(Locale
                    .getString("INMENU_COLUMN_HEADER"));
            int width = (int) new javax.swing.JCheckBox().getPreferredSize()
                    .getWidth();
            mc.setWidth(width);
            mc.setPreferredWidth(width);

            for (int i = 0; i < stored_macros.size(); i++) {
                if (((StoredMacro) stored_macros.get(i)).isOn_startup()) {
                    startup_items++;
                }
            }
            setComboEditor();
            macros_table.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    selected_row = macros_table.getSelectedRow();
                    edit_button.setEnabled(true);
                    delete_button.setEnabled(true);
                    int rows = stored_macros.size();
                    if (selected_row < rows - 1) {
                        down_button.setEnabled(true);
                    } else {
                        down_button.setEnabled(false);
                    }
                    if (selected_row > 0) {
                        up_button.setEnabled(true);
                    } else {
                        up_button.setEnabled(false);
                    }
                    if (e.getClickCount() > 1) {
                        editMacro();
                    }
                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseReleased(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }
            });
        }
        return macros_table;
    }

    /**
     * Set up the editor for startup position
     *  
     */
    private void setComboEditor() {
        // Set up cell editor for startup sequence
        JComboBox combo = new JComboBox();
        combo.addItem(Locale.getString("NO_START"));
        int add_new = 0;
        if (startup_items != stored_macros.size())
            add_new = 1;
        for (int i = 1; i <= startup_items + add_new; i++) {
            combo.addItem(new Integer(i));
        }
        TableColumn col = macros_table.getColumn(Locale
                .getString("STARTUP_COLUMN_HEADER"));
        col.setCellEditor(new DefaultCellEditor(combo));
    }

    /**
     * This method initializes jPanel2
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButton_panel() {
        JPanel button_panel = new JPanel();
        button_panel.add(getAdd_button(), null);
        button_panel.add(getEdit_button(), null);
        button_panel.add(getDelete_button(), null);
        button_panel.add(getUp_button(), null);
        button_panel.add(getDown_button(), null);
        return button_panel;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUp_button() {
        up_button = new JButton();
        up_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = macros_table.getSelectedRow();
                if (row > 0) {
                    StoredMacro prev = (StoredMacro) stored_macros.get(row - 1);
                    stored_macros.set(row - 1, stored_macros.get(row));
                    stored_macros.set(row, prev);
                    table_model.fireTableDataChanged();
                    macros_table.changeSelection(row - 1, macros_table
                            .getSelectedColumn(), false, false);
                }
            }
        });
        up_button.setText(Locale.getString("UP"));
        up_button.setEnabled(false);
        return up_button;
    }

    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getDown_button() {
        down_button = new JButton();
        down_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = macros_table.getSelectedRow();
                int max = stored_macros.size();
                if (row < max) {
                    StoredMacro next = (StoredMacro) stored_macros.get(row + 1);
                    stored_macros.set(row + 1, stored_macros.get(row));
                    stored_macros.set(row, next);
                    table_model.fireTableDataChanged();
                    macros_table.changeSelection(row + 1, macros_table
                            .getSelectedColumn(), false, false);
                }
            }
        });
        down_button.setText(Locale.getString("DOWN"));
        down_button.setEnabled(false);
        return down_button;
    }

    /**
     * This method initializes the edit button
     * 
     * @return javax.swing.JButton
     */
    private JButton getEdit_button() {
        if (edit_button == null) {
            edit_button = new JButton();
            edit_button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editMacro();
                }
            });
            edit_button.setText(Locale.getString("EDIT"));
            edit_button.setEnabled(false);
        }
        return edit_button;
    }

    /**
     * This method initializes the add button
     * 
     * @return javax.swing.JButton
     */
    private JButton getAdd_button() {
        if (add_button == null) {
            add_button = new JButton();
            add_button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    selected_row = -1;
                    if (editor != null) { return; }
                    editMacro(new StoredMacro());
                }
            });
            add_button.setText(Locale.getString("ADD"));
        }
        return add_button;
    }

    /**
     * This method initializes the delete button
     * 
     * @return javax.swing.JButton
     */
    private JButton getDelete_button() {
        if (delete_button == null) {
            delete_button = new JButton();
            delete_button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (selected_row == -1) {return; }
                    StoredMacro macro = (StoredMacro) stored_macros
                            .get(selected_row);
                    String filepath = PreferencesManager.getDirectoryLocation("macros")
                            + File.separator + macro.getFilename();
                    int response = JOptionPane.showInternalConfirmDialog(delete_button,
                            Locale.getString("SURE_DELETE_MACRO", macro
                                    .getName(), filepath), Locale
                                    .getString("DELETE_MACRO_TITLE"),
                            JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        if (macro.delete()) {
                            stored_macros.remove(selected_row);
                            PreferencesManager.putStoredMacros(stored_macros);
                            selected_row = -1;
                            table_model.fireTableDataChanged();
                        } else {
                            JOptionPane.showInternalMessageDialog(
                                    delete_button, Locale.getString(
                                            "UNABLE_TO_DELETE_NAME_ERROR",
                                            filepath), Locale
                                            .getString("WARNINGTITLE"),
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
            delete_button.setText(Locale.getString("DELETE"));
        }
        delete_button.setEnabled(false);
        return delete_button;
    }

    /**
     * This function brings up an editor window for the currently selected macro
     *  
     */
    public void editMacro() {
        selected_row = macros_table.getSelectedRow();
        edited_row = selected_row;
        editMacro((StoredMacro) stored_macros.get(edited_row));
    }

    public void editMacro(StoredMacro macro) {
        edited_macro = macro;
        if (editor != null) { return; }
        editor = new MacroEditor(edited_macro);
        add_button.setEnabled(false);
        edit_button.setEnabled(false);
        editor.addInternalFrameListener(new InternalFrameListener() {
            public void internalFrameClosed(InternalFrameEvent arg0) {
                add_button.setEnabled(true);
                edit_button.setEnabled(true);
                delete_button.setEnabled(true);
                up_button.setEnabled(true);	
                if (editor.isOk_clicked()) {
                    if (edited_row == -1) {
                        System.out.println("clicked ok");
                        stored_macros.add(edited_macro);
                        edited_row = stored_macros.size() - 1;
                        selected_row = edited_row;
                    } else {
                        stored_macros.set(edited_row, edited_macro);
                    }
                    PreferencesManager.putStoredMacros(stored_macros);
                }
                table_model.fireTableDataChanged();
                macros_table.changeSelection(edited_row, 0, false, false);
                editor = null;
            }

            public void internalFrameActivated(InternalFrameEvent arg0) {}
            public void internalFrameClosing(InternalFrameEvent e) {}
            public void internalFrameDeactivated(InternalFrameEvent e) {}
            public void internalFrameDeiconified(InternalFrameEvent arg0) {}
            public void internalFrameIconified(InternalFrameEvent arg0) {}
            public void internalFrameOpened(InternalFrameEvent arg0) {}
        });
    }

}

