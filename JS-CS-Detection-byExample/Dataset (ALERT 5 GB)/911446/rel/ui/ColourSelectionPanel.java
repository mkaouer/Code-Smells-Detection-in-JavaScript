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

/**
 * JPanel for selecting the background colour of a chart. 
 * 
 * @author gbonhevi
 */

package nz.org.venice.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ColourSelectionPanel extends JPanel {
    private ArrayList rbs;
	
    public ColourSelectionPanel(ArrayList colours) {
      	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    	ButtonGroup bg = new ButtonGroup();

	rbs = new ArrayList();
	for(Iterator it = colours.iterator(); it.hasNext();) {
	    Color c = (Color)it.next();
	    JRadioButton btn = new JRadioButton("Venice",false);    	
	    btn.setOpaque(true);
	    btn.setBackground(c);
	    btn.setForeground(c);
	    btn.setAlignmentX(Component.CENTER_ALIGNMENT);
	    add(btn);
	    bg.add(btn);
	    rbs.add(btn);
	}
    }
	
    public Color getSelectedColour() {
        for(Iterator it = rbs.iterator(); it.hasNext();) {
	    JRadioButton btn = (JRadioButton)it.next();
	    if (btn.isSelected()) return btn.getBackground();
	}
	return Color.GRAY;
    }

  public void setSelectedColour(Color c) {
    for(Iterator it = rbs.iterator(); it.hasNext();) {
        JRadioButton btn = (JRadioButton)it.next();
	if (btn.getBackground().equals(c)) btn.setSelected(true);
    }
  }
}
