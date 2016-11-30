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

import java.io.OutputStream;
import java.util.List;
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
import org.w3c.dom.Text;



/**
 * This classes writes Symbol Metadata as XML.
 * 
 * @author Mark Hummel
 * @see SymbolMetadata
 * @see SymbolMetadataReader
 */

public class SymbolMetadataWriter {
    private SymbolMetadataWriter() {

    }

    /**
     * Write the symbol metadata to the output stream in XML format.
     *
     * @param symbolMetadata the symbol metadata to write
     * @param stream      the output stream to write the watch screen.
     */
    public static void write(List symbolMetadata, OutputStream stream) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	

        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element dataElement = (Element)document.createElement("symbolsMetadata");
	    Iterator iterator = symbolMetadata.iterator();
	    while (iterator.hasNext()) {
		SymbolMetadata data = (SymbolMetadata)iterator.next();
		
		Element childElement = (Element)document.createElement("symbolMetadata");
		Element symbolElement = (Element)document.createElement("symbol");
		Element nameElement = (Element)document.createElement("name");
		Element indexElement = (Element)document.createElement("isIndex");
		setValue(document, symbolElement, data.getSymbol().toString());
		setValue(document, nameElement, data.getName().toString());
		setValue(document, indexElement, new Boolean(data.isIndex()));

		childElement.appendChild(symbolElement);
		childElement.appendChild(nameElement);
		childElement.appendChild(indexElement);

		dataElement.appendChild(childElement);
            }

	    document.appendChild(dataElement);

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

    private static void setValue(Document document, Element e, Object value) {
	Text valueText = document.createTextNode(value.toString());	
	e.appendChild(valueText);
    }

}