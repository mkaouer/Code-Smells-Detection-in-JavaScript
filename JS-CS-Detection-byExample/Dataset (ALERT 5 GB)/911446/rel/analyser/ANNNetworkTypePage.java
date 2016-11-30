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

package nz.org.venice.analyser;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import nz.org.venice.analyser.ann.ArtificialNeuralNetwork;
import nz.org.venice.analyser.ann.ANNConstants;
import nz.org.venice.analyser.ann.FileExtensionException;
import nz.org.venice.prefs.PreferencesManager;
import nz.org.venice.ui.GridBagHelper;
import nz.org.venice.ui.ProgressDialog;
import nz.org.venice.util.Locale;


/**
 * @author Alberto Nacher
 */
public class ANNNetworkTypePage extends Page implements AnalyserPage {
    // The core object which manages the Joone ANN
    private ArtificialNeuralNetwork artificialNeuralNetwork;
        
    // Swing items
    private JRadioButton defaultANNButton;
    private JRadioButton customANNButton;
    private JTextField pathTextField;
    private JButton loadButton;
    private JButton saveButton;
    
    // Default directory used for loading/saving ANNs
    private String lastDirectory;
        
    /* Joone filter file. */
    public class JooneFilter extends FileFilter {

        // Got the extension allowed to load/save artificial neural network
        // in Joone formats
        public static final String snet = ANNConstants.SNET;
        public static final String xml = ANNConstants.XML;
        
        // Accept all directories and snet/xml files.
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = this.getExtension(f);
            if (extension != null) {
                if (extension.equals(JooneFilter.snet)) {
                    // .snet file
                    return true;
                } else if (extension.equals(JooneFilter.xml)) {
                    // .xml file
                    return true;
                } else {
                    return false;
                }
            }

            return false;
        }

        // The description of this filter
        public String getDescription() {
            return Locale.getString("JOONE_ONLY");
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
     * Construct a new network type parameters page.
     * It manages:
     * the default/custom artificial neural network choice.
     * the import/export of artificial neural network from/to file system and Joone,
     *
     * @param desktop the desktop
     */
    public ANNNetworkTypePage(JDesktopPane desktop) {
        
        this.desktop = desktop;
        this.artificialNeuralNetwork = new ArtificialNeuralNetwork(desktop);
        
        setGraphic();
        
    }
    
    /** 
     * Save the preferences
     */
    public void save(String key) {
        
        HashMap settings = new HashMap();
        
        settings.put("last_directory", (lastDirectory==null) ? "" : lastDirectory);
        settings.put("ann_file", (pathTextField==null) ? "" : pathTextField.getText());
        settings.put("network_type", defaultANNButton.isSelected() ? "default" : "custom");
        
        // Check if ANN has been already saved, because we want to change it.
        // If not saved, ask if the user want to save it before changing.
        if (!artificialNeuralNetwork.isSaved()) {
            int answer = JOptionPane.showConfirmDialog(desktop,
                Locale.getString("ANN_NOT_SAVED_MESSAGE"),
                Locale.getString("ANN_NOT_SAVED_TITLE"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                // save the network
                try {
                    saveButton.doClick();
                } catch(Exception ex) {
                    // do nothing
                }
            }
        }
        
        PreferencesManager.putAnalyserPageSettings(key + getClass().getName(), settings);
    }
    
    /** 
     * Load the preferences
     */
    public void load(String key) {
        
        // Load last GUI settings from preferences
        HashMap settings =
                PreferencesManager.getAnalyserPageSettings(key + getClass().getName());
        
        Iterator iterator = settings.keySet().iterator();
        
        while (iterator.hasNext()) {
            String setting = (String) iterator.next();
            String value = (String) settings.get((Object) setting);
            
            if (value != null) {
                if (setting.equals("last_directory")) {
                    lastDirectory = value;
                } else if(setting.equals("ann_file")) {
                    pathTextField.setText(value);
                } else if(setting.equals("network_type")) {
                    if(value.equals("default")) {
                        defaultANNButton.setSelected(true);
                    }
                    else {
                        customANNButton.setSelected(true);
                    }
                }
            }
        }
        
    }

    /** 
     * Parse the GUI.
     * We don't need parse() method, we use parse(int inputRows) instead,
     * because we need to know how many input we have to manage with ANN.
     */
    public boolean parse() {
        return true;
    }
    
    /** 
     * Parse the GUI
     */
    public boolean parse(int inputRows) {
        boolean retValue = false;
        int answer = 0;
        if (this.isDefaultSelected()) {
            if (this.artificialNeuralNetwork.isANNNull()) {
                // There is no ANN in memory, we build it as default.
                this.artificialNeuralNetwork.setDefaultANN(inputRows);
                retValue = true;
            } else {
                if (!this.artificialNeuralNetwork.isInputOK(inputRows)) {
                    answer = JOptionPane.showConfirmDialog(desktop,
                        Locale.getString("ANN_INPUT_MESSAGE"),
                        Locale.getString("ANN_INPUT_TITLE"),
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                    if (answer == JOptionPane.YES_OPTION) {
                        // There is an ANN in memory, but with different input.
                        // Change the input of ANN in memory.
                        this.artificialNeuralNetwork.setANNInput(inputRows);
                        retValue = true;
                    } else {
                        // The user doesn't want to change the ANN input number.
                        retValue = false;
                    }
                } else {
                    // ANN already in memory, we keep it without changing it at all.
                    retValue = true;
                }
            }
        } else {
            if (this.artificialNeuralNetwork.isANNNull()) {
                answer = JOptionPane.showConfirmDialog(desktop,
                    Locale.getString("ANN_NO_MESSAGE"),
                    Locale.getString("ANN_NO_TITLE"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    // There is no ANN in memory, we load it from file.
                    loadButton.doClick();
                    retValue = false;
                } else {
                    // There is no ANN in memory,
                    // the user doesn't want to build it as default.
                    retValue = false;
                }
            } else if (!this.artificialNeuralNetwork.isInputOK(inputRows)) {
                answer = JOptionPane.showConfirmDialog(desktop,
                    Locale.getString("ANN_INPUT_MESSAGE"),
                    Locale.getString("ANN_INPUT_TITLE"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    // There is an ANN in memory, but with different input.
                    // Change the input of ANN in memory.
                    this.artificialNeuralNetwork.setANNInput(inputRows);
                    retValue = true;
                } else {
                    // The user doesn't want to change the ANN input number.
                    retValue = false;
                }
            } else if (!this.artificialNeuralNetwork.isOutputOK()) {
                answer = JOptionPane.showConfirmDialog(desktop,
                    Locale.getString("ANN_OUTPUT_MESSAGE"),
                    Locale.getString("ANN_OUTPUT_TITLE"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    // There is an ANN in memory, but with different output.
                    // Change the output of ANN in memory.
                    this.artificialNeuralNetwork.setANNOutput();
                    retValue = true;
                } else {
                    // The user doesn't want to change the ANN output number.
                    retValue = false;
                }
            } else {
                // ANN already in memory, we keep it without changing it at all.
                retValue = true;
            }
        }
        return retValue;
    }
    
    public JComponent getComponent() {
        return this;
    }

    public String getTitle() {
        return Locale.getString("ANN_PAGE_NETWORK_TYPE_SHORT");
    }

    /** 
     * Check if it has been selected the ANN default radio button
     */
    private boolean isDefaultSelected() {
        return defaultANNButton.isSelected();
    }

    /** 
     * Get the ANN currently in memory
     */
    public ArtificialNeuralNetwork getANN() {
        return artificialNeuralNetwork;
    }

    /**
     * Set the progress bar,
     * so that artificial neural network can manage it
     * when training cycle terminated event is raised.
     */
    public void setProgressBar(ProgressDialog progress) {
        artificialNeuralNetwork.setProgressBar(progress);
    }
    
    /** 
     * Set the GUI
     */
    private void setGraphic() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	// Network Type Selection Panel
        TitledBorder networkTypeTitled =
                new TitledBorder(Locale.getString("ANN_PAGE_NETWORK_TYPE_LONG"));
        JPanel panel = new JPanel();
        panel.setBorder(networkTypeTitled);
        panel.setLayout(new BorderLayout());

        JPanel innerPanel = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        innerPanel.setLayout(gridbag);
        

        // Buttons
        ButtonGroup buttonGroup = new ButtonGroup();

        // Default ANN
        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        defaultANNButton = new JRadioButton(Locale.getString("DEFAULT_ANN"));
        defaultANNButton.setSelected(true);
        defaultANNButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    // Disable path text field, because we want to use the default
                    // instead of the .snet/.xml file
                    pathTextField.setEnabled(false);
                    // Check if ANN has been already saved, because we want to change it.
                    // If not saved, ask if the user want to save it before changing.
                    if (!artificialNeuralNetwork.isSaved()) {
                        int answer = JOptionPane.showConfirmDialog(desktop,
                            Locale.getString("ANN_NOT_SAVED_MESSAGE"),
                            Locale.getString("ANN_NOT_SAVED_TITLE"),
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                        if (answer == JOptionPane.YES_OPTION) {
                            // save the network
                            saveButton.doClick();
                            artificialNeuralNetwork.setANNNull();
                        } else if (answer == JOptionPane.NO_OPTION) {
                            // change network without saving
                            artificialNeuralNetwork.setANNNull();
                        } else {
                            // do not change the network
                            customANNButton.setSelected(true);
                            pathTextField.setEnabled(true);
                        }
                    }
                }});
        buttonGroup.add(defaultANNButton);

        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(defaultANNButton, c);
        innerPanel.add(defaultANNButton);


        // Custom ANN
        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
        
        customANNButton = new JRadioButton(Locale.getString("CUSTOM_ANN"));
        customANNButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    // enable path text field, if the user want to set it manually
                    pathTextField.setEnabled(true);
                    int answer = JOptionPane.showConfirmDialog(desktop,
                        Locale.getString("ANN_CHANGE_MESSAGE"),
                        Locale.getString("ANN_CHANGE_TITLE"),
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                    if (answer == JOptionPane.YES_OPTION) {
                        // load the network
                        loadButton.doClick();
                    } else if (answer == JOptionPane.NO_OPTION) {
                        // change network without saving
                        // do nothing
                    } else {
                        // do not change the network
                        defaultANNButton.setSelected(true);
                        pathTextField.setEnabled(false);
                    }
                }});

        buttonGroup.add(customANNButton);

        c.gridwidth = 1;
        gridbag.setConstraints(customANNButton, c);
        innerPanel.add(customANNButton);


        // Components which load from and save to file system.
        // They manage the interchange of artificial neural network between Venice and Joone
        
        // Load button
        loadButton = new JButton(Locale.getString("LOAD_ANN"));
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                // Component for loading an ANN
                JFileChooser chooser;

                if(lastDirectory != null) {
                    chooser = new JFileChooser(lastDirectory);
                } else {
                    chooser = new JFileChooser();
                }

                chooser.setMultiSelectionEnabled(false);
                chooser.setFileFilter(new JooneFilter());

                int action = chooser.showOpenDialog(desktop);

                if(action == JFileChooser.APPROVE_OPTION) {

                    // Remember directory
                    lastDirectory = chooser.getCurrentDirectory().getAbsolutePath();
                    File file = chooser.getSelectedFile();
                    pathTextField.setText(file.toString());

                    // Load file in the system
                    try {
                        // Load
                        artificialNeuralNetwork.loadNeuralNet(
                                pathTextField.getText());
                        JOptionPane.showInternalMessageDialog(desktop,
                                Locale.getString("OK_READING_FROM_FILE"),
                                Locale.getString("ANN_IMPORT_EXPORT"),
                                JOptionPane.INFORMATION_MESSAGE);
                        customANNButton.setSelected(true);
                    } catch (FileExtensionException ex) {
                        // Not a valid Joone extensions error
                        showErrorMessage(
                        		Locale.getString("ERROR_EXTENSION_READING_FROM_FILE",file.toString()),
                                Locale.getString("INVALID_ANN_ERROR"));
                    } catch (Exception ex) {
                        // General file loading error
                    	showErrorMessage(
                        		Locale.getString("ERROR_READING_FROM_FILE",file.toString()),
                                Locale.getString("INVALID_ANN_ERROR"));
                    }

                }
            }
        });
        c.gridwidth = 1;
        gridbag.setConstraints(loadButton, c);
        innerPanel.add(loadButton);

        
        // Save button
        saveButton = new JButton(Locale.getString("SAVE_ANN"));
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                // Run file selection for loading an ANN
                JFileChooser chooser;

                if(lastDirectory != null) {
                    chooser = new JFileChooser(lastDirectory);
                } else {
                    chooser = new JFileChooser();
                }

                chooser.setMultiSelectionEnabled(false);
                chooser.setFileFilter(new JooneFilter());

                int action = chooser.showSaveDialog(desktop);

                if(action == JFileChooser.APPROVE_OPTION) {

                    // Remember directory
                    lastDirectory = chooser.getCurrentDirectory().getAbsolutePath();
                    File file = chooser.getSelectedFile();
                    pathTextField.setText(file.toString());

                    // Save file in the system
                    try {
                        // Save
                        artificialNeuralNetwork.saveNeuralNet(pathTextField.getText());
                        JOptionPane.showInternalMessageDialog(desktop,
                                Locale.getString("OK_WRITING_TO_FILE"),
                                Locale.getString("ANN_IMPORT_EXPORT"),
                                JOptionPane.INFORMATION_MESSAGE);
                        customANNButton.setSelected(true);
                    } catch (FileExtensionException ex) {
                        // Not a valid Joone extensions error
                    	showErrorMessage(
                        		Locale.getString("ERROR_EXTENSION_WRITING_TO_FILE",
                                    file.toString()),
                                Locale.getString("INVALID_ANN_ERROR"));
                    } catch (Exception ex) {
                        // General file saving error
                    	showErrorMessage(
                        		Locale.getString("ERROR_READING_TO_FILE",
                                    file.toString()),
                                Locale.getString("INVALID_ANN_ERROR"));
                    }

                }
            }
        });
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(saveButton, c);
        innerPanel.add(saveButton);
        
        // Path used to save/load the ANN to/from file system
        pathTextField = GridBagHelper.addTextRow(innerPanel,
        Locale.getString("PATH_ANN"), "",
        gridbag, c,
        17);
                
        // Put all on the screen
        panel.add(innerPanel, BorderLayout.NORTH);
        add(panel);
        
    }

}
