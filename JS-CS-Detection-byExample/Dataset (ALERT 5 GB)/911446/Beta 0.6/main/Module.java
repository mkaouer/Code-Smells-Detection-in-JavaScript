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

package org.mov.main;

import java.beans.*;
import javax.swing.*;

/**
 * Provides a common interface that all modules must adhere to. This
 * interface is used to provide seamless intergration between the desktop
 * and its sub-frames. A module is generally defined as a frame that
 * provides a single function such as charting, showing a table of quotes
 * or providing preference options to the user.
 */
public interface Module
{
    /**
     * Return the window title.
     *
     * @return	the window title
     */
    public String getTitle();

    /**
     * Add a property change listener for module change events.
     *
     * @param	listener	listener
     */
    public void addModuleChangeListener(PropertyChangeListener listener);

    /**
     * Remove a property change listener for module change events.
     *
     * @param	listener	listener
     */
    public void removeModuleChangeListener(PropertyChangeListener listener);

    /**
     * Return frame icon for this module.
     *
     * @return	the frame icon
     */
    public ImageIcon getFrameIcon();

    /**
     * Return displayed component for this module.
     *
     * @return the component to display
     */
    public JComponent getComponent();

    /**
     * Return menu bar for this module.
     *
     * @return	the menu bar
     */
    public JMenuBar getJMenuBar();

    /**
     * Return whether the module should be enclosed in a scroll pane.
     *
     * @return	enclose module in scroll bar
     */
    public boolean encloseInScrollPane();

    /**
     * Tell module to save any current state data / preferences data because
     * the window is being closed.
     */
    public void save();
}

