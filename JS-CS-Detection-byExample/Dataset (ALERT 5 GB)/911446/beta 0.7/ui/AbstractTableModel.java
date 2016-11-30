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

package org.mov.ui;

import java.util.List;

/**
 * Helper for constructing table models. This abstract table model allows you to
 * pass a list of columns for describing a table. The model will then take care
 * of returning information to the table about each column.
 *
 * @author Andrew Leppard
 * @see Column
 */
public abstract class AbstractTableModel extends javax.swing.table.AbstractTableModel {

    // List of table columns
    private List columns;

    /**
     * Create a new table model with no columns.
     */
    public AbstractTableModel() {
        columns = null;
    }

    /**
     * Create a new table model with the set of columns.
     *
     * @param columns Table's columns.
     */
    public AbstractTableModel(List columns) {
        this.columns = columns;
    }

    /**
     * Get number of columns in table.
     */
    public int getColumnCount() {
        return columns.size();
    }
    
    /**
     * Get short name of column. This is the name that will be displayed in the table header.
     *
     * @param columnNumber Column number.
     * @return Column's short name.
     */
    public String getColumnName(int columnNumber) {
        return getColumn(columnNumber).getShortName();
    }
    
    /**
     * Get data type of column data.
     *
     * @param columnNumber Column number.
     * @return Column data type
     */
    public Class getColumnClass(int columnNumber) {
        return getColumn(columnNumber).getType();
    }

    /**
     * Sets the columns in the table model.
     *
     * @param columns New columns.
     */
    public void setColumns(List columns) {
        this.columns = columns;
    }

    /**
     * Get a column.
     *
     * @param columnNumber Column number.
     * @return Column.
     */
    public Column getColumn(int columnNumber) {
        return (Column)columns.get(columnNumber);
    }
}
