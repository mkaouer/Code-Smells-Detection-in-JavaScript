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

package org.mov.importer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.mov.util.Locale;

/**
 * Let the user loading or saving the preferences of MoV windows.
 *
 * @author Alberto Nacher
 */
public class PreferencesXML {
    
    private JDesktopPane desktop = null;
    
    private String path = null;
    
    // The base in the prefs tree where all Venice settings are stored
    private final static String base = "org.mov";

    // The user root from Venice's point of view
    private static Preferences userRoot = Preferences.userRoot().node(base);
    
    /* XML Filter File. */
    public class XMLFilter extends FileFilter {

        public static final String xml = "xml";
        
        //Accept all directories and xml files.
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = this.getExtension(f);
            if (extension != null) {
                if (extension.equals(XMLFilter.xml)) {
                        return true;
                } else {
                    return false;
                }
            }

            return false;
        }

        //The description of this filter
        public String getDescription() {
            return Locale.getString("XML_ONLY");
        }
        
        /*
         * Get the extension of a file.
         */  
        private String getExtension(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf(".");

            if (i > 0 &&  i < s.length() - 1) {
                ext = s.substring(i+1).toLowerCase();
            }
            return ext;
        }
    }

    /**
     * Class that manage the import/export of preferences into/from an XML file.
     *
     * @param	desktop	the parent desktop.
     */
    public PreferencesXML(JDesktopPane desktop) {
	this.desktop = desktop;
    }
    
    public void importPreferences() {
        // Get the path preferences file that the user wants to import
        JFileChooser chooser;
        String lastDirectory = loadImportPath();

        if(lastDirectory != null)
            chooser = new JFileChooser(lastDirectory);
        else
            chooser = new JFileChooser();

        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(new XMLFilter());
        int action = chooser.showOpenDialog(desktop);

        if(action == JFileChooser.APPROVE_OPTION) {
            // Remember directory
            lastDirectory = chooser.getCurrentDirectory().getAbsolutePath();
            saveImportPath(lastDirectory);

            File file = chooser.getSelectedFile();

            // Save new preferences from the file
            try {
                InputStream inputStream = new BufferedInputStream(
                    new FileInputStream(file));
                importPreferences(inputStream);
                inputStream.close();
            } catch (IOException ex) {
                JOptionPane.showInternalMessageDialog(desktop,
                                                      Locale.getString("ERROR_READING_FROM_FILE"),
                                                      Locale.getString("INVALID_PREFERENCES_ERROR"),
                                                      JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showInternalMessageDialog(desktop,
                                                      Locale.getString("INVALID_PREFERENCES_FORMAT_ERROR"),
                                                      Locale.getString("INVALID_PREFERENCES_ERROR"),
                                                      JOptionPane.ERROR_MESSAGE);
            }
        }
    }
	
    public void exportPreferences() {
        // Set the path preferences file that the user wants to export
        JFileChooser chooser;
        String lastDirectory = loadExportPath();

        if(lastDirectory != null)
            chooser = new JFileChooser(lastDirectory);
        else
            chooser = new JFileChooser();

        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(new XMLFilter());
        int action = chooser.showSaveDialog(desktop);

        if(action == JFileChooser.APPROVE_OPTION) {
            // Remember directory
            lastDirectory = chooser.getCurrentDirectory().getAbsolutePath();
            saveExportPath(lastDirectory);

            File file = chooser.getSelectedFile();

            // Save file in the system
            try {
                OutputStream outputStream = new BufferedOutputStream(
                    new FileOutputStream(file));
                exportPreferences(outputStream);
                outputStream.close();
            } catch (IOException ex) {
                JOptionPane.showInternalMessageDialog(desktop,
                                                      Locale.getString("ERROR_WRITING_TO_FILE"),
                                                      Locale.getString("INVALID_PREFERENCES_ERROR"),
                                                      JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showInternalMessageDialog(desktop,
                                                      Locale.getString("INVALID_PREFERENCES_FORMAT_ERROR"),
                                                      Locale.getString("INVALID_PREFERENCES_ERROR"),
                                                      JOptionPane.ERROR_MESSAGE);
            }
        }
    }
        
    /**
     * Get the preferences from the input XML stream
     * @param inputStream the input XML stream
     */
    private static void importPreferences(InputStream inputStream)
        throws IOException, InvalidPreferencesFormatException {
            Preferences.importPreferences(inputStream);
    }

    /**
     * Set the preferences in the output XML stream
     * @param outputStream the output XML stream
     */
    private static void exportPreferences(OutputStream outputStream)
        throws IOException, BackingStoreException {
            userRoot.exportSubtree(outputStream);
    }

    /**
     * Load import path.
     *
     * @return import path.
     */
    private static String loadImportPath() {
        Preferences prefs = getUserNode("/prefs");
        String retValue = prefs.get("importPath", "");
        return retValue;
    }

    /**
     * Load export path.
     *
     * @return export path.
     */
    private static String loadExportPath() {
        Preferences prefs = getUserNode("/prefs");
        String retValue = prefs.get("exportPath", "");
        return retValue;
    }

    /**
     * Save import path.
     *
     * @param importPath the new import path.
     */
    private static void saveImportPath(String importPath) {
	Preferences prefs = getUserNode("/prefs");
	prefs.put("importPath", importPath);	
    }

    /**
     * Save export path.
     *
     * @param exportPath the new export path.
     */
    private static void saveExportPath(String exportPath) {
	Preferences prefs = getUserNode("/prefs");
	prefs.put("exportPath", exportPath);	
    }
    
    /**
     * Fetches the desired user node, based at the <code>base</code> branch
     * @param node the path to the node to be fetched
     */
    private static Preferences getUserNode(String node) {
        if (node.charAt(0) == '/') node = node.substring(1);
        return userRoot.node(node);
    }

}
