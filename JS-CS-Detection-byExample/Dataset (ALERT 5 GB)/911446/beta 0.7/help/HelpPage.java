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

package org.mov.help;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.tree.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import org.mov.util.Locale;

/**
 * This package defines a single help page that can be viewed by the
 * {@link HelpModule} help browser. The help page is actually a node in a tree
 * so a help page can contain child pages - creating a hierarchichal document.
 *
 * @author Andrew Leppard
 */
public class HelpPage extends DefaultMutableTreeNode {

    // Location of help docs
    private final static String BASE_PATH = "org/mov/help/doc/";
    private final static String INDEX_DOCUMENT = "org/mov/help/doc/index.xml";

    private String name;
    private String link;
    private String text;
    private boolean isLoaded;

    /**
     * Create a new help page with the given chapter name. The page will display
     * the contents of the help file in the src/org/mov/help/doc/ directory which
     * has the same name as name with a trailing "html".
     *
     * @param name the name of the chapter
     */
    public HelpPage(String name) {
        super(name);
        this.name = name;
        this.link = nameToLink(name);

        isLoaded = false;
    }

    /**
     * Get the name of the chapter.
     *
     * @return the chapter name
     */
    public String getName() {
        return name;
    }

    /**
     * Create the text that a HTML link would have if it pointed to us. This
     * is also the name of the file that contains the page.
     *
     * @return the page link
     */
    public String getLink() {
        return link;
    }

    /**
     * Return the HTML text in this page.
     *
     * @return the HTML text
     */
    public String getText() {
        // Make sure page is loaded
        loadText();

        return text;
    }

    /**
     * Search the document tree, starting with this node and return the first
     * document that has the given link. Or NULL if not found.
     *
     * @param link the page link to search for
     * @return the page
     */
    public HelpPage findPageWithLink(String link) {
        for(Enumeration enumeration = preorderEnumeration();
            enumeration.hasMoreElements();) {
            HelpPage page =
                (HelpPage)enumeration.nextElement();

            if(page.getLink().equals(link))
                return page;
        }
        return null;

    }

    // Convert the given chapter name to link
    private String nameToLink(String name) {
        String link = name.concat(".html");

        return link;
    }

    // Load the help page from disk
    private void loadText() {

        if(!isLoaded) {
            String fileName = BASE_PATH.concat(link);
            URL fileURL = ClassLoader.getSystemResource(fileName);
            StringBuffer stringBuffer = new StringBuffer();

            // Read file
	    if (fileURL != null) {
		try {
		    InputStream is = fileURL.openStream();
		    InputStreamReader isr = new InputStreamReader(fileURL.openStream());
		    BufferedReader br = new BufferedReader(isr);		

		    // ... one line at a time
		    String line = br.readLine();
		
		    while(line != null) {
			stringBuffer = stringBuffer.append(line);
                        stringBuffer = stringBuffer.append("\n");

			line = br.readLine();
		    }

		    br.close();		
		}
		catch(java.io.IOException e) {
		    text = Locale.getString("ERROR_LOADING_HELP_PAGE");
		    return;
		}
	    }
	    else {
		text = Locale.getString("HELP_PAGE_NOT_FOUND");
		return;
	    }

            text = stringBuffer.toString();
            isLoaded = true;
        }
    }

    /**
     * Load the index of the help documentation. This will create a tree of
     * HelpPages for each chapter in the document. The pages' text won't be loaded.
     *
     * @return the root help page
     */
    public static HelpPage loadIndex() {
        HelpPage index = null;
        Document document = loadIndexDocument();

        if(document != null) {
            index = new HelpPage(Locale.getString("VENICE_SHORT"));
            Element root = document.getDocumentElement();

            buildIndex(index, root);
        }

        if(index == null)
            index = new HelpPage(Locale.getString("ERROR_LOADING_INDEX"));

        return index;
    }

    // Recurse through the index file creating help pages
    private static void buildIndex(HelpPage index, Element root) {
        Node node = root.getFirstChild();

        while(node != null) {

            // Skip text, comment nodes etc
            if(node.getNodeType() == Node.ELEMENT_NODE) {

                Element element = (Element)node;

                // Make sure it's correclty formed
                assert element.getNodeName().equals("chapter");
                assert element.hasAttribute("name");

                HelpPage page = new HelpPage(element.getAttribute("name"));
                index.add(page);
                buildIndex(page, element);
            }

            node = node.getNextSibling();
        }
    }

    // This loads the index.xml file which contains the index.
    private static Document loadIndexDocument() {
        Document document = null;

        try {
            URL fileURL = ClassLoader.getSystemResource(INDEX_DOCUMENT);

            if(fileURL != null) {
                DocumentBuilderFactory documentBuilderFactory =
                    DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder =
                    documentBuilderFactory.newDocumentBuilder();
                document = documentBuilder.parse(fileURL.openStream());
            }
        }

        // We don't care about all these individual messages. We can't deal
        // with them all. We only care about dealing with two cases: It either
        // loaded or it didn't. If it didn't, return null.
        catch(IOException i) { }
        catch(DOMException d) { }
        catch(ParserConfigurationException p) { }
        catch(SAXException e) { }

        return document;
    }
}
