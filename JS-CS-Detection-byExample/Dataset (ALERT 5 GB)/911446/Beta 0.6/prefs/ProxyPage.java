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

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JDesktopPane;
import javax.swing.BoxLayout;
import javax.swing.JPasswordField;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import org.mov.ui.GridBagHelper;
import org.mov.util.Locale;

/**
 * Provides a preference page to let the user specify their web proxy.
 *
 * @author Matthias Stockel
 * 
 * Provides the function of handle authentication requests in proxy.
 * @author Bryan Lin 2004-9-11 
 */
public class ProxyPage extends JPanel implements PreferencesPage {
    
    private JDesktopPane desktop = null;
    private PreferencesManager.ProxyPreferences proxyPreferences = null;
    private JCheckBox useProxyCheckBox = null;
    private JTextField hostTextField = null;
    private JTextField portTextField = null;

    private JCheckBox useAuthCheckBox = null;
    private JTextField userTextField = null;
    private JPasswordField passwordTextField= null;
    
    /**
     * Create a new proxy preferences page.
     *
     * @param	desktop	the parent desktop.
     */
    public ProxyPage(JDesktopPane desktop) {
	this.desktop = desktop;
	
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
	add(createProxyPanel());
    }
    
    private JPanel createProxyPanel() {
	JPanel proxyPanel = new JPanel();
	proxyPanel.setLayout(new BorderLayout());
	JPanel borderPanel = new JPanel();
	
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	borderPanel.setLayout(gridbag);
	
	c.weightx = 1.0;
	c.ipadx = 5;
	c.anchor = GridBagConstraints.WEST;

	proxyPreferences = 
	    PreferencesManager.loadProxySettings();

	useProxyCheckBox = 
	    GridBagHelper.addCheckBoxRow(borderPanel, 
					 Locale.getString("USE_PROXY"),
					 proxyPreferences.isEnabled, gridbag, c);
	useProxyCheckBox.addActionListener(new ActionListener() {
		public void actionPerformed(final ActionEvent e) {
		    checkDisabledStatus();
		}
	    });

	hostTextField = GridBagHelper.addTextRow(borderPanel,
						 Locale.getString("PROXY_HOST"),
						 proxyPreferences.host,
						 gridbag, c, 20);
	
	portTextField = GridBagHelper.addTextRow(borderPanel,
						 Locale.getString("PROXY_PORT"),
						 proxyPreferences.port,
						 gridbag, c, 5);
	
	useAuthCheckBox = 
	    GridBagHelper.addCheckBoxRow(borderPanel, 
					 Locale.getString("PROXY_AUTH"),
					 proxyPreferences.authEnabled, gridbag, c);
	useAuthCheckBox.addActionListener(new ActionListener() {
		public void actionPerformed(final ActionEvent e) {
		    checkAuthDisabledStatus();
		}
	    });

	userTextField = GridBagHelper.addTextRow(borderPanel,
						 Locale.getString("PROXY_USER"),
						 proxyPreferences.user,
						 gridbag, c, 10);
	
	passwordTextField = GridBagHelper.addPasswordRow(borderPanel,
						 Locale.getString("PROXY_PASSWORD"),
						 proxyPreferences.password,
						 gridbag, c, 10);
	
	proxyPanel.add(borderPanel, BorderLayout.NORTH);

	checkDisabledStatus();
	checkAuthDisabledStatus();					   
	return proxyPanel;
    }
    
    public String getTitle() {
	return Locale.getString("PROXY_PAGE_TITLE");
    }
    
    public void save() {
	proxyPreferences.host = hostTextField.getText();
	proxyPreferences.port = portTextField.getText();
	proxyPreferences.isEnabled = useProxyCheckBox.isSelected();
	
	proxyPreferences.user= userTextField.getText();
	proxyPreferences.password = new String(passwordTextField.getPassword());
	proxyPreferences.authEnabled = useAuthCheckBox.isSelected();
	
	PreferencesManager.saveProxySettings(proxyPreferences);
    }
    
    public JComponent getComponent() {
	return this;
    }

    private void checkAuthDisabledStatus() {
        boolean useAuth = useAuthCheckBox.isSelected();
	userTextField.setEnabled(useAuth);
	passwordTextField.setEnabled(useAuth);
    }
    
    private void checkDisabledStatus() {
        boolean useProxy = useProxyCheckBox.isSelected();
	hostTextField.setEnabled(useProxy);
	portTextField.setEnabled(useProxy);
    }
    
    /**
     * Setup the networking to handle authentication requests and work http
     * proxies correctly
     */
    public static void setupNetworking() {
        PreferencesManager.ProxyPreferences proxyPreferences = PreferencesManager
                .loadProxySettings();
        if (proxyPreferences.isEnabled) {
            System.getProperties().put("http.proxyHost", proxyPreferences.host);
            System.getProperties().put("http.proxyPort", proxyPreferences.port);
            // this will deal with any authentication requests properly
            if (proxyPreferences.authEnabled) {
            	//bryan
            	java.net.Authenticator.setDefault(new ProxyAuthenticator(proxyPreferences.user,proxyPreferences.password));
            }
        }
    }
}

/**
 * Provides the function of handle authentication requests in proxy.
 * 
 * @author Bryan Lin 2004-11-19
 */
class ProxyAuthenticator extends Authenticator {
    String ProxyUserName;
    char[] ProxyPassword;
    ProxyAuthenticator(String _proxyUserName,String _proxyPassword)
	{   super();
    	ProxyUserName=_proxyUserName;
    	ProxyPassword=_proxyPassword.toCharArray();
    	}
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(ProxyUserName, ProxyPassword);
    }
}