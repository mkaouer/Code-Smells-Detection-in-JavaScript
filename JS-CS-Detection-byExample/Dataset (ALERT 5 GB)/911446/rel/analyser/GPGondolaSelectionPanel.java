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

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Random;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;

import nz.org.venice.ui.GridBagHelper;
import nz.org.venice.util.Locale;
import nz.org.venice.util.VeniceLog;

public class GPGondolaSelectionPanel extends JPanel {

    private final static String format = GPModuleConstants.format;
    private final static double PERCENT_DOUBLE = GPModuleConstants.PERCENT_DOUBLE;
    private final static int PERCENT_INT = GPModuleConstants.PERCENT_INT;
    private final static int MAX_CHARS_IN_TEXTBOXES = 6;
    
    private JTextField[] percTextField;
    private int[] perc;
    private int[] defValues;
    private String[] defTextFieldValues;
    
    // In some panels last and/or last but one elements could
    // be not enough to permit GP working.
    // So we have to check that other percentages in the same panel are different from zero.
    private boolean isLastEnough = true;
    private boolean isLastButOneEnough = true;

    private JDesktopPane desktop;
    private long seed = System.currentTimeMillis();
    private Random random = new Random(seed);



    public GPGondolaSelectionPanel(int elements,
                                   JDesktopPane desktop,
                                   int[] defaultValues,
                                   String[] defaultTextFieldValues) {
        
        this.desktop = desktop;
        
        percTextField = new JTextField[elements];
        perc = new int[elements];
        defValues = defaultValues;
        defTextFieldValues = defaultTextFieldValues;
        
        setGraphic();
	
	VeniceLog.getInstance().log("GPGondolaSelectionPanel seed = " + seed);

    }
    
    public void setLastNotEnough() {
        isLastEnough = false;
    }
    
    public void setLastButOneNotEnough() {
        isLastButOneEnough = false;
    }
    
    public int getRandom() {
        return this.getRandom(true,true);
    }
    
    public int getRandom(boolean isOkLast) {
        return this.getRandom(true,isOkLast);
    }
    
    /** 
     * Generate a random number between 0 and the number of fields in the panel, optionally restricting the last, and the secondlast fields.
     * 
     * @param isOkLastButOne If false the range is restricted to field length-2  
     * @param isOkLast If false the range is restricted to field length - 1
     */

    public int getRandom(boolean isOkLastButOne, boolean isOkLast) {
        
        int retValue = 0;
        int total = 0;
        int totalLength = perc.length;

        for (int ii=0; ii<totalLength; ii++) {
            // skip last and/or last but one from the count
            // according to input parameters
            if( ((!isOkLastButOne) && (ii==(totalLength-2))) ||
                ((!isOkLast) && (ii==(totalLength-1)))){
                continue;
            }
	    
            total += perc[ii];
        }
        int randomValue = random.nextInt(total);
        	
        int totalMin = 0;
        int totalMax = 0;
        for (int ii=0; ii<totalLength; ii++) {
            // skip last and/or last but one from the count
            // according to input parameters
            if(((!isOkLastButOne) && (ii==(totalLength-2))) ||
                ((!isOkLast) && (ii==(totalLength-1)))){
                continue;
            }
            totalMax = totalMin + perc[ii];
 
            if ((randomValue >= totalMin) && (randomValue < totalMax)) {
                retValue = ii;            
	    } 	    
            totalMin += perc[ii];
	}      
        return retValue;
    }

    public void save(HashMap settings, String idStr) {
	for (int ii=0; ii<percTextField.length; ii++) {
            settings.put(idStr + (new Integer(ii)).toString(), percTextField[ii].getText());
        }
    }
    
    public void load(String setting, String idStr, String value) {
	for (int ii=0; ii<percTextField.length; ii++) {
            if(setting.equals(idStr + (new Integer(ii)).toString())) {
                percTextField[ii].setText(value);
            }
        }
    }
    
    // Fit the values, if they differ
    public void fit() {
        if (isAllValuesAcceptable()) {
            int total = 0;
            for (int ii=0; ii<perc.length; ii++) {
                total += perc[ii];
            }

            // Set dummy values according to PERCENT_INT that is the maximum
            int[] dummyPerc = new int[perc.length];
            for (int ii=0; ii<perc.length; ii++) {
                dummyPerc[ii] = Math.round((perc[ii] * PERCENT_INT) / total);
            }
            int dummyTotal = 0;
            for (int ii=0; ii<perc.length; ii++) {
                dummyTotal += dummyPerc[ii];
            }
            // Adjust approximations of Math.round method
            int count=0;
            while (dummyTotal!=PERCENT_INT) {
                if (dummyTotal>PERCENT_INT) {
                    dummyPerc[count]--;
                    dummyTotal--;
                } else {
                    dummyPerc[count]++;
                    dummyTotal++;
                }
                count++;
            }
            // Set new values
            for (int ii=0; ii<perc.length; ii++) {
                perc[ii] = dummyPerc[ii];
            }
            // Update the text in the user interface
            setTexts();
        }
    }

    // Return true if values already fit to percentage
    public boolean isFit() {
        boolean retValue = false;
        if (isAllValuesAcceptable()) {
            int total = 0;
            for (int ii=0; (ii<perc.length); ii++) {
                total += perc[ii];
            }
            if (total==PERCENT_INT) {
                retValue = true;
            }
        }
        return retValue;
    }

    public boolean isAllValuesAcceptable() {
        boolean retValue = true;
        try {
            setNumericalValues();
        }
	catch(ParseException e) {
            JOptionPane.showInternalMessageDialog(desktop,
                                                  Locale.getString("ERROR_PARSING_NUMBER",
                                                                   e.getMessage()),
                                                  Locale.getString("INVALID_GP_ERROR"),
                                                  JOptionPane.ERROR_MESSAGE);
	    retValue = false;
	}

        if(!isAllValuesPositive()) {
            JOptionPane.showInternalMessageDialog(desktop,
                                                  Locale.getString("NO_POSITIVE_VALUES_ERROR"),
                                                  Locale.getString("INVALID_GP_ERROR"),
                                                  JOptionPane.ERROR_MESSAGE);
	    retValue = false;
        }

        if(!isTotalOK()) {
            // Messages inside the isTotalOK method
	    retValue = false;
        }

        return retValue;
    }
    
    private void setNumericalValues() throws ParseException {
        setDefaultsValuesOnly();
    
        // decimalFormat manage the localization.
        DecimalFormat decimalFormat = new DecimalFormat(format);
        for (int ii=0; ii<perc.length; ii++) {
            if(!percTextField[ii].getText().equals("")) {
                perc[ii] =
                    (int) Math.round(PERCENT_DOUBLE*(decimalFormat.parse(percTextField[ii].getText()).doubleValue()));
            }
        }
    }
    
    private boolean isAllValuesPositive() {
        boolean returnValue = true;
        for (int ii=0; ii<perc.length; ii++) {
            returnValue = returnValue && (perc[ii]>=0);
        }
        return returnValue;
    }
    
    private boolean isTotalOK() {
        boolean retValue=true;
        // We should consider the absence of held and order -> totalIntegerModified
        long total = 0;
        int totalLength = perc.length;
        for (int ii=0; (ii<totalLength); ii++) {
            // skip last and/or last but one from the count
            // according to the type of panel
            if(((!isLastButOneEnough) && (ii==(totalLength-2))) ||
                ((!isLastEnough) && (ii==(totalLength-1)))){
                continue;
            }
            total += perc[ii];
        }
        // Check total == 0
        if (total==0) {
            JOptionPane.showInternalMessageDialog(desktop,
                                                  Locale.getString("NO_TOTAL_GREATER_THAN_ZERO_ERROR"),
                                                  Locale.getString("INVALID_GP_ERROR"),
                                                  JOptionPane.ERROR_MESSAGE);
            retValue=false;
        }
        return retValue;
    }
    
    public void setDefaultsValuesOnly() {
        for (int ii=0; ii<perc.length; ii++) {
            perc[ii]=defValues[ii];
        }
    }
        
    public void setDefaults() {
        for (int ii=0; ii<perc.length; ii++) {
            perc[ii]=defValues[ii];
        }
        this.setTexts();
    }
        
    public void setTexts() {
        DecimalFormat decimalFormat = new DecimalFormat(format);
        for (int ii=0; ii<percTextField.length; ii++) {
            percTextField[ii].setText(decimalFormat.format(perc[ii]/PERCENT_DOUBLE));
        }
    }
        
    private void setGraphic() {
        
        GridBagLayout gridbag = new GridBagLayout();
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JPanel upDownPanel = new JPanel();
        upDownPanel.setLayout(new BoxLayout(upDownPanel, BoxLayout.Y_AXIS));
        
        JScrollPane upDownScrollPane = new JScrollPane(upDownPanel);
        upDownScrollPane.setLayout(new ScrollPaneLayout());
        
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(gridbag);
        
        JButton fitButton = new JButton(Locale.getString("FIT"));
        JButton defaultButton = new JButton(Locale.getString("DEFAULT"));
        fitButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                // Fit Values
                fit();
            }
        });
        defaultButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                // Set Default Values
                setDefaults();
            }
        });
        fitButton.setAlignmentX(CENTER_ALIGNMENT);
        defaultButton.setAlignmentX(CENTER_ALIGNMENT);
        innerPanel.setAlignmentX(CENTER_ALIGNMENT);
	upDownPanel.add(fitButton);
	upDownPanel.add(defaultButton);
	upDownPanel.add(innerPanel);
        
        GridBagConstraints c = new GridBagConstraints();
        
        // Fill the tabber
        c.weightx = 1.0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;

        for (int ii=0; ii<percTextField.length; ii++) {
            percTextField[ii] =
                GridBagHelper.addTextRow(innerPanel, defTextFieldValues[ii], "",
                                     gridbag, c,
                                     MAX_CHARS_IN_TEXTBOXES);
        }
        
        this.add(upDownScrollPane);
    }

}
    
