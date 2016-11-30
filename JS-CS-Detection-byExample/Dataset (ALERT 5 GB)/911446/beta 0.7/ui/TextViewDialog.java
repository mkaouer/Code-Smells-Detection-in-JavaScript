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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.mov.main.ModuleFrame;
import org.mov.util.Locale;

/**
 * A dialog which display a text document to the user.
 *
 * @author Andrew Leppard
 */
public class TextViewDialog {

    private boolean isUp = true;
    private int buttonPressed = 0;
    private JInternalFrame internalFrame;

    // Minimum & preferred size to display text
    private final static int ROWS = 20;
    private final static int COLUMNS = 45;

    /** Display text with a fixed-width font. */
    public final static int FIXED_WIDTH = 0;

    /** Display text with a proportional width font. */
    public final static int PROPORTIONAL = 1;

    private TextViewDialog(String text, String title, int rows, int columns, int fontType,
                           String[] buttons) {
        layout(text, title, rows, columns, fontType, buttons);
    }

    private void layout(String text, String title, int rows, int columns, int fontType,
                        String[] buttons) {
        internalFrame = new JInternalFrame(title,
                                           true, /* resizable */
                                           true, /* closable */
                                           true, /* maximisible */
                                           true); /* iconifiable */
	JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel textPanel = new JPanel();
        final JTextArea textArea = new JTextArea(rows, columns);
        textArea.setText(text);
        textArea.setEditable(false);
        if(fontType == FIXED_WIDTH)
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        textPanel.setLayout(new BorderLayout());
        textPanel.add(new JScrollPane(textArea));

        panel.add(textPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();

        for(int i = 0; i < buttons.length; i++) {
            JButton button = new JButton(buttons[i]);
            final int buttonNumber = i;

            button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        buttonPressed = buttonNumber;
                        close();
                    }});
            buttonPanel.add(button);
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

    private int waitUntilClosed() {
	try {
	    while(isUp()) 
		Thread.sleep(10);

	} catch (InterruptedException e) {
            // Finish.
	}      

        return buttonPressed;
    }

    /**
     * Displays the text in a new dialog and waits for the user to cancel the
     * dialog.
     *
     * @param text the text to display
     * @param title the title of the dialog
     */
    public static void showTextDialog(String text, String title) {
        String buttons[] = {Locale.getString("OK")};
        showTextDialog(text, title, ROWS, COLUMNS, PROPORTIONAL, buttons);
    }

    /**
     * Displays the text in a new dialog and waits for the user to cancel the
     * dialog. This function allows the caller to provide a list of choices to
     * the user and also to specify the size of the text box in characters.
     *
     * @param text the text to display
     * @param title the title of the dialog
     * @param rows the height of the text box in characters
     * @param columns the width of the text box in characters
     * @param fontType type of font, either {@link #FIXED_WIDTH} or {@link #PROPORTIONAL}.
     * @param buttons a list of choices to be given to the user
     * @return button pressed
     */
    public static int showTextDialog(String text, String title, int rows, int columns,
                                     int fontType, String[] buttons) {
        assert fontType == FIXED_WIDTH || fontType == PROPORTIONAL;
        TextViewDialog dialog = new TextViewDialog(text, title, rows, columns, fontType, 
                                                   buttons);
	return dialog.waitUntilClosed();
    }
}
