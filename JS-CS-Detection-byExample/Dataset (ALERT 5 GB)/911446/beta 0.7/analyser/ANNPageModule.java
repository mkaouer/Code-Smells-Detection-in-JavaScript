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

package org.mov.analyser;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mov.main.Module;
import org.mov.parser.Expression;
import org.mov.parser.ExpressionFactory;
import org.mov.ui.AbstractTable;
import org.mov.ui.AbstractTableModel;
import org.mov.ui.Column;
import org.mov.ui.ExpressionEditorDialog;
import org.mov.ui.MenuHelper;
import org.mov.util.Locale;

public class ANNPageModule extends AbstractTable implements Module {
    private PropertyChangeSupport propertySupport;
    
    // numbers to identify columns of the table
    public static final int ORDER_COLUMN = 0;
    public static final int EXPRESSION_COLUMN = 1;
    public static final int NUMBER_COLUMN = 2;

    // constants to manage the loading/saving of expression table rows
    private final static String separatorString = GPModuleConstants.separatorString;
    private final static String nullString = GPModuleConstants.nullString;
    // empty string
    private final static String emptyString = nullString;
    
    /* 
     * Put a fixed length for the order column,
     * so that ordering the strings is the same as ordering the numbers.
     * Put blank spaces before the string representation of the number,
     * so that they are comparable as strings.
    */
    public static final int ORDER_LENGTH = 5;

    private JDesktopPane desktop;
    private Model model;
    
    // The input expressions of the ANN
    private Expression[] inputExpressions;
    
    // Menus
    private JMenuBar menuBar;
    private JMenuItem storeMenuItem;
    private JMenuItem editMenuItem;
    private JMenuItem removeMenuItem;
    
    private class Model extends AbstractTableModel {
        private List results;
        
        public Model(List columns) {
            super(columns);
            results = new ArrayList();
        }
        
        public String[] getResult(int row) {
            return (String[])results.get(row);
        }
        
        public void removeAllResults() {
            results.clear();
            
            // Notify table that the whole data has changed
            fireTableDataChanged();
        }
        
        public List getResults() {
            return results;
        }
        
        public void setResults(List results) {
            this.results = results;
            calculateOrderColumn();
            
            // Notify table that the whole data has changed
            fireTableDataChanged();
        }
        
        /*
         * This add the results without ordering them.
         * The method is called only when expressions are loaded from preferences,
         * we do not calulateOrderColumn because we do not want to change the order
         * column.
         */
        public void addResults(List results) {
            this.results.addAll(results);
            
            // Notify table that the whole data has changed
            fireTableDataChanged();
        }
        
        /*
         * This add the results ordering them.
         * The method is called only when add one element to the expressions' table.
         */
        public void addResult(List results) {
            this.results.addAll(results);
            calculateOrderColumn();
            
            // Notify table that the whole data has changed
            fireTableDataChanged();
        }
        
        public int getRowCount() {
            return results.size();
        }
        
        public Object getValueAt(int row, int column) {
            if(row >= getRowCount())
                return "";
            
            String[] result =
                (String[])results.get(row);
            
            if(column == ORDER_COLUMN) {
                return result[ORDER_COLUMN];
            
            } else if(column == EXPRESSION_COLUMN) {
                return result[EXPRESSION_COLUMN];
            
            } else {
                assert false;
                return "";
            }
        }
        
        public boolean setValueAt(int row, int column, String value) {
            if(row >= getRowCount())
                return false;
            
            String[] result = (String[])results.get(row);
            String[] object = new String[NUMBER_COLUMN];
            object[ORDER_COLUMN] = new String(result[ORDER_COLUMN]);
            object[EXPRESSION_COLUMN] = new String(result[EXPRESSION_COLUMN]);
            object[column] = new String(value);
            
            result = (String[])results.set(row, object);
            result = null;
            
            return true;
        }
        
        // Recalculate the order column, so that all elements are always correctly ordered
        private void calculateOrderColumn() {
            // Resize all strings to ORDER_LENGTH length
            for (int jj=0; jj<this.getRowCount(); jj++) {
                String newValue = (String)this.getValueAt(jj, ORDER_COLUMN);
                int len = newValue.length();
                for (int kk=0; kk<ORDER_LENGTH-len; kk++) {
                    newValue = " " + newValue;
                }
                this.setValueAt(jj, ORDER_COLUMN, newValue);
            }
            
            // Look for empty spaces
            int ii=0;
            boolean empty = true;
            while (ii<this.getRowCount()) {
                // Look for empty
                for (int jj=0; jj<this.getRowCount(); jj++) {
                    int current = -1;
                    String strCurr =
                            new String(((String)this.getValueAt(jj, ORDER_COLUMN)).trim());
                    try {
                        current = Integer.parseInt(strCurr);
                    } catch (Exception ex) {
                        // Nothing to do ii is always an integer value
                    }
                    if (ii == current) {
                        empty = false;
                        break;
                    }
                }
                // Recalculate the order column, so that there is no more empty
                if (empty) {
                    for (int jj=0; jj<this.getRowCount(); jj++) {
                        int current = -1;
                        String strCurr =
                                new String(((String)this.getValueAt(jj, ORDER_COLUMN)).trim());
                        try {
                            current = Integer.parseInt(strCurr);
                        } catch (Exception ex) {
                            // Nothing to do ii is always an integer value
                        }
                        // Shift by one less, all the order values higer than empty one
                        if (current > ii) {
                            String newValue = "";
                            try {
                                newValue = Integer.toString(current-1);
                            } catch (Exception ex) {
                                // Nothing to do ii is always an integer value
                            }
                            int len = newValue.length();
                            for (int kk=0; kk<ORDER_LENGTH-len; kk++) {
                                newValue = " " + newValue;
                            }
                            this.setValueAt(jj, ORDER_COLUMN, newValue);
                        }
                    }
                    ii = 0;
                } else {
                    empty = true;
                    ii++;
                }
            }
        }
        
    }
    
    /**
     * Construct a new input artificial neural network parameters grid.
     * It manages:
     * the input of artificial neural network table of expressions.
     *
     * @param desktop the desktop
     */
    public ANNPageModule(JDesktopPane desktop) {
        this.desktop = desktop;
        
        // Define the columns of the table of expressions
        List columns = new ArrayList();
        // Add the order column, it's the column containing the numbers that specify the order
        // of the expression in the artificial neural network input
        columns.add(new Column(ORDER_COLUMN,
            Locale.getString("ORDER_EXPRESSION"),
            Locale.getString("ORDER_EXPRESSION_COLUMN_HEADER"),
            String.class,
            Column.VISIBLE));
        // Add the expression column, it's the column containing the expressions
        columns.add(new Column(EXPRESSION_COLUMN,
            Locale.getString("INPUT_EXPRESSION"),
            Locale.getString("INPUT_EXPRESSION_COLUMN_HEADER"),
            String.class,
            Column.VISIBLE));
        
        model = new Model(columns);
        setModel(model);
        
        model.addTableModelListener(this);
        
        propertySupport = new PropertyChangeSupport(this);
        
        addMenu();
        
        // If the user clicks on the table trap it.
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                handleMouseClicked(evt);
            }
        });
        
        sortResults();
        showColumns(model);
    }
    
    // If the user right clicks over the table, open up a popup menu.
    private void handleMouseClicked(MouseEvent event) {
        Point point = event.getPoint();
        
        // Right click on the table - raise menu
        if(event.getButton() == MouseEvent.BUTTON3) {
            JPopupMenu menu = new JPopupMenu();
            
            // Add a menu item to add a new expression to the table of expressions
            JMenuItem popupStoreMenuItem =
            new JMenuItem(Locale.getString("ADD_EXPRESSION"));
            popupStoreMenuItem.setEnabled(getSelectedRowCount() == 1);
            popupStoreMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addExpression();
                }});
            menu.add(popupStoreMenuItem);

            // Add a menu item to edit an existing expression of the table of expressions
            JMenuItem popupEditMenuItem =
            new JMenuItem(Locale.getString("EDIT_EXPRESSION"));
            popupEditMenuItem.setEnabled(getSelectedRowCount() == 1);
            popupEditMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editExpression();
                }});
            menu.add(popupEditMenuItem);

            // Add a menu item to remove the selected expressions of the table of expressions
            JMenuItem popupRemoveMenuItem =
            new JMenuItem(Locale.getString("REMOVE"));
            popupRemoveMenuItem.setEnabled(getSelectedRowCount() >= 1);
            popupRemoveMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    removeSelectedResults();
                    checkMenuDisabledStatus();
                }});
            menu.add(popupRemoveMenuItem);

            menu.show(this, point.x, point.y);
        }
        
    }
    
    /*
     * Sort the table considering the order column.
     * The method is called when the ANNPageModule is created,
     * so that the values saved in the preferences be ordered correctly.
     */
    private void sortResults() {
        // Call a method of SortedTable class so that the values are sorted by ORDER_COLUMN
        this.setModel(model, this.ORDER_COLUMN, this.SORT_DOWN);
    }
    
    /*
     * Allows the user to add a new expression
     */
    public void addExpression() {
            
        Thread thread = new Thread(new Runnable() {
            public void run() {
                String newExpression = ExpressionEditorDialog.showEditDialog(
                        Locale.getString("ADD_EXPRESSION"),
                        "");
                String[] newExpressionArray = new String[NUMBER_COLUMN];
                // we don't care about order,
                // calculateOrderColumn method will do instead.
                newExpressionArray[ORDER_COLUMN] = 
                        (new Integer(model.getRowCount())).toString();
                if ((newExpression != null) && (!newExpression.equals(""))) {
                    newExpressionArray[EXPRESSION_COLUMN] = newExpression;
                    List newExpressionList = new ArrayList();
                    newExpressionList.add(newExpressionArray);
                    model.addResult(newExpressionList);
                    model.fireTableDataChanged();
                    repaint();
                }
            }});

            thread.start();
            
    }
    
    /*
     * Allows the user to modify the selected expression
     */
    public void editExpression() {
        // Get result at row
        final int row = getSelectedRow();
        
        // Don't do anything if we couldn't retrieve the selected row
        if(row != -1) {
            final String[] result = model.getResult(row);
            
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    String newExpression = ExpressionEditorDialog.showEditDialog(
                            Locale.getString("EDIT_EXPRESSION"),
                            result[EXPRESSION_COLUMN]);
                    model.setValueAt(row, EXPRESSION_COLUMN, newExpression);
                    model.fireTableDataChanged();
                    repaint();
                }});
                
                thread.start();
        }
    }

    /*
     * Removes all the selected results from the table
     */
    public void removeSelectedResults() {
        
        // Get selected rows and put them in order from highest to lowest
        int[] rows = getSelectedRows();
        List rowIntegers = new ArrayList();
        for(int i = 0; i < rows.length; i++)
            rowIntegers.add(new Integer(rows[i]));
        
        List sortedRows = new ArrayList(rowIntegers);
        Collections.sort(sortedRows);
        Collections.reverse(sortedRows);
        
        // Now remove them from the results list starting from the highest row
        // to the lowest
        sortResults();
        List results = model.getResults();
        Iterator iterator = sortedRows.iterator();
        
        while(iterator.hasNext()) {
            Integer rowToRemove = (Integer)iterator.next();
            
            results.remove(rowToRemove.intValue());
        }
        
        model.setResults(results);
        sortResults();
    }

    // Some menu items are only enabled/disabled depending on what is
    // selected in the table or by the size of the table
    private void checkMenuDisabledStatus() {
        int numberOfSelectedRows = getSelectedRowCount();
        
        storeMenuItem.setEnabled(numberOfSelectedRows >= 0);
        editMenuItem.setEnabled(numberOfSelectedRows == 1);
        removeMenuItem.setEnabled(numberOfSelectedRows >= 1);
    }
    
    // Add a menu
    private void addMenu() {
        menuBar = new JMenuBar();
        
        JMenu resultMenu = MenuHelper.addMenu(menuBar, Locale.getString("RESULT"));
        
        storeMenuItem = new JMenuItem(Locale.getString("ADD_EXPRESSION"));
        storeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addExpression();
            }});
        resultMenu.add(storeMenuItem);

        editMenuItem = new JMenuItem(Locale.getString("EDIT_EXPRESSION"));
        editMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editExpression();
            }});
        resultMenu.add(editMenuItem);

        resultMenu.addSeparator();

        removeMenuItem = new JMenuItem(Locale.getString("REMOVE"));
        removeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeSelectedResults();
                checkMenuDisabledStatus();
            }});
        resultMenu.add(removeMenuItem);

        JMenu columnMenu = createShowColumnMenu(model);
        resultMenu.add(columnMenu);

        // Listen for changes in selection so we can update the menus
        getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                checkMenuDisabledStatus();
            }

        });

        checkMenuDisabledStatus();
    }
    
    /*
     * Add a row in the table of expressions
     *
     * @param   expression the expression that's going to be added in the table of expressions
     */
    public void addRowTable(String expression) {
        List results = new ArrayList();
        String[] object = new String[NUMBER_COLUMN];
        object[EXPRESSION_COLUMN] = new String(expression);
        results.add(object);
        model.addResults(results);
        checkMenuDisabledStatus();
        validate();
        repaint();
    }
    
    public String getTitle() {
        return Locale.getString("ANN_PAGE_PARAMETERS_LONG");
    }
    
    public void save() {
        // Free up precious memory
        model.removeAllResults();
    }
    
    public void save(HashMap settings, String idStr) {
        List results = model.getResults();
        // Clean old settings
        for (int i=0; i<settings.size(); i++) {
            settings.put(idStr + (new Integer(i)).toString(), emptyString);
        }
        // Save the new settings
        int counterNew = 0;
        for (int i=0; i<results.size(); i++) {
            String[] values = new String[NUMBER_COLUMN];
            values = (String[])results.get(i);
            // Put a wild char (nullString) to manage the unlucky
            // situation when there is a null field.
            // In that case we can't manage the split operation
            // without using the wild char
            for (int j=0; j<values.length; j++) {
                if (values[j].equals("")) {
                    values[j] = new String(nullString);
                }
            }
            if (!values[EXPRESSION_COLUMN].equals(nullString)) {
                String value = values[ORDER_COLUMN] + separatorString + values[EXPRESSION_COLUMN];
                settings.put(idStr + (new Integer(counterNew)).toString(), value);
                counterNew++;
            }
            values = null;
        }
    }
    
    public void load(String value) {
        String values[] = value.split(separatorString);
        if (values.length==NUMBER_COLUMN) {
            // If the string is a wild char (nullString)
            // change it to null string
            for (int j=0; j<values.length; j++) {
                if (values[j].equals(nullString))
                    values[j] = new String("");
            }
            if (!values[EXPRESSION_COLUMN].equals(nullString)) {
                List newModel = new ArrayList();
                newModel.add(values);
                model.addResults(newModel);
            }
        }
    }
    
    public boolean parse() {
        // Check if input strings are ok for parsing
        inputExpressions = new Expression[model.getRowCount()];
        for (int ii=0; ii<inputExpressions.length; ii++) {
            // We must order the expressions according to order column
            int index=0;
            try {
                index = Integer.parseInt(((String)model.getValueAt(ii, ORDER_COLUMN)).trim());
            } catch (Exception ex) {
                // Nothing to do ii is always an integer value
            }
            String iExp = (String)model.getValueAt(ii, EXPRESSION_COLUMN);
            // We get the index expression, that is the expression put
            // in the correct position (==index)
            inputExpressions[index] = 
                    ExpressionFactory.newExpression(iExp);
            if (inputExpressions[index] == null) {
                JOptionPane.showInternalMessageDialog(desktop,
                    Locale.getString("ERROR_PARSING_EXPRESSION",
                        iExp),
                    Locale.getString("INVALID_ANN_ERROR"),
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }
    
    /*
     * Get input expressions of the artificial neural network
     * The input are not ordered, we'll order them later with the parse() method
     *
     * @return   an expression array with input expressions of ANN
     */
    public Expression[] getInputExpressions() {
        return inputExpressions;
    }

    public void addModuleChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removeModuleChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    public ImageIcon getFrameIcon() {
        return null;
    }
    
    public JComponent getComponent() {
        return this;
    }
    
    public JMenuBar getJMenuBar() {
        return menuBar;
    }
    
    public boolean encloseInScrollPane() {
        return true;
    }
    
}
