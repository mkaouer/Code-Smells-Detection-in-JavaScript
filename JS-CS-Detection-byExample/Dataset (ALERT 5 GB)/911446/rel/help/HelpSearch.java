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

package nz.org.venice.help;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import java.io.Reader;
import java.io.StringReader;

import nz.org.venice.prefs.settings.Settings;
import nz.org.venice.prefs.settings.HelpModuleSettings;


/**
 * This class implements the help search functionality for the {@link HelpPage} Venice.
 * @see HelpModule
 */
public class HelpSearch  {

    String searchText;

    public HelpSearch(String text) {
		HTMLEditorKit.ParserCallback callback =
	    new HTMLEditorKit.ParserCallback () {

		/*
		  Parse the HTML creating a text string which resembles closely
		  as possible how the output appears in the EditorPane.

		  When a search is done, the index of the text in the pane
		  will be the same as the string created here.
		 */

		public void handleText(char[] data, int pos) {
                    for (int i = 0; i < data.length; i++) {
                        searchText += data[i];
                    }
                }

		public void handleSimpleTag(Tag t, MutableAttributeSet a,
				       int pos) {
		}
		public void handleStartTag(Tag t, MutableAttributeSet a,
				       int pos) {
		    if (t == Tag.LI) {
			searchText += "\n";
		    }
		    if (t == Tag.P) {
			searchText += "\n";
		    }

                    if (t == Tag.H2) {
                        searchText += "\n";
                    }

		    if (t == Tag.H3) {
			searchText += "\n";
		    }

		    if (t == Tag.UL) {
		    }

		    if (t == Tag.I) {
		    }

		    if (t == Tag.PRE) {
			searchText += "\n";
		    }
		}


		//The last line of html text is newline
		public void handleEndOfLineString(String eol) {
		    searchText += "\n";
		}
		public void handleComment(Tag t, MutableAttributeSet a,
				       int pos) {
		}

	    };

	try {
	    searchText = "";

	    Reader reader = new StringReader(text);
	    new ParserDelegator().parse(reader, callback, false);
	} catch (java.io.FileNotFoundException e) {
	    System.out.println("filenotfound: " + e);
	} catch (java.io.IOException e) {
	    System.out.println("ioexception: " + e);
	}

    }

    public int find(String searchTerm, int prevIndex) {
	return searchText.indexOf(searchTerm, prevIndex);
    }

}
