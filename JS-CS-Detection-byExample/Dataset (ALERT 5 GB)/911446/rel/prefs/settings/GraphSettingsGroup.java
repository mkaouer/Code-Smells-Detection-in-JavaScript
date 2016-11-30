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

package nz.org.venice.prefs.settings;

import java.util.*;
import nz.org.venice.prefs.settings.GraphSettings;

/*
  This class groups the data necessary to recreate levels and graphs
  for a chart. It is used exclusively by ChartModule and therefore could have 
  been a nested class. However, that forces the persistence library to save 
  details about the ChartModule, which extends JPanel, a Swing class. 
  The resulting file contained a lot of irrelevant data and was much too large. 
 */

public class GraphSettingsGroup {

    private int levelIndex;
    private String symbol;
    private GraphSettings graphSettings;
    private java.util.List subGraphSettingsList; //A list of graphSettingsGroup objects
	

	public GraphSettingsGroup() {

	}

	public GraphSettings getGraphSettings() {
	    return graphSettings;
	}

	public void setGraphSettings(GraphSettings graphSettings) {
	    this.graphSettings = graphSettings;
	}

	public java.util.List getSubGraphSettingsList() {
	    return subGraphSettingsList;
	}

	public void setSubGraphSettingsList(java.util.List subGraphSettingsList) {
	    this.subGraphSettingsList = subGraphSettingsList;
	}

	public int getLevelIndex() {
	    return levelIndex;
	}

	public void setLevelIndex(int levelIndex) {
	    this.levelIndex = levelIndex;
	}

	public String getSymbol() {
	    return symbol;
	}

	public void setSymbol(String symbol) {
	    this.symbol = symbol;
	}
}





