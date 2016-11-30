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

package org.mov.prefs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.mov.util.Locale;
import org.mov.util.LocaleConstants;

/**
 * Provides a preference page to let the user specify their language.
 *
 * @author Alberto Nacher
 */
public class LanguagePage extends JPanel implements PreferencesPage {
    
    final private static int localeCount = LocaleConstants.localeCount;
    final private static java.util.Locale[] locales = LocaleConstants.locales;
    final private ButtonGroup group = new ButtonGroup();
    final private JRadioButton[] radioButtons = new JRadioButton[localeCount];
    
        
    private JDesktopPane desktop = null;
    private String languageCode = null;
    
    /**
     * Create a new language preferences page.
     *
     * @param	desktop	the parent desktop.
     */
    public LanguagePage(JDesktopPane desktop) {
	this.desktop = desktop;
	
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
	add(createLanguagePanel());
    }
    
    private JPanel createLanguagePanel() {
	JPanel languagePanel = new JPanel();
	languagePanel.setLayout(new BorderLayout());
	JPanel borderPanel = new JPanel();
        borderPanel.setLayout(new BoxLayout(borderPanel, BoxLayout.PAGE_AXIS));
	
        languageCode = PreferencesManager.loadLanguageCode();
        
        if (languageCode == null)
            languageCode = Locale.getLocale().getISO3Language();

        for (int i = 0; i < localeCount; i++) {
            radioButtons[i] = new JRadioButton(locales[i].getDisplayLanguage(Locale.getLocale()));
            radioButtons[i].setActionCommand(new Integer(i).toString());
            if (languageCode.equals(locales[i].getISO3Language()))
                radioButtons[i].setSelected(true);
            
            group.add(radioButtons[i]);
            borderPanel.add(radioButtons[i]);
        
            radioButtons[i].addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        String command = group.getSelection().getActionCommand();
                        int i = Integer.parseInt(command);
                        languageCode = locales[i].getISO3Language();
                    }
                });
        }

	languagePanel.add(borderPanel, BorderLayout.NORTH);

	return languagePanel;
    }
    
    public String getTitle() {
	return Locale.getString("LANGUAGE_PAGE_TITLE");
    }
    
    public void save() {
	PreferencesManager.saveLanguageCode(languageCode);
    }
    
    public JComponent getComponent() {
	return this;
    }
}
