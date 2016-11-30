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

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * Table that allows the user to sort by column by clicking on the column's
 * table header
 *
 * @version	0.1, 01/02/19	
 * @author	Andrew Leppard
 */

public class SortedTable extends JTable
{
    /** Row is not sorted */
    public final static int DONT_SORT = 0;

    /** Row is sorted in ascending order */
    public final static int SORT_UP = 1;

    /** Row is sorted in descending order */
    public final static int SORT_DOWN = 2;

    // Column used for sorting and the direction of sort
    private int sortColumn;
    private int sortDirection;

    // Our sort model
    private SortModel model;

    // Up/Down images in header column - these are from jlfgr-1.0.jar
    private String upImage = "toolbarButtonGraphics/navigation/Up16.gif";
    private String downImage = "toolbarButtonGraphics/navigation/Down16.gif";

    private class HeaderCellRenderer extends JLabel implements TableCellRenderer
    {
	public HeaderCellRenderer(Border border, Color background, Color foreground)
	{
	    setBorder(border);
	    setHorizontalTextPosition(SwingConstants.LEFT);
	    setHorizontalAlignment(SwingConstants.CENTER);

            setBackground(background);
            setForeground(foreground);
	}
	
	public Component
	    getTableCellRendererComponent(JTable table,
					  Object value,
					  boolean isSelected,
					  boolean hasFocus,
					  int row, int column)
	{
	    SortedTable sortedTable = (SortedTable)table;
	    SortModel sortModel = (SortModel)sortedTable.getModel();

	    // Deal with moved columns
	    column = convertColumnIndexToModel(column);

	    // Deal with hidden columns
	    column = sortModel.convertFromDisplayedColumn(column);

	    setText((String)value);

	    // Work out which icon to display - is this column a key for
	    // sorting?
	    if(sortedTable.getColumnSortStatus(column) == DONT_SORT)
		setIcon(null);
	    else if(sortedTable.getColumnSortStatus(column) == SORT_UP) {
		// Create up arrow for header column
                URL upImageURL =
                    ClassLoader.getSystemResource(upImage);

                // Handle case where jlfgr isn't installed and the image
                // isn't availabe
                if(upImageURL != null) {
                    ImageIcon upImageIcon = new ImageIcon(upImageURL);
                    setIcon(upImageIcon);
                }
	    }
	    else {
		// Create down arrow for header column
                URL downImageURL =
                    ClassLoader.getSystemResource(downImage);

                // Handle case where jlfgr isn't installed and the image
                // isn't availabe
                if(downImageURL != null) {
                    ImageIcon downImageIcon = new ImageIcon(downImageURL);
                    setIcon(downImageIcon);
                }
	    }

	    return this;
	}
    }

    public class SortModel extends AbstractTableModel implements TableModelListener {

	private TableModel userModel;
	private SortComparator sortComparator;
	private LinkedList sortIndex;

	// Keep track of which column and in which direction we are
	// sorting
	private int currentSortDirection;
	private int currentSortColumn;

	// Keep a set of hidden columns
	private TreeSet hiddenColumns;

	class TableElement {
	    private Object key;
	    private int index;

	    TableElement(Object key, int index) {
		this.key = key;
		this.index = index;
	    }
	
	    Object getKey() {
		return key;
	    }

	    int getIndex() {
		return index;
	    }
	}

	private class SortComparator implements Comparator
	{
	    private int sortDirection;
	
	    public SortComparator(int sortDirection)
	    {
		setDirection(sortDirection);
	    }
	
	    public void setDirection(int sortDirection)
	    {
		this.sortDirection = sortDirection;
	    }

	    public int compare(Object firstObject, Object secondObject)
	    {
		TableElement firstElement = (TableElement)firstObject;
		TableElement secondElement = (TableElement)secondObject;
                Object firstKey = firstElement.getKey();
                Object secondKey = secondElement.getKey();

                // The sorted table supports two way comparable. Objects which sort
                // differently depending on the direction of sort. (I.e. it isn't
                // a mirror image). An Example is columns with total columns that
                // shoud always appear at the bottom.
                try {
                    if(firstKey instanceof TwoWayComparable) {
                        TwoWayComparable firstComparable = (TwoWayComparable)firstKey;
                        TwoWayComparable secondComparable = (TwoWayComparable)secondKey;

                        return firstComparable.compareTo(secondComparable,
                                                         sortDirection == SORT_UP);
                    }
                    else {
                        Comparable firstComparable = (Comparable)firstKey;
                        Comparable secondComparable = (Comparable)secondKey;

                        return applySortDirection(firstComparable.compareTo(secondComparable));
                    }
                }
                catch(ClassCastException e) {
                    // Everything should be either Comparable or TwoWayComparable and match.
                    assert false;
                }

                // If we got here there was an error - so return equality.
                return 0;
	    }
	
	    private int applySortDirection(int comparision)
	    {
		// Changes direction of sort
		if(sortDirection == SORT_UP) {
                    comparision = -comparision;
                    /*
		    if(comparision > 0)
			comparision = -1;
		    else if(comparision < 0)
			comparision = 1;
                    */
		}
		
		return comparision; // no change
	    }
	}
	
	public SortModel(TableModel userModel, int sortColumn, int
			 sortDirection)
	{
	    this.userModel = userModel;
	    userModel.addTableModelListener(this);
	
	    hiddenColumns = new TreeSet();
	    sortComparator = new SortComparator(sortDirection);
	    sortIndex = null;
	
	    sort(sortColumn, sortDirection);
	}

	public void tableChanged(TableModelEvent e) {
	    // Currently ignore this event since sometimes we dont get
	    // the event earlier enough and getValueAt() still thinks
	    // the table hasn't changed. Not sure why, need to look into
	    // it.
	}

	public boolean isColumnVisible(int columnNumber) {

	    Iterator iterator = hiddenColumns.iterator();

	    while(iterator.hasNext()) {
		int hiddenColumnNumber = ((Integer)iterator.next()).intValue();

		if(hiddenColumnNumber == columnNumber)
		    return false;
	    }

	    return true;
	}

	public void showColumn(int columnNumber, boolean show) {
	    if(show == false && isColumnVisible(columnNumber)) {	
		hiddenColumns.add(new Integer(columnNumber));
		fireTableStructureChanged();
	    }
	    else if(show == true && !isColumnVisible(columnNumber)) {
		hiddenColumns.remove(new Integer(columnNumber));
		fireTableStructureChanged();
	    }
	}
	
	// Convert from column index which includes hidden columns to
	// one that doesn't. E.g. if column 3 is hidden and this function
	// is given column number 4 it will return 5 as this is the column
	// number which includes the hidden column number. I.e.
	//
	// 1 2 [H] 3 4 => 1 2 [3] 4 5
	private int convertToDisplayedColumn(int column) {
	    // First count all hidden columns up to the given column number
	    Iterator iterator = hiddenColumns.iterator();

	    while(iterator.hasNext()) {
		int hiddenColumnNumber = ((Integer)iterator.next()).intValue();
		
		if(hiddenColumnNumber <= column)
		    column++;
	    }

	    return column;
	}

	// Does the opposite of convertToDisplayedColumn()
	public int convertFromDisplayedColumn(int column) {
	    // Count all hidden columns up to the given column number
	    int numberHiddenColumns = 0;

	    Iterator iterator = hiddenColumns.iterator();

	    while(iterator.hasNext()) {
		int hiddenColumnNumber = ((Integer)iterator.next()).intValue();
		
		if(hiddenColumnNumber <= column) {
		    numberHiddenColumns++;
                    column++;
                }
	    }

	    return column;
	}

	// Should take original column + direction
	public void sort(int sortColumn, int sortDirection)
	{
	    currentSortColumn = sortColumn;
	    currentSortDirection = sortDirection;

	    // 1. Create linked list of all table elements required to
	    // perform sort
	    sortIndex = new LinkedList();
	    TableElement tableElement;

	    for(int i = 0; i < getRowCount(); i++) {
		tableElement =
		    new TableElement(userModel.getValueAt(i, sortColumn),
				     i);
		sortIndex.add(tableElement);
	    }
	
	    // 2. Get comparator ready and then sort
	    sortComparator.setDirection(sortDirection);
	    Collections.sort(sortIndex, sortComparator);
	}

	public int getRowCount()
	{
	    return userModel.getRowCount();
	}
	
	public int getColumnCount()
	{
	    return userModel.getColumnCount() - hiddenColumns.size();
	}

	public Class getColumnClass(int column)
	{
	    return userModel.getColumnClass(convertToDisplayedColumn(column));
	}

	public String getColumnName(int column)
	{
	    return userModel.getColumnName(convertToDisplayedColumn(column));
	}
	
	public Object getValueAt(int row, int column)
	{
	    column = convertToDisplayedColumn(column);

	    // If the table size has changed then we need to
	    // resort before we return the new value
	    if(sortIndex.size() != getRowCount()) {
		sort(currentSortColumn, currentSortDirection);
	    }

	    // The sortIndex.get(row) will translate the given row to the
	    // sorted row
	    if(sortIndex != null) {
		TableElement index = (TableElement)sortIndex.get(row);

		return userModel.getValueAt(index.getIndex(), column);
	    }
	    else
		return (Object)"";
	}

	// Given the row of the sorted data, what row would it be in the
	// original unsorted data?
	public int getUnsortedRow(int sortedRow)
	{
            // Row is not in the table
            if(sortedRow == -1)
                return -1;
            else {
                TableElement index = (TableElement)sortIndex.get(sortedRow);
                return index.getIndex();
            }
	}

	public int getSortedRow(int unsortedRow)
	{
            // Row is not in the table
            if(unsortedRow == -1)
                return -1;
            else {
                TableElement index;

                for(int i = 0; i < getRowCount(); i++) {
                    index = (TableElement)sortIndex.get(i);

                    if(index.getIndex() == unsortedRow)
                        return i;		
                }

                // No longer in table
                return -1;
            }
	}

	public TableModel getUserModel()
	{
	    return userModel;
	}
    }

    public SortedTable()
    {
	// Set defaults
	sortColumn = 0;
	sortDirection = SORT_DOWN;
	model = null;
	
	// Set custom renderer for the tables headers so we have the
	// sort direction arrow
        setCustomHeaderRenderer();

	// Set tool tip text to inform user of column sorting
        String toolTipText =
            "Click on table header to sort by that column, click again to change sort direction";
	getTableHeader().setToolTipText(toolTipText);

	// Monitor mouse clicks on table header
	getTableHeader().addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e)
                {
                    // Only interested in left mouse button events *and*
                    // only interested them if the table has more than one
                    // entry
                    if((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {

                        // Get which column has been been clicked
                        int column = columnAtPoint(e.getPoint());

                        // Deal with "moved" columns
                        column = convertColumnIndexToModel(column);

                        // Deal with "hidden" columns
                        column = model.convertFromDisplayedColumn(column);

                        // Toggle up/down arrow on column. Assign to new one if needed
                        toggleColumnSortStatus(column);

                        // Tell table model a resort is needed
                        resort();

                        // redraw
                        revalidate();
                        repaint();
                    }
                }
            });
    }

    public void setModel(TableModel model)
    {
	setModel(model, sortColumn, sortDirection);
    }

    public void setModel(TableModel model, int sortColumn, int sortDirection)
    {
	// wrap user's model with our sort model
	this.model = new SortModel(model, sortColumn, sortDirection);

	super.setModel(this.model);
    }

    public void showColumn(int columnNumber, boolean show) {
	// Tell model about column
	model.showColumn(columnNumber, show);
    }

    // Tell table to resort
    public void resort()
    {
	if(model != null) {

	    // Make sure when the table is sorted, the previous people selected
	    // are still selected - rather than selecting the new people
	    // whom occupy their previous rows. First get indices of people
	    // who are currently selected

	    int[] selectedRows = getSelectedRows();
	    int selectedRowCount = getSelectedRowCount();
	
	    // Resort data in table model
	    model.sort(sortColumn, sortDirection);

	    // Reselect people
	    clearSelection();

	    for(int i = 0; i < selectedRowCount; i++)
		addRowSelectionInterval(selectedRows[i],
					selectedRows[i]);
	}
    }

    // We use a custom renderer for the table header so that we can
    // insert the up/down sort arrows.
    private void setCustomHeaderRenderer() {
	TableCellRenderer r = getTableHeader().getDefaultRenderer();	
	
	// Assume the header is rendered using swing components (it is) and
	// get the component used to render the first cell of the header
	JComponent header =
	    (JComponent)r.getTableCellRendererComponent((JTable)this,
							(Object)"",
							false,
							false,
							0, 0);

        HeaderCellRenderer renderer =
            new HeaderCellRenderer(header.getBorder(),
                                   header.getBackground(),
                                   header.getForeground());
        getTableHeader().setDefaultRenderer(renderer);
    }

    public int getColumnSortStatus(int column)
    {
	if(column == sortColumn)
	    return sortDirection;
	else
	    return DONT_SORT;
    }

    public void setColumnSortStatus(int column, int direction)
    {
	if(direction != DONT_SORT) {
	    sortColumn = column;
	    sortDirection = direction;
	}
    }

    public void toggleColumnSortStatus(int column)
    {
	if(getColumnSortStatus(column) == DONT_SORT)
	    setColumnSortStatus(column, SORT_DOWN);
	else if(getColumnSortStatus(column) == SORT_DOWN)
	    setColumnSortStatus(column, SORT_UP);
	else
	    setColumnSortStatus(column, SORT_DOWN);
    }

    // We need to overide this method because it'll return the row in
    // the sorted table - the user will expect the row in the unsorted table
    // Warning: there might be some of these that are not overridden
    //          also classes that listen for row changes may have the wrong
    //          data given to them in the model event
    public int getSelectedRow()
    {
        int row = super.getSelectedRow();

        // No selected row?
        if(row == -1)
            return -1;
        else
            return model.getUnsortedRow(row);
    }

    public int[] getSelectedRows()
    {
	int[] unsortedSelectedRows = super.getSelectedRows();
	int[] selectedRows = new int[getSelectedRowCount()];
	
	if(getSelectedRowCount() > 0) {
	    for(int i = 0; i < getSelectedRowCount(); i++)
		selectedRows[i] =
		    model.getUnsortedRow(unsortedSelectedRows[i]);
	}

	return selectedRows;
    }

    public void setSortedRowSelectionInterval(int index0, int index1)
    {
	super.setRowSelectionInterval(index0, index1);
    }

    // Sometimes you want to know what row in the sorted table too
    public int getSortedSelectedRow()
    {
	return super.getSelectedRow();
    }

    public void setRowSelectionInterval(int index0, int index1)
    {
	// Doing a straight select of data using the user model doesnt
	// convert very well to sorted. I.e. a straight selection of unsorted
	// data could end up being a descrete grouping of sorted data
	// selections. It is for this reason this function will only work
	// when index0 == index1 otherwise it will be ignored.
	// This forces the user to think about what they are doing.
	if(index0 == index1) {
	    // Convert
	    int sortedIndex = model.getSortedRow(index0);

	    if(sortedIndex != -1)
                super.setRowSelectionInterval(sortedIndex, sortedIndex);
	}
    }

    public void setVisible(int row, int column)
    {
        int sortedRow = model.getSortedRow(row);

        if(sortedRow != -1) {
            Rectangle rectangle = super.getCellRect(sortedRow, column, true);
            scrollRectToVisible(rectangle);
        }
    }

    public void addRowSelectionInterval(int index0, int index1)
    {
	if(index0 == index1) {
	    // Convert
	    int sortedIndex = model.getSortedRow(index0);

	    if(sortedIndex != -1)
		super.addRowSelectionInterval(sortedIndex, sortedIndex);
	}
    }

    // Given the row of the sorted data, what row would it be in the
    // original unsorted data?
    public int getUnsortedRow(int sortedRow) {
        if(sortedRow == -1)
            return sortedRow;
        else
            return model.getUnsortedRow(sortedRow);
    }

    public int getSortedRow(int unsortedRow) {
        if(unsortedRow == -1)
            return unsortedRow;
        else
            return model.getSortedRow(unsortedRow);
    }
}

