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

package nz.org.venice.table;

import nz.org.venice.quote.Symbol;
import nz.org.venice.quote.SymbolFormatException;
import nz.org.venice.util.Locale;

import java.io.InputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

/**
 * This class parses watch screens written in XML format.
 *
 * @author Andrew Leppard
 * @see WatchScreen
 * @see WatchScreenWriter
 */
public class WatchScreenReader {

    /**
     * This class cannot be instantiated.
     */
    private WatchScreenReader() {
        // Nothing to do
    }

    /**
     * Read and parse the watch screen in XML format from the input stream and return
     * the watch screen object.
     *
     * @param stream the input stream containing the watch screen in XML format
     * @return the watch screen
     * @exception IOException if there was an I/O error reading from the stream.
     * @exception WatchScreenParserException if there was an error parsing the watch screen.
     */
    public static WatchScreen read(InputStream stream) throws IOException, WatchScreenParserException {
        WatchScreen watchScreen = null;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(stream);
            Element watchScreenElement = (Element)document.getDocumentElement();
            NamedNodeMap watchScreenAttributes = watchScreenElement.getAttributes();
            Node watchScreenNameNode = watchScreenAttributes.getNamedItem("name");

            if(watchScreenNameNode == null)
                throw new WatchScreenParserException(Locale.getString("MISSING_WATCH_SCREEN_NAME_ATTRIBUTE"));

            String watchScreenName = watchScreenNameNode.getNodeValue();

            watchScreen = new WatchScreen(watchScreenName);

            NodeList childNodes = watchScreenElement.getChildNodes();

            if (childNodes.getLength() == 1 &&
                childNodes.item(0).getNodeName().equals("symbols"))
                readSymbols(watchScreen, childNodes.item(0));
            else
                throw new WatchScreenParserException(Locale.getString("WATCH_SCREEN_TOP_LEVEL_ERROR"));

        } catch (SAXException e) {
            throw new WatchScreenParserException(e.getMessage());
        } catch(ParserConfigurationException e) {
            throw new WatchScreenParserException(e.getMessage());
        }

        return watchScreen;
    }

    /**
     * Read and parse the list of symbols in the watch screen.
     *
     * @param watchScreen the watch screen being created
     * @param symbolsNode the node containing the list of symbols.
     * @exception WatchScreenParserException if there was an error parsing the watch screen.
     */
    private static void readSymbols(WatchScreen watchScreen, Node symbolsNode)
        throws WatchScreenParserException {
        NodeList symbolNodeList = symbolsNode.getChildNodes();

        for(int i = 0; i < symbolNodeList.getLength(); i++) {
            Node symbolNode = (Node)symbolNodeList.item(i);
            NamedNodeMap symbolAttributes = symbolNode.getAttributes();
            Node symbolNameNode = symbolAttributes.getNamedItem("name");

            if(symbolNameNode == null)
                throw new WatchScreenParserException(Locale.getString("MISSING_SYMBOL_NAME_ATTRIBUTE"));

            String symbolName = symbolNameNode.getNodeValue();

            if(!symbolNode.getNodeName().equals("symbol"))
                throw new WatchScreenParserException(Locale.getString("EXPECTING_SYMBOL",
                                                                      symbolNode.getNodeName()));
            // Parse symbol, e.g. "CBA".
            Symbol symbol = null;

            try {
                symbol = Symbol.find(symbolNameNode.getNodeValue());
                watchScreen.addSymbol(symbol);
            } catch(SymbolFormatException e) {
                throw new WatchScreenParserException(e.getMessage());
            }
        }
    }
}
