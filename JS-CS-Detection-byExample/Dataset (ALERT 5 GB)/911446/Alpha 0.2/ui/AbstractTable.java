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

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.text.*;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

import org.mov.quote.Symbol;
import org.mov.util.*;
import org.mov.ui.*;

public class AbstractTable extends SortedTable {

    public class Column {
        public int     number;
        public String  fullName;
        public String  shortName;
        public Class   type;
        public boolean isEnabled;

        public Column(int number, String fullName, String shortName, Class type, 
                      boolean isEnabled) {
            this.number = number;
            this.fullName = fullName;
            this.shortName = shortName;
            this.type = type;
            this.isEnabled = isEnabled;
        }
    }
    
    // Default values for rendering table rows
    private static final Color backgroundColor = Color.white;
    private static final Color alternativeBackgroundColor = 
	new Color(237, 237, 237);

    // Images used for arrows representing when stock has gone up, down or is unchanged
    private String upImage = "org/mov/images/Up.png";
    private String downImage = "org/mov/images/Down.png";
    private String unchangedImage = "org/mov/images/Unchanged.png";

    // Keep instance of number format handy
    private NumberFormat format;

    class StockQuoteRenderer extends JPanel implements TableCellRenderer
    {

	private JLabel textLabel = new JLabel();
	private JLabel iconLabel = new JLabel();
	private Component glue = Box.createHorizontalGlue();

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
	    if(isSelected)
		setBackground(table.getSelectionBackground());
	    else
		setBackground(row % 2 != 0?
			      backgroundColor:
			      alternativeBackgroundColor);

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
	    else {
		textLabel.setText(value.toString());
		add(textLabel);
	    }
	    	    
	    return this;
	}

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

	    if(changePercent > 0) {
		// Create up arrow image
		ImageIcon upImageIcon = 
		    new ImageIcon(ClassLoader.getSystemResource(upImage));
		iconLabel.setIcon(upImageIcon);
	    }

	    else if(changePercent < 0) {
		// Create down arrow image
		ImageIcon downImageIcon = 
		    new ImageIcon(ClassLoader.getSystemResource(downImage));
		iconLabel.setIcon(downImageIcon);
	    }
	    else {
		// Create unchanged image
		ImageIcon unchangedImageIcon = 
		    new ImageIcon(ClassLoader.getSystemResource(unchangedImage));
		iconLabel.setIcon(unchangedImageIcon);
	    }

	    add(glue);
	    add(textLabel);
	    add(iconLabel);
	}
    }

    public AbstractTable() {

	setShowGrid(true);

	// Our own stock quote renderer
	setDefaultRenderer(String.class, new StockQuoteRenderer());
	setDefaultRenderer(Integer.class, new StockQuoteRenderer());
	setDefaultRenderer(Double.class, new StockQuoteRenderer());
	setDefaultRenderer(Float.class, new StockQuoteRenderer());
	setDefaultRenderer(TradingDate.class, new StockQuoteRenderer());
	setDefaultRenderer(ChangeFormat.class, new StockQuoteRenderer());
	setDefaultRenderer(PriceFormat.class, new StockQuoteRenderer());
	setDefaultRenderer(QuoteFormat.class, new StockQuoteRenderer());
        setDefaultRenderer(PointChangeFormat.class, new StockQuoteRenderer());
        setDefaultRenderer(Symbol.class, new StockQuoteRenderer());

        // Set up number formatter for rendering ChangeFormat.java
        format = NumberFormat.getInstance();
        format.setMinimumIntegerDigits(1);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
    }

    public void setModel(TableModel model) {
	super.setModel(model);        
    }

    protected void showColumns(List columns) {
        for(Iterator iterator = columns.iterator(); iterator.hasNext();) {
            Column column = (Column)iterator.next();

            showColumn(column.number, column.isEnabled);
        }
    }

    protected JMenu createShowColumnMenu(List columns) {
        JMenu showColumnsMenu = new JMenu("Show Columns");

        for(Iterator iterator = columns.iterator(); iterator.hasNext();) {
            final Column column = (Column)iterator.next();

            JCheckBoxMenuItem showMenuItem = 
                MenuHelper.addCheckBoxMenuItem(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)e.getSource();
                            showColumn(column.number, menuItem.getState());
                        }
                    }, showColumnsMenu, column.fullName);

            showMenuItem.setState(column.isEnabled);
        }

        return showColumnsMenu;
    }

}


