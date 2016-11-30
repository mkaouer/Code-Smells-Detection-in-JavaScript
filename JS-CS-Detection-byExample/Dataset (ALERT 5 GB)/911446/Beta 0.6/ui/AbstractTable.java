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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import org.mov.quote.Symbol;
import org.mov.util.Locale;
import org.mov.util.Money;
import org.mov.util.TradingDate;
import org.mov.util.TradingTime;

/**
 * Helper for constructing tables. The abstract table sets up the look & feel for
 * tables in Venice. It also provides column sorting and helper methods for
 * expression columns.
 *
 * @author Andrew Leppard
 * @see AbstractTableModel
 * @see Column
 * @see ExpressionColumn
 */
public class AbstractTable extends SortedTable {

    // Default values for rendering table rows
    private static final Color backgroundColor = Color.white;
    private static final Color alternativeBackgroundColor = new Color(237, 237, 237);   

    // Images used for arrows representing when stock has gone up, down or is unchanged
    private String upImage = "org/mov/images/Up.png";
    private String downImage = "org/mov/images/Down.png";
    private String unchangedImage = "org/mov/images/Unchanged.png";

    // Keep a single instance of the following so we don't have to instantiate
    // for each cell that is drawn
    private NumberFormat format;
    private ImageIcon upImageIcon;
    private ImageIcon downImageIcon;
    private ImageIcon unchangedImageIcon;

    // List of show expression column menu items
    private List showExpressionColumnMenuItems;

    /**
     * Class for rendering all cells in the table. Not just stock quotes.
     */
    private class StockQuoteRenderer extends JPanel implements TableCellRenderer
    {
	private JLabel textLabel = new JLabel();
	private JLabel iconLabel = new JLabel();
	private Component glue = Box.createHorizontalGlue();

        /**
         * Create a new rendered for rendering a table cell.
         */
	public StockQuoteRenderer() {
	    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}

	public Component getTableCellRendererComponent(JTable table,
						       Object value,
						       boolean isSelected,
						       boolean hasFocus,
						       int row, int column) {
	    AbstractTable t = (AbstractTable) table;

	    // Set font to match default font
	    textLabel.setFont(table.getFont());

	    // Set foreground colour to match default foreground colour
	    textLabel.setForeground(table.getForeground());

	    // Make each alternate row a different colour
	    if(isSelected) {
		setBackground(table.getSelectionBackground());
		textLabel.setForeground(table.getSelectionForeground());
	    } else {
		setBackground(row % 2 != 0?
			      backgroundColor:
			      alternativeBackgroundColor);
            }

	    // The change column has specific rendering
	    if(value instanceof ChangeFormat)
		renderChangeComponent(table, value, isSelected,
				      hasFocus, row, column);
	    else if(value instanceof TradingDate) {
	    	TradingDate date = (TradingDate)value;

	    	String text = date.toString("d?/m?/yyyy");
	    	textLabel.setText(text);
	    	add(textLabel);
	    }
            else if(value != null) {
		textLabel.setText(value.toString());
		add(textLabel);
	    }
            else {
                textLabel.setText("");
                add(textLabel);
            }

	    return this;
	}

        /**
         * Render the {@link ChangeFormat} change which displays a stock's change.
         * This requires extra work because it will display an arrow showing the
         * stock change direction.
         */
	private void renderChangeComponent(JTable table, Object value,
					   boolean isSelected,
					   boolean hasFocus,
					   int row, int column) {

	    ChangeFormat change = (ChangeFormat)value;
	    double changePercent = change.getChange();
	    String text = new String();

	    if(changePercent > 0)
		text = "+";

            text = text.concat(format.format(changePercent));
            text = text.concat("%");
	    textLabel.setText(text);

	    if(changePercent > 0 && upImageIcon != null)
		iconLabel.setIcon(upImageIcon);

	    else if(changePercent < 0 && downImageIcon != null)
		iconLabel.setIcon(downImageIcon);

	    else if(changePercent == 0 && unchangedImageIcon != null)
		iconLabel.setIcon(unchangedImageIcon);

	    add(glue);
	    add(textLabel);
	    add(iconLabel);
	}
    }

    /**
     * Create a new table.
     */
    public AbstractTable() {

	setShowGrid(true);

	// Our own stock quote renderer
	setDefaultRenderer(AccountNameFormat.class, new StockQuoteRenderer());
	setDefaultRenderer(ChangeFormat.class, new StockQuoteRenderer());
	setDefaultRenderer(Double.class, new StockQuoteRenderer());
	setDefaultRenderer(Float.class, new StockQuoteRenderer());
	setDefaultRenderer(Integer.class, new StockQuoteRenderer());
	setDefaultRenderer(Money.class, new StockQuoteRenderer());
	setDefaultRenderer(QuoteFormat.class, new StockQuoteRenderer());
	setDefaultRenderer(String.class, new StockQuoteRenderer());
	setDefaultRenderer(TradingDate.class, new StockQuoteRenderer());
	setDefaultRenderer(TradingTime.class, new StockQuoteRenderer());
        setDefaultRenderer(ExpressionResult.class, new StockQuoteRenderer());
        setDefaultRenderer(PointChangeFormat.class, new StockQuoteRenderer());
        setDefaultRenderer(Symbol.class, new StockQuoteRenderer());

        // Set up number formatter for rendering ChangeFormat.java
        format = NumberFormat.getInstance();
        format.setMinimumIntegerDigits(1);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);

        // Add create the image icons for the up, down & unchanged images
        URL upImageResource = ClassLoader.getSystemResource(upImage);
        upImageIcon = (upImageResource != null? new ImageIcon(upImageResource) : null);

        URL downImageResource = ClassLoader.getSystemResource(downImage);
        downImageIcon = (downImageResource != null? new ImageIcon(downImageResource) : null);

        URL unchangedImageResource = ClassLoader.getSystemResource(unchangedImage);
        unchangedImageIcon = (unchangedImageResource != null?
                              new ImageIcon(unchangedImageResource) : null);
    }

    /**
     * Set the model which describes the table's columns.
     *
     * @param model Table model.
     */
    protected void showColumns(AbstractTableModel model) {
        for(int i = 0; i < model.getColumnCount(); i++) {
            Column column = model.getColumn(i);

            showColumn(column.getNumber(), column.getVisible() == Column.VISIBLE);
        }
    }

    /**
     * Helper method to create and return the "show columns" menu which allows
     * the user to select which columns are visible.
     *
     * @param model Table model.
     */
    protected JMenu createShowColumnMenu(AbstractTableModel model) {
        boolean foundExpressionColumn = false;

        JMenu showColumnsMenu = new JMenu(Locale.getString("SHOW_COLUMNS"));
        showExpressionColumnMenuItems = new ArrayList();

        for(int i = 0; i < model.getColumnCount(); i++) {
            final Column column = model.getColumn(i);

            if(column.getVisible() != Column.ALWAYS_HIDDEN) {
                boolean isExpressionColumn = (column instanceof ExpressionColumn);

                // Insert a bar between the ordinary columns and the expression
                // columns
                if(!foundExpressionColumn && isExpressionColumn) {
                    foundExpressionColumn = true;
                    showColumnsMenu.addSeparator();
                }

                JCheckBoxMenuItem showMenuItem =
                    MenuHelper.addCheckBoxMenuItem(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)e.getSource();
                                showColumn(column.getNumber(), menuItem.getState());
                            }
                        }, showColumnsMenu, column.getFullName());

                showMenuItem.setState(column.getVisible() == Column.VISIBLE);
                
                if(isExpressionColumn)
                    showExpressionColumnMenuItems.add(showMenuItem);
            }
        }
        return showColumnsMenu;
    }

    /**
     * Helper method to display a dialog to let the user set expressions. The
     * user can input expressions which are compiled and run against all the quotes
     * in the table. The results are shown in columns which are made visible.
     *
     * @param model Quote table model.
     */
    protected void applyExpressions(final AbstractQuoteModel model) {
	// Handle all action in a separate thread so we dont
	// hold up the dispatch thread. See O'Reilley Swing pg 1138-9.
	Thread thread = new Thread() {

		public void run() {
		    final ExpressionColumnDialog dialog =
                        new ExpressionColumnDialog(model.getExpressionColumns().length);

		    // Did the user modify the expression columns?
		    if(dialog.showDialog(model.getExpressionColumns())) {
                        final ExpressionColumn[] expressionColumns = dialog.getExpressionColumns();

                        // Load expression columns with data
                        model.setExpressionColumns(expressionColumns);

                        SwingUtilities.invokeLater(new Runnable() {
				public void run() {
				    // Make sure all columns with an expression
				    // are visible and all without are not.
				    // Also update check box menus
				    for(int i = 0; i < expressionColumns.length; i++) {
                                        boolean containsExpression =
                                            expressionColumns[i].getExpressionText().length() > 0;
                                        JCheckBoxMenuItem menuItem =
                                            (JCheckBoxMenuItem)showExpressionColumnMenuItems.get(i);
			
                                        showColumn(expressionColumns[i].getNumber(),
                                                   containsExpression);
                                        menuItem.setState(containsExpression);
                                    }

                                    model.fireTableStructureChanged();
                                }});
		    }
		}
	    };
	thread.start();
    }
}
