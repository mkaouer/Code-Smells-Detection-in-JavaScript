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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mov.ui.AbstractTable;
import org.mov.ui.AbstractTableModel;
import org.mov.ui.Column;
import org.mov.ui.ExpressionComboBox;
import org.mov.ui.ExpressionEditorDialog;
import org.mov.util.Locale;

/**
 * The table in the Expression Preferences page that lists stored expressions.
 */
public class EquationTable extends AbstractTable {
    private static final int NAME_COLUMN = 0;
    private static final int EXPRESSION_COLUMN = 1;

    private Model model;

    // Current list of stored expressions in the table
    private List storedExpressions;

    private class Model extends AbstractTableModel {

	private List storedExpressions;

	public Model(List columns, List storedExpressions) {
	    super(columns);
	    this.storedExpressions = storedExpressions;
	}
	
	public int getRowCount() {
	    return storedExpressions.size();
	}

	public Object getValueAt(int row, int column) {
	    assert row < storedExpressions.size();
	    StoredExpression storedExpression = 
		(StoredExpression)storedExpressions.get(row);

	    if(column == NAME_COLUMN) 
		return storedExpression.name;
	    else
		return storedExpression.expression;
	}
        
    }

    /**
     * Create a new expression table. The expression table will be initially populated from
     * the current stored expressions.
     */
    public EquationTable() {
	List columns = new ArrayList();
	columns.add(new Column(NAME_COLUMN, 
			       Locale.getString("NAME"), 
			       Locale.getString("NAME_COLUMN_HEADER"), 
			       String.class, Column.VISIBLE));
	columns.add(new Column(EXPRESSION_COLUMN, 
			       Locale.getString("EQUATION"), 
			       Locale.getString("FULL_EQUATION_COLUMN_HEADER"), 
			       String.class, Column.VISIBLE));

	storedExpressions = PreferencesManager.getStoredExpressions();

	model = new Model(columns, storedExpressions);
	setModel(model);
	showColumns(model);
    }

    /** 
     * Display a dialog asking the user to enter a new stored expression.
     */
    public void add() {
        Thread thread = new Thread(new Runnable() {
                public void run() {
		    StoredExpression storedExpression = 
			ExpressionEditorDialog.showAddDialog(storedExpressions, 
							     Locale.getString("ADD_EQUATION"));

		    if(storedExpression != null) {
                        storedExpressions.add(storedExpression);
                        setModel(model);
                        model.fireTableDataChanged();
			repaint();
		    }
		}
	    });
    
	thread.start();
    }

    /** 
     * Display a dialog allowing the user to edit the stored expression.
     *
     * @param row the row of the stored expression to edit.
     */
    public void edit(int row) {
	if(row >= 0 && row < storedExpressions.size()) {
	    final StoredExpression storedExpression = (StoredExpression)storedExpressions.get(row);

	    Thread thread = new Thread(new Runnable() {
		    public void run() {
			ExpressionEditorDialog.showEditDialog(storedExpressions,
							      Locale.getString("EDIT_EQUATION"), 
							      storedExpression);
                        model.fireTableDataChanged();
			repaint();
		    }
		});
    
	    thread.start();
	}
    }

    /**
     * Delete the stored expressions in the given rows.
     */
    public void delete(int[] rows) {
	// Remove the last row first, then the second to last, etc...
	Arrays.sort(rows);

	for(int i = rows.length - 1; i >= 0; i--) {
	    int row = rows[i];
	    if(row >= 0 && row < storedExpressions.size())
		storedExpressions.remove(row);
	}

	// For some reason we need to do an explicit repaint call
	// here to get the table to redraw itself.
        model.fireTableDataChanged();
	repaint();
    }
    
    /**
     * Replace the stored expressions in preferences with the stored expressions in
     * this table. Make sure everything is in-sync with the new expressions.
     */
    public void save() {
	PreferencesManager.putStoredExpressions(storedExpressions);
	ExpressionComboBox.updateExpressions();
    }
}
