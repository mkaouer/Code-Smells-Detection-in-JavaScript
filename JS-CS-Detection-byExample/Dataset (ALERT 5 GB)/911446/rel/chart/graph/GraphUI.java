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

package nz.org.venice.chart.graph;

import java.util.HashMap;
import javax.swing.JPanel;

/**
 * Defines a user interface for a graph. Graphs often have user-definable
 * settings, such as the period of a moving average graph. Since
 * there are many types of graphs it makes sense standardising how the
 * user-definable settings are handled and how the user interface is
 * displayed to the user. Therefore each graph may have an associated
 * user interface which lets the user modify the graph's settings.
 *
 * <p>Each implementation of this class is instantiated by the associated
 * graph. Therefore this interface has no set constructor as the constructor
 * can vary depending on the graph.
 *
 * <p>This user interface will be displayed by the {@link nz.org.venice.chart.GraphSettingsDialog}.
 *
 * @author Andrew Leppard
 * @see AbstractGraph
 * @see Graph
 * @see nz.org.venice.chart.GraphSettingsDialog
 * @see nz.org.venice.chart.source.GraphSource
 */
public interface GraphUI {

    /**
     * Return the current settings displayed in the user interface.
     *
     * @return current settings
     */
    public HashMap getSettings();

    /**
     * Check the current settings displayed in the user interface. Return
     * an error message if the settings are invalid.
     *
     * @return the error message or <code>null</code> if the settings are valid
     */
    public String checkSettings();

    /**
     * Check previously set settings. Return
     * an error message if the settings are invalid.
     *
     * @param settings Previously saved settings
     * @return the error message or <code>null</code> if the settings are valid
     */
    public String checkSettings(HashMap settings);

    /**
     * Display the given settings in the user interface.
     *
     * @param settings the new settings
     */
    public void setSettings(HashMap settings);

    /**
     * Return the user interface.
     *
     * @return the user interface
     */
    public JPanel getPanel();
}