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

package org.mov.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.mov.main.ModuleFrame;
import org.mov.util.Locale;

public class EditorDialog {
    
    private JInternalFrame internalFrame;
    
    // Width of text field: Name: [<-width->]
    private final static int NAME_WIDTH = 20;
    
    // Minimum & preferred size to display equation */
    private final static int NUMBER_ROWS = 14;
    private final static int NUMBER_COLUMNS = 30;
    
    // Whether we should display just the OK button or the OK and
    // the cancel button
    private final static int OK_BUTTON        = 0;
    private final static int OK_CANCEL_BUTTON = 1;
    
    private EditorDialog(String title, String shortTitle, String input, int buttonArray, boolean isEditable) {
        assert buttonArray == OK_BUTTON || buttonArray == OK_CANCEL_BUTTON;
        
        buildDialog(title, shortTitle, input, buttonArray, isEditable);
    }
    
    private void buildDialog(String title, String shortTitle, String input, int buttonArray,
        boolean isEditable) {
        internalFrame = new JInternalFrame(title,
            true, /* resizable */
            false, /* closable */
            false, /* maximisible */
            false); /* iconifiable */
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel innerPanel = new JPanel();
        final JTextArea innerEditor = new JTextArea(NUMBER_ROWS,
            NUMBER_COLUMNS);
        innerEditor.setText(input);
        innerEditor.setEditable(isEditable);
        
        TitledBorder titledBorder = new TitledBorder(shortTitle);
        innerPanel.setLayout(new BorderLayout());
        innerPanel.setBorder(titledBorder);
        innerPanel.add(new JScrollPane(innerEditor));

        panel.add(innerPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton(Locale.getString("OK"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }});
            buttonPanel.add(okButton);
            
            // The cancel button may not be displayed
            if(buttonArray == OK_CANCEL_BUTTON) {
                JButton cancelButton = new JButton(Locale.getString("CANCEL"));
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        close();
                    }});
                    buttonPanel.add(cancelButton);
            }
            
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
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
        try {
            internalFrame.setClosed(true);
        }
        catch(PropertyVetoException e) {
            // nothing to do
        }
    }
    
    // make sure you run this in its own thread - not in the swing dispatch thread!
    public static void showViewDialog(String title, String shortTitle, String input) {
        EditorDialog dialog = new EditorDialog(title, shortTitle, input, OK_BUTTON, false);
    }
    
}
