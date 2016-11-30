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

package nz.org.venice.ui;

/**
 * Representation of a column in a table.
 *
 * @author Andrew Leppard
 * @see AbstractTableModel
 */
public class Column {

    // Column number
    private int number;

    // Full name of column which appears in menus etc
    private String fullName;

    // Short name of column which appears in the table header
    private String shortName;

    // Data type displayed in column
    private Class type;

    // Information about whether the column is visible or not
    private int visible;

    /** The column is currently hidden. */
    public final static int HIDDEN = 0;

    /** The column is currently visible. */
    public final static int VISIBLE = 1;

    /** The column is always hidden. */
    public final static int ALWAYS_HIDDEN = 2;

    /**
     * Create a new column.
     *
     * @param number    The column number
     * @param fullName  The full name of the column which appears in menus etc.
     * @param shortName The short name of the column which appears in the table header.
     * @param type      Data type displayed in column.
     * @param visible   Either {@link #HIDDEN}, {@link #VISIBLE} or {@link #ALWAYS_HIDDEN}.
     */
    public Column(int number, String fullName, String shortName, Class type, int visible) {
        this.number = number;
        this.fullName = fullName;
        this.shortName = shortName;
        this.type = type;
        this.visible = visible;
    }

    /**
     * Get the column number.
     *
     * @return Column number.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Get the data type of the data in the column.
     *
     * @return Type of column's data.
     */
    public Class getType() {
        return type;
    }

    /**
     * Set the short name of the column.
     *
     * @param name New short name of the column.
     */
    public void setShortName(String name) {
        this.shortName = name;
    }

    /**
     * Get the short name of the column.
     *
     * @return Short name of column.
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Set the full name of the column.
     *
     * @param name New full name of the column.
     */
    public void setFullName(String name) {
        this.fullName = name;
    }

    /**
     * Get the full name of the column.
     *
     * @return Full name of column.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Return whether the column is visible.
     *
     * @return Either {@link #HIDDEN}, {@link #VISIBLE} or {@link #ALWAYS_HIDDEN}.
     */
    public int getVisible() {
        return visible;
    }
}
    
