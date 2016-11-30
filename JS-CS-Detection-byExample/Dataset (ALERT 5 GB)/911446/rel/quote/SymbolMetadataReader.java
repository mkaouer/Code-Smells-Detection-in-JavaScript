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

package nz.org.venice.quote;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

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
import org.w3c.dom.Text;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

public class SymbolMetadataReader {

    private SymbolMetadataReader() {

    }

    public static List read(InputStream stream) throws IOException {
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

	List symbolsMetadata = new ArrayList();

        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(stream);
            Element symbolsMetadataElement = (Element)document.getDocumentElement();
	    NodeList symbolsMetadataNode = symbolsMetadataElement.getElementsByTagName("symbolMetadata");

	    for (int i = 0; i < symbolsMetadataNode.getLength(); i++) {
		Element n = (Element)symbolsMetadataNode.item(i);
		String symbolString = getValue(n, "symbol");
		String nameString = getValue(n, "name");
		String isIndexString = getValue(n, "isIndex");		

		SymbolMetadata data = new SymbolMetadata(symbolString, nameString,
							 new Boolean(isIndexString).booleanValue());
		symbolsMetadata.add(data);
	    }
        } catch (SAXException e) {
            throw new IOException(e.getMessage());
        } catch(ParserConfigurationException e) {
            throw new IOException(e.getMessage());
        }

        return symbolsMetadata;
    }

    private static String getValue(Element e, String tagName) {
	
	NodeList list = e.getElementsByTagName(tagName);	
	assert list.getLength() == 1;
	
	Element parent = (Element)list.item(0);
	NodeList list2 = parent.getChildNodes();
	assert list2.getLength() == 1;
		
	Text textNode = (Text)list2.item(0);
	
	return textNode.getNodeValue();
    }
}
