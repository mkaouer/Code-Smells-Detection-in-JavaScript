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

import java.io.OutputStream;
import java.util.Iterator;

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

/**
 * This class writes watch screens in XML format.
 *
 * @author Andrew Leppard
 * @see WatchScreen
 * @see WatchScreenReader
 */
public class WatchScreenWriter {

    private WatchScreenWriter() {
        // Nothing to do
    }

    /**
     * Write the watch screen to the output stream in XML format.
     *
     * @param watchScreen the watch screen to write
     * @param stream      the output stream to write the watch screen.
     */
    public static void write(WatchScreen watchScreen, OutputStream stream) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element watchScreenElement = (Element)document.createElement("watch_screen");
            watchScreenElement.setAttribute("name", watchScreen.getName());
            document.appendChild(watchScreenElement);

            Element symbolsElement = (Element)document.createElement("symbols");

            watchScreenElement.appendChild(symbolsElement);
            for(Iterator iterator = watchScreen.getSymbols().iterator(); iterator.hasNext();) {
                Symbol symbol = (Symbol)iterator.next();
                Element symbolElement =
                    (Element)document.createElement("symbol");
                symbolElement.setAttribute("name", symbol.toString());
                symbolsElement.appendChild(symbolElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(stream);
            transformer.transform(source, result);
        }
        catch(ParserConfigurationException e) {
            // This should not occur
            assert false;
        }
        catch(TransformerException e) {
            // This should not occur
            assert false;
        }
    }
}