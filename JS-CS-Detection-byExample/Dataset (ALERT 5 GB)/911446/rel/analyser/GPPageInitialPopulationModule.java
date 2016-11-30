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

package nz.org.venice.analyser;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nz.org.venice.main.Module;
import nz.org.venice.ui.AbstractTable;
import nz.org.venice.ui.AbstractTableModel;
import nz.org.venice.ui.Column;
import nz.org.venice.ui.ConfirmDialog;
import nz.org.venice.ui.ExpressionEditorDialog;
import nz.org.venice.ui.TextsEditorDialog;
import nz.org.venice.ui.MenuHelper;
import nz.org.venice.util.Locale;
import nz.org.venice.prefs.settings.Settings;
import nz.org.venice.util.VeniceLog;

public class GPPageInitialPopulationModule extends AbstractTable implements Module {
    private PropertyChangeSupport propertySupport;
    
    public static final int BUY_RULE_COLUMN = 0;
    public static final int SELL_RULE_COLUMN = 1;
    public static final int PERCENT_COLUMN = 2;
    public static final int NUMBER_COLUMN = 3;

    private final static String separatorString = GPModuleConstants.separatorString;
    private final static String nullString = GPModuleConstants.nullString;
    // empty string
    private final static String emptyString = nullString + separatorString + nullString  + separatorString + nullString;
    
    private final static String format = GPModuleConstants.format;
    private final static double PERCENT_DOUBLE = GPModuleConstants.PERCENT_DOUBLE;
    private final static int PERCENT_INT = GPModuleConstants.PERCENT_INT;

    private Page page;
    private Model model;
    private int[] perc = new int[0];
    private long seed = System.currentTimeMillis();
    private Random random = new Random(seed);
    private Settings settings;
    
    // Menus
    private JMenuBar menuBar;
    private JMenuItem editBuyRuleMenuItem;
    private JMenuItem editSellRuleMenuItem;
    private JMenuItem editPercMenuItem;
    private JMenuItem viewBuyRuleMenuItem;
    private JMenuItem viewSellRuleMenuItem;
    private JMenuItem storeBuyRuleMenuItem;
    private JMenuItem storeSellRuleMenuItem;
    private JMenuItem removeMenuItem;
    private JMenuItem removeAllMenuItem;
    
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
            
            // Notify table that the whole data has changed
            fireTableDataChanged();
        }
        
        public void addResults(List results) {
            this.results.addAll(results);
            
            // Notify table that the whole data has changed
            fireTableDataChanged();
        }
        
        private void addEmpty() {
            String values[] = new String[NUMBER_COLUMN];
            for (int i=0; i<values.length; i++) {
                values[i] = new String("");
            }
            this.results.add(values);
        }
        
        public int getRowCount() {
            return results.size();
        }
        
        public Object getValueAt(int row, int column) {
            if(row >= getRowCount())
                return "";
            
            String[] result =
                (String[])results.get(row);
            
            if(column == BUY_RULE_COLUMN)
                return result[BUY_RULE_COLUMN];
            
            else if(column == SELL_RULE_COLUMN)
                return result[SELL_RULE_COLUMN];
            
            else if(column == PERCENT_COLUMN)
                return result[PERCENT_COLUMN];
            
            else {
                assert false;
                return "";
            }
        }
        
        public boolean setValueAt(int row, int column, String value) {
            if(row >= getRowCount())
                return false;
            
            String[] result = (String[])results.get(row);
            String[] object = new String[NUMBER_COLUMN];
            object[BUY_RULE_COLUMN] = new String(result[BUY_RULE_COLUMN]);
            object[SELL_RULE_COLUMN] = new String(result[SELL_RULE_COLUMN]);
            object[PERCENT_COLUMN] = new String(result[PERCENT_COLUMN]);
            object[column] = new String(value);
            
            result = (String[])results.set(row, object);
            result = null;
            
            return true;
        }
    }
    
    public GPPageInitialPopulationModule(Page page) {
        this.page = page;
        List columns = new ArrayList();
        columns.add(new Column(BUY_RULE_COLUMN,
            Locale.getString("BUY_RULE"),
            Locale.getString("BUY_RULE_COLUMN_HEADER"),
            String.class,
            Column.VISIBLE));
        columns.add(new Column(SELL_RULE_COLUMN,
            Locale.getString("SELL_RULE"),
            Locale.getString("SELL_RULE_COLUMN_HEADER"),
            String.class,
            Column.VISIBLE));
        columns.add(new Column(PERCENT_COLUMN,
            Locale.getString("PERCENT_MUTATION"),
            Locale.getString("PERCENT_MUTATION_COLUMN_HEADER"),
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
        
	VeniceLog.getInstance().log("GPPageInitPopModule seed = " + seed);

        showColumns(model);
    }
    
    // If the user double clicks on a result with the LMB, graph the portfolio.
    // If the user right clicks over the table, open up a popup menu.
    private void handleMouseClicked(MouseEvent event) {
        Point point = event.getPoint();
        
        // Right click on the table - raise menu
        if(event.getButton() == MouseEvent.BUTTON3) {
            JPopupMenu menu = new JPopupMenu();
            
            JMenuItem popupEditBuyRuleMenuItem =
            new JMenuItem(Locale.getString("EDIT_BUY_RULE"));
            popupEditBuyRuleMenuItem.setEnabled(getSelectedRowCount() == 1);
            popupEditBuyRuleMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editBuyRule();
                }});
            menu.add(popupEditBuyRuleMenuItem);

            JMenuItem popupEditSellRuleMenuItem =
            new JMenuItem(Locale.getString("EDIT_SELL_RULE"));
            popupEditSellRuleMenuItem.setEnabled(getSelectedRowCount() == 1);
            popupEditSellRuleMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editSellRule();
                }});
            menu.add(popupEditSellRuleMenuItem);
            
            JMenuItem popupEditPercMenuItem =
            new JMenuItem(Locale.getString("EDIT_PERC"));
            popupEditPercMenuItem.setEnabled(getSelectedRowCount() == 1);
            popupEditPercMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editPerc();
                }});
            menu.add(popupEditPercMenuItem);

            menu.addSeparator();

            JMenuItem popupViewBuyRuleMenuItem =
            new JMenuItem(Locale.getString("VIEW_BUY_RULE"));
            popupViewBuyRuleMenuItem.setEnabled(getSelectedRowCount() == 1);
            popupViewBuyRuleMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    viewBuyRule();
                }});
            menu.add(popupViewBuyRuleMenuItem);

            JMenuItem popupViewSellRuleMenuItem =
            new JMenuItem(Locale.getString("VIEW_SELL_RULE"));
            popupViewSellRuleMenuItem.setEnabled(getSelectedRowCount() == 1);
            popupViewSellRuleMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    viewSellRule();
                }});
            menu.add(popupViewSellRuleMenuItem);

            JMenuItem popupStoreBuyRuleMenuItem =
            new JMenuItem(Locale.getString("STORE_BUY_RULE"));
            popupStoreBuyRuleMenuItem.setEnabled(getSelectedRowCount() == 1);
            popupStoreBuyRuleMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    storeBuyRule();
                }});
            menu.add(popupStoreBuyRuleMenuItem);

            JMenuItem popupStoreSellRuleMenuItem =
            new JMenuItem(Locale.getString("STORE_SELL_RULE"));
            popupStoreSellRuleMenuItem.setEnabled(getSelectedRowCount() == 1);
            popupStoreSellRuleMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    storeSellRule();
                }});
            menu.add(popupStoreSellRuleMenuItem);

            menu.addSeparator();

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
    
    // Display a dialog allowing the user to edit the selected buy rule equation.
    private void editBuyRule() {
        // Get result at row
        final int row = getSelectedRow();
        
        // Don't do anything if we couldn't retrieve the selected row
        if(row != -1) {
            final String[] result = model.getResult(row);
            
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    String newBuyRule = ExpressionEditorDialog.showEditDialog(Locale.getString("EDIT_EQUATION"),
                    result[BUY_RULE_COLUMN]);
                    model.setValueAt(row, BUY_RULE_COLUMN, newBuyRule);
                    model.fireTableDataChanged();
                    repaint();
                }});
                
                thread.start();
        }
    }
    
    // Display a dialog allowing the user to edit the selected sell rule equation.
    private void editSellRule() {
        // Get result at row
        final int row = getSelectedRow();
        
        // Don't do anything if we couldn't retrieve the selected row
        if(row != -1) {
            final String[] result = model.getResult(row);
            
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    String newSellRule = ExpressionEditorDialog.showEditDialog(Locale.getString("EDIT_EQUATION"),
                    result[SELL_RULE_COLUMN]);
                    model.setValueAt(row, SELL_RULE_COLUMN, newSellRule);
                    model.fireTableDataChanged();
                    repaint();
                }});
                
                thread.start();
        }
    }
    
    
    // Display a dialog allowing the user to edit the selected percent.
    private void editPerc() {
        // Get result at row
        final int row = getSelectedRow();
        
        // Don't do anything if we couldn't retrieve the selected row
        if(row != -1) {
            final String[] result = model.getResult(row);
            
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    String newPerc = ExpressionEditorDialog.showEditDialog(Locale.getString("EDIT_PERC"),
                    result[PERCENT_COLUMN]);
                    model.setValueAt(row, PERCENT_COLUMN, newPerc);
                    model.fireTableDataChanged();
                    repaint();
                }});
                
                thread.start();
        }
    }
    
    // Displays the buy rule in a small window
    private void viewBuyRule() {
        // Get result at row
        int row = getSelectedRow();
        
        // Don't do anything if we couldn't retrieve the selected row
        if(row != -1) {
            final String[] result = model.getResult(row);
            
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    ExpressionEditorDialog.showViewDialog(Locale.getString("VIEW_BUY_RULE"),
                    result[BUY_RULE_COLUMN]);
                }});
                
                thread.start();
        }
    }
    
    // Displays the sell rule in a small window
    private void viewSellRule() {
        // Get result at row
        int row = getSelectedRow();
        
        // Don't do anything if we couldn't retrieve the selected row
        if(row != -1) {
            final String[] result = model.getResult(row);
            
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    ExpressionEditorDialog.showViewDialog(Locale.getString("VIEW_SELL_RULE"),
                    result[SELL_RULE_COLUMN]);
                }});
                
                thread.start();
        }
    }
    
    // Allows the user to remember the buy rule
    private void storeBuyRule() {
        // Get result at row
        int row = getSelectedRow();
        
        // Don't do anything if we couldn't retrieve the selected row
        if(row != -1) {
            final String[] result = model.getResult(row);
            
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    ExpressionEditorDialog.showAddDialog(Locale.getString("STORE_BUY_RULE"),
                    result[BUY_RULE_COLUMN]);
                }});
                
                thread.start();
        }
    }
    
    // Allows the user to remember the sell rule
    private void storeSellRule() {
        // Get result at row
        int row = getSelectedRow();
        
        // Don't do anything if we couldn't retrieve the selected row
        if(row != -1) {
            final String[] result = model.getResult(row);
            
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    ExpressionEditorDialog.showAddDialog(Locale.getString("STORE_SELL_RULE"),
                    result[SELL_RULE_COLUMN]);
                }});
                
                thread.start();
        }
    }
    
    // Removes all the selected results from the table
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
        List results = model.getResults();
        Iterator iterator = sortedRows.iterator();
        
        while(iterator.hasNext()) {
            Integer rowToRemove = (Integer)iterator.next();
            
            results.remove(rowToRemove.intValue());
        }
        
        model.setResults(results);
    }
    
    // Display a dialog allowing the user to edit the selected rules and the percentage value.
    public void editRules() {
        // Get result at row
        final int row = getSelectedRow();
        
        // Don't do anything if we couldn't retrieve the selected row
        if(row != -1) {
            final String[] result = model.getResult(row);
            
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    String[] labels = {Locale.getString("BUY_RULE_COLUMN_HEADER"),
                        Locale.getString("SELL_RULE_COLUMN_HEADER"),
                        Locale.getString("PERCENT_MUTATION_COLUMN_HEADER")};
                    boolean[] areas = {true,
                        true,
                        false};
                    String[] newRules = TextsEditorDialog.showEditDialog(Locale.getString("EDIT"),
                        labels, areas, result);
                    model.setValueAt(row, BUY_RULE_COLUMN, newRules[BUY_RULE_COLUMN]);
                    model.setValueAt(row, SELL_RULE_COLUMN, newRules[SELL_RULE_COLUMN]);
                    model.setValueAt(row, PERCENT_COLUMN, newRules[PERCENT_COLUMN]);
                    model.fireTableDataChanged();
                    repaint();
                }});
                
                thread.start();
        }
    }
    
    // Display a dialog allowing the user to add rules and the percentage value.
    public void addRules() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                String[] labels = {Locale.getString("BUY_RULE_COLUMN_HEADER"),
                    Locale.getString("SELL_RULE_COLUMN_HEADER"),
                    Locale.getString("PERCENT_MUTATION_COLUMN_HEADER")};
                boolean[] areas = {true,
                    true,
                    false};
                String[] newRules = TextsEditorDialog.showAddDialog(Locale.getString("ADD"),
                    labels, areas);
                if (newRules[0]!=null)
                    addRowTable(newRules[BUY_RULE_COLUMN], newRules[SELL_RULE_COLUMN], newRules[PERCENT_COLUMN]);
                model.fireTableDataChanged();
                repaint();
            }});

            thread.start();
    }
    
    // Some menu items are only enabled/disabled depending on what is
    // selected in the table or by the size of the table
    private void checkMenuDisabledStatus() {
        int numberOfSelectedRows = getSelectedRowCount();
        
        editBuyRuleMenuItem.setEnabled(numberOfSelectedRows == 1);
        editSellRuleMenuItem.setEnabled(numberOfSelectedRows == 1);
        editPercMenuItem.setEnabled(numberOfSelectedRows == 1);
        viewBuyRuleMenuItem.setEnabled(numberOfSelectedRows == 1);
        viewSellRuleMenuItem.setEnabled(numberOfSelectedRows == 1);
        storeBuyRuleMenuItem.setEnabled(numberOfSelectedRows == 1);
        storeSellRuleMenuItem.setEnabled(numberOfSelectedRows == 1);
        removeMenuItem.setEnabled(numberOfSelectedRows >= 1);
        removeAllMenuItem.setEnabled(model.getRowCount() > 0);
    }
    
    // Add a menu
    private void addMenu() {
        menuBar = new JMenuBar();
        
        JMenu resultMenu = MenuHelper.addMenu(menuBar, Locale.getString("RESULT"));
        
        editBuyRuleMenuItem = new JMenuItem(Locale.getString("EDIT_BUY_RULE"));
        editBuyRuleMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editBuyRule();
            }});
        resultMenu.add(editBuyRuleMenuItem);

        editSellRuleMenuItem = new JMenuItem(Locale.getString("EDIT_SELL_RULE"));
        editSellRuleMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSellRule();
            }});
        resultMenu.add(editSellRuleMenuItem);

        editPercMenuItem = new JMenuItem(Locale.getString("EDIT_PERC"));
        editPercMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSellRule();
            }});
        resultMenu.add(editPercMenuItem);

        resultMenu.addSeparator();

        viewBuyRuleMenuItem = new JMenuItem(Locale.getString("VIEW_BUY_RULE"));
        viewBuyRuleMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewBuyRule();
            }});
        resultMenu.add(viewBuyRuleMenuItem);

        viewSellRuleMenuItem = new JMenuItem(Locale.getString("VIEW_SELL_RULE"));
        viewSellRuleMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewSellRule();
            }});
        resultMenu.add(viewSellRuleMenuItem);

        storeBuyRuleMenuItem = new JMenuItem(Locale.getString("STORE_BUY_RULE"));
        storeBuyRuleMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                storeBuyRule();
            }});
        resultMenu.add(storeBuyRuleMenuItem);

        storeSellRuleMenuItem = new JMenuItem(Locale.getString("STORE_SELL_RULE"));
        storeSellRuleMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                storeSellRule();
            }});
        resultMenu.add(storeSellRuleMenuItem);

        resultMenu.addSeparator();

        removeMenuItem = new JMenuItem(Locale.getString("REMOVE"));
        removeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeSelectedResults();
                checkMenuDisabledStatus();
            }});
        resultMenu.add(removeMenuItem);

        removeAllMenuItem = new JMenuItem(Locale.getString("REMOVE_ALL"));
        removeAllMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.removeAllResults();
                checkMenuDisabledStatus();
            }});
        resultMenu.add(removeAllMenuItem);

        resultMenu.addSeparator();

        JMenu columnMenu = createShowColumnMenu(model);
        resultMenu.add(columnMenu);

        resultMenu.addSeparator();

        // Listen for changes in selection so we can update the menus
        getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                checkMenuDisabledStatus();
            }

        });

        checkMenuDisabledStatus();
    }
    
    public void addResults(List results) {
        model.addResults(results);
        checkMenuDisabledStatus();
        validate();
        repaint();
    }
    
    public void addRowTable(String buyRule, String sellRule, String perc) {
        List results = new ArrayList();
        String[] object = new String[NUMBER_COLUMN];
        object[BUY_RULE_COLUMN] = new String(buyRule);
        object[SELL_RULE_COLUMN] = new String(sellRule);
        object[PERCENT_COLUMN] = new String(perc);
        results.add(object);
        model.addResults(results);
        checkMenuDisabledStatus();
        validate();
        repaint();
    }
    
    // Fit the values, if they differ
    public void fitAll() {
        if (isAllValuesAcceptable()) {
            int total = 0;
            for (int i=0; i<perc.length; i++) {
                total += perc[i];
            }

            // Set dummy values according to PERCENT_INT that is the maximum
            int[] dummyPerc = new int[perc.length];
            for (int i=0; i<perc.length; i++)
                dummyPerc[i] = Math.round((perc[i] * PERCENT_INT) / total);
            int dummyTotal = 0;
            for (int i=0; i<perc.length; i++)
                dummyTotal += dummyPerc[i];
            // Adjust approximations of Math.round method
            int count=0;
            while (dummyTotal!=PERCENT_INT) {
                if (dummyTotal>PERCENT_INT) {
                    dummyPerc[count]--;
                    dummyTotal--;
                } else {
                    dummyPerc[count]++;
                    dummyTotal++;
                }
                count++;
            }
            // Set new values
            for (int i=0; i<perc.length; i++)
                perc[i] = dummyPerc[i];
            // Update the text in the user interface
            setTexts();
        }
        checkMenuDisabledStatus();
        validate();
        repaint();
    }

    // Return true if values already fit to percentage
    private boolean isFitAll() {
        if (isAllValuesAcceptable()) {
            int total = 0;
            for (int i=0; (i<perc.length); i++)
                total += perc[i];
            if (total==PERCENT_INT)
                return true;
        }
        return false;
    }

    private boolean isAllValuesAcceptable() {
        try {
            setNumericalValues();
        } catch(ParseException e) {
        	this.page.showErrorMessage(
        			Locale.getString("ERROR_PARSING_NUMBER", e.getMessage()),
        			Locale.getString("INVALID_GP_ERROR"));
	    return false;
	}

        if(!isAllValuesPositive()) {
        	this.page.showErrorMessage(
        			Locale.getString("NO_POSITIVE_VALUES_ERROR"),
        			Locale.getString("INVALID_GP_ERROR"));
	    return false;
        }

        if(!isTotalOK()) {
            // Messages inside the isTotalOK method
	    return false;
        }

        return true;
    }
    
    private void setNumericalValues() throws ParseException {
        perc = new int[model.getRowCount()];
    
        // decimalFormat manage the localization.
        DecimalFormat decimalFormat = new DecimalFormat(format);
        for (int i=0; i<perc.length; i++) {
            final String[] result = model.getResult(i);
            if((!result[PERCENT_COLUMN].equals("")) && (!result[PERCENT_COLUMN].equals(nullString))) {
                perc[i] =
                    (int) Math.round(PERCENT_DOUBLE*(decimalFormat.parse(result[PERCENT_COLUMN]).doubleValue()));
            } else {
                perc[i] = 0;
            }
        }
    }
    
    private void setTexts() {
        DecimalFormat decimalFormat = new DecimalFormat(format);
        for (int i=0; i<perc.length; i++)
            model.setValueAt(i, PERCENT_COLUMN, decimalFormat.format(perc[i]/PERCENT_DOUBLE));
    }
        
    private boolean isAllValuesPositive() {
        boolean returnValue = true;
        for (int i=0; i<perc.length; i++)
            returnValue = returnValue && (perc[i]>=0);
        return returnValue;
    }
    
    private boolean isTotalOK() {
        long total = 0;
        int totalLength = perc.length;
        for (int i=0; (i<totalLength); i++)
            total += perc[i];
        // Check total == 0
        if (total==0) {
            // If all values are null or 0,
            // then all the percents are set each one equal
            for (int i=0; (i<totalLength); i++)
                perc[i] = 1;
            /* activate this code if you do not want the above behaviour
             JOptionPane.showInternalMessageDialog(desktop,
                                                  Locale.getString("NO_TOTAL_GREATER_THAN_ZERO_PAGE_ERROR"),
                                                  Locale.getString("INVALID_GP_ERROR"),
                                                  JOptionPane.ERROR_MESSAGE);
            return false;
             */
        }
        return true;
    }
    
    public String getTitle() {
        return Locale.getString("GP_PAGE_INITIAL_POPULATION_LONG");
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
            if (!((values[BUY_RULE_COLUMN].compareTo(nullString)==0) &&
                (values[SELL_RULE_COLUMN].compareTo(nullString)==0))) {
                String value = values[BUY_RULE_COLUMN] +
                    separatorString + values[SELL_RULE_COLUMN] +
                    separatorString + values[PERCENT_COLUMN];
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
            if (!((values[BUY_RULE_COLUMN].compareTo("")==0) &&
                (values[SELL_RULE_COLUMN].compareTo("")==0))) {
                List newModel = new ArrayList();
                newModel.add(values);
                model.addResults(newModel);
            }
        }
    }
    
    public int getRandom() {
        if (isAllValuesAcceptable()) {
            int total = 0;
            int totalLength = perc.length;
            for (int i=0; i<totalLength; i++)
                total += perc[i];
            int randomValue = random.nextInt(total);

            int totalMin = 0;
            int totalMax = 0;
            for (int i=0; i<totalLength; i++) {
                totalMax = totalMin + perc[i];
                if ((randomValue >= totalMin) && (randomValue < totalMax)) {
                    return i;
                }
                totalMin += perc[i];
            }

            this.page.showErrorMessage(
            		Locale.getString("ERROR_GENERATING_RANDOM_NUMBER"),
            		Locale.getString("INVALID_GP_ERROR"));
        }
        return 0;
    }
    
    public boolean parse() {
        if(!isAllValuesAcceptable()) {
            return false;
        } else {
            if(!isFitAll()) {
                ConfirmDialog dialog = new ConfirmDialog(this.page.desktop,
                                                         Locale.getString("GP_FIT_PAGE"),
                                                         Locale.getString("GP_FIT_TITLE"));
                boolean returnConfirm = dialog.showDialog();
                if (returnConfirm)
                    fitAll();
                else
                    return false;
            }
        }
        return true;
    }
    
    public String getBuyRule(int row) {
        return (String)model.getValueAt(row, BUY_RULE_COLUMN);
    }

    public String getSellRule(int row) {
        return (String)model.getValueAt(row, SELL_RULE_COLUMN);
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

    public Settings getSettings() {
	return settings;
    }
    
}
