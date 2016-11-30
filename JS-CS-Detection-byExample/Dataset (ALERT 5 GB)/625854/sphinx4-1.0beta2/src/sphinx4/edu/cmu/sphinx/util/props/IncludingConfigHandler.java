package edu.cmu.sphinx.util.props;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Handles configurations like the old one but is also able to process a new "include"-field
 *
 * @author Holger Brandl
 */
public class IncludingConfigHandler extends ConfigHandler {

    private boolean replaceDuplicates = false;
    private URL baseURL;


    public IncludingConfigHandler(Map<String, RawPropertyData> rpdMap, GlobalProperties globalProperties, boolean replaceDuplicates, URL baseURL) {
        super(rpdMap, globalProperties);

        this.replaceDuplicates = replaceDuplicates;
        this.baseURL = baseURL;
    }


    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("config")) {
            // test if this configuration extends another one
            String extendedConfigName = attributes.getValue("extends");
            if (extendedConfigName != null) {
                mergeConfigs(extendedConfigName, true);
                replaceDuplicates = true;
            }

        } else if (qName.equals("include")) {
            String includeFileName = attributes.getValue("file");
            mergeConfigs(includeFileName, false);

        } else if (qName.equals("extendwith")) {
            String includeFileName = attributes.getValue("file");
            mergeConfigs(includeFileName, true);

        } else if (qName.equals("component")) {
            String curComponent = attributes.getValue("name");
            String curType = attributes.getValue("type");

            if (rpdMap.get(curComponent) != null && !replaceDuplicates) {
                throw new SAXParseException("duplicate definition for " + curComponent, locator);
            }

            rpd = new RawPropertyData(curComponent, curType);

        } else if (qName.equals("property")) {
            String name = attributes.getValue("name");

//                  if (rpd == null) {
//                      if (attributes.getValue("string") != null) {
//                          globalProperties.put(name, new GlobalProperty(attributes.getValue("string")));
//
//                      } else if (attributes.getValue("float") != null) {
//                          Double value = Double.parseDouble(attributes.getValue("float"));
//                          globalProperties.put(name, new GlobalProperty(value));
//
//                      } else if (attributes.getValue("int") != null) {
//                          Integer value = Integer.parseInt(attributes.getValue("int"));
//                          globalProperties.put(name, new GlobalProperty(value));
//
//                      } else if (attributes.getValue("boolean") != null) {
//                          Boolean value = Boolean.getBoolean(attributes.getValue("boolean"));
//                          globalProperties.put(name, new GlobalProperty(value));
//                      }
//                  }

            String value = attributes.getValue("value");
            if (attributes.getLength() != 2 || name == null) {
                throw new SAXParseException("property element must only have " + "'name' and 'value' attributes", locator);
            }

            if (rpd == null) {
                // we are not in a component so add this to the global
                // set of symbols
//                    String symbolName = "${" + name + "}"; // why should we warp the global props here
                globalProperties.setValue(name, value);

            } else if (rpd.contains(name) && !replaceDuplicates) {
                throw new SAXParseException("Duplicate property: " + name, locator);

            } else {
                rpd.add(name, value);
            }

        } else if (qName.equals("propertylist")) {
            itemListName = attributes.getValue("name");
            if (attributes.getLength() != 1 || itemListName == null) {
                throw new SAXParseException("list element must only have the 'name'  attribute", locator);
            }
            itemList = new ArrayList<String>();

        } else if (qName.equals("item")) {
            if (attributes.getLength() != 0) {
                throw new SAXParseException("unknown 'item' attribute", locator);
            }
            curItem = new StringBuffer();

        } else {
            throw new SAXParseException("Unknown element '" + qName + "'", locator);
        }
    }


    private void mergeConfigs(String configFileName, boolean replaceDuplicates) {
        try {

            File parent = new File(baseURL.getFile()).getParentFile();
            URL fileURL = new File(parent.getPath() + File.separatorChar +  configFileName).toURI().toURL();

            Logger logger = Logger.getLogger(IncludingConfigHandler.class.getSimpleName());
            logger.fine((replaceDuplicates ? "extending" : "including") + " config:" + fileURL.toURI());

            SaxLoader saxLoader = new SaxLoader(fileURL, globalProperties, rpdMap, replaceDuplicates);
            saxLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error while processing <include file=\"" + configFileName + "\">: " + e.toString(), e);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
