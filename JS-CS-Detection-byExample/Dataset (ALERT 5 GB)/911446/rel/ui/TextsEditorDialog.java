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

package nz.org.venice.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import nz.org.venice.main.ModuleFrame;
import nz.org.venice.util.Locale;

public class TextsEditorDialog {

    private boolean isUp = true;
    private boolean wasCancelled = false;
    private JInternalFrame internalFrame;

    // Texts we are working with
    private String[] texts;
    private boolean[] areas;
    private JTextArea[] textArea;
    private JTextField[] textField;

    // Width of text field: [<-width->]
    private final static int TEXT_WIDTH = 20;

    // Minimum & preferred size to display equation */
    private final static int AREA_ROWS = 5;
    private final static int AREA_COLUMNS = 30;

    // Whether we should display just the OK button or the OK and 
    // the cancel button
    private final static int OK_BUTTON        = 0;
    private final static int OK_CANCEL_BUTTON = 1;

    private TextsEditorDialog(String title,
                                    String[] labels,
                                    boolean[] areas,
                                    String[] texts,
                                    int buttonArray, boolean isEditable) {
	this.texts = texts;
	this.areas = areas;
        assert buttonArray == OK_BUTTON || buttonArray == OK_CANCEL_BUTTON;

        buildDialog(title, labels,
                    buttonArray, isEditable);
    }

    private void buildDialog(String title,
                                    String[] labels,
                                    int buttonArray, boolean isEditable) {
        
        // arrays have to be all of the same length
        if ((this.texts.length!=areas.length) || (this.texts.length!=labels.length))
            return;
        int textLength = this.texts.length;
        
        internalFrame = new JInternalFrame(title,
                                           true, /* resizable */
                                           false, /* closable */
                                           false, /* maximisible */
                                           false); /* iconifiable */
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        textArea = new JTextArea[textLength];
        for (int ii=0; ii<textLength; ii++) {
            textArea[ii] = new JTextArea(AREA_ROWS, AREA_COLUMNS);
            textArea[ii].setText(texts[ii]);
            textArea[ii].setEditable(isEditable);
        }

        textField = new JTextField[textLength];
        for (int ii=0; ii<textLength; ii++) {
            textField[ii] = new JTextField(texts[ii], TEXT_WIDTH);
            textField[ii].setEditable(isEditable);
        }

        JPanel[] textPanel = new JPanel[textLength];
        TitledBorder[] textTitledBorder = new TitledBorder[textLength];
        for (int ii=0; ii<textLength; ii++) {
            textTitledBorder[ii] = new TitledBorder(labels[ii]);
            textPanel[ii] = new JPanel();
            textPanel[ii].setLayout(new BoxLayout(textPanel[ii], BoxLayout.Y_AXIS));
            textPanel[ii].setBorder(textTitledBorder[ii]);
            if (this.areas[ii])
                textPanel[ii].add(new JScrollPane(textArea[ii]));
            else
                textPanel[ii].add(new JScrollPane(textField[ii]));
            panel.add(textPanel[ii]);
        }

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton(Locale.getString("OK"));
        okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Update texts
                    updateTexts();
                    wasCancelled = false;
                    close();
                }});
	
        buttonPanel.add(okButton);	
	okButton.requestFocus();

        // The cancel button may not be displayed
        if(buttonArray == OK_CANCEL_BUTTON) {
            JButton cancelButton = new JButton(Locale.getString("CANCEL"));
            cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // User cancelled dialog so don't modify equation
			wasCancelled = true;
                        close();
                    }});
            buttonPanel.add(cancelButton);
        }

        panel.add(buttonPanel);
        
        internalFrame.getContentPane().add(panel);

        Dimension preferred = internalFrame.getPreferredSize();
        internalFrame.setMinimumSize(preferred);
        ModuleFrame.setSizeAndLocation(internalFrame, DesktopManager.getDesktop(),
                                      true, true);
        DesktopManager.getDesktop().add(internalFrame);
        internalFrame.show();

	try {
	    internalFrame.setSelected(true);
	}
	catch(PropertyVetoException v) {
	    // ignore
	}
	
	internalFrame.moveToFront();		    
    }

    private void close() {
        isUp = false;
        try {
            internalFrame.setClosed(true);
        }
        catch(PropertyVetoException e) {
            // nothing to do
        }
    }

    private boolean isUp() {
        return isUp;
    }

    private void updateTexts() {
        for (int ii=0; ii<this.texts.length; ii++) {
            if (this.areas[ii])
                texts[ii]=textArea[ii].getText();
            else
                texts[ii]=textField[ii].getText();
        }
    }

    private String[] getTexts() {
        return this.texts;
    }

    private boolean waitUntilClosed() {
	try {
	    while(isUp()) 
		Thread.sleep(10);

	} catch (InterruptedException e) {
            // Finish.
	}        

	return wasCancelled;
    }

    // make sure you run this in its own thread - not in the swing dispatch thread!
    public static String[] showAddDialog(String title, String[] labels, boolean[] areas) {
	boolean isValid = false;
        String[] texts = new String[labels.length];
        String[] retValue = new String[labels.length];
	
	while(!isValid) {
	    TextsEditorDialog dialog = new TextsEditorDialog(title, labels, areas, texts,
								       OK_CANCEL_BUTTON,
								       true);
	    boolean wasCancelled = dialog.waitUntilClosed();
	    retValue = dialog.getTexts();

	    isValid = true;
	}
		
	return retValue;
    }

    // make sure you run this in its own thread - not in the swing dispatch thread!
    public static String[] showEditDialog(String title, String[] labels, boolean[] areas, String[] texts) {
	boolean isValid = false;
        String[] retValue = new String[texts.length];
	
	while(!isValid) {
	    TextsEditorDialog dialog = new TextsEditorDialog(title, labels, areas, texts,
								       OK_CANCEL_BUTTON,
								       true);
	    boolean wasCancelled = dialog.waitUntilClosed();
	    retValue = dialog.getTexts();

	    isValid = true;
	}
		
	return retValue;
    }

}
